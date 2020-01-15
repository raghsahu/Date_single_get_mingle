package com.dating.datesinglegetmingle.Activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dating.datesinglegetmingle.Bean.ConnectionDetector;
import com.dating.datesinglegetmingle.Interface.API;
import com.dating.datesinglegetmingle.Interface.Config;
import com.dating.datesinglegetmingle.Pojo.ChatHistory;
import com.dating.datesinglegetmingle.Pojo.Message;
import com.dating.datesinglegetmingle.Pojo.Messages;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.adapter.MessageAdapter_new;
import com.dating.datesinglegetmingle.globle.Session;
import com.dating.datesinglegetmingle.utils.ToastClass;
import com.dating.datesinglegetmingle.utils.Utils;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.dating.datesinglegetmingle.Const.ARG_CHATROOM;
import static com.dating.datesinglegetmingle.Const.ARG_EXTRA;
import static com.dating.datesinglegetmingle.Const.ARG_FROM;
import static com.dating.datesinglegetmingle.Const.ARG_HISTORY;

public class ChatActivity1 extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private String mChatRoom;

    private DatabaseReference mRootReference;
    DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private String mCurrentUserId = "";
    private ImageButton mChatSendButton, mChatAddButton;
    private EditText mMessageView;
    private String mChatUser = "";
    private int mCurrentPage = 1;
    public static final int TOTAL_ITEM_TO_LOAD = 10;
    private String mLastKey = "";
    private String mPrevKey = "";
    private int itemPos = 0;
    private LinearLayoutManager mLinearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mMessagesList;
    private ArrayList<Message> messagesList;
    private MessageAdapter_new mMessageAdapter;
    private ImageView ivBack;
    private ImageView ivProfileImage;
    private TextView tvTitle;
    private String opUserName;
    private String oImage;
    public static LinearLayout ll_chat;
    private LinearLayout ll_menu;
    public static String otherUserNameForToast;
    private ImageView img_menu;
    private TextView tv_view_profile_menu, tv_block_menu, tv_repoert_menu;

    private DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();

    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";

    OkHttpClient mClient = new OkHttpClient();
    String fcmToken = "";
    String userName = "";
    String mImage = "";
    String register_id = "";
    private String userId = "";
    public static String paire = "";
    public String comeFromChatNow = "";
    TextView pared_msg;
    private Session session;
    private boolean isclick = false;

    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    private String block_status = "";
    ValueEventListener seenListener;
    private ChatHistory mHistory;
    public static String send_msg = "";


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
           /* Messages messages = new Messages();
            messages.setMessage(intent.getStringExtra("message"));
            messages.setFrom(intent.getStringExtra("Udid"));
            messagesList.add(messages);
            mMessageAdapter.notifyDataSetChanged();*/
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat1);
        messagesList = new ArrayList<>();

        session = new Session(this);
        userId = session.getUser().user_id;
        userName = session.getUser().user_name;
        mImage = session.getUser().image1;
        register_id = session.getUser().register_id;
        CheckInternet();
        initView();
        clickListner();

    }

    private void CheckInternet() {

        cd = new ConnectionDetector(this);
        isInternetPresent = cd.isConnectingToInternet();
        super.onStart();
    }

    private void initView() {
        //find by id
        tv_repoert_menu = findViewById(R.id.tv_repoert_menu);
        tv_block_menu = findViewById(R.id.tv_block_menu);
        tv_view_profile_menu = findViewById(R.id.tv_view_profile_menu);
        ll_menu = findViewById(R.id.ll_menu);
        img_menu = findViewById(R.id.img_menu);
        pared_msg = findViewById(R.id.pared_msg);
        ll_chat = findViewById(R.id.ll_chat);
        mChatSendButton = findViewById(R.id.chatSendButton);
        mMessageView = findViewById(R.id.chatMessageView);
        mMessagesList = findViewById(R.id.recycleViewMessageList);
        mSwipeRefreshLayout = findViewById(R.id.message_swipe_layout);
        ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener((v) -> finish());
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvTitle = findViewById(R.id.tvTitle);


        //get data from click on list item of user
        Intent intent = getIntent();
        if (ARG_HISTORY.equals(intent.getStringExtra(ARG_FROM))) {
            mHistory = (ChatHistory) intent.getSerializableExtra(ARG_EXTRA);
            mChatRoom = mHistory.roomId;
            mChatUser = mHistory.receiverId;
            opUserName = mHistory.historyName;
            oImage = mHistory.image;
            fcmToken = mHistory.register_id;
            paire = intent.getStringExtra("PAIRE");


        } else if (intent.getExtras() != null) {
            fcmToken = intent.getStringExtra("FCMTOKEN");
            mChatUser = intent.getStringExtra("FUID");
            opUserName = intent.getStringExtra("user_name");
            oImage = intent.getStringExtra("IMAGE");

            paire = intent.getStringExtra("PAIRE");
            comeFromChatNow = intent.getStringExtra("CHATBTN");

            mChatRoom = getChatNode(userId, mChatUser);
            Log.e("fcmToken", "FCM TOKEN =" + fcmToken + "FUID =" + mChatUser);

        }
        if (!mChatRoom.equalsIgnoreCase(null)) {
            String[] a = mChatRoom.split("_(?!.*_)");
            String u_id = a[0];
            String u_id1 = a[1];
            Log.e("a u_id str = ", "" + u_id);
            Log.e("a u_id1 str = ", "" + u_id1);
            if (u_id1.equalsIgnoreCase(userId)) {
                mChatUser = u_id;
            } else {
                mChatUser = u_id1;
            }

            Log.e("chat_user str = ", "" + mChatUser);
        }


        if (intent.getExtras() != null) {
            tvTitle.setText(opUserName);
            if (oImage != null || !oImage.equalsIgnoreCase("")) {
                Log.e("oImage  = ", oImage);
                Picasso.with(this)
                        .load(Config.Image_Url + oImage)
                        .placeholder(R.drawable.download)
                        .error(R.drawable.download)
                        .into(ivProfileImage);
                //Glide.with(this).load(Config.Image_Url + oImage).error(R.drawable.add_image_bg).into(ivProfileImage);
            }
        }
        otherUserNameForToast = opUserName;

        final JSONArray jsonArray = new JSONArray();
        if (register_id.equalsIgnoreCase(fcmToken)) {
            fcmToken = "";
        }

        jsonArray.put(fcmToken);

        Log.e("messagesList size ", "" + messagesList.size());

        try {
            if (paire.equalsIgnoreCase("paire")) {
                pared_msg.setVisibility(View.VISIBLE);
                ll_chat.setVisibility(View.GONE);
                String message = "hi...";
                if (!TextUtils.isEmpty(message)) {
                    final Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("message", message);
                    messageMap.put("senderId", userId);
                    messageMap.put("receiverId", mChatUser);
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("image", mImage);
                    messageMap.put("token", register_id);


                    sendChatMessage(messageMap, jsonArray);
                }
            } else pared_msg.setVisibility(View.VISIBLE);


        } catch (Exception e) {
            Log.e("exception = ", "" + e);


        }

        //set adapter
        mMessageAdapter = new MessageAdapter_new(messagesList, ChatActivity1.this, session);
        mLinearLayoutManager = new LinearLayoutManager(ChatActivity1.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessagesList.setLayoutManager(mLinearLayoutManager);
        mMessagesList.setAdapter(mMessageAdapter);

        //***************************** seen unseen tick ***************
        Log.e("userid", userId);
        //seenMessage(userId);

        MessageStatus();

        //loadMoreMessages();
        mChatSendButton.setOnClickListener(view -> {

            String message = mMessageView.getText().toString();
            if (!TextUtils.isEmpty(message)) {
                final Map<String, Object> messageMap = new HashMap<>();
                messageMap.put("message", message);
                messageMap.put("senderId", userId);
                messageMap.put("receiverId", mChatUser);
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("image", mImage);
                messageMap.put("token", register_id);


                sendChatMessage(messageMap, jsonArray);
                //messagesList.clear();
                mMessageAdapter = new MessageAdapter_new(messagesList, ChatActivity1.this, session);
                mLinearLayoutManager = new LinearLayoutManager(ChatActivity1.this);
                mLinearLayoutManager.setStackFromEnd(true);
                mMessagesList.setLayoutManager(mLinearLayoutManager);
                mMessagesList.setAdapter(mMessageAdapter);
                mMessageAdapter.notifyDataSetChanged();

            }
        });


        /*mReference.child(ARG_CHATROOM).getRef().child(mChatRoom).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    messagesList.add(ds.getValue(Message.class));
                }
                mMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        mReference.child(ARG_CHATROOM).getRef().child(mChatRoom).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              try {
                  messagesList.add(dataSnapshot.getValue(Message.class));
                  mMessageAdapter = new MessageAdapter_new(messagesList, ChatActivity1.this, session);
                  mLinearLayoutManager = new LinearLayoutManager(ChatActivity1.this);
                  mLinearLayoutManager.setStackFromEnd(true);
                  mMessagesList.setLayoutManager(mLinearLayoutManager);
                  mMessagesList.setAdapter(mMessageAdapter);
                  mMessageAdapter.notifyDataSetChanged();

              }catch (Exception e){

              }



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                /*Message newMessage = dataSnapshot.getValue(Message.class);
                if (newMessage != null) {
                    for (int i = 0; i < messagesList.size(); i++) {
                        if (messagesList.get(i).message == newMessage.message) {
                            messagesList.remove(i);
                            messagesList.add(i, newMessage);
                            break;
                        }
                    }
                }*/
                //mMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                /*String key = dataSnapshot.getKey();
                int index = messagesList.indexOf(key);

                if (index != -1) {
                    messagesList.remove(index);
                    // messagesList.remove(index);
                    // mMessageAdapter.notifyDataSetChanged();
                }*/
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

/////////////////////////////block status ////////////////////////////

        if (isInternetPresent) {
            try {
                String url_block_status = API.BASE_URL + "Block_status";
                Log.e("url_block_status url=", url_block_status);
                //user_Block_status(url_block_status);
            } catch (NullPointerException e) {
                Log.e("error =", "" + e);
                //ToastClass.showToast(this, getString(R.string.too_slow));
            }
        } else {
            ToastClass.showToast(this, getString(R.string.no_internal_connection));
        }

    }

    private void MessageStatus() {
        Log.e("aaa_status","aaaaa");

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference();
////You must remember to remove the listener when you finish using it, also to keep track of changes you can use the ChildChange
//        myRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.e(dataSnapshot.getKey(),dataSnapshot.getChildrenCount() + "");
//               // Log.e("bbb_status",""+dataSnapshot.getChildrenCount());
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        })

//        DatabaseReference mDatabase = mReference.child(ARG_CHATROOM).getRef()
//                .child(mChatRoom).child("seen");
//        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//              //  Boolean seen_status = dataSnapshot.getc;
//                mDatabase.setValue(true);
//                Log.e("aaa_stat","aaaaa");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }


    private void seenMessage(String userId) {
        Log.e("msg_status","not read");

        seenListener = mReference.child(ARG_CHATROOM).getRef().child(mChatRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                            Message message = snapshot1.getValue(Message.class);
                            Log.e("receiverid", "" + message.receiverId);
                            //Log.e("mHistory.roomId", "" + mHistory.roomId);
                            try {
                                if (!message.senderId.equalsIgnoreCase(userId) && message.receiverId.equalsIgnoreCase(mChatUser)) {
                                    HashMap<String, Object> seenhashMap = new HashMap<>();
                                    seenhashMap.put("seen", true);
                                    snapshot1.getRef().updateChildren(seenhashMap);

                                    Log.e("read_seen_messsage", "" + message.seen + " " + message.message);
                                }

                                // Log.e("read_seen_messsage",""+message.message);
                            } catch (Exception e) {
                                Log.e("error_seen", "" + e.toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        //******************second method read unread**********************
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            Log.e("msg_status","not read");
//            String UID = currentUser.getUid();
//            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//            Query query = rootRef.child(ARG_CHATROOM).getRef().child(mChatRoom).orderByChild("seen").equalTo(false);
//            query.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    String messageID = dataSnapshot.getKey();
//                    Message message = dataSnapshot.getValue(Message.class);
//                    Log.e("sender_hiid", "" + message.senderId);
//
//                    Log.e("msg_status","not read");
//                    //TODO: Handle Notification here, using the messageID
//                    // A datasnapshot received here will be a new message that the user has not read
//                    // If you want to display data about the message or chat,
//                    // Use the chatID and/or messageID and declare a new
//                    // SingleValueEventListener here, and add it to the chat/message DatabaseReference.
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                    String messageID = dataSnapshot.getKey();
//                    Log.e("msg_status1","read");
//
//                    //TODO: Remove the notification
//                    // If the user reads the message in the app, before checking the notification
//                    // then the notification is no longer relevant, remove it here.
//                    // In onChildAdded you could use the messageID(s) to keep track of the notifications
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });

       // }


    }
//****************************************************************


    private void user_Block_status(String url_block_status) {
        //Utils.showDialog(this, "Loading Please Wait...");

        AndroidNetworking.post(url_block_status)
                .addBodyParameter("user_id", userId)
                .addBodyParameter("to_user_id", mChatUser)
                .setTag("status")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        //Utils.dismissDialog();
                        Log.e("block response =", jsonObject.toString());

                        try {
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");
                            block_status = jsonObject.getString("block_status");

                            if (block_status.equalsIgnoreCase("0")) {
                                Toast.makeText(ChatActivity1.this, message, Toast.LENGTH_SHORT).show();
                                ll_chat.setVisibility(View.GONE);
                                tv_block_menu.setText("UnBlock");
                            } else if (block_status.equalsIgnoreCase("1")) {
                                //Toast.makeText(ChatActivity1.this, message, Toast.LENGTH_SHORT).show();
                                if (messagesList.size() > 1) {
                                    ll_chat.setVisibility(View.VISIBLE);
                                    tv_block_menu.setText("Block");
                                }

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

    private void clickListner() {
        img_menu.setOnClickListener(this);
        tv_repoert_menu.setOnClickListener(this);
        //tv_block_menu.setOnClickListener(this);
        tv_view_profile_menu.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_menu:

                /*if (isInternetPresent) {
                    try {
                        String url_block_status = API.BASE_URL + "Block_status";
                        user_Block_status(url_block_status);
                    } catch (NullPointerException e) {
                        Log.e("error =", "" + e);
                        //ToastClass.showToast(this, getString(R.string.too_slow));
                    }
                } else {
                    ToastClass.showToast(this, getString(R.string.no_internal_connection));
                }*/
                if (!isclick) {
                    ll_menu.setVisibility(View.VISIBLE);
                    isclick = true;

                } else {
                    ll_menu.setVisibility(View.GONE);
                    isclick = false;

                    /*if (isInternetPresent) {
                        try {
                            String url_block_status = API.BASE_URL + "Block_status";
                            user_Block_status(url_block_status);
                        } catch (NullPointerException e) {
                            Log.e("error =", "" + e);
                            //ToastClass.showToast(this, getString(R.string.too_slow));
                        }
                    } else {
                        ToastClass.showToast(this, getString(R.string.no_internal_connection));
                    }*/
                }

                break;

            case R.id.tv_repoert_menu:
                ll_menu.setVisibility(View.GONE);

                openDialog();

                break;

           /* case R.id.tv_block_menu:
                ll_menu.setVisibility(View.GONE);
                if (tv_block_menu.getText().toString().equalsIgnoreCase("Block")) {
                    if (isInternetPresent) {
                        try {
                            String url_block = API.BASE_URL + "Block";
                            Log.e("Block_user_chat url=", url_block);
                            Block_user_chat(url_block);
                        } catch (NullPointerException e) {
                            Log.e("error =", "" + e);
                            //ToastClass.showToast(this, getString(R.string.too_slow));
                        }
                    } else {
                        ToastClass.showToast(this, getString(R.string.no_internal_connection));
                    }

                } else if (tv_block_menu.getText().toString().equalsIgnoreCase("UnBlock")) {
                    if (isInternetPresent) {
                        try {
                            String url_block = API.BASE_URL + "UnBlock";
                            Log.e("Block_user_chat url=", url_block);
                            Block_user_chat(url_block);
                        } catch (NullPointerException e) {
                            Log.e("error =", "" + e);
                            //ToastClass.showToast(this, getString(R.string.too_slow));
                        }
                    } else {
                        ToastClass.showToast(this, getString(R.string.no_internal_connection));
                    }
                }


                break;*/


            case R.id.tv_view_profile_menu:

                Intent intent = new Intent(ChatActivity1.this, UserDetail.class);
                intent.putExtra("CHAT", mChatUser);
                intent.putExtra("Chat_USER", "chat");
                startActivity(intent);
                Log.e("mChatUser profile ", "" + mChatUser);
                Log.e("mChatRoom profile ", "" + mChatRoom + "mChatUser" + mChatUser);
                ll_menu.setVisibility(View.GONE);

                break;
        }
    }

    private void Block_user_chat(String url_block) {
        Utils.showDialog(this, "Loading Please Wait...");

        Log.e("user_id_log = ", userId);
        AndroidNetworking.post(url_block)
                .addBodyParameter("user_id", userId)
                .addBodyParameter("to_user_id", mChatUser)
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
                                Toast.makeText(ChatActivity1.this, message, Toast.LENGTH_SHORT).show();
                                if (message.equalsIgnoreCase("User Unblocked successfully.")) {
                                    tv_block_menu.setText("Block");
                                    ll_chat.setVisibility(View.VISIBLE);
                                } else if (message.equalsIgnoreCase("block Sucessfully")) {
                                    tv_block_menu.setText("UnBlock");
                                    ll_chat.setVisibility(View.GONE);
                                }

                            } else {
                                ToastClass.showToast(ChatActivity1.this, message);
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

    private void openDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_report);

        Button btn_send = dialog.findViewById(R.id.btn_send);
        EditText et_report = dialog.findViewById(R.id.et_report);

        btn_send.setOnClickListener(v -> {
            String report = et_report.getText().toString();
            if (report.equalsIgnoreCase("")) {
                et_report.setError("Can't empty");
                et_report.requestFocus();
            } else {
                if (isInternetPresent) {
                    try {
                        String url_report = API.BASE_URL + "Report";
                        UserReport(url_report, report, dialog);
                    } catch (NullPointerException e) {
                        Log.e("error =", "" + e);
                        //ToastClass.showToast(this, getString(R.string.too_slow));
                    }
                } else {
                    ToastClass.showToast(ChatActivity1.this, getString(R.string.no_internal_connection));
                }
            }
        });
        dialog.show();
    }

    private void UserReport(String url_report, String report, Dialog dialog) {
        Utils.showDialog(this, "Loading Please Wait...");

        Log.e("user_id_log = ", userId);

        AndroidNetworking.post(url_report)
                .addBodyParameter("user_id", userId)
                .addBodyParameter("to_user_id", mChatUser)
                .addBodyParameter("report_msg", report)
                .setTag("status")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        Utils.dismissDialog();
                        Log.e("report response =", jsonObject.toString());

                        try {
                            String message = jsonObject.getString("msg");
                            String result = jsonObject.getString("result");

                            if (result.equalsIgnoreCase("true")) {
                                Toast.makeText(ChatActivity1.this, message, Toast.LENGTH_SHORT).show();
                                finish();
                                dialog.dismiss();
                            } else {
                                ToastClass.showToast(ChatActivity1.this, message);
                                //  Utils.openAlertDialog(Notification.this, message);
                                dialog.dismiss();
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

    //---FIRST 10 MESSAGES WILL LOAD ON START----
    private void loadMessages() {
        DatabaseReference messageRef = mReference.child(mChatRoom).child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEM_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Message message = dataSnapshot.getValue(Message.class);

                    Log.e("Message", message.message);
                    itemPos++;

                    if (itemPos == 1) {
                        String mMessageKey = dataSnapshot.getKey();

                        mLastKey = mMessageKey;
                        mPrevKey = mMessageKey;
                    }

                    messagesList.add(message);

                    Log.e("load mor msgList size1", "msg-" + messagesList.size());


                    //set adapter

                    mMessageAdapter.notifyDataSetChanged();

                    mMessagesList.scrollToPosition(messagesList.size() - 1);

                    //mSwipeRefreshLayout.setRefreshing(false);
                } catch (Throwable e) {
                    Log.e(" ", "" + e);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //---ON REFRESHING 10 MORE MESSAGES WILL LOAD----
    private void loadMoreMessages() {

        DatabaseReference messageRef = mReference.child(mChatRoom).child(mCurrentUserId).child(mChatUser);
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                String messageKey = dataSnapshot.getKey();


                if (!mPrevKey.equals(messageKey)) {
                    messagesList.add(itemPos++, message);

                } else {
                    mPrevKey = mLastKey;
                }

                if (itemPos == 1) {
                    String mMessageKey = dataSnapshot.getKey();
                    mLastKey = mMessageKey;
                }

                Log.e("load more list size", "msg-" + messagesList.size());

                //set adapter
                mMessageAdapter = new MessageAdapter_new(messagesList, ChatActivity1.this, session);
                mLinearLayoutManager = new LinearLayoutManager(ChatActivity1.this);
                mLinearLayoutManager.setStackFromEnd(true);
                mMessagesList.setLayoutManager(mLinearLayoutManager);
                mMessagesList.setAdapter(mMessageAdapter);
                mMessageAdapter.notifyDataSetChanged();

                //mSwipeRefreshLayout.setRefreshing(false);

                mLinearLayoutManager.scrollToPositionWithOffset(10, 0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void sendMessage(final JSONArray recipients, final String title, final String body, final String icon, final String message) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {

                    Log.e("useridand name", userId + "  name " + userName);
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
                    notification.put("icon", icon);

                    JSONObject data = new JSONObject();
                    data.put("message", message);

                    data.put("fcmId", fcmToken);
                    //data.put("Udid", mChatUser);
                    data.put("Udid", "" + mCurrentUserId);
                    data.put("userId", userId);
                    data.put("userName", userName);

                    root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", recipients);

                    String result = postToFCM(root.toString());
                    Log.d("Main Activity", "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                    // Toast.makeText(ChatActivity1.this, "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Toast.makeText(ChatActivity1.this, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {


        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + "AAAASGMi2Jk:APA91bGHycPZElcdpr1qDB8V00Buocmyu4Wm2puA9kFyi0O3zT9ZzZin1yheegCdbxzXZwShiwRChnz7yqzI3amy2DW-YyGh2R7YY-mRjIZO40zcluI8t6gxSRAUPahd3Bruk2Ywy5lV")
                .build();

        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));*/
        //messagesList.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();

//        mReference.removeEventListener(seenListener);
    }


    private void sendChatMessage(Map<String, Object> data, JSONArray tokens) {
        try {
            mReference.child(ARG_CHATROOM).getRef().child(mChatRoom).push().setValue(data).addOnSuccessListener(aVoid -> {
                data.put("seen", false);
                data.put("roomId", mChatRoom);

                sendToMyChatHistory(data);
                sendToOtherChatHistory(data);

                sendMessage(tokens, userName, String.valueOf(data.get("message")), "Http:\\google.com", String.valueOf(data.get("message")));
                mMessageView.setText("");
            });
        } catch (Exception e) {
            Log.e("Exception sendChatMsg ", "" + e);
        }

    }

    private void sendToMyChatHistory(Map<String, Object> data) {
        data.put("historyName", opUserName);
        data.put("image", oImage);
        data.put("register_id", fcmToken);
        mReference.child(ARG_HISTORY).getRef().child(userId).child(mChatRoom).setValue(data);
    }

    private void sendToOtherChatHistory(Map<String, Object> data) {
        data.put("historyName", userName);
        data.put("image", mImage);
        data.put("register_id", register_id);
        mReference.child(ARG_HISTORY).getRef().child(mChatUser).child(mChatRoom).setValue(data);
    }

    private String getChatNode(String mUserId, String oUserId) {
        int mId = Integer.parseInt(mUserId);
        int oId = Integer.parseInt(oUserId);

        String mNode;
        if (mId > oId) {
            mNode = oId + "_" + mId;
        } else {
            mNode = mId + "_" + oId;
        }
        return mNode;
    }


    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
        /*if (messagesList != null) {
            messagesList.clear();
        }
        mSwipeRefreshLayout.setRefreshing(false);
        loadMoreMessages();*/
        //mMessageAdapter.notifyDataSetChanged();
    }
}
