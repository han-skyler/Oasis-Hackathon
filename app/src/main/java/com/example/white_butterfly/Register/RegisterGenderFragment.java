package com.example.white_butterfly.Register;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class RegisterGenderFragment extends Fragment {
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    // 페이지 이전, 다음 버튼
    ImageView text_before;
    Button btn_next;

    // 입력 있으면 1, 입력 없으면 0
    int gender_active = 0;

    // 성별 버튼
    Button btn_female;
    Button btn_male;

    // 클릭된 버튼 true
    private boolean femaleisSelected = false;
    private boolean maleisSelected = false;

    // 임시 데이터 저장 모델
    public InfoModel infoModel;

    // 뷰
    private View  rootView;
    private static final String TAG = "RegisterGenderFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register_gender, container, false);

        Log.w(TAG, "--- RegisterGenderFragment ---");

        // Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String id = currentUser.getEmail();
        docRef = db.collection("Users").document(id);

        Log.w(TAG, "User: " + id);

        text_before = rootView.findViewById(R.id.image_before);
        btn_next = rootView.findViewById(R.id.btn_next);
        btn_female = rootView.findViewById(R.id.btn_female);
        btn_male = rootView.findViewById(R.id.btn_male);

        // 임시 데이터 공간
        infoModel = new ViewModelProvider(requireActivity()).get(InfoModel.class);
        infoModel.setInitialValues();

        // 저장된 데이터 가져오기
        String savedGender = infoModel.getInputGender();

        // 가져온 데이터 사용
        if (savedGender != null)
        {
            btn_next.setEnabled(true);

            if (savedGender.equals("남성")) { btn_male.setSelected(true); }
            else if (savedGender.equals("여성")) { btn_female.setSelected(true); }
        }

        // 이전 버튼 누른 경우
        text_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "이전 버튼 누름");

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.view_fragment, new RegisterBirthFragment());
                transaction.commit();

                rootView.findViewById(R.id.page_register).setVisibility(View.GONE);
            }
        });

        // 다음 버튼 누른 경우
        btn_next.setOnClickListener(v -> {
            Log.w(TAG, "다음 버튼 누름");

            onNextButtonClick();
        });

        // 정보가 입력됐을 때 다음 버튼 활성화를 위함
        btn_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maleisSelected) // 만약 남성 버튼이 선택되어 있다면 비활성화
                {
                    maleisSelected = !maleisSelected;
                    btn_male.setSelected(maleisSelected);
                }
                femaleisSelected = !femaleisSelected;
                btn_female.setSelected(femaleisSelected);
                gender_state_check();
            }
        });

        btn_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (femaleisSelected) // 만약 여성 버튼이 선택되어 있다면 비활성화
                {
                    femaleisSelected = !femaleisSelected;
                    btn_female.setSelected(femaleisSelected);
                }
                maleisSelected = !maleisSelected;
                btn_male.setSelected(maleisSelected);
                gender_state_check();
            }
        });

        return rootView;
    }

    private void gender_state_check()
    {
        if (!femaleisSelected && !maleisSelected) { gender_active = 0; }
        else { gender_active = 1; }

        check();
    }

    private void onNextButtonClick() {
        Log.w(TAG, "### Register_Gender - Next");

        String inputGender = "";

        if (maleisSelected) { inputGender = "남성"; }
        else if (femaleisSelected) { inputGender = "여성"; }

        infoModel.setInputGender(inputGender);

        // Firebase에 업로드
        String gender = "";
        if (maleisSelected) { gender = "남성"; }
        else if (femaleisSelected) { gender = "여성"; }

        if (!inputGender.equals(3)) { docRef.update("Gender", gender); navigateToNextFragment(); }
        else { Toast.makeText(getContext(), "성별을 선택해 주세요.", Toast.LENGTH_SHORT).show(); }
    }

    private void navigateToNextFragment() {
        Log.w(TAG, "navigateToNextFragment");

        Fragment fragment2 = new RegisterAddressFragment();
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

        Log.w(TAG, "### Register2 - onPause");

        // 회원가입을 위한 정보 전달
        String inputGender = "";

        if (maleisSelected) { inputGender = "남성"; }
        else if (femaleisSelected) { inputGender = "여성"; }

        infoModel.setInputGender(inputGender);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "### Register2 - onResume");

        // 회원가입을 위한 정보 읽기
        String inputGender = infoModel.getInputGender();

        if (inputGender != null)
        {
            if (inputGender.equals("남성")) { btn_male.setSelected(true); }
            else if (inputGender.equals("여성")) { btn_female.setSelected(true); }
        }

    }

    // 칸이 모두 채워졌는지 확인
    public void check() {
        if (gender_active == 1) {
            btn_next.setEnabled(true);
        } else {
            btn_next.setEnabled(false);
        }
    }
}
