package com.dating.datesinglegetmingle.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dating.datesinglegetmingle.Bean.AlertConnection;
import com.dating.datesinglegetmingle.Bean.AppConfig;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Bean.DislikesListData;
import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Pojo.CityData;
import com.dating.datesinglegetmingle.Pojo.NewAndOnlineList;
import com.dating.datesinglegetmingle.Pojo.NewAndOnlineResponse;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.dating.datesinglegetmingle.Constant.Constants.SHARED_PREF;

public class SkippedProfile extends AppCompatActivity {

    private RelativeLayout unliked_profiles_lay;
    private ProgressDialog progressDialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String user_id = "", other_user_id = "", status = "";
    private RecyclerView dislike_rl;
    private String register_id;
    private Session session;
    private EditText et_search;
    private DislikeAdapter adaper;
    public List<DislikesListData> disLikeList;
    private RelativeLayout rl_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skipped_profile);
        progressDialog = new ProgressDialog(SkippedProfile.this);

        session = new Session(this);
        user_id = session.getUser().user_id;
        register_id = session.getTokenId();
        CheckInternet();
        //GetFirebaseId();
        Init();
        Onclick();

    }

    private void CheckInternet() {
        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void Init() {
        disLikeList = new ArrayList<>();

        rl_search = findViewById(R.id.rl_search);
        et_search = findViewById(R.id.et_search);
        unliked_profiles_lay = findViewById(R.id.unliked_profiles_lay);
        dislike_rl = findViewById(R.id.dislike_rl);

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
                ArrayList<DislikesListData> temp = new ArrayList();
                for (DislikesListData disLikeData : disLikeList) {
                    //use .toLowerCase() for better matches
                    if (disLikeData.user_name.toLowerCase().startsWith(s.toLowerCase())) {
                        temp.add(disLikeData);
                    }
                }
                //update recyclerview
                adaper.updateList(temp);
            }
        });


        if (isInternetPresent) {
            GetDislike_call();
        } else {
            AlertConnection.showAlertDialog(SkippedProfile.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
        }
    }

    private void Onclick() {

        unliked_profiles_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void GetDislike_call() {

        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface().getDisLikes(user_id);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("dislike response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("success");
                        if (error.equals("true")) {

                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<DislikesListData>>() {
                            }.getType();

                            //json array
                            List<DislikesListData> data = gson.fromJson(object.getString("data"), listType);

                            //json object
                            //UserInfoData user = gson.fromJson(jsonObject.getJSONObject("data").toString(), UserInfoData.class);

                            disLikeList.addAll(data);


                            //set adapter
                            adaper = new DislikeAdapter(SkippedProfile.this, disLikeList);
                            dislike_rl.setLayoutManager(new LinearLayoutManager(SkippedProfile.this, LinearLayoutManager.VERTICAL, false));
                            dislike_rl.setAdapter(adaper);
                            adaper.notifyDataSetChanged();

                            /*try {
                                JSONArray jsonArray = object.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject userdetail = jsonArray.getJSONObject(i);
                                    DislikesListData dislikeData = new DislikesListData();
                                    dislikeData.user_id = userdetail.getString("user_id");
                                    dislikeData.user_name = userdetail.getString("user_name");
                                    dislikeData.user_email = userdetail.getString("user_email");
                                    dislikeData.user_mobile = userdetail.getString("user_mobile");
                                    dislikeData.user_pass = userdetail.getString("user_pass");
                                    dislikeData.address = userdetail.getString("address");
                                    dislikeData.city = userdetail.getString("city");
                                    dislikeData.city_name = userdetail.getString("city_name");
                                    dislikeData.lat = userdetail.getString("lat");
                                    dislikeData.lng = userdetail.getString("lng");
                                    dislikeData.gender = userdetail.getString("gender");
                                    dislikeData.user_status = userdetail.getString("user_status");
                                    dislikeData.age = userdetail.getString("age");
                                    dislikeData.age_status = userdetail.getString("age_status");
                                    dislikeData.register_id = userdetail.getString("register_id");
                                    dislikeData.image1 = userdetail.getString("image1");
                                    dislikeData.image2 = userdetail.getString("image2");
                                    dislikeData.image3 = userdetail.getString("image3");
                                    dislikeData.image4 = userdetail.getString("image4");
                                    dislikeData.image5 = userdetail.getString("image5");
                                    dislikeData.image6 = userdetail.getString("image6");
                                    dislikeData.education = userdetail.getString("education");
                                    dislikeData.location = userdetail.getString("location");
                                    dislikeData.profession = userdetail.getString("profession");
                                    dislikeData.drinking_habit = userdetail.getString("drinking_habit");
                                    dislikeData.smoking = userdetail.getString("smoking");
                                    dislikeData.eating_habit = userdetail.getString("eating_habit");
                                    dislikeData.about_self = userdetail.getString("about_self");
                                    dislikeData.interest_id = userdetail.getString("interest_id");
                                    dislikeData.interest_field_name = userdetail.getString("interest_field_name");
                                    dislikeData.like_status = userdetail.getString("like_status");
                                    dislikeData.create_date_time = userdetail.getString("create_date_time");
                                    dislikeData.login_time = userdetail.getString("login_time");
                                    dislikeData.last_login = userdetail.getString("last_login");
                                    dislikeData.f_Uid = userdetail.getString("f_Uid");

                                    disLikeList.add(dislikeData);

                                    //set adapter
                                    adaper = new DislikeAdapter(SkippedProfile.this, disLikeList);
                                    dislike_rl.setLayoutManager(new LinearLayoutManager(SkippedProfile.this, LinearLayoutManager.VERTICAL, false));
                                    dislike_rl.setAdapter(adaper);
                                    adaper.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                        } else {
                            String message = object.getString("msg");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }

                        if (disLikeList.size() > 0) {
                            rl_search.setVisibility(View.VISIBLE);
                        } else rl_search.setVisibility(View.GONE);

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

    //------------------- DislikeAdapter adapter -----------

    public class DislikeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<DislikesListData> disLikeList;
        RecyclerView recyclerView;
        View view;
        String age_status = "";


        public DislikeAdapter(Context context, List<DislikesListData> disLikeList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.disLikeList = disLikeList;

            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.dislike_user_list_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Log.e("Dattasize***", "" + disLikeList.size());
            final MyHolder myHolder = (MyHolder) holder;

            if (disLikeList.size() > 0) {

                Log.e("image1", disLikeList.get(position).image1);

                Glide.with(context).load(Config.Image_Url + disLikeList.get(position).image1).error(R.drawable.download).into(myHolder.dislike_user_profile_image);
                myHolder.dislike_user_name_txt.setText(disLikeList.get(position).user_name);

                if (disLikeList.get(position).age_status.equalsIgnoreCase("Show")) {
                    age_status = disLikeList.get(position).age;
                }
                myHolder.dislike_age_txt.setText(age_status + " " + disLikeList.get(position).city_name);
                myHolder.dislike_profession_txt.setText(disLikeList.get(position).profession);

                if (/*disLikeList.size() == 1 ||*/ disLikeList.size() == 0) {
                    rl_search.setVisibility(View.GONE);
                } else rl_search.setVisibility(View.VISIBLE);

                myHolder.reset_dislike_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        other_user_id = disLikeList.get(position).user_id;
                        if (isInternetPresent) {
                            ResetUnlikeUser_call();
                        } else {
                            AlertConnection.showAlertDialog(SkippedProfile.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                        }
                        disLikeList.remove(position);
                        notifyDataSetChanged();
                    }
                });

            } else {
                rl_search.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return disLikeList.size();
        }

        public void updateList(ArrayList<DislikesListData> temp) {
            disLikeList = temp;
            notifyDataSetChanged();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            CircleImageView dislike_user_profile_image;
            Button reset_dislike_btn;
            TextView dislike_user_name_txt, dislike_age_txt, dislike_profession_txt;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                reset_dislike_btn = itemView.findViewById(R.id.reset_dislike_btn);
                dislike_user_profile_image = itemView.findViewById(R.id.dislike_user_profile_image);
                dislike_user_name_txt = itemView.findViewById(R.id.dislike_user_name_txt);
                dislike_age_txt = itemView.findViewById(R.id.dislike_age_txt);
                dislike_profession_txt = itemView.findViewById(R.id.dislike_profession_txt);
            }
        }
    }

    //--------------------- reset unlike user  ------------------

    private void ResetUnlikeUser_call() {
        Call<ResponseBody> resultCall = AppConfig.loadInterface().userLikeUnlike(user_id, other_user_id, "0", register_id);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responedata = response.body().string();
                        Log.e("reset unlike** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        String message = object.getString("msg");
                        if (error.equals("true")) {
                            Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();
                            if (disLikeList.size() == 0) {
                                rl_search.setVisibility(View.GONE);
                            } else rl_search.setVisibility(View.VISIBLE);
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
}
