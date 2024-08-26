package com.example.white_butterfly.Register;
// https://ewaterland.blogspot.com/2023/08/api.html
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.white_butterfly.databinding.ActivityAddressApiBinding;

public class AddressApiActivity extends AppCompatActivity {

    private WebView webView;
    private String TAG = "AddressApiActivity";
    private ActivityAddressApiBinding activityAddressApiBinding;

    public class MyJavaScriptInterface {
        @JavascriptInterface
        public void processDATA(String data) {
            Intent intent = new Intent();
            intent.putExtra("data", data);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 위에서 작성한 블로거 페이지의 URL
        String blogspot = "https://ewaterland.blogspot.com/2023/08/api.html";
        //String blogspot = "http://127.0.0.1/addr_daum.html";

        activityAddressApiBinding = ActivityAddressApiBinding.inflate(getLayoutInflater());
        setContentView(activityAddressApiBinding.getRoot());
        activityAddressApiBinding.webView.getSettings().setJavaScriptEnabled(true);
        activityAddressApiBinding.webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
        activityAddressApiBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // 위 웹페이지가 로드가 끝나면 코드에서 작성했던 스크립트을 호출한다.
                view.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });
        // 위 블로거 페이지를 호출한다.
        activityAddressApiBinding.webView.loadUrl(blogspot);
    }
}