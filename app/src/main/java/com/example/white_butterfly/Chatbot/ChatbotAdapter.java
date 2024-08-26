package com.example.white_butterfly.Chatbot;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ChatbotAdapter extends FragmentStateAdapter {

    public int mCount;

    public ChatbotAdapter(FragmentActivity fa, int count) {
        super(fa);
        mCount = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);

        if(index==0) return new Chatbot_BoyFragment();
        else if(index==1) return new Chatbot_GirlFragment();
        else return new Chatbot_CounFragment();
    }

    @Override
    public int getItemCount() {
        return 2000;
    }

    public int getRealPosition(int position) { return position % mCount; }

}