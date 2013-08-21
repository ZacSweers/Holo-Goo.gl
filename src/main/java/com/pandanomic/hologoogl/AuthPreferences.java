package com.pandanomic.hologoogl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AuthPreferences {

    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "token";
    private static final String LOGGEDIN = "loggedin";

    private SharedPreferences preferences;

    public AuthPreferences(Context context) {
        preferences = context
                .getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    public void setUser(String user) {
        Editor editor = preferences.edit();
        editor.putString(KEY_USER, user);
        if (user == null) {
            editor.putBoolean(LOGGEDIN, false);
        } else {
            editor.putBoolean(LOGGEDIN, true);
        }
        editor.commit();
    }

    public void setToken(String password) {
        Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN, password);
        if (password == null) {
            editor.putBoolean(LOGGEDIN, false);
        } else {
            editor.putBoolean(LOGGEDIN, true);
        }
        editor.commit();
    }

    public void login(String user, String token) {
        Editor editor = preferences.edit();
        editor.putString(KEY_USER, user);
        editor.putString(KEY_TOKEN, token);
        editor.putBoolean(LOGGEDIN, true);
        editor.commit();
    }

    public String getUser() {
        return preferences.getString(KEY_USER, null);
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public void logout() {
        Editor editor = preferences.edit();
        editor.remove(KEY_USER);
        editor.remove(KEY_TOKEN);
        editor.putBoolean(LOGGEDIN, false);
        editor.commit();
    }

    public boolean loggedIn() {
        return preferences.getBoolean(LOGGEDIN, false);
    }
}