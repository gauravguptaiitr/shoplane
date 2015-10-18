package com.shoplane.muon.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.shoplane.muon.R;
import com.shoplane.muon.common.handler.VolleyRequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by ravmon on 21/8/15.
 */
public class SearchAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = SearchAdapter.class.getSimpleName();

    private final Context mContext;
    private List<JSONObject> mSearchItemList;
    private static OnItemClickListener mSearchItemClickListener;
    private int prevItemPosition = 0;
    private ImageLoader mImageLoader;
    private LinearLayout.LayoutParams mLayoutParams;
    private int mScreenWidth;
    private int mScreenHeight;

    // Item type
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;


    // ViewHolder for Header
    public class ViewHolderHeader extends RecyclerView.ViewHolder {

        public ViewHolderHeader(final View headerView) {
            super(headerView);
        }
    }

    // ViewHolder for Footer
    public class ViewHolderFooter extends RecyclerView.ViewHolder {

        public ViewHolderFooter(final View footerView) {
            super(footerView);
        }
    }


    // Viewholder for each row element
    public class ViewHolderItem extends RecyclerView.ViewHolder {
        public NetworkImageView mSearchImgView;
        public ImageButton mItemAddButton;
        public TextView mSearchItemTitle;
        public ImageButton mSearchItemBrowser;
        public TextView mSearchItemSpecs;


        public ViewHolderItem(final View itemView) {
            super(itemView);
            this.mSearchImgView = (NetworkImageView) itemView.findViewById(
                    R.id.search_list_column_image);
            this.mItemAddButton = (ImageButton) itemView.findViewById(R.id.search_item_add);
            this.mSearchItemBrowser = (ImageButton) itemView.findViewById(
                    R.id.search_item_browser);
            this.mSearchItemTitle = (TextView) itemView.findViewById(R.id.search_item_title);
            this.mSearchItemSpecs = (TextView) itemView.findViewById(R.id.search_item_specs);

            mSearchImgView.setDefaultImageResId(R.drawable.ic_no_image);
            mSearchImgView.setErrorImageResId(R.drawable.ic_image_error);
            mSearchImgView.setAdjustViewBounds(true);


            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View itemView) {
                    if (mSearchItemClickListener != null)
                        mSearchItemClickListener.onItemClick(itemView, getLayoutPosition() - 1);
                }
            });

            // Add button listener
            mItemAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View addItemButton) {
                    //WishlistHelper.getWishlist().addItem(mSearchItemList.get(
                           // getAdapterPosition() - 1));
                }
            });

            // Open item in browser
            mSearchItemBrowser.setOnClickListener(new View.OnClickListener() {
                public void onClick(View searchItemButton) {
                    try {
                        openItemInBrowser(mSearchItemList.get((getAdapterPosition() - 1)).
                                getString("itemUrl"));
                    } catch (JSONException je) {
                        Log.e(TAG, "Failed to get item Url");
                    }
                }

            });


        }
    }

    public SearchAdapter(Context context, List<JSONObject> searchItemList,
                         LinearLayout.LayoutParams layoutParams, int screenWidth,
                         int screenHeight) {
        this.mContext = context;
        this.mSearchItemList = searchItemList;
        this.mLayoutParams = layoutParams;
        this.mScreenWidth = screenWidth;
        this.mScreenHeight = screenHeight;
        mImageLoader = VolleyRequestHandler.getVolleyRequestHandlerInstance(mContext).
                getImageLoader();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (TYPE_HEADER == viewType) {
            View headerView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.search_list_header, parent, false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)
                    (mScreenWidth * 0.05), (int) (mScreenHeight * 0.8));
            headerView.setLayoutParams(layoutParams);
            //TextView headerText = (TextView) headerView.findViewById(R.id.search_header_text);
            //headerText.setGravity(Gravity.CENTER);
            return new ViewHolderHeader(headerView);
        } else if (TYPE_FOOTER == viewType) {
            View footerView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.search_list_footer, parent, false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)
                    (mScreenWidth * 0.05), (int) (mScreenHeight * 0.8));
            footerView.setLayoutParams(layoutParams);
            //TextView footerText = (TextView) footerView.findViewById(R.id.search_footer_text);
            //footerText.setGravity(Gravity.CENTER);
            return new ViewHolderFooter(footerView);
        } else {
            // search item
            View searchViewColumn = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_list_column, parent, false);
            searchViewColumn.setLayoutParams(mLayoutParams);
            return new ViewHolderItem(searchViewColumn);
        }
    }

    // Replace contents of a view.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderItem) {
            // populate item here
            JSONObject item = mSearchItemList.get(position -1);
            try {
                JSONArray jsonArr = item.getJSONArray("styles");
                StringBuilder text  = new StringBuilder();
                // Style and brand
                int len = jsonArr.length();
                for (int i = 0; i < len - 1; i++) {
                    text.append(jsonArr.getString(i));
                    text.append("-");
                }
                text.append(jsonArr.get(len - 1));
                text.append("\n\n");
                text.append(item.getString("brand"));
                ((ViewHolderItem) holder).mSearchItemTitle.setText(text.toString());

                // title and size
                text.setLength(0);
                text.append(item.getString("title"));
                text.append("\n\n");

                jsonArr = item.getJSONArray("sizes");
                len = jsonArr.length();
                for (int i = 0; i < len - 1; i++) {
                    text.append(jsonArr.getString(i));
                    text.append("-");
                }
                text.append(jsonArr.getString(len - 1));
                ((ViewHolderItem) holder).mSearchItemSpecs.setText(text.toString());

                JSONObject imageUrl = item.getJSONObject("images");
                ((ViewHolderItem) holder).mSearchImgView.setImageUrl(imageUrl.getString("primary"),
                        mImageLoader);

            } catch (JSONException je) {
               Log.e(TAG, "Failed to load data for item position " + position);
            }

        }
    }

    // Return the size of your dataset
    @Override
    public int getItemCount() {
        // header and footer included
        return mSearchItemList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == position) {
            return TYPE_HEADER;
        }

        if ((getItemCount() - 1) == position) {
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }

    private void openItemInBrowser(String itemUrl) {
        String itemPage = itemUrl;
        if (!itemPage.startsWith("http://") && !itemPage.startsWith("https://"))
            itemPage = "https://" + itemPage;

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(itemPage));
            mContext.startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "Please install a webbrowser",
                    Toast.LENGTH_LONG).show();
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mSearchItemList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<JSONObject> searchItemList) {
        mSearchItemList.addAll(searchItemList);
        notifyDataSetChanged();
    }

    // Listener for click event
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mSearchItemClickListener = listener;

    }
}
