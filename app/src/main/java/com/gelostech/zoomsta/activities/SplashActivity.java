package com.gelostech.zoomsta.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.gelostech.zoomsta.BuildConfig;
import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.commoners.InstaUtils;
import com.gelostech.zoomsta.commoners.ZoomstaUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class SplashActivity extends AppCompatActivity {
    private TextView splashTitle;
    private ImageView imageView;
    private static final String font = "Billabong.ttf";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        splashTitle = findViewById(R.id.splash_title);
        imageView = findViewById(R.id.spash_logo);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true);
        final String id = UUID.randomUUID().toString();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference df = db.getReference("users");
        final String date = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime());

        if(isFirstLaunch){
            df.child(id).setValue(date);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLaunch", false);
            editor.apply();
        }

        final Typeface typeface = Typeface.createFromAsset(getAssets(), font);
        splashTitle.setTypeface(typeface);

        final Animation zoomin = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        final Animation zoomout = AnimationUtils.loadAnimation(this, R.anim.zoom_out);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageView.animate().rotation(100).start();
            }
        }, 300);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //imageView.setAnimation(zoomout);
                imageView.animate().rotation(-60).start();
            }
        },1600);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (ZoomstaUtil.getStringPreference(SplashActivity.this, "userid").equals(BuildConfig.VERSION_NAME)) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                    InstaUtils.setCookies(ZoomstaUtil.getStringPreference(SplashActivity.this, "cookie"));
                    InstaUtils.setCsrf(ZoomstaUtil.getStringPreference(SplashActivity.this, "csrf"), null);
                    InstaUtils.setUserId(ZoomstaUtil.getStringPreference(SplashActivity.this, "userid"));
                    InstaUtils.setSessionId(ZoomstaUtil.getStringPreference(SplashActivity.this, "sessionid"));

                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
                }
            }
        }, 2500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
