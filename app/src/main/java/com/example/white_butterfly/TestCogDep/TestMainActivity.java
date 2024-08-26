package com.example.white_butterfly.TestCogDep;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.white_butterfly.Z_etc.NetworkUtils;
import com.example.white_butterfly.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TestMainActivity extends AppCompatActivity {
    // Firebase
    private FirebaseDatabase question_db;

    // TAG
    private static final String TAG = "TestMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);

        Log.w(TAG, "--- TestMainActivity ---");

        String kakao_email = getIntent().getStringExtra("Email");

        getData();

        Button btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtils.isNetworkAvailable(TestMainActivity.this)) {
                    // 인터넷 연결이 가능한 상태
                    Intent intent = new Intent(getApplication(), TestExampleActivity.class);
                    intent.putExtra("Email", kakao_email);
                    startActivity(intent);
                } else {
                    Toast.makeText(TestMainActivity.this, "인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getData() {
        // Firebase
        FirebaseApp.initializeApp(TestMainActivity.this);
        question_db = FirebaseDatabase.getInstance();

        try {
            question_db.getReference("C01").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String value = dataSnapshot.getValue(String.class);

                        Log.w(TAG, "질문 세팅 완료");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "질문 세팅 실패");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, String.format("%s", e));
        }
    }
}