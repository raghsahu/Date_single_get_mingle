package com.dating.datesinglegetmingle.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.dating.datesinglegetmingle.Constant.Constants;
import com.dating.datesinglegetmingle.globle.Session;
import com.google.firebase.iid.FirebaseInstanceId;



/**
 * Created by Nitin on 14/12/2017.
 */

/*public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = String.valueOf(FirebaseInstanceId.getInstance().getInstanceId());

        // Saving reg id to shared preferences
        //storeRegIdInPref(refreshedToken);
        Session session  = new Session(this);
        session.saveToken(refreshedToken);

        Log.e("fcm_token", refreshedToken);
        Log.e(" session.getTokenId()",  "" + session.getTokenId());



        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);

    }

    *//*private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
        Log.e("fcm_token", token);
    }*//*
}*/

