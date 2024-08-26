package com.example.white_butterfly;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.white_butterfly.Login.LoginMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {
    // Firebase
    FirebaseFirestore db;
    FirebaseUser currentUser;
    DocumentReference docRef;
    private StorageReference storageReference;
    private static final int PICK_IMAGE_REQUEST = 1;

    // 뷰
    ImageView image_before;
    TextView Text_Name;
    TextView Text_Birthday;
    private ImageView imageView;
    private ProgressBar loadBar;

    // 입력 받은 정보를 저장할 공간
    public String name;
    String gender;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Log.w(TAG, "--- UserActivity ---");

        initializeViews();

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(UserActivity.this);

        TextView Text_Email = findViewById(R.id.text_userEmail);
        Text_Email.setText(id);

        // DocumentSnapshot 객체 생성, 데이터 가져오기
        getData();

        storageReference = FirebaseStorage.getInstance().getReference(id);

        onPageTransition();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        image_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_main = new Intent(getApplication(), MainActivity.class);
                startActivity(intent_main);
            }
        });
    }

    ///////////////////////////////// 뷰 관련

    private void initializeViews() {
        String kakao_email = getIntent().getStringExtra("Email");
        Text_Name = findViewById(R.id.text_userName);
        Text_Birthday = findViewById(R.id.text_userBirth);

        imageView = findViewById(R.id.profileImageView);

        image_before = findViewById(R.id.image_before);
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
                        Text_Name.setText(documentSnapshot.getString("Name"));
                        Text_Birthday.setText(String.valueOf(documentSnapshot.getString("Birth")));
                        gender = documentSnapshot.getString("Gender");
                        Log.d(TAG, "Gender: " + gender);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error getting document", e);
                }
            });

            // 작업 완료 후 로딩 화면 숨김
            loadBar.setVisibility(View.GONE);
        }
    }

    private void getData() {
        // AsyncTask 실행
        new getDataTask().execute();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void onPageTransition() {
        // StorageReference를 통해 이미지 파일을 가져옵니다.
        StorageReference fileReference = storageReference.child("image.jpg");

        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // 이미지가 존재하는 경우, 이미지 다운로드 URL을 가져와 ImageView에 표시
            Glide.with(this)
                    .load(uri)
                    .into(imageView);
            Log.d(TAG, "저장된 프로필: " + "프로필 사진을 불러옴");
        }).addOnFailureListener(urlFailure -> {
            // 이미지가 존재하지 않는 경우
            if(Objects.equals(gender, "여성")) {
                imageView.setImageResource(R.drawable.app_icon_female);
            } else {
                imageView.setImageResource(R.drawable.app_icon_male);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            StorageReference fileReference = storageReference.child("image.jpg");

            // 이미지 업로드 수행
            UploadTask uploadTask = fileReference.putFile(imageUri);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // 이미지 업로드 성공 시, 이미지 다운로드 URL을 가져와 ImageView에 표시
                fileReference.getDownloadUrl().addOnSuccessListener(newDownloadUri -> {
                    if (newDownloadUri != null) {
                        // 이미지 다운로드 URL(uri)을 이용하여 이미지 뷰에 이미지 설정
                        // Glide 라이브러리 등을 사용하여 이미지 로딩 가능
                        Glide.with(this)
                                .load(newDownloadUri)
                                .into(imageView);
                        Toast.makeText(this, "프로필 사진을 변경하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "프로필 변경: " + "프로필 사진을 변경함");
                    } else {
                        Log.d(TAG, "저장소에 이미지가 없습니다.");
                    }
                });

            }).addOnFailureListener(uploadFailure -> {
                Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Logout 버튼 누를 경우
    public void Logout(View target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("로그아웃");
        builder.setMessage("정말 로그아웃을 하시겠습니까?");

        // '네' 버튼 설정
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 유저 로그아웃
                FirebaseAuth.getInstance().signOut();

                Toast.makeText(UserActivity.this, "로그아웃 되었습니다.",
                        Toast.LENGTH_SHORT).show();

                // Login 화면으로 이동
                Intent intent = new Intent(getApplication(), LoginMainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // '아니오' 버튼 설정
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(UserActivity.this, "로그아웃이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // 팝업 닫기
            }
        });

        // 팝업 표시
        AlertDialog dialog = builder.create();
        dialog.show();

        // 버튼 텍스트 색상 변경
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(Color.BLACK);
        negativeButton.setTextColor(Color.BLACK);
    }

    // 회원 탈퇴 버튼 누를 경우
    public void withdraw(View target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회원 탈퇴");
        builder.setMessage("정말 탈퇴하시겠습니까?");

        // '네' 버튼 설정
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String kakao_email = getIntent().getStringExtra("Email");

                // 로그인한 사용자 정보 가져오기
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null) {
                    String id = currentUser.getEmail();

                    // 유저 삭제
                    currentUser.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // 회원 탈퇴 성공
                                        Log.d(TAG, "< 파이어베이스 유저 탈퇴 성공 >");
                                        deleteUserInfo(id);
                                    } else {
                                        // 회원 탈퇴 실패
                                        Log.d(TAG, "< 파이어베이스 유저 탈퇴 실패 >", task.getException());
                                    }
                                }
                            });
                } else {
                    deleteUserInfo(kakao_email);
                }
            }
        });

        // '아니오' 버튼 설정
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(UserActivity.this, "회원 탈퇴가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                dialog.dismiss(); // 팝업 닫기
            }
        });

        // 팝업 표시
        AlertDialog dialog = builder.create();

        // 버튼 텍스트 색상 변경
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                positiveButton.setTextColor(Color.BLACK);
                negativeButton.setTextColor(Color.BLACK);
            }
        });

        dialog.show();
    }

    // 유저 정보 삭제 메서드
    private void deleteUserInfo(String id) {
        db.collection("Users").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserActivity.this, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();

                        // 로그인 화면으로 이동
                        Intent intent = new Intent(UserActivity.this, LoginMainActivity.class);
                        startActivity(intent);
                        finish();

                        Log.d(TAG, "< 유저 정보 삭제 성공 >");
                        Log.d(TAG, "< 회원 탈퇴 > Email: " + id);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "< 유저 정보 삭제 실패 >", e);
                    }
                });
    }
}