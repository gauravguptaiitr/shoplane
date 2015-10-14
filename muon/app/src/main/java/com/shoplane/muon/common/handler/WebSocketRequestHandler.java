package com.shoplane.muon.common.handler;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.callback.WritableCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.shoplane.muon.activities.QueryActivity;
import com.shoplane.muon.common.Constants;
import com.shoplane.muon.common.communication.Ring;
import com.shoplane.muon.common.utils.JsonReadUtil;
import com.shoplane.muon.interfaces.UpdateUITask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by ravmon on 24/8/15.
 */
public class WebSocketRequestHandler {
    private static final String TAG = WebSocketRequestHandler.class.getSimpleName();
    private static final String WEB_SOCKET_CONNECTION_URL = "192.168.0.107";
    private static final int SERVER_PORT = 9000;
    private static final String CONNECTION_TOKEN_FORMAT = "/stream?accessToken=";


    private AsyncHttpClient mAsyncHttpClient;
    private boolean mIsConnectedToServer;
    private WebSocket mWebsocket;
    private Ring mRing;
    private Context mContext;
    private String mAuthServerToken;

    private AsyncHttpClient.WebSocketConnectCallback mWebSocketConnectCallback;
    private static WebSocketRequestHandler mWebSocketHandlerInstance;

    public static WebSocketRequestHandler getInstance(Context context) {
        if (null == mWebSocketHandlerInstance) {
            mWebSocketHandlerInstance = new WebSocketRequestHandler(context);
        }
        return mWebSocketHandlerInstance;
    }

    private WebSocketRequestHandler(final Context context) {
        mIsConnectedToServer = false;
        mWebsocket = null;
        mRing = new Ring(4);
        this.mContext = context;
        this.mAuthServerToken = null;
    }

    public boolean isConnectedToServer() {
        return mIsConnectedToServer;
    }

    public void connectToServer(String authToken) {
        String hostAddr = "ws://" + WEB_SOCKET_CONNECTION_URL + ":" + SERVER_PORT +
                CONNECTION_TOKEN_FORMAT + authToken;
        mAuthServerToken = authToken;

        mWebSocketConnectCallback = new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (ex != null) {
                    Log.e(TAG, "Failed to connect to Server");
                    mIsConnectedToServer= false;
                    return;
                }
                mIsConnectedToServer = true;
                mWebsocket = webSocket;

                webSocket.setStringCallback(new RingStringCallback());

                webSocket.setClosedCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception e) {
                        Log.d(TAG, "websocket connection closed " + e);
                        mIsConnectedToServer = false;
                    }
                });

                webSocket.setEndCallback(new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception e) {
                        Log.d(TAG, "websocket connection ended " + e);
                        mIsConnectedToServer = false;
                    }
                });

            }
        };

        mAsyncHttpClient = AsyncHttpClient.getDefaultInstance();
        mAsyncHttpClient.websocket(hostAddr, null, mWebSocketConnectCallback);
    }

    public WebSocket getWebsocket() {
        return mWebsocket;
    }

    public void createAndSendGetRequestToServer(String request, Activity weakRef) {

    }

    public void createAndSendPostRequestToServer(String request,
                                                 WeakReference<UpdateUITask> actWeakRef,
                                                 String queryPath) {

        if (!mIsConnectedToServer) {
            connectToServer(mAuthServerToken);
        }

        if(mIsConnectedToServer) {
            mRing.addTaskToUpdateUIForPostRequest(queryPath.trim().toLowerCase(), actWeakRef);
            mWebsocket.send(request);
        } else {
            Log.e(TAG, "Failed to send post request to server");
        }
    }

    private void processPostResponse(final JSONObject jsonResponse, final String messageType) {

        if (messageType.trim().equalsIgnoreCase(Constants.QUERY_SUGGESTION_STYLES_PATH)) {
            // check if activity exists or not

            final UpdateUITask task =  mRing.getTaskToUpdateUIForPostRequest(
                    Constants.QUERY_SUGGESTION_STYLES_PATH).get();
            if (task != null && task.getActivityAvailableStatus()) {

                //run update task asociated with the request
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("UI thread", "Run query activity update task on UI thread");
                        task.updateUI(jsonResponse);
                    }
                });
            }

        }
    }

    private class RingStringCallback implements WebSocket.StringCallback {

        @Override
        public void onStringAvailable(String response) {
            // get requestid from string
            //final int reqId;
            JSONObject responseObject;
            try {
                responseObject = new JSONObject(response);
                String type = responseObject.getString("messageType");
                if (type != null) {
                    processPostResponse(responseObject, type);
                    return;
                }
            } catch (JSONException je) {
                Log.e(TAG, "Failed to get messagetype");
                return;
            }

            // check request type to see if it is a message from server



            /*if (0 < reqId || 8 > reqId) {
                Log.e(TAG, "Response do not have valid request id");
                return;
            }

            // check if request is active
            if (!mRing.isRequestActive(reqId)) {
                Log.d(TAG, "Reqest is not active");
                return;
            }

            //run update task asociated with the request
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.d("UI thread", "Run update task on UI thread");
                    UpdateUITask updateUITask= mRing.getTaskToUpdateUI(reqId);
                    updateUITask.updateUI();
                }
            });*/
        }
    }

}
