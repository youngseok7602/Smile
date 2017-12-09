package com.example.mju.smile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jeong on 2017-12-05.
 */

// 유저 리스트에서 유저 선택시 선택한 유저 객체가 반환되도록 하기 위해 checkable 기능을 추가한 커스텀 리스트 뷰 어뎁터
public class CustomAdapter2 extends BaseAdapter {

    private ArrayList<AllUserData> items = new ArrayList<>();

    // CustomAdapter2의 생성자
    public CustomAdapter2() {
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public AllUserData getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        // 'listview_user_check' Layout을 inflate하여 convertView 참조 획득
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_user_check, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        TextView tv_name = (TextView) convertView.findViewById(R.id.textView1) ;

        CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox_user);

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 추억 데이터 재활용 */
        final AllUserData allUserData = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        tv_name.setText(allUserData.getName());

        return convertView;
    }

    public void addItem(AllUserData allUserData){
        items.add(allUserData);
    }

}
