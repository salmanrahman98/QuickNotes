package com.example.quicknotes.activity;

import static com.example.quicknotes.common.Config.NOTES_LIST;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicknotes.R;
import com.example.quicknotes.adapter.NoteAdapter;
import com.example.quicknotes.common.Config;
import com.example.quicknotes.common.PreferenceClass;
import com.example.quicknotes.databinding.ActivityMainBinding;
import com.example.quicknotes.models.Note;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    NoteAdapter adapter;
    List<Note> noteList;
    PreferenceClass preferenceClass;

    // this method sets up the main screen and initializes necessary components
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
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

        binding.addAnnouncementFloat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNotesActivity.class);
            startActivity(intent);
        });

        recyclerViewSetup();

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(MainActivity.this, "refreshing...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                noteList = preferenceClass.getList(NOTES_LIST);
                adapter.updateList(noteList);
                handleEmptyNoteState();
                binding.swipeRefreshLayout.setRefreshing(false);
            }, 200);
        });
    }

    // this method configures the recyclerview and handles swipe actions
    private void recyclerViewSetup() {

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // no drag and drop needed
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Note removedItem = noteList.get(position);
                noteList.remove(position);
                adapter.notifyItemRemoved(position);
                preferenceClass.setList(NOTES_LIST, noteList);
                Config.sendMessage(preferenceClass.getStringOfJson(NOTES_LIST), MainActivity.this);
                Toast.makeText(MainActivity.this, getString(R.string.noteDeletedSuccessfully), Toast.LENGTH_SHORT).show();
                handleEmptyNoteState();
            }
        });

        itemTouchHelper.attachToRecyclerView(binding.noteRecyclerView);

        adapter = new NoteAdapter(this);
        adapter.updateList(noteList);
        handleEmptyNoteState();

        binding.noteRecyclerView.setAdapter(adapter);
    }

    // this method shows an empty state message if no notes are available
    public void handleEmptyNoteState() {
        if(noteList.size() == 0) {
            binding.rlEmptyNotes.setVisibility(View.VISIBLE);
            binding.noteRecyclerView.setVisibility(View.GONE);
        } else {
            binding.rlEmptyNotes.setVisibility(View.GONE);
            binding.noteRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}