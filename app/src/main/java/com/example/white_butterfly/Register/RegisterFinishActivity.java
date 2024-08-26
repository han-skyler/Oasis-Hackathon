package com.example.white_butterfly.Register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.white_butterfly.R;

public class RegisterFinishActivity extends AppCompatActivity {

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_finish);

        Button btn_main = (Button) findViewById(R.id.btn_main);
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.page_register, new RegisterNameFragment())  // page_register 는 해당 액티비티의 xml에 있어야 함
                        .commit();

                findViewById(R.id.page_register_finish).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
            super.onBackPressed(); // 앱 종료
        } else {
            backPressedTime = currentTime;
            Toast.makeText(this, "한 번 더 누를 시 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }
}
