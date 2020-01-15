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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.dating.datesinglegetmingle.Activity.ChatActivity1;
import com.dating.datesinglegetmingle.Activity.UserDetail;
import com.dating.datesinglegetmingle.Bean.AppConfig;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Bean.LikeYouData;
import com.dating.datesinglegetmingle.Interface.Config;
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
public class LikeYouAdapter extends RecyclerView.Adapter<LikeYouAdapter.ViewHolder> {
    private ArrayList<LikeYouData> likeList;
    private Context context;
    private LikeYouData likeData;
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
    public LikeYouAdapter(ArrayList<LikeYouData> likeList, Context context) {
        this.likeList = likeList;
        this.context = context;
    }


    @NonNull
    @Override
    public LikeYouAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_adapter_row, parent, false);
        session = new Session(context);
        logid = session.getUser().user_id;
        user_name = session.getUser().user_name;
        user_image = session.getUser().image1;


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeYouAdapter.ViewHolder holder, int position) {

        //set data from arraylist
        if (likeList.size() > 0) {

            likeData = likeList.get(position);

            holder.tv_name.setText(likeData.user_name);
            holder.tv_age.setText(likeData.age);
            holder.tv_location.setText(likeData.city_name);


            // holder.tv_send.setBackgroundResource(likeData.isSelected() ? R.drawable.circle_purple_bg : R.drawable.circle_gray_bg);

            if (likeData.image1 != null && !likeData.image1.equalsIgnoreCase("")) {
                Log.e("image url  = ", likeData.image1);
                Picasso.with(context)
                        .load(Config.Image_Url + likeData.image1)
                        .placeholder(R.drawable.download)
                        .error(R.drawable.download)
                        .into(holder.users_profile_image);
            }


        } else ToastClass.showToast(context, "no record for list");
    }

    @Override
    public int getItemCount() {
        return likeList.size();  //count array size
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name, tv_age, tv_location;
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

            //click listner
            users_profile_image.setOnClickListener(this);
            ll_private_msg.setOnClickListener(this);
            user_superlike_lay.setOnClickListener(this);
            user_unlike_lay.setOnClickListener(this);
            user_like_lay.setOnClickListener(this);


            register_id =  new Session(context).getTokenId();
        }


        @Override
        public void onClick(View view) {
            int position;
            switch (view.getId()) {
                case R.id.users_profile_image:
                    position = getAdapterPosition();
                    likeData = likeList.get(position);
                    sendUserData(likeData);
                    /*Intent intent = new Intent(context, UserDetail.class);
                    intent.putExtra("MODEL", likeData);
                    context.startActivity(intent);*/
                    break;
                case R.id.ll_private_msg:

                    position = getAdapterPosition();
                    user_id = likeData.user_id;
                    status = "2";
                    LikeUnlikeSuperLike_call();
                    likeList.remove(position);
                    notifyDataSetChanged();

                    /*position = getAdapterPosition();
                    likeData = likeList.get(position);
                    otherUserRegisterId = likeData.register_id;
                    status = "5";
                    Intent i = new Intent(context, ChatActivity1.class);

                    i.putExtra("FUID", likeData.user_id);
                    i.putExtra("FCMTOKEN", otherUserRegisterId);
                    i.putExtra("user_name", likeData.user_name);
                    i.putExtra("IMAGE", likeData.image1);
                    context.startActivity(i);*/
                    //((Activity) context).finish();
                    break;

                case R.id.user_superlike_lay:

                    position = getAdapterPosition();
                    likeData = likeList.get(position);
                    user_id = likeData.user_id;
                    other_user_name = likeData.user_name;
                    other_image = likeData.image1;
                    status = "3";
                    if (likeData.super_count >= 10) {
                        Log.e("count", String.valueOf(likeData.super_count));
                        Toast.makeText(context, "you have crossed your superlike limit", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("super_count", String.valueOf(likeData.super_count));
                        likeData.super_count++;
                        LikeUnlikeSuperLike_call();
                        likeList.remove(position);
                        notifyDataSetChanged();
                        openDialog(likeData);
                    }
                    break;

                case R.id.user_unlike_lay:
                    position = getAdapterPosition();
                    user_id = likeData.user_id;
                    status = "2";
                    LikeUnlikeSuperLike_call();
                    likeList.remove(position);
                    notifyDataSetChanged();
                    break;
                case R.id.user_like_lay:
                    position = getAdapterPosition();
                    likeData = likeList.get(position);
                    user_id = likeData.user_id;
                    other_user_name = likeData.user_name;
                    other_image = likeData.image1;
                    status = "1";
                    LikeUnlikeSuperLike_call();
                    likeList.remove(position);
                    notifyDataSetChanged();

                    openDialog(likeData);

                    break;

            }
        }

        private void openDialog(LikeYouData likeData) {
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setContentView(R.layout.dialog_paire);

            TextView tv_paired_name = dialog.findViewById(R.id.tv_paired_name);
            Button btn_paired_send_msg = dialog.findViewById(R.id.btn_paired_send_msg);
            Button btn_paired_keep_matching = dialog.findViewById(R.id.btn_paired_keep_matching);
            ImageView iv_paired_first = dialog.findViewById(R.id.iv_paired_first);
            ImageView iv_paired_second = dialog.findViewById(R.id.iv_paired_second);

            //set data
            tv_paired_name.setText(user_name + " & " + other_user_name);

            if (!user_image.equalsIgnoreCase("") && !user_image.equalsIgnoreCase("null")) {
            /*Glide.with(context).load(Config.Image_Url + user_image).placeholder(R.drawable.download)
                    .into(iv_paired_first);*/
                Picasso.with(context)
                        .load(Config.Image_Url + user_image)
                        .placeholder(R.drawable.download)
                        .error(R.drawable.download)
                        .into(iv_paired_first);
            }

            if (!other_image.equalsIgnoreCase("") && !other_image.equalsIgnoreCase("null")) {
            /*Glide.with(context).load(Config.Image_Url + other_image).placeholder(R.drawable.download)
                    .into(iv_paired_second);*/
                Picasso.with(context)
                        .load(Config.Image_Url + other_image)
                        .placeholder(R.drawable.download)
                        .error(R.drawable.download)
                        .into(iv_paired_second);
            }

            btn_paired_send_msg.setOnClickListener(v -> {
                /*position = getAdapterPosition();
                likeData = likeList.get(position);*/
                String otherUserRegisterId = likeData.register_id;
                Intent in = new Intent(context, ChatActivity1.class);

                in.putExtra("PAIRE", "paire");
                in.putExtra("FUID", likeData.user_id);
                in.putExtra("FCMTOKEN", otherUserRegisterId);
                in.putExtra("user_name", likeData.user_name);
                in.putExtra("IMAGE", likeData.image1);
                context.startActivity(in);
                ((Activity) context).finish();
            });
            btn_paired_keep_matching.setOnClickListener(v -> {

                String otherUserRegisterId = likeData.register_id;
                Intent in = new Intent(context, ChatActivity1.class);
                in.putExtra("PAIRE", "paire");
                in.putExtra("FUID", likeData.user_id);
                in.putExtra("FCMTOKEN", otherUserRegisterId);
                in.putExtra("user_name", likeData.user_name);
                in.putExtra("IMAGE", likeData.image1);
                context.startActivity(in);
                ((Activity) context).finish();
            });

            dialog.show();
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


    private void sendUserData(LikeYouData likeData) {
        Intent intent = new Intent(context, UserDetail.class);
        intent.putExtra("ONLINE", "1");
        intent.putExtra("image1", likeData.image1);
        intent.putExtra("image2", likeData.image2);
        intent.putExtra("image3", likeData.image3);
        intent.putExtra("image4", likeData.image4);
        intent.putExtra("image5", likeData.image5);
        intent.putExtra("image6", likeData.image6);
        intent.putExtra("other_user_id", likeData.user_id);
        intent.putExtra("username", likeData.user_name);
        //intent.putExtra("age", age_status);
        intent.putExtra("age", likeData.age);
        intent.putExtra("location", likeData.address);
        intent.putExtra("mobile", likeData.user_mobile);
        intent.putExtra("education", likeData.education);
        intent.putExtra("profession", likeData.profession);
        intent.putExtra("drinking", likeData.drinking_habit);
        intent.putExtra("smoking", likeData.smoking);
        intent.putExtra("eating", likeData.eating_habit);
        intent.putExtra("about", likeData.about_self);
        intent.putExtra("interest_name", likeData.interest_field_name);
        intent.putExtra("Fuid", likeData.f_Uid);
        intent.putExtra("FcmId", likeData.register_id);
        //intent.putExtra("count", String.valueOf(super_count));
        intent.putExtra("count", String.valueOf(0));
        context.startActivity(intent);
        Animatoo.animateSlideUp(context);
    }
}
