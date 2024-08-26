package com.example.white_butterfly.Login;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.white_butterfly.MainActivity;
import com.example.white_butterfly.R;
import com.example.white_butterfly.Register.RegisterEmailFragment;
import com.example.white_butterfly.Z_etc.NetworkUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText editText_email;
    EditText editText_password;

    // 입력 있으면 1, 입력 없으면 0
    int email_active = 0;
    int pw_active = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        // firebase 접근 권한 갖기
        FirebaseApp.initializeApp(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();

        editText_email = findViewById(R.id.editText_EmailAddress);
        editText_password = findViewById(R.id.editText_Password);

        // 정보가 입력됐을 때 다음 버튼 활성화를 위함
        editText_email.addTextChangedListener(textWatcher);
        editText_password.addTextChangedListener(textWatcher);
    }

    private final TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            email_active = editText_email.length() > 0 ? 1 : 0;
            pw_active = editText_password.length() > 0 ? 1 : 0;

            check();
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    // 이메일과 비밀번호가 둘 다 입력됐는지 확인
    public void check()
    {
        Button btn_login = (Button) findViewById(R.id.btn_signin);

        if (email_active == 1 && pw_active == 1)
        {
            btn_login.setEnabled(true);
        }
        else
        {
            btn_login.setEnabled(false);
        }
    }

    // 회원가입하기 버튼 누를 경우 회원가입 화면으로 이동
    public void Register(View target)
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.page_register_ac, new RegisterEmailFragment())
                .addToBackStack(null)
                .commit();

        findViewById(R.id.page_login).setVisibility(View.GONE);
    }

    // Sign in 버튼 누를 경우 로그인 시도
    public void Login(View target)
    {
        EditText editText_email = (EditText) findViewById(R.id.editText_EmailAddress);
        EditText editText_password = (EditText) findViewById(R.id.editText_Password);

        String email = editText_email.getText().toString();
        String password = editText_password.getText().toString();

        if (email.length() == 0 && password.length() == 0)
        {
            Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력하세요.",
                    Toast.LENGTH_SHORT).show();
        }
        else if (email.length() == 0)
        {
            Toast.makeText(LoginActivity.this, "이메일을 입력하세요.",
                    Toast.LENGTH_SHORT).show();
        }
        else if (password.length() == 0)
        {
            Toast.makeText(LoginActivity.this, "비밀번호를 입력하세요.",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            // 로그인 요청
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful()) // 로그인 성공
                    {
                        Toast.makeText(LoginActivity.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();

                        // 홈 화면으로 이동
                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        startActivity(intent);
                        finish();

                        Log.d(TAG, "< 로그인 성공 >");
                    }
                    else // 로그인 실패
                    {
                        if (NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
                            // 인터넷 연결이 가능한 상태
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(LoginActivity.this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();

                                Log.w(TAG, "< 로그인 실패 - 이메일 형식 오류 >", task.getException());
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();

                                Log.w(TAG, "< 로그인 실패 - 기타 오류 >", task.getException());
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "인터넷 연결이 필요합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}