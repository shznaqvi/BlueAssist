package edu.aku.hassannaqvi.blueassist.utils;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.aku.hassannaqvi.blueassist.model.ACLSProtocol;

public class ProtocolLoader {
    private final Context context;
    private Map<String, ACLSProtocol> protocols = new HashMap<>();

    public ProtocolLoader(Context context) {
        this.context = context;
        loadProtocols();
    }

    private void loadProtocols() {
        try {
            InputStream inputStream = context.getAssets().open("json/acls_protocols.json");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();

            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObj = new JSONObject(jsonStr);

            // Use `keys()` instead of `keySet()`
            Iterator<String> keys = jsonObj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONArray stepsArray = jsonObj.getJSONObject(key).getJSONArray("steps");
                ACLSProtocol protocol = new ACLSProtocol(key, stepsArray);
                protocols.put(key, protocol);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ACLSProtocol getProtocol(String name) {
        return protocols.get(name);
    }
}
