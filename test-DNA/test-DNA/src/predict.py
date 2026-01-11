# src/predict.py

import numpy as np
import pandas as pd
import pickle
import shap
from tensorflow.keras.models import load_model
from src.dna_utils import extract_mutations
import lime
import lime.lime_tabular

# Load saved objects
model = load_model("models/disease_model.h5")
scaler = pickle.load(open("models/scaler.pkl", "rb"))
le = pickle.load(open("models/label_encoder.pkl", "rb"))
X_columns = pickle.load(open("models/x_columns.pkl", "rb"))

# Expose class names for app.py
classes = le.classes_

# --- SHAP Explainer Setup ---
# Background reference (neutral baseline input)
background = np.zeros((1, len(X_columns)))
explainer = shap.Explainer(model, background)


def adjust_probabilities(probs):
    """Round probabilities for readability."""
    probs = np.array(probs)
    top_idx = np.argmax(probs)
    probs_rounded = np.round(probs, 3)
    return probs_rounded, int(top_idx)


def predict_from_input(dna_seq, Age, Gender, Ethinicity, Smoker, Alcoholi, BMI, Family_history):
    # Extract mutations from DNA sequence
    muts = extract_mutations(dna_seq)

    # Build input row
    input_dict = {
        "m1": [muts["m1"]], "m2": [muts["m2"]], "m3": [muts["m3"]],
        "m4": [muts["m4"]], "m5": [muts["m5"]],
        "Age": [Age], "Gender": [Gender], "Ethinicity": [Ethinicity],
        "Smoker": [Smoker], "Alcoholic": [Alcoholi], "BMI": [BMI],
        "Family_history": [Family_history]
    }
    input_df = pd.DataFrame(input_dict)

    # One-hot encode & align with training columns
    input_encoded = pd.get_dummies(input_df, columns=["Gender", "Ethinicity"], drop_first=True)
    input_encoded = input_encoded.reindex(columns=X_columns, fill_value=0)

    # Scale numeric inputs
    input_scaled = scaler.transform(input_encoded)

    # Predict with model
    raw_probs = model.predict(input_scaled, verbose=0)[0]
    probs, pred_index = adjust_probabilities(raw_probs)

    # --- SHAP feature importance ---
    shap_values = explainer(input_scaled)  # full SHAP object for this input
    shap_values_for_pred = shap_values.values[0, :, pred_index]  # SHAP values for predicted class
    feat_imp = pd.Series(shap_values_for_pred, index=input_encoded.columns, dtype=float)

    return (
        dict(zip(le.classes_, probs)),  # probabilities per class
        le.classes_[pred_index],        # predicted class
        pred_index,                     # index of predicted class
        feat_imp,                       # SHAP feature importance
        input_encoded,                  # encoded input row
        shap_values                     # full SHAP object
    )



def plain_language_summary(probs, pred_idx, feature_importances, input_row):
    """
    Convert prediction results into a human-readable explanation.
    """
    top_class = le.classes_[pred_idx]
    top_prob = float(probs[pred_idx])

    # Pick top 3 contributing features (from SHAP values now)
    top_features = feature_importances.abs().sort_values(ascending=False).head(3).index.tolist()

    reasons = []
    for f in top_features:
        val = input_row[f].iloc[0]
        if f.startswith("m"):  # mutation feature
            if val > 0:
                reasons.append(f"presence of {f}")
            else:
                reasons.append(f"absence of {f}")
        else:
            reasons.append(f"{f}={val}")

    summary = f"The model predicts **{top_class}** ({top_prob:.1%}). Top contributors: " + ", ".join(reasons) + "."
    return summary


def explain_with_lime(model, input_df, X_train):
    """
    Run LIME explanation for a single input sample.
    """
    explainer = lime.lime_tabular.LimeTabularExplainer(
        training_data=np.array(X_train),
        feature_names=X_train.columns.tolist(),
        class_names=classes,
        mode="classification"
    )

    exp = explainer.explain_instance(
        data_row=input_df.iloc[0].values,
        predict_fn=model.predict_proba
    )

    return exp