package com.example.white_butterfly.Z_etc;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProgressModel extends ViewModel {
    private MutableLiveData<Integer> progressLiveData = new MutableLiveData<>();

    public LiveData<Integer> getProgressLiveData() {
        return progressLiveData;
    }

    public void updateProgress(int progress) {
        progressLiveData.setValue(progress);
    }
}
