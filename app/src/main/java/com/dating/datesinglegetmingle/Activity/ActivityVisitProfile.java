package com.dating.datesinglegetmingle.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Interface.API;
import com.dating.datesinglegetmingle.Pojo.VisitorData;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.adapter.VisitorAdapter;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.dating.datesinglegetmingle.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ActivityVisitProfile extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RelativeLayout add_credit_back_lay;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private VisitorAdapter mAdapter;
    private RecyclerView recycler_view;
    private RelativeLayout rl_no_record;
    private SwipeRefreshLayout swipeToRefresh;
    private String url = "";
    private String user_id = "";
    private ArrayList<VisitorData> visitorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_profile);
        Session session = new Session(this);
        user_id = session.getUser().user_id;
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            user_id = intent.getStringExtra("user_id");
        }
        CheckInternet();
        initView();
        clickListner();

    }

    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void initView() {
        visitorList = new ArrayList<>();

        add_credit_back_lay = findViewById(R.id.add_credit_back_lay);
        rl_no_record = findViewById(R.id.rl_no_record);
        recycler_view = findViewById(R.id.recycler_view);
        swipeToRefresh = findViewById(R.id.swipeToRefresh);


        //set adapter
        mAdapter = new VisitorAdapter(visitorList, this);
        //RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(this);
        RecyclerView.LayoutManager mLayoutManger = new GridLayoutManager(this, 2);
        recycler_view.setLayoutManager(mLayoutManger);
        // recycler_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler_view.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(mAdapter);


        if (isInternetPresent) {
            url = API.BASE_URL + "VisitorList";
            callVisitorApi(url);
            Log.e("VisitorList url = ", url);
        } else {
            //AlertConnection.showAlertDialog(Notification.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
        }

    }

    private void clickListner() {
        add_credit_back_lay.setOnClickListener(this);
        swipeToRefresh.setOnRefreshListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ActivityVisitProfile.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_credit_back_lay:
                Intent intent = new Intent(ActivityVisitProfile.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }


    private void callVisitorApi(String url) {
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
                        Log.e("VisitorList_response =", jsonObject.toString());

                        try {
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {

                                //JSONObject job = jsonObject.getJSONObject("data");
                                //json obj parsing
                                Gson gson = new Gson();
                                Type listType = new TypeToken<List<VisitorData>>() {
                                }.getType();

                                //json array
                                List<VisitorData> data = gson.fromJson(jsonObject.getString("data"), listType);

                                //json object
                                //VisitorData user = gson.fromJson(jsonObject.getJSONObject("data").toString(), VisitorData.class);
                                visitorList.addAll(data);

                            } else {
                                ToastClass.showToast(ActivityVisitProfile.this, message);

                                //  Utils.openAlertDialog(Notification.this, message);
                            }

                            //check arraylist size
                            if (visitorList.size() == 0) {
                                swipeToRefresh.setVisibility(View.GONE);
                                rl_no_record.setVisibility(View.VISIBLE);
                            } else {
                                swipeToRefresh.setVisibility(View.VISIBLE);
                                rl_no_record.setVisibility(View.GONE);
                            }

                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Utils.dismissDialog();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error = ", String.valueOf(anError));
                        Utils.dismissDialog();
                    }
                });

    }

    @Override
    public void onRefresh() {

        //visitorList.clear();
        swipeToRefresh.setRefreshing(false);
        //callVisitorApi(url);
    }


    //for grid item show on recyclerview
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        //for two row show item
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column  ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1)  ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column  ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1)  ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
