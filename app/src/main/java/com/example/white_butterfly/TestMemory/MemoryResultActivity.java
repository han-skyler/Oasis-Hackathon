package com.example.white_butterfly.TestMemory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.white_butterfly.MainActivity;
import com.example.white_butterfly.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MemoryResultActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "MemoryResultActivity";
    private ImageView imageView;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_result);

        Log.w(TAG, "--- MemoryResultActivity ---");

        // Intent에서 데이터 가져오기
        int score = getIntent().getIntExtra("score", 0);

        Button btn_main = (Button) findViewById(R.id.btn_main);
        imageView = findViewById(R.id.image_score);

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(MemoryResultActivity.this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference().child("memory_result");

        Log.w(TAG, "score: " + score);

        if(score == 100) {
            onPageTransition(100);
        } else if (score >= 90) {
            onPageTransition(90);
        } else if (score >= 80) {
            onPageTransition(80);
        } else if (score >= 70) {
            onPageTransition(70);
        } else if (score >= 60) {
            onPageTransition(60);
        } else if (score >= 50) {
            onPageTransition(50);
        } else if (score >= 40) {
            onPageTransition(40);
        } else if (score >= 30) {
            onPageTransition(30);
        } else if (score >= 20) {
            onPageTransition(20);
        } else if (score >= 10) {
            onPageTransition(10);
        } else {
            onPageTransition(0);
        }

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemoryResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void onPageTransition(int score) {
        // StorageReference를 통해 이미지 파일을 가져옵니다.
        StorageReference fileReference = storageReference.child(String.valueOf(score)).child("image.png");

        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // 이미지가 존재하는 경우, 이미지 다운로드 URL을 가져와 ImageView에 표시
            Glide.with(this).load(uri).into(imageView);
            Log.d(TAG, "결과 이미지 : " + score);
        }).addOnFailureListener(urlFailure -> {
            // 이미지가 존재하지 않는 경우
            Log.d(TAG, "사진 없음");
        });
    }
}