package edu.aku.hassannaqvi.blueassist.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.Map;

public class ACLSProtocol {
    private final String name;
    private final Map<String, ACLSStep> steps = new LinkedHashMap<>();
    private ACLSStep currentStep;

    public ACLSProtocol(String name, JSONArray stepsArray) throws JSONException {
        this.name = name;
        for (int i = 0; i < stepsArray.length(); i++) {
            JSONObject stepObj = stepsArray.getJSONObject(i);
            ACLSStep step = new ACLSStep(stepObj);
            steps.put(step.getName(), step);
        }

        // ✅ Prevent crash if stepsArray is empty
        if (!steps.isEmpty()) {
            this.currentStep = steps.values().iterator().next(); // Start with first step
        } else {
            this.currentStep = null; // No steps available
        }
    }

    public ACLSStep getCurrentStep() {
        return currentStep;
    }

    public boolean nextStep() {
        if (currentStep != null && currentStep.getNextStep() != null && steps.containsKey(currentStep.getNextStep())) {
            currentStep = steps.get(currentStep.getNextStep());
            return true;
        }
        return false;
    }
}