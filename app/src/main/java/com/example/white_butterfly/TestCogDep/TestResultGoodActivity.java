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

public class TestResultGoodActivity extends AppCompatActivity {
    // 전화 권한
    private static final int REQUEST_CALL_PHONE_PERMISSION = 1001;

    // TAG
    private String TAG = "TestResultGoodActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result_good);

        Log.w(TAG, "--- TestResultGoodActivity ---");

        String kakao_email = getIntent().getStringExtra("Email");

        // 치매상담콜센터와 전화 연결 버튼 누른 경우
        Button btn_call = findViewById(R.id.btn_call);
        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(TestResultGoodActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TestResultGoodActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
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
                Intent intent = new Intent(TestResultGoodActivity.this, MainActivity.class);
                intent.putExtra("Email", kakao_email);
                startActivity(intent);
                finish();
            }
        });
    }
}