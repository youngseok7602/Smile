package com.example.jeong.test_firebase;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Jeong on 2017-11-26.
 */

public class PermissionUtil {

    public static final int REQUEST_STORAGE = 1;
    public static final String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_PHONE_STATE
    };

    public static boolean checkPermissions(Activity activity, String permission){

        int permissionResult = ActivityCompat.checkSelfPermission(activity, permission);

        if(permissionResult == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    public static void requestPermissions(Activity activity){
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_STORAGE);
    }

    public static boolean verifyPermission(int[] grantresults){
        if(grantresults.length < 1){
            return false;
        }

        for(int result : grantresults){
            if(result != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }

        return true;
    }

}
