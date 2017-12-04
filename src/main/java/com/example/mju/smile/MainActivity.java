package com.example.mju.smile;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private GoogleApiClient mGoogleApiClient;

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAzRzp4OA:APA91bFy79iR5Ack84dnPSeIw2yT3rnBy1PS2nGVhz5ANqsX7rzybKACUdeiLdc-8LQMPtD2vX2FdssA95NtBU3iBFrXmJMPvv3VVPxuKo8eRHILjR-aO9IlE91OSZXqbN2PKBv7C2oh";

    private String regID;                              //FCM에서 발급하는 RegstrationID 변수
    private String Pnumber;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<UserData> UserDataList;
    private ListView cListView;
    private ListView listview2;
    private SQLiteDatabase mDB;
    private DBHelper mDBHelper;
    private Uri filePath;
    private TabHost tabHost1;
    private String selectedRegID;
    private CustomChoiceListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();                               //뷰 설정 초기화 메소드
        initFirebaseDatabase();                   //Firebase 설정 초기화 메소드
        getPermission();                          // 앱에 필요한 권한 요청 메소드
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("FCMB")
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            saveSQLite(intent.getStringArrayListExtra("arrayList"));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initView(){

        // Adapter 생성
        adapter = new CustomChoiceListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview2 = (ListView) findViewById(R.id.listView2);
        listview2.setAdapter(adapter);

        tabHost1 = (TabHost) findViewById(R.id.tabHost1);
        tabHost1.setup();

        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.content1);
        ts1.setIndicator("Tab1");
        tabHost1.addTab(ts1);

        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.content2);
        ts2.setIndicator("Tab2");
        tabHost1.addTab(ts2);

        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3");
        ts3.setContent(R.id.content3);
        ts3.setIndicator("Tab3");
        tabHost1.addTab(ts3);

        tabHost1.setCurrentTab(0);

        /*
        tabHost1.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {

                switch(tabHost1.getCurrentTab()){
                    case 2:
                        showMemoryList();
                        break;
                }
            }
        });
        */

        cListView = (ListView) findViewById(R.id.listView1);
    }

    private void initFirebaseDatabase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void getPermission(){

        //마쉬멜로 버전 이하인 경우 권한체크 필요없음
        if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)){
            startMain();
        }

        if(PermissionUtil.checkPermissions(this, PermissionUtil.PERMISSIONS_STORAGE[0])
                && PermissionUtil.checkPermissions(this, PermissionUtil.PERMISSIONS_STORAGE[1])
                && PermissionUtil.checkPermissions(this, PermissionUtil.PERMISSIONS_STORAGE[2])
                && PermissionUtil.checkPermissions(this, PermissionUtil.PERMISSIONS_STORAGE[3])
                && PermissionUtil.checkPermissions(this, PermissionUtil.PERMISSIONS_STORAGE[4])){
            // 권한이 있으므로 메소드 실행
            startMain();
        }else{
            //마쉬멜로 버전이상 & 앱에서 요구하는 모든 권한을 가지고 있지 않으면 권한요청 실행
            PermissionUtil.requestPermissions(this);
        }
    }

    public void startMain(){

        initSQLite();                                  //SQLite 초기화
        getPnumber();                                  //내 핸드폰 번호를 가져오는 메소드
        sendRegID();                                   //FCM에서 발급하는 RegistrationID를 얻고 firebase에 전송 메소드
        showUsers();                                   //첫 화면에서 내 주소록 목록과 firebase 목록 싱크하여 존재하는 인원만 친구 리스트에 표시 메소드
        wiatFirebaseMessage();                         //Firebase 알람을 기다리는 메소드
        findClickedUsers();                            //클릭한 유저의 토큰정보를 얻어오는 메소드
        showMemoryList();
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

    private void initSQLite(){
        mDBHelper = new DBHelper(this);
        mDB = mDBHelper.getWritableDatabase();
        mDBHelper.onCreate(mDB);
        mDB.close();
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
                            customAdapter.addItem(list.get(i));
                        }
                        cListView.setAdapter(customAdapter);
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
                String strToken = allUserData.getToken();
                selectedRegID = strToken;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("추억만들기");
                builder.setMessage(allUserData.getName() + " 과(와) 추억 만들기");
                builder.setCancelable(true);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tabHost1.setCurrentTab(1);
                        TextView tv = (TextView) findViewById(R.id.textView1);
                        tv.setText(selectedRegID);
                        selectedRegID = null;
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedRegID = null;
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void initAdapter(){
        if(adapter.getCount() > 0){
            listview2.clearChoices();
            adapter.clearItems();
            adapter.notifyDataSetChanged();
        }
    }


    public void btnOpen(View view){
        int count, countCheck;
        count = adapter.getCount();

        if(count <= 0){
            Toast.makeText(this, "열람할 리스트가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        countCheck = listview2.getCheckedItemCount();
        SparseBooleanArray checked = listview2.getCheckedItemPositions();

        if(0 >= countCheck || 1 < countCheck){
            Toast.makeText(this, "한개의 추억만 선택해야 합니다.", Toast.LENGTH_SHORT).show();
        }else{
            for(int i = 0; i < listview2.getAdapter().getCount(); i++){
                if(checked.get(i)){
                    Toast.makeText(this, "선택한 추억정보: " + ((Memory) listview2.getItemAtPosition(i)).getMessage_name(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void btnDelete(View view){

        String returnValue = "";
        ArrayList<String> arrayList = new ArrayList<String>();
        int count, countCheck;
        count = adapter.getCount();

        if(count <= 0) {
            Toast.makeText(this, "열람할 리스트가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        countCheck = listview2.getCheckedItemCount();
        SparseBooleanArray checked = listview2.getCheckedItemPositions();

        if(0 >= countCheck){
            Toast.makeText(this, "한개 이상의 추억을 선택해야 합니다.", Toast.LENGTH_SHORT).show();
        }else{
            for(int i = 0; i < listview2.getAdapter().getCount(); i++){
                if(checked.get(i)){
                    arrayList.add("\"" + ((Memory) listview2.getItemAtPosition(i)).getPicture() + "\"");
                }
            }
        }

        returnValue = TextUtils.join(", ", arrayList);

        deleteSQLite(returnValue);
    }

    public void btnClicked1(View view){

        String b = regID;

        try{
            JSONArray registration_ids = new JSONArray();
            JSONObject data = new JSONObject();

            registration_ids.put(b);
            data.put(DBContract.Entry.COLUMN_NAME_MESSAGE_NAME, "제목1");
            data.put(DBContract.Entry.COLUMN_NAME_SENDER, "정윤수");
            data.put(DBContract.Entry.COLUMN_NAME_LATITUDE, "10");
            data.put(DBContract.Entry.COLUMN_NAME_LONGTITUDE, "20");
            data.put(DBContract.Entry.COLUMN_NAME_PICTURE, "사진.JPG");
            data.put(DBContract.Entry.COLUMN_NAME_DDAY, "12월 2일");

            sendPostToFCM(registration_ids, data);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void btnClicked2(View view) {
        String b = regID;

        try{
            JSONArray registration_ids = new JSONArray();
            JSONObject data = new JSONObject();

            registration_ids.put(b);
            data.put(DBContract.Entry.COLUMN_NAME_MESSAGE_NAME, "제목2");
            data.put(DBContract.Entry.COLUMN_NAME_SENDER, "짱구");
            data.put(DBContract.Entry.COLUMN_NAME_LATITUDE, "1000");
            data.put(DBContract.Entry.COLUMN_NAME_LONGTITUDE, "20000");
            data.put(DBContract.Entry.COLUMN_NAME_PICTURE, "사진2.JPG");
            data.put(DBContract.Entry.COLUMN_NAME_DDAY, "11월 2일");

            sendPostToFCM(registration_ids, data);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void btnClicked3(View view){
        String b = regID;

        try{
            JSONArray registration_ids = new JSONArray();
            JSONObject data = new JSONObject();

            registration_ids.put(b);
            data.put(DBContract.Entry.COLUMN_NAME_MESSAGE_NAME, "제목3");
            data.put(DBContract.Entry.COLUMN_NAME_SENDER, "홍길동");
            data.put(DBContract.Entry.COLUMN_NAME_LATITUDE, "100");
            data.put(DBContract.Entry.COLUMN_NAME_LONGTITUDE, "200");
            data.put(DBContract.Entry.COLUMN_NAME_PICTURE, "사진3.JPG");
            data.put(DBContract.Entry.COLUMN_NAME_DDAY, "1월 2일");

            sendPostToFCM(registration_ids, data);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //FCM을 이용하여 선택한 regID(토큰)를 가진 사용자에게 알람 보내기
    private void sendPostToFCM(final JSONArray registration_ids, final JSONObject data) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // FMC 메시지 생성 start
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();

                    notification.put("body", "새로운 추억이 도착했습니다.");
                    notification.put("title", getString(R.string.app_name));

                    root.put("registration_ids", registration_ids);
                    root.put("notification", notification);
                    root.put("data", data);

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

    private void deleteSQLite(String PictureNames){
        mDB = mDBHelper.getWritableDatabase();

        mDB.execSQL(String.format("DELETE FROM %s WHERE %s IN (%s);",
                DBContract.Entry.TABLE_NAME, DBContract.Entry.COLUMN_NAME_PICTURE, PictureNames));

        if(mDB != null)
            mDB.close();

        showMemoryList();
    }

    private void saveSQLite(ArrayList<String> arrayList){

        mDB = mDBHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DBContract.Entry.COLUMN_NAME_MESSAGE_NAME, arrayList.get(0));
        cv.put(DBContract.Entry.COLUMN_NAME_SENDER, arrayList.get(1));
        cv.put(DBContract.Entry.COLUMN_NAME_LATITUDE, arrayList.get(2));
        cv.put(DBContract.Entry.COLUMN_NAME_LONGTITUDE, arrayList.get(3));
        cv.put(DBContract.Entry.COLUMN_NAME_PICTURE, arrayList.get(4));
        cv.put(DBContract.Entry.COLUMN_NAME_DDAY, arrayList.get(5));

        mDB.insert(DBContract.Entry.TABLE_NAME, null, cv);

        if(mDB != null)
            mDB.close();

        showMemoryList();
    }

    private void showMemoryList(){

        //어댑터 초기화
        initAdapter();

        mDB = mDBHelper.getReadableDatabase();
        Cursor cursor = mDB.rawQuery(DBContract.SQL_SELECT_ENTRIES, null);

        ArrayList<Memory> memoryList = new ArrayList<Memory>();

        if(cursor != null)
        {
            if(cursor.moveToFirst())
            {
                do{
                    String arrayMemory[] = new String[6];
                    for(int j = 1; j < cursor.getColumnCount(); j++)
                    {
                        arrayMemory[j-1] = cursor.getString(j);
                    }
                    Memory memory = new Memory(arrayMemory);
                    memoryList.add(memory);
                }while(cursor.moveToNext());
            }
        }

        for(int i = 0; i < memoryList.size(); i++){
            adapter.addItem(memoryList.get(i)) ;
        }

        adapter.notifyDataSetChanged();

        if(mDB != null)
            mDB.close();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0 && resultCode == RESULT_OK){
            filePath = data.getData();
        }
    }

    public void clickChoose(View view){
        selectImage();
    }
    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "이미지를 선택하세요."), 0);
    }

    public void clickUpload(View view){
        String fileName = uploadeFile("test");
    }

    private String uploadeFile(String Name){

        String returnValue = null;

        //업로드 할 파일이 있으면 수행
        if(filePath != null){
            //업로드 진행 Dialog 보이기
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("업로드 중...");
            progressDialog.show();

            //firebase stroage 설정
            FirebaseStorage storage = FirebaseStorage.getInstance();

            //파일명 설정
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
            Date now = new Date();
            String fileName = formatter.format(now) + "_" + Name + ".png";

            //storage 주소와 폴더 파일명 지정해주기

            final StorageReference storageRef = storage.getReferenceFromUrl("gs://test-firebase-4caa5.appspot.com")
                    .child("images").child(fileName);

            StorageReference s = storage.getReference().child("images");


            //파일 업로드
            storageRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();  //업로드 다이얼로그 닫기
                            Toast.makeText(getApplicationContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();  //업로드 다이얼로그 닫기
                            Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests")
                            double progress = (100 * taskSnapshot.getBytesTransferred()) /  taskSnapshot.getTotalByteCount();
                            //dialog에 진행률을 퍼센트로 출력해 준다
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                        }
                    });

            returnValue = fileName;

        }else{
            Toast.makeText(getApplicationContext(), "파일을 먼저 선택하세요.", Toast.LENGTH_SHORT).show();
        }

        return returnValue;
    }
}
