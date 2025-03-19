package edu.aku.hassannaqvi.blueassist.utils;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;

import edu.aku.hassannaqvi.blueassist.model.ACLSProtocol;
import edu.aku.hassannaqvi.blueassist.model.ACLSStep;
import edu.aku.hassannaqvi.blueassist.model.ACLSSubstep;
import edu.aku.hassannaqvi.blueassist.ui.MainActivity;

public class ACLSFlowController implements TimerManager.TimerListener {
    private MainActivity activity;
    private ACLSProtocol protocol;
    private ACLSStep currentStep;
    private final TimerManager timerManager;
    private final ToneGenerator toneGenerator;
    private boolean isOvertime = false;
    private final Handler handler = new Handler();
    private boolean beating;


    public ACLSFlowController(ACLSProtocol protocol, MainActivity activity) {
        this.protocol = protocol;
        this.activity = activity;
        this.timerManager = new TimerManager(this);
        this.toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80); // 80% volume
        resetProtocol();
    }

    public void startProtocol() {
        if (currentStep == null) {
            System.out.println("No steps available in protocol.");
            return;
        }
        activity.updateActionButtonText("Start Protocol");
        executeStep(currentStep);
    }

    private void executeStep(ACLSStep step) {
        System.out.println("Current Step: " + step.getName());
        System.out.println("Description: " + step.getTtsMessage());
        System.out.println("Duration: " + step.getDuration() + " seconds");
        if(step.getId()>1)
            activity.updateActionButtonText("Done!");
        activity.runOnUiThread(() -> activity.updateUI(step));

        if (step.getDuration() > 0) {

            timerManager.startTimer(step.getDuration()*60);
        } else if (step.hasDecisionBranch()) {
            // Wait for user input if it's a decision step
            // activity.showDecisionButtons(this);
        } else {
            onTimerFinished();
        }
    }

    private void executeSubStep(ACLSSubstep subStep) {
        System.out.println("Question: " + subStep.getQuestion());


        activity.runOnUiThread(() -> activity.updateUI(subStep));



    }

    private void playSoftTone() {
        beating = false;
        int interval = 60000 / 110; // 110 beats per minute (average CPR rate)
        playCPRTone();
        handler.postDelayed(this::playCPRTone, interval - 150); // Schedule next beep

  /*      if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150); // Beep for 150ms
        }*/
    }

    private void playCPRTone() {
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150); // Beep for 150ms

        }
    }

    private void playAlarmTone() {
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300); // Beep for 300ms
        }
    }

    @Override
    public void onTimerTick(int secondsRemaining) {
        System.out.println("Time Remaining: " + secondsRemaining + "s");
        if(!isOvertime) {
            activity.runOnUiThread(() -> activity.updateProgressBar(currentStep.getDuration()*60, secondsRemaining));
            playSoftTone();
        }        else {
            activity.runOnUiThread(() -> activity.updateProgressBar(currentStep.getDuration()*60, currentStep.getDuration()*60));
            playAlarmTone();
        }
    }

    @Override
    public void onTimerFinished() {
        System.out.println("Step Timer Finished. Moving to next step...");
        if(currentStep.getDuration() >0)
            startOvertime();


    }

    private void startOvertime() {
        isOvertime = true;
        timerManager.startTimer(Integer.MAX_VALUE); // Start a no-limit timer
        toneGenerator.stopTone();
    }

    public void stopOvertime() {
        if (isOvertime) {
            timerManager.cancelTimer();
            if (toneGenerator != null)
                toneGenerator.stopTone();
            isOvertime = false;
        }
    }

    public void moveToNextStep(boolean isYes) {
        timerManager.cancelTimer();
        toneGenerator.stopTone();
        stopOvertime();
        String nextStepName = currentStep.getNextStepBasedOnResponse(isYes);
        if (nextStepName != null && protocol.getSteps().containsKey(nextStepName)) {
            currentStep = protocol.getSteps().get(nextStepName);
            executeStep(currentStep);
        } else {
            System.out.println("Protocol Completed.");
        }
    }


    public void moveToNextStep() {
        timerManager.cancelTimer();
        toneGenerator.stopTone();
        stopOvertime();
        if(currentStep.hasDecisionBranch()) {
            executeSubStep(currentStep.getSubstep());
            return;

        }
        timerManager.cancelTimer();
        String nextStepName = currentStep.getNextStep();
        if (nextStepName != null && protocol.getSteps().containsKey(nextStepName)) {
            currentStep = protocol.getSteps().get(nextStepName);
            executeStep(currentStep);
        } else {
            System.out.println("Protocol Completed.");
        }
    }

    public void resetProtocol() {
        // Reset any active timers
        timerManager.cancelTimer();
        isOvertime = false;

        // Stop any tones that are currently playing
        if (toneGenerator != null) {
            toneGenerator.stopTone();
        }

        // Reset the protocol steps
        if (!protocol.getSteps().isEmpty()) {
            this.currentStep = protocol.getSteps().values().iterator().next();
        } else {
            this.currentStep = null;
            System.out.println("No steps available in protocol: " + protocol.getProtocolName());
        }
    }
}