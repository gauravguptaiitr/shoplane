package com.shoplane.muon.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.shoplane.muon.R;

import java.util.List;

/**
 * Created by ravmon on 18/8/15.
 */
public class CatalogueItem {
    private long mCuid;
    private long mVarId;
    private String mStyle;
    private String mDesc;
    private String mStyleTips;
    private String mItemUrl;
    private String mPrimaryImageUrl;
    private List<String> mAltImageUrl;
    private String mTitle;
    private String mBrand;
    private List<String> mSizes;
    private List<String> mColors;
    private long mPrice;


    public CatalogueItem(long mCuid, long mVarId, String mStyle, String mDesc, String mStyleTips,
                         String mItemUrl, String mPrimaryImageUrl, List<String> mAltImageUrl,
                         String mTitle, String mBrand, List<String> mSizes, List<String> mColors,
                         long mPrice) {
        this.mCuid = mCuid;
        this.mVarId = mVarId;
        this.mStyle = mStyle;
        this.mDesc = mDesc;
        this.mStyleTips = mStyleTips;
        this.mItemUrl = mItemUrl;
        this.mPrimaryImageUrl = mPrimaryImageUrl;
        this.mAltImageUrl = mAltImageUrl;
        this.mTitle = mTitle;
        this.mBrand = mBrand;
        this.mSizes = mSizes;
        this.mColors = mColors;
        this.mPrice = mPrice;
    }

    public long getmCuid() {
        return mCuid;
    }

    public String getmStyle() {
        return mStyle;
    }

    public long getmVarId() {
        return mVarId;
    }

    public String getmDesc() {
        return mDesc;
    }

    public String getmStyleTips() {
        return mStyleTips;
    }

    public String getmItemUrl() {
        return mItemUrl;
    }

    public String getmPrimaryImageUrl() {
        return mPrimaryImageUrl;
    }

    public List<String> getmAltImageUrl() {
        return mAltImageUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmBrand() {
        return mBrand;
    }

    public List<String> getmSizes() {
        return mSizes;
    }

    public List<String> getmColors() {
        return mColors;
    }

    public long getmPrice() {
        return mPrice;
    }

    public void setmPrice(long mPrice) {
        this.mPrice = mPrice;
    }

    public void setmColors(List<String> mColors) {
        this.mColors = mColors;
    }

    public void setmSizes(List<String> mSizes) {
        this.mSizes = mSizes;
    }

    public void setmBrand(String mBrand) {
        this.mBrand = mBrand;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmAltImageUrl(List<String> mAltImageUrl) {
        this.mAltImageUrl = mAltImageUrl;
    }

    public void setmPrimaryImageUrl(String mPrimaryImageUrl) {
        this.mPrimaryImageUrl = mPrimaryImageUrl;
    }

    public void setmItemUrl(String mItemUrl) {
        this.mItemUrl = mItemUrl;
    }

    public void setmStyleTips(String mStyleTips) {
        this.mStyleTips = mStyleTips;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public void setmStyle(String mStyle) {
        this.mStyle = mStyle;
    }

    public void setmVarId(long mVarId) {
        this.mVarId = mVarId;
    }

    public void setmCuid(long mCuid) {
        this.mCuid = mCuid;
    }
}
