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
 * Created by Jeong on 2017-12-02.
 */

public class CustomChoiceListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Memory> items = new ArrayList<Memory>() ;

    // ListViewAdapter의 생성자
    public CustomChoiceListViewAdapter() {
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return items.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_check, parent, false);
            //convertView = inflater.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView textTextView1 = (TextView) convertView.findViewById(R.id.textView1) ;
        TextView textTextView2 = (TextView) convertView.findViewById(R.id.textView2) ;

        CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        Memory MemoryItem = getItem(position);

        // 아이템 내 각 위젯에 데이터 반영
        textTextView1.setText(MemoryItem.getMessage_name());
        textTextView2.setText(MemoryItem.getSender());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Memory getItem(int position) {
        return items.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Memory memory) {
        items.add(memory);
    }

    public void clearItems(){
        items.clear();
    }
}
