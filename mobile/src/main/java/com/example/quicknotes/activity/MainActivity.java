package com.example.quicknotes.activity;

import static com.example.quicknotes.common.Config.NOTES_LIST;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceClass = new PreferenceClass(this);
        //getting task list from preference class for previous tasks.
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
            Toast.makeText(MainActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {

                noteList = preferenceClass.getList(NOTES_LIST);
                adapter.updateList(noteList);

                binding.swipeRefreshLayout.setRefreshing(false);
            }, 200); // Delay for demonstration
        });


    }

    private void recyclerViewSetup() {

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // No drag-and-drop functionality needed
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Get the position of the swiped item
                int position = viewHolder.getAdapterPosition();
                Note removedItem = noteList.get(position);

                // Remove the item from the data source
                noteList.remove(position);
                adapter.notifyItemRemoved(position);

                // Save the updated list to SharedPreferences
                preferenceClass.setList(NOTES_LIST, noteList);
                Config.sendMessage(preferenceClass.getStringOfJson(NOTES_LIST), MainActivity.this);

                // Display a toast message
                Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        // Attach the ItemTouchHelper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.noteRecyclerView);

        adapter = new NoteAdapter(this);
        adapter.updateList(noteList);
        binding.noteRecyclerView.setAdapter(adapter);
    }


}