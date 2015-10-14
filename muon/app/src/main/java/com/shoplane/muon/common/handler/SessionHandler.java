package com.shoplane.muon.common.handler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.shoplane.muon.common.Constants;
import com.shoplane.muon.common.service.DeleteRequestService;
import com.shoplane.muon.interfaces.AuthStatus;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import java.util.Map;

/**
 * Created by ravmon on 24/8/15.
 */
public class SessionHandler {
    private static final String TAG = SessionHandler.class.getSimpleName();


    private static SessionHandler mSessionHandler;
    private boolean mIsUserLoggedIn;
    private String mServerAuthToken;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;
    Context mContext;


    private SessionHandler(Context context) {
        this.mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(Constants.SHARED_PREF_FILE_NAME,
                Constants.SHAREF_PREF_PRIVATE_MODE);
        mEditor = mSharedPreferences.edit();
        mIsUserLoggedIn = false;
        mServerAuthToken = "";
    }

    public static SessionHandler getInstance(Context context) {
        if (null == mSessionHandler) {
            mSessionHandler = new SessionHandler(context);
        }
        return mSessionHandler;
    }

    public boolean checkLoginSessionOnStart(){
        // Get authtoken
        mServerAuthToken = mSharedPreferences.getString(Constants.SHARED_PREF_USERAUTHTOKEN, null);
        if (null == mServerAuthToken ||
                mServerAuthToken.trim().equalsIgnoreCase(Constants.EMPTY_STRING)) {
            mIsUserLoggedIn = false;
        } else{
            WebSocketRequestHandler.getInstance(mContext).connectToServer(mServerAuthToken);
            if (WebSocketRequestHandler.getInstance(mContext).isConnectedToServer()) {
                mIsUserLoggedIn = true;
            } else {
                mIsUserLoggedIn = false;
            }
        }

        return mIsUserLoggedIn;
    }

    public boolean isUserLoggedIn() {
        return mIsUserLoggedIn;
    }

    //Create login session
    public void createUserLoginSession(int mode, String uid, String authToken,
                                          AuthStatus status){

        if (Constants.FB_LOGIN == mode) {
            // FB login
            Map<String, String> loginRequest = new HashMap<String, String>();
            loginRequest.put("fbuid", uid);
            loginRequest.put("fbauthtoken", authToken);
            String json = new GsonBuilder().create().toJson(loginRequest, Map.class);
            Log.e(TAG, "FB login request " + json);

            new InitiateUserLogin(status).executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    "http://192.168.0.107:9000/v1/oauth/with-facebook",
                    json);
        }

    }

    public void logoutUser(){
        mEditor.clear();
        mEditor.commit();
    }

    public void closeSessionHandler() {
        mEditor.clear();
    }

    private class InitiateUserLogin extends AsyncTask<String, Void, String>{

        AuthStatus authStatus;
        InitiateUserLogin(AuthStatus status){
            this.authStatus = status;
        }

        @Override
        protected String doInBackground(String ...uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                HttpPost httpPost = new HttpPost(uri[0]);
                httpPost.setEntity(new StringEntity(uri[1]));
                httpPost.setHeader("Content-type", "application/json");

                Log.d(TAG, "Call " + uri[0]);
                response = httpclient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                Log.d(TAG, "Call " + uri[0] + " Status Code = " + statusLine.getStatusCode());
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                    Log.d(TAG, "Call to " + uri[0] + " Response body = " + responseString);
                }
                else if (statusLine.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                    Log.e(TAG, "Call to " + uri[0] + " received no content.");
                }
                else {
                    Log.e(TAG, "Exception while calling  " + uri[0] + " " +
                            statusLine.getReasonPhrase());
                }

            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "UnsupportedEncodingException while calling  " + uri[0] + " " + e);
            } catch (ClientProtocolException e) {
                Log.e(TAG, "ClientProtocolException while calling  " + uri[0] + " " + e);
            } catch (IOException e) {
                Log.e(TAG, "IOException while calling  " + uri[0] + " " + e);
            } catch(Exception e){
                Log.e(TAG, "Exception while calling  " + uri[0] + " " + e);
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null == result) {
                Log.e(TAG, "Server token is null");
                authStatus.authStatus(false);
                return;
            }

            JsonObject jobj = new Gson().fromJson(result, JsonObject.class);

            mServerAuthToken =  jobj.get("token").toString();

            Log.e(TAG, mServerAuthToken);

            if (null == mServerAuthToken || mServerAuthToken.trim().
                    equalsIgnoreCase(Constants.EMPTY_STRING)) {
                Log.d(TAG, "Sever auth token is null");
                authStatus.authStatus(false);
                return;
            }

            // Create websocket connection
            WebSocketRequestHandler.getInstance(mContext).connectToServer(mServerAuthToken);
            if (WebSocketRequestHandler.getInstance(mContext).isConnectedToServer()) {
                // Storing server token in sharedpreference
                mEditor.putString(Constants.SHARED_PREF_USERAUTHTOKEN, mServerAuthToken);
                // commit changes
                mEditor.commit();
                mIsUserLoggedIn = true;
                authStatus.authStatus(true);
            } else {
                Log.d(TAG, "Websocket connection failed");
                mIsUserLoggedIn = false;

                authStatus.authStatus(false);
            }
        }
    }

}

