package com.dating.datesinglegetmingle.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    private RelativeLayout search_back_lay;
    private Spinner from_age_spn, to_age_spn;
    private TextView from_age_text, to_age_text;
    private Dialog dialog;
    private RecyclerView age_recycler;
    private List<String> age_list = new ArrayList<>();
    private LinearLayout from_age_lay, to_age_lay;
    private RadioButton active_radio_btn, newest_radio_btn;
    private EditText search_location_edt;
    private ProgressDialog progressDialog;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String user_id = "", location = "";
    private String from_age = "", to_age = "", city_id = "";
    private Button btn_search;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progressDialog = new ProgressDialog(Search.this);
        session = new Session(this);
        user_id = session.getUser().user_id;
        CheckInternet();
        //GetLoginId();
        init();
        onclick();

        for (int i = 18; i < 71; i++) {
            age_list.add(String.valueOf(i));
        }
    }

    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void init() {

        search_back_lay = findViewById(R.id.search_back_lay);
        from_age_spn = findViewById(R.id.from_age_spn);
        to_age_spn = findViewById(R.id.to_age_spn);
        from_age_text = findViewById(R.id.from_age_text);
        to_age_text = findViewById(R.id.to_age_text);
        from_age_lay = findViewById(R.id.from_age_lay);
        to_age_lay = findViewById(R.id.to_age_lay);
        active_radio_btn = findViewById(R.id.active_radio_btn);
        newest_radio_btn = findViewById(R.id.newest_radio_btn);
        search_location_edt = findViewById(R.id.search_location_edt);
        btn_search = findViewById(R.id.btn_search);
        newest_radio_btn.setChecked(true);

        //set select city
       /* if (!location.equalsIgnoreCase("") && !location.equalsIgnoreCase("null")) {
            search_location_edt.setText(location);
        }*/
    }

    private void onclick() {

        search_back_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        from_age_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(Search.this);
                dialog.setContentView(R.layout.from_age_layout);
                age_recycler = dialog.findViewById(R.id.from_age_rl);
                //------------- set adapter ------------
                AgeAdapter adaper = new AgeAdapter(Search.this, age_list);
                age_recycler.setLayoutManager(new LinearLayoutManager(Search.this, LinearLayoutManager.VERTICAL, false));
                age_recycler.setAdapter(adaper);
                adaper.notifyDataSetChanged();
                dialog.show();
                //from_age_spn.setVisibility(View.VISIBLE);
            }
        });

        to_age_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new Dialog(Search.this);
                dialog.setContentView(R.layout.from_age_layout);
                age_recycler = dialog.findViewById(R.id.from_age_rl);
                //------------- set adapter ------------
                AgeAdapter1 adaper = new AgeAdapter1(Search.this, age_list);
                age_recycler.setLayoutManager(new LinearLayoutManager(Search.this, LinearLayoutManager.VERTICAL, false));
                age_recycler.setAdapter(adaper);
                adaper.notifyDataSetChanged();
                dialog.show();
                //to_age_spn.setVisibility(View.VISIBLE);
            }
        });

        active_radio_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    newest_radio_btn.setChecked(false);
                }
            }
        });

        newest_radio_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    active_radio_btn.setChecked(false);
                }
            }
        });

        search_location_edt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ActivitySelectCity.class);
                intent.putExtra("SEARCH", "2");
                startActivity(intent);
                Animatoo.animateSlideUp(Search.this);
            }
        });

        btn_search.setOnClickListener(v -> {
            location = search_location_edt.getText().toString();
            from_age = from_age_text.getText().toString();
            to_age = to_age_text.getText().toString();

            session.saveSearchage(from_age, to_age);
            session.saveSearchCity(location, city_id);

            Intent intent = new Intent(getApplicationContext(), Home.class);
            intent.putExtra("SEARCH", "SEARCH");
            intent.putExtra("search", location);
            intent.putExtra("from_age", from_age);
            intent.putExtra("to_age", to_age);
            intent.putExtra("city_id", city_id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Animatoo.animateSlideUp(Search.this);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        from_age = session.getFrom_Age();
        to_age = session.getTo_Age();
        location = session.getCityy();
        city_id = session.getCityId();

        if (from_age != null && !from_age.equalsIgnoreCase("")) {
            from_age_text.setText(from_age);
        }
        if (to_age != null && !to_age.equalsIgnoreCase("")) {
            to_age_text.setText(to_age);
        }

        if (location != null && !location.equalsIgnoreCase("")) {
            search_location_edt.setText(location);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Search.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    //------------------- from age adapter -----------

    public class AgeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<String> stringList;
        RecyclerView recyclerView;
        View view;

        public AgeAdapter(Context context, List<String> stringList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.stringList = stringList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.age_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Log.e("Dattasize***", "" + stringList.size());
            final MyHolder myHolder = (MyHolder) holder;

            if (stringList.size() > 0) {
                myHolder.age_pojo_txt.setText(String.valueOf(stringList.get(position)));

                myHolder.age_pojo_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        from_age_text.setText(String.valueOf(stringList.get(position)));
                        dialog.cancel();

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView age_pojo_txt;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                age_pojo_txt = itemView.findViewById(R.id.age_pojo_txt);
            }
        }
    }

    //------------------- from age adapter -----------

    public class AgeAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        public List<String> stringList;
        RecyclerView recyclerView;
        View view;

        public AgeAdapter1(Context context, List<String> stringList) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.stringList = stringList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            view = inflater.inflate(R.layout.age_pojo_lay, parent, false);
            MyHolder holder = new MyHolder(view);
            return holder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            Log.e("Dattasize***", "" + stringList.size());
            final MyHolder myHolder = (MyHolder) holder;

            if (stringList.size() > 0) {
                myHolder.age_pojo_txt.setText(String.valueOf(stringList.get(position)));
                myHolder.age_pojo_txt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        to_age_text.setText(String.valueOf(stringList.get(position)));
                        dialog.cancel();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return stringList.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView age_pojo_txt;

            // create constructor to get widget reference
            public MyHolder(View itemView) {
                super(itemView);
                age_pojo_txt = itemView.findViewById(R.id.age_pojo_txt);
            }
        }
    }
}
