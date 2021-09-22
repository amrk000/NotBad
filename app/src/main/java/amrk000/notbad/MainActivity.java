package amrk000.notbad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    FrameLayout frameLayout;
    FloatingActionButton floatingButton;
    ImageButton about;

    boolean readPermission;
    boolean writePermission;

    int buttonstate;
    public static final int BUTTON_ADD = 0;
    public static final int BUTTON_SAVE = 1;
    public static float buttonDefaultPositionX;

    public static NoteManager noteManager;
    public NoteEditor noteEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout=findViewById(R.id.framelayout);
        floatingButton=findViewById(R.id.floatingActionButton);
        about=findViewById(R.id.about);

        noteManager =new NoteManager();

        //check permissions before loading
        readPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        writePermission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (readPermission && writePermission) loadNotes();
        else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.zoomin,0)
                    .replace(R.id.framelayout,new PermissionRequired())
                    .commit();
        }

        //initial button state
        buttonstate=BUTTON_ADD;
        floatingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!(readPermission && writePermission)){
                    frameLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoomin_out));
                    return;
                }

                if(buttonstate==BUTTON_ADD) {

                    noteEditor=new NoteEditor();

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right, R.anim.slide_in_from_right, R.anim.slide_out_to_left)
                            .addToBackStack("")
                            .replace(R.id.framelayout, noteEditor)
                            .commit();

                }
                else if(buttonstate==BUTTON_SAVE){

                 if(noteEditor.getMode().equals("new"))   noteManager.createNoteFile(noteEditor.getNewNote());
                 else noteManager.editNoteFile(noteEditor.getCurrentNote(),noteEditor.getNewNote());

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left,R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                            .replace(R.id.framelayout, new NoteList())
                            .commit();
                }
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),about.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(buttonDefaultPositionX==0) {
            //get floatingButton X position after layout created
            floatingButton.post(() -> {
                buttonDefaultPositionX = floatingButton.getX();
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==0){
            //check if permission granted
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                readPermission = writePermission = true;
                loadNotes();
            }
            else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    void loadNotes(){
        noteManager.initFolder();

        if(noteManager.isEmpty())
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.zoomin,0)
                    .replace(R.id.framelayout,new EmptyNote())
                    .commit();

        else
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout,new NoteList())
                    .commit();
    }

}