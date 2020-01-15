package com.dating.datesinglegetmingle.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dating.datesinglegetmingle.Pojo.CityData;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;

import java.util.ArrayList;

/**
 * Created by Ravindra Birla on 24/06/2019.
 */
public class SelectCityAdapter extends RecyclerView.Adapter<SelectCityAdapter.ViewHolder> {
    private ArrayList<CityData> cityList;
    private Context context;
    private CityData cityData;


    //constructor
    public SelectCityAdapter(ArrayList<CityData> cityList, Context context) {
        this.cityList = cityList;
        this.context = context;
    }


    @NonNull
    @Override
    public SelectCityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int pos) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_pojo_lay, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectCityAdapter.ViewHolder holder, int position) {

        //set data from arraylist
        if (cityList.size() > 0) {

            cityData = cityList.get(position);

            holder.tv_name.setText(cityData.city_name);

        } else ToastClass.showToast(context, "no record for list");
    }

    @Override
    public int getItemCount() {
        return cityList.size();  //count array size
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_name, tv_age, tv_location;
        ImageView users_profile_image;
        LinearLayout ll_item;


        //initialization and click listner
        public ViewHolder(View v) {
            super(v);
            tv_name = v.findViewById(R.id.city_name_txt);

            tv_name.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.city_name_txt) {
                tv_name.setTextColor(Color.parseColor("#09dade"));
                int position = getAdapterPosition();
                cityData = cityList.get(position);

                if (cityData.selectPage.equalsIgnoreCase("1")) {
                    //intent = new Intent(context, Signup.class);
                    //sp.saveData(context, "search", cityData.city_name);
                    //sp.saveData(context, "city_id", cityData.city_id);
                    new Session(context).saveSearchCity(cityData.city_name,cityData.city_id);
                    ((Activity) context).finish();
                } else if (cityData.selectPage.equalsIgnoreCase("2")) {
                    //sp.saveData(context, "search", cityData.city_name);
                    //sp.saveData(context, "city_id", cityData.city_id);
                    new Session(context).saveSearchCity(cityData.city_name,cityData.city_id);
                    ((Activity) context).finish();
                } else if (cityData.selectPage.equalsIgnoreCase("3")) {
                   // sp.saveData(context, "search", cityData.city_name);
                    //sp.saveData(context, "city_id", cityData.city_id);
                    new Session(context).saveSearchCity(cityData.city_name,cityData.city_id);
                    ((Activity) context).finish();
                }
            }
        }
    }


    public void updateList(ArrayList<CityData> temp) {
        cityList = temp;
        notifyDataSetChanged();
    }

}
