package com.dating.datesinglegetmingle.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dating.datesinglegetmingle.R;

public class FAQ extends AppCompatActivity {

    private RelativeLayout faq_lay;
    private WebView about_us_web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        faq_lay = findViewById(R.id.faq_lay);
        about_us_web = findViewById(R.id.about_us_web);

        about_us_web.getSettings().setJavaScriptEnabled(true); // enable javascript
        about_us_web.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(FAQ.this, description, Toast.LENGTH_SHORT).show();
            }
        });
        //about_us_web.loadUrl("http://logicalsofttech.com/singleGetMingle/welcome/About_Us");
        about_us_web.loadUrl("https://datesinglegetmingle.com/About_us.html");

        faq_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FAQ.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(FAQ.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
