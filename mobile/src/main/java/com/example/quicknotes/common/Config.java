package com.example.quicknotes.common;

import android.content.Context;
import android.util.Log;

import com.example.quicknotes.models.Note;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Config {
    public static String NOTE_ID = "noteId";
    public static String NOTES_LIST = "notesList";

    //format timestamp to dd MMMM yyyy, h:mm a
    public static String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, h:mm a", Locale.ENGLISH);
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static void sendMessage(String message, Context context) {
//        Wearable.getNodeClient(this);
        getWearNodeId(context, nodeId -> {
            Wearable.getMessageClient(context)
                    .sendMessage(nodeId, "/notes", message.getBytes())
                    .addOnSuccessListener(aVoid -> Log.d("Phone", "Message sent successfully"))
                    .addOnFailureListener(e -> Log.e("Phone", "Failed to send message", e));
        });
    }

    public static void getWearNodeId(Context context, OnSuccessListener<String> callback) {
        Wearable.getNodeClient(context).getConnectedNodes().addOnSuccessListener(nodes -> {
            if (!nodes.isEmpty()) {
                // Assuming the first node is the phone (adjust logic as needed)
                Node node = nodes.get(0);
                callback.onSuccess(node.getId());
            }
        }).addOnFailureListener(e -> Log.e("Wearable", "Failed to get nodes", e));
    }

    public static List<Note> convertJsonToList(String json) {
        Type listType = new TypeToken<List<Note>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }


}
