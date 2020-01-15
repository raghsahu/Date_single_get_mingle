package com.dating.datesinglegetmingle.Activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.dating.datesinglegetmingle.R;
import com.dating.datesinglegetmingle.globle.Session;
import com.google.firebase.auth.FirebaseAuth;

public class Setting extends AppCompatActivity {

    private RelativeLayout setting_back_lay;
    private CardView about_card, logout_card, terms_card, invite_card,
            change_pass_card, skipped_card, rate_card;
    private Session session;
    private RelativeLayout rl_block_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        session = new Session(this);


        rl_block_user = findViewById(R.id.rl_block_user);
        setting_back_lay = findViewById(R.id.setting_back_lay);
        about_card = findViewById(R.id.about_card);
        logout_card = findViewById(R.id.logout_card);
        terms_card = findViewById(R.id.terms_card);
        invite_card = findViewById(R.id.invite_card);
        about_card = findViewById(R.id.about_card);
        change_pass_card = findViewById(R.id.change_pass_card);
        skipped_card = findViewById(R.id.skipped_card);
        rate_card = findViewById(R.id.rate_card);

        about_card.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), FAQ.class));
            Animatoo.animateSlideUp(Setting.this);
        });
        skipped_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SkippedProfile.class));
                Animatoo.animateSlideUp(Setting.this);
            }
        });

        terms_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TermsAndCondition.class));
                Animatoo.animateSlideUp(Setting.this);
            }
        });

        change_pass_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChangePassword.class));
                Animatoo.animateSlideUp(Setting.this);
            }
        });

        logout_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });

        invite_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTextUrl();
            }
        });

        rate_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMarket();
            }
        });


        setting_back_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Setting.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        rl_block_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setting.this, ActivityBlockUser.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void Logout() {

        new AlertDialog.Builder(Setting.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /*sp = new MySharedPref();
                        Toast.makeText(Setting.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        Animatoo.animateFade(Setting.this);
                        sp.removeFromSharedPreferences(getApplicationContext(), "remove");
                        sp.DeleteData(getApplicationContext());*/
                        session.logout();
                        FirebaseAuth.getInstance().signOut();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Date Single Get Mingle");
        share.putExtra(Intent.EXTRA_TEXT, "Welcome to Date Single Get Mingle! You can download app from Play Store:-https://play.google.com/store/apps/details?id=com.dating.datesinglegetmingle");
        startActivity(Intent.createChooser(share, "Share link!"));
    }

    private void launchMarket() {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.logical.neibar&hl=en");
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find Neibar app", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        Intent intent = new Intent(Setting.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
