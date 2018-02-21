package com.gelostech.zoomsta.fragments;


import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gelostech.zoomsta.R;
import com.gelostech.zoomsta.adapters.SavedStoriesAdapter;
import com.gelostech.zoomsta.commoners.ZoomstaUtil;
import com.gelostech.zoomsta.models.StoryModel;
import com.github.ybq.android.spinkit.SpinKitView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SavedStoriesFragment extends Fragment {
    private ArrayList<StoryModel> storyModels;
    private RecyclerView recyclerView;
    private SavedStoriesAdapter adapter;
    private LinearLayout noSaved;
    private SpinKitView wave;
    private SwipeRefreshLayout refreshLayout;


    public SavedStoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_saved_stories, container, false);

        storyModels = new ArrayList<>();
        recyclerView = view.findViewById(R.id.saved_stories_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        adapter = new SavedStoriesAdapter(getActivity(), storyModels);
        noSaved = view.findViewById(R.id.no_saved_stories);
        wave = view.findViewById(R.id.loading_saved_stories);
        refreshLayout = view.findViewById(R.id.refresh_saved_stories);

        if(Build.VERSION.SDK_INT >= 23)
            checkPermission();
        else
            loadFiles();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!storyModels.isEmpty())
                    storyModels.clear();

                recyclerView.setVisibility(View.GONE);

                loadFiles();

            }
        });

        return view;
    }

    private void checkPermission(){
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        loadFiles();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        PermissionListener dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                                .withContext(getActivity())
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

    public void loadFiles(){
        if(!storyModels.isEmpty()){
            storyModels.clear();
            adapter.notifyDataSetChanged();
        }
        if(noSaved.isShown())
            noSaved.setVisibility(View.GONE);

        StoryModel storyModel = null;

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Zoomsta";

        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();

        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".png") || s.endsWith(".jpg") || s.endsWith(".jpeg") || s.endsWith(".mp4") || s.endsWith(".avi");
            }
        });

        if(files.length != 0){
            if(!refreshLayout.isRefreshing())
                wave.setVisibility(View.VISIBLE);
            if(storyModels.size() > 0)
                storyModels.clear();

            for(File f:files){
                if(isImage(f))
                    storyModel = new StoryModel(f.getAbsolutePath(), f.getName(), 0, true);
                if(isVideo(f))
                    storyModel = new StoryModel(f.getAbsolutePath(), f.getName(), 1, true);

                storyModels.add(storyModel);

            }

            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            wave.setVisibility(View.GONE);
            if(noSaved.isShown())
                noSaved.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if(refreshLayout.isRefreshing()){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }

        } else {
            if(wave.isShown())
                wave.setVisibility(View.GONE);

            if(refreshLayout.isRefreshing()){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }

            noSaved.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }

    public static boolean isImage(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith("jpg") || fileName.endsWith("jpeg") || fileName.endsWith("png");
    }

    public static boolean isVideo(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith("mp4") || fileName.endsWith("avi") || fileName.endsWith("gif") || fileName.endsWith("mkv");
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter.notifyDataSetChanged();
        noSaved.setVisibility(View.GONE);

        if(Build.VERSION.SDK_INT >= 23)
            checkPermission();
        else
            loadFiles();


    }


}
