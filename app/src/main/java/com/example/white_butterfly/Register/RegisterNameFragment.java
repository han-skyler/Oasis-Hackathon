package com.example.white_butterfly.Register;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.white_butterfly.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterNameFragment extends Fragment {
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    // 페이지 다음 버튼 (이름은 정보 입력 페이지의 처음이라 이전 버튼이 없음)
    Button btn_next;

    // 입력 있으면 1, 입력 없으면 0
    int name_active = 0;

    // 정보 입력칸
    EditText editText_name;

    // 임시 데이터 저장 모델
    public InfoModel infoModel;

    // 뒤로가기 버튼
    private static final int BACK_PRESS_INTERVAL = 2000; // 뒤로가기 버튼을 두 번 누르는 간격 (밀리초)
    private long backPressedTime = 0;

    // 뷰
    private View rootView;

    // 태그
    private static final String TAG = "RegisterNameFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register_name, container, false);

        Log.w(TAG, "--- RegisterNameFragment ---");

        // Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = currentUser.getEmail();
        docRef = db.collection("Users").document(id);

        Log.w(TAG, "User: " + id);

        btn_next = rootView.findViewById(R.id.btn_next);

        // 임시 데이터 공간
        infoModel = new ViewModelProvider(requireActivity()).get(InfoModel.class);
        infoModel.setInitialValues();

        // 저장된 데이터 가져오기
        String savedName = infoModel.getInputName();

        // 입력 데이터 선언
        editText_name = rootView.findViewById(R.id.editText_Name);

        // 가져온 데이터 사용
        editText_name.setText(savedName);

        // 다음 버튼 누른 경우
        btn_next.setOnClickListener(v -> {
            onNextButtonClick();
        });

        // 정보가 입력됐을 때 다음 버튼 활성화를 위함
        editText_name.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if   (count > 0)  { name_active = 1; check(); }
                else              { name_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return rootView;
    }

    private void onNextButtonClick() {
        Log.w(TAG, "### Register_Name - Next");

        String inputName = editText_name.getText().toString();

        infoModel.setInputName(inputName);

        if (inputName.length() > 0) { docRef.update("Name", inputName); navigateToNextFragment(); }
        else { Toast.makeText(getContext(), "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show(); }
    }

    private void navigateToNextFragment() {
        Log.w(TAG, "navigateToNextFragment");

        Fragment fragment2 = new RegisterPhoneFragment();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.view_fragment, fragment2);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        rootView.findViewById(R.id.page_register).setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.w(TAG, "### Register_Name - onPause");

        // 회원가입을 위한 정보 전달
        String inputName = editText_name.getText().toString();

        infoModel.setInputName(inputName);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "### Register_Name - onResume");

        // 회원가입을 위한 정보 읽기
        String inputName = infoModel.getInputName();

        editText_name.setText(inputName);

    }

    // 칸이 모두 채워졌는지 확인
    public void check() {
        if (name_active == 1) {
            btn_next.setEnabled(true);
        } else {
            btn_next.setEnabled(false);
        }
    }
}
