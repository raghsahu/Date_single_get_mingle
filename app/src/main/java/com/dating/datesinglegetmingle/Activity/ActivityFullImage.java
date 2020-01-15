package com.dating.datesinglegetmingle.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Interface.ViewPagerListener;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.adapter.Slider_Dialog_Adapter;
import com.dating.datesinglegetmingle.utils.ZoomOutPageTransformer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ActivityFullImage extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    private String image;
     ViewPager viewPager;
    // DetailsSliderAdapter detailsSliderAdapter;
    Slider_Dialog_Adapter slider_dialog_adapter;
     ArrayList<String> Image_Array=new ArrayList<>();
    private String image1="",image2 = "", image3 = "", image4 = "", image5 = "", image6 = "";
    LinearLayout details_linear_layout;
    private int dotsCount;
    private ImageView[] dotes;
    LinkedHashSet<String>image_new_array=new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        viewPager = findViewById(R.id.slider_pager);
        details_linear_layout = findViewById(R.id.details_linear_layout);

        Intent intent = getIntent();

        if (intent != null) {
            image = intent.getStringExtra("IMAGE");
        }

        ImageView detail_full_image = findViewById(R.id.detail_full_image);
        ImageView iv_cross = findViewById(R.id.iv_cross);
        iv_cross.setOnClickListener(v -> finish());

        final Bundle stringArrayList = getIntent().getExtras();
        Image_Array = stringArrayList.getStringArrayList("img_model");

        Log.e("img_list_size",""+Image_Array.size());

        for (int i=0; i<Image_Array.size();i++){

            if (image.equalsIgnoreCase(Image_Array.get(i))){
                Log.e("img_position",""+Image_Array.get(i));

               // image_new_array.add(Image_Array.get(i));
                Image_Array.remove(Image_Array.get(i));


            }
        }



        setImage_DialogVIewPager();

//*********************************************************
        if (image != null) {
            Picasso.with(this).load(Config.Image_Url + image)
                    .fit()
                    //.centerCrop()
                    .placeholder(R.drawable.download)
                    .error(R.drawable.download)
                    .fit()
                    .into(detail_full_image);
//             Glide.with(getApplicationContext())
//                     .load(Config.Image_Url + image)
//                     .error(R.drawable.add_image_bg).into(detail_full_image);
        }
    }
//******************************************************************
    private void setImage_DialogVIewPager() {

        if (Image_Array != null) {

            if (image1 != null && !image1.equalsIgnoreCase("")) {
                //strings.add(image1);
                Image_Array.add(image1);
            }

            if (image2 != null && !image2.equalsIgnoreCase("")) {
                //strings.add(image2);
                Image_Array.add(image2);
            }
            if (image3 != null && !image3.equalsIgnoreCase("")) {
                //strings.add(image3);
                Image_Array.add(image3);
            }
            if (image4 != null && !image4.equalsIgnoreCase("")) {
                //strings.add(image4);
                Image_Array.add(image4);
            }
            if (image5 != null && !image5.equalsIgnoreCase("")) {
                // strings.add(image5);
                Image_Array.add(image5);
            }
            if (image6 != null && !image6.equalsIgnoreCase("")) {
                //strings.add(image6);
                Image_Array.add(image6);
            }
            if (image != null && !image.equalsIgnoreCase("")) {
                //strings.add(image6);
                Image_Array.add(0,image);
            }
            Log.e("araylist size img = ", "" + Image_Array.size());

            slider_dialog_adapter = new Slider_Dialog_Adapter(ActivityFullImage.this, Image_Array, new ViewPagerListener() {
                @Override
                public void getImagePosition(int position) {
                    Log.e("image listner = ", Image_Array.get(position));
                    //listDilog(this, detailsModals.get(position));
                }
            });
            viewPager.setAdapter(slider_dialog_adapter);
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            viewPager.setCurrentItem(0);
            if (Image_Array.size() > 0) {
                setUiPageViewController();
            }
            viewPager.setOnPageChangeListener(ActivityFullImage.this);
        }
    }

    private void setUiPageViewController() {

        details_linear_layout.removeAllViews();
        dotsCount = slider_dialog_adapter.getCount();
        dotes = new ImageView[dotsCount];
        for (int i = 0; i < dotsCount; i++) {
            dotes[i] = new ImageView(ActivityFullImage.this);
            dotes[i].setImageResource(R.drawable.circle_inactive);

            details_linear_layout.addView(dotes[i]);
        }
        if (dotes.length == 1) {
            details_linear_layout.setVisibility(View.GONE);
        }
        dotes[0].setImageResource(R.drawable.circle_active);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

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

    @Override
    public void onPageScrollStateChanged(int i) {

    }

}
