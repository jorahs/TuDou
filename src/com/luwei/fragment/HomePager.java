package com.luwei.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.luwei.activity.FrameActivity;
import com.luwei.potato.R;
import com.luwei.ui.util.PagerSlidingTabStrip;

/*
主页面，使用FragmentPager来control childfragment
使用PagerSlidingTabStrip视图类来做Tabs
需要注意ChildFragment视图保存原视图不变
 */

public class HomePager extends SherlockFragment {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private View root;//缓存Fragment view

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (root == null) {
            root = inflater.inflate(R.layout.home_pager, container, false);

            tabs = (PagerSlidingTabStrip) root.findViewById(R.id.home_tabs);
            tabs.setBackgroundResource(R.color.white);
            tabs.setTextSize(28);
            tabs.setShouldExpand(true);
            pager = (ViewPager) root.findViewById(R.id.home_pager);
            pager.setOffscreenPageLimit(3);
            adapter = new MyPagerAdapter(getChildFragmentManager());
            pager.setAdapter(adapter);
            tabs.setViewPager(pager);

            tabs.setDefineTextColor(getResources().getColor(R.color.blue),
                    0); //第一个tab颜色设置

            ((FrameActivity) this.getSherlockActivity()).registerListeren(new FrameActivity.MyOnTouchListeren() {
                @Override
                public boolean onTouch(MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_MOVE:
                            Log.d("XY", event.getX() + "  " + event.getY());
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });



            tabs.setOnPageChangeListener(new OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    if (arg0 == 0) {
                        tabs.setDefineTextColor(getResources().getColor(R.color.blue),
                                arg0);
                        tabs.setDefineTextColor(getResources().getColor(R.color.remote),
                                1);
                        tabs.setDefineTextColor(getResources().getColor(R.color.remote),
                                2);
                        return;
                    }
                    if (arg0 == 1) {
                        tabs.setDefineTextColor(getResources().getColor(R.color.blue),
                                arg0);
                        tabs.setDefineTextColor(getResources().getColor(R.color.remote),
                                0);
                        tabs.setDefineTextColor(getResources().getColor(R.color.remote),
                                2);
                        return;
                    }
                    if (arg0 == 2) {
                        tabs.setDefineTextColor(getResources().getColor(R.color.blue),
                                arg0);
                        tabs.setDefineTextColor(getResources().getColor(R.color.remote),
                                0);
                        tabs.setDefineTextColor(getResources().getColor(R.color.remote),
                                1);
                        return;
                    }

                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });

            return root;
        } else {
            ViewGroup parent = (ViewGroup) root.getParent();
            if (parent != null) {
                parent.removeView(root);
            }
            return root;
        }

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = {"智能生活", "智能产品", "留言墙"};
        ProductionListFragment productionListFragment = new ProductionListFragment();
        ProductionFittingList productionFittingList = new ProductionFittingList();
        ProductionIntroduction productionIntroduction = new ProductionIntroduction();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (productionListFragment.isDetached())
                        return new ProductionListFragment();
                    else
                        return productionListFragment;

                case 1:
                    if (productionFittingList.isDetached())
                        return new ProductionFittingList();
                    else
                        return productionFittingList;

                case 2:
                    if (productionIntroduction.isDetached())
                        return new ProductionIntroduction();
                    return productionIntroduction;

                default:
                    return productionListFragment;
            }
        }

    }
}
