package com.shoplane.muon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.shoplane.muon.R;
import com.shoplane.muon.common.handler.VolleyRequestHandler;
import com.shoplane.muon.models.FeedItem;

import java.util.List;

/**
 * Created by ravmon on 2/10/15.
 */
public class FilterSelectionListAdapter extends BaseAdapter{

    private final Context mContext;
    private final List<String> mFilterSelectionItemList;
    private ViewHolder viewHolder;
    private LayoutInflater mInflater;

    // class for caching the views in a row
    private class ViewHolder {
        TextView textView;
    }

    @Override
    public int getCount() {
        return mFilterSelectionItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilterSelectionItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public FilterSelectionListAdapter(Context context, List<String> filterSelectionItemList) {
        this.mContext = context;
        this.mFilterSelectionItemList = filterSelectionItemList;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.filter_selection_list_row, null);
            final TextView textView = (TextView) convertView.findViewById(
                    R.id.filter_selection_item);

            viewHolder = new ViewHolder();
            viewHolder.textView = textView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Fill textview
        viewHolder.textView.setText(mFilterSelectionItemList.get(position));
        return convertView;
    }

}
