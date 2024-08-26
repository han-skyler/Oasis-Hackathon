package com.example.white_butterfly.Register;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoModel extends ViewModel {
    // 이메일
    private String inputEmail;
    private String inputPW;
    private String inputPW2;

    // 이름
    private String inputName;

    // 본인 연락처
    String My;
    private String inputMyFirst;
    private String inputMyMiddle;
    private String inputMyLast;

    // 보호자 연락처
    String Guardian;
    private String inputGuardianFirst;
    private String inputGuardianMiddle;
    private String inputGuardianLast;

    // 생년월일
    String Birth;
    private String inputYear;
    private String inputMonth;
    private String inputDay;

    // 성별
    private String inputGender;

    // 주소
    private String inputAddress;

    // 학력
    // 1: 학교x. 2: 초등학교 졸업. 3: 중학교 졸업. 4: 고등학교 졸업. 5: 대학교 졸업.
    private int inputSchool;

    // 점수
    private int inputScore_cog;
    private int inputScore_dep;

    // ---------------------------------

    // Firebase 에서 초기값 설정
    public void setInitialValues() {
        Log.w(TAG, "setInitialValues");

        // Firebase에서 데이터를 가져와서 초기값 설정
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String id = currentUser.getEmail();
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(id);

            // 여기에서 필요한 데이터를 가져와서 초기값 설정
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    inputName = documentSnapshot.getString("Name");
                    My = documentSnapshot.getString("My");
                    Guardian = documentSnapshot.getString("Guardian");
                    Birth = documentSnapshot.getString("Birth");
                    inputGender = documentSnapshot.getString("Gender");
                    inputAddress = documentSnapshot.getString("Address");
                    inputSchool = documentSnapshot.getLong("School").intValue();
                }
            });
        }

        try
        {
            String[] my_parts = My.split("-");
            inputMyFirst = my_parts[0];
            inputMyMiddle = my_parts[1];
            inputMyLast = my_parts[2];
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            String[] guard_parts = Guardian.split("-");
            inputGuardianFirst = guard_parts[0];
            inputGuardianMiddle = guard_parts[1];
            inputGuardianLast = guard_parts[2];
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            String[] birth_parts = Birth.split(" ");
            String yearPart = birth_parts[0];  // "2001년"
            String monthPart = birth_parts[1]; // "10월"
            String dayPart = birth_parts[2];   // "12일"

            inputYear = yearPart.replace("년", "");
            inputMonth = monthPart.replace("월", "");
            inputDay = dayPart.replace("일", "");
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // 이메일, 비밀번호
    public String getInputEmail() {
        return inputEmail;
    }
    public void setInputEmail(String inputEmail) {
        this.inputEmail = inputEmail;
    }

    public String getInputPW() {
        return inputPW;
    }
    public void setInputPW(String inputPW) {
        this.inputPW = inputPW;
    }

    public String getInputPW2() {
        return inputPW2;
    }
    public void setInputPW2(String inputPW2) {
        this.inputPW2 = inputPW2;
    }


    // 이름
    public String getInputName() {
        return inputName;
    }
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }


    // 본인 연락처
    public String getInputMyFirst() {
        return inputMyFirst;
    }
    public void setInputMyFirst(String inputMyFirst) {
        this.inputMyFirst = inputMyFirst;
    }

    public String getInputMyMiddle() {
        return inputMyMiddle;
    }
    public void setInputMyMiddle(String inputMyMiddle) {
        this.inputMyMiddle = inputMyMiddle;
    }

    public String getInputMyLast() {
        return inputMyLast;
    }
    public void setInputMyLast(String inputMyPhone) {
        this.inputMyLast = inputMyLast;
    }


    // 보호자 연락처
    public String getInputGuardianFirst() {
        return inputGuardianFirst;
    }
    public void setInputGuardianFirst(String inputGuardianFirst) {
        this.inputGuardianFirst = inputGuardianFirst;
    }

    public String getInputGuardianMiddle() {
        return inputGuardianMiddle;
    }
    public void setInputGuardianMiddle(String inputGuardianMiddle) {
        this.inputGuardianMiddle = inputGuardianMiddle;
    }

    public String getInputGuardianLast() {
        return inputGuardianLast;
    }
    public void setInputGuardianLast(String inputGuardianLast) {
        this.inputGuardianLast = inputGuardianLast;
    }


    // 생년월일
    public String getInputYear() { return inputYear; }
    public void setInputYear(String inputYear) { this.inputYear = inputYear; }

    public String getInputMonth() { return inputMonth; }
    public void setInputMonth(String inputMonth) { this.inputMonth = inputMonth; }

    public String getInputDay() { return inputDay; }
    public void setInputDay(String inputDay) { this.inputDay = inputDay; }


    // 성별
    public String getInputGender() {
        return inputGender;
    }
    public void setInputGender(String inputGender) {
        this.inputGender = inputGender;
    }


    // 주소
    public String getInputAddress() { return inputAddress; }
    public void setInputAddress(String inputAddress) { this.inputAddress = inputAddress; }


    // 학력
    public int getInputSchool() {
        return inputSchool;
    }
    public void setInputSchool(int inputSchool) {
        this.inputSchool = inputSchool;
    }


    // 검사 점수
    public int getInputScore_cog() {
        return inputScore_cog;
    }
    public void setInputScore_cog(int inputScore_cog) {
        this.inputScore_cog = inputScore_cog;
    }
    public int getInputScore_dep() {
        return inputScore_dep;
    }
    public void setInputScore_dep(int inputScore_dep) {
        this.inputScore_dep = inputScore_dep;
    }
}
