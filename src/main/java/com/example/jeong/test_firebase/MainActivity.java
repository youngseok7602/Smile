package com.example.jeong.test_firebase;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAzRzp4OA:APA91bFy79iR5Ack84dnPSeIw2yT3rnBy1PS2nGVhz5ANqsX7rzybKACUdeiLdc-8LQMPtD2vX2FdssA95NtBU3iBFrXmJMPvv3VVPxuKo8eRHILjR-aO9IlE91OSZXqbN2PKBv7C2oh";

    private String regID;                              //FCM에서 발급하는 RegstrationID 변수
    private String Pnumber;
    private ArrayAdapter adapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<UserData> UserDataList;
    private ListView cListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();                               //뷰 설정 초기화 메소드
        initFirebaseDatabase();                   //Firebase 설정 초기화 메소드
        getPermission();                          // 앱에 필요한 권한 요청 메소드
    }

    private void initView(){
        cListView = (ListView) findViewById(R.id.listview);
        //adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
    }

    private void initFirebaseDatabase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void getPermission(){

        //마쉬멜로 버전 이하인 경우 권한체크 필요없음
        if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)){
        }

        if(PermissionUtil.checkPermissions(this, PermissionUtil.PERMISSIONS_STORAGE[0])
                && (PermissionUtil.checkPermissions(this, PermissionUtil.PERMISSIONS_STORAGE[1]))){
                // 권한이 있으므로 메소드 실행
                getPnumber();                                  //내 핸드폰 번호를 가져오는 메소드
                sendRegID();                                   //FCM에서 발급하는 RegistrationID를 얻고 firebase에 전송 메소드
                showUsers();                                   //첫 화면에서 내 주소록 목록과 firebase 목록 싱크하여 존재하는 인원만 친구 리스트에 표시 메소드
                wiatFirebaseMessage();                         //Firebase 알람을 기다리는 메소드
                findClickedUsers();
        }else{
            //마쉬멜로 버전이상 & 앱에서 요구하는 모든 권한을 가지고 있지 않으면 권한요청 실행
            PermissionUtil.requestPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if(requestCode == PermissionUtil.REQUEST_STORAGE){
            if(PermissionUtil.verifyPermission(grantResults)){
                getPermission();
            }
            else {
                showRequestAgainDialog();
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showRequestAgainDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("모든 권한을 허용하지 않으면 앱을 실행할 수 없습니다. 재실행 후 모든 권한을 허용해 주세요.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exit();
            }
        });
        builder.show();
    }

    private void exit(){
        finish();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void sendRegID(){

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        boolean isChanged = false;

        if(pref.getString("token", "").isEmpty()){
            editor.putString("token", FirebaseInstanceId.getInstance().getToken());
            editor.commit();
            isChanged = true;
        }else{
            if(!(pref.getString("token", "").equals(FirebaseInstanceId.getInstance().getToken()))){
                editor.remove("token");
                editor.commit();
                editor.putString("token", FirebaseInstanceId.getInstance().getToken());
                editor.commit();
                isChanged = true;
            }
        }

        regID = pref.getString("token", "");

        if(isChanged){
            UserData myinfo = new UserData(Pnumber, regID);
            databaseReference.child("data").push().setValue(myinfo);
        }

        return;
    }

    private void getPnumber(){
        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Pnumber = mgr.getLine1Number();
            if(Pnumber.startsWith("+82")){
                Pnumber = Pnumber.replace("+82", "0");
            }else if(Pnumber.startsWith("-")){
                Pnumber = Pnumber.replaceAll("-", "");
            }
        } catch (SecurityException e) {}

        return;
    }

    private void showUsers(){

        databaseReference.child("data")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserDataList = new ArrayList<UserData>();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserData userData = snapshot.getValue(UserData.class);  // chatData를 가져오고
                            UserDataList.add(userData);

                        }
                        ArrayList<AllUserData> list = compareContracts(UserDataList);

                        if(list.isEmpty()){
                            return;
                        }

                        CustomAdapter customAdapter = new CustomAdapter();

                        for(int i = 0; i < list.size(); i++){
                            //adapter.add(list.get(i).getName());  // adapter에 추가
                            customAdapter.addItem(list.get(i));
                        }
                        cListView.setAdapter(customAdapter);
                        //listView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private ArrayList<AllUserData> compareContracts(List<UserData> userDataList){

        ArrayList<AllUserData> list = new ArrayList<AllUserData>();

        ArrayList<Contacts> contactsList = getContacts();

        if(contactsList.isEmpty()){
            return list;
        }
        for(int i=0; i < contactsList.size(); i++){
            for(int j=0; j < userDataList.size(); j++){
                String replacedPNumber = contactsList.get(i).getPhoneNumber().replaceAll("-", "");
                if(replacedPNumber.equals(userDataList.get(j).getPhoneNumber())){
                    String Name = contactsList.get(i).getName();
                    String PhoneNumber = contactsList.get(i).getPhoneNumber();
                    String Token = userDataList.get(j).getToken();
                    AllUserData allUserData = new AllUserData(Name, PhoneNumber, Token);
                    list.add(allUserData);
                }
            }
        }

        return list;
    }

    private ArrayList<Contacts> getContacts(){

        ArrayList<Contacts> ContactsList = new ArrayList<Contacts>();
        Uri ContactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String C_PhoneNumber = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String C_Name = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsUri, new String[]{C_PhoneNumber, C_Name}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String PhoneNumber = cursor.getString(0);
                String Name = cursor.getString(1);
                Contacts contacts = new Contacts(PhoneNumber, Name);
                ContactsList.add(contacts);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return ContactsList;
    }

    private void wiatFirebaseMessage(){
        FirebaseMessaging.getInstance().subscribeToTopic("notice");
    }

    private void findClickedUsers(){
        cListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                AllUserData allUserData = (AllUserData) parent.getItemAtPosition(position);
                String str = allUserData.getToken();
                Toast.makeText(getApplicationContext(), "토큰 : " + str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void btnMessage(View view){
        String PNumber = "+821082889197";
        String Message = "test message";
        sendPostToFCM(PNumber, Message);
    }

    //FCM을 이용하여 선택한 regID(토큰)를 가진 사용자에게 알람 보내기
    private void sendPostToFCM(final String PNumber, final String Message){

        databaseReference.child("data")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            if(snapshot.child("phoneNumber").getValue(String.class).equals(PNumber)){
                                final UserData userData = snapshot.getValue(UserData.class);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            // FMC 메시지 생성 start
                                            JSONObject root = new JSONObject();
                                            JSONObject notification = new JSONObject();
                                            notification.put("body", Message);
                                            notification.put("title", getString(R.string.app_name));
                                            root.put("notification", notification);
                                            root.put("to", userData.getToken());
                                            // FMC 메시지 생성 end

                                            URL Url = new URL(FCM_MESSAGE_URL);
                                            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                                            conn.setRequestMethod("POST");
                                            conn.setDoOutput(true);
                                            conn.setDoInput(true);
                                            conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                                            conn.setRequestProperty("Accept", "application/json");
                                            conn.setRequestProperty("Content-type", "application/json");
                                            OutputStream os = conn.getOutputStream();
                                            os.write(root.toString().getBytes("utf-8"));
                                            os.flush();
                                            conn.getResponseCode();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void clickChoose(View view){
    }

    public void clickUpload(View view){
    }

    public void clickDownload(View view){
    }
}
