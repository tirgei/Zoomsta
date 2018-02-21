package com.gelostech.zoomsta.commoners;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by tirgei on 11/8/17.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseID";

    @Override
    public void onTokenRefresh() {
        String newToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(newToken);

        super.onTokenRefresh();
    }

    private void sendRegistrationToServer(String token){

    }
}
