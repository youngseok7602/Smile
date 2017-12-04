package com.example.mju.smile;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jeong on 2017-11-26.
 */

public class CustomAdapter extends BaseAdapter {

    private ArrayList<AllUserData> items = new ArrayList<>();

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

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_custom, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img) ;
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name) ;

        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        //MyItem myItem = getItem(position);
        final AllUserData allUserData = getItem(position);



        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        iv_img.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.usericon1, null));
        tv_name.setText(allUserData.getName());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */
        return convertView;
    }

    public void addItem(AllUserData allUserData){
        items.add(allUserData);
    }

}
