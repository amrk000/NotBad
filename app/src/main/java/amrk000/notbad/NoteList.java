package amrk000.notbad;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class NoteList extends Fragment {
    RecyclerView list;
    NoteListAdapter adapter;

    Note lastDeletedNote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notelist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = view.findViewById(R.id.list);

        ArrayList<Note> notes = MainActivity.noteManager.getNotes();
        adapter= new NoteListAdapter(getActivity(),notes,getActivity().getSupportFragmentManager(),((MainActivity)getActivity()));
        //setting layout & adapter
        list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        list.setAdapter(adapter);
        //swipe to delete callback
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                lastDeletedNote = notes.get(position);
                //swipe to remove
                MainActivity.noteManager.deleteNoteFile(notes.get(position));
                notes.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemChanged(position);

                //snackbar notification with undo option
                Snackbar.make(getActivity().findViewById(R.id.snackBarCoordinatorLayout),lastDeletedNote.getTitle()+" is deleted.", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(R.color.colorAccent))
                        .setTextColor(Color.WHITE)
                        .setActionTextColor(Color.WHITE)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //restore deleted note
                                MainActivity.noteManager.createNoteFile(lastDeletedNote);
                                notes.add(position,lastDeletedNote);
                                adapter.notifyItemInserted(position);
                            }
                        })
                        .addCallback(new Snackbar.Callback(){
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                super.onDismissed(transientBottomBar, event);
                                if(notes.isEmpty())((MainActivity)getActivity()).loadNotes();
                            }
                        })
                        .show();

            }

        };

        //attach swipe callback to recycler view
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(list);

    }
}