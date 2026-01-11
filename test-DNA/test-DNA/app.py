# app.py
import shap
import streamlit as st
from streamlit_shap import st_shap
import matplotlib.pyplot as plt
from src.predict import predict_from_input, plain_language_summary, classes
from src.dna_utils import extract_mutations, highlight_mutations

st.set_page_config(page_title="DNA Disease Prediction XAI", layout="centered")
st.title("🧬 DNA Disease Prediction with Explainability")

# --- User Inputs ---
dna_seq = st.text_area("Enter DNA Sequence (≥ 300 bases):")
age = st.text_input("Age")
gender = st.selectbox("Gender", ["Male", "Female"])
ethnicity = st.selectbox("Ethnicity", ["Caucasian", "Asian", "African", "Other"])
smoker = st.selectbox("Smoker", [0, 1])
alcoholi = st.selectbox("Alcoholic", [0, 1])
bmi = st.text_input("BMI")
family_history = st.selectbox("Family History", [0, 1])

# --- Prediction ---
if st.button("Predict"):
    if dna_seq and len(dna_seq) >= 300:
        # Run prediction
        probs, prediction, pred_idx, feat_imp, input_row, shap_values = predict_from_input(
        dna_seq, age, gender, ethnicity, smoker, alcoholi, bmi, family_history
    )


        # Final Prediction
        st.success(f"✅ Final Prediction: {prediction}")


        # Show DNA mutation visualization
        st.subheader("🔍 DNA Mutation Map")
        muts = extract_mutations(dna_seq)
        highlighted_dna = highlight_mutations(dna_seq, muts)
        st.markdown(
            f"<div style='font-family:monospace; word-wrap:break-word;'>{highlighted_dna}</div>",
            unsafe_allow_html=True,
        )
        st.caption("Colored regions indicate detected mutations in the DNA sequence.")

        # Probabilities (as numbers)
        st.subheader("📊 Prediction Probabilities")
        for cls, p in probs.items():
            st.write(f"{cls}: {p:.3f}")

        
        # Probability Bar Chart
        fig, ax = plt.subplots(figsize=(7, 3.5))
        ax.bar(list(probs.keys()), list(probs.values()))
        ax.set_ylabel("Probability")
        ax.set_title("Prediction Probabilities")
        ax.set_xticklabels(list(probs.keys()), rotation=25, ha="right")
        st.pyplot(fig)

        # Plain-language summary
        st.subheader("📝 Explanation")
        summary = plain_language_summary(list(probs.values()), pred_idx, feat_imp, input_row)
        st.markdown(summary)

        
        # --- SHAP Feature Importance ---
        st.subheader("🔍 Feature Importance (SHAP)")
        shap_fig, shap_ax = plt.subplots(figsize=(7, 4))
        feat_imp_sorted = feat_imp.abs().sort_values(ascending=True)[:10]  # ascending for horizontal bar
        shap_ax.barh(feat_imp_sorted.index, feat_imp_sorted.values, color="steelblue")
        shap_ax.set_title("Top Feature Contributions")
        shap_ax.set_xlabel("mean(|SHAP value|) (impact on prediction)")
        shap_ax.set_ylabel("")
        shap_ax.tick_params(axis="x", which="both", bottom=False, top=False, labelbottom=False)
        st.pyplot(shap_fig)

        # --- SHAP Force Plot ---
        st.subheader("🧲 Feature Impact on Prediction (SHAP Force Plot)")

        # Use values from shap_values returned by predict_from_input
        force_plot = shap.force_plot(
            shap_values.base_values[0][pred_idx],  # baseline for predicted class
            shap_values.values[0, :, pred_idx],    # SHAP values for features
            input_row.iloc[0],                     # feature values
            matplotlib=False
        )

        st_shap(force_plot, height=300)



        # --- SHAP Signed Force Plot (Horizontal, centered at 0) ---
        st.subheader("🧲 Feature Impact (Positive / Negative SHAP values)")

        # Take top 10 absolute contributors
        top_feats = feat_imp.abs().sort_values(ascending=False).head(10).index
        shap_top = feat_imp[top_feats]

        fig, ax = plt.subplots(figsize=(8, 5))

        # Positive values in red, negative in blue
        colors = ["blue" if val > 0 else "red" for val in shap_top.values]
        ax.barh(shap_top.index, shap_top.values, color=colors)

        # Symmetric x-axis around 0
        max_val = max(abs(shap_top.min()), abs(shap_top.max()))
        ax.set_xlim(-max_val*1.1, max_val*1.1)  # slight padding

        ax.axvline(0, color="black", linewidth=0.8)  # vertical line at 0
        ax.set_xlabel("SHAP value (effect on prediction)")
        ax.set_ylabel("Feature")
        ax.set_title("Feature Impact on Prediction (Positive / Negative)")

        st.pyplot(fig)




    else:
        st.error("Please enter a valid DNA sequence (≥ 300 bases).")
