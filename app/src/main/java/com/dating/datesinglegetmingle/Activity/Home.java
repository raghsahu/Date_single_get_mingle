package com.dating.datesinglegetmingle.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.dating.datesinglegetmingle.Bean.AlertConnection;
import com.dating.datesinglegetmingle.Bean.AppConfig;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Bean.GPSTracker;
import com.dating.datesinglegetmingle.Interface.API;
import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Pojo.NewAndOnlineList;
import com.dating.datesinglegetmingle.Pojo.NewAndOnlineResponse;
import com.dating.datesinglegetmingle.Pojo.UserInfoData;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.dating.datesinglegetmingle.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.dating.datesinglegetmingle.Const.ARG_CHATROOM;
import static com.dating.datesinglegetmingle.Const.ARG_HISTORY;
import static com.dating.datesinglegetmingle.Const.ARG_ONLINE;
import static com.dating.datesinglegetmingle.Const.ARG_USER;
import static com.dating.datesinglegetmingle.Constant.Constants.SHARED_PREF;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private RecyclerView common_rl;
    private List<String> home_list = new ArrayList<>();
    private LinearLayout noti_lay, likes_you_lay, comman_int_lay, ll_visit_profile,
            message_lay, new_and_online_lay, invite_friend_lay;
    private LinearLayout search_lay, my_account_lay;
    private String ldata = "", logid = "", other_user_id = "", status = "", count = "";
    private ProgressDialog progressDialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GPSTracker tracker;
    private double P_latitude = 0.0, P_longitude = 0.0;
    private CircleImageView nav_user_profile_image;
    private CircleImageView img_profile_home;
    private TextView nav_user_name_txt, nav_user_email_txt;
    private String username = "", email = "", image1 = "", city_id = "";
    private String interest_field_name = "";
    private String intentFilter;
    private String from_age, to_age, location;
    private String online_offline_status = "";
    private int super_count = 0;
    private NewAndOnlineResponse newAndOnlineResponse;
    private TextView tv_invite;
    private String register_id = "";
    private String f_Uid = "";
    private String otherUserRegisterId = "";
    private String urlCount = "";
    private String Likecount = "";
    private String count_like_you = "";
    private TextView tv_count_online;
    private TextView tv_count_like;
    private TextView tv_count_like_you, tv_visit_count;
    private String url_seen = "";
    private Session session;
    boolean doubleBackToExitPressedOnce = false;
    public static int ScreenPos = 0;
    NavigationView navigationView;
    private List<UserInfoData> mOnlineUser;

    Handler h = new Handler();
    Runnable runnable;
    int delay = 3 * 1000; //Delay for 15 seconds.  One second = 1000 milliseconds.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        progressDialog = new ProgressDialog(Home.this);
        Utils.hideSoftKeyboard(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);


        // Prompt the user to re-provide their sign-in credentials
        //FirebaseDatabase.getInstance().getReference().child(ARG_ONLINE)

        session = new Session(this);
        logid = session.getUser().user_id;
        Log.e("user_id home... = ", logid);
        getcurrentlatlon();
        CheckInternet();
        // GetFirebaseId();
        register_id = session.getTokenId();
        Log.e("firebase id home r", " " + register_id);

        Init();
        OnClick();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        View view = navigationView.getHeaderView(0);

        Menu menu = navigationView.getMenu();

        String title = menu.getItem(3).toString();
        SpannableString spanString = new SpannableString(title);
        spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#09dade")), 0, spanString.length(), 0); //fix the color to white
        menu.getItem(3).setTitle(spanString);


        nav_user_profile_image = view.findViewById(R.id.nav_user_profile_image);
        nav_user_name_txt = view.findViewById(R.id.nav_user_name_txt);
        nav_user_email_txt = view.findViewById(R.id.nav_user_email_txt);

        //-------------- get profile ------

        if (isInternetPresent) {
            GetProfile_call();
        } else {
            AlertConnection.showAlertDialog(Home.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
        }


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void openDialog() {

        new AlertDialog.Builder(Home.this)
                .setTitle("ALERT")
                .setMessage("First of fill complete profile(Common Interst field is mandatory)for better response!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Home.this, Account.class);
                        /*i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
                        startActivity(i);
                        Animatoo.animateFade(Home.this);

                    }
                })
                //.setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void openNotiDialog() {

        new AlertDialog.Builder(Home.this)
                .setTitle("ALERT")
                .setMessage("Message Notification")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Intent i = new Intent(Home.this, MessageConversation.class);
                    i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    Animatoo.animateFade(Home.this);

                })
                //.setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void Init() {
        mOnlineUser = new ArrayList<>();

        common_rl = findViewById(R.id.common_rl);
        tv_invite = findViewById(R.id.tv_invite);
        noti_lay = findViewById(R.id.noti_lay);
        likes_you_lay = findViewById(R.id.likes_you_lay);
        comman_int_lay = findViewById(R.id.comman_int_lay);
        ll_visit_profile = findViewById(R.id.ll_visit_profile);
        search_lay = findViewById(R.id.search_lay);
        my_account_lay = findViewById(R.id.my_account_lay);
        message_lay = findViewById(R.id.message_lay);
        new_and_online_lay = findViewById(R.id.new_and_online_lay);
        invite_friend_lay = findViewById(R.id.invite_friend_lay);
        tv_count_online = findViewById(R.id.tv_count_online);
        tv_count_like = findViewById(R.id.tv_count_like);
        tv_count_like_you = findViewById(R.id.tv_count_like_you);
        tv_visit_count = findViewById(R.id.tv_visit_count);
        img_profile_home = findViewById(R.id.img_profile_home);

        NewAndOnlineUser_call();


        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            try {
                String noti = intent.getStringExtra("NOTIFICATION");
                if (noti.equalsIgnoreCase("NOTIFICATION")) {
                    openNotiDialog();
                }
            } catch (Exception e) {

            }

        }

    }


    private void OnClick() {

        noti_lay.setOnClickListener(this);
        likes_you_lay.setOnClickListener(this);
        message_lay.setOnClickListener(this);
        comman_int_lay.setOnClickListener(this);
        ll_visit_profile.setOnClickListener(this);
        search_lay.setOnClickListener(this);
        my_account_lay.setOnClickListener(this);
        new_and_online_lay.setOnClickListener(this);
        invite_friend_lay.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.noti_lay:
                if (isInternetPresent) {
                    tv_count_like.setVisibility(View.GONE);
                    try {
                        url_seen = API.BASE_URL + "seen_notification";
                        Log.e("seen_notificationURL=", url_seen);
                        seen_notification(url_seen);
                    } catch (NullPointerException e) {
                        Log.e("error =", "" + e);
                        //ToastClass.showToast(this, getString(R.string.too_slow));
                    }
                } else {
                    ToastClass.showToast(this, getString(R.string.no_internal_connection));
                }
                startActivity(new Intent(getApplicationContext(), Notification.class));
                Animatoo.animateSlideUp(Home.this);
                break;
            case R.id.likes_you_lay:
                if (isInternetPresent) {
                    tv_count_like_you.setVisibility(View.GONE);
                    try {
                        url_seen = API.BASE_URL + "seen_notification";
                        Log.e("seen_notificationURL=", url_seen);
                        seen_notification(url_seen);
                    } catch (NullPointerException e) {
                        Log.e("error =", "" + e);
                        //ToastClass.showToast(this, getString(R.string.too_slow));
                    }
                } else {
                    ToastClass.showToast(this, getString(R.string.no_internal_connection));
                }
                startActivity(new Intent(getApplicationContext(), ActivityLikeYou.class));
                Animatoo.animateSlideUp(Home.this);
                break;

            case R.id.message_lay:
                startActivity(new Intent(getApplicationContext(), MessageConversation.class));
                Animatoo.animateSlideUp(Home.this);
                break;

            case R.id.comman_int_lay:
                startActivity(new Intent(getApplicationContext(), CommonInterest.class));
                Animatoo.animateSlideUp(Home.this);
                break;

            case R.id.search_lay:
                startActivity(new Intent(getApplicationContext(), Search.class));
                Animatoo.animateSlideUp(Home.this);
                break;

            case R.id.ll_visit_profile:
                if (isInternetPresent) {
                    tv_visit_count.setVisibility(View.GONE);
                    try {
                        url_seen = API.BASE_URL + "seen_notification";
                        Log.e("seen_notificationURL=", url_seen);
                        seen_notification(url_seen);
                    } catch (NullPointerException e) {
                        Log.e("error =", "" + e);
                        //ToastClass.showToast(this, getString(R.string.too_slow));
                    }
                } else {
                    ToastClass.showToast(this, getString(R.string.no_internal_connection));
                }

                startActivity(new Intent(getApplicationContext(), ActivityVisitProfile.class));
                Animatoo.animateSlideUp(Home.this);
                break;

            case R.id.my_account_lay:
                startActivity(new Intent(getApplicationContext(), Account.class));
                Animatoo.animateSlideUp(Home.this);
                break;

            case R.id.new_and_online_lay:
               /* if (isInternetPresent) {
                    tv_count_online.setVisibility(View.GONE);
                    try {
                        url_seen = API.BASE_URL + "seen_notification";
                        Log.e("seen_notificationURL=", url_seen);
                        seen_notification(url_seen);
                    } catch (NullPointerException e) {
                        Log.e("error =", "" + e);
                        //ToastClass.showToast(this, getString(R.string.too_slow));
                    }
                } else {
                    ToastClass.showToast(this, getString(R.string.no_internal_connection));
                }*/
                startActivity(new Intent(getApplicationContext(), NewAndOnlineUsers.class));
                Animatoo.animateSlideUp(Home.this);
                break;

            case R.id.invite_friend_lay:
                shareTextUrl();
                break;
        }
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
           /* lat = String.valueOf(P_latitude);
            lon = String.valueOf(P_longitude);*/
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        online_offline_status = "1";
        if (getIntent() != null && getIntent().hasExtra("SEARCH")) {
            from_age = getIntent().getStringExtra("from_age");
            to_age = getIntent().getStringExtra("to_age");
            location = getIntent().getStringExtra("search");
            city_id = getIntent().getStringExtra("city_id");
            Log.e("City_id", city_id);
            Log.e("from_age", from_age);
            Log.e("to_age", to_age);
            if (isInternetPresent) {
                HomeUserFilter_call();
            } else {
                AlertConnection.showAlertDialog(Home.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
            }

        } else {
            if (isInternetPresent) {
                HomeCommonInt_call();

            } else {
                AlertConnection.showAlertDialog(Home.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
            }

        }

        // OnlineOffline_call();

        Log.e("intent data = ", "intent data" + from_age + to_age + location + "1");

        //count..............
        h.postDelayed(runnable = new Runnable() {
            public void run() {

                /*urlCount = API.BASE_URL + "notification_count";
                Log.e("notification URL = ", urlCount);
                //getNotificationList(url);
                getcount(urlCount);*/


                if (isInternetPresent) {
                    try {
                        //url = API.BASE_URL + "Notification_List_By_UserId";
                        urlCount = API.BASE_URL + "Notification_List";
                        Log.e("notifiCount URL = ", urlCount);
                        getcount(urlCount);

                        String like_url = API.BASE_URL + "LikeYou";
                        getYouLikeApi(like_url);

                        // NewAndOnlineUser_call();


                        String url_visitor = API.BASE_URL + "VisitorList";
                        callVisitorApi(url_visitor);
                        Log.e("url_visitor  = ", url_visitor);


                    } catch (NullPointerException e) {
                        Log.e("error = ", "" + e);
                        //ToastClass.showToast(Home.this, getString(R.string.no_internal_connection));
                    }
                } else {
                    //ToastClass.showToast(Home.this, getString(R.string.no_internal_connection));
                }
                h.postDelayed(runnable, delay);
            }
        }, delay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        online_offline_status = "0";
        //OnlineOffline_call();
    }



    /*@Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
            Toast.makeText(this, "Please Press Back again to exit the app", Toast.LENGTH_SHORT).show();
           *//* new AlertDialog.Builder(Home.this)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to Exit?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Home.this.finish();

                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_logout)
                    .show();*//*
        }


    }*/

    @Override
    public void onBackPressed() {


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else /*if (ScreenPos == 0) {*/

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please Press Back again to exit the app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    } /*else {

            //Call home fragment
            //SelectOption(0);
            ScreenPos= 0;

        }*/


    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Date Single Get Mingle");
        //share.putExtra(Intent.EXTRA_TEXT, "Welcome to Date Single Get Mingle! You can download app from Play Store:- https://play.google.com/store/apps/details?id=com.logical.neibar&hl=en");
        share.putExtra(Intent.EXTRA_TEXT, "Welcome to Date Single Get Mingle! You can download app from Play Store:-  https://play.google.com/store/apps/details?id=com.dating.datesinglegetmingle");
        startActivity(Intent.createChooser(share, "Share link!"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_setting) {
            startActivity(new Intent(getApplicationContext(), Setting.class));
            Animatoo.animateSlideUp(Home.this);
            return true;
        }

        if (id == R.id.menu_terms) {
            startActivity(new Intent(getApplicationContext(), TermsAndCondition.class));
            Animatoo.animateSlideUp(Home.this);
            return true;
        }
        if (id == R.id.menu_faq) {
            startActivity(new Intent(getApplicationContext(), FAQ.class));
            Animatoo.animateSlideUp(Home.this);
            return true;
        }
        if (id == R.id.menu_support) {
            startActivity(new Intent(getApplicationContext(), Support.class));
            Animatoo.animateSlideUp(Home.this);
            return true;
        }
        if (id == R.id.menu_logout) {
            Logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            if (item == navigationView.getMenu().getItem(i)) {
                int currentPosition = i;
                Log.e("current pos 1= ", "" + currentPosition);
                if (currentPosition >= 0) {
                    //Call open Fragment
                    //SelectOption(currentPosition);
                    Log.e("item size 0>= ", "" + currentPosition);
                }
                break;
            }

        }

        Log.e("item size = ", "" + navigationView.getMenu().size());
        // Log.e("current pos 3= ","" + currentPosition);

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            ScreenPos = 0;
            //finish();
            startActivity(new Intent(getApplicationContext(), Home.class));
            Animatoo.animateSlideUp(Home.this);
            // Handle the camera action
        } else if (id == R.id.nav_common_int) {
            //ScreenPos = 1;
            startActivity(new Intent(getApplicationContext(), CommonInterest.class));
            Animatoo.animateSlideUp(Home.this);

        } else if (id == R.id.nav_chat_request) {
            // ScreenPos = 2;
            startActivity(new Intent(getApplicationContext(), ChatActivity1.class));
            Animatoo.animateSlideUp(Home.this);
        } else if (id == R.id.nav_new_online) {
            startActivity(new Intent(getApplicationContext(), NewAndOnlineUsers.class));
            Animatoo.animateSlideUp(Home.this);
        } else if (id == R.id.nav_online) {
            startActivity(new Intent(getApplicationContext(), NewAndOnlineUsers.class));
            Animatoo.animateSlideUp(Home.this);
        } else if (id == R.id.nav_my_conversation) {

            startActivity(new Intent(getApplicationContext(), MessageConversation.class));
            Animatoo.animateSlideUp(Home.this);
        } else if (id == R.id.nav_my_account) {

            startActivity(new Intent(getApplicationContext(), Account.class));
            Animatoo.animateSlideUp(Home.this);

        } else if (id == R.id.nav_search) {

            startActivity(new Intent(getApplicationContext(), Search.class));
            Animatoo.animateSlideUp(Home.this);

        } else if (id == R.id.nav_notification) {
            if (isInternetPresent) {
                try {
                    url_seen = API.BASE_URL + "seen_notification";
                    Log.e("seen_notificationURL=", url_seen);
                    seen_notification(url_seen);
                } catch (NullPointerException e) {
                    Log.e("error =", "" + e);
                    //ToastClass.showToast(this, getString(R.string.too_slow));
                }
            } else {
                ToastClass.showToast(this, getString(R.string.no_internal_connection));
            }

            startActivity(new Intent(getApplicationContext(), Notification.class));
            Animatoo.animateSlideUp(Home.this);

        } else if (id == R.id.nav_visit_profile) {

            startActivity(new Intent(getApplicationContext(), ActivityVisitProfile.class));
            Animatoo.animateSlideUp(Home.this);

        }/*  else if (id == R.id.nav_visitor) {

            startActivity(new Intent(getApplicationContext(), ActivityVisitor.class));
            Animatoo.animateSlideUp(Home.this);

        }*/ else if (id == R.id.nav_setting) {
            startActivity(new Intent(getApplicationContext(), Setting.class));
            Animatoo.animateSlideUp(Home.this);

        } else if (id == R.id.nav_faq) {

            startActivity(new Intent(getApplicationContext(), FAQ.class));
            Animatoo.animateSlideUp(Home.this);

        } else if (id == R.id.nav_terms) {

            startActivity(new Intent(getApplicationContext(), TermsAndCondition.class));
            Animatoo.animateSlideUp(Home.this);

        } else if (id == R.id.nav_support) {

            startActivity(new Intent(getApplicationContext(), Support.class));
            Animatoo.animateSlideUp(Home.this);

        } else if (id == R.id.nav_Logout) {
            Logout();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void seen_notification(String url_seen) {

        //Utils.showDialog(this, "Loading Please Wait...");
        AndroidNetworking.post(url_seen)
                .addBodyParameter("user_id", logid)
                .addBodyParameter("status", "1")
                .setTag("seen notification")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utils.dismissDialog();
                        Log.e("seen notification = ", "" + jsonObject);
                        try {
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {

                                Toast.makeText(Home.this, "Success", Toast.LENGTH_SHORT).show();
                                tv_count_like.setVisibility(View.GONE);
                            }
                            //addBadgeView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                        Log.e("error = ", "" + error);
                    }
                });

    }


    private void Logout() {

        new AlertDialog.Builder(Home.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        /*new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                try {
                                    FirebaseInstanceId.getInstance().deleteInstanceId();
                                    ToastClass.showToast(Home.this, "aya logout me...");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                // Call your Activity where you want to land after log out
                            }
                        }.execute();*/

                        try {
                            FirebaseMessaging.getInstance().setAutoInitEnabled(false);
                            FirebaseAuth.getInstance().signOut();
                            FirebaseDatabase.getInstance().getReference().child(ARG_ONLINE).child(logid).removeValue();
                            //FirebaseDatabase.getInstance().getReference().child(ARG_USER).child(logid).removeValue();
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                            FirebaseInstanceId.getInstance().deleteToken(register_id, "");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        //delete register id
                        try {
                            String url_logout = API.BASE_URL + "logout";
                            Log.e("logout url=", url_logout);
                            server_logout(url_logout);
                        } catch (NullPointerException e) {
                            Log.e("error =", "" + e);
                            //ToastClass.showToast(this, getString(R.string.too_slow));
                        }

                        deleteCache(Home.this);
                        //session.saveToken("");
                        session.logout();


                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void server_logout(String url_logout) {

        //Utils.showDialog(this, "Loading Please Wait...");
        AndroidNetworking.post(url_logout)
                .addBodyParameter("user_id", logid)
                .setTag("message")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //Utils.dismissDialog();
                        Log.e("logout resp = ", "" + jsonObject);
                        try {
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {

                                Toast.makeText(Home.this, message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {

                        Log.e("error = ", "" + error);
                    }
                });

    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    //------------------------- get profile ------------------

    private void GetProfile_call() {
        Call<ResponseBody> resultCall = AppConfig.loadInterface().Get_Profile(logid);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("get profile** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        if (error.equals("true")) {

                            JSONObject jsonObject = object.getJSONObject("data");
                            username = jsonObject.getString("user_name");
                            email = jsonObject.getString("user_email");
                            image1 = jsonObject.getString("image1");
                            interest_field_name = jsonObject.getString("interest_field_name");
                            Glide.with(Home.this).load(Config.Image_Url + image1).error(R.drawable.download).into(nav_user_profile_image);
                            Glide.with(Home.this).load(Config.Image_Url + image1).error(R.drawable.download).into(img_profile_home);
                            nav_user_name_txt.setText(username);
                            nav_user_email_txt.setText(email);

                            if (interest_field_name.equalsIgnoreCase("") /*|| image1.equalsIgnoreCase("")*/) {
                                openDialog();
                            }
                        } else {
                            String message = object.getString("msg");
                            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                            try {

                                FirebaseDatabase.getInstance().getReference().child(ARG_ONLINE).child(logid).removeValue();
                                // FirebaseDatabase.getInstance().getReference().child(ARG_USER).child(logid).removeValue();
                                FirebaseInstanceId.getInstance().deleteInstanceId();
                                FirebaseInstanceId.getInstance().deleteToken(register_id, "");
                                FirebaseAuth.getInstance().signOut();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            session.saveToken("");
                            session.logout();
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
                Toast.makeText(getApplicationContext(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void HomeUserFilter_call() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Log.e("param userid", logid);
        Log.e("param City_id", city_id);
        Log.e("param from_age", from_age);
        Log.e("param to_age", to_age);
        Call<ResponseBody> resultCall = AppConfig.loadInterface().UserFilter(logid, from_age, to_age, city_id, "1");
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("home_filter response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");


                        if (error.equals("true")) {
                            count = object.getString("count");
                            super_count = Integer.parseInt(count);
                            Gson gson = new Gson();
                            newAndOnlineResponse = gson.fromJson(responedata, NewAndOnlineResponse.class);
                            CommonInterestAdapter adaper = new CommonInterestAdapter(Home.this, newAndOnlineResponse.getData());
                            common_rl.setLayoutManager(new LinearLayoutManager(Home.this, LinearLayoutManager.VERTICAL, false));
                            common_rl.setAdapter(adaper);
                            adaper.notifyDataSetChanged();

                            Log.e("List1 =", "" + newAndOnlineResponse.getData().size());
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

    private void HomeCommonInt_call() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface().CommonInterestUser(logid);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("home_common response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        count = object.getString("count");
                        super_count = Integer.parseInt(count);
                        if (error.equals("true")) {
                            Gson gson = new Gson();
                            NewAndOnlineResponse newAndOnlineResponse = gson.fromJson(responedata, NewAndOnlineResponse.class);
                            CommonInterestAdapter adaper = new CommonInterestAdapter(Home.this, newAndOnlineResponse.getData());
                            common_rl.setLayoutManager(new LinearLayoutManager(Home.this, LinearLayoutManager.VERTICAL, false));
                            common_rl.setAdapter(adaper);
                            adaper.notifyDataSetChanged();
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

    //--------------------- Like,Unlike,SuperLike  ------------------

    private void LikeUnlikeSuperLike_call() {
        Log.e("fid home method ", " " + register_id);
        Call<ResponseBody> resultCall = AppConfig.loadInterface().userLikeUnlike(logid, other_user_id, status, register_id);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("like_unlike** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        String message = object.getString("msg");
                        if (error.equals("true")) {
                            if (status.equalsIgnoreCase("2")) {
                                Toast.makeText(getApplicationContext(), "Skip successfull...", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();

                        } else {
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
                Toast.makeText(getApplicationContext(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //--------------------- online offline api  ------------------

    private void OnlineOffline_call() {
        Call<ResponseBody> resultCall = AppConfig.loadInterface().updateOffOnline(logid, online_offline_status);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("offline_online** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("success");
                        String message = object.getString("msg");
                        if (error.equals("true")) {
                            //Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                        } else {
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
                Toast.makeText(getApplicationContext(), getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getcount(String urlCount) {
        //Utils.showDialog(this, "Loading Please Wait...");

        Log.e("user_id = ", logid);
        AndroidNetworking.post(urlCount)
                .addBodyParameter("user_id", logid)
                .setTag("status")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        //Utils.dismissDialog();
                        Log.e("notifi Count response =", jsonObject.toString());

                        try {
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            /*if (result.equalsIgnoreCase("true")) {
                                Likecount = jsonObject.getString("count_Vistors");
                                if (status_seen.equalsIgnoreCase("0")) {
                                    tv_count_like.setVisibility(View.VISIBLE);
                                    tv_count_like.setText(Likecount);
                                } else if (status_seen.equalsIgnoreCase("1")) {
                                    tv_count_like.setVisibility(View.GONE);
                                    tv_count_like.setText(Likecount);
                                }*/


                            if (result.equalsIgnoreCase("true")) {
                                String total_count = jsonObject.getString("total_count");
                                if (!total_count.equalsIgnoreCase("0")) {
                                    tv_count_like.setVisibility(View.VISIBLE);
                                    tv_count_like.setText(total_count);
                                } else {
                                    tv_count_like.setVisibility(View.GONE);
                                    tv_count_like.setText(total_count);
                                }
                            } else {
                                tv_count_like.setVisibility(View.GONE);
                            }


                                /*if (result.equalsIgnoreCase("true")) {
                                Likecount = jsonObject.getString("like_count");
                                String status_seen = jsonObject.getString("status_seen");
                                if (status_seen.equalsIgnoreCase("0")) {
                                    tv_count_like.setVisibility(View.VISIBLE);
                                    tv_count_like.setText(Likecount);
                                } else if (status_seen.equalsIgnoreCase("1")) {
                                    tv_count_like.setVisibility(View.GONE);
                                    tv_count_like.setText(Likecount);
                                }*/

                                /*JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    status = job.getString("status");
                                    if (status.equalsIgnoreCase("0")){
                                        tv_count_like.setVisibility(View.VISIBLE);
                                        tv_count_like.setText(Likecount);
                                    }

                                }*/

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error getcount= ", String.valueOf(anError));

                    }
                });
    }

    private void getYouLikeApi(String like_url) {
        //Utils.showDialog(this, "Loading Please Wait...");

        AndroidNetworking.post(like_url)

                .addBodyParameter("user_id", logid)
                .setTag("status")
                .setPriority(Priority.MEDIUM)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        //Utils.dismissDialog();
                        Log.e("LIke you response = ", jsonObject.toString());

                        try {
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("success");
                            try {
                                if (result.equalsIgnoreCase("true")) {
                                    count_like_you = jsonObject.getString("count_like");
                                    // String status_seen = jsonObject.getString("status_seen");
                                    if (!count_like_you.equalsIgnoreCase("0")) {
                                        tv_count_like_you.setVisibility(View.VISIBLE);
                                        tv_count_like_you.setText(count_like_you);
                                    }
                                }

                            } catch (Exception e) {
                                Log.e("exception like", "" + e);
                            }

                            /*if (result.equalsIgnoreCase("true")) {
                                count_like_you = jsonObject.getString("count_like");
                               // String status_seen = jsonObject.getString("status_seen");
                                if (!count_like_you.equalsIgnoreCase("0")) {
                                    tv_count_like_you.setVisibility(View.VISIBLE);
                                    tv_count_like_you.setText(count_like_you);
                                }

                                *//*JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    status = job.getString("status");
                                    if (status.equalsIgnoreCase("0")){
                                        tv_count_like.setVisibility(View.VISIBLE);
                                        tv_count_like.setText(Likecount);
                                    }

                                }*//*
                            } else {
                                tv_count_like.setVisibility(View.GONE);
                                Utils.openAlertDialog(Home.this, message);
                            }*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error getYouLikeApi= ", String.valueOf(anError));

                    }
                });
    }

    private void callVisitorApi(String url) {
        //Utils.showDialog(this, "Loading Please Wait...");

        AndroidNetworking.post(url)
                .addBodyParameter("user_id", logid)
                .setTag("status")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        // Utils.dismissDialog();
                        Log.e("notification response =", jsonObject.toString());

                        try {
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                String count_Vistors = jsonObject.getString("count_Vistors");
                                if (!count_Vistors.equalsIgnoreCase("0")) {
                                    tv_visit_count.setVisibility(View.VISIBLE);
                                    tv_visit_count.setText(count_Vistors);
                                } else {
                                    tv_visit_count.setVisibility(View.GONE);
                                    tv_visit_count.setText(count_Vistors);
                                }
                            } else {
                                tv_visit_count.setVisibility(View.GONE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error visitor count = ", String.valueOf(anError));

                    }
                });

    }

    private void NewAndOnlineUser_call() {
        FirebaseDatabase.getInstance().getReference().child(ARG_ONLINE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    //delete user when is offline
                    //FirebaseDatabase.getInstance().getReference().child(ARG_ONLINE).child(session.getUser().user_id).removeValue();

                    Log.e("online_data", dataSnapshot.getChildren().toString());
                    mOnlineUser.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        UserInfoData uid = ds.getValue(UserInfoData.class);
                        Log.e("gender =", "" + session.getUser().gender);
                        if (uid != null) {
                            if (uid.isOnline && !uid.gender.equalsIgnoreCase(session.getUser().gender)) {
                                mOnlineUser.add(uid);
                            }
                            Log.e("mOnlineUser size = ", "" + mOnlineUser.size());
                            String count_online = String.valueOf(mOnlineUser.size());
                            if (!count_online.equalsIgnoreCase("0")) {
                                tv_count_online.setText(count_online);
                                tv_count_online.setVisibility(View.VISIBLE);
                            } else {
                                tv_count_online.setText(count_online);
                                tv_count_online.setVisibility(View.GONE);
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    Log.e("exception =", "" + e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*FirebaseDatabase.getInstance().getReference().child(ARG_ONLINE).orderByKey().equalTo(session.getUser().user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postsnapshot :dataSnapshot.getChildren()) {

                    String key = postsnapshot.getKey();
                    dataSnapshot.getRef().removeValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


    }

//------------------- common interest adapter -----------

    public class CommonInterestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<NewAndOnlineList> stringList;
        RecyclerView recyclerView;
        View view;
        String age_status = "";
        private NewAndOnlineList newAndOnlineList;

        public CommonInterestAdapter(Context context, List<NewAndOnlineList> stringList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.stringList = stringList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.home_adapter_row, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Log.e("Dattasize***", "" + stringList.size());
            final MyHolder myHolder = (MyHolder) holder;

            if (stringList.size() > 0) {

                Glide.with(context).load(Config.Image_Url + stringList.get(position).getImage1()).placeholder(R.drawable.download).into(myHolder.users_profile_image);
                myHolder.home_user_name_text.setText(stringList.get(position).getUserName());
                myHolder.home_user_location_txt.setText(stringList.get(position).getCity_name());

                if (stringList.get(position).getAgeStatus().equalsIgnoreCase("Show")) {
                    age_status = stringList.get(position).getAge();
                } else age_status = "";
                myHolder.home_user_age_txt.setText(age_status);

            }
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            RoundedImageView users_profile_image;
            TextView home_user_age_txt, home_user_name_text, home_user_location_txt;
            LinearLayout ll_private_msg, user_like_lay, user_unlike_lay, user_superlike_lay;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                ll_private_msg = itemView.findViewById(R.id.ll_private_msg);
                user_like_lay = itemView.findViewById(R.id.user_like_lay);
                users_profile_image = itemView.findViewById(R.id.users_profile_image);
                home_user_age_txt = itemView.findViewById(R.id.home_user_age_txt);
                home_user_name_text = itemView.findViewById(R.id.home_user_name_text);
                home_user_location_txt = itemView.findViewById(R.id.home_user_location_txt);
                user_unlike_lay = itemView.findViewById(R.id.user_unlike_lay);
                user_superlike_lay = itemView.findViewById(R.id.user_superlike_lay);


                ll_private_msg.setOnClickListener(this);
                users_profile_image.setOnClickListener(this);
                user_superlike_lay.setOnClickListener(this);
                user_unlike_lay.setOnClickListener(this);
                user_like_lay.setOnClickListener(this);
            }


            @Override
            public void onClick(View v) {
                int position;
                switch (v.getId()) {
                    case R.id.ll_private_msg:

                        position = getAdapterPosition();
                        other_user_id = stringList.get(position).getUserId();
                        status = "2";
                        if (isInternetPresent) {
                            LikeUnlikeSuperLike_call();
                        } else {
                            AlertConnection.showAlertDialog(Home.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                        }
                        stringList.remove(position);
                        notifyDataSetChanged();





                        /*Intent i = new Intent(Home.this, ChatActivity1.class);

                        i.putExtra("FUID", stringList.get(position).getUserId());
                        i.putExtra("FCMTOKEN", otherUserRegisterId);
                        i.putExtra("user_name", stringList.get(position).getUserName());
                        i.putExtra("IMAGE", stringList.get(position).getImage1());
                        startActivity(i);*/
                        //finish();
                        break;

                    case R.id.users_profile_image:
                        position = getAdapterPosition();
                        sendUserData(position);
                        break;

                    case R.id.user_superlike_lay:
                        position = getAdapterPosition();
                        newAndOnlineList = stringList.get(position);
                        other_user_id = stringList.get(position).getUserId();
                        status = "3";
                        try {
                            if (super_count >= 10) {
                                Log.e("count", String.valueOf(super_count));
                                Toast.makeText(Home.this, "you have crossed your superlike limit", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("count", String.valueOf(super_count));
                                super_count++;
                                if (isInternetPresent) {
                                    LikeUnlikeSuperLike_call();
                                } else {
                                    AlertConnection.showAlertDialog(Home.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                                }
                                stringList.remove(position);
                                notifyDataSetChanged();

                                //first message send for chat
                                /*Intent in = new Intent(context, ChatActivity1.class);
                                in.putExtra("PAIRE", "paire");
                                in.putExtra("FUID", newAndOnlineList.getUserId());
                                in.putExtra("FCMTOKEN", newAndOnlineList.getRegisterId());
                                in.putExtra("user_name", newAndOnlineList.getUserName());
                                in.putExtra("IMAGE", newAndOnlineList.getImage1());
                                context.startActivity(in);
                                ((Activity) context).finish();*/
                            }
                        } catch (Exception e) {
                            Log.e("Exception ", "" + e);
                        }

                        break;

                    case R.id.user_unlike_lay:
                        position = getAdapterPosition();
                        other_user_id = stringList.get(position).getUserId();
                        status = "2";
                        if (isInternetPresent) {
                            LikeUnlikeSuperLike_call();
                        } else {
                            AlertConnection.showAlertDialog(Home.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                        }
                        stringList.remove(position);
                        notifyDataSetChanged();
                        break;
                    case R.id.user_like_lay:
                        position = getAdapterPosition();
                        newAndOnlineList = stringList.get(position);
                        other_user_id = stringList.get(position).getUserId();
                        status = "1";
                        if (isInternetPresent) {
                            LikeUnlikeSuperLike_call();
                        } else {
                            AlertConnection.showAlertDialog(Home.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);

                        }
                        stringList.remove(position);
                        notifyDataSetChanged();

                        //first message send for chat
                        Intent in = new Intent(context, ChatActivity1.class);
                        in.putExtra("PAIRE", "paire");
                        in.putExtra("FUID", newAndOnlineList.getUserId());
                        in.putExtra("FCMTOKEN", newAndOnlineList.getRegisterId());
                        in.putExtra("user_name", newAndOnlineList.getUserName());
                        in.putExtra("IMAGE", newAndOnlineList.getImage1());
                        context.startActivity(in);
                        ((Activity) context).finish();

                        break;
                }
            }

            private void sendUserData(int position) {
                Intent intent = new Intent(context, UserDetail.class);
                intent.putExtra("ONLINE", "1");
                intent.putExtra("image1", stringList.get(position).getImage1());
                intent.putExtra("image2", stringList.get(position).getImage2());
                intent.putExtra("image3", stringList.get(position).getImage3());
                intent.putExtra("image4", stringList.get(position).getImage4());
                intent.putExtra("image5", stringList.get(position).getImage5());
                intent.putExtra("image6", stringList.get(position).getImage6());
                intent.putExtra("other_user_id", stringList.get(position).getUserId());
                intent.putExtra("username", stringList.get(position).getUserName());
                intent.putExtra("age", stringList.get(position).getAge());
                intent.putExtra("location", stringList.get(position).getCity_name());
                intent.putExtra("mobile", stringList.get(position).getUserMobile());
                intent.putExtra("education", stringList.get(position).getEducation());
                intent.putExtra("profession", stringList.get(position).getProfession());
                intent.putExtra("drinking", stringList.get(position).getDrinkingHabit());
                intent.putExtra("smoking", stringList.get(position).getSmoking());
                intent.putExtra("eating", stringList.get(position).getEatingHabit());
                intent.putExtra("about", stringList.get(position).getAboutSelf());
                intent.putExtra("interest_name", stringList.get(position).getInterest_name());
                intent.putExtra("Fuid", stringList.get(position).getF_Uid());
                intent.putExtra("FcmId", stringList.get(position).getRegisterId());
                intent.putExtra("count", String.valueOf(super_count));
                startActivity(intent);
                Animatoo.animateSlideUp(context);
            }
        }
    }

}