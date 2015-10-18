package com.shoplane.muon.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shoplane.muon.R;
import com.shoplane.muon.adapters.SearchAdapter;
import com.shoplane.muon.common.Constants;
import com.shoplane.muon.common.handler.WebSocketRequestHandler;
import com.shoplane.muon.interfaces.UpdateUITask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements UpdateUITask {

    private final String TAG = SearchActivity.class.getSimpleName();

    private List<JSONObject> mSearchItemList;
    private SearchAdapter mSearchAdapter;
    private RecyclerView mSearchRecyclerView;
    private LinearLayoutManager mSearchLayoutManager;
    private RecyclerView.ItemDecoration mSearchItemDecorator;
    private ProgressDialog mProgressDialog;
    WeakReference<UpdateUITask> mActRef;
    private boolean mIsActivityAvailable;

    // Snapping variables
    private float mItemWidth;
    private float mPadding;
    private float mPixelMovedX;
    private int mLastPosition;
    private static final String BUNDLE_LIST_PIXELS = "mixelMovedX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mActRef = new WeakReference<UpdateUITask>(this);
        mIsActivityAvailable = true;

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Handle intent
        mSearchRecyclerView = (RecyclerView) findViewById(R.id.search_list_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mSearchRecyclerView.setHasFixedSize(true);

        // Item decoration
        // mSearchItemDecorator = new ItemDecorationUtil(10, false, false);
        // mSearchRecyclerView.addItemDecoration(mSearchItemDecorator);

        // Item animation

        // Layout manager for Recycler view
        mSearchLayoutManager = new LinearLayoutManager(this);
        mSearchLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mSearchLayoutManager.scrollToPosition(0);
        mSearchRecyclerView.setLayoutManager(mSearchLayoutManager);

        mSearchItemList = new ArrayList<>();

        // Set proper size for search list columns
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        int screenWidth = displaymetrics.widthPixels;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)
                (screenWidth * 0.9), (int) (screenHeight * 0.8));

        mItemWidth = (int) (screenWidth * 0.9);
        //mItemWidth = findViewById(R.id.search_list_frame).getWidth();
        mPadding = (screenWidth - mItemWidth) / 2;
        mPixelMovedX = 0;

        mSearchAdapter = new SearchAdapter(this, mSearchItemList, layoutParams, screenWidth,
                screenHeight);

        ImageView searchListImageview = (ImageView) findViewById(R.id.search_list_imageview);
        searchListImageview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.
                FILL_PARENT, (int) (screenHeight * 0.03)));

        // Set feed item click listener
        mSearchAdapter.setOnItemClickListener(
                new SearchAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // bundle json object representing catalogue item
                        openItemDetailActivity(mSearchItemList.get(position));

                    }
                });

        mSearchRecyclerView.setAdapter(mSearchAdapter);

        // Set feed recycler view scroll listener
        mSearchRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                synchronized (this) {
                    if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                        calculateViewPositionAndScroll(recyclerView);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mPixelMovedX += dx;
                int childCount = recyclerView.getChildCount();
                int width = recyclerView.getChildAt(0).getWidth();

                for (int j = 0; j < childCount; j++) {
                    View v = recyclerView.getChildAt(j);
                    float rate = 0;
                    if (v.getLeft() <= mPadding) {
                        if (v.getLeft() >= mPadding - v.getWidth()) {
                            rate = (mPadding - v.getLeft()) * 1f / v.getWidth();
                        } else {
                            rate = 1;
                        }
                        v.setScaleY(1 - rate * 0.1f);
                        v.setAlpha(1 - rate * 0.5f);
                    } else {
                        if (v.getLeft() <= recyclerView.getWidth() - mPadding) {
                            rate = (recyclerView.getWidth() - mPadding -
                                    v.getLeft()) * 1f / v.getWidth();
                        }
                        v.setScaleY(0.9f + rate * 0.1f);
                        v.setAlpha(0.5f + rate * 0.5f);
                    }
                }
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearchBoxInQueryActivity();
                return true;
            case R.id.action_wishlist:
                openWishlistActivity();
                return true;
            case R.id.action_filter:
                openFilterInQueryActivity();
                return true;
            case android.R.id.home:
                openFeedActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        mIsActivityAvailable = false;
        super.onDestroy();
    }

    private void searchForQuery(String queryId) {

        mProgressDialog.setMessage("Loading Search Results");
        mProgressDialog.show();
        String queryPath = "/search/result";

        JSONObject retrievequery = new JSONObject();
        try {
            // request id and ts for get request should be set in ring
            retrievequery.put("type", "get");
            retrievequery.put("uri", queryPath);

            JSONObject params = new JSONObject();
            params.put("sruid", queryId);

            retrievequery.put("params", params);

        } catch (JSONException je) {
            Log.e(TAG, "Failed to create retrieve request");
        }
        Log.i(TAG, "Query is " + retrievequery.toString());
        WebSocketRequestHandler.getInstance().createAndSendGetRequestToServer(
                retrievequery, mActRef, queryPath);
        Log.e(TAG, "Query sent");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPixelMovedX = savedInstanceState.getFloat(BUNDLE_LIST_PIXELS);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(BUNDLE_LIST_PIXELS, mPixelMovedX);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewTreeObserver vto = mSearchRecyclerView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSearchRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                calculateViewPositionAndScroll(mSearchRecyclerView);
            }
        });

    }


    private void handleIntent(Intent intent) {
        String queryId = intent.getStringExtra(Constants.SEARCH_ID).trim();
        searchForQuery(queryId);
    }

    private void openSearchBoxInQueryActivity() {
        Intent queryActivityIntent = new Intent(this, QueryActivity.class);
        queryActivityIntent.putExtra(Constants.OPEN_QUERY_BOX, true);
        startActivity(queryActivityIntent);
    }

    private void openWishlistActivity() {
        Intent wishlistActivityIntent = new Intent(this, WishListActivity.class);
        startActivity(wishlistActivityIntent);
    }

    private void openItemDetailActivity(JSONObject detailJson) {
        Intent itemDetailActivityIntent = new Intent(this, ItemDetailActivity.class);
        itemDetailActivityIntent.putExtra(Constants.ITEM_DATA, detailJson.toString());
        startActivity(itemDetailActivityIntent);
        overridePendingTransition(0, 0);
    }

    private void openFeedActivity() {
        Intent feedActivityIntent = new Intent(this, FeedActivity.class);
        startActivity(feedActivityIntent);
    }

    private void openFilterInQueryActivity() {
        Intent filterIntent = new Intent(this, QueryActivity.class);
        filterIntent.putExtra(Constants.OPEN_QUERY_FILTER, true);
        startActivity(filterIntent);

    }

    private void calculateViewPositionAndScroll(RecyclerView recyclerView) {
        int movePosition = Math.round((mPixelMovedX + mPadding) / mItemWidth);
        if (-1 == movePosition) {
            movePosition = 0;
        }
        if (recyclerView.getAdapter().getItemCount() <= movePosition) {
            movePosition--;
        }
        float targetPosition = movePosition * mItemWidth - mPadding;
        float missingPixels = targetPosition - mPixelMovedX;
        if (0 != missingPixels) {
            recyclerView.smoothScrollBy((int) missingPixels, 0);
        }
    }

    @Override
    public boolean getActivityAvailableStatus() {
        return mIsActivityAvailable;
    }

    @Override
    public void updateUI(JSONObject jsonResponse) {

        Log.i(TAG, jsonResponse.toString());

        try {
            String searchId = jsonResponse.getString("sruid");
            // styles and filterid
            JSONArray cItemArray = jsonResponse.getJSONArray("result");
            int cItemArrLen = cItemArray.length();
            for (int i = 0; i < cItemArrLen; i++) {
                mSearchItemList.add(0, (JSONObject) cItemArray.get(i));
            }

            mSearchAdapter.notifyDataSetChanged();
            mSearchRecyclerView.scrollToPosition(1);

        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse search results");
        }

        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

    }
}
