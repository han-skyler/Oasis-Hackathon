package com.example.white_butterfly.TestCogDep;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.white_butterfly.R;

public class TestDepMainActivity extends AppCompatActivity {

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // TAG
    private static final String TAG = "TestDepMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dep_main);

        Log.w(TAG, "--- TestDepMainActivity ---");

        String kakao_email = getIntent().getStringExtra("Email");
        int score_cog = getIntent().getIntExtra("score_cog", 0);

        Button btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_dep = new Intent(getApplication(), TestDepActivity.class);
                intent_dep.putExtra("score_cog", score_cog);
                intent_dep.putExtra("Email", kakao_email);
                startActivity(intent_dep);
            }
        });
    }

    ///////////////////////////////// 뒤로 가기 버튼

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
            Intent intent = new Intent(getApplication(), TestMainActivity.class);
            startActivity(intent);
            finish();
        } else {
            backPressedTime = currentTime;
            Toast.makeText(this, "한 번 더 누를 시 테스트가 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }
}