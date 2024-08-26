package com.example.white_butterfly.Community;

import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.white_butterfly.MainActivity;
import com.example.white_butterfly.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Locale;

public class CommunityWriteActivity extends AppCompatActivity {
    private EditText edit_title;
    private EditText edit_content;
    private Button writeButton;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser currentUser;
    DatabaseReference docRef;
    String id;
    String name;

    //현재 날짜로 지정
    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    String date = currentDate;

    // TAG
    private static final String TAG = "CommunityWriteActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_write);

        Log.w(TAG, "--- CommunityWriteActivity ---");

        initializeViews();

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(CommunityWriteActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        docRef = firebaseDatabase.getReference();
        View rootView = findViewById(android.R.id.content);

        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 키보드 숨기기
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        try {
            name = maskName(getIntent().getStringExtra("name"));
        } catch (Exception e) {
            name = "홍길동";
            name = maskName("홍길동");
        }

        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToFirebase();
            }
        });
    }

    private void initializeViews() {
        edit_title = findViewById(R.id.title);
        edit_content = findViewById(R.id.content);
        writeButton = findViewById(R.id.btn_add);
    }

    private void writeToFirebase() {
        String nodetitle = edit_title.getText().toString();
        String nodecontent = edit_content.getText().toString();

        // 빈 값이 아닐 경우에만 Firebase에 저장
        if (!nodetitle.isEmpty() && !nodecontent.isEmpty()) {
            DatabaseReference newNodeReference = docRef.child("Community").child(date).child(name).child(nodetitle);
            newNodeReference.setValue(nodecontent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(CommunityWriteActivity.this, CommunityMainActivity.class);
                            intent.putExtra("Email", id);
                            startActivity(intent);
                            Toast.makeText(CommunityWriteActivity.this, "저장 성공", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "< 저장 성공 >");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CommunityWriteActivity.this, "저장 실패", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "저장 실패: " + e.getMessage());
                        }
                    });
        }

        ImageView image_before = findViewById(R.id.image_before);
        image_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_main = new Intent(getApplication(), CommunityMainActivity.class);
                startActivity(intent_main);
            }
        });
    }

    public static String maskName(String fullName) {
        if (fullName.length() < 2) {
            return fullName; // 이름이 2글자 이하면 변환하지 않고 반환
        }

        char firstChar = fullName.charAt(0);
        String maskedPortion = new String(new char[fullName.length() - 1]).replace('\0', '*');

        return firstChar + maskedPortion;
    }
}