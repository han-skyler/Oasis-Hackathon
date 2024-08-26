package com.example.white_butterfly.Register;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.white_butterfly.Login.LoginActivity;
import com.example.white_butterfly.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterEmailFragment extends Fragment {
    // Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private String id;

    // 페이지 다음 버튼
    Button btn_next;

    // 입력 있으면 1, 입력 없으면 0
    private int email_active = 0;
    private int pw_active = 0;
    private int pwc_active = 0;

    // 정보 입력칸
    private EditText editText_email;
    private EditText editText_password;
    private EditText editText_password_2;

    // 임시 데이터 저장 모델
    private InfoModel infoModel;

    // 뷰
    private View rootView;
    private static final String TAG = "RegisterEmailFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w(TAG, "--- RegisterEmailFragment ---");

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(requireActivity());
        mAuth = FirebaseAuth.getInstance();

        // 임시 데이터 공간
        infoModel = new ViewModelProvider(requireActivity()).get(InfoModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register_email, container, false);

        initializeViews();

        return rootView;
    }

    private void initializeViews() {
        // 입력 데이터 선언
        editText_email = rootView.findViewById(R.id.editText_EmailAddress);
        editText_password = rootView.findViewById(R.id.editText_Password);
        editText_password_2 = rootView.findViewById(R.id.editText_Password_2);

        // 이전 버튼 누른 경우
        ImageView text_before = rootView.findViewById(R.id.image_before);
        text_before.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        // 다음 버튼 누른 경우
        btn_next = rootView.findViewById(R.id.btn_next);
        btn_next.setOnClickListener(v -> {
            Log.w(TAG, "다음 버튼 누름");

            onNextButtonClick();
        });

        // 정보가 입력됐을 때 다음 버튼 활성화를 위함
        editText_email.addTextChangedListener(textWatcher);
        editText_password.addTextChangedListener(textWatcher);
        editText_password_2.addTextChangedListener(textWatcher);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            email_active = editText_email.length() > 0 ? 1 : 0;
            pw_active = editText_password.length() > 0 ? 1 : 0;
            pwc_active = editText_password_2.length() > 0 ? 1 : 0;

            check();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void onNextButtonClick() {
        Log.w(TAG, "### Register - Next");

        String inputEmail = editText_email.getText().toString();
        String inputPW = editText_password.getText().toString();
        String inputPW2 = editText_password_2.getText().toString();

        infoModel.setInputEmail(inputEmail);
        infoModel.setInputPW(inputPW);
        infoModel.setInputPW2(inputPW2);

        id = inputEmail;

        if (inputEmail.length() > 0 && inputPW.length() > 0 && inputPW2.length() > 0) {
            if (inputPW.equals(inputPW2)) {
                if (inputPW.length() >= 6) {
                    registerUser(inputEmail, inputPW);
                } else {
                    Toast.makeText(getContext(), "비밀번호를 6자리 이상 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "이메일과 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        saveUserInfoToDatabase();
                    } else {
                        Toast.makeText(getContext(), "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserInfoToDatabase() {
        Map<String, Object> user = new HashMap<>();
        user.put("Email", id);        // 이메일
        user.put("Name", "");      // 이름
        user.put("Gender", "");    // 성별
        user.put("Address", "");   // 주소
        user.put("Birth", "");     // 생년월일
        user.put("My", "");        // 본인 연락처
        user.put("Guardian", "");  // 보호자 연락처
        user.put("Score", "검사한 적이 없어요!");   // 마지막 치매 선별 검사 결과
        user.put("Pay", 0);    // 유료/무료 계정 구분 (0: 무료, 1: 유료)

        db.collection("Users").document(id).set(user)
                .addOnSuccessListener(unused -> Log.d(TAG, "< 데이터베이스에 유저 정보 저장 성공 >"))
                .addOnFailureListener(e -> Log.d(TAG, "< 데이터베이스에 유저 정보 저장 실패 >"));

        navigateToNextFragment();
    }

    private void navigateToNextFragment() {
        rootView.findViewById(R.id.page_register).setVisibility(View.GONE);

        Intent intent = new Intent(requireActivity(), RegisterFinishActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.w(TAG, "### Register_Email - Pause");

        String inputEmail = editText_email.getText().toString();
        String inputPW = editText_password.getText().toString();
        String inputPW2 = editText_password_2.getText().toString();

        infoModel.setInputEmail(inputEmail);
        infoModel.setInputPW(inputPW);
        infoModel.setInputPW2(inputPW2);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "### Register_Email - Resume");

        String inputEmail = infoModel.getInputEmail();
        String inputPW = infoModel.getInputPW();
        String inputPW2 = infoModel.getInputPW2();

        editText_email.setText(inputEmail);
        editText_password.setText(inputPW);
        editText_password_2.setText(inputPW2);
    }

    private void check() {
        if (email_active == 1 && pw_active == 1 && pwc_active == 1) {
            btn_next.setEnabled(true);
        } else {
            btn_next.setEnabled(false);
        }
    }
}