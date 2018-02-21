package com.gelostech.zoomsta.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import com.gelostech.zoomsta.fragments.StoryFragment;
import com.gelostech.zoomsta.models.StoryModel;

import java.util.List;

/**
 * Created by tirgei on 11/4/17.
 */

public class StoryViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<StoryModel> modelList;
    private List<String> list;
    private Boolean isFromNet;

    public StoryViewPagerAdapter(FragmentManager fm, List<StoryModel> models, Boolean isFromNet, List<String> list){
        super(fm);

        this.modelList = models;
        this.isFromNet = isFromNet;
        this.list = list;

    }

    @Override
    public Fragment getItem(int position) {
        StoryFragment storyFragment;

        if(isFromNet){
            storyFragment = StoryFragment.newInstance(true);
            String url = list.get(position);
            storyFragment.getStories(url);

        } else {
            storyFragment = StoryFragment.newInstance(false);
            StoryModel story = modelList.get(position);
            storyFragment.setStoryList(story);
        }

        return storyFragment;
    }

    @Override
    public int getCount() {
        if(!isFromNet)
            return modelList.size();
        else
            return list.size();
    }

    public void deletePage(int position) {
        if (canDelete()) {
            modelList.remove(position);
            notifyDataSetChanged();
        }
    }

    boolean canDelete() {
        return modelList.size() > 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }


}
