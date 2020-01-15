package com.dating.datesinglegetmingle.globle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.dating.datesinglegetmingle.Activity.Login;
import com.dating.datesinglegetmingle.Pojo.UserInfoData;
import com.google.gson.Gson;

public class Session extends Object {

    private static final String TAG = Session.class.getSimpleName();
    private Context _context;
    private SharedPreferences mypref, mypref2;
    private SharedPreferences.Editor editor, editor2, editor3;
    private static final String PREF_NAME = "JamTime_pref";
    private static final String PREF_NAME2 = "JamTime_pref2";
    private static SharedPreferences sharedPreferences;
    private static final String IS_LOGGEDIN = "isLoggedIn";
    private static final String FAV = "fav";
    private static final String From_Age = "from_age";
    private static final String To_Age = "to_age";
    private static final String Cityy = "city";
    private static final String CityId = "city_id";
    private static final String Token_Id = "token";

    public Session(Context context) {
        this._context = context;
        mypref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor.apply();

        mypref2 = _context.getSharedPreferences(PREF_NAME2, Context.MODE_PRIVATE);
        editor2 = mypref2.edit();
        editor2.apply();
    }

    public void createSession(UserInfoData userInfoData) {
        Gson gson = new Gson();
        String json = gson.toJson(userInfoData);
        editor.putString("user", json);
        editor.putBoolean(IS_LOGGEDIN, true);
        editor.apply();
    }

    public UserInfoData getUser() {
        Gson gson = new Gson();
        String str = mypref.getString("user", "");
        if (str.isEmpty())
            return null;
        return gson.fromJson(str, UserInfoData.class);
    }


    public void saveSearchage(String from_age, String to_age) {

        editor2.putString(From_Age, from_age);
        editor2.putString(To_Age, to_age);
        editor2.apply();
        editor2.commit();
    }

    public String getFrom_Age() {
        return mypref2.getString(From_Age, "");

    }

    public String getTo_Age() {
        return mypref2.getString(To_Age, "");

    }

    public void saveSearchCity(String city, String city_id) {

        editor2.putString(Cityy, city);
        editor2.putString(CityId, city_id);
        editor2.apply();
        editor2.commit();
    }


    public String getCityy() {
        return mypref2.getString(Cityy, "");
    }

    public String getCityId() {
        return mypref2.getString(CityId, "");
    }


    public void saveToken(String token) {

        editor2.putString(Token_Id, token);
        editor2.apply();
        editor2.commit();
    }

    public String getTokenId() {
        return mypref2.getString(Token_Id, "");
    }


    public void logout() {
        editor.clear();
        editor2.clear();


        editor.apply();
        editor2.apply();
        Intent showLogin = new Intent(_context, Login.class);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        showLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(showLogin);
    }

    public boolean isLoggedIn() {
        return mypref.getBoolean(IS_LOGGEDIN, false);
    }

    /*public void setPassword(String password) {
        editor2.putString("userpassword", password);
        this.editor2.apply();
    }

    public void setReminderState(boolean isset) {
        editor2.putBoolean("ReminderState", isset);
        this.editor2.apply();
    }


    public void setEmailPhone(String emailPhone) {
        editor2.putString("emailPhone", emailPhone);
        this.editor2.apply();
    }

    public String getEmailPhone() {
        return mypref2.getString("emailPhone", "");
    }

    public boolean getReminderState() {
        return mypref2.getBoolean("ReminderState", true);
    }


    public String getPassword() {
        return mypref2.getString("userPassword", "");
    }

    public void rememberMe(String user, String password) {
        editor2.putString("email", getEmailPhone());
        editor2.putString("pass", getPassword());
        editor2.apply();
    }*/


    /*public void rememberMe(String email, String password) {
        editor2.putString("rem_email", email);
        editor2.putString("rem_password", password);
        editor2.apply();
    }

    public String getRemEmail() {
        return mypref2.getString("rem_email", "");
    }

    public String getRemPassword() {
        return mypref2.getString("rem_password", "");
    }

    public void setPriceRangerValue(String product_name, String quantity, String minValue, String maxValue) {
        editor.putString("product_name", product_name);
        editor.putString("quantity", quantity);
        editor.putString("minPrice", minValue);
        editor.putString("maxPrice", maxValue);
        editor.apply();
    }

    public String getMinValue() {
        return mypref.getString("minPrice", "0");
    }

    public String getMaxValue() {
        return mypref.getString("maxPrice", "500");
    }

    public String getProductName() {
        return mypref.getString("product_name", "");
    }

    public String getProductQuantity() {
        return mypref.getString("quantity", "");
    }*/
}
