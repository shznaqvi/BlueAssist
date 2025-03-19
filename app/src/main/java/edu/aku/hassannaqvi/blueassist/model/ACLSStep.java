package edu.aku.hassannaqvi.blueassist.model;

import android.util.Log;

import org.json.JSONObject;

public class ACLSStep {
    private static final String TAG = "ACLSStep";

    private final int id;
    private final String name;
    private final String type;
    private final int duration;
    private final boolean doneButton;
    private final String ttsMessage;
    private final int overtimeDuration;
    private final String nextStep;
    private final ACLSSubstep substep;

    public ACLSStep(JSONObject jsonObject) {
        this.id = jsonObject.optInt("id", -1);
        this.name = jsonObject.optString("name", "Unknown Step");
        this.type = jsonObject.optString("type", "Unknown Type");
        this.duration = jsonObject.optInt("timer", 0);
        this.doneButton = jsonObject.optBoolean("done_button", false);
        this.ttsMessage = jsonObject.optString("tts_message", "");
        this.overtimeDuration = jsonObject.optString("overtime_timer", "none").equals("no_limit") ? -1 : jsonObject.optInt("overtime_timer", 0);
        this.nextStep = jsonObject.optString("next", null);

        // Handle substep parsing
        JSONObject substepObj = jsonObject.optJSONObject("decision_sub_step");
        if (substepObj != null) {
            this.substep = new ACLSSubstep(substepObj);
        } else {
            this.substep = null;
            Log.w(TAG, "No substep found for step: " + this.name);
        }
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }


    public boolean hasDoneButton() {
        return doneButton;
    }

    public String getTtsMessage() {
        return ttsMessage;
    }

    public int getOvertimeDuration() {
        return overtimeDuration;
    }

    public String getNextStep() {
        return nextStep;
    }

    public ACLSSubstep getSubstep() {
        return substep;
    }

    // Check if step has decision-based branching
    public boolean hasDecisionBranch() {
        return substep != null && (substep.getOptionYes() != null || substep.getOptionNo() != null);
    }

    // Determine the next step based on user response
    public String getNextStepBasedOnResponse(boolean isShockable) {
        if (hasDecisionBranch()) {
            return isShockable ? substep.getOptionYes() : substep.getOptionNo();
        }
        return nextStep;
    }
}