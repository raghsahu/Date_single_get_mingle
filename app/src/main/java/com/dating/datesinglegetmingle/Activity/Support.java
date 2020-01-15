package com.dating.datesinglegetmingle.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dating.datesinglegetmingle.Bean.AlertConnection;
import com.dating.datesinglegetmingle.Bean.AppConfig;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class Support extends AppCompatActivity {

    private RelativeLayout support_back_lay;
    private ProgressDialog progressDialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String user_id = "", email = "", message = "";
    private EditText support_email_edt, support_msg_edt;
    private CardView support_send_card;
    private WebView support_web;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        progressDialog = new ProgressDialog(Support.this);

        session = new Session(this);
        user_id = session.getUser().user_id;

        CheckInternet();
        Init();
        Onclick();

    }

    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void Init() {

        support_back_lay = findViewById(R.id.support_back_lay);
        support_msg_edt = findViewById(R.id.support_msg_edt);
        support_email_edt = findViewById(R.id.support_email_edt);
        support_send_card = findViewById(R.id.support_send_card);
        support_web = findViewById(R.id.support_web);

        support_web.getSettings().setJavaScriptEnabled(true); // enable javascript

        support_web.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(Support.this, description, Toast.LENGTH_SHORT).show();
            }
        });

        support_web.loadUrl("http://logicalsofttech.com/singleGetMingle/welcome/Contact_us");
        // setContentView(support_web);


    }

    private void Onclick() {

        support_back_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Support.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        support_send_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (support_msg_edt.getText().toString().trim().equalsIgnoreCase("")) {
                    support_msg_edt.setError(getString(R.string.can_not_be_empty));
                    support_msg_edt.requestFocus();

                } else if (support_msg_edt.getText().toString().trim().equalsIgnoreCase("")) {
                    support_msg_edt.setError(getString(R.string.can_not_be_empty));
                    support_msg_edt.requestFocus();

                } else {

                    email = support_msg_edt.getText().toString().trim();
                    message = support_msg_edt.getText().toString().trim();

                    if (isInternetPresent) {
                        ContactSupport_call();
                    } else {
                        AlertConnection.showAlertDialog(Support.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                    }
                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Support.this, Home.class);
        startActivity(intent);
        finish();
    }
    //------------- support call ------------------

    private void ContactSupport_call() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface().contactsupport(user_id, email, message);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("support response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        if (error.equals("true")) {
                            Toast.makeText(Support.this, "Your Contact Message Sent Successfully", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            String message = object.getString("msg");
                            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("error", "some error");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
