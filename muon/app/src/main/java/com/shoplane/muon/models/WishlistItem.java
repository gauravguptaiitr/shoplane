package com.shoplane.muon.models;

import android.graphics.Bitmap;

/**
 * Created by ravmon on 28/8/15.
 */
public class WishlistItem {
    private long mCuid;
    private String mTitle;
    private String mImageUrl;
    private String mPrice;
    private String mItemUrl;


    public WishlistItem(long mCuid, String mTitle, String mImageUrl, String mPrice,
                        String mItemUrl) {
        this.mCuid = mCuid;
        this.mTitle = mTitle;
        this.mImageUrl = mImageUrl;
        this.mPrice = mPrice;
        this.mItemUrl = mItemUrl;
    }

    public long getCuid() {
        return mCuid;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getItemUrl() {
        return mItemUrl;
    }
}
