package com.example.mju.smile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

//다중유저 선택이 가능한 리스트뷰를 띄우는 엑티비티(별도의 커스텀 리스트뷰 사용)
public class UserListActivity extends AppCompatActivity {

    private CustomAdapter2 adapter;
    private ListView listview;
    private ArrayList<AllUserData> userDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        Intent intent = getIntent();
        userDataArrayList = (ArrayList<AllUserData>) intent.getSerializableExtra("AllUserData");

        initView();

        for(int i = 0; i < userDataArrayList.size(); i++){
            adapter.addItem(userDataArrayList.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    private void initView(){
        // Adapter 생성
        adapter = new CustomAdapter2() ;
        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listView);
        listview.setAdapter(adapter);
    }

    public void btnSelect(View view){
        int count, countCheck;
        count = adapter.getCount();

        if(count <= 0){
            Toast.makeText(this, "선택된 리스트가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        countCheck = listview.getCheckedItemCount();
        SparseBooleanArray checked = listview.getCheckedItemPositions();

        if(0 >= countCheck){
            Toast.makeText(this, "한명 이상을 선택해야 합니다..", Toast.LENGTH_SHORT).show();
        }else{
            ArrayList<AllUserData> allUserDataList = new ArrayList<AllUserData>();
            for(int i = 0; i < listview.getAdapter().getCount(); i++){
                if(checked.get(i)){
                    allUserDataList.add(adapter.getItem(i));
                }
            }

            Intent intent = new Intent();
            intent.putExtra("selectedUsers", allUserDataList);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    public void btnCancel(View view){
        finish();
    }
}
