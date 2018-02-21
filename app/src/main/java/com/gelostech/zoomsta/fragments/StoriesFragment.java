package com.gelostech.zoomsta.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.adapters.StoriesListAdapter;
import com.gelostech.zoomsta.commoners.InstaUtils;
import com.gelostech.zoomsta.commoners.ZoomstaUtil;
import com.gelostech.zoomsta.models.UserObject;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoriesFragment extends Fragment{
    private List<UserObject> userObjectList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StoriesListAdapter adapter;
    private SpinKitView wave;
    private LinearLayout noNet;
    private ImageButton noNetRefresh;
    private SwipeRefreshLayout refreshLayout;
    private LinearLayout noStories;
    private TextView text1, text2;

    public StoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private class GetStoriesFeed extends AsyncTask<Boolean, String, String>{
        private String response;

        private GetStoriesFeed(){}

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!refreshLayout.isRefreshing())
                wave.setVisibility(View.VISIBLE);
            if(noNet.isShown())
                noNet.setVisibility(View.GONE);
            if(!userObjectList.isEmpty())
                userObjectList.clear();
            adapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(Boolean... booleans) {
            try {
                userObjectList.addAll(InstaUtils.usersList(getActivity()));

            } catch (Exception e){
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(userObjectList.size() == 0){
                text1.setText(R.string.no_fave_stories);
                text2.setText(R.string.refresh);
                noStories.setVisibility(View.VISIBLE);
            }
            else
                adapter.notifyDataSetChanged();


            wave.setVisibility(View.GONE);
            if(refreshLayout.isRefreshing()){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }

            if(noStories.isShown())
                noStories.setVisibility(View.GONE);

            if(!recyclerView.isShown())
                recyclerView.setVisibility(View.VISIBLE);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stories, container, false);

        Log.d("StoriesFragment", "Loading stories");
        recyclerView = view.findViewById(R.id.stories_rv);
        wave = view.findViewById(R.id.loading_stories);
        noNet = view.findViewById(R.id.no_net_stories);
        refreshLayout = view.findViewById(R.id.refresh_stories);
        noNetRefresh = view.findViewById(R.id.refresh_stories_button);
        noStories = view.findViewById(R.id.no_stories);
        text1 = view.findViewById(R.id.text1);
        text2 = view.findViewById(R.id.text2);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new StoriesListAdapter(getActivity(), userObjectList);
        recyclerView.setAdapter(adapter);

        loadStories();

        noNetRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadStories();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!userObjectList.isEmpty())
                    userObjectList.clear();

                loadStories();
            }
        });

        return view;
    }

    private void loadStories(){
        if(ZoomstaUtil.haveNetworkConnection(getActivity())){
            new GetStoriesFeed().execute(new Boolean[]{Boolean.FALSE});
        } else {
            noNet.setVisibility(View.VISIBLE);
            if(refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
    }

}
