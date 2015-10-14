package com.shoplane.muon.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.shoplane.muon.common.helper.FilterHelper;
import com.shoplane.muon.fragments.FilterPagerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravmon on 16/9/15.
 */
public class FilterPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = FilterPagerAdapter.class.getSimpleName();
    private static int NUM_TABS = 2;

    private String[] mFilterFieldTitle = {"colors", "sizes"};
    private Long mFilterId;

    public FilterPagerAdapter(FragmentManager fm, Long filterId) {
        super(fm);
        this.mFilterId = filterId;
    }

    @Override
    public Fragment getItem(int position) {

        FilterPagerFragment filterPagerFragment = FilterPagerFragment.getInstance(position,
                mFilterFieldTitle[position], mFilterId);
        return filterPagerFragment;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFilterFieldTitle[position];
    }

    @Override
    public int getCount() {
        return NUM_TABS;
    }

    public void setFilterId(Long filterId) {
        mFilterId = filterId;
    }
}
