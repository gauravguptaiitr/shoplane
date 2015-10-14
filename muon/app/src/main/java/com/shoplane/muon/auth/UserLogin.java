package com.shoplane.muon.auth;

import android.os.AsyncTask;

import com.shoplane.muon.common.handler.AsyncTaskHandler;

/**
 * Created by ravmon on 15/8/15.
 */
public class UserLogin extends AsyncTask<String, Void, Boolean> {

    private AsyncTaskHandler parentHandler;
    private boolean error = false;
    private boolean exceptionOccured = false;
    private String exceptionMsg = null;
    private boolean isSuccess = false;

    public UserLogin(AsyncTaskHandler parentHandler) {
        this.parentHandler = parentHandler;
    }

    @Override
    protected void onCancelled() {
        parentHandler.onTaskCancelled();
    }
    @Override
    protected void onCancelled(Boolean result) {
        parentHandler.onTaskCancelled();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        parentHandler.onPostExecute(result, exceptionMsg);
    }

    @Override
    protected Boolean doInBackground(String... loginParams) {
        // TODO: Create connection with server and login user
        return true;
    }



}
