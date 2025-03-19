package edu.aku.hassannaqvi.blueassist.model;

import android.util.Log;

import org.json.JSONObject;

public class ACLSSubstep {
    private static final String TAG = "ACLSSubstep";

    private final String question;
    private final String optionYes;
    private final String optionNo;

    public ACLSSubstep(String question, String optionYes, String optionNo) {
        this.question = (question != null && !question.isEmpty()) ? question : "No question provided";
        this.optionYes = (optionYes != null && !optionYes.isEmpty()) ? optionYes : "N/A";
        this.optionNo = (optionNo != null && !optionNo.isEmpty()) ? optionNo : "N/A";
    }

    // JSON Constructor with Improved Error Handling
    public ACLSSubstep(JSONObject substepObj) {
        if (substepObj == null) {
            Log.e(TAG, "JSON object is null while creating ACLSSubstep");
        }
        this.question = substepObj != null ? substepObj.optString("question", "No question provided") : "No question provided";
        this.optionYes = substepObj != null ? substepObj.optString("yes", "N/A") : "N/A";
        this.optionNo = substepObj != null ? substepObj.optString("no", "N/A") : "N/A";

        if (substepObj == null || question.equals("No question provided")) {
            Log.w(TAG, "Missing or empty question field in ACLSSubstep");
        }
    }

    // Getters
    public String getQuestion() {
        return question;
    }

    public String getOptionYes() {
        return optionYes;
    }

    public String getOptionNo() {
        return optionNo;
    }
}
