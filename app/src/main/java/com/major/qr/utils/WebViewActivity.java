package com.major.qr.utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;

import com.major.qr.databinding.ActivityWebviewBinding;

public class WebViewActivity extends Activity {

    ActivityWebviewBinding binding;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        String URL = bundle.getString("URL");

        binding.webView1.getSettings().setJavaScriptEnabled(true);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();

        String data = "<html><head><title>Example</title><meta name=\"viewport\"\"content=\"width=" + width + ", initial-scale=0.65 \" /></head>";
        data = data + "<body><center><img width=\"" + width + "\" src=\"" + URL + "\" /></center></body></html>";
        binding.webView1.loadData(data, "text/html", null);

//        binding.webView1.getSettings().setLoadWithOverviewMode(true);
//        binding.webView1.getSettings().setUseWideViewPort(true);
//        binding.webView1.loadDataWithBaseURL(URL, "<style>img{display: inline;height: auto;max-width: 100%;}</style>"
//               , "text/html", "UTF-8", null);
//        binding.webView1.loadUrl(URL);
    }
}