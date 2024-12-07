package com.example.quicknotes;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quicknotes.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.sendBtn.setOnClickListener(v -> {
            sendMessage("Hello from Wear");
        });

    }

    public void sendMessage(String message) {
//        Wearable.getNodeClient(this);
        getPhoneNodeId(nodeId -> {
            Wearable.getMessageClient(MainActivity.this)
                    .sendMessage(nodeId, "/message", message.getBytes())
                    .addOnSuccessListener(aVoid -> Log.d("Wearable", "Message sent successfully"))
                    .addOnFailureListener(e -> Log.e("Wearable", "Failed to send message", e));
        });
    }

    public void getPhoneNodeId(OnSuccessListener<String> callback) {
        Wearable.getNodeClient(MainActivity.this).getConnectedNodes().addOnSuccessListener(nodes -> {
            if (!nodes.isEmpty()) {
                // Assuming the first node is the phone (adjust logic as needed)
                Node node = nodes.get(0);
                callback.onSuccess(node.getId());
            }
        }).addOnFailureListener(e -> Log.e("Wearable", "Failed to get nodes", e));
    }
}