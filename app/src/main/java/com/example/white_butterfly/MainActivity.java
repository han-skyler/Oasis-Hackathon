package com.example.white_butterfly;

import static android.view.View.GONE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.white_butterfly.Center.CenterActivity;
import com.example.white_butterfly.Chatbot.ChatbotMainActivity;
import com.example.white_butterfly.Community.CommunityMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    // Firebase
    FirebaseFirestore db;
    FirebaseUser currentUser;
    DocumentReference docRef;

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // 광고 배너
    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 3;

    // 뷰
    TextView text_UserName;
    TextView text_finalDay;
    TextView text_1;
    TextView text_chatbot_msg;
    TextView text_finalResult;
    Button btn_resultCheck;
    private ProgressBar loadBar;

    // 변수
    String result;  // 검사 결과
    Boolean hide = false;  // 검사 결과 숨길지 말지 (기본값: 결과 보임)

    // 권한
    private static final int REQUEST_CALL_PHONE_PERMISSION = 1001;

    // 문자열
    String chatbot_msg_breakfast;
    String chatbot_msg_morning;
    String chatbot_msg_lunch;
    String chatbot_msg_afternoon;
    String chatbot_msg_dinner;
    String chatbot_msg_night;
    String chatbot_msg_else;
    String id;

    // 태그
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.w(TAG, "--- MainActivity ---");

        initializeViews();

        getToken();

        // DocumentSnapshot 객체 생성, 데이터 가져오기
        getData();

        //ViewPager2
        mPager = findViewById(R.id.viewpager);

        //Adapter
        pagerAdapter = new com.example.white_butterfly.Ad.AdAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);

        // ViewPager Setting
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mPager.setCurrentItem(999); //시작 지점
        mPager.setOffscreenPageLimit(3); //최대 이미지 수

        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        ConstraintLayout btn_reservation = findViewById(R.id.btn_reservation);
        btn_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HospitalListActivity.class); // 다른 화면으로 이동
                startActivity(intent);
                docRef.update("ResultHide", hide);
            }
        });

        ConstraintLayout btn_dementiaTest = findViewById(R.id.btn_dementia);
        btn_dementiaTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.white_butterfly.TestCogDep.TestMainActivity.class); // 다른 화면으로 이동
                intent.putExtra("Email", id);
                startActivity(intent);
                docRef.update("ResultHide", hide);
            }
        });

        ConstraintLayout btn_memoryTest = findViewById(R.id.btn_memory);
        btn_memoryTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), com.example.white_butterfly.TestMemory.Memory01Activity.class);
                startActivity(intent);
                docRef.update("ResultHide", hide);
            }
        });

        ConstraintLayout btn_chatbot = findViewById(R.id.btn_chatbot);
        btn_chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_chatbot = new Intent(getApplication(), ChatbotMainActivity.class);

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Number pay = documentSnapshot.getLong("Pay");
                            intent_chatbot.putExtra("Pay", pay);

                            Log.d(TAG, "결제 여부: " + pay);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                    }
                });

                startActivity(intent_chatbot);
                docRef.update("ResultHide", hide);
            }
        });

        TextView text_myPage = findViewById(R.id.text_myPage);
        text_myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(), UserActivity.class);
                intent.putExtra("Email", id);
                startActivity(intent);
                docRef.update("ResultHide", hide);
            }
        });

        btn_resultCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeHideCheck(hide);
            }
        });

        // 치매상담콜센터 번호 누른 경우
        TextView text_call = findViewById(R.id.text_call);
        text_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
                    } else {
                        // 권한이 이미 있으면 전화 걸기 로직을 실행
                        String tel = "tel:18999988";
                        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                    }
                } else {
                    backPressedTime = currentTime;
                    Toast.makeText(MainActivity.this, "한 번 더 누를 시 치매상담콜센터로 연결됩니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                Log.i("My Token",token);

                /* 로그인 된 계정에 업로드 된다는 가정으로 코드를 작성함 */
                // id: 해당 유저의 이메일(아이디)
                docRef.update("fcmToken",token);
            }
        });
    }

    ///////////////////////////////// 뷰 관련

    private void initializeViews() {
        String kakao_email = "";
        try {
            kakao_email = getIntent().getStringExtra("Email");
            Log.d("kakao_email", kakao_email);
        }catch (Exception e){

        }
        text_UserName = findViewById(R.id.text_UserName);
        text_finalDay = findViewById(R.id.text_finalDay);
        text_chatbot_msg = findViewById(R.id.text_chatbot_msg);
        text_finalResult = findViewById(R.id.text_finalResult);
        text_1 = findViewById(R.id.text_1);

        btn_resultCheck = findViewById(R.id.btn_resultCheck);

        chatbot_msg_breakfast = getResources().getString(R.string.chatbot_msg_breakfast);
        chatbot_msg_morning = getResources().getString(R.string.chatbot_msg_morning);
        chatbot_msg_lunch = getResources().getString(R.string.chatbot_msg_lunch);
        chatbot_msg_afternoon = getResources().getString(R.string.chatbot_msg_afternoon);
        chatbot_msg_dinner = getResources().getString(R.string.chatbot_msg_dinner);
        chatbot_msg_night = getResources().getString(R.string.chatbot_msg_night);
        chatbot_msg_else = getResources().getString(R.string.chatbot_msg_else);

        loadBar = findViewById(R.id.loadBar);

        // 현재 로그인 된 유저 정보 읽기
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (kakao_email != null && !kakao_email.isEmpty()) {
            // 이전 액티비티에서 받아온 값이 있는 경우
            id = kakao_email;
        } else {
            // 이전 액티비티에서 값이 없는 경우
            id = currentUser.getEmail();
        }
        docRef = db.collection("Users").document(id);
    }

    ///////////////////////////////// 유저 데이터 불러오기

    private void resultHideCheck(Boolean hide_) {
        try {
            if (hide_) {
                text_finalResult.setText("검사 결과 숨김");
                text_finalResult.setTextColor(ContextCompat.getColor(this, R.color.gray));

                btn_resultCheck.setText("보기");
                hide = true;
            } else {
                text_finalResult.setText(result);
                text_finalResult.setTextColor(ContextCompat.getColor(this, R.color.black));

                btn_resultCheck.setText("숨김");
                hide = false;
            }
        } catch (Exception e) {
            text_finalResult.setText(result);
            text_finalResult.setTextColor(ContextCompat.getColor(this, R.color.black));
            hide = false;
        }
    }

    private void changeHideCheck(Boolean hide_) {
        try {
            if (!hide_) {
                text_finalResult.setText("검사 결과 숨김");
                text_finalResult.setTextColor(ContextCompat.getColor(this, R.color.gray));

                btn_resultCheck.setText("보기");
                hide = true;
                docRef.update("ResultHide", hide);
            } else {
                text_finalResult.setText(result);
                text_finalResult.setTextColor(ContextCompat.getColor(this, R.color.black));

                btn_resultCheck.setText("숨김");
                hide = false;
                docRef.update("ResultHide", hide);
            }
        } catch (Exception e) {
            text_finalResult.setText(result);
            text_finalResult.setTextColor(ContextCompat.getColor(this, R.color.black));
            hide = false;
        }
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
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        text_UserName.setText(documentSnapshot.getString("Name"));

                        try {
                            // 마지막 검사일 D-Day
                            int year = Integer.parseInt(documentSnapshot.getLong("year").toString());
                            int month = Integer.parseInt(documentSnapshot.getLong("month").toString());
                            int day = Integer.parseInt(documentSnapshot.getLong("day").toString());
                            setData(year, month, day);
                            text_1.setVisibility(View.VISIBLE);
                            btn_resultCheck.setVisibility(View.VISIBLE);

                            // 검사 결과
                            try {
                                result = documentSnapshot.getString("Score");
                                text_finalResult.setText(result);
                            } catch (Exception e) {
                                Log.w(TAG, "점수 기록 없음");
                            }

                            // 검사 결과 표시 여부
                            hide = documentSnapshot.getBoolean("ResultHide");
                            resultHideCheck(hide);
                        } catch (Exception e) {
                            text_finalDay.setText("기록이 없어요.");
                            text_1.setVisibility(View.GONE);
                            btn_resultCheck.setVisibility(View.GONE);
                            text_finalResult.setText("검사는 6개월 주기를 추천해요.");
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error getting document", e);
                }
            });

            // 작업 완료 후 로딩 화면 숨김
            loadBar.setVisibility(GONE);
        }
    }

    private void getData() {
        // AsyncTask 실행
        new MainActivity.getDataTask().execute();
    }

    private void setData(int year, int month, int day) {
        try {
            // 테스트 디데이 계산
            LocalDate today = LocalDate.now();
            LocalDate Testday;
            Testday = LocalDate.of(year, month, day);

            long daysBetween = ChronoUnit.DAYS.between(Testday, today);

            if (daysBetween == 0) {
                text_finalDay.setText("오늘");
            } else {
                text_finalDay.setText(daysBetween + "일 전");
            }

            // 현재 시간 가져오기
            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("HH");
            sdf.setTimeZone(TimeZone.getDefault());

            int now_hour = Integer.parseInt(sdf.format(currentDate));

            if (6 <= now_hour && now_hour <= 8) {
                text_chatbot_msg.setText(chatbot_msg_breakfast);
            } else if (9 <= now_hour && now_hour <= 10) {
                text_chatbot_msg.setText(chatbot_msg_morning);
            } else if (11 <= now_hour && now_hour <= 14) {
                text_chatbot_msg.setText(chatbot_msg_lunch);
            } else if (15 <= now_hour && now_hour <= 17) {
                text_chatbot_msg.setText(chatbot_msg_afternoon);
            } else if (18 <= now_hour && now_hour <= 21) {
                text_chatbot_msg.setText(chatbot_msg_dinner);
            } else if (22 <= now_hour && now_hour <= 23 || (0 <= now_hour && now_hour <= 5)) {
                text_chatbot_msg.setText(chatbot_msg_night);
            } else {
                text_chatbot_msg.setText(chatbot_msg_else);
            }
        } catch (Exception e) {
            Log.w(TAG, "Error: " + e);
        }
    }

    ///////////////////////////////// 뒤로 가기 버튼

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - backPressedTime < BACK_PRESS_INTERVAL) {
            super.onBackPressed(); // 앱 종료
        } else {
            backPressedTime = currentTime;
            Toast.makeText(this, "한 번 더 누를 시 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }

    ///////////////////////////////// 치매안심센터

    public void Center(View target)
    {
        Intent intent = new Intent(getApplication(), CenterActivity.class);
        startActivity(intent);
    }

    ///////////////////////////////// 커뮤니티

    public void Naver(View target)
    {
        Intent intent = new Intent(getApplication(), CommunityMainActivity.class);
        intent.putExtra("name", text_UserName.getText().toString());
        startActivity(intent);

        /*
        // 네이버 홈페이지 URL
        String naverUrl = "https://cafe.naver.com/whitebutterflys2";

        // 인텐트 생성 및 웹 브라우저로 이동
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(naverUrl));
        startActivity(intent);

         */
    }
}
