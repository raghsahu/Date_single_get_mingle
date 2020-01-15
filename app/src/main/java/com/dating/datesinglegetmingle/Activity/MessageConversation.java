package com.dating.datesinglegetmingle.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dating.datesinglegetmingle.Pojo.ChatHistory;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.adapter.MsgConversationAdapter;
import com.dating.datesinglegetmingle.globle.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.dating.datesinglegetmingle.Const.ARG_HISTORY;

public class MessageConversation extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_back;
    private DatabaseReference mRootReference;
    private RecyclerView recyclerview;
    private String url, userId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout l_no_record;
    //Session session;
    private MsgConversationAdapter mAdapter;


    private List<ChatHistory> mHistory;


    private DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_conversation);


        Session session = new Session(this);
        userId = session.getUser().user_id;


        initView();
        clickListner();
    }


    private void initView() {
        mHistory = new ArrayList<>();

        iv_back = findViewById(R.id.iv_back);
        recyclerview = findViewById(R.id.recycler_view);
        //swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);



        //mAdapter.notifyDataSetChanged();

        mReference.child(ARG_HISTORY).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mHistory.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mHistory.add(ds.getValue(ChatHistory.class));
                }

                mAdapter = new MsgConversationAdapter(mHistory, MessageConversation.this);
                RecyclerView.LayoutManager mLayoutManger = new LinearLayoutManager(MessageConversation.this);
                recyclerview.setLayoutManager(mLayoutManger);
                recyclerview.setLayoutManager(new LinearLayoutManager(MessageConversation.this, LinearLayoutManager.VERTICAL, false));
                recyclerview.setItemAnimator(new DefaultItemAnimator());
                recyclerview.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void clickListner() {
        iv_back.setOnClickListener(this);
        //swipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                //onBackPressed();
                Intent intent = new Intent(MessageConversation.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }
    }

    /*@Override
    public void onRefresh() {
        msgConversationModels.clear();
        swipeRefreshLayout.setRefreshing(false);
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MessageConversation.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
