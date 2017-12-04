package com.example.mju.smile;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FCM_MESSAGE";
    private LocalBroadcastManager broadcastManager;

    public FirebaseMessagingService() {}

    @Override
    public void onCreate() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //Notification 알람 띄우기
        sendPushNotification(remoteMessage.getNotification().getTitle()
                ,remoteMessage.getNotification().getBody());

        //데이터가 있는경우 데이터를 자신의 SQLite에 저장
        if(!(remoteMessage.getData().isEmpty())){
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add(remoteMessage.getData().get(DBContract.Entry.COLUMN_NAME_MESSAGE_NAME));
            arrayList.add(remoteMessage.getData().get(DBContract.Entry.COLUMN_NAME_SENDER));
            arrayList.add(remoteMessage.getData().get(DBContract.Entry.COLUMN_NAME_LATITUDE));
            arrayList.add(remoteMessage.getData().get(DBContract.Entry.COLUMN_NAME_LONGTITUDE));
            arrayList.add(remoteMessage.getData().get(DBContract.Entry.COLUMN_NAME_PICTURE));
            arrayList.add(remoteMessage.getData().get(DBContract.Entry.COLUMN_NAME_DDAY));

            //인텐트 생성 후 브로드 케스트로 저장할 데이터를 메인엑티비티로 전송
            Intent intent = new Intent("FCMB");
            intent.putExtra("arrayList", arrayList);
            broadcastManager.sendBroadcast(intent);
        }
    }

    private void sendPushNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background).setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher) )
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setLights(000000255,500,2000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        wakelock.acquire(5000);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
