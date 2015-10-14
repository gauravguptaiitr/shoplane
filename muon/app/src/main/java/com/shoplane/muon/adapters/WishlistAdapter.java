package com.shoplane.muon.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.shoplane.muon.R;
import com.shoplane.muon.common.handler.VolleyRequestHandler;
import com.shoplane.muon.common.helper.WishlistHelper;
import com.shoplane.muon.models.WishlistItem;

import java.util.List;

/**
 * Created by ravmon on 28/8/15.
 */
public class WishlistAdapter extends
        RecyclerView.Adapter<WishlistAdapter.ViewHolder> {
    private static final String TAG = WishlistAdapter.class.getSimpleName();

    private final Context mContext;
    private final List<WishlistItem> mWishlistItemList;
    private OnItemClickListener mWishlistItemClickListener;
    private ImageLoader mImageLoader;

    // Viewholder for each row element
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mWishlistTextView;
        public ImageButton mItemRemoveButton;
        public ImageButton mOpenItemInBrowserButton;
        public NetworkImageView mWishlistImgView;


        public ViewHolder(final View itemView) {
            super(itemView);
            this.mWishlistTextView = (TextView) itemView.findViewById(R.id.title_wishlist_row);
            this.mItemRemoveButton = (ImageButton) itemView.findViewById(
                    R.id.btn_wishlist_remove_item);
            this.mOpenItemInBrowserButton = (ImageButton) itemView.findViewById(
                    R.id.wishlist_item_browser);
            this.mWishlistImgView = (NetworkImageView) itemView.findViewById(
                    R.id.search_list_column_image);

            mWishlistImgView.setDefaultImageResId(R.drawable.ic_no_image);
            mWishlistImgView.setErrorImageResId(R.drawable.ic_image_error);
            mWishlistImgView.setAdjustViewBounds(true);

            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mWishlistItemClickListener != null)
                        mWishlistItemClickListener.onItemClick(itemView, getLayoutPosition());
                }
            });

            mItemRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View removeItemButton) {
                    removeWishlistItem(getAdapterPosition());
                }
            });

            mOpenItemInBrowserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openItemInBrowser(mWishlistItemList.get((getAdapterPosition())).
                            getImageUrl());
                }
            });

        }
    }

    public WishlistAdapter(Context context, List<WishlistItem> wishlistItemList) {
        this.mContext = context;
        this.mWishlistItemList = wishlistItemList;
        mImageLoader = VolleyRequestHandler.getVolleyRequestHandlerInstance(mContext).
                getImageLoader();
    }

    @Override
    public WishlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {

        View wishlistViewRow = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wishlist_row, parent, false);

        return new ViewHolder(wishlistViewRow);
    }

    // Replace contents of a view.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.mWishlistTextView.setText(mWishlistItemList.get(position).getTitle());
        holder.mWishlistImgView.setImageUrl(mWishlistItemList.get(position).getImageUrl(),
                mImageLoader);
    }

    // Return the size of your dataset
    @Override
    public int getItemCount() {
        return mWishlistItemList.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mWishlistItemList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<WishlistItem> searchItemList) {
        mWishlistItemList.addAll(searchItemList);
        notifyDataSetChanged();
    }

    // Listener for click event
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mWishlistItemClickListener = listener;

    }

    private void removeWishlistItem(int position) {
        //long wishlistItemUid = mWishlistItemList.get(position).getCUid();
        mWishlistItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mWishlistItemList.size());
        //WishlistHelper.getWishlist().removeItem(wishlistItemUid);
    }

    private void addWishlistItem(WishlistItem wishlistItem) {
        mWishlistItemList.add(0, wishlistItem);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, mWishlistItemList.size());
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
}
