package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HospitalFinishActivity extends AppCompatActivity {

    // 뷰
    ArrayList<HospitalData> hospitalDataList;

    // 태그
    private static final String TAG = "HospitalFinishActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_finish);

        TextView text_name = findViewById(R.id.text_name);
        TextView text_adr = findViewById(R.id.text_address);
        TextView text_date = findViewById(R.id.text_date);
        TextView text_time = findViewById(R.id.text_time);

        String hospitalName = getIntent().getStringExtra("hospitalName");
        String hospitalAddress = getIntent().getStringExtra("hospitalAddress");
        String selectedButtonText = getIntent().getStringExtra("selectedButtonText");
        String selectedDate = getIntent().getStringExtra("selectedDate");

        text_name.setText(hospitalName);
        text_adr.setText(hospitalAddress);
        text_date.setText(selectedDate);
        text_time.setText(selectedButtonText);

        Button btn_next = findViewById(R.id.btn_done);
        btn_next.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(getApplication(), MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
    }
}
