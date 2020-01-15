package com.dating.datesinglegetmingle.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.dating.datesinglegetmingle.Const.ARG_USER;

public class Signup extends AppCompatActivity implements View.OnClickListener {

    private final static int REQUEST_CODE = 999;
    private PhoneNumber phoneNumber;
    private String phone;
    String phoneNumberString = "";
    private String socoial_id;

    private TextView hide_age_txt;
    private String isage = "Show", mobile = "", age = "", fid = "", city = "";
    private Button btn_signup;
    private LinearLayout go_to_login;
    private ProgressDialog progressDialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GPSTracker tracker;
    double P_latitude = 0.0, P_longitude = 0.0;
    private String email = "", pass = "", username = "", lat = "", lon = "", gender = "";
    private EditText edittxt_username, edittxt_email, edittxt_phonenumber, edittxt_age;
    private EditText edittxt_password, edittxt_confirmpassword;
    RadioButton radioMale, radioFemale;
    private TextView tv_city;
    private String city_id = "";
    //private FirebaseAuth auth;
    private String f_Uid = "";
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        progressDialog = new ProgressDialog(Signup.this);
        //auth = FirebaseAuth.getInstance();
        session = new Session(this);
        //resetInstanceId();

        //fid = session.getTokenId();
        Log.e("token signup ", "" + session.getTokenId());
        //GetFirebaseId();
        CheckInternet();
        getcurrentlatlon();
        initMethod();
        OnClick();

    }

    public static void resetInstanceId() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    Log.e("token instaaaaaa","" +  FirebaseInstanceId.getInstance().getToken());

                    Log.e("", "InstanceId removed and regenerated.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initMethod() {

        hide_age_txt = findViewById(R.id.hide_age_txt);
        btn_signup = findViewById(R.id.btn_signup);
        go_to_login = findViewById(R.id.go_to_login);
        edittxt_username = findViewById(R.id.edittxt_username);
        edittxt_email = findViewById(R.id.edittxt_email);
        edittxt_phonenumber = findViewById(R.id.edittxt_phonenumber);
        edittxt_age = findViewById(R.id.edittxt_age);
        edittxt_password = findViewById(R.id.edittxt_password);
        edittxt_confirmpassword = findViewById(R.id.edittxt_confirmpassword);
        radioFemale = findViewById(R.id.radioFemale);
        radioMale = findViewById(R.id.radioMale);
        tv_city = findViewById(R.id.tv_city);

        //otp verify
        //phoneLogin(LoginType.PHONE);
    }

    private void OnClick() {

        tv_city.setOnClickListener(this);
        hide_age_txt.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
        go_to_login.setOnClickListener(this);

        radioMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    gender = "Male";
                }
            }
        });

        radioFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked == true) {
                    gender = "Female";
                }
            }
        });
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

    public String getAddress(double lat, double lng) {
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.ENGLISH);
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        city = addresses.get(0).getLocality();
      /*  state = addresses.get(0).getAdminArea();
        country = addresses.get(0).getCountryName();
        postalCode = addresses.get(0).getPostalCode();
        knownName = addresses.get(0).getFeatureName();*/
        Log.e("city", "" + city);

        return address;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_city:
                Intent intent = new Intent(getApplicationContext(), ActivitySelectCity.class);
                intent.putExtra("SEARCH", "1");
                startActivity(intent);
                Animatoo.animateSlideUp(Signup.this);
                break;

            case R.id.hide_age_txt:
                isage = hide_age_txt.getText().toString();
                if (isage.equalsIgnoreCase("Hide")) {
                    hide_age_txt.setText("Show");
                } else {
                    hide_age_txt.setText("Hide");
                }
                break;

            case R.id.btn_signup:
                checkValidation();
                break;

            case R.id.go_to_login:
                startActivity(new Intent(getApplicationContext(), Login.class));
                Animatoo.animateSlideUp(Signup.this);
                break;
        }
    }

    private void checkValidation() {
        if (edittxt_username.getText().toString().trim().equalsIgnoreCase("")) {
            edittxt_username.setError(getString(R.string.can_not_be_empty));
            edittxt_username.requestFocus();

        } else if (edittxt_email.getText().toString().trim().equalsIgnoreCase("")) {
            edittxt_email.setError(getString(R.string.can_not_be_empty));
            edittxt_email.requestFocus();

        } /*else if (edittxt_phonenumber.getText().toString().trim().equalsIgnoreCase("")) {
            edittxt_phonenumber.setError(getString(R.string.can_not_be_empty));
            edittxt_phonenumber.requestFocus();

        } else if (edittxt_phonenumber.getText().toString().length() < 10) {
            Toast.makeText(Signup.this, "Invalid mobile number", Toast.LENGTH_SHORT).show();
        } */ else if (edittxt_age.getText().toString().trim().equalsIgnoreCase("")) {
            edittxt_age.setError(getString(R.string.can_not_be_empty));
            edittxt_age.requestFocus();

        } else if (tv_city.getText().toString().trim().equalsIgnoreCase("")) {
            tv_city.setError(getString(R.string.can_not_be_empty));
            tv_city.requestFocus();

        } else if (edittxt_password.getText().toString().trim().equalsIgnoreCase("")) {
            edittxt_password.setError(getString(R.string.can_not_be_empty));
            edittxt_password.requestFocus();

        } else if (edittxt_password.getText().toString().length() < 5) {
            Toast.makeText(Signup.this, "Password should have minimum 5 character", Toast.LENGTH_SHORT).show();

        } else if (edittxt_confirmpassword.getText().toString().trim().equalsIgnoreCase("")) {
            edittxt_confirmpassword.setError(getString(R.string.can_not_be_empty));
            edittxt_confirmpassword.requestFocus();

        } else if (!edittxt_password.getText().toString().equalsIgnoreCase(edittxt_confirmpassword.getText().toString())) {
            Toast.makeText(Signup.this, R.string.passmismatch, Toast.LENGTH_SHORT).show();
        } else if (gender.equalsIgnoreCase("")) {
            Toast.makeText(Signup.this, R.string.pleaseselctgender, Toast.LENGTH_SHORT).show();

        } else {
            getcurrentlatlon();
            //getAddress(P_latitude, P_longitude);
            city = tv_city.getText().toString().trim();
            username = edittxt_username.getText().toString().trim();
            email = edittxt_email.getText().toString().trim();
            // mobile = edittxt_phonenumber.getText().toString().trim();
            pass = edittxt_password.getText().toString().trim();
            age = edittxt_age.getText().toString().trim();
            boolean isemail = isValidMail(email);
            if (isemail == false) {
                edittxt_email.setError(getString(R.string.invalidemail));
                edittxt_email.requestFocus();
            } else {
                email = edittxt_email.getText().toString().trim();

                if (edittxt_phonenumber.getText().toString().trim().equalsIgnoreCase("")) {
                    phoneLogin(LoginType.PHONE);
                } else {
                    //  openDialog();
                    ToastClass.showToast(this, "Please enter mobile number !");
                }
            }
        }
    }


    private void phoneLogin(LoginType loginType) {


        if (loginType == LoginType.PHONE) {
            final Intent intent = new Intent(this, AccountKitActivity.class);
            AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                    new AccountKitConfiguration.AccountKitConfigurationBuilder(
                            LoginType.PHONE,
                            AccountKitActivity.ResponseType.TOKEN); // Use token when 'Enable client Access Token Flow' is YES

            intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build());

            startActivityForResult(intent, REQUEST_CODE);
        }
    }


    //=========================================== onActivity result ==============================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError() != null) {
                Toast.makeText(this, "errror typr =" + result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } else if (result.wasCancelled()) {
                finish();
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                return;

            } else {
                if (result.getAccessToken() != null) {
                    // Toast.makeText(this, "Success" + result.getAccessToken().getAccountId(), Toast.LENGTH_SHORT).show();
                    Log.e("result.getAccessToken =", result.getAccessToken().getAccountId());
                    getAccount();
                } else {
                    Toast.makeText(this, "Success" + result.getAuthorizationCode().substring(0, 10), Toast.LENGTH_SHORT).show();
                    Log.e("getAuthorizationCode =", result.getAuthorizationCode());
                    Log.e("Substrng AuthorCode =", result.getAuthorizationCode().substring(0, 10));
                }

            }
        }
    }

    //=========================================== get account call ==============================

    public void getAccount() {
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                System.out.println("**********success aaya h **********");
                // Get Account Kit ID
                socoial_id = account.getId();

                // Get phone number
                phoneNumber = account.getPhoneNumber();
                // Get email
                String email = account.getEmail();
                Log.e("phone = ", "" + phoneNumber);
                if (phoneNumber != null) {
                    System.out.println("********* phone m aaya h *********");

                    //String phoneNumberString = phoneNumber.toString();
                    phone = phoneNumber.toString();
                    // remove all space
                    String phoneNo = phone.replaceAll("\\s", "");


                    if (phoneNo.length() != 0) {
                        if (phoneNo.length() == 12) {
                            phoneNumberString = phoneNo.substring(2);

                        } else if (phoneNo.length() == 13) {
                            phoneNumberString = phoneNo.substring(3);

                        } else if (phoneNo.length() == 14) {
                            phoneNumberString = phoneNo.substring(4);

                        } else if (phoneNo.length() == 15) {
                            phoneNumberString = phoneNo.substring(5);
                        } else phoneNumberString = phoneNo;
                    }


                    if (isInternetPresent) {
                        String url = API.BASE_URL + "SignUp";
                        Log.e("SignUp url = ", "" + url);
                        CallSignupApi(url);
                    } else {
                        AlertConnection.showAlertDialog(Signup.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                    }

                    System.out.println("*********fb s mila phone*********" + phoneNumber);
                    Log.e("phoneNumberString = ", "" + phoneNumberString);
                }


                Log.e("Account data = ", "socoial id =" + socoial_id + "email = " + email);
            }

            @Override
            public void onError(final AccountKitError error) {

                System.out.println("**********aaya h **********");
                // Handle Error
                Log.e("AccountKit", error.toString());
            }
        });
    }

    //------------------------------------ signup call -----------------------------------

    private void Signup_call() {

        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface().SignUp(username, email, mobile, pass, gender, age, city, session.getTokenId(), isage, lat, lon, f_Uid);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("signup response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");

                        if (error.equals("true")) {

                            final JSONObject ressult = object.getJSONObject("data");

                            Toast.makeText(Signup.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), Home.class));
                            Animatoo.animateSlideUp(Signup.this);

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


    private void CallSignupApi(String url) {
        Utils.showDialog(this, "Loading Please Wait...");


        AndroidNetworking.post(url)
                .addBodyParameter("user_name", username)
                .addBodyParameter("user_email", email)
                .addBodyParameter("user_pass", pass)
                .addBodyParameter("mobile", phoneNumberString)
                .addBodyParameter("gender", gender)
                .addBodyParameter("age", age)
                .addBodyParameter("age_status", isage)
                .addBodyParameter("city", city)
                .addBodyParameter("lat", lat)
                .addBodyParameter("lng", lon)
                .addBodyParameter("f_Uid", f_Uid)
                .addBodyParameter("register_id", session.getTokenId())
                .setTag("usersignup")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("signup resp = ", "" + jsonObject);
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


                                FirebaseDatabase.getInstance().getReference().child(ARG_USER).child(user.user_id).setValue(user);

                                Intent intent = new Intent(Signup.this, Home.class);
                                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                                Animatoo.animateSlideUp(Signup.this);


                                //json obj parsing
                                /*Gson gson = new Gson();
                                Type listType = new TypeToken<List<UserInfoData>>() {
                                }.getType();

                                //json array
                                List<UserInfoData> data = gson.fromJson(jsonObject.getString("data"), listType);*/

                                //json object
                                //UserInfoData user = gson.fromJson(jsonObject.getJSONObject("data").toString(), UserInfoData.class);


                            } else {
                                ToastClass.showToast(Signup.this, msg);
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

    @Override
    protected void onResume() {
        super.onResume();
        city = session.getCityy();
        city_id = session.getCityId();

        if (city != null && !city.equalsIgnoreCase("")) {
            tv_city.setText(city);
        }


    }

    private void openDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.mobile_number_dialog);

        TextView btn_send = dialog.findViewById(R.id.btn_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneLogin(LoginType.PHONE);
            }
        });


        dialog.show();
    }
}
