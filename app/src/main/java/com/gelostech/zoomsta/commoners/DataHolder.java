package com.gelostech.zoomsta.commoners;

import android.util.Log;

import com.gelostech.zoomsta.models.StoryModel;
import com.gelostech.zoomsta.models.UserObject;

import java.util.List;

/**
 * Created by tirgei on 11/4/17.
 */

public enum DataHolder {
    INSTANCE;

    private List<StoryModel> mObjectList;

    public static void setData(final List<StoryModel> objectList) {
        INSTANCE.mObjectList = objectList;
        Log.d("data", "Data is set" + objectList.size());
    }

    public static List<StoryModel> getData() {
        final List<StoryModel> retList = INSTANCE.mObjectList;
        INSTANCE.mObjectList = null;
        Log.d("data", "Data is fetched");
        return retList;
    }
}