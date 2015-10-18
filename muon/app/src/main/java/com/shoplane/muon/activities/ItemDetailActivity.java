package com.shoplane.muon.activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shoplane.muon.R;
import com.shoplane.muon.adapters.ItemDetailImagePagerAdapter;
import com.shoplane.muon.common.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ItemDetailActivity extends AppCompatActivity {
    private static  final  String TAG = ItemDetailActivity.class.getSimpleName();

    private long mItemId;
    private String mItemLink;
    private ProgressDialog mProgressDialog;
    private List<String> mImageUrls;
    private ItemDetailImagePagerAdapter mItemImagePagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_home);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        ViewPager itemImagePager = (ViewPager) findViewById(R.id.item_image_pager);
        itemImagePager.setOffscreenPageLimit(5);

        // Set proper size for search list columns
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (screenHeight * 0.65));
        itemImagePager.setLayoutParams(layoutParams);

        mImageUrls = new ArrayList<>();
        mItemImagePagerAdapter = new ItemDetailImagePagerAdapter(
                getSupportFragmentManager(), mImageUrls);
        itemImagePager.setAdapter(mItemImagePagerAdapter);

        final ImageButton openItemInBrowser = (ImageButton) findViewById(R.id.open_browser_button);
        openItemInBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openItemInBrowser();
            }
        });

        final ImageButton addToWishlist = (ImageButton) findViewById(R.id.add_wishlist_button);
        addToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  WishlistHelper.getWishlist().addItem(new CatalogueItem(0l, "fromDetail", "abc"));
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);

        handleIntent(getIntent());
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

    private void openItemInBrowser() {
        String itemPage = mItemLink;
        if (!itemPage.startsWith("http://") && !itemPage.startsWith("https://"))
            itemPage = "https://" + itemPage;

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(itemPage));
            startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ItemDetailActivity.this, "Please install a webbrowser",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void handleIntent(Intent intent) {
        TextView itemTitleBrand = (TextView) findViewById(R.id.item_title_brand);
        TextView productTitle = (TextView) findViewById(R.id.product_title_group_price_gender);
        TextView productSizes = (TextView) findViewById(R.id.product_sizes);
        TextView productColors = (TextView) findViewById(R.id.product_colors);
        TextView productDescription = (TextView) findViewById(R.id.product_description);
        TextView productStyletips = (TextView) findViewById(R.id.product_styletips);


        String itemData = intent.getStringExtra(Constants.ITEM_DATA);
        Log.i(TAG, itemData);


        try {
            StringBuilder text = new StringBuilder();
            int len = 0;

            JSONObject item = new JSONObject(itemData);
            Log.i(TAG, item.toString());

            // Style and brand
            JSONArray jsonArr = item.getJSONArray("styles");
            len = jsonArr.length();
            for (int i = 0; i < len - 1; i++) {
                text.append(jsonArr.getString(i));
                text.append("-");
            }
            text.append(jsonArr.getString(len - 1));
            text.append("\n\n");
            text.append(item.getString("brand"));
            itemTitleBrand.setText(text.toString());

            // images
            JSONObject imageUrl = item.getJSONObject("images");
            mImageUrls.add(imageUrl.getString("primary"));

            jsonArr = imageUrl.getJSONArray("alt");
            len = jsonArr.length();
            for (int i = 0; i < len; i++) {
                mImageUrls.add(jsonArr.getString(i));
            }

            // group, title, price and gender
            text.setLength(0);
            jsonArr = item.getJSONArray("groups");
            len = jsonArr.length();
            for (int i = 0; i < len - 1; i++) {
                text.append(jsonArr.getString(i));
                text.append("->");
            }
            text.append(jsonArr.getString(len - 1));
            text.append("\n\n");
            text.append("Title - ");
            text.append(item.getString("title"));
            text.append("\n\n");
            text.append("Price - ");
            text.append(item.getString("price"));
            text.append("\n\n");
            text.append("Gender - ");
            text.append(item.getString("gender"));
            text.append("\n");
            productTitle.setText(text.toString());


            // colors
            text.setLength(0);
            jsonArr = item.getJSONArray("colors");
            len = jsonArr.length();
            for (int i = 0; i < len; i++) {
                text.append(jsonArr.getString(i));
                text.append(" ");
            }
            text.append("\n");
            productColors.setText(text.toString());

            // sizes
            text.setLength(0);
            jsonArr = item.getJSONArray("sizes");
            len = jsonArr.length();
            for (int i = 0; i < len - 1; i++) {
                text.append(jsonArr.getString(i));
                text.append("-");
            }
            text.append(jsonArr.getString(len - 1));
            text.append("\n");
            productSizes.setText(text.toString());

            // description
            text.setLength(0);
            text.append(item.getString("descr"));
            text.append("\n");
            productDescription.setText(text.toString());

            // styling tips
            text.setLength(0);
            text.append(item.getString("stylingTips"));
            text.append("\n");
            productStyletips.setText(text.toString());

            mItemId = item.getLong("cuid");
            mItemLink = item.getString("itemUrl");

        } catch (JSONException je) {
            Log.e(TAG, "Failed to load item details");
        }

        mItemImagePagerAdapter.notifyDataSetChanged();
        if(mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
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
