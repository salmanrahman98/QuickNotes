package com.example.quicknotes.activity;

import static com.example.quicknotes.common.Config.NOTES_LIST;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddNotesActivity extends AppCompatActivity {

    ActivityAddNotesBinding binding;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    PreferenceClass preferenceClass;

    List<Note> noteList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceClass = new PreferenceClass(this);

        //getting task list from preference class for previous tasks.
        noteList = preferenceClass.getList(NOTES_LIST);
        if (noteList == null) {
            noteList = new ArrayList<>();
        }

        binding.voiceInputImg.setOnClickListener(v -> startVoiceInput());

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validator()) {
                    String name = binding.nameEt.getText().toString();
                    String description = binding.descriptionEt.getText().toString();

                    Note note = new Note();
                    note.setNoteId(1);
                    note.setName(name);
                    note.setDescription(description);
                    note.setDateTime(System.currentTimeMillis());

                    noteList.add(note);
                    preferenceClass.setList(NOTES_LIST, noteList);

                    Toast.makeText(AddNotesActivity.this, getString(R.string.noteSaved), Toast.LENGTH_SHORT).show();

                    binding.nameEt.setText("");
                    binding.descriptionEt.setText("");

                    Config.sendMessage(preferenceClass.getStringOfJson(NOTES_LIST), AddNotesActivity.this);
                }
            }
        });

    }

    //validaet input data
    private boolean validator() {
        boolean isValid = true;
        if (binding.nameEt.getText().toString().isEmpty()) {
            binding.nameEt.setError(getString(R.string.requiredField));
            isValid = false;
        }
        if (binding.descriptionEt.getText().toString().isEmpty()) {
            binding.descriptionEt.setError(getString(R.string.requiredField));
            isValid = false;
        }
        return isValid;
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speakSomething));

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.unsupportedVoiceInput), Toast.LENGTH_SHORT).show();
        }
    }

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