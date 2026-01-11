# src/train_model.py

import pandas as pd
import pickle
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder, StandardScaler
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense
from tensorflow.keras.utils import to_categorical

# Load dataset
df = pd.read_csv(r"C:\Users\HARSHITHA\test-DNA\data\XAI-dataset.csv")

# Drop unused columns
df_model = df.drop(columns=["DNA_sequence", "Total_mutations"])

# Features & labels
X = df_model.drop(columns=["Label"])
y = df_model["Label"]

# One-hot encode categorical vars
X_encoded = pd.get_dummies(X, columns=["Gender", "Ethinicity"], drop_first=True)

# Encode labels
le = LabelEncoder()
y_encoded = le.fit_transform(y)
y_categorical = to_categorical(y_encoded)

# Scale numerical features
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X_encoded)

# Train-test split
X_train, X_test, y_train, y_test = train_test_split(
    X_scaled, y_categorical, test_size=0.2, random_state=42, stratify=y_categorical
)

# ANN model
model = Sequential([
    Dense(64, activation="relu", input_dim=X_train.shape[1]),
    Dense(32, activation="relu"),
    Dense(y_categorical.shape[1], activation="softmax")
])
model.compile(optimizer="adam", loss="categorical_crossentropy", metrics=["accuracy"])

# Train
history = model.fit(X_train, y_train, epochs=50, batch_size=8, validation_split=0.1, verbose=1)

# Save model & preprocessors
model.save("models/disease_model.h5")
pickle.dump(scaler, open("models/scaler.pkl", "wb"))
pickle.dump(le, open("models/label_encoder.pkl", "wb"))
pickle.dump(X_encoded.columns, open("models/x_columns.pkl", "wb"))

print("\n✅ Model training complete. Saved to /models/")
