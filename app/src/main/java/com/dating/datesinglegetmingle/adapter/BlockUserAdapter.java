package com.dating.datesinglegetmingle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dating.datesinglegetmingle.Bean.BlockUserData;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Interface.API;
import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.dating.datesinglegetmingle.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ravindra Birla on 24/06/2019.
 */
public class BlockUserAdapter extends RecyclerView.Adapter<BlockUserAdapter.ViewHolder> {
    private ArrayList<BlockUserData> blockList;
    private Context context;
    private BlockUserData blockData;
    private String user_id;
    private String logid;
    private String status;
    private String contact_no;
    private ConnectionDetector cd;
    private Session session;

    private String userId;


    //constructor
    public BlockUserAdapter(ArrayList<BlockUserData> blockList, Context context) {
        this.blockList = blockList;
        this.context = context;
    }


    @NonNull
    @Override
    public BlockUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_block, parent, false);
        session = new Session(context);
        userId = session.getUser().user_id;


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockUserAdapter.ViewHolder holder, int position) {

        //set data from arraylist
        if (blockList.size() > 0) {

            blockData = blockList.get(position);

            holder.tv_name.setText(blockData.user_name);
            holder.tv_age.setText(blockData.age);
            holder.tv_city.setText(blockData.city_name);

            // holder.tv_send.setBackgroundResource(blockData.isSelected() ? R.drawable.circle_purple_bg : R.drawable.circle_gray_bg);

            if (blockData.image1 != null && !blockData.image1.equalsIgnoreCase("")) {
                Log.e("image url  = ", blockData.image1);
                Picasso.with(context)
                        .load(Config.Image_Url + blockData.image1)
                        .placeholder(R.drawable.download)
                        .error(R.drawable.download)
                        .into(holder.iv_profile);
            }

        } else ToastClass.showToast(context, "no record for list");
    }

    @Override
    public int getItemCount() {
        return blockList.size();  //count array size
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name, tv_age, tv_city;
        ImageView iv_profile;
        Button btn_unblock;


        //initialization and click listner
        public ViewHolder(View v) {
            super(v);
            tv_name = v.findViewById(R.id.tv_name);
            tv_age = v.findViewById(R.id.tv_age);
            tv_city = v.findViewById(R.id.tv_city);
            iv_profile = v.findViewById(R.id.iv_profile);
            btn_unblock = v.findViewById(R.id.btn_unblock);

            //click listner
            iv_profile.setOnClickListener(this);
            btn_unblock.setOnClickListener(this);


        }


        @Override
        public void onClick(View view) {
            int position;
            switch (view.getId()) {
                case R.id.btn_unblock:
                    position = getAdapterPosition();
                    blockData = blockList.get(position);
                    String url_block = API.BASE_URL + "Block";
                    UnBlockApi(url_block, blockData);
                    blockList.remove(position);
                    notifyDataSetChanged();

                    break;

            }
        }

        private void UnBlockApi(String url_block, BlockUserData blockData) {
            Utils.showDialog(context, "Loading Please Wait...");

            Log.e("user_id_log = ", url_block);
            Log.e("to_user_id = ", blockData.user_id);
            AndroidNetworking.post(url_block)
                    .addBodyParameter("user_id", userId)
                    .addBodyParameter("to_user_id", blockData.user_id)
                    .setTag("status")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {

                            Utils.dismissDialog();
                            Log.e("block response =", jsonObject.toString());

                            try {
                                String message = jsonObject.getString("msg");
                                String result = jsonObject.getString("result");

                                if (result.equalsIgnoreCase("true")) {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();


                                } else {
                                    ToastClass.showToast(context, message);
                                    //  Utils.openAlertDialog(Notification.this, message);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("error = ", String.valueOf(anError));

                        }
                    });
        }
    }

}
