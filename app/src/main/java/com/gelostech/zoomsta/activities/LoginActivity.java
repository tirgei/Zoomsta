package com.gelostech.zoomsta.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gelostech.zoomsta.BuildConfig;
import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.commoners.InstaUtils;
import com.gelostech.zoomsta.commoners.ZoomstaUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.roger.catloadinglibrary.CatLoadingView;

public class LoginActivity extends AppCompatActivity {
    private EditText passwordField;
    private EditText usernameField;
    private String password;
    private String username;
    private Button login;
    private CatLoadingView catLoadingView;
    private InterstitialAd interstitialAd;
    private TextView termsOfUse;
    private CheckBox acceptTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        passwordField = findViewById(R.id.password);
        usernameField = findViewById(R.id.username);
        login = findViewById(R.id.login);
        catLoadingView = new CatLoadingView();
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        termsOfUse = findViewById(R.id.terms_of_use);
        acceptTerms = findViewById(R.id.accept_terms);

        usernameField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == usernameField.getId())
                    usernameField.setCursorVisible(true);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(acceptTerms.isChecked()){
                    if(!TextUtils.isEmpty(usernameField.getText().toString()) || !TextUtils.isEmpty(passwordField.getText().toString())){
                        username = usernameField.getText().toString().trim();
                        password = passwordField.getText().toString();

                        new Sign().execute(new String[0]);
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter username / password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please accept terms and conditions", Toast.LENGTH_LONG).show();
                }
            }
        });

        termsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String url = "https://sites.google.com/view/zoomsta/privacy-policy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }

    private class Sign extends AsyncTask<String, String, String> {
        String resp;

        private Sign() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            catLoadingView.show(getSupportFragmentManager(), null);
            catLoadingView.setCancelable(false);
        }

        protected String doInBackground(String... args) {
            try {
                this.resp = InstaUtils.login(LoginActivity.this.username, LoginActivity.this.password);
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            return null;
        }

        protected void onPostExecute(String img) {
            try {
                if (this.resp.equals(BuildConfig.VERSION_NAME)) {
                    catLoadingView.dismiss();
                    ZoomstaUtil.showToast(LoginActivity.this, "User not found", 1);
                }
                if (this.resp.equals("true")) {
                    ZoomstaUtil.setStringPreference(LoginActivity.this, InstaUtils.getCookies(), "cooki");
                    ZoomstaUtil.setStringPreference(LoginActivity.this, InstaUtils.getCsrf(), "csrf");
                    ZoomstaUtil.setStringPreference(LoginActivity.this, InstaUtils.getUserId(), "userid");
                    ZoomstaUtil.setStringPreference(LoginActivity.this, InstaUtils.getSessionid(), "sessionid");
                    ZoomstaUtil.setStringPreference(LoginActivity.this, username, "username");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            i.putExtra("user", InstaUtils.getUserId());

                            catLoadingView.dismiss();
                            LoginActivity.this.finish();
                            LoginActivity.this.startActivity(i);
                            LoginActivity.this.overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
                        }
                    }, 1500);
                } else if (this.resp.equals("false")) {
                    catLoadingView.dismiss();
                    ZoomstaUtil.showToast(LoginActivity.this, "Incorrect Username / Password", 1);
                } else {
                    catLoadingView.dismiss();
                    ZoomstaUtil.showToast(LoginActivity.this, "Problem occurred logging in. Please try again", 1);
                }
            } catch (Exception e) {
                catLoadingView.dismiss();
                ZoomstaUtil.showToast(LoginActivity.this, "Problem occurred logging in. Please try again", 1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
