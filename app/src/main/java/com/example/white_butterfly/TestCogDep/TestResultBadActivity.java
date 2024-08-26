package com.example.white_butterfly.TestCogDep;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.white_butterfly.MainActivity;
import com.example.white_butterfly.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;

public class TestResultBadActivity extends AppCompatActivity {
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    // 권한
    private static final int REQUEST_CALL_PHONE_PERMISSION = 1001;

    // 변수
    String Score = "";
    String id;

    // TAG
    private String TAG = "TestResultBadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result_bad);

        Log.w(TAG, "--- TestResultBadActivity ---");

        String kakao_email = getIntent().getStringExtra("Email");

        // Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (kakao_email != null && !kakao_email.isEmpty()) {
            // 이전 액티비티에서 받아온 값이 있는 경우
            id = kakao_email;
        } else {
            // 이전 액티비티에서 값이 없는 경우
            id = currentUser.getEmail();
        }
        docRef = db.collection("Users").document(id);

        int score_cog = getIntent().getIntExtra("score_cog", 0);
        int score_dep = getIntent().getIntExtra("score_dep", 0);

        Log.w(TAG, "치매 점수: " + score_cog);
        Log.w(TAG, "우울증 점수: " + score_dep);

        // 텍스트 뷰
        TextView text_result = findViewById(R.id.text_result);

        // 점수에 따른 결과 표시
        if (score_cog >= 6) // 치매 의심
        {
            if (score_dep >= 5) // 우울증 의심
            {
                Score = "치매와 우울증이 의심됩니다";
                //text_result.setText("치매와 우울증이 의심됩니다");  // (디폴트값)
            }
            else  // 우울증 아님
            {
                Score = "치매가 의심됩니다";
                text_result.setText("치매가 의심됩니다");
            }
        }
        else  // 치매 아님, 그럼 우울증
        {
            text_result.setText("우울증이 의심돼요");
            Score = "우울증이 의심됩니다";
        }

        docRef.update("Score", Score);
        Log.w(TAG, "Score: " + Score);


        // 치매상담콜센터와 전화 연결 버튼 누른 경우
        Button btn_call = findViewById(R.id.btn_call);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(TestResultBadActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TestResultBadActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
                } else {
                    // 권한이 이미 있으면 전화 걸기 로직을 실행
                    String tel = "tel:18999988";
                    startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                }
            }
        });

        // 돌아가기 버튼 누른 경우
        Button btn_done = (Button) findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestResultBadActivity.this, MainActivity.class);
                intent.putExtra("Email", kakao_email);
                startActivity(intent);
                finish();
            }
        });
    }
}