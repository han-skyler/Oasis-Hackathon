package com.example.white_butterfly.Register;

import static android.app.Activity.RESULT_OK;

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

import com.example.white_butterfly.MainActivity;
import com.example.white_butterfly.R;
import com.example.white_butterfly.Z_etc.NetworkStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterAddressFragment extends Fragment {
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;

    // 페이지 다음 버튼
    Button btn_next;

    // 입력 있으면 1, 입력 없으면 0
    int address_active = 0;

    // 정보 입력칸
    EditText editText_address;

    // 주소 요청코드 상수 requestCode
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    // 임시 데이터 저장 모델
    public InfoModel infoModel;

    // 뷰
    private View  rootView;

    // 태그
    private static final String TAG = "RegisterAddressFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register_address, container, false);

        Log.w(TAG, "--- RegisterAddressFragment ---");

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
        String savedAddress = infoModel.getInputName();

        // 입력 데이터 선언
        editText_address = rootView.findViewById(R.id.editText_Address);

        // 터치 안 되게 막기
        editText_address.setFocusable(false);

        // 주소입력창 클릭
        editText_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("주소설정페이지", "주소입력창 클릭");
                int status = NetworkStatus.getConnectivityStatus(requireActivity());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI)
                {
                    Log.i(TAG, "주소 입력 액티비티 활성화");
                    // 액티비티
                    Intent i = new Intent(requireActivity(), AddressApiActivity.class);

                    // 주소 결과
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);

                    // 프래그먼트
                    //AddressApiDialogFragment fragment = new AddressApiDialogFragment();
                    //fragment.show(getChildFragmentManager(), "address_api_dialog_fragment");
                } else {
                    Toast.makeText(requireActivity(), "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 가져온 데이터 사용
        //editText_address.setText(savedAddress);

        // 이전 버튼 누른 경우
        ImageView text_before = rootView.findViewById(R.id.image_before);
        text_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "이전 버튼 누름");

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.view_fragment, new RegisterGenderFragment());
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
        editText_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) { address_active = 1; check(); }
                else           { address_active = 0; check(); }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return rootView;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("test", "onActivityResult");

        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        Log.i("test", "data:" + data);
                        editText_address.setText(data);
                        editText_address.requestLayout();
                    }
                }
                break;
        }
    }

    private void onNextButtonClick() {
        Log.w(TAG, "### Register_Address - Next");

        String inputAddress = editText_address.getText().toString();
        infoModel.setInputAddress(inputAddress);

        if (inputAddress.length() > 0) { docRef.update("Address", inputAddress); navigateToNextFragment(); }
        else { Toast.makeText(getContext(), "주소를 입력해 주세요.", Toast.LENGTH_SHORT).show(); }
    }

    private void navigateToNextFragment() {
        Log.w(TAG, "navigateToNextFragment");

        Toast.makeText(getContext(), "정보 입력이 끝났습니다.", Toast.LENGTH_SHORT).show();

        rootView.findViewById(R.id.page_register).setVisibility(View.GONE);

        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.w(TAG, "### Register_Address - onPause");

        // 회원가입을 위한 정보 전달
        String inputAddress = editText_address.getText().toString();

        infoModel.setInputAddress(inputAddress);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "### Register_Address - onResume");

        // 회원가입을 위한 정보 읽기
        String inputAddress = infoModel.getInputAddress();

        if (inputAddress != null && !inputAddress.equals("")) {
            Log.w(TAG, "inputAddress: " + inputAddress);
            editText_address.setText(inputAddress);
        }
    }

    // 칸이 모두 채워졌는지 확인
    public void check() {
        if (address_active == 1) {
            btn_next.setEnabled(true);
        } else {
            btn_next.setEnabled(false);
        }
    }
}
