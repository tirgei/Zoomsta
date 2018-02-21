package com.gelostech.zoomsta.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.gelostech.zoomsta.BuildConfig;
import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.commoners.DatabaseHelper;
import com.gelostech.zoomsta.commoners.ExitDialog;
import com.gelostech.zoomsta.commoners.OverviewDialog;
import com.gelostech.zoomsta.commoners.TermsDialog;
import com.gelostech.zoomsta.commoners.ZoomstaUtil;
import com.gelostech.zoomsta.fragments.FaveStoriesFragment;
import com.gelostech.zoomsta.fragments.SavedStoriesFragment;
import com.gelostech.zoomsta.fragments.StoriesFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private static final String font = "Billabong.ttf";
    private MenuItem userIcon;
    private ActionBarDrawerToggle drawerToggle;
    private  Toolbar toolbar;
    private boolean doubleBackToExit = false;
    private NavigationView drawer;
    private static final String storeLink = "https://play.google.com/store/apps/details?id=";
    private DatabaseHelper databaseHelper;
    private InterstitialAd interstitialAd;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private final int[] tabIcons = {R.drawable.ic_feed, R.drawable.ic_fave, R.drawable.ic_saved};
    private Bitmap bitmap, icon = null;
    private String link, username;
    private OverviewDialog overviewDialog;
    private ExitDialog exitDialog;
    private static final String DB_TABLE = "zoomsta_user";
    private TermsDialog termsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initItems();
        checkStoragePermissions(MainActivity.this);

        setupTabLayout();

        username = ZoomstaUtil.getStringPreference(MainActivity.this, "username");
        if(databaseHelper.hasObject(username, DB_TABLE))
            icon = databaseHelper.getUserIcon(this, username);
        else
            icon = null;

        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        if(ZoomstaUtil.haveNetworkConnection(this))
            loadPic(username);

        drawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                handlerDrawerClick(id);

                return true;
            }
        });

    }

    private void initItems(){
        toolbar = findViewById(R.id.main_toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        viewPager = findViewById(R.id.main_viewpager);
        tabLayout = findViewById(R.id.tabs);
        drawer = findViewById(R.id.drawer);

        databaseHelper = new DatabaseHelper(this);
        interstitialAd = new InterstitialAd(this);
        exitDialog = new ExitDialog(this);

        final TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        final Typeface typeface = Typeface.createFromAsset(getAssets(), font);

        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        mTitle.setTypeface(typeface);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setupDrawerToggle();
        viewPager.setOffscreenPageLimit(2);
        termsDialog = new TermsDialog(this);
        termsDialog.setCancelable(false);
    }

    private void checkStoragePermissions(final Context context){
        if(Build.VERSION.SDK_INT >= 23){
            Dexter.withActivity(this)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            PermissionListener dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                                    .withContext(context)
                                    .withTitle("Storage permission")
                                    .withMessage("Storage permission is needed to save pictures")
                                    .withButtonText(android.R.string.ok)
                                    .build();

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }
    }

    private void setupTabLayout(){
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        //setUpTabIcons();

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StoriesFragment(), "STORIES");
        adapter.addFragment(new FaveStoriesFragment(), "FAVE");
        adapter.addFragment(new SavedStoriesFragment(), "SAVED");
        viewPager.setAdapter(adapter);
    }

    private void setUpTabIcons(){
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupDrawerToggle(){
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        userIcon = menu.findItem(R.id.toolbar_icon);
        if(icon != null){
            userIcon.setIcon(new BitmapDrawable(getResources(), cropCircle(icon)));
            userIcon.setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.toolbar_icon:
                    overviewDialog = new OverviewDialog(this, icon, username, true);
                    overviewDialog.show();
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawers();

        } else {
            if(doubleBackToExit){
                super.onBackPressed();

            } else {
                doubleBackToExit = true;

                Toast.makeText(this, "Tap BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExit = false;
                    }
                }, 2000);
            }
        }

    }

    private void handlerDrawerClick(int id){
        switch (id){
            case R.id.navigation_exit:
                drawerLayout.closeDrawers();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitDialog.show();
                    }
                }, 300);

                //Toast.makeText(MainActivity.this, "Log out clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.navigation_rate:
                drawerLayout.closeDrawers();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Uri uri = Uri.parse(storeLink + getPackageName());
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse(storeLink + getPackageName())));
                        }
                    }
                }, 300);

                //Toast.makeText(MainActivity.this, "Open play store", Toast.LENGTH_SHORT).show();
                break;

            case R.id.navigation_share:
                drawerLayout.closeDrawers();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                        String message = getResources().getString(R.string.invite_body) + "\n\n" + storeLink + getPackageName();
                        intent.putExtra(Intent.EXTRA_TEXT, message);
                        startActivity(Intent.createChooser(intent, "Send link to..."));
                    }
                }, 300);

                //Toast.makeText(MainActivity.this, "Share to friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.navigation_mailus:
                drawerLayout.closeDrawers();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setData(Uri.parse("mailto: zoomsta.17@gmail.com"));
                        startActivity(Intent.createChooser(emailIntent, "Send feedback"));
                    }
                }, 300);

                //Toast.makeText(MainActivity.this, "Send feedback", Toast.LENGTH_SHORT).show();
                break;

            case R.id.navigation_tac:
                drawerLayout.closeDrawers();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final String url = "https://sites.google.com/view/zoomsta/terms-and-conditions";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);

                    }
                }, 300);
                break;

            default:
                break;


        }
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (bitmap != null) {
                                    userIcon.setIcon(new BitmapDrawable(getResources(), cropCircle(bitmap)));
                                    databaseHelper.insertIntoDb(username, bitmap);
                                }


                                link = null;
                            }
                        });
                    }

                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public Bitmap cropCircle(Bitmap bm){

        int width = bm.getWidth();
        int height = bm.getHeight();

        Bitmap cropped_bitmap;

    /* Crop the bitmap so it'll display well as a circle. */
        if (width > height) {
            cropped_bitmap = Bitmap.createBitmap(bm,
                    (width / 2) - (height / 2), 0, height, height);
        } else {
            cropped_bitmap = Bitmap.createBitmap(bm, 0, (height / 2)
                    - (width / 2), width, width);
        }

        BitmapShader shader = new BitmapShader(cropped_bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        height = cropped_bitmap.getHeight();
        width = cropped_bitmap.getWidth();

        Bitmap mCanvasBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(mCanvasBitmap);
        canvas.drawCircle(width/2, height/2, width/2, paint);

        return mCanvasBitmap;
    }


    class ViewPagerAdapter extends FragmentPagerAdapter{
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        private void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
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
