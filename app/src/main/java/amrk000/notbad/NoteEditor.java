package amrk000.notbad;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import android.os.Parcel;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.xml.sax.XMLReader;

public class NoteEditor extends Fragment {
    EditText title;
    EditText content;
    TextView modeLabel;
    HorizontalScrollView formatMenu;

    Button bigger,smaller,bold,italic,underline,strikeThrough,superScript,subScript; //Formatting
    Button redColor,orangeColor,yellowColor,greenColor,cyanColor,blueColor,purpleColor,blackColor,whiteColor; //Coloring
    Button redHl,orangeHl,yellowHl,greenHl,cyanHl,blueHl,purpleHl,blackHl,whiteHl; //Highlighting

    MainActivity mainActivity;
    String mode;
    Note currentnote;

    public NoteEditor() {
        mode="new";
        currentnote=new Note();
    }

    public NoteEditor(Note note) {
        mode="edit";
        currentnote=note;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.note_title);
        content = view.findViewById(R.id.note_content);
        modeLabel = view.findViewById(R.id.edit_mode);
        formatMenu = view.findViewById(R.id.formatMenu);

        bigger=view.findViewById(R.id.editor_size_increase);
        smaller=view.findViewById(R.id.editor_size_decrease);
        bold=view.findViewById(R.id.editor_bold);
        italic=view.findViewById(R.id.editor_italic);
        underline=view.findViewById(R.id.editor_underline);
        strikeThrough=view.findViewById(R.id.editor_strikethrough);
        superScript=view.findViewById(R.id.editor_superscript);
        subScript=view.findViewById(R.id.editor_subscript);

        redColor=view.findViewById(R.id.editor_red);
        orangeColor=view.findViewById(R.id.editor_orange);
        yellowColor=view.findViewById(R.id.editor_yellow);
        greenColor=view.findViewById(R.id.editor_green);
        cyanColor=view.findViewById(R.id.editor_cyan);
        blueColor=view.findViewById(R.id.editor_blue);
        purpleColor=view.findViewById(R.id.editor_purble);
        blackColor=view.findViewById(R.id.editor_black);
        whiteColor=view.findViewById(R.id.editor_white);

        redHl=view.findViewById(R.id.editor_hl_red);
        orangeHl=view.findViewById(R.id.editor_hl_orange);
        yellowHl=view.findViewById(R.id.editor_hl_yellow);
        greenHl=view.findViewById(R.id.editor_hl_green);
        cyanHl=view.findViewById(R.id.editor_hl_cyan);
        blueHl=view.findViewById(R.id.editor_hl_blue);
        purpleHl=view.findViewById(R.id.editor_hl_purple);
        blackHl=view.findViewById(R.id.editor_hl_black);
        whiteHl=view.findViewById(R.id.editor_hl_white);

        modeLabel.setText(mode);

        mainActivity=((MainActivity)getActivity());

        //Change Button Position & state
        mainActivity.floatingButton.animate().rotation(360).setDuration(750).setInterpolator(new DecelerateInterpolator()).start();
        mainActivity.floatingButton.animate().x((mainActivity.buttonDefaultPositionX*2) - 50).setDuration(700).setInterpolator(new DecelerateInterpolator()).start();
        mainActivity.floatingButton.setImageResource(R.drawable.ic_round_done_24);
        mainActivity.buttonstate = MainActivity.BUTTON_SAVE;

        //load Note Data
        title.setText(currentnote.getTitle());
        content.setText(HtmlCompat.fromHtml(currentnote.getContent(),HtmlCompat.FROM_HTML_MODE_COMPACT));

        //set formatting buttons
        bigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseSize();
            }
        });

        smaller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseSize();
            }
        });

        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                styleSpanSwitch(Typeface.BOLD);
            }
        });

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                styleSpanSwitch(Typeface.ITALIC);
            }
        });

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                underlineSpanSwitch();
            }
        });

        strikeThrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strikeThroughSpanSwitch();
            }
        });

        superScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                superScriptSpanSwitch();
            }
        });

        subScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subScriptSpanSwitch();
            }
        });

        redColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foregroundColorSpanChanger(Color.rgb(255,0,0));
            }
        });

        orangeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foregroundColorSpanChanger(Color.rgb(255,128,0));
            }
        });

        yellowColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foregroundColorSpanChanger(Color.rgb(255,255,0));
            }
        });

        greenColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foregroundColorSpanChanger(Color.rgb(0,255,100));
            }
        });

        cyanColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foregroundColorSpanChanger(Color.rgb(0,220,255));
            }
        });

        blueColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foregroundColorSpanChanger(Color.rgb(0,50,255));
            }
        });

        purpleColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foregroundColorSpanChanger(Color.rgb(230,0,255));
            }
        });

        blackColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foregroundColorSpanChanger(Color.rgb(0,0,0));
            }
        });

        whiteColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                foregroundColorSpanChanger(Color.rgb(255,255,255));
            }
        });


        redHl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundColorSpanChanger(Color.rgb(255,0,0));
            }
        });

        orangeHl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundColorSpanChanger(Color.rgb(255,128,0));
            }
        });

        yellowHl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundColorSpanChanger(Color.rgb(255,255,0));
            }
        });

        greenHl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundColorSpanChanger(Color.rgb(0,255,100));
            }
        });

        cyanHl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundColorSpanChanger(Color.rgb(0,220,255));
            }
        });

        blueHl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundColorSpanChanger(Color.rgb(0,50,255));
            }
        });

        purpleHl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundColorSpanChanger(Color.rgb(230,0,255));
            }
        });

        blackHl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundColorSpanChanger(Color.rgb(0,0,0));
            }
        });

        whiteHl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backgroundColorSpanChanger(Color.rgb(255,255,255));
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Restore Button Default Position & state
        mainActivity.floatingButton.animate().rotation(-360).setDuration(750).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        mainActivity.floatingButton.animate().x(mainActivity.buttonDefaultPositionX).setDuration(750).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        mainActivity.floatingButton.setImageResource(R.drawable.ic_baseline_add_24);
        mainActivity.buttonstate=MainActivity.BUTTON_ADD;
    }

    public Note getCurrentNote(){
        return currentnote;
    }

    public Note getNewNote(){
        Note newNote=new Note();
        newNote.setTitle(title.getText().toString());
        newNote.setContent(HtmlCompat.toHtml(content.getText(),HtmlCompat.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE));

        //handling size tags problem using Jsoup
            //toHtml() & fromHtml() handles size differently
            //toHtml() set size using style which isn't read by fromHtml()
            //fromHtml() can read <small> <big> <h1> <h2> <h3> ... <h6>
        Document document = Jsoup.parse(newNote.getContent());
        for(Element element:document.select("span")){
            if(element.attr("style").equals("font-size:1.25em;")) element.tagName("big").removeAttr("style");
            else if(element.attr("style").equals("font-size:0.80em;")) element.tagName("small").removeAttr("style");
        }
        newNote.setContent(document.html());

        return newNote;
    }

    public String getMode(){return mode;}

    public void increaseSize(){
        int start = content.getSelectionStart();
        int end = content.getSelectionEnd();
        content.getText().replace(start,end,HtmlCompat.fromHtml("<big>"+content.getText().subSequence(start,end)+"</big>",HtmlCompat.FROM_HTML_MODE_COMPACT));
        content.setSelection(start,end);
    }
    public void decreaseSize(){
        int start = content.getSelectionStart();
        int end = content.getSelectionEnd();
        content.getText().replace(start,end,HtmlCompat.fromHtml("<small>"+content.getText().subSequence(start,end)+"</small>",HtmlCompat.FROM_HTML_MODE_COMPACT));
        content.setSelection(start,end);
    }
    public void styleSpanSwitch(int effect){
        int start = content.getSelectionStart();
        int end = content.getSelectionEnd();
        Boolean applied=false;

        StyleSpan[] spans = content.getText().getSpans(start, end, StyleSpan.class);

        for (StyleSpan styleSpan : spans) {
            if (styleSpan.getStyle() == effect) {
                content.getText().removeSpan(styleSpan);
                applied = true;
                break;
            }
        }

        if(!applied) content.getText().setSpan(new StyleSpan(effect), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    public void underlineSpanSwitch(){
        int start = content.getSelectionStart();
        int end = content.getSelectionEnd();

        UnderlineSpan[] spans = content.getText().getSpans(start, end, UnderlineSpan.class);

        if(spans.length>0)
        {
            for (UnderlineSpan underlineSpan : spans ) {
                content.getText().removeSpan(underlineSpan);
                break;
            }
        }
        else content.getText().setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    public void strikeThroughSpanSwitch(){
        int start = content.getSelectionStart();
        int end = content.getSelectionEnd();

        StrikethroughSpan[] spans = content.getText().getSpans(start, end, StrikethroughSpan.class);

        if(spans.length>0)
        {
            for (StrikethroughSpan strikethroughSpan : spans ) {
                content.getText().removeSpan(strikethroughSpan);
                break;
            }
        }
        else content.getText().setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    public void superScriptSpanSwitch(){
        int start = content.getSelectionStart();
        int end = content.getSelectionEnd();

        SuperscriptSpan[] spans = content.getText().getSpans(start, end, SuperscriptSpan.class);

        if(spans.length>0)
        {
            for (SuperscriptSpan superscriptSpan : spans ) {
                content.getText().removeSpan(superscriptSpan);
                break;
            }
        }
        else content.getText().setSpan(new SuperscriptSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    public void subScriptSpanSwitch(){
        int start = content.getSelectionStart();
        int end = content.getSelectionEnd();

        SubscriptSpan[] spans = content.getText().getSpans(start, end, SubscriptSpan.class);

        if(spans.length>0)
        {
            for (SubscriptSpan subscriptSpan : spans ) {
                content.getText().removeSpan(subscriptSpan);
                break;
            }
        }
        else content.getText().setSpan(new SubscriptSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    public void foregroundColorSpanChanger(int color){
        int start = content.getSelectionStart();
        int end = content.getSelectionEnd();

        ForegroundColorSpan[] spans = content.getText().getSpans(start, end, ForegroundColorSpan.class);

        for (ForegroundColorSpan foregroundColorSpan : spans) content.getText().removeSpan(foregroundColorSpan);

        content.getText().setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
    public void backgroundColorSpanChanger(int color){
        int start = content.getSelectionStart();
        int end = content.getSelectionEnd();

        BackgroundColorSpan[] spans = content.getText().getSpans(start, end, BackgroundColorSpan.class);

        for (BackgroundColorSpan backgroundColorSpan : spans) content.getText().removeSpan(backgroundColorSpan);

        content.getText().setSpan(new BackgroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}