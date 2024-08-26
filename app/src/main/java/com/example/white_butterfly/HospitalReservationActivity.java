package com.example.white_butterfly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HospitalReservationActivity extends AppCompatActivity {
    // 변수
    private Button lastSelectedButton; // 마지막으로 선택된 버튼
    private String selectedButtonText; // 선택한 버튼의 텍스트
    String selectedDate; // 선택한 날짜

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // 뷰
    ArrayList<HospitalData> hospitalDataList;

    // 태그
    private static final String TAG = "HospitalInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_reservation);

        TextView text_name = findViewById(R.id.text_title);
        TextView text_sub = findViewById(R.id.text_sub);
        TextView text_adr = findViewById(R.id.text_adr);

        String hospitalName = getIntent().getStringExtra("hospitalName");
        String hospitalSub = getIntent().getStringExtra("hospitalSub");
        String hospitalAddress = getIntent().getStringExtra("hospitalAddress");

        text_name.setText(hospitalName);
        text_sub.setText(hospitalSub);
        text_adr.setText(hospitalAddress);

        Button btn_done = findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(getApplication(), HospitalFinishActivity.class);

                                                    intent.putExtra("hospitalName", text_name.getText());
                                                    intent.putExtra("hospitalAddress", text_adr.getText());
                                                    intent.putExtra("selectedButtonText", selectedButtonText);
                                                    intent.putExtra("selectedDate", selectedDate);

                                                    Log.w(TAG, "Date: " + selectedDate);

                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });

        int[] buttonIds = {
                R.id.btn_11, R.id.btn_12, R.id.btn_14,
                R.id.btn_15, R.id.btn_16, R.id.btn_17, R.id.btn_18
        };

        View.OnClickListener commonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectedButton != null) {
                    lastSelectedButton.setSelected(false);
                }

                Button clickedButton = (Button) v;
                clickedButton.setSelected(true);
                lastSelectedButton = clickedButton;
                selectedButtonText = clickedButton.getText().toString(); // 선택한 버튼의 텍스트 저장
            }
        };

        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(commonClickListener);
        }

        DatePicker datePicker = findViewById(R.id.vDatePicker);

        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int dayOfMonth = datePicker.getDayOfMonth();
        selectedDate = year + "년 " + (month + 1) + "월 " + dayOfMonth + "일";

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selectedDate = year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일"; // 선택한 날짜 저장
                    }
                });
    }

    ///////////////////////////////// 뒤로 가기 버튼

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
            Intent intent = new Intent(getApplication(), HospitalListActivity.class);
            startActivity(intent);
        } else {
            backPressedTime = currentTime;
            Toast.makeText(this, "뒤로가기를 누르면 예약이 취소됩니다", Toast.LENGTH_SHORT).show();
        }
    }
}
