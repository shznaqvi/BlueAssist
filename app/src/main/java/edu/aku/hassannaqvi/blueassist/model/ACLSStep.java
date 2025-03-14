package edu.aku.hassannaqvi.blueassist.model;

import org.json.JSONObject;

public class ACLSStep {
    private String next_step;
    private String name;
    private String message;
    private String instruction;
    private long duration;

    public ACLSStep(String name, String message, long duration) {
        this.name = name;
        this.message = message;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public long getDuration() {
        return duration;
    }

    public String getNextStep() {
        return next_step;
    }

    public void setNextStep(String next_step) {
        this.next_step = next_step;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    // If you're loading steps from JSON, add a constructor
    public ACLSStep(JSONObject jsonObject) {
        this.name = jsonObject.optString("name", "Unknown Step");
        this.message = jsonObject.optString("message", "");
        this.instruction = jsonObject.optString("instructions", null);
        this.duration = jsonObject.optLong("duration", 0);
        this.next_step = jsonObject.optString("next_step", null);

    }
}
