package com.dating.datesinglegetmingle.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dating.datesinglegetmingle.Bean.AlertConnection;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Bean.GPSTracker;
import com.dating.datesinglegetmingle.Interface.API;
import com.dating.datesinglegetmingle.Pojo.CityData;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.adapter.SelectCityAdapter;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.dating.datesinglegetmingle.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.dating.datesinglegetmingle.Constant.Constants.SHARED_PREF;

public class ActivitySelectCity extends AppCompatActivity {
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String fid = "", lat = "", lon = "";
    double P_latitude = 0.0, P_longitude = 0.0;
    private GPSTracker tracker;
    private ArrayList<CityData> cityList = new ArrayList<>();
    private SelectCityAdapter mAdapter;
    private RecyclerView recyclerView;
    private EditText et_search;
    private String selectPage;
    private RelativeLayout select_city_back_lay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        fid = new Session(this).getTokenId();
        //GetFirebaseId();
        CheckInternet();
        getcurrentlatlon();
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("SEARCH").equalsIgnoreCase("1")) {
                selectPage = intent.getStringExtra("SEARCH");
            } else if (intent.getStringExtra("SEARCH").equalsIgnoreCase("2")) {
                selectPage = intent.getStringExtra("SEARCH");
            } else if (intent.getStringExtra("SEARCH").equalsIgnoreCase("3")) {
                selectPage = intent.getStringExtra("SEARCH");
            }
        }
        initView();
        //OnClick();
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

    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void initView() {

        select_city_back_lay = findViewById(R.id.select_city_back_lay);
        recyclerView = findViewById(R.id.recyclerView);
        et_search = findViewById(R.id.et_search);


        select_city_back_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //filter list item
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }

            private void filter(String s) {
                ArrayList<CityData> temp = new ArrayList();
                for (CityData cityData : cityList) {
                    //use .toLowerCase() for better matches
                    if (cityData.city_name.toLowerCase().startsWith(s.toLowerCase())) {
                        temp.add(cityData);
                    }
                }
                //update recyclerview
                mAdapter.updateList(temp);
            }
        });

        mAdapter = new SelectCityAdapter(cityList, this);
        RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        if (isInternetPresent) {
            String url_city = API.BASE_URL + "cityList";
            getCityApi(url_city);
            Log.e("url_city = ", url_city);

        } else {
            AlertConnection.showAlertDialog(ActivitySelectCity.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
        }
    }


    //--------------------------- city list ----------------------------------------
    private void getCityApi(String url) {
        Utils.showDialog(this, "Loading Please Wait...");

        AndroidNetworking.post(url)
                .addBodyParameter("search", "")
                .setTag("userInfo")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        Utils.dismissDialog();
                        Log.e("city response = ", jsonObject.toString());

                        try {
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");
                            //cityList.clear();

                            /*if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    CityData cityData = new CityData("");
                                    //notiData.id = job.getString("city_id");
                                    cityData.city_id = job.getString("city_id");
                                    cityData.city_name = job.getString("city_name");
                                    cityData.selectPage = selectPage;

                                    cityList.add(cityData);*/
                            if (result.equalsIgnoreCase("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject job = jsonArray.getJSONObject(i);
                                    CityData cityData = new CityData("");
                                    if (i == 0) {
                                        cityData.city_id = "";
                                        cityData.city_name = "All City";

                                    } else {
                                        //notiData.id = job.getString("city_id");
                                        cityData.city_id = job.getString("city_id");
                                        cityData.city_name = job.getString("city_name");

                                    }

                                    cityData.selectPage = selectPage;
                                    cityList.add(cityData);

                                }
                            } else {
                                ToastClass.showToast(ActivitySelectCity.this, message);
                            }
                            //set adapter
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
