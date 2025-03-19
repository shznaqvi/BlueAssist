package edu.aku.hassannaqvi.blueassist.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ACLSProtocol {
    private static final String TAG = "ACLSProtocol";
    private final String name;
    private final Map<String, ACLSStep> steps = new LinkedHashMap<>();
    private ACLSStep currentStep;

    public ACLSProtocol(String name, JSONArray stepsArray) {
        this.name = name;

        try {
            for (int i = 0; i < stepsArray.length(); i++) {
                JSONObject stepObj = stepsArray.getJSONObject(i);
                ACLSStep step = new ACLSStep(stepObj);
                steps.put(String.valueOf(step.getId()), step);
            }

            // Set the first step if available
            resetProtocol();
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing ACLS protocol: " + name, e);
        }
    }

    public static Map<String, ACLSStep> getProtocol(String name, JSONArray stepsArray) {
        ACLSProtocol protocolInstance = new ACLSProtocol(name, stepsArray);
        return protocolInstance.getSteps();
    }

    public ACLSStep getCurrentStep() {
        return currentStep;
    }

    public boolean nextStep() {
        if (currentStep == null) {
            Log.e(TAG, "Current step is null, cannot proceed.");
            return false;
        }

        String nextStepName = currentStep.getNextStep();
        if (nextStepName != null && steps.containsKey(nextStepName)) {
            currentStep = steps.get(nextStepName);
            return true;
        } else {
            Log.w(TAG, "No valid next step found for: " + currentStep.getName());
            return false;
        }
    }

    public boolean handleDecision(boolean isYes) {
        if (currentStep == null) {
            Log.e(TAG, "Current step is null, cannot proceed with decision.");
            return false;
        }

        String nextStepName = currentStep.getNextStepBasedOnResponse(isYes);
        if (nextStepName != null && steps.containsKey(nextStepName)) {
            currentStep = steps.get(nextStepName);
            return true;
        } else {
            Log.w(TAG, "No valid next step found for decision: " + currentStep.getName());
            return false;
        }
    }

    public void resetProtocol() {
        if (!steps.isEmpty()) {
            this.currentStep = steps.values().iterator().next();
        } else {
            this.currentStep = null;
            Log.e(TAG, "No steps available in protocol: " + name);
        }
    }

    public Map<String, ACLSStep> getSteps() {
        return steps;
    }

    public String getProtocolName() {
        return name;
    }
}