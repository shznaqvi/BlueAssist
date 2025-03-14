package edu.aku.hassannaqvi.blueassist.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.aku.hassannaqvi.blueassist.R;
import edu.aku.hassannaqvi.blueassist.model.ACLSProtocol;
import edu.aku.hassannaqvi.blueassist.model.ACLSStep;
import edu.aku.hassannaqvi.blueassist.utils.ProtocolLoader;
import edu.aku.hassannaqvi.blueassist.utils.TTSManager;
import edu.aku.hassannaqvi.blueassist.utils.TimerManager;

public class MainActivity extends AppCompatActivity {
    private ACLSProtocol currentProtocol;
    private ProtocolLoader protocolLoader;
    private TTSManager ttsManager;
    private TimerManager timerManager;
    private TextView statusText;
    private TextView instructionsText;
    private ProgressBar progressBar;
    private Button nextStepButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ttsManager = new TTSManager(this);
        protocolLoader = new ProtocolLoader(this);
        currentProtocol = protocolLoader.getProtocol("Adult_Cardiac_Arrest");

        statusText = findViewById(R.id.status_text);
        instructionsText = findViewById(R.id.instructions_text);
        progressBar = findViewById(R.id.progress_bar);
        nextStepButton = findViewById(R.id.next_step_button);

        updateUI();

        nextStepButton.setOnClickListener(v -> {
            if (currentProtocol.nextStep()) {
                updateUI();
            }
        });
    }

    private void updateUI() {
        ACLSStep step = currentProtocol.getCurrentStep();
        statusText.setText(step.getName());
        instructionsText.setText(step.getInstruction());
        ttsManager.speak(step.getMessage());

        // Start a new timer for the step duration
        if (timerManager != null) {
            timerManager.cancel();
        }

        timerManager = new TimerManager(step.getDuration(), 1000, progressBar, new TimerManager.TimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                // UI updates already handled in TimerManager
            }

            @Override
            public void onFinish() {
                statusText.setText("Step completed!");
            }
        });

        timerManager.start();
    }
}
