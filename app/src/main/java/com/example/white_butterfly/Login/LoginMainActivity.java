package com.example.white_butterfly.Login;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.white_butterfly.MainActivity;
import com.example.white_butterfly.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;

import java.util.HashMap;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginMainActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // 버튼
    ConstraintLayout kakaologinButton;
    ConstraintLayout emailloginButton;
    String name;
    String email;
    String birth;

    // TAG
    private static final String TAG = "LoginMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        Log.w(TAG, "--- LoginMainActivity ---");

        Log.e("Debug", Utility.INSTANCE.getKeyHash(this));

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(LoginMainActivity.this);

        // SDK 초기화
        KakaoSdk.init(this, "7dbd9a5212706340ef14160f7b431a33");

        kakaologinButton = findViewById(R.id.kakaologin);
        emailloginButton = findViewById(R.id.emaillogin);


        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                if (oAuthToken != null) {
                    Log.d("카카오 로그인 성공", "카카오 로그인 성공");
                    Toast.makeText(LoginMainActivity.this, "카카오 로그인이 되었습니다.",
                            Toast.LENGTH_SHORT).show();

                    // 로그인 성공 후 사용자 정보 요청
                    UserApiClient.getInstance().me((user, error) -> {
                        if (error != null) {
                            Log.e("카카오 사용자 정보 요청 실패", String.valueOf(error));
                        } else if (user != null) {
                            // 사용자 정보 출력
                            Log.d("사용자 ID", String.valueOf(user.getId()));
                            Log.d("사용자 닉네임", user.getKakaoAccount().getProfile().getNickname());
                            Log.d("사용자 이메일", user.getKakaoAccount().getEmail());
                            Log.d("사용자 프로필", String.valueOf(user.getKakaoAccount().getProfileImageNeedsAgreement()));
                            Log.d("사용자 생일", user.getKakaoAccount().getBirthday());

                            name = user.getKakaoAccount().getProfile().getNickname();
                            email = user.getKakaoAccount().getEmail();
                            birth = user.getKakaoAccount().getBirthday();

                            DocumentReference docRef = db.collection("Users").document(email);

                            docRef.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // 이미 같은 이메일로 만들어진 문서가 존재할 때
                                        Log.d(TAG, "Document exists!");
                                    } else {
                                        // 같은 이메일로 만들어진 문서가 없을 때
                                        Log.d(TAG, "Document does not exist!");
                                        Map<String, Object> userData = new HashMap<>();
                                        userData.put("Email", email);
                                        userData.put("Name", name);
                                        userData.put("Gender", "");
                                        userData.put("Address", "");
                                        userData.put("Birth", birth);
                                        userData.put("School", 0);
                                        userData.put("My", "");
                                        userData.put("Guardian", "");
                                        userData.put("Score_cog", "검사한 적이 없어요!");
                                        userData.put("Score_dep", "검사한 적이 없어요!");

                                        docRef.set(userData)
                                                .addOnSuccessListener(unused -> {
                                                    Log.d(TAG, "< 데이터베이스에 유저 정보 저장 성공 >");
                                                })
                                                .addOnFailureListener(e -> Log.d(TAG, "< 데이터베이스에 유저 정보 저장 실패 >"));
                                    }
                                    Intent intent = new Intent(LoginMainActivity.this, MainActivity.class); // 다른 화면으로 이동
                                    intent.putExtra("Email", email);
                                    startActivity(intent);
                                }
                            });
                        }
                        return null;
                    });
                } else {
                    Log.d("카카오 로그인 실패", "카카오 로그인 실패");
                    Toast.makeText(LoginMainActivity.this, "카카오 로그인이 실패하였습니다.",
                            Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        };

        // 카카오 로그인
        kakaologinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginMainActivity.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(LoginMainActivity.this, callback);
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(LoginMainActivity.this, callback);
                }
            }
        });

        // 이메일 로그인
        emailloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
                finish();

                Log.d(ContentValues.TAG, "< emain 로그인으로 이동 >");
            }
        });
    }

    private void saveUserInfoToDatabase() {
    }
}