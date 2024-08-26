package com.example.white_butterfly.TestCogDep;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.white_butterfly.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;

public class TestExampleActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // Firebase
    FirebaseFirestore user_db;
    DocumentReference docRef;
    String path;  // Firebase DB 메인 주소
    private FirebaseDatabase question_db;
    private FirebaseAuth mAuth;

    // 검사 페이지 진행바
    private ProgressBar loadBar;

    // TTS
    private TextToSpeech textToSpeech;

    // 변수
    TextView text_question;  // 질문 텍스트 뷰
    TextView text_ex;
    Button btn_start;
    ImageView btn_reply_1;
    ImageView btn_reply_2;
    ImageView btn_reply_3;
    Boolean reply_1_Selected = false;
    Boolean reply_2_Selected = false;
    Boolean reply_3_Selected = false;
    Boolean selected = false;
    Vibrator vibrator;

    // TAG
    private static final String TAG = "TestExampleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ex);

        Log.w(TAG, "--- TestExampleActivity ---");

        initializeViews();

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestStart();
            }
        });

        btn_reply_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply_2_Selected = false;
                reply_3_Selected = false;
                btn_reply_2.setSelected(false);
                btn_reply_3.setSelected(false);

                reply_1_Selected = true;
                btn_reply_1.setSelected(true);

                button_state_check();
            }
        });

        btn_reply_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply_1_Selected = false;
                reply_3_Selected = false;
                btn_reply_1.setSelected(false);
                btn_reply_3.setSelected(false);

                reply_2_Selected = true;
                btn_reply_2.setSelected(true);

                button_state_check();
            }
        });

        btn_reply_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reply_1_Selected = false;
                reply_2_Selected = false;
                btn_reply_1.setSelected(false);
                btn_reply_2.setSelected(false);

                reply_3_Selected = true;
                btn_reply_3.setSelected(true);

                button_state_check();
            }
        });

        text_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = text_question.getText().toString();
                TTS(text);
            }
        });
    }

    private void button_state_check() {
        if (!reply_1_Selected && !reply_2_Selected && !reply_3_Selected) { selected = false; }
        else { selected = true; }

        if (selected) { // 버튼이 선택되어 있다면
            btn_start.setEnabled(true);
        } else { // 버튼이 선택되어 있지 않다면
            btn_start.setEnabled(false);
        }
    }

    private void TTS(String text) {
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }

        // 읽을 텍스트를 설정하고 음성 출력 시작
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
    }

    private void initializeViews() {
        text_question = findViewById(R.id.text_question);
        text_ex = findViewById(R.id.text_ex);

        btn_start = findViewById(R.id.btn_start);

        btn_reply_1 = findViewById(R.id.btn_reply_1);
        btn_reply_2 = findViewById(R.id.btn_reply_2);
        btn_reply_3 = findViewById(R.id.btn_reply_3);

        textToSpeech = new TextToSpeech(this, this);

        loadBar = findViewById(R.id.loadBar);
    }

    // 스피커
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int langResult = textToSpeech.setLanguage(Locale.KOREA);
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported or missing data");
            } else {
                // 초기화 성공, 필요한 추가 설정 가능
            }
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    // 다음 질문 페이지를 표시하는 메서드
    public void TestStart() {
        Log.w(TAG, "TestStart");

        String kakao_email = getIntent().getStringExtra("Email");

        // 치매 선별 검사 페이지 출력
        Intent intent = new Intent(getApplication(), TestCogActivity.class);
        intent.putExtra("Email", kakao_email);
        startActivity(intent);
        finish();
    }
}
