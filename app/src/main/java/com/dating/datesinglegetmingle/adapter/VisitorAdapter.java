package com.dating.datesinglegetmingle.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.dating.datesinglegetmingle.Activity.ChatActivity1;
import com.dating.datesinglegetmingle.Activity.UserDetail;
import com.dating.datesinglegetmingle.Bean.AppConfig;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Pojo.VisitorData;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.dating.datesinglegetmingle.Constant.Constants.SHARED_PREF;

/**
 * Created by Ravindra Birla on 24/06/2019.
 */
public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.ViewHolder> {
    private ArrayList<VisitorData> visitorList;
    private Context context;
    private VisitorData visitorData;
    private String user_id;
    private String logid;
    private String status;
    private String contact_no;
    private ConnectionDetector cd;
    private Session session;
    private String user_name;
    private String user_image;
    private String other_user_name;
    private String other_image;


    //constructor
    public VisitorAdapter(ArrayList<VisitorData> visitorList, Context context) {
        this.visitorList = visitorList;
        this.context = context;
    }


    @NonNull
    @Override
    public VisitorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_visitor, parent, false);
        session = new Session(context);
        logid = session.getUser().user_id;
        user_name = session.getUser().user_name;
        user_image = session.getUser().image1;


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitorAdapter.ViewHolder holder, int position) {

        //set data from arraylist
        if (visitorList.size() > 0) {

            visitorData = visitorList.get(position);

            holder.tv_name.setText(visitorData.user_name);
            holder.tv_age.setText(visitorData.age);
            holder.tv_time.setText(visitorData.time);
            holder.tv_date.setText(visitorData.date);
            holder.tv_location.setText(visitorData.city_name);

            // holder.tv_send.setBackgroundResource(visitorData.isSelected() ? R.drawable.circle_purple_bg : R.drawable.circle_gray_bg);

            if (visitorData.image1 != null && !visitorData.image1.equalsIgnoreCase("")) {
                Log.e("image url  = ", visitorData.image1);
                Picasso.with(context)
                        .load(Config.Image_Url + visitorData.image1)
                        .placeholder(R.drawable.download)
                        .error(R.drawable.download)
                        .into(holder.users_profile_image);
            }

        } else ToastClass.showToast(context, "no record for list");
    }

    @Override
    public int getItemCount() {
        return visitorList.size();  //count array size
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name, tv_age, tv_location,tv_date,tv_time;
        ImageView users_profile_image;
        LinearLayout ll_item;
        String register_id;

        String otherUserRegisterId;
        LinearLayout ll_private_msg, user_superlike_lay, user_unlike_lay, user_like_lay;


        //initialization and click listner
        public ViewHolder(View v) {
            super(v);
            tv_name = v.findViewById(R.id.home_user_name_text);
            tv_age = v.findViewById(R.id.home_user_age_txt);
            tv_location = v.findViewById(R.id.home_user_location_txt);
            users_profile_image = v.findViewById(R.id.users_profile_image);
            ll_private_msg = v.findViewById(R.id.ll_private_msg);
            user_superlike_lay = v.findViewById(R.id.user_superlike_lay);
            user_unlike_lay = v.findViewById(R.id.user_unlike_lay);
            user_like_lay = v.findViewById(R.id.user_like_lay);
            tv_date = v.findViewById(R.id.tv_date);
            tv_time = v.findViewById(R.id.tv_time);

            //click listner
            users_profile_image.setOnClickListener(this);
            ll_private_msg.setOnClickListener(this);
            user_superlike_lay.setOnClickListener(this);
            user_unlike_lay.setOnClickListener(this);
            user_like_lay.setOnClickListener(this);

            register_id = new Session(context).getTokenId();
        }

        @Override
        public void onClick(View view) {
            int position;
            switch (view.getId()) {
                case R.id.users_profile_image:
                    position = getAdapterPosition();
                    visitorData = visitorList.get(position);
                    sendUserData(visitorData);
                    /*Intent intent = new Intent(context, UserDetail.class);
                    intent.putExtra("MODEL", visitorData);
                    context.startActivity(intent);*/
                    break;
                case R.id.ll_private_msg:
                    /*position = getAdapterPosition();
                    visitorData = visitorList.get(position);
                    otherUserRegisterId = visitorData.register_id;
                    status = "5";
                    Intent i = new Intent(context, ChatActivity1.class);

                    i.putExtra("FUID", visitorData.user_id);
                    i.putExtra("FCMTOKEN", otherUserRegisterId);
                    i.putExtra("user_name", visitorData.user_name);
                    i.putExtra("IMAGE", visitorData.image1);
                    context.startActivity(i);*/
                    //((Activity) context).finish();
                    break;

                case R.id.user_superlike_lay:

                    /*position = getAdapterPosition();
                    visitorData = visitorList.get(position);
                    user_id = visitorData.user_id;
                    other_user_name = visitorData.user_name;
                    other_image = visitorData.image1;
                    status = "3";
                    if (visitorData.super_count >= 10) {
                        Log.e("count", String.valueOf(visitorData.super_count));
                        Toast.makeText(context, "you have crossed your superlike limit", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("super_count", String.valueOf(visitorData.super_count));
                        visitorData.super_count++;
                        LikeUnlikeSuperLike_call();
                        visitorList.remove(position);
                        notifyDataSetChanged();
                        openDialog(visitorData);
                    }*/
                    break;

                case R.id.user_unlike_lay:
                   /* position = getAdapterPosition();
                    user_id = visitorData.user_id;
                    status = "2";
                    LikeUnlikeSuperLike_call();
                    visitorList.remove(position);
                    notifyDataSetChanged();*/
                    break;
                case R.id.user_like_lay:
                    /*position = getAdapterPosition();
                    visitorData = visitorList.get(position);
                    user_id = visitorData.user_id;
                    other_user_name = visitorData.user_name;
                    other_image = visitorData.image1;
                    status = "1";
                    LikeUnlikeSuperLike_call();
                    visitorList.remove(position);
                    notifyDataSetChanged();

                    openDialog(visitorData);*/

                    break;
            }
        }
        //--------------------- Like,Unlike,SuperLike  ------------------

        private void LikeUnlikeSuperLike_call() {

            Call<ResponseBody> resultCall = AppConfig.loadInterface().userLikeUnlike(logid, user_id, status, register_id);
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
                                Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, context.getString(R.string.server_problem), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendUserData(VisitorData visitorData) {
        Intent intent = new Intent(context, UserDetail.class);
        intent.putExtra("ONLINE", "1");
        intent.putExtra("image1", visitorData.image1);
        intent.putExtra("image2", visitorData.image2);
        intent.putExtra("image3", visitorData.image3);
        intent.putExtra("image4", visitorData.image4);
        intent.putExtra("image5", visitorData.image5);
        intent.putExtra("image6", visitorData.image6);
        intent.putExtra("other_user_id", visitorData.user_id);
        intent.putExtra("username", visitorData.user_name);
        //intent.putExtra("age", age_status);
        intent.putExtra("age", visitorData.age);
        intent.putExtra("location", visitorData.address);
        intent.putExtra("mobile", visitorData.user_mobile);
        intent.putExtra("education", visitorData.education);
        intent.putExtra("profession", visitorData.profession);
        intent.putExtra("drinking", visitorData.drinking_habit);
        intent.putExtra("smoking", visitorData.smoking);
        intent.putExtra("eating", visitorData.eating_habit);
        intent.putExtra("about", visitorData.about_self);
        intent.putExtra("interest_name", visitorData.interest_field_name);
        intent.putExtra("Fuid", visitorData.f_Uid);
        intent.putExtra("FcmId", visitorData.register_id);
        //intent.putExtra("count", String.valueOf(super_count));
        intent.putExtra("count", String.valueOf(0));
        context.startActivity(intent);
        Animatoo.animateSlideUp(context);
    }
}
