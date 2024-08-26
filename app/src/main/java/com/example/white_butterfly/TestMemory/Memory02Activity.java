package com.example.white_butterfly.TestMemory;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.Random;

public class Memory02Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;

    private FirebaseDatabase memory_db;
    private TextView dataTextView1, dataTextView2, dataTextView3, dataTextView4, dataTextView5;
    private Button nextButton;
    private ImageButton speak1, speak2, speak3, speak4, speak5;
    int questionCounter = 0;

    TextView text_q_num; // 현재 질문 개수
    Random random;
    private List<Integer> numberList;  // 아직 안 한 질문 리스트
    private static final String TAG = "Memory02Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory02);

        Log.w(TAG, "--- Memory02Activity ---");

        FirebaseApp.initializeApp(Memory02Activity.this);
        memory_db = FirebaseDatabase.getInstance();

        initializeViews();

        random_question();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ++questionCounter;

                if (questionCounter < 3) {
                    random_question();
                }
            }
        });

        speak1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = dataTextView1.getText().toString();
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                // 읽을 텍스트를 설정하고 음성 출력 시작
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        });
        speak2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = dataTextView2.getText().toString();
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                // 읽을 텍스트를 설정하고 음성 출력 시작
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        });
        speak3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = dataTextView3.getText().toString();
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                // 읽을 텍스트를 설정하고 음성 출력 시작
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        });
        speak4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = dataTextView4.getText().toString();
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                // 읽을 텍스트를 설정하고 음성 출력 시작
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        });
        speak5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = dataTextView5.getText().toString();
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                // 읽을 텍스트를 설정하고 음성 출력 시작
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utteranceId");
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        });
    }
    private void initializeViews() {
        text_q_num = findViewById(R.id.text_q_num);
        dataTextView1 = findViewById(R.id.dataTextView1);
        dataTextView2 = findViewById(R.id.dataTextView2);
        dataTextView3 = findViewById(R.id.dataTextView3);
        dataTextView4 = findViewById(R.id.dataTextView4);
        dataTextView5 = findViewById(R.id.dataTextView5);
        nextButton = findViewById(R.id.nextButton);
        speak1 = findViewById(R.id.speak1);
        speak2 = findViewById(R.id.speak2);
        speak3 = findViewById(R.id.speak3);
        speak4 = findViewById(R.id.speak4);
        speak5 = findViewById(R.id.speak5);

        // TextToSpeech 초기화
        textToSpeech = new TextToSpeech(this, this);

        // 아직 안 한 질문 리스트
        numberList = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        // 랜덤 인덱스
        random = new Random();
    }
    private void random_question() {
        if (numberList.size() < 5) {
            return;
        }

        List<Integer> randomIndexes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int randomIndex = random.nextInt(numberList.size());
            randomIndexes.add(numberList.get(randomIndex));
            numberList.remove(randomIndex);
        }

        // memory 함수 호출하여 세 개의 랜덤 인덱스에 대해 데이터 가져오기
        memory(randomIndexes.get(0), randomIndexes.get(1), randomIndexes.get(2), randomIndexes.get(3), randomIndexes.get(4));
    }

    private void memory(int randomNumber1, int randomNumber2, int randomNumber3, int randomNumber4, int randomNumber5) {
        String path_m1 = "M" + String.format("%02d", randomNumber1);
        String path_m2 = "M" + String.format("%02d", randomNumber2);
        String path_m3 = "M" + String.format("%02d", randomNumber3);
        String path_m4 = "M" + String.format("%02d", randomNumber4);
        String path_m5 = "M" + String.format("%02d", randomNumber5);

        // 질문 가져오기
        memory_db.getReference(path_m1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                // 가져온 값들을 각각의 dataTextView에 설정
                dataTextView1.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // 질문 가져오기
        memory_db.getReference(path_m2).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                // 가져온 값들을 각각의 dataTextView에 설정
                dataTextView2.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // 질문 가져오기
        memory_db.getReference(path_m3).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                // 가져온 값들을 각각의 dataTextView에 설정
                dataTextView3.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // 질문 가져오기
        memory_db.getReference(path_m4).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                // 가져온 값들을 각각의 dataTextView에 설정
                dataTextView4.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        // 질문 가져오기
        memory_db.getReference(path_m5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                // 가져온 값들을 각각의 dataTextView에 설정
                dataTextView5.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
        if (questionCounter == 1) {
            nextButton.setBackgroundResource(R.drawable.btn_meory_startgame);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Memory02Activity.this, Memory03Activity.class);
                    startActivity(intent);
                }
            });
        }
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
}