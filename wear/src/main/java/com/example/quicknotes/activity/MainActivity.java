package com.example.quicknotes.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quicknotes.common.Config;
import com.example.quicknotes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.addNotesCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNotesActivity.class);
            startActivity(intent);
//            Config.sendMessage("Hello from MainActivity!", MainActivity.this);
        });

        binding.viewNotesCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewNotesActivity.class);
            startActivity(intent);
        });

    }
}