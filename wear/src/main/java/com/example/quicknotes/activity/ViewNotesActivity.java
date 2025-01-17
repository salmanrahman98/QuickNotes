package com.example.quicknotes.activity;

import static com.example.quicknotes.common.Config.NOTES_LIST;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quicknotes.R;
import com.example.quicknotes.adapter.NoteAdapter;
import com.example.quicknotes.common.Config;
import com.example.quicknotes.common.PreferenceClass;
import com.example.quicknotes.databinding.ActivityViewNotesBinding;
import com.example.quicknotes.models.Note;

import java.util.ArrayList;
import java.util.List;

public class ViewNotesActivity extends AppCompatActivity {

    ActivityViewNotesBinding binding;
    NoteAdapter adapter;
    List<Note> noteList;
    PreferenceClass preferenceClass;

    public static ViewNotesActivity Instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Instance = this;

        preferenceClass = new PreferenceClass(this);
        //getting task list from preference class for previous tasks.
        noteList = preferenceClass.getList(NOTES_LIST);
        if (noteList == null) {
            noteList = new ArrayList<>();
        }

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
//            Toast.makeText(ViewNotesActivity.this, "Refreshing...", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {

                noteList = preferenceClass.getList(NOTES_LIST);
                adapter.updateList(noteList);
                handleEmptyNoteState();
                binding.swipeRefreshLayout.setRefreshing(false);
            }, 200); // Delay for demonstration
        });

        recyclerViewSetup();
    }

    //setup the rv
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
                Config.sendMessage(preferenceClass.getStringOfJson(NOTES_LIST), ViewNotesActivity.this);
                handleEmptyNoteState();
                // Display a toast message
                Toast.makeText(ViewNotesActivity.this, getString(R.string.deletedToast), Toast.LENGTH_SHORT).show();
            }
        });

        // Attach the ItemTouchHelper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.viewTasksRecyclerView);
        adapter = new NoteAdapter(this, note -> {
            showPopup(note.getName(), note.getDescription());
        });
        adapter.updateList(noteList);
        handleEmptyNoteState();
        binding.viewTasksRecyclerView.setAdapter(adapter);
    }

    //show pop up
    private void showPopup(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    //handle for note state change
    public void handleEmptyNoteState() {
        if(noteList.size() == 0) {
            binding.rlEmptyNotes.setVisibility(View.VISIBLE);
            binding.viewTasksRecyclerView.setVisibility(View.GONE);
        } else {
            binding.rlEmptyNotes.setVisibility(View.GONE);
            binding.viewTasksRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}