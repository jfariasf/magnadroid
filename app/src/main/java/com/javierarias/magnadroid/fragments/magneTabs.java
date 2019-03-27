package com.javierarias.magnadroid.fragments;

/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Javier Felipe Arias
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.javierarias.magnadroid.R;
import com.javierarias.magnadroid.sys.utils;

import java.util.ArrayList;
import java.util.List;

public class magneTabs extends Fragment {

    private View view;
    private ArrayList<Integer> drawableTab = new ArrayList<>();
    private List<SamplePagerItem> mTabs = new ArrayList<>();
    private Activity mActivity;


    private static class SamplePagerItem {
        private final CharSequence mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;

        SamplePagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
        }

        /**
         * @return A new {@link Fragment} to be displayed by a {@link ViewPager}
         */
        Fragment createFragment(int fragmentPage) {
            ////Log.i("test none",""+fragmentPage);
            switch (fragmentPage) {
                case 0:
                    return StrengthTab.newInstance(fragmentPage);
                case 1:
                    ////Log.i("test",""+fragmentPage);
                    return BarsPlot.newInstance(fragmentPage);
                case 2:
                    ////Log.i("test",""+fragmentPage);
                    return HistoryPlot.newInstance(fragmentPage);//venueTab.newInstance(fragmentPage);// new twitterTab();
                default:
                    return null;
            }
        }

        /**
         * @return the title which represents this tab. In this sample this is used directly by
         * {@link PagerAdapter#getPageTitle(int)}
         */
        CharSequence getTitle() {
            return mTitle;
        }

        int getIndicatorColor() {
            return mIndicatorColor;
        }

        int getDividerColor() {
            return mDividerColor;
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        // BEGIN_INCLUDE (populate_tabs)
        /**
         * Populate our tab list with tabs. Each item contains a title, indicator color and divider
         * color, which are used by {@link SlidingTabLayout}.
         */
        mTabs.add(new SamplePagerItem(
                "",//getString(R.string.near_tab), // Title
                Color.parseColor("#D80000"), //"#2A91DD"), // Indicator color
                Color.TRANSPARENT // Div0ider color
        ));

        mTabs.add(new SamplePagerItem(
                "",//getString(R.string.top_tab), // Title
                Color.parseColor("#5FA9DD"), // Indicator color
                Color.TRANSPARENT // Divider color
        ));
        mTabs.add(new SamplePagerItem(
                "",//getString(R.string.top_tab), // Title
                Color.parseColor("#DEDC16"), // Indicator color
                Color.TRANSPARENT // Divider color
        ));
        drawableTab.add(R.drawable.ic_magna_graph);
        drawableTab.add(R.drawable.ic_bars_graph);
        drawableTab.add(R.drawable.ic_lines_graph);

        // END_INCLUDE (populate_tabs)
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.magnet_viewpager, container, false);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager mViewPager;
        tempTabsLayout mTabsLayout;
        mViewPager = view.findViewById(R.id.viewpager_tabs);
        venue_tabs_adapter mAdapter = new venue_tabs_adapter(getChildFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabsLayout = view.findViewById(R.id.venue_tabs);
        mTabsLayout.setTabNumber(mTabs.size());
        mTabsLayout.setCustomTabView(R.layout.magnet_tab, R.id.tabTitle, R.id.tabImage);
        mTabsLayout.setViewPager(mViewPager);
        mTabsLayout.setCustomTabColorizer(new tempTabsLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }
        });
    }


    /**
     * Called when returning to the activity
     */


    protected class venue_tabs_adapter extends FragmentStatePagerAdapter {


        // private Activity mActivity;
        protected venue_tabs_adapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * This method will be invoked when a page is requested to create
         */
        @Override
        public Fragment getItem(int fragmentPage) {
            utils.hide_keyboard(mActivity);
            return mTabs.get(fragmentPage).createFragment(fragmentPage);


        }

        protected int getDrawable(int position) {
            return drawableTab.get(position);
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }
        // END_INCLUDE (pageradapter_getpagetitle)

    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}