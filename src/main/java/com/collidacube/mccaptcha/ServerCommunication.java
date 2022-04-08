package com.collidacube.mccaptcha;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerCommunication {

    public static boolean getVerified(String uuid) {
        String response;
        try {
            response = getResponse("https://captcha.collidacube.repl.co/check?uuid=" + uuid);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return response.contains("\"success\":true");
    }

    public static String getResponse(String url) throws Exception {
        StringBuilder result = new StringBuilder();
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }
        return result.toString();
    }

}
