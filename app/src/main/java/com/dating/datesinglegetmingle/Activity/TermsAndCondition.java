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

public class TermsAndCondition extends AppCompatActivity {

    private RelativeLayout terms_back_lay;
    private WebView terms_web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        terms_back_lay = findViewById(R.id.terms_back_lay);
        terms_web = findViewById(R.id.terms_web);

        terms_web.getSettings().setJavaScriptEnabled(true); // enable javascript
        terms_web.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(TermsAndCondition.this, description, Toast.LENGTH_SHORT).show();
            }
        });
        //terms_web.loadUrl("http://logicalsofttech.com/singleGetMingle/welcome/Term_Conditions");
        terms_web.loadUrl("https://datesinglegetmingle.com/Terms_&_Condition.html");

        terms_back_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TermsAndCondition.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TermsAndCondition.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
