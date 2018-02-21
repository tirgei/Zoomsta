package com.gelostech.zoomsta.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.activities.ViewStoryActivity;
import com.gelostech.zoomsta.commoners.DataHolder;
import com.gelostech.zoomsta.models.StoryModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tirgei on 11/3/17.
 */

public class SavedStoriesAdapter extends RecyclerView.Adapter<SavedStoriesAdapter.SavedStoriesViewHolder> {
    private ArrayList<StoryModel> modelList = new ArrayList<>();
    private Context context;

    public static class SavedStoriesViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private RelativeLayout selectItem;
        private ImageView imageViewVideo;

        public SavedStoriesViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.media_holder);
            selectItem = itemView.findViewById(R.id.select_saved_item);
            imageViewVideo = itemView.findViewById(R.id.is_video);
        }

    }

    public SavedStoriesAdapter(Context context, ArrayList<StoryModel> models) {
        this.context = context;
        this.modelList = models;
    }

    @Override
    public SavedStoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_story_object, parent, false);

        return new SavedStoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SavedStoriesViewHolder holder, final int position) {
        final StoryModel model = modelList.get(position);

        if (model.getType() == 0) {
            Glide.with(context).load(model.getFilePath()).thumbnail(0.5f).into(holder.imageView);

            holder.imageViewVideo.setVisibility(View.GONE);

        } else if (model.getType() == 1) {
            Glide.with(context).load(model.getFilePath()).thumbnail(0.5f).into(holder.imageView);
            holder.imageViewVideo.setVisibility(View.VISIBLE);
        }

        holder.selectItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, ViewStoryActivity.class);
                if(modelList.size() <= 300){
                    intent.putParcelableArrayListExtra("storylist", modelList);
                    Log.d("data", "Data is small");
                } else {
                    Log.d("data", "Setting data....");
                    DataHolder.setData(modelList);
                    intent.putExtra("isLarge", true);
                }
                intent.putExtra("pos", position);
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.enter_main, R.anim.exit_splash);

                Log.d("issaved", "" + model.getSaved().toString());
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

}
