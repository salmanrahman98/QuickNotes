package com.example.quicknotes.common;

import static com.example.quicknotes.common.Config.NOTES_LIST;
import static com.example.quicknotes.common.Config.convertJsonToList;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.quicknotes.R;
import com.example.quicknotes.models.Note;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class MessageListenerService extends WearableListenerService {
    private static final String TAG = "MessageListenerService";
    PreferenceClass preferenceClass;

    // this method handles incoming messages from the wearable device
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        preferenceClass = new PreferenceClass(this);
        Log.d(TAG, "Message received: " + new String(messageEvent.getData()));

        if (messageEvent.getPath().equals("/notes")) {
            String message = new String(messageEvent.getData());
            Log.d(TAG, "Message content: " + message);
            Toast.makeText(this, getString(R.string.newNoteReceived), Toast.LENGTH_SHORT).show();

            List<Note> note = convertJsonToList(message);
            preferenceClass.setList(NOTES_LIST, note);

            Intent intent = new Intent("com.example.UPDATE_UI");
            intent.putExtra("message", message);
            sendBroadcast(intent);
        } else {
            Log.d(TAG, "Unexpected message path: " + messageEvent.getPath());
        }
    }
}