/*
package com.dating.datesinglegetmingle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dating.datesinglegetmingle.Pojo.Messages;
import com.dating.datesinglegetmingle.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

*/
/**
 * Created by KSHITIZ on 3/27/2018.
 *//*


public class MessageAdapter1 extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private List<Messages> mMessagesList;
    private FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference ;
    Context context;

    //-----GETTING LIST OF ALL MESSAGES FROM CHAT ACTIVITY ----
    public MessageAdapter1(List<Messages> mMessagesList) {
        this.mMessagesList = mMessagesList;
    }


    //---CREATING SINGLE HOLDER AND RETURNING ITS VIEW---
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        if (viewType==VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent,parent,false);

            return new MyMessageViewHolder(view);
        }
       else if (viewType==VIEW_TYPE_MESSAGE_RECEIVED){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received,parent,false);
            return new OtherMessageViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Messages messages=mMessagesList.get(i);
        switch (viewHolder.getItemViewType()){
            case VIEW_TYPE_MESSAGE_SENT:{
                ((MyMessageViewHolder)viewHolder).bind(messages);
                break;
            }
            case VIEW_TYPE_MESSAGE_RECEIVED:{
                ((OtherMessageViewHolder)viewHolder).bind(messages);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages=mMessagesList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getFrom())){
            return VIEW_TYPE_MESSAGE_SENT;
        }else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }

    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }
}
*/
