package com.gelostech.zoomsta.activities;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.adapters.StoryViewPagerAdapter;
import com.gelostech.zoomsta.commoners.DataHolder;
import com.gelostech.zoomsta.commoners.RefreshListener;
import com.gelostech.zoomsta.commoners.StoryListener;
import com.gelostech.zoomsta.commoners.ZoomstaUtil;
import com.gelostech.zoomsta.models.StoryModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

public class ViewStoryActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, StoryListener {
    private ViewPager viewPager;
    private StoryViewPagerAdapter adapter;
    private ArrayList<StoryModel> modelList;
    private int position = 0, totalPages;
    private List<String> urls;
    private Boolean isFromNet;
    private AdView adView;
    private InterstitialAd interstitialAd;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

        viewPager = findViewById(R.id.story_view_pager);
        adView = findViewById(R.id.story_banner_ad);
        interstitialAd = new InterstitialAd(this);

        adView.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
        modelList = new ArrayList<>();
        urls = new ArrayList<>();

        loadStories();
        adapter = new StoryViewPagerAdapter(getSupportFragmentManager(), modelList, isFromNet, urls);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(position);


    }

    private void loadStories(){
        isFromNet = getIntent().getBooleanExtra("isFromNet", false);
        Boolean isLarge = getIntent().getBooleanExtra("isLarge", false);
        if(!isFromNet){
            if(isLarge){
                Log.d("data", "Receiving large data...");
                modelList = (ArrayList<StoryModel>) DataHolder.getData();

            } else {
                modelList = getIntent().getParcelableArrayListExtra("storylist");
                Log.d("data", "Data received is small");

            }
            position = getIntent().getIntExtra("pos", 0);
            totalPages = modelList.size();
        } else {
            urls = getIntent().getStringArrayListExtra("urls");
            position = getIntent().getIntExtra("pos", 0);
            totalPages = urls.size();
        }

        //adapter.notifyDataSetChanged();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        count++;

        if(count > 0 && count % 6 == 0){
            if(interstitialAd.isLoaded()){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        interstitialAd.show();
                    }
                }, 250);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.enter_signin, R.anim.exit_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void deletePage(Boolean delete) {
        if(delete)
            adapter.deletePage(viewPager.getCurrentItem());

        if(viewPager.getChildCount() == 0){
            ZoomstaUtil.setBooleanPreference(this, "refreshSaved", true);
            finish();
        }
    }
}
