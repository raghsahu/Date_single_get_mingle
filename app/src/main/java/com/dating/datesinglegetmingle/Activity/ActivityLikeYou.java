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
import android.widget.RelativeLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dating.datesinglegetmingle.Bean.AlertConnection;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Bean.LikeYouData;
import com.dating.datesinglegetmingle.Interface.API;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.adapter.LikeYouAdapter;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.dating.datesinglegetmingle.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityLikeYou extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private RelativeLayout rl_back, rl_no_record;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeToRefresh;
    private ArrayList<LikeYouData> like_list;

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;

    private String user_id = "";
    private LikeYouAdapter mAdapter;
    private String url;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_you);
        session = new Session(this);
        user_id = session.getUser().user_id;
        Intent intent = getIntent();
        if (intent.getExtras() != null){
            user_id =   intent.getStringExtra("user_id");
        }
        CheckInternet();
        Init();
        clickListner();
    }

    private void clickListner() {
        swipeToRefresh.setOnRefreshListener(this);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                Intent intent = new Intent(ActivityLikeYou.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }


    private void Init() {
        like_list = new ArrayList<>();

        rl_back = findViewById(R.id.rl_back);
        recyclerView = findViewById(R.id.recyclerView);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);
        rl_no_record = findViewById(R.id.rl_no_record);
        if (isInternetPresent) {
            // url = API.BASE_URL + "getNextUserLikes";
            url = API.BASE_URL + "LikeYou";
            getYouLikeApi(url);
        } else {
            AlertConnection.showAlertDialog(ActivityLikeYou.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
        }


        //set adapter
        mAdapter = new LikeYouAdapter(like_list, this);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    private void getYouLikeApi(String url) {
        Utils.showDialog(this, "Loading Please Wait...");

        Log.e("user_id= ", user_id);
        AndroidNetworking.post(url)

                .addBodyParameter("user_id", user_id)
                .setTag("status")
                .setPriority(Priority.MEDIUM)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        Utils.dismissDialog();
                        Log.e("LIke you response = ", jsonObject.toString());

                        try {

                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("success");

                            //like_list.clear();
                            if (result.equalsIgnoreCase("true")) {
                                String count_like = jsonObject.getString("count_like");
                                int super_count = Integer.parseInt(count_like);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    LikeYouData likeData = new LikeYouData();
                                    likeData.like_id = job.getString("like_id");
                                    //likeData.to_user_id = job.getString("to_user_id");
                                    likeData.user_id = job.getString("user_id");
                                    likeData.date_time = job.getString("date_time");
                                    likeData.user_name = job.getString("user_name");
                                    likeData.user_email = job.getString("user_email");
                                    likeData.user_mobile = job.getString("user_mobile");
                                    likeData.user_pass = job.getString("user_pass");
                                    likeData.address = job.getString("address");
                                    likeData.city = job.getString("city");
                                    likeData.city_name = job.getString("city_name");
                                    likeData.lat = job.getString("lat");
                                    likeData.lng = job.getString("lng");
                                    likeData.gender = job.getString("gender");
                                    likeData.user_status = job.getString("user_status");
                                    likeData.age = job.getString("age");
                                    likeData.age_status = job.getString("age_status");
                                    likeData.register_id = job.getString("register_id");
                                    likeData.status = job.getString("status");
                                    likeData.image1 = job.getString("image1");
                                    likeData.image2 = job.getString("image2");
                                    likeData.image3 = job.getString("image3");
                                    likeData.image4 = job.getString("image4");
                                    likeData.image5 = job.getString("image5");
                                    likeData.image6 = job.getString("image6");
                                    likeData.education = job.getString("education");
                                    likeData.location = job.getString("location");
                                    likeData.profession = job.getString("profession");
                                    likeData.drinking_habit = job.getString("drinking_habit");
                                    likeData.smoking = job.getString("smoking");
                                    likeData.eating_habit = job.getString("eating_habit");
                                    likeData.about_self = job.getString("about_self");
                                    likeData.interest_id = job.getString("interest_id");
                                    likeData.interest_field_name = job.getString("interest_field_name");
                                    likeData.like_status = job.getString("like_status");
                                    likeData.create_date_time = job.getString("create_date_time");
                                    likeData.login_time = job.getString("login_time");
                                    likeData.last_login = job.getString("last_login");
                                    likeData.f_Uid = job.getString("f_Uid");
                                    likeData.super_count = super_count;
                                    like_list.add(likeData);
                                }
                            } else {
                                ToastClass.showToast(ActivityLikeYou.this, message);
                                //Utils.openAlertDialog(ActivityScheduling.this, message);
                            }

                            //check arraylist size

                            if (like_list.size() == 0) {
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
        like_list.clear();
        // stopping swipe refresh
        swipeToRefresh.setRefreshing(false);
        getYouLikeApi(url);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ActivityLikeYou.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
