package com.example.allchat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class create_new_account extends AppCompatActivity {

    private ValueCallback<Uri[]> filePathCallback;
    private static final int FILE_CHOOSER_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_account);
        getSupportActionBar().hide();
        otherMethods.changeStatusBarColor(this);

        WebView webView = findViewById(R.id.webViewContainer);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(DBInfo.hostName+DBInfo.siteName+"Mobile/createAccount.php?check=fromMobile1090");
    }

    public void goBack(View v) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (filePathCallback == null) return;

            Uri[] results = null;

            if (resultCode == RESULT_OK && data != null) {
                Uri dataUri = data.getData();
                if (dataUri != null) {
                    results = new Uri[]{dataUri};
                }
            }

            filePathCallback.onReceiveValue(results);
            filePathCallback = null;
        }
    }
}