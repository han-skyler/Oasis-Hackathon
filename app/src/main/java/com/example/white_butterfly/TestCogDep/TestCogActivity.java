package com.example.white_butterfly.TestCogDep;

import static android.view.View.GONE;

import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.white_butterfly.R;
import com.example.white_butterfly.Z_etc.ProgressModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TestCogActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // Firebase
    FirebaseFirestore user_db;
    DocumentReference docRef;
    String path;  // Firebase DB 메인 주소
    private FirebaseDatabase question_db;
    private FirebaseAuth mAuth;

    // 검사 페이지 진행바
    public ProgressModel progressModel;
    public ProgressBar progressBar;
    private ProgressBar loadBar;

    // 페이지 추적
    private int currentPage = 1;  // 현재 페이지 (1~15)
    private int currentProgress = 1; // (1~15)

    // TTS
    private TextToSpeech textToSpeech;

    // 뷰
    TextView text_q_num; // 현재 질문 개수
    TextView text_question;  // 질문 텍스트 뷰
    TextView text_ex;
    Button btn_next;
    ImageView btn_reply_1;
    ImageView btn_reply_2;
    ImageView btn_reply_3;
    String id;

    // 버튼 선택 확인용 변수
    Boolean reply_1_Selected = false;
    Boolean reply_2_Selected = false;
    Boolean reply_3_Selected = false;
    Boolean selected = false;

    // 인지 능력 점수
    int score_cog = 0;

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // TAG
    private static final String TAG = "TestCogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_cog);

        Log.w(TAG, "--- TestCogActivity ---");

        initializeViews();

        // Firebase
        FirebaseApp.initializeApp(TestCogActivity.this);
        question_db = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user_db = FirebaseFirestore.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        String kakao_email = getIntent().getStringExtra("Email");

        if (kakao_email != null && !kakao_email.isEmpty()) {
            // 이전 액티비티에서 받아온 값이 있는 경우
            id = kakao_email;
        } else {
            // 이전 액티비티에서 값이 없는 경우
            id = currentUser.getEmail();
        }
        docRef = db.collection("Users").document(id);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextQuestion();
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

        test();
    }

    ///////////////////////////////// 뷰 관련

    private void initializeViews() {
        text_q_num = findViewById(R.id.text_q_num);
        text_question = findViewById(R.id.text_question);
        text_ex = findViewById(R.id.text_ex);

        btn_next = findViewById(R.id.btn_next);

        btn_reply_1 = findViewById(R.id.btn_reply_1);
        btn_reply_2 = findViewById(R.id.btn_reply_2);
        btn_reply_3 = findViewById(R.id.btn_reply_3);

        textToSpeech = new TextToSpeech(this, this);

        progressBar = findViewById(R.id.progress);
        progressModel = new ViewModelProvider(TestCogActivity.this).get(ProgressModel.class);

        loadBar = findViewById(R.id.loadBar);
    }

    ///////////////////////////////// TTS 관련

    private void TTS(String text) {
        if (textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }

        // 읽을 텍스트를 설정하고 음성 출력 시작
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
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

    ///////////////////////////////// 버튼 관련

    // 버튼 활성화
    private void button_enable()
    {
        btn_reply_1.setEnabled(true);
        btn_reply_2.setEnabled(true);
        btn_reply_3.setEnabled(true);
    }

    // 모든 버튼 unselected
    private void button_unselected()
    {
        btn_reply_1.setSelected(false);
        btn_reply_2.setSelected(false);
        btn_reply_3.setSelected(false);
        btn_next.setEnabled(false);
    }

    private void button_state_check() {
        if (!reply_1_Selected && !reply_2_Selected && !reply_3_Selected) { selected = false; }
        else { selected = true; }

        if (selected) { // 버튼이 선택되어 있다면
            btn_next.setEnabled(true);
        } else { // 버튼이 선택되어 있지 않다면
            btn_next.setEnabled(false);
        }
    }

    ///////////////////////////////// 점수 관련

    private void score_check() {
        if (reply_1_Selected) {
            score_cog += 2;
        }
        else if (reply_2_Selected) {
            score_cog += 1;
        }
        /* // 변함이 없기 때문에 주석 처리
        else if (reply_3_Selected) {
            score_cog += 0;
        }
         */
    }

    ///////////////////////////////// 다음 페이지로 넘어가는 부분

    // 다음 질문 페이지를 표시하는 메서드
    public void loadNextQuestion() {
        Log.w(TAG, "loadNextQuestion");

        currentPage++;
        currentProgress++;

        if (currentPage <= 15)
        {
            score_check();
            test();
            button_unselected();

            text_q_num.setText(String.valueOf(currentPage));

            progressModel.getProgressLiveData().observe(TestCogActivity.this, progress -> {
                progressBar.setProgress(currentProgress);
                Log.w(TAG, "프로그레스: " + currentProgress);
            });

            updateDataAndProgress();
        }
        else
        {
            Log.w(TAG, "score_cog: " + score_cog);

            docRef.update("Score_cog", score_cog);

            // 우울증 검사 페이지 출력
            Intent intent_dep = new Intent(getApplication(), TestDepMainActivity.class);
            intent_dep.putExtra("score_cog", score_cog);
            intent_dep.putExtra("Email", id);
            startActivity(intent_dep);
            finish();
        }
    }

    private void updateDataAndProgress() {
        progressModel.updateProgress(currentPage);
    }

    ///////////////////////////////// 질문 불러오는 부분

    public void test() {
        path = "C" + String.format("%02d", currentPage);
        Log.w(TAG, "# test " + currentPage + " / 현재 질문 " + path);

        // 데이터 로드할 때 로딩 중 표시, 완료 후 숨김
        loadBar.setVisibility(View.VISIBLE);

        // 질문 표시
        question();
    }

    private class getDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // 로딩 화면 표시
            loadBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // 백그라운드 작업 수행
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.w(TAG, "case: " + currentPage);
            Log.w(TAG, "# question: " + currentPage);

            // 질문 불러오기
            try {
                question_db.getReference(path).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String value = dataSnapshot.getValue(String.class);
                            value = value.replace("\\n", "\n");
                            text_question.setText(value);
                            button_enable();

                            switch (currentPage)
                            {
                                case 9:
                                    text_ex.setText(R.string.ex09);
                                    break;
                                case 11:
                                    text_ex.setText(R.string.ex11);
                                    break;
                                default:
                                    text_ex.setText("");
                                    break;
                            }

                            Log.w(TAG, currentPage + " 질문 세팅 완료");
                        } else {
                            Log.w(TAG, currentPage + " 질문 경로가 존재하지 않음. path: " + path);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // 데이터 읽기 실패 시 처리
                        Log.w(TAG, currentPage + " 질문 데이터 읽기 실패");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, String.format("%s", e));
            }

            // 작업 완료 후 로딩 화면 숨김
            loadBar.setVisibility(GONE);
        }
    }

    // "question()" 함수를 AsyncTask로 호출하는 부분
    private void question() {
        // AsyncTask 실행
        new getDataTask().execute();
    }

    ///////////////////////////////// 뒤로 가기 버튼

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
            Intent intent = new Intent(getApplication(), TestMainActivity.class);
            startActivity(intent);
            finish();
        } else {
            backPressedTime = currentTime;
            Toast.makeText(this, "한 번 더 누를 시 테스트가 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }
}
