package com.dating.datesinglegetmingle.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.dating.datesinglegetmingle.Bean.AlertConnection;
import com.dating.datesinglegetmingle.Bean.AppConfig;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Bean.GPSTracker;
import com.dating.datesinglegetmingle.Interface.API;
import com.dating.datesinglegetmingle.Pojo.UserInfoData;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.dating.datesinglegetmingle.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.dating.datesinglegetmingle.Const.ARG_USER;

public class Login extends AppCompatActivity {

    private LinearLayout go_signup_lay;
    private TextView txtview_forgetpassword;
    private Button btn_login;
    private ProgressDialog progressDialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GPSTracker tracker;
    double P_latitude = 0.0, P_longitude = 0.0;
    private String lat = "", lon = "", email = "", pass = "", forgot_email = "", fid = "";
    private EditText edittxt_email, edittxt_password;
    private FirebaseAuth auth;
    private String f_Uid = "";
    private Session session;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(Login.this);
        session = new Session(this);

        resetInstanceId();

        Log.e("token login ", "token " + FirebaseInstanceId.getInstance().getToken());
        Log.e("token login ses11111 ", "token " + session.getTokenId());

        CheckInternet();
        getcurrentlatlon();
        initMethod();
        OnClick();


    }

    //reset fcm id
    public void resetInstanceId() {
        new Thread(() -> {
            try {
                FirebaseInstanceId.getInstance().deleteInstanceId();
                Log.e("token instaaaaaa", "" + FirebaseInstanceId.getInstance().getToken());

                Log.e("", "InstanceId removed and regenerated.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void OnClick() {

        go_signup_lay.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), Signup.class));
            Animatoo.animateSlideUp(Login.this);
        });

        btn_login.setOnClickListener(v -> {
            boolean isemail = isValidMail(email);
            if (edittxt_email.getText().toString().trim().equalsIgnoreCase("")) {
                edittxt_email.setError(getString(R.string.can_not_be_empty));
                edittxt_email.requestFocus();

            } else if (edittxt_password.getText().toString().trim().equalsIgnoreCase("")) {
                edittxt_password.setError(getString(R.string.can_not_be_empty));
                edittxt_password.requestFocus();

            } /*else if (isemail == false) {
                edittxt_email.setError(getString(R.string.invalidemail));
                edittxt_email.requestFocus();
            } */ else {
                getcurrentlatlon();
                // GetFirebaseId();
                email = edittxt_email.getText().toString().trim();
                pass = edittxt_password.getText().toString().trim();
                if (isInternetPresent) {
                    //Login_call();
                    String url = API.BASE_URL + "login";
                    CallLoginApi(url);
                    Log.e("email", email);
                    Log.e("password", pass);

                } else {
                    AlertConnection.showAlertDialog(Login.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                }
            }
        });


        txtview_forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(Login.this);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.setContentView(R.layout.forgotpass_lay);

                final EditText forgot_edittxt_email = dialog.findViewById(R.id.forgot_edittxt_email);
                Button btn_forgot_send = dialog.findViewById(R.id.btn_forgot_send);

                btn_forgot_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (forgot_edittxt_email.getText().toString().equalsIgnoreCase("")) {
                            forgot_edittxt_email.setError(getString(R.string.can_not_be_empty));
                            forgot_edittxt_email.requestFocus();
                        } else {
                            forgot_email = forgot_edittxt_email.getText().toString().trim();

                            if (isInternetPresent) {
                                String url = API.BASE_URL + "forgotpassword";
                                Forgot_call1(url, forgot_email);


                            } else {
                                AlertConnection.showAlertDialog(Login.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                            }

                            /*if (isInternetPresent) {
                                Forgot_call();
                            } else {
                                AlertConnection.showAlertDialog(Login.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                            }*/
                            dialog.cancel();
                        }
                    }
                });

                dialog.show();

            }
        });
    }

    private boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;
        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        p = Pattern.compile(EMAIL_STRING);
        m = p.matcher(email);
        check = m.matches();
        if (!check) {

        }
        return check;
    }

    private void initMethod() {

        go_signup_lay = findViewById(R.id.go_signup_lay);
        txtview_forgetpassword = findViewById(R.id.txtview_forgetpassword);
        btn_login = findViewById(R.id.btn_login);
        edittxt_email = findViewById(R.id.edittxt_email);
        edittxt_password = findViewById(R.id.edittxt_password);
    }

    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void getcurrentlatlon() {

        tracker = new GPSTracker(getApplicationContext());
        if (tracker.canGetLocation()) {
            P_latitude = tracker.getLatitude();
            P_longitude = tracker.getLongitude();
            Log.e("current_lat ", " " + P_latitude);
            Log.e("current_Lon ", " " + P_longitude);
            lat = String.valueOf(P_latitude);
            lon = String.valueOf(P_longitude);
        }
    }

    //------------------------------------ login api  -----------------------------------

    private void Login_call() {
        Utils.showDialog(this, "Please wait...");
        Call<ResponseBody> resultCall = AppConfig.loadInterface().login(email, pass, session.getTokenId(), lat, lon);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                //progressDialog.dismiss();
                Utils.dismissDialog();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("login response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");

                        if (error.equals("true")) {

                            final JSONObject ressult = object.getJSONObject("data");

                            //Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            Log.e("user_name = ", ressult.getString("user_name"));

                            //firebase login
                            //fireBaseLogin();

                        } else {
                            String message = object.getString("msg");
                            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Utils.dismissDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utils.dismissDialog();
                    }
                } else {
                    Log.e("error", "some error");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //progressDialog.dismiss();
                Utils.dismissDialog();
                Toast.makeText(getApplicationContext(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CallLoginApi(String url) {
        Utils.showDialog(this, "Loading Please Wait...");
        Log.e("token login session ", "token " + session.getTokenId());
        AndroidNetworking.post(url)
                .addBodyParameter("user_email", email)
                .addBodyParameter("user_pass", pass)
                .addBodyParameter("lat", lat)
                .addBodyParameter("lng", lon)
                .addBodyParameter("register_id", session.getTokenId())
                .setTag("userLogin")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("Login resp = ", "" + jsonObject);
                        Utils.dismissDialog();
                        try {

                            //JSONObject jsonObject = new JSONObject(response);

                            String msg = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {

                                JSONObject userdetail = jsonObject.getJSONObject("data");
                                UserInfoData user = new UserInfoData();
                                user.user_id = userdetail.getString("user_id");
                                user.user_name = userdetail.getString("user_name");
                                user.user_email = userdetail.getString("user_email");
                                user.user_mobile = userdetail.getString("user_mobile");
                                user.user_pass = userdetail.getString("user_pass");
                                user.address = userdetail.getString("address");
                                user.city = userdetail.getString("city");
                                user.city_name = userdetail.getString("city_name");
                                user.lat = userdetail.getString("lat");
                                user.lng = userdetail.getString("lng");
                                user.gender = userdetail.getString("gender");
                                user.user_status = userdetail.getString("user_status");
                                user.age = userdetail.getString("age");
                                user.age_status = userdetail.getString("age_status");
                                user.register_id = userdetail.getString("register_id");
                                user.image1 = userdetail.getString("image1");
                                user.image2 = userdetail.getString("image2");
                                user.image3 = userdetail.getString("image3");
                                user.image4 = userdetail.getString("image4");
                                user.image5 = userdetail.getString("image5");
                                user.image6 = userdetail.getString("image6");
                                user.education = userdetail.getString("education");
                                user.location = userdetail.getString("location");
                                user.profession = userdetail.getString("profession");
                                user.drinking_habit = userdetail.getString("drinking_habit");
                                user.smoking = userdetail.getString("smoking");
                                user.eating_habit = userdetail.getString("eating_habit");
                                user.about_self = userdetail.getString("about_self");
                                user.interest_id = userdetail.getString("interest_id");
                                user.interest_field_name = userdetail.getString("interest_field_name");
                                user.like_status = userdetail.getString("like_status");
                                user.create_date_time = userdetail.getString("create_date_time");
                                user.login_time = userdetail.getString("login_time");
                                user.last_login = userdetail.getString("last_login");
                                user.f_Uid = userdetail.getString("f_Uid");


                                session.createSession(user);
                                //firebase login
                                //fireBaseLogin();


                                //json obj parsing
                                /*Gson gson = new Gson();
                                Type listType = new TypeToken<List<UserInfoData>>() {
                                }.getType();

                                //json array
                                List<UserInfoData> data = gson.fromJson(jsonObject.getString("data"), listType);*/

                                //json object
                                //UserInfoData user = gson.fromJson(jsonObject.getJSONObject("data").toString(), UserInfoData.class);
                                FirebaseMessaging.getInstance().setAutoInitEnabled(true);
                                FirebaseDatabase.getInstance().getReference().child(ARG_USER).child(user.user_id).setValue(user);
                                Intent intent = new Intent(Login.this, Home.class);
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();

                            } else {
                                ToastClass.showToast(Login.this, msg);
                                //Utils.openAlertDialog(Login.this, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("error = ", error.toString());
                    }
                });

    }

    private void fireBaseLogin() {
        Utils.showDialog(this, "Please wait...");
        auth.signInWithEmailAndPassword(email, "123456789")
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        //progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {

                            Utils.dismissDialog();
                            // there was an error
                            ToastClass.showToast(Login.this, "" + task.getException());
                        } else {
                            Toast.makeText(Login.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Home.class));
                            Animatoo.animateSlideUp(Login.this);
                            finish();
                        }
                    }
                });
    }

    //------------------------------------ Forgot_call -----------------------------------

    private void Forgot_call1(String url, String email) {
        Utils.showDialog(this, "Loading Please Wait...");

        AndroidNetworking.post(url)
                .addBodyParameter("user_email", email)
                .setTag("userLogin")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("forgot resp = ", "" + jsonObject);
                        Utils.dismissDialog();
                        try {

                            //JSONObject jsonObject = new JSONObject(response);

                            String msg = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                ToastClass.showToast(Login.this, msg);
                            } else {
                                ToastClass.showToast(Login.this, msg);
                                //Utils.openAlertDialog(Login.this, msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("error = ", error.toString());
                    }
                });

    }


     /*private void Forgot_call() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface().forgot_password(forgot_email);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("forgot response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        if (error.equals("true")) {
                            Toast.makeText(Login.this, "Password Sent To Mail Successfully", Toast.LENGTH_SHORT).show();
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
    }*/
}
