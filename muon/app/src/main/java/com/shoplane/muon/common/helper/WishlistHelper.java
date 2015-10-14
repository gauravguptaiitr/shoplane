package com.shoplane.muon.common.helper;

import android.view.View;

import com.shoplane.muon.common.service.DeleteRequestService;
import com.shoplane.muon.common.service.GetRequestService;
import com.shoplane.muon.common.service.UpdateRequestService;
import com.shoplane.muon.models.CatalogueItem;
import com.shoplane.muon.models.WishlistItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ravmon on 2/9/15.
 */
public class WishlistHelper {
    private static WishlistHelper wishlistHelperInstance;
    private long mWishlistUid;
    private Map<Long, WishlistItem> mWishlistMap;
    private int mTotalItems;
    private static final String mServerUrl =
            "http://imagizer.imageshack.us/v2/1080x720q90/908/I41ZTq.jpg";

    public static WishlistHelper getWishlist() {
        // get id from token or something
        if (null == wishlistHelperInstance) {
            wishlistHelperInstance = new WishlistHelper();
        }
        return wishlistHelperInstance;
    }

    private WishlistHelper() {
        // TODO: Get data from server with userid
        mWishlistUid = 1L;
        mTotalItems = 0;
        mWishlistMap = new HashMap<>();

        updateWishlistMapFromServer();

    }

    public void addItem(final CatalogueItem catalogueItem) {
       // WishlistItem wishlistItem = new WishlistItem(mTotalItems,
               // catalogueItem.getTitle(),catalogueItem.getTitle(),mServerUrl,
               // mServerUrl);
        //if (!mWishlistMap.containsKey(wishlistItem.getCUid())) {
           // mWishlistMap.put(wishlistItem.getCUid(), wishlistItem);
       // }
       // mTotalItems++;
        // Update wishlist in the server
        // AddToWishListService
    }

    public void removeItem(final long wishlistItemUid) {
        if (mWishlistMap.containsKey(wishlistItemUid)) {
            mWishlistMap.remove(wishlistItemUid);
        }
        mTotalItems--;
        // Remove item from wishlist in server
        // RemoveFromWishlistService
    }

    public void clear() {
        mWishlistMap.clear();
        mTotalItems = 0;
    }

    public int getTotalItems() {
        return mTotalItems;
    }

    // Service to delete item from cart on server
    private class RemoveFromWishlistService extends DeleteRequestService {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    // Service to add item to cart on server
    private class AddToWishlistService extends UpdateRequestService {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    // Service to add item to cart on server
    private class GetWishlistService extends GetRequestService {
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    public List<WishlistItem> getWishlistItems(){
        List<WishlistItem> wishlistItems = new ArrayList<>();
        if (null != mWishlistMap){
            wishlistItems.clear();
            wishlistItems.addAll(mWishlistMap.values());
        }
        return wishlistItems;
    }

    private void updateWishlistMapFromServer() {
        // Get wishlist from server
        // GetWishlistServiceprivate long mItemUid;
    }
}
