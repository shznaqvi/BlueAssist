package edu.aku.hassannaqvi.blueassist.utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.aku.hassannaqvi.blueassist.model.ACLSProtocol;

public class ProtocolLoader {
    private static final String TAG = "ProtocolLoader";
    private static final String PROTOCOL_FILE = "json/acls_protocols.json";

    private final Context context;
    private final Map<String, ACLSProtocol> protocols = new HashMap<>();

    public ProtocolLoader(Context context) {
        this.context = context;
        loadProtocols();
    }

    public void reloadProtocols() {
        protocols.clear();
        loadProtocols();
    }

    private void loadProtocols() {
        try (InputStream inputStream = context.getAssets().open(PROTOCOL_FILE);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder jsonStr = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStr.append(line);
            }

            parseProtocols(jsonStr.toString());

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Protocol file not found: " + PROTOCOL_FILE, e);
        } catch (Exception e) {
            Log.e(TAG, "Error loading ACLS protocols", e);
        }
    }

    private void parseProtocols(String jsonString) {
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            Iterator<String> keys = jsonObj.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject protocolObj = jsonObj.optJSONObject(key);

                if (protocolObj != null) {
                    JSONArray stepsArray = protocolObj.optJSONArray("steps");

                    if (stepsArray != null) {
                        ACLSProtocol protocol = new ACLSProtocol(key, stepsArray);
                        protocols.put(key, protocol);
                    } else {
                        Log.e(TAG, "Missing 'steps' array in protocol: " + key);
                    }
                } else {
                    Log.e(TAG, "Invalid protocol format for key: " + key);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing protocols JSON", e);
        }
    }

    public ACLSProtocol getProtocol(String name) {
        ACLSProtocol protocol = protocols.get(name);
        if (protocol == null) {
            Log.w(TAG, "Requested protocol not found: " + name);
        }
        return protocol;
    }

    public Map<String, ACLSProtocol> getAllProtocols() {
        return new HashMap<>(protocols);
    }
}