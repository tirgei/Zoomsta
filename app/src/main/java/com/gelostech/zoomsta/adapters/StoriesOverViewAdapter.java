package com.gelostech.zoomsta.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.activities.ViewStoryActivity;
import com.gelostech.zoomsta.commoners.DataHolder;
import com.gelostech.zoomsta.commoners.SquareLayout;
import com.gelostech.zoomsta.commoners.ZoomstaUtil;
import com.gelostech.zoomsta.models.StoryModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tirgei on 11/4/17.
 */

public class StoriesOverViewAdapter extends RecyclerView.Adapter<StoriesOverViewAdapter.StoriesOverViewHolder> {
    private List<String> modelList;
    private List<StoryModel> storyModels;
    private Context context;
    private int count;
    private InterstitialAd interstitialAd;

    public StoriesOverViewAdapter(Context context, List<String> models, List<StoryModel> storyModels){
        this.context = context;
        this.modelList = models;
        this.storyModels = storyModels;
    }

    @Override
    public StoriesOverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stories_overview_object, parent, false);
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
        return new StoriesOverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StoriesOverViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final String model = modelList.get(position);

        Glide.with(context).load(model).thumbnail(0.2f).into(holder.imageView);
        holder.layout.setVisibility(View.VISIBLE);
        if(!model.endsWith(".jpg"))
            holder.isVideo.setVisibility(View.VISIBLE);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, ViewStoryActivity.class);
//                DataHolder.setData(storyModels);
//                intent.putExtra("isLarge", true);
//                intent.putExtra("pos", position);
//                context.startActivity(intent);
                count = ZoomstaUtil.getIntegerPreference(context, "itemCount");
                if(count > 0 && count % 13  == 0){
                    if(interstitialAd.isLoaded()){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                interstitialAd.show();
                            }
                        }, 250);
                    }
                }

                Intent intent = new Intent(context, ViewStoryActivity.class);
                intent.putExtra("isFromNet", true);
                intent.putStringArrayListExtra("urls", (ArrayList<String>) modelList);
                intent.putExtra("pos", position);
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);
            }
        });


    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class StoriesOverViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private ImageView isVideo;
        private SquareLayout layout;

        public StoriesOverViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.overview_media_holder);
            isVideo = itemView.findViewById(R.id.overview_is_video);
            this.layout = itemView.findViewById(R.id.select_stories_overview_item);
        }
    }



}
