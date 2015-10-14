package com.shoplane.muon.common.handler;

/**
 * Created by ravmon on 14/8/15.
 */
public interface AsyncTaskHandler {
    // handles event when async task is cancelled
    void onTaskCancelled();

    // handles post execution
    void onPostExecute(boolean isSuccess, String exceptionMsg);
}
