package com.dating.datesinglegetmingle.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Interface.ViewPagerListener;
import com.dating.datesinglegetmingle.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class Slider_Dialog_Adapter  extends PagerAdapter {

        ArrayList<String> detailsModals;
        private Context context;
        String sel_image;
        private LayoutInflater layoutInflater;
        private ViewPagerListener viewPagerListener;


        public Slider_Dialog_Adapter(Context context, ArrayList<String> detailsModals, ViewPagerListener viewPagerListener) {
            this.context = context;
            //this.sel_image=image;
            this.detailsModals = detailsModals;
            this.viewPagerListener = viewPagerListener;
        }

        @Override
        public int getCount() {
            return detailsModals.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.full_dialog_image, container, false);

            ImageView imageView = view.findViewById(R.id.imagee);
            RelativeLayout whole_view = view.findViewById(R.id.whole_view);

            String img = detailsModals.get(position);
            Log.e("img adapter = ", img);

//            Glide.with(context)
//                        .load(Config.Image_Url + sel_image)
//                        .error(R.drawable.add_image_bg)
//                        .into(imageView);

            if (detailsModals.size() > 0) {
            Picasso.with(context).load(Config.Image_Url + detailsModals.get(position))
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.download)
                    .error(R.drawable.download)
                    .into(imageView);

//                Glide.with(context)
//                        .load(Config.Image_Url + detailsModals.get(position))
//                        .error(R.drawable.add_image_bg)
//                        .into(imageView);

            }

//            whole_view.setOnClickListener(v -> {
//                Intent i = new Intent(context, ActivityFullImage.class);
//                i.putExtra("IMAGE", detailsModals.get(position));
//                i.putStringArrayListExtra("img_model",detailsModals);
//                context.startActivity(i);
//
//                //viewPagerListener.getImagePosition(position);
//            });


            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }



}
