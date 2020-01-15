package com.dating.datesinglegetmingle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.dating.datesinglegetmingle.Pojo.Message;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import static com.dating.datesinglegetmingle.Activity.ChatActivity1.ll_chat;
import static com.dating.datesinglegetmingle.Activity.ChatActivity1.otherUserNameForToast;
import static com.dating.datesinglegetmingle.Activity.ChatActivity1.paire;

/**
 * Created by KSHITIZ on 3/27/2018.
 */

public class MessageAdapter_new extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;
    private ArrayList<Message> mMessagesList;
    private FirebaseAuth mAuth;
    Context context;
    private Session session;

    //-----GETTING LIST OF ALL MESSAGES FROM CHAT ACTIVITY ----
    public MessageAdapter_new(ArrayList<Message> mMessagesList, Context context, Session session) {
        this.context = context;
        this.mMessagesList = mMessagesList;
        this.session = session;
    }


    //---CREATING SINGLE HOLDER AND RETURNING ITS VIEW---
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_MY_MESSAGE) {

            return new MyMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_my_message, parent, false));
        } else if (viewType == VIEW_TYPE_OTHER_MESSAGE) {
            return new OtherMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_other_message, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Message messages = mMessagesList.get(i);
        //******************
        if (i == mMessagesList.size() - 1) {
            if (messages.seen) {
                Log.e("seen_status", "seen");
                Log.e("seenMsg_status", "" + messages.seen);
            } else {
                Log.e("seenDel_status", "" + messages.seen);
                Log.e("seen_delevered", "delivered");
            }
        } else {
            Log.e("ssen_ssss", "ssss");
        }
        //******************

        if (mMessagesList.size() == 1) {
            try {
                //if (paire.equalsIgnoreCase("paire")) {
                if ("paire".equalsIgnoreCase(paire)) {
                    ll_chat.setVisibility(View.GONE);
                    switch (viewHolder.getItemViewType()) {
                        case VIEW_TYPE_MY_MESSAGE: {
                            ((MyMessageViewHolder) viewHolder).bind(messages);
                            break;
                        }
                        case VIEW_TYPE_OTHER_MESSAGE: {

                            ((OtherMessageViewHolder) viewHolder).bind(messages);
                            break;
                        }
                    }

                } else {
                    ll_chat.setVisibility(View.GONE);
                    switch (viewHolder.getItemViewType()) {
                        case VIEW_TYPE_MY_MESSAGE: {
                            ll_chat.setVisibility(View.GONE);
                            ToastClass.showToast(context, "unable to chat, wait for ..." + " " + otherUserNameForToast + " response");
                            ((MyMessageViewHolder) viewHolder).bind(messages);
                            break;
                        }
                        case VIEW_TYPE_OTHER_MESSAGE: {
                            ll_chat.setVisibility(View.VISIBLE);
                            ((OtherMessageViewHolder) viewHolder).bind(messages);
                            break;
                        }
                    }
                }

            } catch (Exception e) {
                Log.e("exception = ", "" + e);
            }


        } else {

            ll_chat.setVisibility(View.VISIBLE);
            switch (viewHolder.getItemViewType()) {
                case VIEW_TYPE_MY_MESSAGE: {
                    ((MyMessageViewHolder) viewHolder).bind(messages);
                    break;
                }
                case VIEW_TYPE_OTHER_MESSAGE: {

                    ((OtherMessageViewHolder) viewHolder).bind(messages);
                    break;
                }
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (session.getUser().user_id.equals(mMessagesList.get(position).senderId)) {
            return VIEW_TYPE_MY_MESSAGE;
        } else {
            return VIEW_TYPE_OTHER_MESSAGE;
        }

    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }
}
