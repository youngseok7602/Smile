package com.example.mju.smile;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by Jeong on 2017-12-02.
 */

//추억리스트 대한 커스텀 리스트뷰의 check box 설정을 체크 레이아웃
public class CheckableLinearLayout2 extends LinearLayout implements Checkable {

    public CheckableLinearLayout2(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    @Override
    public boolean isChecked() {
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox_user) ;
        return cb.isChecked();
    }

    @Override
    public void toggle() {
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox_user) ;
        setChecked(cb.isChecked() ? false : true) ;
    }

    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = (CheckBox) findViewById(R.id.checkBox_user) ;

        if (cb.isChecked() != checked) {
            cb.setChecked(checked) ;
        }
    }
}
