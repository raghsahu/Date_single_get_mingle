package com.dating.datesinglegetmingle.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dating.datesinglegetmingle.Activity.ChatActivity1;
import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Pojo.ChatHistory;
import com.dating.datesinglegetmingle.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.dating.datesinglegetmingle.Const.ARG_EXTRA;
import static com.dating.datesinglegetmingle.Const.ARG_FROM;
import static com.dating.datesinglegetmingle.Const.ARG_HISTORY;

public class MsgConversationAdapter extends RecyclerView.Adapter<MsgConversationAdapter.ViewHolder> {
    private List<ChatHistory> histories;
    private Context context;
    int count=0;

    public MsgConversationAdapter(List<ChatHistory> histories, Context context) {
        this.histories = histories;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_conversation, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.tv_name.setText(histories.get(position).historyName);
        //histories.get(histories.size()-1);
        holder.tv_msg.setText(histories.get(position).message);

        long timeStamp = Long.parseLong(String.valueOf(histories.get(position).time));
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        String cal[] = calendar.getTime().toString().split(" ");
        String time_of_message = cal[1] + "," + cal[2] + "  " + cal[3].substring(0, 5);
        Log.e("TIME_IS : ", calendar.getTime().toString());
        holder.tv_time.setText(time_of_message);


        if (histories.get(position).image != null || !histories.get(position).image.equalsIgnoreCase("")) {
            Log.e("image_url= ", histories.get(position).image);
            Picasso.with(context)
                    .load(Config.Image_Url + histories.get(position).image)
                    .placeholder(R.drawable.download)
                    .error(R.drawable.download)
                    .into(holder.img_profile);
        }

       //Log.e("seen_count",""+histories.size());
       // for (int i=0;i<histories.size();i++){

            if (histories.get(position).seen){
                //int seen_count=0;
               // seen_count++;
               // Log.e("seen_count",""+seen_count);

            }else {
                count++;

                Log.e("unseen_count",""+count);
            }
       // }




    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tv_name, tv_msg, tv_time;
        LinearLayout li_notify_message;

        ImageView img_profile;

        public ViewHolder(View parent) {
            super(parent);
            img_profile = itemView.findViewById(R.id.img_profile);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_msg = itemView.findViewById(R.id.tv_msg);
            tv_time = itemView.findViewById(R.id.tv_time);
            li_notify_message = itemView.findViewById(R.id.li_notify_message);

            li_notify_message.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.li_notify_message:
                    Intent i = new Intent(context, ChatActivity1.class);
                    i.putExtra(ARG_FROM, ARG_HISTORY);
                    i.putExtra(ARG_EXTRA, histories.get(getAdapterPosition()));
                    context.startActivity(i);
                    break;
            }
        }
    }
}

