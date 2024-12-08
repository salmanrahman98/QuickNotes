package com.example.quicknotes.common;

import static com.example.quicknotes.common.Config.NOTES_LIST;
import static com.example.quicknotes.common.Config.convertJsonToList;

import android.util.Log;
import android.widget.Toast;

import com.example.quicknotes.models.Note;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class MessageListenerService extends WearableListenerService {
    private static final String TAG = "WearableListener";
    private static final String PATH = "/notes";
    PreferenceClass preferenceClass;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        preferenceClass = new PreferenceClass(this);

        if (messageEvent.getPath().equals(PATH)) {
            String message = new String(messageEvent.getData());
            Log.d(TAG, "Received message: " + message);

            Toast.makeText(this, "Changes Received", Toast.LENGTH_SHORT).show();

            List<Note> note = convertJsonToList(message);
            preferenceClass.setList(NOTES_LIST, note);
        }
    }
}