package com.example.white_butterfly.Community;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.white_butterfly.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommunityReadActivity extends AppCompatActivity {

    private TextView text_title;
    private TextView text_content;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser currentUser;
    private DatabaseReference docRef;
    private static final String TAG = "CommunityReadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_read);

        Log.w(TAG, "--- CommunityReadActivity ---");

        // Firebase 초기화 및 데이터베이스 접근 설정
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        docRef = firebaseDatabase.getReference();

        // 이메일 정보 받아오기
        String kakaoEmail = getIntent().getStringExtra("Email");
        String id = (kakaoEmail != null && !kakaoEmail.isEmpty()) ? kakaoEmail : currentUser.getEmail();

        // 전달받은 데이터 처리
        String title = getIntent().getStringExtra("Title");
        String date = getIntent().getStringExtra("Date");
        String contentId = getIntent().getStringExtra("Id");

        // 데이터베이스에서 내용 가져오기
        DatabaseReference contentRef = docRef.child("Community").child(date).child(contentId).child(title);
        contentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String contentName = dataSnapshot.getValue(String.class);
                Log.w(TAG, "Title : " + title);
                Log.w(TAG, "ContentName : " + contentName);

                // TextView 업데이트
                text_title = findViewById(R.id.title);
                text_content = findViewById(R.id.content);
                text_title.setText(title);
                text_content.setText(contentName);

                // 하이퍼링크 처리
                try {
                    String contentText = text_content.getText().toString();
                    SpannableString spannableString = new SpannableString(contentText);

                    Pattern pattern = Pattern.compile("http://[a-zA-Z0-9./]+"); // http로 시작하는 패턴
                    Matcher matcher = pattern.matcher(contentText);
                    while (matcher.find()) {
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                String linkText = contentText.substring(matcher.start(), matcher.end());
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkText));
                                startActivity(intent);
                            }

                            @Override
                            public void updateDrawState(@NonNull TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(true); // 하이퍼링크 텍스트에 밑줄 표시
                            }
                        };

                        spannableString.setSpan(clickableSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    text_content.setText(spannableString);
                    text_content.setMovementMethod(LinkMovementMethod.getInstance());
                } catch (Exception e) {
                    Log.e(TAG, "하이퍼링크 처리 오류: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "데이터 가져오기 실패: " + databaseError.getMessage());
            }
        });

        // 뒤로 가기 버튼 처리
        ImageView image_before = findViewById(R.id.image_before);
        image_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_main = new Intent(getApplication(), CommunityMainActivity.class);
                startActivity(intent_main);
            }
        });
    }
}
