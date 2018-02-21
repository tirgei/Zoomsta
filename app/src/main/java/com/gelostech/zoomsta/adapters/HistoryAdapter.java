package com.gelostech.zoomsta.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gelostech.zoomsta.activities.SearchActivity;
import com.gelostech.zoomsta.activities.ViewProfileActivity;
import com.gelostech.zoomsta.commoners.OverviewDialog;
import com.gelostech.zoomsta.commoners.ZoomstaUtil;
import com.gelostech.zoomsta.fragments.StoriesOverview;
import com.gelostech.zoomsta.models.HistoryModel;
import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.models.UserObject;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tirgei on 10/29/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<HistoryModel> modelList = new ArrayList<>();
    private Activity context;
    private OverviewDialog overviewDialog;
    private FragmentManager fm;
    private int count;
    private InterstitialAd interstitialAd;

    public HistoryAdapter(Activity context, List<HistoryModel> models){
        this.modelList = models;
        this.context = context;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        fm = ((SearchActivity) context).getSupportFragmentManager();
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getResources().getString(R.string.interstitial_ad_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.HistoryViewHolder holder, int position) {
        //holder.setIsRecyclable(false);
        final HistoryModel object = modelList.get(position);

        holder.storyObject.setVisibility(View.VISIBLE);

        holder.userName.setText(object.getUserName());
        holder.userIcon.setImageBitmap(ZoomstaUtil.getImage(object.getUserPic()));

        holder.storyObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = ZoomstaUtil.getIntegerPreference(context, "clickCount");
                if(count > 0 && count % 8 == 0){
                    if(interstitialAd.isLoaded()){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                interstitialAd.show();
                            }
                        }, 250);
                    }
                }

                StoriesOverview overview = new StoriesOverview();
                Bundle args = new Bundle();
                args.putString("username", object.getUserName());
                args.putString("user_id", object.getUserId());
                overview.setArguments(args);
                overview.show(fm, "Story Overview");


            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView userIcon;
        private TextView userName;
        private LinearLayout storyObject;

        public HistoryViewHolder(View view){
            super(view);

            userIcon = view.findViewById(R.id.image_thumb);
            userName = view.findViewById(R.id.prof_name);
            storyObject = view.findViewById(R.id.history_image);

        }
    }
}
