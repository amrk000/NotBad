package amrk000.notbad;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.text.format.Time;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

public class NoteManager {
    private File folder;
    private File file;

    private final String notesFolderPath;

    public NoteManager(){
        notesFolderPath = Environment.getExternalStorageDirectory().getPath() + "/Notes";
    }

    public String initFolder(){

        folder = new File(notesFolderPath);

        if(folder.exists()) return "File Exists";

        boolean created = folder.mkdir();

        if(created) return folder.getAbsolutePath();
        else return "Not Created";
    }

    public String createNoteFile(Note note){

        if(note.getTitle().length()==0) note.setTitle("Note");

        file=new File(folder.getAbsolutePath()+"/"+note.getTitle()+".txt");

        if(file.exists()){
            int i=2;

            do {
                file=new File(folder.getAbsolutePath()+"/"+note.getTitle()+i+".txt");
                ++i;
            }
            while (file.exists());
        }

        try {
            FileWriter contentwriter = new FileWriter(file.getAbsolutePath());
            contentwriter.write(note.getContent());
            contentwriter.close();
        } catch (Exception e){return e.toString();}

        return file.getAbsolutePath();
    }

    public String editNoteFile(Note currentNote,Note newNote){

        deleteNoteFile(currentNote);

        if(newNote.getTitle().length()==0) newNote.setTitle("Note");

        file=new File(folder.getAbsolutePath()+"/"+newNote.getTitle()+".txt");

        if(file.exists()){
            int i=2;

            do {
                file=new File(folder.getAbsolutePath()+"/"+newNote.getTitle()+i+".txt");
                ++i;
            }
            while (file.exists());
        }

        try {
            FileWriter contentwriter = new FileWriter(file.getAbsolutePath());
            contentwriter.write(newNote.getContent());
            contentwriter.close();
        } catch (Exception e){return e.toString();}


        return file.getAbsolutePath();
    }

    public String deleteNoteFile(Note note){

        file=new File(folder.getAbsolutePath()+"/"+note.getTitle()+".txt");

        if(file.exists()){
          file.delete();
          return "deleted successfully";
        }

       else return "File Not Found";
    }

    public int countNotes(){
        return folder.listFiles().length;
    }

    public Boolean isEmpty(){
        if(countNotes()>0) return false;
        else return true;
    }

    public ArrayList<Note> getNotes(){
       ArrayList<Note> notefiles = new ArrayList<>();

       File[] files = folder.listFiles();

        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                return (int) (t1.lastModified()-file.lastModified());
            }
        });

       int count=countNotes();
        try {
            for (int i = 0; i < count; ++i) {
                String title = files[i].getName();
                title=title.substring(0,title.length()-4);

                String content="";
                Scanner reader = new Scanner(files[i]);
                while (reader.hasNextLine()) content=content.concat(reader.nextLine()+"\n");
                reader.close();

                long epoch = files[i].lastModified();
                String time = new SimpleDateFormat("hh:mm aa").format(new Date(epoch));

                notefiles.add(new Note().setTitle(title).setContent(content).setTime(time));

            }
        } catch (Exception e){e.printStackTrace();}

        return notefiles;
    }


}
