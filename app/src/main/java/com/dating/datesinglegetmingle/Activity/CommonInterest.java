package com.dating.datesinglegetmingle.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dating.datesinglegetmingle.Pojo.NewAndOnlineList;
import com.dating.datesinglegetmingle.Pojo.NewAndOnlineResponse;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.dating.datesinglegetmingle.Constant.Constants.SHARED_PREF;

public class CommonInterest extends AppCompatActivity {

    private RelativeLayout commoninterest_back_lay;
    private RecyclerView common_rl2;
    private List<String> common_list = new ArrayList<>();
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private GPSTracker tracker;
    double P_latitude = 0.0, P_longitude = 0.0;
    private String user_id = "", other_user_id = "", status = "", count = "";
    private ProgressDialog progressDialog;
    private int user_position;
    private int super_count = 0;
    private NewAndOnlineResponse newAndOnlineResponse;
    private TextView tv_invite;
    private String register_id;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_interest);
        progressDialog = new ProgressDialog(CommonInterest.this);
        session = new Session(this);
        user_id = session.getUser().user_id;
        register_id = session.getTokenId();
        CheckInternet();
        //GetFirebaseId();
        Init();
        Onclick();
    }

    private void Onclick() {

        commoninterest_back_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //finish();
                Intent intent = new Intent(CommonInterest.this, Home.class);
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


    private void GetFirebaseId() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(SHARED_PREF, 0);
        register_id = pref.getString("regId", "");
        Log.e("fire id commoninterest ", " " + register_id);
    }

    private void Init() {

        commoninterest_back_lay = findViewById(R.id.commoninterest_back_lay);
        common_rl2 = findViewById(R.id.common_rl2);
        tv_invite = findViewById(R.id.tv_invite);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInternetPresent) {
            CommonInterest_call();
        } else {
            AlertConnection.showAlertDialog(CommonInterest.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
        }
    }

    private void CommonInterest_call() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface().CommonInterestUser(user_id);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("common response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");

                        if (error.equals("true")) {
                            count = object.getString("count");
                            super_count = Integer.parseInt(count);
                            Gson gson = new Gson();
                            newAndOnlineResponse = gson.fromJson(responedata, NewAndOnlineResponse.class);
                            CommonInterestAdapter adaper = new CommonInterestAdapter(CommonInterest.this, newAndOnlineResponse.getData());
                            common_rl2.setLayoutManager(new LinearLayoutManager(CommonInterest.this, LinearLayoutManager.VERTICAL, false));
                            common_rl2.setAdapter(adaper);
                            adaper.notifyDataSetChanged();
                        } else {
                            String message = object.getString("msg");
                            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                        }

                        /*if (newAndOnlineResponse.getData().size() > 0) {
                            common_rl2.setVisibility(View.VISIBLE);
                            tv_invite.setVisibility(View.GONE);
                        } else {
                            common_rl2.setVisibility(View.GONE);
                            tv_invite.setVisibility(View.VISIBLE);
                        }*/

                        // Log.e("List2 =", "" + newAndOnlineResponse.getData().size());
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

    public class CommonInterestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<NewAndOnlineList> stringList;
        View view;
        String age_status = "";

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
            CardView fav_card;
            TextView home_user_age_txt, home_user_name_text, home_user_location_txt, fav_company_email;
            LinearLayout user_like_lay, user_unlike_lay, user_superlike_lay, ll_private_msg;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                ll_private_msg = itemView.findViewById(R.id.ll_private_msg);
                users_profile_image = itemView.findViewById(R.id.users_profile_image);
                home_user_age_txt = itemView.findViewById(R.id.home_user_age_txt);
                home_user_name_text = itemView.findViewById(R.id.home_user_name_text);
                home_user_location_txt = itemView.findViewById(R.id.home_user_location_txt);
                user_unlike_lay = itemView.findViewById(R.id.user_unlike_lay);
                user_superlike_lay = itemView.findViewById(R.id.user_superlike_lay);
                user_like_lay = itemView.findViewById(R.id.user_like_lay);


                ll_private_msg.setOnClickListener(this);
                user_superlike_lay.setOnClickListener(this);
                users_profile_image.setOnClickListener(this);
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
                            AlertConnection.showAlertDialog(CommonInterest.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                        }
                        stringList.remove(position);
                        notifyDataSetChanged();

                        /*position = getAdapterPosition();
                        String otherUserRegisterId = stringList.get(position).getRegisterId();

                        Intent i = new Intent(CommonInterest.this, ChatActivity1.class);
                        i.putExtra("FUID", stringList.get(position).getUserId());
                        i.putExtra("FCMTOKEN", otherUserRegisterId);
                        i.putExtra("user_name", stringList.get(position).getUserName());
                        i.putExtra("IMAGE", stringList.get(position).getImage1());
                        startActivity(i);
                        finish();*/
                        break;

                    case R.id.users_profile_image:
                        position = getAdapterPosition();
                        sendUserData(position);
                        break;

                    case R.id.user_superlike_lay:

                        position = getAdapterPosition();
                        other_user_id = stringList.get(position).getUserId();
                        status = "3";
                        if (super_count >= 5) {
                            Log.e("count", String.valueOf(super_count));
                            Toast.makeText(CommonInterest.this, "you have crossed your superlike limit", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("count", String.valueOf(super_count));
                            super_count++;
                            if (isInternetPresent) {
                                LikeUnlikeSuperLike_call();
                            } else {
                                AlertConnection.showAlertDialog(CommonInterest.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                            }
                            stringList.remove(position);
                            notifyDataSetChanged();
                        }
                        break;

                    case R.id.user_unlike_lay:
                        position = getAdapterPosition();
                        other_user_id = stringList.get(position).getUserId();
                        status = "2";
                        if (isInternetPresent) {
                            LikeUnlikeSuperLike_call();
                        } else {
                            AlertConnection.showAlertDialog(CommonInterest.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                        }
                        stringList.remove(position);
                        notifyDataSetChanged();
                        break;
                    case R.id.user_like_lay:
                        position = getAdapterPosition();
                        other_user_id = stringList.get(position).getUserId();
                        status = "1";
                        if (isInternetPresent) {
                            LikeUnlikeSuperLike_call();
                        } else {
                            AlertConnection.showAlertDialog(CommonInterest.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);

                        }
                        stringList.remove(position);
                        notifyDataSetChanged();

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
                //intent.putExtra("age", age_status);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CommonInterest.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
