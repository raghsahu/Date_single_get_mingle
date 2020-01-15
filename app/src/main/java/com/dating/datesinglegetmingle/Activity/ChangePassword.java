package com.dating.datesinglegetmingle.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class ChangePassword extends AppCompatActivity {

    private RelativeLayout change_pass_back_lay;
    private ProgressDialog progressDialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String user_id = "", old_pass = "", new_pass = "";
    private Button chnage_pass_save_btn, chnage_pass_cancel_btn;
    private EditText current_pass_edt, new_pass_edt, confirm_new_pass_edt;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        progressDialog = new ProgressDialog(ChangePassword.this);

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

        change_pass_back_lay = findViewById(R.id.change_pass_back_lay);
        chnage_pass_save_btn = findViewById(R.id.chnage_pass_save_btn);
        chnage_pass_cancel_btn = findViewById(R.id.chnage_pass_cancel_btn);
        current_pass_edt = findViewById(R.id.current_pass_edt);
        new_pass_edt = findViewById(R.id.new_pass_edt);
        confirm_new_pass_edt = findViewById(R.id.confirm_new_pass_edt);
    }

    private void Onclick() {

        change_pass_back_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        chnage_pass_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        chnage_pass_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (current_pass_edt.getText().toString().trim().equalsIgnoreCase("")) {
                    current_pass_edt.setError(getString(R.string.can_not_be_empty));
                    current_pass_edt.requestFocus();

                } else if (new_pass_edt.getText().toString().trim().equalsIgnoreCase("")) {
                    new_pass_edt.setError(getString(R.string.can_not_be_empty));
                    new_pass_edt.requestFocus();

                } else if (new_pass_edt.getText().toString().length() < 5) {
                    Toast.makeText(ChangePassword.this, "Password should have minimum 5 character", Toast.LENGTH_SHORT).show();

                } else if (confirm_new_pass_edt.getText().toString().trim().equalsIgnoreCase("")) {
                    confirm_new_pass_edt.setError(getString(R.string.can_not_be_empty));
                    confirm_new_pass_edt.requestFocus();

                } else if (!new_pass_edt.getText().toString().equalsIgnoreCase(confirm_new_pass_edt.getText().toString())) {
                    Toast.makeText(ChangePassword.this, R.string.passmismatch, Toast.LENGTH_SHORT).show();
                } else {

                    old_pass = current_pass_edt.getText().toString().trim();
                    new_pass = new_pass_edt.getText().toString().trim();

                    if (isInternetPresent) {
                        ChangePassword_call();
                    } else {
                        AlertConnection.showAlertDialog(ChangePassword.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                    }
                }


            }
        });
    }

    //------------- change password ------------------

    private void ChangePassword_call() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface().changePassword(user_id, old_pass, new_pass);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("change_pass response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        if (error.equals("true")) {
                            Toast.makeText(ChangePassword.this, "Password Changed successfully", Toast.LENGTH_SHORT).show();
                            session.logout();
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
