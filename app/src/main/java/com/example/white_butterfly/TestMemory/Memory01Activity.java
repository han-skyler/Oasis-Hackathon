package com.example.white_butterfly.TestMemory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.white_butterfly.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Memory01Activity extends AppCompatActivity {
    private Button StartButton;
    private FirebaseDatabase memory_db, temporarily_db;
    private static final String TAG = "Memory01Activity";
    String path_m = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory01);

        StartButton = findViewById(R.id.btn_testStart);

        Log.w(TAG, "--- Memory01Activity ---");

        FirebaseApp.initializeApp(Memory01Activity.this);
        memory_db = FirebaseDatabase.getInstance();
        temporarily_db = FirebaseDatabase.getInstance();

        path_m = "M01";
        Log.d("path_m", "path_m : " + path_m);

        // 질문 가져오기
        memory_db.getReference(path_m).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        StartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Memory01Activity.this, Memory02Activity.class);
                startActivity(intent);
            }
        });
    }
}