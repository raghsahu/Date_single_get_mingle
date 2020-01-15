package com.dating.datesinglegetmingle.Activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Interface.API;
import com.dating.datesinglegetmingle.Pojo.NotificationData;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.adapter.NotificationAdapter;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.dating.datesinglegetmingle.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Notification extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RelativeLayout noti_back_lay;
    private ArrayList<NotificationData> notificationList;
    private NotificationAdapter mAdapter;
    private RecyclerView recycler_view;
    private RelativeLayout rl_no_record;
    private SwipeRefreshLayout swipeToRefresh;
    private ImageView iv_back;
    private String url = "";
    private String user_id = "";

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        session = new Session(this);
        user_id = session.getUser().user_id;
        CheckInternet();
        initView();
        clickLisner();

    }

    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void initView() {
        notificationList = new ArrayList<>();

        rl_no_record = findViewById(R.id.rl_no_record);
        recycler_view = findViewById(R.id.recycler_view);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        iv_back = findViewById(R.id.iv_back);

        //set adapter
        mAdapter = new NotificationAdapter(notificationList, this);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManger);
        recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(mAdapter);

        if (isInternetPresent) {
            //url = API.BASE_URL + "getMyNotification";
            url = API.BASE_URL + "Notification_List";
            callNotificationApi(url);
            Log.e("Notification url = ", url);
        } else {
            //AlertConnection.showAlertDialog(Notification.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
        }
    }

    private void clickLisner() {
        swipeToRefresh.setOnRefreshListener(this);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Notification.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void callNotificationApi(String url) {
        Utils.showDialog(this, "Loading Please Wait...");

        Log.e("user_id = ", user_id);
        AndroidNetworking.post(url)
                .addBodyParameter("user_id", user_id)
                .setTag("status")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        Utils.dismissDialog();
                        Log.e("notification response =", jsonObject.toString());

                        try {
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                String Likecount = jsonObject.getString("like_count");

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    NotificationData notiData = new NotificationData();
                                    notiData.notification_id = job.getString("notification_id");
                                    notiData.user_id = job.getString("user_id");
                                    notiData.user_name = job.getString("user_name");
                                    notiData.entity_type = job.getString("entity_type");
                                    notiData.entity = job.getString("entity");
                                    notiData.status = job.getString("status");
                                    notiData.date = job.getString("date");
                                    notiData.time = job.getString("time");

                                    notificationList.add(notiData);
                                }
                            } else {
                                ToastClass.showToast(Notification.this, message);
                                //  Utils.openAlertDialog(Notification.this, message);
                            }

                            //check arraylist size
                            if (notificationList.size() == 0) {
                                swipeToRefresh.setVisibility(View.GONE);
                                rl_no_record.setVisibility(View.VISIBLE);
                            } else {
                                swipeToRefresh.setVisibility(View.VISIBLE);
                                rl_no_record.setVisibility(View.GONE);
                            }

                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error = ", String.valueOf(anError));

                    }
                });

    }

    @Override
    public void onRefresh() {
        notificationList.clear();
        // stopping swipe refresh
        swipeToRefresh.setRefreshing(false);
        // callNotificationApi(url);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Notification.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
