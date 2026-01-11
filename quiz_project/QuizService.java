package services;

import models.Question;
import models.Quiz;
import models.User;
import exceptions.InvalidAnswerException;
import exceptions.QuizSubmissionException;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Manages the overall quiz flow, including loading questions,
 * interacting with users, tracking scores, and handling exceptions.
 */
public class QuizService {
    private List<Question> availableQuestions;
    private Map<String, User> registeredUsers; // Optional: Store registered users
    private ScoreService scoreService;
    private Scanner scanner; // For user input

    public QuizService() {
        this.availableQuestions = new ArrayList<>();
        this.registeredUsers = new HashMap<>();
        this.scoreService = new ScoreService();
        this.scanner = new Scanner(System.in);
        loadQuestions(); // Load initial questions when service is created
    }

    /**
     * Loads predefined questions into the application.
     * In a real application, these might be loaded from a database or file.
     */
    private void loadQuestions() {
        availableQuestions.add(new Question("1", "What is the capital of France?",
                new String[]{"Berlin", "Madrid", "Paris", "Lisbon"}, 2)); // Paris is at index 2 (0-indexed)
        availableQuestions.add(new Question("2", "Which planet is known as the Red Planet?",
                new String[]{"Earth", "Mars", "Jupiter", "Saturn"}, 1)); // Mars is at index 1
        availableQuestions.add(new Question("3", "What is the chemical symbol for gold?",
                new String[]{"Au", "Ag", "Cu", "Fe"}, 0)); // Au is at index 0
        availableQuestions.add(new Question("4", "Who developed the theory of relativity?",
                new String[]{"Isaac Newton", "Albert Einstein", "Galileo Galilei", "Nikola Tesla"}, 1)); // Albert Einstein is at index 1
        availableQuestions.add(new Question("5", "What is the largest ocean on Earth?",
                new String[]{"Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean"}, 3)); // Pacific Ocean is at index 3
    }

    /**
     * Registers a new user in the system.
     *
     * @param userId The unique ID for the user.
     * @param name   The name of the user.
     * @return The created User object.
     */
    public User registerUser(String userId, String name) {
        User user = new User(userId, name);
        registeredUsers.put(userId, user);
        return user;
    }

    /**
     * Starts the quiz for a given user.
     *
     * @param user The user attempting the quiz.
     * @throws QuizSubmissionException If the quiz cannot be submitted due to incomplete responses.
     */
    public void startQuiz(User user) throws QuizSubmissionException {
        System.out.println("\nWelcome to the Quiz Application, " + user.getName() + "!");
        System.out.println("You have 30 seconds to answer each question.");

        Quiz currentQuiz = new Quiz(availableQuestions);
        user.setScore(0); // Reset user score for the current quiz attempt

        for (Question question : currentQuiz.getQuestions()) {
            question.displayQuestion();
            long startTime = System.currentTimeMillis();
            int userAnswerIndex = -1; // Default to an invalid choice

            while (true) {
                System.out.print("Your answer (1-" + question.getOptions().length + "): ");
                try {
                    // Check if there's input available within a short timeout
                    // This non-blocking read is tricky with standard Scanner and System.in
                    // For a true timer, a separate thread or more advanced input handling is needed.
                    // For simplicity, we'll just check time after user enters input.
                    if (scanner.hasNextLine()) {
                        String input = scanner.nextLine();
                        userAnswerIndex = Integer.parseInt(input) - 1; // Convert to 0-indexed
                    } else {
                        // No input, or input stream closed (unlikely in console app)
                        // This path is less likely to be hit in a simple console app
                        // without non-blocking input.
                        continue;
                    }

                    // Validate the answer choice
                    if (!question.isValidAnswer(userAnswerIndex)) {
                        throw new InvalidAnswerException("Invalid answer choice. Please enter a number between 1 and " + question.getOptions().length + ".");
                    }
                    break; // Exit loop if input is valid
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                } catch (InvalidAnswerException e) {
                    System.out.println(e.getMessage());
                }
            }

            long endTime = System.currentTimeMillis();
            long timeTakenSeconds = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime);
            System.out.println("Time taken: " + timeTakenSeconds + " seconds");

            // Check if time limit exceeded (optional feature)
            if (timeTakenSeconds > 30) { // Example: 30 seconds per question
                System.out.println("Time's up! Moving to the next question.");
                // Do not mark as answered if time is up and no valid answer was given
                // For this example, we proceed even if time is up, but don't give points.
            } else {
                if (userAnswerIndex == question.getCorrectAnswerIndex()) {
                    scoreService.updateScore(user, 1); // Add 1 point for correct answer
                    System.out.println("Correct answer!");
                } else {
                    System.out.println("Incorrect answer. The correct answer was: " + (question.getCorrectAnswerIndex() + 1) + ". " + question.getOptions()[question.getCorrectAnswerIndex()]);
                }
            }
            currentQuiz.markQuestionAsAnswered(question.getQuestionId()); // Mark question as answered regardless of correctness
        }

        // After all questions are attempted, check for submission completeness
        if (!currentQuiz.areAllQuestionsAnswered()) {
            throw new QuizSubmissionException("Quiz cannot be submitted: Not all questions were attempted.");
        }

        System.out.println("\nQuiz completed!");
        System.out.println("Your final score: " + user.getScore() + " out of " + availableQuestions.size());
    }

    /**
     * Displays the leaderboard based on user scores.
     */
    public void displayLeaderboard() {
        System.out.println("\n--- Leaderboard ---");
        if (registeredUsers.isEmpty()) {
            System.out.println("No users registered yet.");
            return;
        }

        // Sort users by score in descending order
        List<User> sortedUsers = new ArrayList<>(registeredUsers.values());
        sortedUsers.sort((u1, u2) -> Integer.compare(u2.getScore(), u1.getScore()));

        for (int i = 0; i < sortedUsers.size(); i++) {
            User user = sortedUsers.get(i);
            System.out.println((i + 1) + ". " + user.getName() + " - Score: " + user.getScore());
        }
    }

    /**
     * Closes the scanner used for input. Should be called when the application exits.
     */
    public void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}