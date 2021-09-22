package amrk000.notbad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class about extends AppCompatActivity {
    TextView appversion;
    Button moreapps;
    ImageView appicon;
    View credit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        appicon = findViewById(R.id.appcion);
        appversion= findViewById(R.id.appversion);
        moreapps = findViewById(R.id.moreapps);
        credit= findViewById(R.id.credit);

        credit.setVisibility(View.INVISIBLE);

        appversion.setText("Version "+BuildConfig.VERSION_NAME);

        moreapps.setOnClickListener((View v)->{
            Intent profilelink = new Intent(Intent.ACTION_VIEW);
            profilelink.setData(Uri.parse("https://play.google.com/store/apps/dev?id=5289896800613171020"));
            startActivity(profilelink);
        });

        new Handler().postDelayed(()->{
            credit.setVisibility(View.VISIBLE);
            credit.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide));
        },250);

    }


}