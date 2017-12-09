package com.example.mju.smile;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//선택한 추억 정보를 표시하는 엑티비티
public class MemoryInfoActivity extends AppCompatActivity {

    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    ImageView imageView1;
    Memory memory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_info);

        initView();

        Intent intent = getIntent();
        memory = (Memory) intent.getSerializableExtra("object");

        inputData(memory);

    }

    private void initView(){
        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
    }

    private void inputData(Memory memory){

        String fileName = memory.getPicture();


        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pathReference = storageRef.child("images/" + fileName + ".png");
        ImageView iv = (ImageView)findViewById(R.id.imageView1);

        Glide.with(this).using(new FirebaseImageLoader()).load(pathReference).into(iv);

        textView1.setText(memory.getMessage_name());
        textView2.setText(memory.getSender());
        textView3.setText(getAddress(memory.getLatitude(), memory.getLongtitude()));
        textView4.setText(memory.getDday());

    }

    private String getAddress(String latitude, String longtitude){

        String returnValue = null;
        Double dLatitude = Double.parseDouble(latitude);
        Double dLongtitude = Double.parseDouble(longtitude);
        try{
            Geocoder geo = new Geocoder(this, Locale.KOREAN);
            List<Address> addr = geo.getFromLocation(dLatitude, dLongtitude, 3);
            String addrLine = addr.get(0).getAddressLine(0).toString();
            returnValue = addrLine;
        }catch(IOException e){}

        return returnValue;
    }

    public void btnLocation(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("latitude", memory.getLatitude());
        intent.putExtra("longtitude", memory.getLongtitude());
        startActivity(intent);
    }
}
