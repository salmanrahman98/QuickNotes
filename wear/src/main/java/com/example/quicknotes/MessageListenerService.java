package com.example.quicknotes;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageListenerService extends WearableListenerService {
    private static final String TAG = "WearableListener";
    private static final String PATH = "/message";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(PATH)) {
            String message = new String(messageEvent.getData());
            Log.d(TAG, "Received message: " + message);
            Toast.makeText(this, "t -" + message, Toast.LENGTH_SHORT).show();
        }
    }
}
