package com.shoplane.muon.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.shoplane.muon.R;
import com.shoplane.muon.adapters.WishlistAdapter;
import com.shoplane.muon.common.Constants;
import com.shoplane.muon.common.helper.WishlistHelper;
import com.shoplane.muon.common.utils.userinterface.ItemDecorationUtil;
import com.shoplane.muon.models.WishlistItem;

import java.util.ArrayList;
import java.util.List;

public class WishListActivity extends AppCompatActivity{
    private static final String TAG = WishListActivity.class.getSimpleName();

    private static List<WishlistItem> mWishlistItems = new ArrayList<WishlistItem>();

    private WishlistAdapter mWishlistAdapter;
    private RecyclerView mWishlistRecyclerView;
    private LinearLayoutManager mWishlistLayoutManager;
    private RecyclerView.ItemDecoration mWishlistItemDecorator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mWishlistRecyclerView = (RecyclerView) findViewById(R.id.wishlist_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mWishlistRecyclerView.setHasFixedSize(true);

        // Item decoration
        mWishlistItemDecorator = new ItemDecorationUtil(10, true, true);
        mWishlistRecyclerView.addItemDecoration(mWishlistItemDecorator);

        // Layout manager for Recycler view
        mWishlistLayoutManager = new LinearLayoutManager(this);
        mWishlistLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mWishlistRecyclerView.setLayoutManager(mWishlistLayoutManager);

        mWishlistItems = WishlistHelper.getWishlist().getWishlistItems();
        mWishlistAdapter = new WishlistAdapter(this, mWishlistItems);

        // Set wishlist item click listener
        mWishlistAdapter.setOnItemClickListener(
                new WishlistAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        openItemDetailActivity();
                    }
                });

        mWishlistRecyclerView.setAdapter(mWishlistAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wishlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                openQueryActivity();
                return true;
            case android.R.id.home:
                openFeedActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openQueryActivity() {
        Intent queryActivityIntent = new Intent(this, QueryActivity.class);
        queryActivityIntent.putExtra(Constants.OPEN_QUERY_BOX, true);
        startActivity(queryActivityIntent);
    }

    private void openFeedActivity() {
        Intent feedActivityIntent = new Intent(this, FeedActivity.class);
        startActivity(feedActivityIntent);
    }

    private void openItemDetailActivity() {
        Intent itemDetailActivityIntent = new Intent(this, ItemDetailActivity.class);
        startActivity(itemDetailActivityIntent);
        overridePendingTransition(0, 0);
    }
}