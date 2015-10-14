package com.shoplane.muon.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.shoplane.muon.R;
import com.shoplane.muon.common.handler.SessionHandler;
import com.shoplane.muon.common.utils.NetworkConnectionUtil;

public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();

    // Time to display splash in mili seconds
    private final int SPLASH_DISPLAY_TIME = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (!(NetworkConnectionUtil.checkNetworkConnection(this))) {
            alertNoConnection();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkIfUserLoggedIn();
                }
            }, SPLASH_DISPLAY_TIME);
        }
    }

    private void checkNetworkConnection() {
        if (!(NetworkConnectionUtil.checkNetworkConnection(this))) {
            alertNoConnection();
        } else {
            checkIfUserLoggedIn();
        }
    }

    private void alertNoConnection() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("No Internet Connection");

        alertDialogBuilder
                .setMessage("Please enable network connection and Retry!!!")
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        checkNetworkConnection();
                    }
                })
                .setNegativeButton("Close App", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        SplashActivity.this.finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void openLoginActivity() {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        startActivity(loginActivityIntent);
        finish();
    }

    private void openFeedActivity() {
        Intent feedActivityIntent = new Intent(this, FeedActivity.class);
        startActivity(feedActivityIntent);
        finish();
    }

    private void checkIfUserLoggedIn() {
        // Check for credentials
        if (SessionHandler.getInstance(this).checkLoginSessionOnStart()) {
            openFeedActivity();
        } else {
            // login not successful
            openLoginActivity();
        }
    }
}
