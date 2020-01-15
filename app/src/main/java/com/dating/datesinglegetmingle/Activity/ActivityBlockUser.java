package com.dating.datesinglegetmingle.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dating.datesinglegetmingle.Bean.BlockUserData;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Interface.API;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.adapter.BlockUserAdapter;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.dating.datesinglegetmingle.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityBlockUser extends AppCompatActivity {
    private String user_id;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private BlockUserAdapter mAdapter;
    private RecyclerView recycler_view;
    private RelativeLayout rl_back;
    private String url;
    private ArrayList<BlockUserData> blockList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_user);
        Session session = new Session(this);
        user_id = session.getUser().user_id;

        CheckInternet();
        initView();
        clickListner();

    }

    private void clickListner() {
        rl_back.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        blockList = new ArrayList<>();

        rl_back = findViewById(R.id.rl_back);
        recycler_view = findViewById(R.id.recycler_view);

        //set adapter
        mAdapter = new BlockUserAdapter(blockList, this);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(mLayoutManger);
        recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(mAdapter);


        if (isInternetPresent) {
            url = API.BASE_URL + "getBlockList";
            getBlockUserApi(url);
            Log.e("VisitorList url = ", url);
        } else {
            //AlertConnection.showAlertDialog(Notification.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
        }
    }


    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void getBlockUserApi(String url) {
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
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    BlockUserData likeData = new BlockUserData();
                                    likeData.block_id = job.getString("block_id");
                                    likeData.block_status = job.getString("block_status");
                                    likeData.to_user_id = job.getString("to_user_id");
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
                                    blockList.add(likeData);
                                }
                            } else {
                                ToastClass.showToast(ActivityBlockUser.this, message);
                                //Utils.openAlertDialog(ActivityScheduling.this, message);
                            }

                            //check arraylist size

                            /*if (blockList.size() == 0) {
                                swipeToRefresh.setVisibility(View.GONE);
                                rl_no_record.setVisibility(View.VISIBLE);
                            } else {
                                swipeToRefresh.setVisibility(View.VISIBLE);
                                rl_no_record.setVisibility(View.GONE);
                            }*/

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
}
