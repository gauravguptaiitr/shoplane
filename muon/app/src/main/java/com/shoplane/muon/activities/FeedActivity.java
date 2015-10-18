package com.shoplane.muon.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.emilsjolander.components.StickyScrollViewItems.StickyScrollView;
import com.shoplane.muon.R;
import com.shoplane.muon.common.handler.SessionHandler;
import com.shoplane.muon.common.handler.VolleyRequestHandler;
import com.shoplane.muon.common.handler.WebSocketRequestHandler;


public class FeedActivity extends AppCompatActivity {

    private final String TAG = FeedActivity.class.getSimpleName();

    private String[] mServerUrls = {
            "http://imagizer.imageshack.us/v2/1080x720q90/673/fYKMcb.jpg",
            "http://imagizer.imageshack.us/v2/1080x720q90/661/b7B7Aa.jpg",
            "http://imagizer.imageshack.us/v2/1080x720q90/661/ydjkV8.jpg",
            "http://imagizer.imageshack.us/v2/1080x720q90/673/dUsfNv.jpg",
            "http://imagizer.imageshack.us/v2/1080x720q90/673/lQJ1xH.jpg",
            "http://imagizer.imageshack.us/v2/1080x720q90/673/j0tCUL.jpg",
            "http://imagizer.imageshack.us/v2/1080x720q90/901/yDq86D.jpg",
            "http://imagizer.imageshack.us/v2/1080x720q90/661/73Bv58.jpg"};

    private int[] mFeedImageViews = {
            R.id.adv_imageView1,
            R.id.adv_imageView2,
            R.id.adv_imageView3,
            R.id.adv_imageView4,
            R.id.adv_imageView5,
            R.id.adv_imageView6,
            R.id.adv_imageView7,
            R.id.adv_imageView8
    };

    private void openQueryActivity() {
        Intent queryActivityIntent = new Intent(this, QueryActivity.class);
        startActivity(queryActivityIntent);
        overridePendingTransition(R.xml.push_in_down, R.xml.push_out_down);
    }

    private void openWishlistActivity() {
        Intent wishlistActivityIntent = new Intent(this, WishListActivity.class);
        startActivity(wishlistActivityIntent);
    }

    private void fetchNewFeeds() {

        ImageLoader imageLoader = VolleyRequestHandler.getVolleyRequestHandlerInstance(this).
                getImageLoader();

        for (int i = 0; i < 8; i++) {
            NetworkImageView feedImgView = (NetworkImageView) findViewById(mFeedImageViews[i]);

            feedImgView.setDefaultImageResId(R.drawable.ic_no_image);
            feedImgView.setErrorImageResId(R.drawable.ic_image_error);
            feedImgView.setAdjustViewBounds(true);
            feedImgView.setImageUrl(mServerUrls[i], imageLoader);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View advView = findViewById(R.id.adv_image_layout1);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        advView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (height * 0.8)));


        final ImageView imageView = (ImageView) findViewById(R.id.adv_imageView1);
        final ScrollView scrollView = (StickyScrollView) findViewById(R.id.sticky_scrollview);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(
                new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {

                        int scrollY = scrollView.getScrollY();
                        imageView.setTranslationY(scrollY / 2);
                    }
                });

        scrollView.smoothScrollTo(0, 0);
        fetchNewFeeds();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Close session handler editor
        SessionHandler.getInstance(this).closeSessionHandler();

        // Close websocket connection when home activity is destroyed
        if(WebSocketRequestHandler.getInstance().getWebsocket() != null) {
            WebSocketRequestHandler.getInstance().getWebsocket().close();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                openQueryActivity();
                return true;
            case R.id.action_wishlist:
                openWishlistActivity();
                return true;
            case R.id.action_signout:
                //signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
