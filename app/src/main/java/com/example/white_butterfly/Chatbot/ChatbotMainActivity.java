package com.example.white_butterfly.Chatbot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.white_butterfly.R;

import me.relex.circleindicator.CircleIndicator3;

public class ChatbotMainActivity extends AppCompatActivity {
    // 챗봇 자기소개
    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 3;
    private CircleIndicator3 mIndicator;

    // 현재 페이지 추적
    int now = 0;

    // TAG
    private static final String TAG = "ChatbotMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot_main);

        Log.w(TAG, "--- ChatbotMainActivity ---");

        Button btn_next = findViewById(R.id.btn_next);

        mPager = findViewById(R.id.viewpager);

        pagerAdapter = new ChatbotAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);

        //Indicator
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.createIndicators(num_page,0);

        // ViewPager Setting
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mPager.setCurrentItem(999); //시작 지점
        mPager.setOffscreenPageLimit(3); //최대 이미지 수

        // 유료/무료 계정 확인
        Number pay = getIntent().getLongExtra("Pay", 0);

        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position%num_page);
                now = position%num_page;

                if (now == 2)  // 상담사 페이지일 때
                {
                    Log.w(TAG, "ChatbotMain - Pay: " + pay);
                    if (pay.equals(1)) {  // 유료 계정인 경우 버튼 활성화
                        btn_next.setEnabled(true);
                    }
                    else {  // 무료 계정인 경우 버튼 비활성화
                        btn_next.setEnabled(false);
                    }
                }
                else  // 나머지 애들 버튼 활성화
                {
                    btn_next.setEnabled(true);
                }
            }
        });
        
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (now)
                {
                    case 0:
                        Intent intent_boy = new Intent(getApplication(), ChatbotBoyActivity.class);
                        startActivity(intent_boy);
                        break;
                    case 1:
                        Intent intent_girl = new Intent(getApplication(), ChatbotGirlActivity.class);
                        startActivity(intent_girl);
                        break;
                    case 2:
                        Intent intent_coun = new Intent(getApplication(), ChatbotCounActivity.class);
                        startActivity(intent_coun);
                        break;
                }
            }
        });
    }
}