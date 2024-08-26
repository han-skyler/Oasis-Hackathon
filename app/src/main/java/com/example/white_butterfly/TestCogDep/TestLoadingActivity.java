package com.example.white_butterfly.TestCogDep;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.white_butterfly.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

public class TestLoadingActivity extends AppCompatActivity {
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    // TAG
    private String TAG = "TestLoadingActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_loading);

        Log.w(TAG, "--- TestLoadingActivity ---");

        // Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = currentUser.getEmail();
        docRef = db.collection("Users").document(id);

        // 애니메이션 리소스 로드
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);

        // 뷰에 애니메이션 적용
        View viewToRotate = findViewById(R.id.icon_loading);
        viewToRotate.startAnimation(rotateAnimation);

        // 마지막 검사일
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();        // 2023
        int month = currentDate.getMonthValue(); // 8
        int day = currentDate.getDayOfMonth();   // 21

        docRef.update("year", year);
        docRef.update("month", month);
        docRef.update("day", day);

        // 점수에 따른 결과 표시
        String kakao_email = getIntent().getStringExtra("Email");
        int score_cog = getIntent().getIntExtra("score_cog", 0);
        int score_dep = getIntent().getIntExtra("score_dep", 0);

        if (score_cog >= 6 || score_dep >= 5) // 치매 또는 우울증 의심
        {
            Intent intent_bad = new Intent(getApplicationContext(), TestResultBadActivity.class);
            intent_bad.putExtra("score_cog", score_cog);
            intent_bad.putExtra("score_dep", score_dep);
            intent_bad.putExtra("Email", kakao_email);
            startActivity(intent_bad);
            finish();
        }
        else  // 치매, 우울증 아님
        {
            String Score = "아주 건강한 정신상태예요";
            docRef.update("Score", Score);
            Log.w(TAG, "Score: " + Score);

            Intent intent_good = new Intent(getApplicationContext(), TestResultGoodActivity.class);
            intent_good.putExtra("Email", kakao_email);
            startActivity(intent_good);
            finish();
        }
    }
}