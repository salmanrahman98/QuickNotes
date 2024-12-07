package com.example.quicknotes;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageListenerService extends WearableListenerService {
    private static final String TAG = "MessageListenerService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "Message received: " + new String(messageEvent.getData()));

        // Check the path to ensure it matches the one used in sendMessage()
        if (messageEvent.getPath().equals("/message")) {
            String message = new String(messageEvent.getData());
            Log.d(TAG, "Message content: " + message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            // Handle the message (e.g., update UI, send a notification)
        } else {
            Log.d(TAG, "Unexpected message path: " + messageEvent.getPath());
        }
    }
}
