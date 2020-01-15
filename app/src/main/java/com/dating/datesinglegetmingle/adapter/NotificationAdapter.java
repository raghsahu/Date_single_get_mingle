package com.dating.datesinglegetmingle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dating.datesinglegetmingle.Pojo.NotificationData;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.utils.ToastClass;

import java.util.ArrayList;

/**
 * Created by Ravindra Birla on 08/07/2019.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private ArrayList<NotificationData> notificationList;
    private Context context;
    private NotificationData notificationData;


    //constructor
    public NotificationAdapter(ArrayList<NotificationData> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }


    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {


        //set data from arraylist
        if (notificationList.size() > 0) {

            notificationData = notificationList.get(position);

            holder.tv_name.setText(notificationData.user_name);
            holder.tv_message.setText(notificationData.entity);
            holder.tv_time.setText(notificationData.time);
            holder.tv_date.setText(notificationData.date);

            /*if (notificationData.image != null || !notificationData.image.equalsIgnoreCase("")) {
                Log.e("image url  = ", notificationData.image);
                Picasso.with(context)
                        .load(notificationData.image)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(holder.img_profile);
            }*/

        } else ToastClass.showToast(context, "no record for list");
    }

    @Override
    public int getItemCount() {
        return notificationList.size();  //count array size
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_message, tv_send,tv_date,tv_time;
        ImageView img_profile;
        LinearLayout ll_item;


        //initialization and click listner
        public ViewHolder(View v) {
            super(v);
            ll_item = v.findViewById(R.id.ll_item);
            tv_name = v.findViewById(R.id.tv_name);
            tv_message = v.findViewById(R.id.tv_message);
            tv_date = v.findViewById(R.id.tv_date);
            tv_time = v.findViewById(R.id.tv_time);
            // img_profile = v.findViewById(R.id.img_profile);

        }
    }
}
