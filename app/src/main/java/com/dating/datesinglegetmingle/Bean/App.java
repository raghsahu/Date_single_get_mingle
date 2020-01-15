package com.dating.datesinglegetmingle.Bean;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.dating.datesinglegetmingle.Pojo.UserInfoData;
import com.dating.datesinglegetmingle.globle.Session;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

import static com.dating.datesinglegetmingle.Const.ARG_ONLINE;


/**
 * Created by pintu22 on 15/9/17.
 */

public class App extends Application implements LifecycleObserver {
    private static App instance = null;

    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

    private static Session session;

    @Override
    public void onCreate() {
        super.onCreate();
        session = new Session(this);

        MultiDex.install(this);
        Fabric.with(this, new Crashlytics());
        setInstance(this);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    public static App getInstance() {
        return instance;
    }

    public void setInstance(App instance) {
        App.instance = instance;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected void onMoveToForeground() {
        if (session.isLoggedIn()) {
            /*Map<String,Object> status = new HashMap<>();
            status.put("isOnline",true);
            status.put("id",session.getUser().user_id);*/
            UserInfoData model = session.getUser();
            model.isOnline = true;
            FirebaseDatabase.getInstance().getReference().child(ARG_ONLINE).child(session.getUser().user_id).setValue(model);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected void onMoveToBackground() {
        if (session.isLoggedIn()) {
            /*Map<String,Object> status = new HashMap<>();
            status.put("isOnline",false);
            status.put("id",session.getUser().user_id);*/
            UserInfoData model = session.getUser();
            model.isOnline = false;
            FirebaseDatabase.getInstance().getReference().child(ARG_ONLINE).child(session.getUser().user_id).setValue(model);
        }
    }
}
