package com.gelostech.zoomsta.commoners;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gelostech.zoomsta.BuildConfig;
import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.activities.ViewProfileActivity;
import com.gelostech.zoomsta.models.UserObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tirgei on 11/1/17.
 */

public class OverviewDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private String image;
    private String username;
    private Button openInsta;
    private ImageView imageView;
    private TextView userText;
    private String link = null;
    private Bitmap bitmap = null;
    private ProgressBar progressBar;
    private ImageButton imageButton;
    private String userId;
    private Boolean isFave = false;
    private UserObject object;
    private Boolean isMe = false;

    public OverviewDialog(Activity activity, Bitmap bitmap, String username, Boolean isMe){
        super(activity);

        this.activity = activity;
        this.bitmap = bitmap;
        this.username = username;
        this.isMe = isMe;
    }

    public OverviewDialog(Activity activity, UserObject userObject){
        super(activity);

        this.activity  = activity;
        this.object = userObject;
        this.image = userObject.getImage();
        this.username = userObject.getUserName();
        this.isFave = userObject.getFaved();
        this.userId = userObject.getUserId();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_prof_overview);

        imageView = findViewById(R.id.overview_image);
        openInsta = findViewById(R.id.overview_button);
        userText = findViewById(R.id.overview_username);
        progressBar = findViewById(R.id.loading_overview_image);
        imageButton = findViewById(R.id.fave_user_button);
        if(isFave && !isMe)
            imageButton.setImageResource(R.drawable.ic_bookmark_selected);
        else if(!isMe && !isFave)
            imageButton.setImageResource(R.drawable.ic_bookmark_unselected);
        else if(isMe)
            imageButton.setVisibility(View.GONE);

        loadPic(username);
        if(!isMe)
            Glide.with(activity).load(image).thumbnail(0.2f).into(imageView);
        else
            imageView.setImageBitmap(bitmap);

        userText.setText(username);

        int count = ZoomstaUtil.getIntegerPreference(activity, "profCount");
        count++;
        if(count < 2822)
            ZoomstaUtil.setIntegerPreference(activity, count, "profCount");
        else
            ZoomstaUtil.setIntegerPreference(activity, 1, "profCount");

        openInsta.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        imageView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.overview_button:
                final String appLink = "http://instagram.com/_u/" + username;
                final String webLink = "http://instagram.com/" + username;

                Uri uri = Uri.parse(appLink);
                Intent insta = new Intent(Intent.ACTION_VIEW, uri);
                insta.setPackage("com.instagram.android");

                if (isIntentAvailable(activity, insta)){
                    activity.startActivity(insta);
                } else{
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webLink)));
                }

                break;

            case R.id.fave_user_button:
                if(!isFave){
                    ZoomstaUtil.addFaveUser(activity, userId);
                    imageButton.setImageResource(R.drawable.ic_bookmark_selected);
                    object.setFaved(true);
                    isFave = true;
                    Toast.makeText(activity, username + " added to fave IG'ers", Toast.LENGTH_SHORT).show();

                } else {
                    ZoomstaUtil.removeFaveUser(activity, userId);
                    imageButton.setImageResource(R.drawable.ic_bookmark_unselected);
                    object.setFaved(false);
                    isFave = false;
                    Toast.makeText(activity, username + " removed from fave IG'ers", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.overview_image:
                if(bitmap == null)
                    Toast.makeText(activity, "Please wait for the image to load", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void loadPic(final String username) {

        final String requestImage = "https://www.instagram.com/" + username + "/";

        new Thread() {
            @Override
            public void run() {
                super.run();

                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(requestImage).openConnection();
                    connection.connect();

                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    String html = BuildConfig.FLAVOR;

                    for (int data = reader.read(); data != -1; data = reader.read()) {
                        html = html + ((char) data);
                    }

                    Matcher matcher = Pattern.compile("profile_pic_url_hd\": \"(.*?)\"").matcher(html);
                    if (matcher.find()) {
                        String picLink = matcher.group(1);
                        link = matcher.group(1);
                    }

                    if (link != null) {
                        final String newLink = link.replaceAll("/s320x320", "+");

                        connection = (HttpURLConnection) new URL(newLink).openConnection();
                        connection.connect();

                        try {
                            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bitmap != null) {
                                    imageView.setImageBitmap(null);
                                    imageView.setImageBitmap(bitmap);
                                    progressBar.setVisibility(View.GONE);
                                    imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (bitmap != null) {
                                                Intent intent = new Intent(activity, ViewProfileActivity.class);
                                                ZoomstaUtil.createImageFromBitmap(activity, bitmap);
                                                activity.startActivity(intent);
                                            }
                                        }
                                    });

                                }


                                link = null;
                            }
                        });
                    }

                } catch (final Exception e) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (e.getMessage().equals("Unable to resolve host \"www.instagram.com\": Unknown error")) {
                                //Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(activity, "Wrong username entered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }.start();

    }
}
