package com.shoplane.muon.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.shoplane.muon.activities.FeedActivity;

import java.io.IOException;

/**
 * Created by ravmon on 8/9/15.
 */
public class LoginUserWithGPlus extends AsyncTask<Void, Void, String> {
    private static final String TAG = LoginUserWithGPlus.class.getSimpleName();

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private boolean mSignInError;
    private AlertDialog.Builder mAlertDialogBuilder;
    private ProgressDialog mProgressdialog;
    private String mGPlusAccessToken;

    public LoginUserWithGPlus(Context context, GoogleApiClient mGoogleApiClient) {
        this.mContext = context;
        this.mGoogleApiClient = mGoogleApiClient;
        this.mSignInError = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressdialog = new ProgressDialog(mContext);
        mProgressdialog.setMessage("Logging with GPlus...");
        mProgressdialog.setIndeterminate(true);
        mProgressdialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
       /* mGPlusAccessToken = null;
        try {
            String accountname = Plus.AccountApi.getAccountName(mGoogleApiClient);
            String scope = "oauth2:" + Scopes.PLUS_LOGIN + " " +
                    "https://www.googleapis.com/auth/userinfo.email" +
                    " https://www.googleapis.com/auth/plus.profile.agerange.read";
            mGPlusAccessToken = GoogleAuthUtil.getToken(mContext, accountname, scope);
        } catch (IOException ioe) {
            mSignInError = true;
            Log.e(TAG, "ioexception");
        } catch (UserRecoverableAuthException urae) {
            //mSignInError = true;
            //Recover
            Log.e(TAG, "Userrecexp");
            //mContext.startActivityForResult(e.getIntent(), RECOVERABLE_REQUEST_CODE);
        } catch (GoogleAuthException authEx) {
            mSignInError = true;
            Log.e(TAG, "GoogleAuthExcp");
        } catch (Exception e) {
            Log.e(TAG, "Exception");
            mSignInError = true;
        }
        // Connect to server with authtoken and initiate session
        return "true";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (mProgressdialog.isShowing()) {
            mProgressdialog.dismiss();
        }
        if (true == mSignInError) {
            mAlertDialogBuilder = new AlertDialog.Builder(mContext);
            mAlertDialogBuilder.setTitle("Sign In Error");
            mAlertDialogBuilder.setCancelable(false);
            mAlertDialogBuilder.setMessage("Login failed. Try Again.");
            mAlertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Do nothing remain on login activity
                        }
                    });
            mAlertDialogBuilder.setNegativeButton("Later",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent feedActivityIntent = new Intent(mContext,
                                    FeedActivity.class);
                            mContext.startActivity(feedActivityIntent);
                            ((Activity) mContext).finish();
                        }
                    });

            AlertDialog alertDialog = mAlertDialogBuilder.create();
            alertDialog.show();
        } else {
            Toast.makeText(mContext, mGPlusAccessToken, Toast.LENGTH_LONG).show();
        } */
        return "a";
    }
}
