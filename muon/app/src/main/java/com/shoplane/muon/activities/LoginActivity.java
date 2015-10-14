package com.shoplane.muon.activities;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.shoplane.muon.R;
import com.shoplane.muon.common.Constants;
import com.shoplane.muon.common.handler.SessionHandler;
import com.shoplane.muon.interfaces.AuthStatus;

import java.io.IOException;
import java.util.Arrays;

public class LoginActivity extends Activity implements OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener, AuthStatus {
    // Tag for logcat
    private static final String TAG = "LoginActivity";
    private static final String USER_NOT_LOGGEDIN = "User not loggedin";
    private static final String GPLUS_CONNECTION_FAILED =
            "Error while connecting to play services. Please try again";


    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    //request code for sign in non negative
    private static final int RC_SIGN_IN = 0;
    private static final int RECOVERABLE_REQUEST_CODE = 1;
    // Flag to indicate existing intent
    private boolean mLoginIntentInProgress;

    // Flag to check Sign In button clicked
    private boolean mGPlusSignInClicked;
    private boolean mFbSignInClicked;
    private String mGPlusAccessToken;

    private ConnectionResult mConnectionResult;

    // Facebook Login
    private LoginButton mfbLoginButton;
    private CallbackManager mfbCallbackManager;

    private ProgressDialog mProgressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fb initialize
        FacebookSdk.sdkInitialize(LoginActivity.this);
        mfbCallbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        setupGplusSignIn();
        setupFBSignIn();

        // Setup button listeners
        Button skipLoginButton = (Button) findViewById(R.id.skip_login_button);
        skipLoginButton.setOnClickListener(this);

        mProgressdialog = new ProgressDialog(this);
        mProgressdialog.setIndeterminate(true);
    }

    private void setupGplusSignIn() {
        SignInButton btnSignIn = (SignInButton) findViewById(R.id.gplus_signin_button);
        // Button click listeners
        btnSignIn.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    private void setupFBSignIn() {
        mfbLoginButton = (LoginButton) findViewById(R.id.fb_signin_button);
        mfbLoginButton.setOnClickListener(this);
        mfbLoginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email",
                "user_location"));

        mfbLoginButton.registerCallback(mfbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Fb login successful
                String uid = loginResult.getAccessToken().getUserId();
                String fbAuthToken = loginResult.getAccessToken().getToken();
                // Call server with this information and get login token from it
                Log.e(TAG, fbAuthToken + uid);
                createSession(Constants.FB_LOGIN, uid, fbAuthToken);
            }

            @Override
            public void onCancel() {
                if (mProgressdialog.isShowing()) {
                    mProgressdialog.dismiss();
                }
                Toast.makeText(LoginActivity.this, "fb login attempt cancelled",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                if (mProgressdialog.isShowing()) {
                    mProgressdialog.dismiss();
                }
                Toast.makeText(LoginActivity.this, "fb login attempt failed ",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    /** GPLUS SIgnin **/

    // Sign-in into google
    private void signInWithGplus() {
        mGPlusSignInClicked = true;

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mGPlusSignInClicked = false;
        /* Now get the token using and async call*/
        Log.v(TAG, "onconnected success");
        LoginUserWithGPlus loginWithGPlus = new LoginUserWithGPlus(this);
        loginWithGPlus.execute();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.v(TAG, "onconnectedsuspend");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.v(TAG, "onconnectedfailed");

        if (mProgressdialog.isShowing()) {
            mProgressdialog.dismiss();
        }

        if (!result.hasResolution()) {
            Toast.makeText(this, GPLUS_CONNECTION_FAILED, Toast.LENGTH_LONG).show();
            mGPlusSignInClicked = false;
        }
    }

    @Override
    public void authStatus(boolean status) {
        if (mProgressdialog.isShowing()) {
            mProgressdialog.dismiss();
        }

        if (status) {
            Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show();
            openFeedActivity();
            finish();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_LONG).show();
        }
    }

    private class LoginUserWithGPlus extends AsyncTask<Void, Void, Void> {
        private final String TAG = LoginUserWithGPlus.class.getSimpleName();

        private Context mContext;
        private boolean mSignInError;
        private AlertDialog.Builder mAlertDialogBuilder;

        public LoginUserWithGPlus(Context context) {
            this.mContext = context;
            this.mSignInError = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mGPlusAccessToken = null;
            try {
                String accountName = Plus.AccountApi.getAccountName(mGoogleApiClient);
                //Account account = new Account(accountName, GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                //String scopes = "audience:server:client_id:" + SERVER_CLIENT_ID;
                String scope = "oauth2:" + Scopes.PLUS_LOGIN + " " +
                        "https://www.googleapis.com/auth/userinfo.email" +
                        " https://www.googleapis.com/auth/userinfo.profile";
                mGPlusAccessToken = GoogleAuthUtil.getToken(mContext, accountName, scope);
            } catch (IOException ioe) {
                mSignInError = true;
                Log.e(TAG, "ioexception");
            } catch (UserRecoverableAuthException urae) {
                //mSignInError = true;
                //Recover
                Log.e(TAG, "Userrecexp");
                startActivityForResult(urae.getIntent(), RECOVERABLE_REQUEST_CODE);
            } catch (GoogleAuthException authEx) {
                mSignInError = true;
                Log.e(TAG, "GoogleAuthExcp");
            } catch (Exception e) {
                Log.e(TAG, "Exception");
                mSignInError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void none) {
            super.onPostExecute(null);

            if (mSignInError) {
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
                                finish();
                            }
                        });

                AlertDialog alertDialog = mAlertDialogBuilder.create();
                alertDialog.show();
            } else {
                Log.e("AUthtokengplus", mGPlusAccessToken);
                //Toast.makeText(mContext, mGPlusAccessToken, Toast.LENGTH_LONG).show();
            }
        }
    }

    /** GPLUS SIgnin end**/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mFbSignInClicked) {
            mfbCallbackManager.onActivityResult(requestCode, resultCode, data);
            mFbSignInClicked = false;
        }

        if (mGPlusSignInClicked) {
            if (requestCode == RC_SIGN_IN) {
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, GPLUS_CONNECTION_FAILED, Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == RECOVERABLE_REQUEST_CODE && resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                mGPlusAccessToken = extra.getString("authtoken");
            }
            mGPlusSignInClicked = false;
            createSession(1, "", mGPlusAccessToken);
        }
    }

    // 3 modes for signing in
    // 1 for UserPass Login
    // 2 for GPlus login
    // 3 for FB login
    private void createSession(int mode, String userid, String userAccessToken) {
        SessionHandler.getInstance(this).createUserLoginSession(mode,
                userid, userAccessToken,this);
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    // Button onclick listener
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gplus_signin_button:
                mProgressdialog.setMessage("Logging with GPlus");
                mProgressdialog.show();
                signInWithGplus();
                break;
            case R.id.fb_signin_button:
                //signInWithFb();
                mProgressdialog.setMessage("Logging with FaceBook");
                mProgressdialog.show();
                mFbSignInClicked = true;
                break;
            case R.id.skip_login_button:
                openFeedActivity();
                finish();
                break;
        }
    }

    private void openFeedActivity() {
        Intent feedActivityIntent = new Intent(this, FeedActivity.class);
        startActivity(feedActivityIntent);
    }
}