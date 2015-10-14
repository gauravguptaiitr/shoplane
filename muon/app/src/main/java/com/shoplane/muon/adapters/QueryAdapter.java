package com.shoplane.muon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shoplane.muon.R;

import java.util.List;

// Custom filter adapter
public class QueryAdapter extends BaseAdapter {
    private ViewHolder viewHolder;
    private Context mContext;
    private List<String> mStyleItemAdapterList;
    private LayoutInflater mInflater;
    private List<Boolean> mStyleItemSelectionList;
    private int mSelectedBkgColor;
    private int mUnselectedBkgColor;

    public QueryAdapter(Context context, List<String> arrayListItems,
                        List<Boolean> styleItemSelectionList) {
        mStyleItemAdapterList = arrayListItems;
        mStyleItemSelectionList = styleItemSelectionList;
        this.mContext = context;
        mSelectedBkgColor = context.getResources().getColor(R.color.orange);
        mUnselectedBkgColor = context.getResources().getColor(R.color.transparent);
    }

    // class for caching the views in a row
    private class ViewHolder {
        TextView textView;
    }

    @Override
    public int getCount() {
        return mStyleItemAdapterList.size();
    }

    @Override
    public Object getItem(int position) {
        return mStyleItemAdapterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.suggested_query_row, null);
            final TextView textView = (TextView) convertView.findViewById(
                    R.id.suggested_query_value);
            viewHolder = new ViewHolder();
            viewHolder.textView = textView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Fill textview
        viewHolder.textView.setText(mStyleItemAdapterList.get(position));

        if(mStyleItemSelectionList.get(position)) {
            convertView.setBackgroundColor(mSelectedBkgColor);
        } else {
            convertView.setBackgroundColor(mUnselectedBkgColor);
        }
        return convertView;
    }
}