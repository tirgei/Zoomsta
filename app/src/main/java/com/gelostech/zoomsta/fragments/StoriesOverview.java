package com.gelostech.zoomsta.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.adapters.StoriesOverViewAdapter;
import com.gelostech.zoomsta.commoners.InstaUtils;
import com.gelostech.zoomsta.commoners.ZoomstaUtil;
import com.gelostech.zoomsta.models.StoryModel;
import com.gelostech.zoomsta.models.UserObject;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tirgei on 11/4/17.
 */

public class StoriesOverview extends DialogFragment {
    private TextView username;
    private RecyclerView recyclerView;
    private ArrayList<String> modelList;
    private ArrayList<StoryModel> stories;
    private StoriesOverViewAdapter adapter;
    private UserObject user;
    private String name, id;
    private LinearLayout noNet, noStories;
    private SpinKitView wave;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stories_overview, container, false);

        username = view.findViewById(R.id.stories_overview_username);
        recyclerView = view.findViewById(R.id.stories_overview_rv);
        noNet = view.findViewById(R.id.no_net_overview_stories);
        wave = view.findViewById(R.id.loading_stories_overview);
        noStories = view.findViewById(R.id.no_stories_found);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelList = new ArrayList<>();
        stories = new ArrayList<>();
        adapter = new StoriesOverViewAdapter(getActivity(), modelList, stories);
        recyclerView.setAdapter(adapter);

        name = getArguments().getString("username");
        id = getArguments().getString("user_id");
        username.setText(name);

        int count = ZoomstaUtil.getIntegerPreference(getActivity(), "clickCount");
        count++;
        if(count < 6562)
            ZoomstaUtil.setIntegerPreference(getActivity(), count, "clickCount");
        else
            ZoomstaUtil.setIntegerPreference(getActivity(), 1, "clickCount");
        setStories();

        return view;
    }

    private void setStories(){
        if(ZoomstaUtil.haveNetworkConnection(getActivity())){
            new GetStoriesFeed().execute(new String[0]);
        } else {
            noNet.setVisibility(View.VISIBLE);
        }

    }

    private class GetStoriesFeed extends AsyncTask<String, String, String>{
        private String response;

        private GetStoriesFeed(){}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            wave.setVisibility(View.VISIBLE);
            if(noNet.isShown())
                noNet.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... args) {
            try{
                modelList.addAll(InstaUtils.stories(id, getActivity()));
                stories.addAll(InstaUtils.fetchStories(id, getActivity()));
            } catch (Exception e){
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            wave.setVisibility(View.GONE);

            if(modelList.size() == 0)
                noStories.setVisibility(View.VISIBLE);
            else
                adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
