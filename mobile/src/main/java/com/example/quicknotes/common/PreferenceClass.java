package com.example.quicknotes.common;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.quicknotes.models.Note;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class PreferenceClass {
    private static final String PREFS_NAME = "sharedPrefNotes";
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;

    // this method sets up the shared preferences
    public PreferenceClass(Context context) {
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // this method saves an integer value in shared preferences
    public void putInt(String key, int val) {
        try {
            doEdit();
            if (editor != null) {
                editor.putInt(key, val);
                doCommit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this method retrieves an integer from shared preferences
    public int getInt(String key) {
        return sharedPrefs.getInt(key, 0);
    }

    // this method begins the edit process for shared preferences
    private void doEdit() {
        editor = sharedPrefs.edit();
    }

    // this method applies and commits changes to shared preferences
    private void doCommit() {
        if (editor != null) {
            editor.commit();
            editor = null;
        }
    }

    // this method saves a list of objects as json in shared preferences
    public <T> void setList(String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor = sharedPrefs.edit();
        editor.putString(key, json);
        editor.commit();
        editor = null;
    }

    // this method retrieves a list of notes from shared preferences
    public List<Note> getList(String key) {
        Gson gson = new Gson();
        String json = getStringOfJson(key);
        Type type = new TypeToken<List<Note>>() {}.getType();
        return gson.fromJson(json, type);
    }

    // this method returns the json string associated with a key
    @Nullable
    public String getStringOfJson(String key) {
        return sharedPrefs.getString(key, null);
    }
}