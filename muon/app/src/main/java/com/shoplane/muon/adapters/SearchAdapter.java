package com.shoplane.muon.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.shoplane.muon.R;
import com.shoplane.muon.common.Constants;
import com.shoplane.muon.common.handler.VolleyRequestHandler;
import com.shoplane.muon.common.helper.WishlistHelper;
import com.shoplane.muon.models.CatalogueItem;

import java.util.List;

/**
 * Created by ravmon on 21/8/15.
 */
public class SearchAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private List<CatalogueItem> mSearchItemList;
    private static OnItemClickListener mSearchItemClickListener;
    private int prevItemPosition = 0;
    private ImageLoader mImageLoader;
    private LinearLayout.LayoutParams mLayoutParams;

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
                        mSearchItemClickListener.onItemClick(itemView, getLayoutPosition());
                }
            });

            // Add button listener
            mItemAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View addItemButton) {
                    WishlistHelper.getWishlist().addItem(mSearchItemList.get(
                            getAdapterPosition() - 1));
                }
            });

            // Open item in browser
            mSearchItemBrowser.setOnClickListener(new View.OnClickListener() {
                public void onClick(View searchItemButton) {
                    //openItemInBrowser(mSearchItemList.get((getAdapterPosition() - 1)).getImageUrl());
                }

            });


        }
    }

    public SearchAdapter(Context context, List<CatalogueItem> searchItemList,
                         LinearLayout.LayoutParams layoutParams) {
        this.mContext = context;
        this.mSearchItemList = searchItemList;
        this.mLayoutParams = layoutParams;
        mImageLoader = VolleyRequestHandler.getVolleyRequestHandlerInstance(mContext).
                getImageLoader();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (TYPE_HEADER == viewType) {
            View headerView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.search_list_header, parent, false);
            headerView.setLayoutParams(mLayoutParams);
            //TextView headerText = (TextView) headerView.findViewById(R.id.search_header_text);
            //headerText.setGravity(Gravity.CENTER);
            return new ViewHolderHeader(headerView);
        } else if (TYPE_FOOTER == viewType) {
            View footerView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.search_list_footer, parent, false);
            footerView.setLayoutParams(mLayoutParams);
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
            //((ViewHolderItem) holder).mSearchImgView.setImageUrl(mSearchItemList.get(position - 1).
                  //  getImageUrl(), mImageLoader);

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
        String itemPage = "www.google.com";
        if (!itemPage.startsWith("http://") && !itemPage.startsWith("https://"))
            itemPage = "http://" + itemPage;

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
    public void addAll(List<CatalogueItem> searchItemList) {
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
