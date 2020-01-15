package com.dating.datesinglegetmingle.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dating.datesinglegetmingle.Bean.AlertConnection;
import com.dating.datesinglegetmingle.Bean.AppConfig;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Bean.LikeYouData;
import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Interface.ViewPagerListener;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.adapter.DetailsSliderAdapter;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.dating.datesinglegetmingle.utils.ZoomOutPageTransformer;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.dating.datesinglegetmingle.Constant.Constants.SHARED_PREF;

public class UserDetail extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private RelativeLayout user_detail_back_lay;
    private ProgressDialog progressDialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String user_id = "", username = "", age = "", mobile = "", education = "",
            profession = "", drinking = "", eating = "", about = "", smoke = "", image1 = "",
            other_user_id = "", location = "", interest_name = "", fuid = "", otherUserRegisterId = "", status = "";
    private TextView detail_user_name_txt, detail_user_name_txt2, detail_age_txt;
    private TextView detail_location_txt, detail_mobile_txt, detail_education_txt;
    private TextView detail_profession_txt, detail_drinking_txt, detail_smoke_txt;
    private TextView detail_eating_txt, detail_about_txt;
    private RoundedImageView detail_user_profile;
    private RecyclerView interest_rl;
    private RecyclerView other_user_rl;
    private String image2 = "", image3 = "", image4 = "", image5 = "", image6 = "";
    private List<String> strings;
    private LinearLayout detail_super_like_lay, detail_like_lay, detail_unlike_lay;
    private int super_count = 0;
    private String count = "";
    private LikeYouData likeYouData;
    private LinearLayout ll_private_msg;
    private TextView tv_msg;
    private ImageView img_online;

    private ViewPager viewPager;
    ArrayList<String> detailsModals = new ArrayList<>();
    private DetailsSliderAdapter detailsSliderAdapter;
    private ImageView[] dotes;
    private int dotsCount;
    private LinearLayout details_linear_layout,ll_like_su_un;
    private String register_id;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        session = new Session(this);
        user_id = session.getUser().user_id;
        register_id = session.getTokenId();
        CheckInternet();
        //GetFirebaseId();
        init();
        Onclick();
        strings = new ArrayList<>();
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            /*Log.e("intent model = ", "" + intent.getSerializableExtra("MODEL"));
            Log.e("intent model data = ", "" + likeYouData);
            likeYouData = (LikeYouData) intent.getSerializableExtra("MODEL");
            if (likeYouData != null) {
                likeYouData = (LikeYouData) intent.getSerializableExtra("MODEL");
            } else {
                //come from online now,home,likeyou activity
                getIntentData();
            }*/
            try {
                if (intent.getStringExtra("Chat_USER").equalsIgnoreCase("chat")) {
                    user_id = intent.getStringExtra("CHAT");
                    ll_private_msg.setVisibility(View.GONE);
                    ll_like_su_un.setVisibility(View.GONE);

                    //-------------- get profile ------

                    if (isInternetPresent) {
                        GetProfile_call();
                    } else {
                        AlertConnection.showAlertDialog(UserDetail.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                    }


                }
            } catch (Exception e) {
                getIntentData();
                Log.e("user dertail exception", "" + e);
            }


            ////////////////////// visit profile //////////////////////////////
            status = "4";
            if (isInternetPresent) {
                LikeUnlikeSuperLike_call();
            } else {
                AlertConnection.showAlertDialog(UserDetail.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
            }
////////////////////// visit profile //////////////////////////////
        }
    }


    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void GetFirebaseId() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
        register_id = pref.getString("regId", "");
        Log.e("firebase id userdetail ", " " + register_id);
    }


    private void init() {

        progressDialog = new ProgressDialog(UserDetail.this);
        ll_private_msg = findViewById(R.id.ll_private_msg);
        user_detail_back_lay = findViewById(R.id.user_detail_back_lay);
        detail_user_name_txt = findViewById(R.id.detail_user_name_txt);
        detail_user_name_txt2 = findViewById(R.id.detail_user_name_txt2);
        detail_age_txt = findViewById(R.id.detail_age_txt);
        detail_location_txt = findViewById(R.id.detail_location_txt);
        detail_mobile_txt = findViewById(R.id.detail_mobile_txt);
        detail_education_txt = findViewById(R.id.detail_education_txt);
        detail_profession_txt = findViewById(R.id.detail_profession_txt);
        detail_drinking_txt = findViewById(R.id.detail_drinking_txt);
        detail_smoke_txt = findViewById(R.id.detail_smoke_txt);
        detail_eating_txt = findViewById(R.id.detail_eating_txt);
        detail_about_txt = findViewById(R.id.detail_about_txt);
        interest_rl = findViewById(R.id.interest_rl);
        other_user_rl = findViewById(R.id.other_user_rl);
        detail_super_like_lay = findViewById(R.id.detail_super_like_lay);
        detail_like_lay = findViewById(R.id.detail_like_lay);
        detail_unlike_lay = findViewById(R.id.detail_unlike_lay);
        img_online = findViewById(R.id.img_online);
        tv_msg = findViewById(R.id.tv_msg);
        ll_like_su_un = findViewById(R.id.ll_like_su_un);

        viewPager = findViewById(R.id.slider_pager);
        details_linear_layout = findViewById(R.id.details_linear_layout);


    }

    private void getIntentData() {

        Intent intent = getIntent();

        if (intent.getStringExtra("ONLINE").equalsIgnoreCase("1")) {
            img_online.setVisibility(View.GONE);
            ll_private_msg.setBackgroundResource(R.drawable.btn_gredient);
            tv_msg.setText("Private Message");

        } else if (intent.getStringExtra("ONLINE").equalsIgnoreCase("2")) {
            img_online.setVisibility(View.VISIBLE);
            ll_private_msg.setBackgroundResource(R.drawable.btn_green);
            tv_msg.setText("Chat Now");
        }
        username = intent.getStringExtra("username");
        image1 = intent.getStringExtra("image1");
        image2 = intent.getStringExtra("image2");
        image3 = intent.getStringExtra("image3");
        image4 = intent.getStringExtra("image4");
        image5 = intent.getStringExtra("image5");
        image6 = intent.getStringExtra("image6");
        other_user_id = intent.getStringExtra("other_user_id");
        age = intent.getStringExtra("age");
        location = intent.getStringExtra("location");
        mobile = intent.getStringExtra("mobile");
        education = intent.getStringExtra("education");
        profession = intent.getStringExtra("profession");
        drinking = intent.getStringExtra("drinking");
        smoke = intent.getStringExtra("smoking");
        eating = intent.getStringExtra("eating");
        about = intent.getStringExtra("about");
        interest_name = intent.getStringExtra("interest_name");
        fuid = intent.getStringExtra("Fuid");
        otherUserRegisterId = intent.getStringExtra("FcmId");
        count = intent.getStringExtra("count");
        try {
            super_count = Integer.parseInt(count);
        } catch (NumberFormatException e) {
            super_count = 0;
        }

        Log.e("counttt", count);

        //Picasso.with(UserDetail.this).load(Config.Image_Url + image1).placeholder(R.drawable.download).into(detail_user_profile);
        //detail_user_name_txt.setText(username);
        detail_user_name_txt2.setText(username);
        detail_age_txt.setText(age + " Years");
        detail_location_txt.setText(location);
        detail_mobile_txt.setText(mobile);
        detail_education_txt.setText(education);
        detail_profession_txt.setText(profession);
        detail_drinking_txt.setText(drinking);
        detail_smoke_txt.setText(smoke);
        detail_eating_txt.setText(eating);
        detail_about_txt.setText(about);

        if (!interest_name.equalsIgnoreCase("")) {

            List<String> myList = new ArrayList<String>(Arrays.asList(interest_name.split(",")));
            Log.e("string2", myList.toString());
            InterestAdapter adaper = new InterestAdapter(UserDetail.this, myList);
            interest_rl.setLayoutManager(new GridLayoutManager(UserDetail.this, 3));
            interest_rl.setAdapter(adaper);
            adaper.notifyDataSetChanged();

        }

        if (detailsModals != null) {

            if (image1 != null && !image1.equalsIgnoreCase("")) {
                //strings.add(image1);
                detailsModals.add(image1);
            }

            if (image2 != null && !image2.equalsIgnoreCase("")) {
                //strings.add(image2);
                detailsModals.add(image2);
            }
            if (image3 != null && !image3.equalsIgnoreCase("")) {
                //strings.add(image3);
                detailsModals.add(image3);
            }
            if (image4 != null && !image4.equalsIgnoreCase("")) {
                //strings.add(image4);
                detailsModals.add(image4);
            }
            if (image5 != null && !image5.equalsIgnoreCase("")) {
                // strings.add(image5);
                detailsModals.add(image5);
            }
            if (image6 != null && !image6.equalsIgnoreCase("")) {
                //strings.add(image6);
                detailsModals.add(image6);
            }
            Log.e("araylist size img = ", "" + detailsModals.size());

            /*ImagesAdapter adaper = new ImagesAdapter(UserDetail.this, strings);
            other_user_rl.setLayoutManager(new LinearLayoutManager(UserDetail.this, LinearLayoutManager.HORIZONTAL, false));
            other_user_rl.setAdapter(adaper);
            adaper.notifyDataSetChanged();*/
            detailsSliderAdapter = new DetailsSliderAdapter(UserDetail.this, detailsModals, new ViewPagerListener() {
                @Override
                public void getImagePosition(int position) {
                    Log.e("image listner = ", detailsModals.get(position));
                    listDilog(this, detailsModals.get(position));
                }
            });

            viewPager.setAdapter(detailsSliderAdapter);
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            viewPager.setCurrentItem(0);
            if (detailsModals.size() > 0) {
                setUiPageViewController();
            }
            viewPager.setOnPageChangeListener(UserDetail.this);
        }
    }


    //------------------------- get profile ------------------

    private void GetProfile_call() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface().Get_Profile(user_id);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("get profile** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        if (error.equals("true")) {

                            JSONObject jsonObject = object.getJSONObject("data");
                            username = jsonObject.getString("user_name");
                            //email = jsonObject.getString("user_email");
                            // pass = jsonObject.getString("user_pass");
                            location = jsonObject.getString("city_name");
                            image1 = jsonObject.getString("image1");
                            image2 = jsonObject.getString("image2");
                            image3 = jsonObject.getString("image3");
                            image4 = jsonObject.getString("image4");
                            image5 = jsonObject.getString("image5");
                            image6 = jsonObject.getString("image6");
                            education = jsonObject.getString("education");
                            profession = jsonObject.getString("profession");
                            drinking = jsonObject.getString("drinking_habit");
                            smoke = jsonObject.getString("smoking");
                            eating = jsonObject.getString("eating_habit");
                            //int_ids = jsonObject.getString("interest_id");
                            interest_name = jsonObject.getString("interest_field_name");
                            about = jsonObject.getString("about_self");

                            //Glide.with(Account.this).load(Config.Image_Url + image1).error(R.drawable.download).into(account_user_profile_img);


                            detail_user_name_txt2.setText(username);
                            detail_age_txt.setText(age + " Years");
                            detail_location_txt.setText(location);
                            detail_mobile_txt.setText(mobile);
                            detail_education_txt.setText(education);
                            detail_profession_txt.setText(profession);
                            detail_drinking_txt.setText(drinking);
                            detail_smoke_txt.setText(smoke);
                            detail_eating_txt.setText(eating);
                            detail_about_txt.setText(about);

                            if (!interest_name.equalsIgnoreCase("")) {

                                List<String> myList = new ArrayList<String>(Arrays.asList(interest_name.split(",")));
                                Log.e("string2", myList.toString());
                                InterestAdapter adaper = new InterestAdapter(UserDetail.this, myList);
                                interest_rl.setLayoutManager(new GridLayoutManager(UserDetail.this, 3));
                                interest_rl.setAdapter(adaper);
                                adaper.notifyDataSetChanged();

                            }

                            if (detailsModals != null) {

                                if (image1 != null && !image1.equalsIgnoreCase("")) {
                                    //strings.add(image1);
                                    detailsModals.add(image1);
                                }

                                if (image2 != null && !image2.equalsIgnoreCase("")) {
                                    //strings.add(image2);
                                    detailsModals.add(image2);
                                }
                                if (image3 != null && !image3.equalsIgnoreCase("")) {
                                    //strings.add(image3);
                                    detailsModals.add(image3);
                                }
                                if (image4 != null && !image4.equalsIgnoreCase("")) {
                                    //strings.add(image4);
                                    detailsModals.add(image4);
                                }
                                if (image5 != null && !image5.equalsIgnoreCase("")) {
                                    // strings.add(image5);
                                    detailsModals.add(image5);
                                }
                                if (image6 != null && !image6.equalsIgnoreCase("")) {
                                    //strings.add(image6);
                                    detailsModals.add(image6);
                                }
                                Log.e("araylist size img = ", "" + detailsModals.size());

            /*ImagesAdapter adaper = new ImagesAdapter(UserDetail.this, strings);
            other_user_rl.setLayoutManager(new LinearLayoutManager(UserDetail.this, LinearLayoutManager.HORIZONTAL, false));
            other_user_rl.setAdapter(adaper);
            adaper.notifyDataSetChanged();*/
                                detailsSliderAdapter = new DetailsSliderAdapter(UserDetail.this, detailsModals, new ViewPagerListener() {
                                    @Override
                                    public void getImagePosition(int position) {
                                        Log.e("image listner = ", detailsModals.get(position));
                                        listDilog(this, detailsModals.get(position));
                                    }
                                });

                                viewPager.setAdapter(detailsSliderAdapter);
                                viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
                                viewPager.setCurrentItem(0);
                                if (detailsModals.size() > 0) {
                                    setUiPageViewController();
                                }
                                viewPager.setOnPageChangeListener(UserDetail.this);
                            }


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

////////////////////// visit profile api call //////////////////////////////

    /*private void VisitProfileApi(String url) {
        //Utils.showDialog(this, "Loading Please Wait...");

        Log.e("user_id= ", user_id + "to_user_id = " + other_user_id);
        AndroidNetworking.post(url)
                .addBodyParameter("to_user_id", other_user_id)
                .addBodyParameter("user_id", user_id)
                .setTag("status")
                .setPriority(Priority.MEDIUM)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        Utils.dismissDialog();
                        Log.e("VisitProfile respo= ", jsonObject.toString());

                        try {

                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");
                            //count_like = jsonObject.getString("count_like");
                            //super_count = Integer.parseInt(count);
                            //like_list.clear();
                            if (result.equalsIgnoreCase("true")) {
                                *//*JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject job = jsonArray.getJSONObject(i);
                                    LikeYouData likeData = new LikeYouData();
                                    likeData.notification_id = job.getString("notification_id");
                                    likeData.to_user_id = job.getString("to_user_id");
                                    likeData.user_id = job.getString("user_id");

                                    //like_list.add(likeData);
                                }*//*
                            } else {
                                ToastClass.showToast(UserDetail.this, message);
                                //Utils.openAlertDialog(ActivityScheduling.this, message);
                            }

                            //check arraylist size

                            //mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error = ", String.valueOf(anError));
                    }
                });
    }*/

////////////////////// visit profile api call end //////////////////////////////

    /*........................listDilog()...................................*/
    public void listDilog(ViewPagerListener context, String imageView) {
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.view_full_image_dilog);

        ImageView detail_full_image = dialog.findViewById(R.id.detail_full_image);
        ImageView iv_cross = dialog.findViewById(R.id.iv_cross);


        iv_cross.setOnClickListener(v -> dialog.dismiss());

        if (detailsModals.size() > 0) {
            iv_cross.setVisibility(View.VISIBLE);

            if (imageView != null) {
                Picasso.with(this).load(Config.Image_Url + imageView).fit().centerCrop()
                        .placeholder(R.drawable.download)
                        .error(R.drawable.download)
                        .into(detail_full_image);
            }
        }
        dialog.show();
    }

    /*...................................... set dot ..........................*/

    private void setUiPageViewController() {

        details_linear_layout.removeAllViews();
        dotsCount = detailsSliderAdapter.getCount();
        dotes = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dotes[i] = new ImageView(UserDetail.this);
            dotes[i].setImageResource(R.drawable.circle_inactive);

            details_linear_layout.addView(dotes[i]);
        }
        if (dotes.length == 1) {
            details_linear_layout.setVisibility(View.GONE);
        }
        dotes[0].setImageResource(R.drawable.circle_active);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        for (int i = 0; i < dotsCount; i++) {
            dotes[i].setImageResource(R.drawable.circle_inactive);
        }
        dotes[position].setImageResource(R.drawable.circle_active);
        if (position + 1 == dotsCount) {
            //  btnFinish.setVisibility(View.VISIBLE);
        } else if (position == 0) {
        } else {
        }
        if (dotes.length == 1) {
        }
    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dotes[i].setImageResource(R.drawable.circle_inactive);
        }
        dotes[position].setImageResource(R.drawable.circle_active);
        if (position + 1 == dotsCount) {
            //  btnFinish.setVisibility(View.VISIBLE);
        } else if (position == 0) {
        } else {
        }
        if (dotes.length == 1) {
        }
    }

    /*.........................................................*/
    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private void Onclick() {

        ll_private_msg.setOnClickListener(this);
        user_detail_back_lay.setOnClickListener(this);
        detail_super_like_lay.setOnClickListener(this);
        detail_like_lay.setOnClickListener(this);
        detail_unlike_lay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_detail_back_lay:
                finish();
                break;

            case R.id.detail_super_like_lay:

                status = "3";
                if (super_count >= 5) {
                    Log.e("count", String.valueOf(super_count));
                    Toast.makeText(UserDetail.this, "you have crossed your superlike limit", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("count", String.valueOf(super_count));
                    super_count++;
                    if (isInternetPresent) {
                        LikeUnlikeSuperLike_call();
                    } else {
                        AlertConnection.showAlertDialog(UserDetail.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);

                    }
                }
                break;

            case R.id.detail_like_lay:
                status = "1";
                if (isInternetPresent) {
                    LikeUnlikeSuperLike_call();
                } else {
                    AlertConnection.showAlertDialog(UserDetail.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);

                }
                break;
            case R.id.detail_unlike_lay:
                status = "2";
                if (isInternetPresent) {
                    LikeUnlikeSuperLike_call();
                } else {
                    AlertConnection.showAlertDialog(UserDetail.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                }
                break;

            case R.id.ll_private_msg:
                /*Intent i = new Intent(UserDetail.this, ChatActivity1.class);
                startActivity(i);*/

                Intent i = new Intent(getApplicationContext(), ChatActivity1.class);
                i.putExtra("FUID", other_user_id);
                i.putExtra("FCMTOKEN", otherUserRegisterId);
                i.putExtra("user_name", username);
                i.putExtra("IMAGE", image1);
                startActivity(i);
                finish();
                break;

        }
    }


    public class InterestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<String> interestLists;
        RecyclerView recyclerView;
        View view;

        public InterestAdapter(Context context, List<String> interestLists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.interestLists = interestLists;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.other_user_interest_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Log.e("Dattasize***", "" + interestLists.size());
            final MyHolder myHolder = (MyHolder) holder;

            if (interestLists.size() > 0) {
                myHolder.other_interest_txt.setText(String.valueOf(interestLists.get(position)));
            }
        }

        @Override
        public int getItemCount() {
            return interestLists.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView other_interest_txt;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                other_interest_txt = itemView.findViewById(R.id.other_interest_txt);
            }
        }
    }

    //------------------- ImagesAdapter -----------

   /* public class ImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<String> stringList;
        View view;

        public ImagesAdapter(Context context, List<String> stringList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.stringList = stringList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.other_user_profile_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Log.e("Dattasize***", "" + stringList.size());
            final MyHolder myHolder = (MyHolder) holder;

            if (stringList.size() > 0) {
                Glide.with(context).load(Config.Image_Url + stringList.get(position)).error(R.drawable.download).into(myHolder.detail_user_profile);
            }
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            RoundedImageView detail_user_profile;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                detail_user_profile = itemView.findViewById(R.id.detail_user_profile);
            }
        }

    }*/

    //--------------------- Like,Unlike,SuperLike  ------------------

    private void LikeUnlikeSuperLike_call() {
        Call<ResponseBody> resultCall = AppConfig.loadInterface().userLikeUnlike(user_id, other_user_id, status, register_id);
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
                            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                            if (!status.equalsIgnoreCase("4")) {
                                finish();
                            }

                        } else {
                            ToastClass.showLongToast(UserDetail.this, message,2000);

                            //Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
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
}
