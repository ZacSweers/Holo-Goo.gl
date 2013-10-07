package com.pandanomic.hologoogl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AuthPreferences {

    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "token";
    private static final String LOGGEDIN = "loggedin";

    private SecurePreferences preferences;


    public AuthPreferences(Context context) {
        preferences = new SecurePreferences(context, "HoloGooglPrefs", "352b5a39617e676c3d52584a7379692c5f2e5e477a5a5e7d2226395c25", true);
    }

    public void setUser(String user) {
        preferences.put(KEY_USER, user);
        if (user == null) {
            preferences.put(LOGGEDIN, "false");
        } else {
            preferences.put(LOGGEDIN, "true");
        }
    }

    public void setToken(String password) {
        preferences.put(KEY_TOKEN, password);
        if (password == null) {
            preferences.put(LOGGEDIN, "false");
        } else {
            preferences.put(LOGGEDIN, "true");
        }
    }

    public void login(String user, String token) {
        preferences.put(KEY_USER, user);
        preferences.put(KEY_TOKEN, token);
        preferences.put(LOGGEDIN, "true");
    }

    public String getUser() {
        return preferences.getString(KEY_USER);
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN);
    }

    public void logout() {
        preferences.removeValue(KEY_USER);
        preferences.removeValue(KEY_TOKEN);
        preferences.put(LOGGEDIN, "false");
    }

    public boolean loggedIn() {
        return Boolean.parseBoolean(preferences.getString(LOGGEDIN));
    }
}