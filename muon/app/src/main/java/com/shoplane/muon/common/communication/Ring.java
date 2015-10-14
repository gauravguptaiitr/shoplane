package com.shoplane.muon.common.communication;

import android.app.Activity;

import com.shoplane.muon.interfaces.UpdateUITask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ravmon on 9/10/15.
 */
public class Ring {

    private int mMaxActiveRequest;
    private int mSizeOfRing;
    private List<Integer> mResonseRing;
    private int mCurrentIndex;
    private ConcurrentHashMap<Integer, WeakReference<UpdateUITask>> mUpdateTaskMapForGetRequest;
    private ConcurrentHashMap<String, WeakReference<UpdateUITask>> mUpdateTaskMapForPostRequest;

    public Ring(int maxRequests) {
        this.mMaxActiveRequest = maxRequests;
        this.mSizeOfRing = 2 * maxRequests;
        this.mResonseRing = new ArrayList<>();
        this.mUpdateTaskMapForGetRequest = new ConcurrentHashMap<>();
        this.mUpdateTaskMapForPostRequest = new ConcurrentHashMap<>();
        this.mCurrentIndex = 0;
    }

    public boolean isRequestActive(int index) {
        return true;
    }


    private void registerRequest() {

    }

    private void clearRequest() {

    }

    public WeakReference<UpdateUITask> getTaskToUpdateUIForGetRequest(int reqId) {
        return mUpdateTaskMapForGetRequest.get(reqId);
    }

    public WeakReference<UpdateUITask> getTaskToUpdateUIForPostRequest(String messageType) {
        messageType = messageType.trim().toLowerCase();
        return mUpdateTaskMapForPostRequest.get(messageType);
    }

    public void addTaskToUpdateUIForGetRequest(int reqId, WeakReference<UpdateUITask> actWeakRef) {
        mUpdateTaskMapForGetRequest.put(reqId, actWeakRef);
    }

    public void addTaskToUpdateUIForPostRequest(String messageType,
                                                        WeakReference<UpdateUITask> actWeakRef) {
        messageType = messageType.trim().toLowerCase();
        mUpdateTaskMapForPostRequest.put(messageType, actWeakRef);
    }
}
