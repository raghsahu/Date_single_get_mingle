package com.dating.datesinglegetmingle.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.dating.datesinglegetmingle.Bean.AlertConnection;
import com.dating.datesinglegetmingle.Bean.AppConfig;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Bean.GPSTracker;
import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Pojo.NewAndOnlineResponse;
import com.dating.datesinglegetmingle.Pojo.UserInfoData;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.dating.datesinglegetmingle.Const.ARG_ONLINE;

public class NewAndOnlineUsers extends AppCompatActivity {

    private RelativeLayout new_online_back_lay;
    private RecyclerView news_online_rl;
    private ProgressDialog progressDialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GPSTracker tracker;
    double P_latitude = 0.0, P_longitude = 0.0;
    private String user_id = "", count = "";
    private SwipeRefreshLayout pullToRefresh;
    private int super_count = 0;
    private Session session;
    private List<UserInfoData> mOnlineUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_and_online_users);
        progressDialog = new ProgressDialog(NewAndOnlineUsers.this);
        mOnlineUser = new ArrayList<>();

        session = new Session(this);
        user_id = session.getUser().user_id;
        CheckInternet();
        Init();
        Onclick();
    }


    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void Init() {

        new_online_back_lay = findViewById(R.id.new_online_back_lay);
        news_online_rl = findViewById(R.id.news_online_rl);
        pullToRefresh = findViewById(R.id.pullToRefresh);
        NewOnlineAdapter adaper = new NewOnlineAdapter(NewAndOnlineUsers.this, mOnlineUser);
        news_online_rl.setLayoutManager(new LinearLayoutManager(NewAndOnlineUsers.this, LinearLayoutManager.VERTICAL, false));
        news_online_rl.setAdapter(adaper);
        try {
            FirebaseDatabase.getInstance().getReference().child(ARG_ONLINE).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mOnlineUser.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        UserInfoData uid = ds.getValue(UserInfoData.class);

                        if (uid != null) {
                            try {

                            } catch (Exception e) {
                                Log.e("Exception online", "" + e);
                            }
                            if (uid.isOnline && !uid.gender.equalsIgnoreCase(session.getUser().gender)) {
                                mOnlineUser.add(uid);

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Log.e("online home excep =", "" + e);
        }


        if (isInternetPresent) {
            //NewAndOnlineUser_call();
        } else {
            AlertConnection.showAlertDialog(NewAndOnlineUsers.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
        }
    }

    private void Onclick() {

        pullToRefresh.setOnRefreshListener(() -> {
            if (isInternetPresent) {
                //NewAndOnlineUser_call();
            } else {
                AlertConnection.showAlertDialog(NewAndOnlineUsers.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
            } // your code
            pullToRefresh.setRefreshing(false);
        });

        new_online_back_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewAndOnlineUsers.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(NewAndOnlineUsers.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void NewAndOnlineUser_call() {

        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface().Online_List_By_UserId(user_id);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("onlineuser response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        count = object.getString("count");
                        super_count = Integer.parseInt(count);
                        if (error.equals("true")) {
                            Gson gson = new Gson();
                            NewAndOnlineResponse newAndOnlineResponse = gson.fromJson(responedata, NewAndOnlineResponse.class);
                            //NewOnlineAdapter adaper = new NewOnlineAdapter(NewAndOnlineUsers.this, newAndOnlineResponse.getData());
                            news_online_rl.setLayoutManager(new LinearLayoutManager(NewAndOnlineUsers.this, LinearLayoutManager.VERTICAL, false));
                            //news_online_rl.setAdapter(adaper);
                            //adaper.notifyDataSetChanged();
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

    //------------------- common interest adapter -----------

    public class NewOnlineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<UserInfoData> newAndOnlineLists;
        RecyclerView recyclerView;
        View view;
        String age_status = "";

        public NewOnlineAdapter(Context context, List<UserInfoData> newAndOnlineLists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.newAndOnlineLists = newAndOnlineLists;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.new_online_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Log.e("Dattasize***", "" + newAndOnlineLists.size());
            final MyHolder myHolder = (MyHolder) holder;

            if (newAndOnlineLists.size() > 0) {

                Log.e("image1", newAndOnlineLists.get(position).image1);

                Glide.with(context).load(Config.Image_Url + newAndOnlineLists.get(position).image1).error(R.drawable.download).into(myHolder.online_image);
                myHolder.online_username_txt.setText(newAndOnlineLists.get(position).user_name);

                if (newAndOnlineLists.get(position).age_status.equalsIgnoreCase("Show")) {
                    age_status = newAndOnlineLists.get(position).age;
                } else age_status = "";
                myHolder.online_age_txt.setText(age_status);
                myHolder.tv_city.setText(newAndOnlineLists.get(position).city_name);
            }
        }

        @Override
        public int getItemCount() {
            return newAndOnlineLists.size();
        }

        class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            CircleImageView online_image;
            LinearLayout ll_profile_online;
            Button chat_now_btn;
            TextView online_username_txt, online_age_txt, tv_city;


            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                chat_now_btn = itemView.findViewById(R.id.chat_now_btn);
                online_image = itemView.findViewById(R.id.online_image);
                ll_profile_online = itemView.findViewById(R.id.ll_profile_online);
                online_username_txt = itemView.findViewById(R.id.online_username_txt);
                online_age_txt = itemView.findViewById(R.id.online_age_txt);
                tv_city = itemView.findViewById(R.id.tv_city);

                ll_profile_online.setOnClickListener(this);
                chat_now_btn.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position;
                switch (v.getId()) {
                    case R.id.ll_profile_online:
                        position = getAdapterPosition();
                        sendProfileData(position);
                        break;

                    case R.id.chat_now_btn:
                        position = getAdapterPosition();
                        String other_user_id = newAndOnlineLists.get(position).user_id;
                        String username = newAndOnlineLists.get(position).user_name;
                        String image1 = newAndOnlineLists.get(position).image1;
                        String otherUserRegisterId = newAndOnlineLists.get(position).register_id;

                        Intent i = new Intent(NewAndOnlineUsers.this, ChatActivity1.class);
                        i.putExtra("FUID", other_user_id);
                        i.putExtra("FCMTOKEN", otherUserRegisterId);
                        i.putExtra("user_name", username);
                        i.putExtra("IMAGE", image1);
                        i.putExtra("CHATBTN", "chatnow");
                        Animatoo.animateSlideUp(context);
                        startActivity(i);

                        break;
                }
            }

            private void sendProfileData(int position) {
                Intent intent = new Intent(context, UserDetail.class);
                intent.putExtra("ONLINE", "2");
                intent.putExtra("image1", newAndOnlineLists.get(position).image1);
                intent.putExtra("image2", newAndOnlineLists.get(position).image2);
                intent.putExtra("image3", newAndOnlineLists.get(position).image3);
                intent.putExtra("image4", newAndOnlineLists.get(position).image4);
                intent.putExtra("image5", newAndOnlineLists.get(position).image5);
                intent.putExtra("image6", newAndOnlineLists.get(position).image6);
                intent.putExtra("other_user_id", newAndOnlineLists.get(position).user_id);
                intent.putExtra("username", newAndOnlineLists.get(position).user_name);
                intent.putExtra("age", newAndOnlineLists.get(position).age);
                intent.putExtra("location", newAndOnlineLists.get(position).city_name);
                intent.putExtra("mobile", newAndOnlineLists.get(position).user_mobile);
                intent.putExtra("education", newAndOnlineLists.get(position).education);
                intent.putExtra("profession", newAndOnlineLists.get(position).profession);
                intent.putExtra("drinking", newAndOnlineLists.get(position).drinking_habit);
                intent.putExtra("smoking", newAndOnlineLists.get(position).smoking);
                intent.putExtra("eating", newAndOnlineLists.get(position).eating_habit);
                intent.putExtra("about", newAndOnlineLists.get(position).about_self);
                intent.putExtra("interest_name", newAndOnlineLists.get(position).interest_field_name);
                intent.putExtra("Fuid", newAndOnlineLists.get(position).f_Uid);
                intent.putExtra("FcmId", newAndOnlineLists.get(position).register_id);
                intent.putExtra("count", count);
                startActivity(intent);
                Animatoo.animateSlideUp(context);
            }
        }
    }
}
