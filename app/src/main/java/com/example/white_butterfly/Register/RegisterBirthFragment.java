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
import android.widget.ImageView;
import android.widget.TextView;
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

public class RegisterBirthFragment extends Fragment {
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    // 페이지 이전, 다음 버튼
    ImageView text_before;
    Button btn_next;

    // 입력 있으면 1, 입력 없으면 0
    int yy_active = 0;
    int mm_active = 0;
    int dd_active = 0;

    // 정보 입력칸
    EditText editText_yyyy;
    EditText editText_mm;
    EditText editText_dd;

    // 임시 데이터 저장 모델
    public InfoModel infoModel;

    // 뷰
    private View  rootView;
    private static final String TAG = "RegisterBirthFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register_birth, container, false);

        Log.w(TAG, "--- RegisterBirthFragment ---");

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
        String savedYear = infoModel.getInputYear();
        String savedMonth = infoModel.getInputMonth();
        String savedDay = infoModel.getInputDay();

        // 입력 데이터 선언
        editText_yyyy = rootView.findViewById(R.id.editText_MyFirst);
        editText_mm = rootView.findViewById(R.id.editText_MyMiddle);
        editText_dd = rootView.findViewById(R.id.editText_MyLast);

        // 가져온 데이터 사용
        editText_yyyy.setText(savedYear);
        editText_mm.setText(savedMonth);
        editText_dd.setText(savedDay);

        // 이전 버튼 누른 경우
        text_before = rootView.findViewById(R.id.image_before);
        text_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.view_fragment, new RegisterPhoneFragment());
                transaction.commit();

                rootView.findViewById(R.id.page_register).setVisibility(View.GONE);
            }
        });

        // 다음 버튼 누른 경우
        btn_next.setOnClickListener(v -> {
            onNextButtonClick();
        });

        // 정보가 입력됐을 때 다음 버튼 활성화를 위함 & 다음 칸으로 넘어감
        setupAutoFocus(editText_yyyy, 4, editText_mm);
        setupAutoFocus(editText_mm, 2, editText_dd);
        setupAutoFocus(editText_dd, 2, null);

        return rootView;
    }

    private void setupAutoFocus(final EditText editText, final int maxLength, final EditText nextEditText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                yy_active = editText_yyyy.length() > 0 ? 1 : 0;
                mm_active = editText_mm.length() > 0 ? 1 : 0;
                dd_active = editText_dd.length() > 0 ? 1 : 0;

                if (charSequence.length() == maxLength && nextEditText != null) {
                    nextEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void onNextButtonClick() {
        Log.w(TAG, "### Register_Birth - Next");

        String inputYear = editText_yyyy.getText().toString();
        String inputMonth = editText_mm.getText().toString();
        String inputDay = editText_dd.getText().toString();

        infoModel.setInputYear(inputYear);
        infoModel.setInputMonth(inputMonth);
        infoModel.setInputDay(inputDay);

        try {
            if (Integer.parseInt(inputYear) > 1900 && Integer.parseInt(inputYear) <= 2023) {
                if (Integer.parseInt(inputMonth) > 0 && Integer.parseInt(inputMonth) <= 12) {
                    if (Integer.parseInt(inputDay) > 0 && Integer.parseInt(inputDay) <= 31) {
                        String birth = String.format("%s년 %s월 %s일", infoModel.getInputYear(), infoModel.getInputMonth(), infoModel.getInputDay());
                        docRef.update("Birth", birth);
                        navigateToNextFragment();
                    } else {
                        Toast.makeText(getContext(), "태어난 일이 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "태어난 월이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "태어난 연도가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e) {
            Toast.makeText(getContext(), "생년월일이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToNextFragment() {
        Log.w(TAG, "navigateToNextFragment");

        Fragment fragment2 = new RegisterGenderFragment();
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

        Log.w(TAG, "### Register_Birth - onPause");

        // 회원가입을 위한 정보 전달
        String inputYear = editText_yyyy.getText().toString();
        String inputMonth = editText_mm.getText().toString();
        String inputDay = editText_dd.getText().toString();

        infoModel.setInputYear(inputYear);
        infoModel.setInputMonth(inputMonth);
        infoModel.setInputDay(inputDay);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "### Register_Birth - onResume");

        // 회원가입을 위한 정보 읽기
        String inputYear = infoModel.getInputYear();
        String inputMonth = infoModel.getInputMonth();
        String inputDay = infoModel.getInputDay();

        if (inputYear != null)
        {
            editText_yyyy.setText(inputYear);
        }
        if (inputMonth != null)
        {
            editText_mm.setText(inputMonth);
        }
        if (inputDay != null)
        {
            editText_dd.setText(inputDay);
        }
    }

    // 칸이 모두 채워졌는지 확인
    public void check() {
        if (yy_active == 1 && mm_active == 1 && dd_active == 1) {
            btn_next.setEnabled(true);
        } else {
            btn_next.setEnabled(false);
        }
    }
}
