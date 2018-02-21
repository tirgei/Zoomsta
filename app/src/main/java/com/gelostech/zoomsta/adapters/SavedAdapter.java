package com.gelostech.zoomsta.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.activities.ViewProfileActivity;
import com.gelostech.zoomsta.models.StoryModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tirgei on 10/29/17.
 */

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.SavedViewHolder> {
    private List<StoryModel> modelList = new ArrayList<>();
    private Context context;

    public SavedAdapter(Context context, List<StoryModel> models){
        this.modelList = models;
        this.context = context;
    }

    @Override
    public SavedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_item, parent, false);

        return new SavedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SavedAdapter.SavedViewHolder holder, int position) {
        final StoryModel model = modelList.get(position);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(model.getFilePath(), options);
        holder.imageView.setImageBitmap(bitmap);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewProfileActivity.class);
                intent.putExtra("type", "from_saved");
                intent.putExtra("filepath", model.getFilePath());
                context.startActivity(intent);
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

    public class SavedViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;

        public SavedViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.saved_image);

        }
    }
}
