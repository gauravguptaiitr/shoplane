package com.shoplane.muon.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.shoplane.muon.R;

import com.shoplane.muon.adapters.FilterPagerAdapter;
import com.shoplane.muon.adapters.FilterSelectionListAdapter;
import com.shoplane.muon.adapters.QueryAdapter;
import com.shoplane.muon.common.Constants;
import com.shoplane.muon.common.handler.WebSocketRequestHandler;
import com.shoplane.muon.common.helper.FilterHelper;
import com.shoplane.muon.common.utils.UniqueIdGenerator;
import com.shoplane.muon.common.utils.userinterface.SlidingTabLayout;
import com.shoplane.muon.interfaces.UpdateUITask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QueryActivity extends AppCompatActivity implements UpdateUITask {
    private static final String TAG = QueryActivity.class.getSimpleName();

    private static final String ENTER_QUERY = "Please enter a query to search";
    private static final String LAST_SEARCHED_QUERY = "last_searched_query";

    private String mQuery;
    private static List<String> mSuggestedQueryItems;
    private List<String> mTotalSuggestedItems;
    private List<Boolean> mStyleItemSelectionList;
    private List<String> mStyleFilters;
    private List<String> mStyleSelected;
    private Map<Long, List<String>> mFilterIdToStylesMap;
    private List<String> mFilterSelectionItemList;
    private int mNumStylesSelected;
    private int mFilterPosition;
    private boolean mIsStyleDisplayed;
    private int mStylesLeftToDisplay;
    private String mUniqueQueryId;
    WeakReference<UpdateUITask> mActRef;
    private boolean mIsActivityAvailable;


    private QueryAdapter mQueryAdapter;
    private MaterialEditText mSearchText;
    private int mSelectedBkgColor;
    private int mUnselectedBkgColor;
    private FilterPagerAdapter mFilterPagerAdapter;
    private TextView mCurrentStyleView;
    private TextView mStylesForFilterView;
    private View mQueryListFooter;
    private ListView mQuerySuggestionView;
    private QuerySendScheduler mQuerySendScheduler;
    private FilterSelectionListAdapter mFilterSelectionAdapter;
    private FloatingActionButton filterButton;
    private ViewGroup mQueryMainLayout;
    private ViewGroup mStyleNavLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        setupUI(findViewById(R.id.query_main_layout));

        mSearchText = (MaterialEditText) findViewById(R.id.searchbox_search_query);
        mSearchText.requestFocus();


        final Long mFilterId = 0L;

        ViewPager mFilterPager = (ViewPager) findViewById(R.id.filter_pager);
        mFilterPager.setOffscreenPageLimit(6);
        mFilterPagerAdapter = new FilterPagerAdapter(getSupportFragmentManager(), mFilterId);
        mFilterPager.setAdapter(mFilterPagerAdapter);

        final SlidingTabLayout mFilterSlidingTabLayout = (SlidingTabLayout) findViewById(
                R.id.filter_tabs);
        mFilterSlidingTabLayout.setDistributeEvenly(true);
        mFilterSlidingTabLayout.setViewPager(mFilterPager);

        mCurrentStyleView = (TextView) findViewById(R.id.style_title);
        mStylesForFilterView = (TextView) findViewById(R.id.styles_for_filter);


        mSuggestedQueryItems = new ArrayList<>();
        mTotalSuggestedItems = new ArrayList<>();
        mStyleItemSelectionList = new ArrayList<>();
        mStyleSelected = new ArrayList<>();
        mStyleFilters = new ArrayList<>();
        mFilterSelectionItemList = new ArrayList<>();
        mFilterIdToStylesMap = new HashMap<>();
        mNumStylesSelected = 0;
        mFilterPosition = 0;
        mStylesLeftToDisplay = 0;
        mUniqueQueryId = getUniqueQueryIdentifier();
        mIsStyleDisplayed = false;
        // use this reference to determine whether activity exist or not
        mIsActivityAvailable = true;
        mActRef = new WeakReference<UpdateUITask>(this);


        mQuerySuggestionView = (ListView) findViewById(R.id.query_suggestion_view);
        mQueryAdapter = new QueryAdapter(this,
                mSuggestedQueryItems, mStyleItemSelectionList);

        // add footer
        mQueryListFooter = (View) ((LayoutInflater) this.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.query_list_footer_view,
                null, false);
        Button loadMoreButton = (Button) mQueryListFooter.findViewById(R.id.query_list_footer_button);
        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get text from searchbox
                // Send query to search activity
                loadMoreStyles();
            }
        });
        mQuerySuggestionView.addFooterView(mQueryListFooter);

        mQuerySuggestionView.setAdapter(mQueryAdapter);

        mSelectedBkgColor = getResources().getColor(R.color.orange);
        mUnselectedBkgColor = getResources().getColor(R.color.transparent);

        filterButton = (FloatingActionButton) findViewById(
                R.id.floating_filter_button);

        mQuerySuggestionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mIsStyleDisplayed) {
                    TextView textView = (TextView) view.findViewById(R.id.suggested_query_value);
                    String suggestedQuery = textView.getText().toString().trim();
                    mSearchText.setText(suggestedQuery);
                } else {
                    if (!mStyleItemSelectionList.get(position)) {
                        mStyleItemSelectionList.set(position, true);
                        view.setBackgroundColor(mSelectedBkgColor);
                        mStyleSelected.add(mSuggestedQueryItems.get(position));
                        mNumStylesSelected++;
                        if (1 == mNumStylesSelected) {
                            // update filters only when styles changes
                            filterButton.setVisibility(View.VISIBLE);
                        }
                        updateFilterMapForSelection(mStyleSelected);
                        mQuerySendScheduler.resetTask();
                    } else {
                        mStyleItemSelectionList.set(position, false);
                        view.setBackgroundColor(mUnselectedBkgColor);
                        mStyleSelected.remove(mSuggestedQueryItems.get(position));
                        mNumStylesSelected--;
                        if (0 == mNumStylesSelected) {
                            filterButton.setVisibility(View.GONE);
                        }
                        updateFilterMapForSelection(mStyleSelected);
                        mQuerySendScheduler.resetTask();
                    }
                }
            }
        });

        FloatingActionButton searchButton = (FloatingActionButton) findViewById(
                R.id.floating_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get text from searchbox
                searchForQuery();
            }
        });

        FloatingActionButton homeButton = (FloatingActionButton) findViewById(
                R.id.floating_home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFeedActivity();
            }
        });

        // Adapter for filter selection list
        GridView filterSelectionView = (GridView) findViewById(R.id.filter_selection_gridview);
        mFilterSelectionAdapter = new FilterSelectionListAdapter(this, mFilterSelectionItemList);
        filterSelectionView.setAdapter(mFilterSelectionAdapter);

        // View groups
        mQueryMainLayout = (ViewGroup) findViewById(R.id.query_main_layout);
        mStyleNavLayout = (ViewGroup) findViewById(R.id.style_navigator_layout);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterPosition = 0;
                updateFilterLayout();
                mQueryMainLayout.setVisibility(View.GONE);
                mStyleNavLayout.setVisibility(View.VISIBLE);
            }
        });

        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // if timer exxpired then then send query otherwise reset timer
                if (0 == s.length()) {
                    getDefaultQuerySuggestion();
                    mIsStyleDisplayed = false;
                    filterButton.setVisibility(View.GONE);
                    //loadMoreButton.setVisibility(View.GONE);
                    mStyleSelected.clear();
                    mNumStylesSelected = 0;
                    mQuerySendScheduler.stopTask();
                } //else {
                //getStyleSuggestionList();
                //}
                mQuerySendScheduler.resetTask();
            }
        });

        // Style navigation
        ImageButton styleNavigateBackButton = (ImageButton) findViewById(R.id.style_back_button);
        ImageButton styleNavigateForwardButton = (ImageButton) findViewById(
                R.id.style_forward_button);
        ImageButton filterCloseButton = (ImageButton) findViewById(R.id.style_down_button);

        styleNavigateBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (0 == mFilterPosition) {
                    return;
                }
                mFilterPosition--;
                updateFilterLayout();

            }
        });

        styleNavigateForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mStyleFilters.size() - 1) == mFilterPosition) {
                    return;
                }
                mFilterPosition++;
                // Indicate fragment to change data
                updateFilterLayout();
            }
        });

        filterCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //queryFilterLayout.setVisibility(View.GONE);
                mStyleNavLayout.setVisibility(View.GONE);
                mQueryMainLayout.setVisibility(View.VISIBLE);
            }
        });
        getDefaultQuerySuggestion();
        mQuerySendScheduler = new QuerySendScheduler(1000);

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
        mQuerySendScheduler.stopTask();
    }

    @Override
    public void onDestroy() {
        mIsActivityAvailable = false;
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //store the new intent unless getIntent() will return the old one
        setIntent(intent);
        Boolean isSearchIntent = intent.getExtras().getBoolean(Constants.OPEN_QUERY_BOX);
        Boolean isFilterIntent = intent.getExtras().getBoolean(Constants.OPEN_QUERY_FILTER);

        if (!isSearchIntent && !isFilterIntent) {
            return;
        }

        if (isSearchIntent) {
            mStyleNavLayout.setVisibility(View.GONE);
            mQueryMainLayout.setVisibility(View.VISIBLE);
            mSearchText.setVisibility(View.VISIBLE);
        }

        if (isFilterIntent) {
            if (0 == mNumStylesSelected) {
                mStyleNavLayout.setVisibility(View.GONE);
                mQueryMainLayout.setVisibility(View.VISIBLE);
                mSearchText.setVisibility(View.GONE);
            } else {
                mFilterPosition = 0;
                updateFilterLayout();
                mQueryMainLayout.setVisibility(View.GONE);
                mStyleNavLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean getActivityAvailableStatus() {
        return mIsActivityAvailable;
    }

    private void getDefaultQuerySuggestion() {
        mSuggestedQueryItems.clear();
        mStyleItemSelectionList.clear();
        mSuggestedQueryItems.add("I want something trending for Diwali");
        mStyleItemSelectionList.add(false);
        mSuggestedQueryItems.add("I am looking for a formal wear for a meeting");
        mStyleItemSelectionList.add(false);
        mSuggestedQueryItems.add("A party wear for cold weather");
        mStyleItemSelectionList.add(false);
        mSuggestedQueryItems.add("looking for fancy wear for Goa vacation");
        mStyleItemSelectionList.add(false);
        mSuggestedQueryItems.add("A birthday present for my wife who is turning 30");
        mStyleItemSelectionList.add(false);
        mQueryAdapter.notifyDataSetChanged();

        mQueryListFooter.setVisibility(View.GONE);

    }

    private void getStyleSuggestionList() {

        mSuggestedQueryItems.clear();
        mStyleItemSelectionList.clear();

        for (int i = 0; i < 4 && mStylesLeftToDisplay > 0; i++) {
            mSuggestedQueryItems.add(mTotalSuggestedItems.get(i));
            mTotalSuggestedItems.remove(i);
            mStyleItemSelectionList.add(false);
            mStylesLeftToDisplay--;
        }

        mQueryAdapter.notifyDataSetChanged();
        mIsStyleDisplayed = true;
        // display load more button
        mQueryListFooter.setVisibility(View.VISIBLE);
        // successful reset everything
    }

    private void resetStyleSelection() {
        mStyleItemSelectionList.clear();
        mTotalSuggestedItems.clear();
        mStyleSelected.clear();
        mStyleFilters.clear();
        mFilterSelectionItemList.clear();
        mFilterIdToStylesMap.clear();
        mNumStylesSelected = 0;
        mIsStyleDisplayed = false;
        mFilterPosition = 0;

        filterButton.setVisibility(View.GONE);
    }

    private void loadMoreStyles() {
        if ( 0 == mStylesLeftToDisplay) {
            Toast.makeText(this, "No more styles", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; i < 4 && mStylesLeftToDisplay > 0; i++) {
            mSuggestedQueryItems.add(mTotalSuggestedItems.get(i));
            mTotalSuggestedItems.remove(i);
            mStyleItemSelectionList.add(false);
            mStylesLeftToDisplay--;
        }
        mQueryAdapter.notifyDataSetChanged();
    }

    private void updateFilterMapForSelection(final List<String> stylesSelected) {
        // get filter ids
        if (null == stylesSelected || 0 == stylesSelected.size()) {
            return;
        }
        mFilterIdToStylesMap.clear();
        mStyleFilters.clear();
        try {
            mFilterIdToStylesMap = FilterHelper.getFilterHelperInstance().getFiltersForStyles(
                    stylesSelected);
        } catch (Exception e) {
            Log.e(TAG, "filters not found for styles");
        }

        // set filter titles
        for (Long filterId : mFilterIdToStylesMap.keySet()) {
            String filterTitle = "";
            try {
                filterTitle = FilterHelper.getFilterHelperInstance().
                        getFilterTitleForFilterId(filterId);
            } catch (Exception e) {
                Log.e(TAG, "could not get filter title");
            }
            mStyleFilters.add(filterTitle);
        }
    }

    public Long getCurrentFilterId() {
        Long filterId = 0L;
        try {
            filterId = FilterHelper.getFilterHelperInstance().getFilterIdForFilterTitle(
                    mStyleFilters.get(mFilterPosition));
        } catch (Exception e) {
            Log.e(TAG, "could not get filterid");
        }
        return filterId;
    }

    private void updateFilterLayout() {
        String currentFilter = mStyleFilters.get(mFilterPosition).toLowerCase().trim();
        mCurrentStyleView.setText(currentFilter);

        Log.i(TAG, "filter text updated");

        Long filterId = getCurrentFilterId();
        // get styles for filterid
        List<String> styleList = mFilterIdToStylesMap.get(filterId);
        // Show styles for filter

        Log.i(TAG, "filter id updated");
        StringBuilder styleText = new StringBuilder();
        styleText.append("STYLES \n");
        for (String style : styleList) {
            styleText.append(" - ");
            styleText.append(style);
            styleText.append("\n");
        }
        mStylesForFilterView.setText(styleText);
        // Indicate fragment to change data
        mFilterPagerAdapter.setFilterId(filterId);
        mFilterPagerAdapter.notifyDataSetChanged();

        Log.i(TAG, "style text updated");
        // Update filters
        mFilterSelectionItemList.clear();
        Set<String> selectedFilterTypes = FilterHelper.getFilterHelperInstance().
                getSelectedFiltersKeys(filterId);
        for (String key : selectedFilterTypes) {
            mFilterSelectionItemList.addAll(FilterHelper.getFilterHelperInstance().
                    getFilterSelectionList(filterId, key));
        }
        mFilterSelectionAdapter.notifyDataSetChanged();
    }


    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof MaterialEditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard(v);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void openFeedActivity() {
        Intent feedActivityIntent = new Intent(this, FeedActivity.class);
        startActivity(feedActivityIntent);
    }

    private void searchForQuery() {
        String mQuery = mSearchText.getText().toString().trim();
        if (null == mQuery || mQuery.equalsIgnoreCase(Constants.EMPTY_STRING)) {
            Toast.makeText(QueryActivity.this, ENTER_QUERY, Toast.LENGTH_SHORT).show();
        } else {
            // Send query to search activity
            Intent searchIntent = new Intent(QueryActivity.this, SearchActivity.class);
            searchIntent.putExtra(Constants.SEARCH_ID, mUniqueQueryId);
            // get new id
            mUniqueQueryId = getUniqueQueryIdentifier();
            startActivity(searchIntent);
        }
    }

    private void sendQueryToServer() {
        // get filters
        // get query text
        // create json
        // send it to server

        JSONObject updatequery = new JSONObject();
        try {
            updatequery.put("reqid", Integer.MAX_VALUE + "");
            updatequery.put("type", "post");
            updatequery.put("uri", "/query/update");

            JSONObject params = new JSONObject();
            params.put("sruid", mUniqueQueryId);

            JSONObject query = new JSONObject();
            query.put("queryStr", mSearchText.getText());

            JSONObject styles = new JSONObject();
            for (String style : mStyleSelected) {
                styles.put(style, FilterHelper.getFilterHelperInstance().
                        getFilterIdForStyle(style) + "");
            }
            query.put("styles", styles);

            JSONObject filters = new JSONObject();
            for (String styleFilter : mStyleFilters) {
                JSONObject filterIdObj = new JSONObject();

                Long filterId = FilterHelper.getFilterHelperInstance().
                        getFilterIdForFilterTitle(styleFilter);

                filterIdObj.put("filterTitle", FilterHelper.getFilterHelperInstance().
                        getFilterTitleForFilterId(filterId));

                JSONArray filterTypeColor = new JSONArray();
                List<String> values = new ArrayList<>();
                values.addAll(FilterHelper.getFilterHelperInstance().
                        getFilterSelectionList(filterId, "colors"));
                for (String value : values) {
                    filterTypeColor.put(value);
                }
                filterIdObj.put("colors", filterTypeColor);

                values.clear();
                JSONArray filterTypeSize = new JSONArray();
                values.addAll(FilterHelper.getFilterHelperInstance().
                        getFilterSelectionList(filterId, "sizes"));
                for (String value : values) {
                    filterTypeSize.put(value);
                }
                filterIdObj.put("sizes", filterTypeSize);
                filters.put(filterId.toString(), filterIdObj);
            }

            query.put("filters", filters);
            params.put("query", query);
            updatequery.put("params", params);

        } catch (JSONException je) {
            Log.e(TAG, "Failed to create request");
        }
        Log.e(TAG, "Query is " + updatequery.toString());
        WebSocketRequestHandler.getInstance().createAndSendPostRequestToServer(
                updatequery.toString(), mActRef, Constants.QUERY_SUGGESTION_STYLES_PATH);
        Log.e(TAG, "Query sent");
    }

    public FilterSelectionListAdapter getFilterSelectionAdapter() {
        return mFilterSelectionAdapter;
    }

    public void resetQuerySchedulerForFilterChange() {
        mQuerySendScheduler.resetTask();
    }

    public List<String> getFilterSelectionList() {
        return mFilterSelectionItemList;
    }


    @Override
    public void updateUI(JSONObject jsonResponse) {
        // process query suggestions
        final Map<String, Long> styleToFilterId;
        final Map<Long, Map<String, List<String>>> filterIdToFilterMap;
        final Map<Long, String> filterIdToFilterTitleMap;
        final Map<String, Long> filterTitleToFilterIdMap;
        final List<String> styleList;

        Log.e(TAG, jsonResponse.toString());
        try {
            String searchId = jsonResponse.getString("sruid");
            // styles and filterid
            JSONObject styles = jsonResponse.getJSONObject("styles");
            styleToFilterId = new HashMap<>();
            styleList = new ArrayList<>();
            Iterator<String> styleIt = styles.keys();
            while(styleIt.hasNext()) {
                String key = styleIt.next();
                styleList.add(key.trim().toLowerCase());
                styleToFilterId.put(key.trim().toLowerCase(), styles.getLong(key));
            }

            // filter id and filters
            JSONObject filters = jsonResponse.getJSONObject("filters");
            filterIdToFilterMap = new HashMap<>();
            filterIdToFilterTitleMap = new HashMap<>();
            filterTitleToFilterIdMap = new HashMap<>();

            Iterator<String> filterIt = filters.keys();
            while(filterIt.hasNext()) {
                String key = filterIt.next();
                Long keyInLong = Long.parseLong(key);
                filterIdToFilterMap.put(keyInLong, new HashMap<String, List<String>>());

                JSONObject singleFilter = filters.getJSONObject(key);

                String filterTiltle = singleFilter.getString("filterTitle");
                filterIdToFilterTitleMap.put(keyInLong, filterTiltle.trim().toLowerCase());
                filterTitleToFilterIdMap.put(filterTiltle.trim().toLowerCase(), keyInLong);

                Iterator<String> filterTypesIt = singleFilter.keys();
                // skip title
                filterTypesIt.next();

                while(filterTypesIt.hasNext()) {
                    String filterType = filterTypesIt.next();
                    JSONArray filterValuesJson = singleFilter.getJSONArray(filterType);

                    List<String> filterValues = new ArrayList<>();
                    int len = filterValuesJson.length();
                    for (int i = 0; i < len; i++){
                        filterValues.add(filterValuesJson.get(i).toString().trim().toLowerCase());
                    }

                    filterIdToFilterMap.get(keyInLong).put(filterType.trim().toLowerCase(),
                            new ArrayList<>(filterValues));
                }
            }

        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse query suggestions");
            // do not update anything if there is parsing error
            return;
        }

        resetStyleSelection();
        mTotalSuggestedItems.addAll(styleList);
        mStylesLeftToDisplay = mTotalSuggestedItems.size();
        FilterHelper.getFilterHelperInstance().updateFilters(styleToFilterId,
                filterIdToFilterMap, filterIdToFilterTitleMap, filterTitleToFilterIdMap);
        getStyleSuggestionList();
    }

    // Schedular for query sent to server
    private class QuerySendScheduler {

        private int mInterval;
        private Handler mHandler;

        public QuerySendScheduler(int interval) {
            this.mHandler = new Handler();
            this.mInterval = interval;
        }

        Runnable querySenderRunnable = new Runnable() {
            @Override
            public void run() {
                sendQueryToServer();
            }
        };

        void startTask() {
            if (mHandler != null) {
                if (querySenderRunnable != null) {
                    mHandler.postDelayed(querySenderRunnable, mInterval);
                }
            }
        }

        void stopTask() {
            if (mHandler != null) {
                if (querySenderRunnable != null) {
                    mHandler.removeCallbacks(querySenderRunnable);
                }
            }
        }

        public void resetTask() {
            if (mHandler != null) {
                if (querySenderRunnable != null) {
                    mHandler.removeCallbacks(querySenderRunnable);
                    mHandler.postDelayed(querySenderRunnable, mInterval);
                }
            }
        }

    }

    // Query sending to server
    private String getUniqueQueryIdentifier() {
        return UniqueIdGenerator.getInstance().getUniqueId();
    }
}


