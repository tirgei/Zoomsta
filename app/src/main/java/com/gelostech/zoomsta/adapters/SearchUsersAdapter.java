package com.gelostech.zoomsta.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.activities.SearchActivity;
import com.gelostech.zoomsta.commoners.DatabaseHelper;
import com.gelostech.zoomsta.commoners.OverviewDialog;
import com.gelostech.zoomsta.commoners.ZoomstaUtil;
import com.gelostech.zoomsta.fragments.StoriesOverview;
import com.gelostech.zoomsta.models.UserObject;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tirgei on 10/31/17.
 */

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.SearchUsersHolder> {
    private static final String TAG = "SearchUsersAdapter";
    private Activity context;
    private List<UserObject> userObjects;
    private OverviewDialog overviewDialog;
    private FragmentManager fm;
    private InterstitialAd interstitialAd;
    private int count;
    private DatabaseHelper databaseHelper;
    private int prof;

    public SearchUsersAdapter(Activity context, List<UserObject> list){
        this.userObjects = list;
        this.context = context;

    }

    @Override
    public SearchUsersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.story_list_object, parent, false);
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
        databaseHelper = new DatabaseHelper(context);

        return new SearchUsersHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchUsersHolder holder, final int position) {
        //holder.setIsRecyclable(false);
        final UserObject object = userObjects.get(position);
        fm = ((SearchActivity) context).getSupportFragmentManager();
        final Bitmap[] bitmap = new Bitmap[1];

        Log.d("searched", "Version 2:" + object.getUserName());

        holder.storyObject.setVisibility(View.VISIBLE);

        holder.realName.setText(object.getRealName());
        holder.userName.setText(object.getUserName());
        Glide.with(context).load(object.getImage()).thumbnail(0.1f).into(holder.userIcon);
        Glide.with(context).asBitmap().load(object.getImage()).thumbnail(0.1f).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                bitmap[0] = resource;
                //holder.userIcon.setImageBitmap(resource);
            }
        });

        holder.userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHelper.addHistory(object.getUserId(), object.getUserName(), object.getRealName(), bitmap[0]);

                if(object.getUserId().equals(ZoomstaUtil.getStringPreference(context, "userid"))){
                    overviewDialog = new OverviewDialog(context, bitmap[0], object.getUserName(), true);
                    overviewDialog.show();
                } else {
                    overviewDialog = new OverviewDialog(context, object);
                    overviewDialog.show();
                }

                prof = ZoomstaUtil.getIntegerPreference(context, "profCount");
                if(prof > 0 && prof % 13 == 0){
                    if(interstitialAd.isLoaded()){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                interstitialAd.show();
                            }
                        }, 250);
                    }
                }

                if(context instanceof SearchActivity){
                    ((SearchActivity)context).fetchHistory();
                }
            }
        });

        holder.storyObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap[0]!=null)
                    databaseHelper.addHistory(object.getUserId(), object.getUserName(), object.getRealName(), bitmap[0]);

                count = ZoomstaUtil.getIntegerPreference(context, "clickCount");
                if(count > 0 && count % 9 == 0){
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

                if(context instanceof SearchActivity){
                    ((SearchActivity)context).fetchHistory();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return userObjects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class SearchUsersHolder extends RecyclerView.ViewHolder{
        private CircleImageView userIcon;
        private TextView userName;
        private TextView realName;
        private RelativeLayout storyObject;

        public SearchUsersHolder(View view){
            super(view);

            userIcon = view.findViewById(R.id.story_icon);
            userName = view.findViewById(R.id.user_name);
            realName = view.findViewById(R.id.real_name);
            storyObject = view.findViewById(R.id.story_object);

        }
    }

}
