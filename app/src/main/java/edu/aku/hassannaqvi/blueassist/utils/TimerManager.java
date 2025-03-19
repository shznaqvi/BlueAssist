package edu.aku.hassannaqvi.blueassist.utils;

import android.os.CountDownTimer;
import android.util.Log;

public class TimerManager {
    private static final String TAG = "TimerManager";

    private CountDownTimer countDownTimer;
    private TimerListener listener;

    public TimerManager(TimerListener listener) {
        this.listener = listener;
    }

    public void startTimer(int durationSeconds) {
        if (durationSeconds <= 0) {
            Log.w(TAG, "Invalid duration, skipping timer.");
            listener.onTimerFinished();
            return;
        }

        countDownTimer = new CountDownTimer(durationSeconds * 1000L, 1000) {
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                listener.onTimerTick(secondsRemaining);
            }

            public void onFinish() {
                listener.onTimerFinished();
            }
        }.start();
    }

    public void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public interface TimerListener {
        void onTimerTick(int secondsRemaining);
        void onTimerFinished();
    }
}
