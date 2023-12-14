package com.system32;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.prefs.Preferences;

public class Stats {
    private static final String API_URL = "http://system32world.studio:50046/api";
    
    public Stats(Preferences prefs) {

        LocalDate now = LocalDate.now();
        String savedMonth = prefs.get("autoclick.month", null);
            if (savedMonth == null) {
                prefs.put("autoclick.month", String.valueOf(now.getMonthValue()));
                sendStats();
            } else if (Integer.parseInt(savedMonth) != now.getMonthValue()) {
                prefs.put("autoclick.month", String.valueOf(now.getMonthValue()));
                sendStats();
            }

    }

    private void sendStats() {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);

            String jsonInputString = "1";

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int code = con.getResponseCode();
            System.out.println("Data sended" + code);
        } catch (ConnectException e) {
            System.out.println("Could not connect to server");
        } catch (IOException e) {
            System.out.println("Error!");
        }
    }
}