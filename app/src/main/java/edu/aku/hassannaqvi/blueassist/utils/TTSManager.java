// In `app/src/main/java/edu/aku/hassannaqvi/blueassist/utils/TTSManager.java`
package edu.aku.hassannaqvi.blueassist.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

public class TTSManager {
    private TextToSpeech tts;

    public TTSManager(Context context) {
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(Locale.US);
                tts.setSpeechRate(0.9f); // Slightly slower speech rate
                tts.setPitch(0.8f); // Lower pitch for a more authoritative tone
            } else {
                Log.e("TTSManager", "Initialization failed");
            }
        });
    }

    public void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    public void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }
}