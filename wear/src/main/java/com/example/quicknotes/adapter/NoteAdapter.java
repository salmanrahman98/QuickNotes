package com.example.quicknotes.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.wear.widget.WearableRecyclerView;

import com.example.quicknotes.R;
import com.example.quicknotes.common.Config;
import com.example.quicknotes.databinding.ListNoteBinding;
import com.example.quicknotes.models.Note;

import java.util.List;

public class NoteAdapter extends WearableRecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    Context context;
    List<Note> noteList;
    public NoteInterface noteInterface;

    public NoteAdapter(Context context, NoteInterface taskInterface) {
        this.context = context;
        this.noteInterface = taskInterface;
    }

    public void updateList(List<Note> noteList) {
        this.noteList = noteList;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        //binding data to list item
        holder.binding.noteId.setText("ID: " + noteList.get(position).getNoteId());
        holder.binding.nameTv.setText("Name: " + noteList.get(position).getName());
        holder.binding.descriptionTv.setText("Description: " + noteList.get(position).getDescription());

        var targetX = holder.itemView.getX();
        var startingX = 0f - holder.itemView.getWidth();
        long animationStartDelay = position * 100L;
        holder.itemView.setX(startingX);

        ObjectAnimator rightSlide = ObjectAnimator.ofFloat(holder.itemView, "x", startingX, targetX);
        rightSlide.setStartDelay(animationStartDelay);
        rightSlide.setDuration(250L);
        rightSlide.setInterpolator(new OvershootInterpolator());
        rightSlide.start();

        //formatting date and time
        String formattedDateTime = Config.formatTimestamp(noteList.get(position).getDateTime());
        holder.binding.noteDateTv.setText(formattedDateTime);

        holder.binding.itemLayout.setOnClickListener(v -> {
            noteInterface.onNoteClick(noteList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return noteList == null ? 0 : noteList.size();
    }

    public static class NoteViewHolder extends WearableRecyclerView.ViewHolder {
        public ListNoteBinding binding;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListNoteBinding.bind(itemView);
        }
    }

    public interface NoteInterface {
        void onNoteClick(Note note);
    }
}
