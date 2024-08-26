package com.example.white_butterfly.TestMemory;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayoutStates;

import com.example.white_butterfly.R;

public class MemoryLoadingActivity extends AppCompatActivity {
    private static final String TAG = "MemoryLoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_loading);

        Log.w(TAG, "--- MemoryLoadingActivity ---");

        // 애니메이션 리소스 로드
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        // 뷰에 애니메이션 적용
        View viewToRotate = findViewById(R.id.icon_loading);
        viewToRotate.startAnimation(rotateAnimation);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Log.w(TAG, "================== TEST FINISH ==================");

                // Intent에서 데이터 가져오기
                int score = getIntent().getIntExtra("score", 0);
                Log.w(ConstraintLayoutStates.TAG, "Loding one: " + score);

                Intent intent = new Intent(getApplicationContext(), MemoryResultActivity.class);
                intent.putExtra("score", score); // 데이터를 Intent에 첨부
                startActivity(intent);
            }
        }, 3000);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}