package edu.aku.hassannaqvi.blueassist.utils;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.widget.ProgressBar;

public class TimerManager {
    private final ToneGenerator toneGenerator;
    private CountDownTimer countDownTimer;
    private TimerListener listener;
    private long duration;
    private ProgressBar progressBar;

    public TimerManager(long duration, long interval, ProgressBar progressBar, TimerListener listener) {
        this.listener = listener;
        this.duration = duration;
        this.progressBar = progressBar;
        this.toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80); // 80% volume

        countDownTimer = new CountDownTimer(duration, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (listener != null) {
                    listener.onTick(millisUntilFinished);
                }
                updateProgressBar(millisUntilFinished);
                playBeep();

            }

            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.onFinish();
                }
                progressBar.setProgress(0);
            }
        };
    }

    private void playBeep() {
        if (toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 150); // Beep for 150ms
        }
    }

    private void updateProgressBar(long millisUntilFinished) {
        int progress = (int) ((millisUntilFinished * 100) / duration);
        progressBar.setProgress(progress);
    }

    public void start() {
        countDownTimer.start();
    }

    public void cancel() {
        countDownTimer.cancel();
        progressBar.setProgress(100);
    }

    public interface TimerListener {
        void onTick(long millisUntilFinished);
        void onFinish();
    }
}
