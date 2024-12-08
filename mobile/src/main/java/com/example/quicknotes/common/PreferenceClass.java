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

    public PreferenceClass(Context context) {
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

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

    // Add other methods for storing and retrieving other data types
    public int getInt(String key) {
        return sharedPrefs.getInt(key, 0);
    }

    private void doEdit() {
        editor = sharedPrefs.edit();
    }

    private void doCommit() {
        if (editor != null) {
            editor.commit();
            editor = null;
        }
    }

    public <T> void setList(String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor = sharedPrefs.edit();
        editor.putString(key, json);
        editor.commit();
        editor = null;
    }

    public List<Note> getList(String key) {
        Gson gson = new Gson();
        String json = getStringOfJson(key);
        Type type = new TypeToken<List<Note>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    @Nullable
    public String getStringOfJson(String key) {
        return sharedPrefs.getString(key, null);
    }


}
