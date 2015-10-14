package com.shoplane.muon.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shoplane.muon.R;
import com.shoplane.muon.adapters.FilterPagerAdapter;
import com.shoplane.muon.adapters.ItemDetailImagePagerAdapter;
import com.shoplane.muon.common.helper.WishlistHelper;
import com.shoplane.muon.models.CatalogueItem;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailActivity extends AppCompatActivity {

    private long mItemId;
    private List<String> mImageUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mImageUrls =  new ArrayList<>();

        ViewPager itemImagePager = (ViewPager) findViewById(R.id.item_image_pager);
        itemImagePager.setOffscreenPageLimit(5);

        // Set proper size for search list columns
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                (int) (screenHeight * 0.65));
        itemImagePager.setLayoutParams(layoutParams);

        mItemId = 0L;
        ItemDetailImagePagerAdapter itemImagePagerAdapter = new ItemDetailImagePagerAdapter(
                getSupportFragmentManager(), mItemId, mImageUrls);
        itemImagePager.setAdapter(itemImagePagerAdapter);

        final ImageButton openItemInBrowser = (ImageButton) findViewById(R.id.open_browser_button);
        openItemInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openItemInBrowser(mItemId);
            }
        });

        final ImageButton addToWishlist = (ImageButton) findViewById(R.id.add_wishlist_button);
        addToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //WishlistHelper.getWishlist().addItem(new CatalogueItem(0l, "fromDetail", "abc"));
            }
        });

        getItemFullDataFomServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_back:
                onBackPressed();
                return true;
            case R.id.action_wishlist:
                openWishlistActivity();
                return true;
            case android.R.id.home:
                openFeedActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    private void openItemInBrowser(Long itemId) {
        String itemPage = "www.google.com";
        if (!itemPage.startsWith("http://") && !itemPage.startsWith("https://"))
            itemPage = "http://" + itemPage;

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(itemPage));
            startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ItemDetailActivity.this, "Please install a webbrowser",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void getItemFullDataFomServer() {
        mImageUrls.add("http://imagizer.imageshack.us/v2/1080x720q90/908/I41ZTq.jpg");
        mImageUrls.add("http://imagizer.imageshack.us/v2/1080x720q90/908/I41ZTq.jpg");
        mImageUrls.add("http://imagizer.imageshack.us/v2/1080x720q90/908/I41ZTq.jpg");
        mImageUrls.add("http://imagizer.imageshack.us/v2/1080x720q90/908/I41ZTq.jpg");
        mImageUrls.add("http://imagizer.imageshack.us/v2/1080x720q90/908/I41ZTq.jpg");
    }

    private void openWishlistActivity() {
        Intent wishlistActivityIntent = new Intent(this, WishListActivity.class);
        startActivity(wishlistActivityIntent);
    }

    private void openFeedActivity() {
        Intent feedActivityIntent = new Intent(this, FeedActivity.class);
        startActivity(feedActivityIntent);
    }
}
