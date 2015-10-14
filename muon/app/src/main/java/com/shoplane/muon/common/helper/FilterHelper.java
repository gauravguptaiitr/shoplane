package com.shoplane.muon.common.helper;

import android.view.View;

import com.shoplane.muon.common.service.DeleteRequestService;
import com.shoplane.muon.common.service.GetRequestService;
import com.shoplane.muon.common.service.UpdateRequestService;
import com.shoplane.muon.models.CatalogueItem;
import com.shoplane.muon.models.WishlistItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ravmon on 2/9/15.
 */
public class FilterHelper {
    private static FilterHelper mFilterHelperInstance;
    private long mQueryFilterUid;
    private Map<String, Long> mStyleToFilterIdMap;
    private Map<Long, Map<String, List<String>>> mFilterIdToFiltersMap;
    private Map<Long, Map<String, List<Boolean>>> mFiltertSelectionMap;
    private Map<Long, Map<String, List<String>>> mFilterSelecttionListMap;
    private Map<Long, String> mFilterIdToTitleMap;
    private Map<String, Long> mFilterTitleToIdMap;
    private static final String mServerUrl = "dummy";
    private Set<String> mValidFilters;

    public static FilterHelper getFilterHelperInstance() {
        // get id from token or something
        if (null == mFilterHelperInstance) {
            mFilterHelperInstance = new FilterHelper();
        }
        return mFilterHelperInstance;
    }

    private FilterHelper() {
        // TODO: Get data from server with userid
        mQueryFilterUid = 1L;
        mStyleToFilterIdMap = new HashMap<>();
        mFilterIdToFiltersMap = new HashMap<>();
        mFiltertSelectionMap = new HashMap<>();
        mFilterSelecttionListMap = new HashMap<>();
        mFilterIdToTitleMap = new HashMap<>();
        mFilterTitleToIdMap = new HashMap<>();
        mValidFilters = new HashSet<>();
        setValidFilters();
    }

    public List<String> getFilterFields(Long filterId, String filterType) {

        return mFilterIdToFiltersMap.get(filterId).get(filterType);
    }

    private void setValidFilters() {
        mValidFilters.add("colors");
        mValidFilters.add("sizes");
    }


    public void updateFilters(Map<String, Long> styleToFilterId,
                              Map<Long, Map<String, List<String>>> filterIdToFilterMap) {
        mStyleToFilterIdMap.clear();
        mFilterIdToFiltersMap.clear();
        mFilterIdToTitleMap.clear();
        mFiltertSelectionMap.clear();
        mFilterSelecttionListMap.clear();

        mStyleToFilterIdMap.putAll(styleToFilterId);
        mFilterIdToFiltersMap.putAll(filterIdToFilterMap);

        for (long key : filterIdToFilterMap.keySet()) {
            mFilterIdToTitleMap.put(key, "shirt filter");
        }

        for (long filterIdKey : filterIdToFilterMap.keySet() ) {
            mFiltertSelectionMap.put(filterIdKey, new HashMap<String, List<Boolean>>());
            for (String filterTypeKey : mFilterIdToFiltersMap.get(filterIdKey).keySet()) {
                int lenOfTypeValues = mFilterIdToFiltersMap.get(filterIdKey).
                        get(filterTypeKey).size();

                List<Boolean> boolValues = new ArrayList();
                for (int i = 0; i < lenOfTypeValues; i++) {
                    boolValues.add(false);
                }

                mFiltertSelectionMap.get(filterIdKey).put(filterTypeKey,
                        new ArrayList<Boolean>(boolValues));
            }
        }

    }

    public Map<Long, List<String>> getFiltersForStyles(List<String> styles) {


        Map<Long, List<String>> filterToStylesMap = new HashMap<>();
        for (String style : styles) {
            style = style.toLowerCase().trim();
            long filterIdForStyle = mStyleToFilterIdMap.get(style);
            if (filterToStylesMap.containsKey(filterIdForStyle)) {
                filterToStylesMap.get(filterIdForStyle).add(style);
            } else {
                List<String> styleList = new ArrayList<>();
                styleList.add(style);
                filterToStylesMap.put(filterIdForStyle, new ArrayList<String>(styleList));
            }
        }
        return filterToStylesMap;
    }

    public String getFilterTitleForFilterId(Long filterId) {
        return mFilterIdToTitleMap.get(filterId);
    }

    public Long getFilterIdForFilterTitle(String filterTitle) {
        filterTitle = filterTitle.toLowerCase().trim();
        return mFilterTitleToIdMap.get(filterTitle);
    }

    public Boolean getFilterValueSelectionStatus(Long filterId, String filterType,
                                                 int filterValuePosition) {
        return mFiltertSelectionMap.get(filterId).get(filterType).get(filterValuePosition);
    }

    public void setFilterValueSelectionStatus(Long filterId, String filterType,
                                                 int filterValuePosition, boolean value) {
        mFiltertSelectionMap.get(filterId).get(filterType).set(filterValuePosition, value);
    }

    public List<Boolean> getFilterTypeSelectionStatus(Long filterId, String filterType) {
        return mFiltertSelectionMap.get(filterId).get(filterType);
    }

    public void addFilterToSelectionList(Long filterId, String filterGroup, String filterValue){
        if (mFilterSelecttionListMap.get(filterId).containsKey(filterGroup)) {
            mFilterSelecttionListMap.get(filterId).get(filterGroup).add(filterValue);
        } else {
            mFilterSelecttionListMap.get(filterId).put(filterGroup, new ArrayList<String>());
            mFilterSelecttionListMap.get(filterId).get(filterGroup).add(filterValue);
        }
    }

    public void removeFilterFromSelectionList(Long filterId, String filterGroup,
                                              String filterValue) {
        mFilterSelecttionListMap.get(filterId).get(filterGroup).remove(filterValue);
    }

    public List<String> getFilterSelectionList(Long filterId, String filterGroup) {
        return mFilterSelecttionListMap.get(filterId).get(filterGroup);
    }

    public long getFilterIdForStyle(String style) {
        style = style.trim().toLowerCase();
        return mStyleToFilterIdMap.get(style);
    }
}
