package com.example.mju.smile;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

// FCM 로 부터 RegID를 받으며 RegID 가 갱신되면 반영하도록 하는 기능 수행
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FCM_ID";
    protected static String token = "";

    public FirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "My Token : " + token);
    }

    public static String getToken(){return token;}

}
