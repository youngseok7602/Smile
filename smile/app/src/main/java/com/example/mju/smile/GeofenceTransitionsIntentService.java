package com.example.mju.smile;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//Geofence 기능을 수행하는 클래스
public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent event = GeofencingEvent.fromIntent(intent);

        List<Geofence> triggeringGeofences = event.getTriggeringGeofences();
        ArrayList triggeringGeofencesIdsList = new ArrayList();
        for(Geofence geofence : triggeringGeofences)
            triggeringGeofencesIdsList.add(geofence.getRequestId());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String sDate = TextUtils.join(", ", triggeringGeofencesIdsList);
        String now = formatter.format(new Date());

        Date dday = null;
        Date nowDate = null;

        try{
            dday = formatter.parse(sDate);
            nowDate = formatter.parse(now);
        }catch(ParseException e){
            e.printStackTrace();
        }

        int compare = dday.compareTo(nowDate);

        // 설정한 디데이와 같은 날이거나 디데이가 지나면 지오펜스에 들어왔는지 체크
        if(compare <= 0){
            int transitiontype = event.getGeofenceTransition();
            if(transitiontype == Geofence.GEOFENCE_TRANSITION_ENTER){
                pushNotification(sDate);
            }
        }

    }

    //디데이 + 지오펜스 존에 접속중이면 알람 발생
    private void pushNotification(String sDate){

        NotificationManager noManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent noIntent = new Intent(this, MainActivity.class);
        noIntent.putExtra("noID", 10);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 3, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle("Smile")
                .setContentText("친구와 만들었던 추억을 확인해 보세요 (" + sDate + ")")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND);

        noManager.notify(10, builder.build());
    }
}
