package glistersoft.com.architecturecomponent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.UUID;

import glistersoft.com.architecturecomponent.adapter.NoteListAdapter;
import glistersoft.com.architecturecomponent.room.Note;
import glistersoft.com.architecturecomponent.viewmodel.NoteViewModel;

public class MainActivity extends AppCompatActivity implements NoteListAdapter.OnDeleteClickListener {
    public static final int NEW_NOTE_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_NOTE_ACTIVITY_REQUEST_CODE = 2;

    FloatingActionButton button;
    NoteViewModel noteViewModel;
    RecyclerView recyclerView;
    NoteListAdapter noteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        button = findViewById(R.id.fab);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, NewNoteActivity.class), NEW_NOTE_ACTIVITY_REQUEST_CODE);
            }
        });

        recyclerView = findViewById(R.id.rv);
        noteListAdapter = new NoteListAdapter(this, this);
        recyclerView.setAdapter(noteListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteListAdapter.setNotes(notes);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_NOTE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final String npte_id = UUID.randomUUID().toString();
                assert data != null;
                Note note = new Note(npte_id, data.getStringExtra(NewNoteActivity.NOTE_ADDED));
                noteViewModel.insert(note);

                Toast.makeText(this, "Note Saved.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Note not Saved.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == UPDATE_NOTE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Code to update the note
                Note note = new Note(
                        data.getStringExtra(EditNoteActivity.NOTE_ID),
                        data.getStringExtra(EditNoteActivity.UPDATED_NOTE));
                noteViewModel.update(note);

                Toast.makeText(this, R.string.updated, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.update_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void OnDeleteClickListener(Note myNote) {
        noteViewModel.delete(myNote);
    }
}
