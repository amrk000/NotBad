package amrk000.notbad;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {
    private final ArrayList<Note> notes;
    private final Context context;

    private final FragmentManager fragmentManager;
    private MainActivity mainActivity;

    public NoteListAdapter(Context context,ArrayList<Note> notes,FragmentManager fragmentManager,MainActivity mainActivity){
        this.notes=notes;
        this.context=context;
        this.fragmentManager=fragmentManager;
        this.mainActivity=mainActivity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView title,content,lastModified;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            content = itemView.findViewById(R.id.item_content);
            lastModified = itemView.findViewById(R.id.item_modification_time);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(HtmlCompat.fromHtml(note.getContent(),HtmlCompat.FROM_HTML_MODE_COMPACT));
        holder.lastModified.setText(note.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainActivity.noteEditor = new NoteEditor(note);
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                        .addToBackStack("")
                        .replace(R.id.framelayout,mainActivity.noteEditor)
                        .commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
