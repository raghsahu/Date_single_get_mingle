package com.dating.datesinglegetmingle.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Pojo.Message;
import com.dating.datesinglegetmingle.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MyMessageViewHolder extends RecyclerView.ViewHolder {
    TextView tvText, tvTime;
    ImageView ivUserImage;
    public static TextView tvseen;

    MyMessageViewHolder(View itemView) {
        super(itemView);
        tvText = (TextView) itemView.findViewById(R.id.tvText);
        tvTime = (TextView) itemView.findViewById(R.id.tvTime);
        ivUserImage = itemView.findViewById(R.id.ivUserImage);
        tvseen = itemView.findViewById(R.id.tvseen);

    }

    void bind(Message message) {

        long timeStamp = message.time;
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        String cal[] = calendar.getTime().toString().split(" ");
        String time_of_message = cal[1] + "," + cal[2] + "  " + cal[3].substring(0, 5);
        Log.e("TIME IS : ", calendar.getTime().toString());
        tvText.setText(message.message);
        tvTime.setText(time_of_message);

        if (message.seen) {
            tvseen.setText("Read");
            tvseen.setTextColor(Color.parseColor("#FF0000"));
        } else {
            tvseen.setText("Delivered");
        }

        Log.e("seen_related",""+message.seen);

        if (message.image != null || !message.image.equalsIgnoreCase("")) {
            Log.e("message image url  = ", message.image);
            Picasso.with(itemView.getContext())
                    .load(Config.Image_Url + message.image)
                    .placeholder(R.drawable.download)
                    .error(R.drawable.download)
                    .into(ivUserImage);
        }
    }

}