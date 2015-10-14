package com.shoplane.muon.common.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.koushikdutta.async.http.WebSocket;
import com.koushikdutta.async.http.server.AsyncHttpServer;
//import com.shoplane.muon.common.handler.WebSocketHandler;

import java.util.List;

/**
 * Created by ravmon on 27/9/15.
 */
public class WebSocketService extends Service {
    private static final String TAG = WebSocketService.class.getSimpleName();
    private static final String ACTION_PING = "ACTION_PING";
    private static final String ACTION_CONNECT = "ACTION_CONNECT";
    private static final String ACTION_STOP = "ACTION_STOP";

    private boolean mStop;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private List<String> mCancelled;
    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // do work

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    public static Intent startIntent(Context context){
        Intent intent = new Intent(context, WebSocketService.class);
        intent.setAction(ACTION_CONNECT);
        return intent;
    }

    public static Intent pingIntent(Context context){
        Intent intent = new Intent(context, WebSocketService.class);
        intent.setAction(ACTION_PING);
        return intent;
    }

    public static Intent closeIntent(Context context){
        Intent intent = new Intent(context, WebSocketService.class);
        intent.setAction(ACTION_STOP);
        return intent;
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("WebSocketServiceStartArgs",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Start WebSocket Service");

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("WebSocket", "Destroying Service " + this.toString());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
