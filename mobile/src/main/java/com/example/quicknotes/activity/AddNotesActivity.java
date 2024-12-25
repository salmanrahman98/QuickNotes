package com.example.quicknotes.activity;

import static com.example.quicknotes.common.Config.NOTES_LIST;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quicknotes.R;
import com.example.quicknotes.common.Config;
import com.example.quicknotes.common.PreferenceClass;
import com.example.quicknotes.databinding.ActivityAddNotesBinding;
import com.example.quicknotes.models.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddNotesActivity extends AppCompatActivity {

    ActivityAddNotesBinding binding;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    PreferenceClass preferenceClass;

    List<Note> noteList;

    // this method sets up the initial view and hides the action bar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        preferenceClass = new PreferenceClass(this);

        noteList = preferenceClass.getList(NOTES_LIST);
        if (noteList == null) {
            noteList = new ArrayList<>();
        }

        binding.dateEt.setText(getCurrentDateFormatted());

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.addAnnouncementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNote();
            }
        });

        binding.voiceInputImg.setOnClickListener(v -> startVoiceInput());
    }

    // this method adds a new note after validating user input
    private void addNote() {
        if (validator()) {
            String name = binding.headerEt.getText().toString();
            String description = binding.descriptionEt.getText().toString();

            Note note = new Note();
            note.setNoteId(1);
            note.setName(name);
            note.setDescription(description);
            note.setDateTime(System.currentTimeMillis());

            noteList.add(note);
            preferenceClass.setList(NOTES_LIST, noteList);

            Toast.makeText(AddNotesActivity.this, getString(R.string.note_saved), Toast.LENGTH_SHORT).show();

            binding.headerEt.setText("");
            binding.descriptionEt.setText("");

            Config.sendMessage(preferenceClass.getStringOfJson(NOTES_LIST), AddNotesActivity.this);
        }
    }

    // this method checks if required fields are filled in correctly
    private boolean validator() {
        boolean isValid = true;
        if (binding.headerEt.getText().toString().isEmpty()) {
            binding.headerEt.setError(getString(R.string.required_field));
            isValid = false;
        }
        if (binding.descriptionEt.getText().toString().isEmpty()) {
            binding.descriptionEt.setError(getString(R.string.required_field));
            isValid = false;
        }
        return isValid;
    }

    // this method returns the current date in a readable format
    private String getCurrentDateFormatted() {
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }

    // this method initiates voice input to quickly add note content
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speakSomething));

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.unsupportedVoiceInput), Toast.LENGTH_SHORT).show();
        }
    }

    // this method processes the result of the voice input activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                binding.descriptionEt.setText(result.get(0));
            }
        }
    }
}