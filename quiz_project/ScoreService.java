package services;

import models.User;

/**
* Manages the updating of user scores.
*/
public class ScoreService {

/**
* Updates the score of a given user.
* @param user The User object whose score needs to be updated.
* @param points The number of points to add to the user's score.
*/
public void updateScore(User user, int points) {
if (user != null && points >= 0) {
user.addScore(points);
// In a real application, this might also persist the score to a database.
} else {
System.err.println("Error: Cannot update score for null user or with negative points.");
}
}
}