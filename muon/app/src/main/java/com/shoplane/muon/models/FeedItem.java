package com.shoplane.muon.models;

/**
 * Created by ravmon on 18/8/15.
 */
public class FeedItem {
    private long mFeedUid;
    private String mDesc;
    private String mImageUrl;

    public FeedItem(long mFeedUid, String mDesc, String mImageUrl) {
        this.mFeedUid = mFeedUid;
        this.mDesc = mDesc;
        this.mImageUrl = mImageUrl;
    }

    public long getFeedUid() {
        return mFeedUid;
    }

    public String getDesc() {
        return mDesc;
    }

    public String getImageUrl() {
        return mImageUrl;
    }
}
