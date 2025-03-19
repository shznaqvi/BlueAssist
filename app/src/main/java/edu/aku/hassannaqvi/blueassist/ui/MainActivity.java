// MainActivity.java
package edu.aku.hassannaqvi.blueassist.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.aku.hassannaqvi.blueassist.R;
import edu.aku.hassannaqvi.blueassist.model.ACLSProtocol;
import edu.aku.hassannaqvi.blueassist.model.ACLSStep;
import edu.aku.hassannaqvi.blueassist.model.ACLSSubstep;
import edu.aku.hassannaqvi.blueassist.utils.ACLSFlowController;
import edu.aku.hassannaqvi.blueassist.utils.ProtocolLoader;
import edu.aku.hassannaqvi.blueassist.utils.TTSManager;

public class MainActivity extends AppCompatActivity {
    private TextView stepNameTextView;
    private TextView stepDescriptionTextView;
    private TextView stepDurationTextView;
    private ProgressBar progressBar;
    private Button actionButton;
    private Button yesButton;
    private Button noButton;
    private LinearLayout yesNoButtons;

    private ACLSFlowController flowController;
    private ProtocolLoader protocolLoader;
    private TTSManager ttsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ttsManager = new TTSManager(this);


        stepNameTextView = findViewById(R.id.stepName);
        stepDescriptionTextView = findViewById(R.id.stepDescription);
        stepDurationTextView = findViewById(R.id.stepDuration);
        progressBar = findViewById(R.id.progressBar);
        actionButton = findViewById(R.id.actionButton);
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);
        yesNoButtons = findViewById(R.id.yesNoButtons);

        protocolLoader = new ProtocolLoader(this);
        ACLSProtocol currentProtocol = protocolLoader.getProtocol("Adult_Cardiac_Arrest");


        // Initialize flowController with the appropriate protocol
        flowController = new ACLSFlowController(currentProtocol, this);
        flowController.startProtocol();

        actionButton.setOnClickListener(v -> flowController.moveToNextStep());

        yesButton.setOnClickListener(v -> flowController.moveToNextStep(true));
        noButton.setOnClickListener(v -> flowController.moveToNextStep(false));
    }

    public void updateUI(ACLSStep step) {
        stepNameTextView.setText(step.getName());
        stepDescriptionTextView.setText(step.getTtsMessage());
        stepDurationTextView.setText(String.valueOf(step.getDuration()));
        yesNoButtons.setVisibility(View.GONE);
        actionButton.setVisibility(View.VISIBLE);
        ttsManager.speak(step.getTtsMessage());

        if (step.getDuration()>0) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(step.getDuration()*60);


        } else {
            progressBar.setVisibility(View.GONE);
        }

/*        if (step.hasDecisionBranch()) {
            yesNoButtons.setVisibility(View.VISIBLE);
            actionButton.setVisibility(View.GONE);
        } else {
            yesNoButtons.setVisibility(View.GONE);
            actionButton.setVisibility(View.VISIBLE);
        }*/
    }

    public void updateUI(ACLSSubstep subStep) {
        stepNameTextView.setText("Is Rhythm Shockable?");
        stepDescriptionTextView.setText(subStep.getQuestion());

        yesNoButtons.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        actionButton.setVisibility(View.GONE);

    }

    public void showDecisionButtons(ACLSFlowController controller) {
        yesNoButtons.setVisibility(View.VISIBLE);
        actionButton.setVisibility(View.GONE);
    }

    public void updateProgressBar(int durationInSeconds, int secondsRemaining) {
        progressBar.setMax(durationInSeconds);
        progressBar.setProgress(durationInSeconds - secondsRemaining);
    }

    public void updateActionButtonText(String button_text) {
        actionButton.setText(button_text);
    }
}