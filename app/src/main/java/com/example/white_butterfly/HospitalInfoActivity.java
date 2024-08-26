package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class HospitalInfoActivity extends AppCompatActivity {

    // 뷰
    ArrayList<HospitalData> hospitalDataList;

    // 태그
    private static final String TAG = "HospitalInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_info);

        ImageView image_before;
        image_before = findViewById(R.id.image_before);
        image_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "이전 버튼 클릭");
                Intent intent_main = new Intent(getApplication(), MainActivity.class);
                startActivity(intent_main);
            }
        });

        TextView text_name = findViewById(R.id.text_title);
        TextView text_sub = findViewById(R.id.text_sub);
        TextView text_adr = findViewById(R.id.text_adr);

        String hospitalName = getIntent().getStringExtra("hospitalName");
        String hospitalSub = getIntent().getStringExtra("hospitalSub");
        String hospitalAddress = getIntent().getStringExtra("hospitalAddress");

        text_name.setText(hospitalName);
        text_sub.setText(hospitalSub);
        text_adr.setText(hospitalAddress);

        Button btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Log.w(TAG, "예약하기 버튼 클릭");

                                                    Intent intent = new Intent(getApplication(), HospitalReservationActivity.class);

                                                    intent.putExtra("hospitalName", text_name.getText());
                                                    intent.putExtra("hospitalSub", text_sub.getText());
                                                    intent.putExtra("hospitalAddress", text_adr.getText());

                                                    startActivity(intent);
                                                }
                                            });
    }
}
