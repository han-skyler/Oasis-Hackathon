package com.example.white_butterfly.TestMemory;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.white_butterfly.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Memory03Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private static final int REQUEST_CODE_SPEECH_INPUT = 200;
    private FirebaseDatabase empty_db, answer_db;

    private TextView dataTextView;
    private ImageButton speak;
    private ImageButton microphone;
    private Button endButton;
    private EditText editText_ex;
    private LinearLayout view_top04;
    private TextView TextView;
    String speak_edit;
    int questionCounter = 1; // 답변
    int one = 0; // 1
    int zero = 0; // 0
    int aa;
    TextView text_q_num; // 현재 질문 개수
    String path_e = "", path_a = "";
    Random random;
    private List<Integer> numberList;  // 아직 안 한 질문 리스트
    private static final String TAG = "Memory03Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory03);

        Log.w(TAG, "--- Memory03Activity ---");

        FirebaseApp.initializeApp(Memory03Activity.this);
        empty_db = FirebaseDatabase.getInstance();
        answer_db = FirebaseDatabase.getInstance();

        initializeViews();

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = dataTextView.getText().toString();
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                // 읽을 텍스트를 설정하고 음성 출력 시작
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        });

        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "mic");
                startSpeechToText();
            }
        });

        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "----- endButton -----");
                questionCounter++;
                if (questionCounter < 12) {
                    question_answer(aa);

                    editText_ex.setText("");
                }
                if (questionCounter == 11) {
                    endButton.setBackgroundResource(R.drawable.btn_memory_result_game);
                    dataTextView.setText("검사가 끝났습니다!");
                    endButton.setEnabled(true);
                    editText_ex.setVisibility(View.GONE);
                    speak.setVisibility(View.GONE);
                    microphone.setVisibility(View.GONE);
                    text_q_num.setVisibility(View.GONE);
                    view_top04.setVisibility(View.GONE);
                    TextView.setVisibility(View.GONE);

                    endButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int total_score = one * 10; // 총점 계산
                            Log.w(TAG, "total_score: " + total_score);

                            Intent intent = new Intent(Memory03Activity.this, MemoryLoadingActivity.class);
                            intent.putExtra("score", total_score); // 데이터를 Intent에 첨부
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
    private void initializeViews() {
        Log.w(TAG, "----- initializeViews -----");
        text_q_num = findViewById(R.id.text_q_num);
        dataTextView = findViewById(R.id.dataTextView);
        endButton = findViewById(R.id.endButton);
        speak = findViewById(R.id.speak);
        microphone = findViewById(R.id.microphone);
        editText_ex = findViewById(R.id.editText_ex);
        view_top04 = findViewById(R.id.view_top04);
        TextView = findViewById(R.id.TextView);

        // TextToSpeech 초기화
        textToSpeech = new TextToSpeech(this, this);

        // 아직 안 한 질문 리스트
        numberList = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        // 랜덤 인덱스
        random = new Random();

        random_question();
    }
    private void random_question() {
        Log.w(TAG, "----- random_question -----");
        if (numberList.isEmpty()) {
            return;
        }
        // 랜덤한 인덱스 생성
        int randomIndex = random.nextInt(numberList.size());
        int randomValue = numberList.get(randomIndex);
        numberList.remove(Integer.valueOf(randomValue));

        aa = randomValue;

        Log.d(TAG, "randomValue : " + randomValue);

        memory(randomValue);
    }

    private void memory(int randomNumber) {
        Log.w(TAG, "----- memory -----");
        path_e = "E" + String.format("%02d", randomNumber);
        Log.d("path_e", "path_e : " + path_e);

        // 질문 가져오기
        empty_db.getReference(path_e).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (questionCounter < 11) {
                    String originalText = String.valueOf(questionCounter);
                    String newText = originalText + ". ";
                    text_q_num.setText(newText);
                    Log.d("questionCounter", "questionCounter : " + questionCounter);
                }
                dataTextView.setText(value);

                Log.w(TAG, "numberList: " + numberList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void question_answer(int randomNumber) {
        Log.w(TAG, "----- question_answer -----");
        path_a = "A" + String.format("%02d", randomNumber);
        Log.d("path_a", "path_a : " + path_a);

        speak_edit = editText_ex.getText().toString();;
        Log.d("speak_edit", "speak_edit : " + speak_edit);

        // 정답 가져오기
        answer_db.getReference(path_a).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                Log.w(TAG, "answer: " + value);

                if (speak_edit != null && !speak_edit.isEmpty()) {

                    if(speak_edit.equals(value)) {
                        one++;
                        Log.d("editText", "editText : " + speak_edit);
                        Log.d("맞음", "맞음 : " + 1);
                        Log.d("one", "one : " + one);
                    }
                    else {
                        zero++;
                        Log.d("editText", "editText : " + speak_edit);
                        Log.d("틀림", "틀림 : " + 0);
                        Log.d("zero", "zero : " + zero);
                    }
                    random_question();
                }
                else {
                    zero++;
                    Log.d("입력한 값이 없음", "입력한 값이 없음");
                    Log.d("틀림", "틀림 : " + 0);
                    Log.d("zero", "zero : " + zero);
                    random_question();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

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

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // 여러 언어 지원을 위한 문자열 배열로 설정
        String[] supportedLanguages = {"en-US", "ko-KR"};
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, supportedLanguages);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && result.size() > 0) {
                    String recognizedText = result.get(0);
                    // 음성 인식 결과를 두 번째 화면의 텍스트뷰에 표시
                    editText_ex.setText(recognizedText);
                    speak_edit = recognizedText;
                }
            }
        }
    }
}