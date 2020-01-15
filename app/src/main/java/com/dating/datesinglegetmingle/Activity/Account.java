package com.dating.datesinglegetmingle.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.dating.datesinglegetmingle.Bean.LikeYouData;
import com.dating.datesinglegetmingle.Interface.API;
import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Interface.ViewPagerListener;
import com.dating.datesinglegetmingle.Pojo.InterestList;
import com.dating.datesinglegetmingle.Pojo.InterestResponse;
import com.dating.datesinglegetmingle.Pojo.UserInfoData;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.adapter.DetailsSliderAdapter;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.dating.datesinglegetmingle.utils.Utils;
import com.dating.datesinglegetmingle.utils.ZoomOutPageTransformer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.dating.datesinglegetmingle.Const.ARG_CHATROOM;
import static com.dating.datesinglegetmingle.Const.ARG_HISTORY;
import static com.dating.datesinglegetmingle.Const.ARG_ONLINE;
import static com.dating.datesinglegetmingle.Const.ARG_USER;

public class Account extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private RelativeLayout my_account_back_lay;
    private TextView common_text;
    private Button add_profile_pic_btn, update_profile_btn;
    private String ldata = "", user_id = "", int_ids = "", interest_name = "";
    private ProgressDialog progressDialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private RecyclerView interest_rl;
    private List<String> int_list;
    private List<String> int__name_list;
    private Button interest_save_btn;
    private EditText profile_about_self_edt;
    private TextView profile_username, profile_email, profile_password;
    private Spinner eduction_spn, profession_spn, drinking_habit_spn, smoking_spn, eating_habit;
    private String education = "", profession = "", drinking = "", smokeing = "", eating = "", about = "";
    private String username = "", city = "", email = "", pass = "", image1 = "", interest_id = "";
    private String image2 = "", image3 = "", image4 = "", image5 = "", image6 = "", int_names = "";
    private TextView tv_city;
    private ImageView account_user_profile_img;
    private String city_id = "";

    ArrayList<String> detailsModals = new ArrayList<>();
    private DetailsSliderAdapter detailsSliderAdapter;
    private ImageView[] dotes;
    private int dotsCount;
    private LinearLayout details_linear_layout;
    private ViewPager viewPager;
    private Session session;
    private TextView tv_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        progressDialog = new ProgressDialog(Account.this);
        session = new Session(this);
        user_id = session.getUser().user_id;
        Log.e("user_id = ", user_id);

         /*Intent intent = getIntent();
       if (intent.getExtras() != null) {
            user_id = intent.getStringExtra("CHAT");
        }*/
        CheckInternet();
        Init();
        Onclick();
        Spinner_Function();


    }

    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void Init() {

        tv_delete = findViewById(R.id.tv_delete);
        my_account_back_lay = findViewById(R.id.my_account_back_lay);
        common_text = findViewById(R.id.common_text);
        add_profile_pic_btn = findViewById(R.id.add_profile_pic_btn);
        profile_username = findViewById(R.id.profile_username);
        profile_email = findViewById(R.id.profile_email);
        profile_password = findViewById(R.id.profile_password);
        eduction_spn = findViewById(R.id.eduction_spn);
        profession_spn = findViewById(R.id.profession_spn);
        drinking_habit_spn = findViewById(R.id.drinking_habit_spn);
        smoking_spn = findViewById(R.id.smoking_spn);
        eating_habit = findViewById(R.id.eating_habit);
        profile_about_self_edt = findViewById(R.id.profile_about_self_edt);
        update_profile_btn = findViewById(R.id.update_profile_btn);
        tv_city = findViewById(R.id.tv_city);
        account_user_profile_img = findViewById(R.id.account_user_profile_img);
        viewPager = findViewById(R.id.slider_pager);
        details_linear_layout = findViewById(R.id.details_linear_layout);

        //-------------- get profile ------

        if (isInternetPresent) {
            GetProfile_call();
        } else {
            AlertConnection.showAlertDialog(Account.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
        }
    }


    private void Onclick() {

        tv_delete.setOnClickListener(v -> {

            openDialog();

        });
        tv_city.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ActivitySelectCity.class);
            intent.putExtra("SEARCH", "3");
            startActivity(intent);
            Animatoo.animateSlideUp(Account.this);
        });

        my_account_back_lay.setOnClickListener(v -> {
            Intent intent = new Intent(Account.this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        add_profile_pic_btn.setOnClickListener(v -> {

            Intent intent = new Intent(getApplicationContext(), AddProfilePictures.class);
            intent.putExtra("image1", image1);
            intent.putExtra("image2", image2);
            intent.putExtra("image3", image3);
            intent.putExtra("image4", image4);
            intent.putExtra("image5", image5);
            intent.putExtra("image6", image6);
            startActivity(intent);
            Animatoo.animateSlideUp(Account.this);
        });

        common_text.setOnClickListener(v -> {

            int_list = new ArrayList<>();
            int__name_list = new ArrayList<>();
            final Dialog dialog = new Dialog(Account.this);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.common_interest_lay);
            interest_rl = dialog.findViewById(R.id.interest_rl);
            interest_save_btn = dialog.findViewById(R.id.interest_save_btn);
            Interest_call();

            interest_save_btn.setOnClickListener(v1 -> {

                if (int_list != null && int_list.isEmpty()) {
                    Toast.makeText(Account.this, "Please select any Interest", Toast.LENGTH_SHORT).show();
                } else {
                    int_ids = int_list.toString().substring(1, int_list.toString().length() - 1).replace(", ", ",");
                    int_names = int__name_list.toString().substring(1, int__name_list.toString().length() - 1).replace(", ", ",");
                    common_text.setText(int_names);
                    dialog.cancel();

                }
            });

            dialog.show();
        });

        update_profile_btn.setOnClickListener(v -> {

            if (profile_username.getText().toString().equalsIgnoreCase("")) {
                profile_username.setError(getString(R.string.can_not_be_empty));
                profile_username.requestFocus();
            } else if (tv_city.getText().toString().equalsIgnoreCase("")) {
                tv_city.setError(getString(R.string.can_not_be_empty));
                tv_city.requestFocus();
            } else if (int_ids != null && int_ids.equalsIgnoreCase("")) {
                Toast.makeText(Account.this, "Please select any Interest", Toast.LENGTH_SHORT).show();
            } /*else if (profile_about_self_edt.getText().toString().equalsIgnoreCase("")) {
                profile_about_self_edt.setError(getString(R.string.can_not_be_empty));
                profile_about_self_edt.requestFocus();
            } */ else {

                username = profile_username.getText().toString();
                about = profile_about_self_edt.getText().toString();
                city = tv_city.getText().toString();
                Log.e("ids", int_ids);

                if (isInternetPresent) {
                    UpdateProfile_call();
                } else {
                    AlertConnection.showAlertDialog(Account.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                }

            }
        });

    }

    private void openDialog() {
        new AlertDialog.Builder(Account.this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to Delete Account?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (isInternetPresent) {
                        // url = API.BASE_URL + "getNextUserLikes";
                        String url_delete = API.BASE_URL + "delete_User";
                        CallDeleteApi(url_delete);
                    } else {
                        AlertConnection.showAlertDialog(Account.this, getString(R.string.no_internal_connection), getString(R.string.donothaveinternet), false);
                    }

                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void CallDeleteApi(String url_delete) {
        Utils.showDialog(this, "Loading Please Wait...");

        Log.e("user_id= ", user_id);
        AndroidNetworking.post(url_delete)

                .addBodyParameter("user_id", user_id)
                .setTag("status")
                .setPriority(Priority.MEDIUM)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Utils.dismissDialog();
                        Log.e("Delete response = ", jsonObject.toString());

                        try {

                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");
                            if (result.equalsIgnoreCase("true")) {
                                ToastClass.showToast(Account.this, message);
                                /*Intent intent = new Intent(Account.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);



                                startActivity(intent);*/

                                /*FirebaseDatabase.getInstance().getReference().child(ARG_ONLINE).removeValue((databaseError, databaseReference) -> {
                                    if (databaseError == null) {
                                        arrayAdapter.remove(chatRoomToDelete);
                                    } else {
                                        // show databaseError to user
                                    }
                                });*/

                                session.logout();
                                FirebaseDatabase.getInstance().getReference().child(ARG_HISTORY).child(user_id).removeValue();
                                //FirebaseDatabase.getInstance().getReference().child(ARG_CHATROOM).child(user_id).removeValue();
                                FirebaseDatabase.getInstance().getReference().child(ARG_ONLINE).child(user_id).removeValue();
                                FirebaseDatabase.getInstance().getReference().child(ARG_USER).child(user_id).removeValue();
                                Intent intent = new Intent(Account.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            } else ToastClass.showToast(Account.this, message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("exception delete ac= ", "" + e);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error delete ac= ", String.valueOf(anError));

                    }
                });
    }

    private void Spinner_Function() {

        eduction_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                education = eduction_spn.getSelectedItem().toString();
                Log.e("eduction", education);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        profession_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                profession = profession_spn.getSelectedItem().toString();
                Log.e("profession", profession);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        drinking_habit_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                drinking = drinking_habit_spn.getSelectedItem().toString();
                Log.e("drinking", drinking);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        smoking_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                smokeing = smoking_spn.getSelectedItem().toString();
                Log.e("smokeing", smokeing);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        eating_habit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                eating = eating_habit.getSelectedItem().toString();
                Log.e("eating", eating
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    //------------------------------------ Interst_call -----------------------------------

    private void Interest_call() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface().Intrest_List(user_id);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("interst response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        if (error.equals("true")) {
                            Gson gson = new Gson();
                            InterestResponse interestResponse = gson.fromJson(responedata, InterestResponse.class);
                            InterestAdapter adaper = new InterestAdapter(Account.this, interestResponse.getData());
                            interest_rl.setLayoutManager(new GridLayoutManager(Account.this, 3));
                            interest_rl.setAdapter(adaper);
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

    public class InterestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<InterestList> interestLists;
        RecyclerView recyclerView;
        View view;

        public InterestAdapter(Context context, List<InterestList> interestLists) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.interestLists = interestLists;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.interest_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Log.e("Dattasize***", "" + interestLists.size());
            final MyHolder myHolder = (MyHolder) holder;

            if (interestLists.size() > 0) {
                myHolder.interest_txt.setText(interestLists.get(position).getInterestFieldName());

                if (interestLists.get(position).getStatus() != null && interestLists.get(position).getStatus().equalsIgnoreCase("1")) {
                    int_list.add(interestLists.get(position).getInterestId());
                    int__name_list.add(interestLists.get(position).getInterestFieldName());
                    myHolder.interest_txt.setTextColor(Color.parseColor("#09dade"));
                }

                Log.e("selected_list", int_list.toString());

                myHolder.interest_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (myHolder.interest_txt.getCurrentTextColor() == Color.parseColor("#09dade")) {

                            if (int_list != null && !int_list.isEmpty()) {
                                int_list.remove(interestLists.get(position).getInterestId());
                                int__name_list.remove(interestLists.get(position).getInterestFieldName());
                            }
                            myHolder.interest_txt.setTextColor(Color.parseColor("#000000"));

                        } else {
                            int_list.add(interestLists.get(position).getInterestId());
                            int__name_list.add(interestLists.get(position).getInterestFieldName());
                            myHolder.interest_txt.setTextColor(Color.parseColor("#09dade"));
                        }

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return interestLists.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            CardView interest_card;
            TextView interest_txt;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                interest_card = itemView.findViewById(R.id.interest_card);
                interest_txt = itemView.findViewById(R.id.interest_txt);

            }
        }
    }

    //------------------------- update profile ------------------

    private void UpdateProfile_call() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        Call<ResponseBody> resultCall = AppConfig.loadInterface().Update_Profile(user_id, username, city, education, profession, drinking, smokeing, eating, about, int_ids, int_names);
        resultCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {

                        String responedata = response.body().string();
                        Log.e("update_pro response** ", " " + responedata);
                        JSONObject object = new JSONObject(responedata);
                        String error = object.getString("result");
                        if (error.equals("true")) {
                            Toast.makeText(Account.this, "Profile Updated successfully", Toast.LENGTH_SHORT).show();

                            //json obj parsing
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<UserInfoData>>() {
                            }.getType();

                            //json array
                            //List<UserInfoData> data = gson.fromJson(jsonObject.getString("data"), listType);

                            //json object
                            UserInfoData user = gson.fromJson(object.getJSONObject("data").toString(), UserInfoData.class);
                            session.createSession(user);

                            Intent i = new Intent(Account.this, Home.class);
                            i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
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
                            email = jsonObject.getString("user_email");
                            pass = jsonObject.getString("user_pass");
                            city = jsonObject.getString("city_name");
                            image1 = jsonObject.getString("image1");
                            image2 = jsonObject.getString("image2");
                            image3 = jsonObject.getString("image3");
                            image4 = jsonObject.getString("image4");
                            image5 = jsonObject.getString("image5");
                            image6 = jsonObject.getString("image6");
                            education = jsonObject.getString("education");
                            profession = jsonObject.getString("profession");
                            drinking = jsonObject.getString("drinking_habit");
                            smokeing = jsonObject.getString("smoking");
                            eating = jsonObject.getString("eating_habit");
                            int_ids = jsonObject.getString("interest_id");
                            int_names = jsonObject.getString("interest_field_name");
                            about = jsonObject.getString("about_self");

                            Glide.with(Account.this).load(Config.Image_Url + image1).error(R.drawable.download).into(account_user_profile_img);

                            setImageVIewPager();
                            profile_email.setText(email);
                            profile_password.setText(pass);
                            profile_username.setText(username);
                            tv_city.setText(city);
                            common_text.setText(int_names);
                            profile_about_self_edt.setText(about);


                            String education_list[] = getResources().getStringArray(R.array.education_array);
                            for (int i = 0; i < education_list.length; i++) {
                                String edu = education_list[i];
                                if (edu != null && edu.equalsIgnoreCase(education)) {
                                    eduction_spn.setSelection(i);
                                    break;
                                }
                            }

                            String profession_list[] = getResources().getStringArray(R.array.profession_array);
                            for (int i = 0; i < profession_list.length; i++) {
                                String profesion = profession_list[i];
                                if (profesion != null && profesion.equalsIgnoreCase(profession)) {
                                    profession_spn.setSelection(i);
                                    break;
                                }
                            }

                            String drinking_list[] = getResources().getStringArray(R.array.drink_array);
                            for (int i = 0; i < drinking_list.length; i++) {
                                String driking = drinking_list[i];
                                if (driking != null && driking.equalsIgnoreCase(drinking)) {
                                    drinking_habit_spn.setSelection(i);
                                    break;
                                }
                            }

                            String smokeing_list[] = getResources().getStringArray(R.array.smoke_array);
                            for (int i = 0; i < smokeing_list.length; i++) {
                                String smoke = smokeing_list[i];
                                if (smoke != null && smoke.equalsIgnoreCase(smokeing)) {
                                    smoking_spn.setSelection(i);
                                    break;
                                }
                            }

                            String eating_list[] = getResources().getStringArray(R.array.eating_array);
                            for (int i = 0; i < eating_list.length; i++) {
                                String eat = eating_list[i];
                                if (eat != null && eat.equalsIgnoreCase(eating)) {
                                    eating_habit.setSelection(i);
                                    break;
                                }
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

    private void setImageVIewPager() {

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


            detailsSliderAdapter = new DetailsSliderAdapter(Account.this, detailsModals, new ViewPagerListener() {
                @Override
                public void getImagePosition(int position) {
                    Log.e("image listner = ", detailsModals.get(position));
                    //listDilog(this, detailsModals.get(position));
                }
            });

            viewPager.setAdapter(detailsSliderAdapter);
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            viewPager.setCurrentItem(0);
            if (detailsModals.size() > 0) {
                setUiPageViewController();
            }
            viewPager.setOnPageChangeListener(Account.this);
        }
    }

    /*...................................... set dot ..........................*/

    private void setUiPageViewController() {

        details_linear_layout.removeAllViews();
        dotsCount = detailsSliderAdapter.getCount();
        dotes = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dotes[i] = new ImageView(Account.this);
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


    @Override
    protected void onResume() {
        super.onResume();
        city = session.getCityy();
        city_id = session.getCityId();
        if (city != null && !city.equalsIgnoreCase("")) {
            tv_city.setText(city);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Account.this, Home.class);
        /*i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
