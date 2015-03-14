package com.luwei.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.luwei.potato.R;
import com.luwei.ui.util.BadgeView;
import com.luwei.util.MyViewPagerAdapter;

/**
 * 购物车和收银台界面
 * 采用滑块和ViewPage + PagerFragment；
 * Created by luwei on 2015-2-9.
 */
public class ServerSale extends SherlockFragment {
    LinearLayout.LayoutParams lp;
    ViewPager myPager;
    CarePager carePager;
    PayforPager payforPager;
    TextView sale_payfor;
    TextView sale_care;
    MyViewPagerAdapter myViewPagerAdapter;
    ImageView ic_swip_actionbar;
    View rootView;
    LinearLayout linearLayout;
    BadgeView badgeView;
    int screen1_2;
    int offsetIndex = 0;
    boolean flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.sale_plan, container, false);
            sale_care = (TextView) rootView.findViewById(R.id.sale_care_text);
            linearLayout = (LinearLayout) rootView.findViewById(R.id.linear_badge_sale_care);
            sale_care.setTextColor(getResources().getColor(R.color.blue));
            sale_payfor = (TextView) rootView.findViewById(R.id.sale_payfor_text);
            ic_swip_actionbar = (ImageView) rootView.findViewById(R.id.ic_swip_actionbar);
            carePager = new CarePager();
            payforPager = new PayforPager();
            myViewPagerAdapter = new MyViewPagerAdapter(getChildFragmentManager());
            myViewPagerAdapter.setStateChangeListeren(new MyViewPagerAdapter.OnStateChangeListeren() {
                @Override
                public SherlockFragment onPositonChange(int position) {
                    if (position == 0) {
                        return carePager;
                    } else {
                        return payforPager;
                    }
                }
            });

            init();
            myPager = (ViewPager) rootView.findViewById(R.id.sale_page_vp);
            myPager.setOffscreenPageLimit(2);
            myPager.setAdapter(myViewPagerAdapter);
            //字体颜色变化就交给ViewPager自己的监听吧，自己写的都不知道执行什么去了。
            myPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    //positon指的是当前手势滑动方向的值，不是当前所在的position。我在onPageSelected 中赋值
                    //positon在第0页时候。offIndex也是0，这时候它的postion == 0 , 而不是1.这个规律只能Log中看。
                    // positionOffset(0~1)区间 就是偏移量比值 用positionOffset*screen1_2正好是屏幕一半宽度的比值   pixels 是像素值
//                    Log.d("swipe","postions"+position+"  offset"+positionOffset);
                    if (position == 0 && offsetIndex == 0) {
                        //第一页往第二页走
                        offsetIndex = position;
                        lp.leftMargin = (int) (positionOffset * screen1_2 + offsetIndex * screen1_2);
                    } else if (position == 0 && offsetIndex == 1) {
                        //第二页往第一页走
                        offsetIndex = position;
                        lp.leftMargin = (int) ((positionOffset - 1) * screen1_2 + offsetIndex * screen1_2);
                    }
                    ic_swip_actionbar.setLayoutParams(lp);
                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        flag = false;
                        sale_care.setTextColor(getResources().getColor(R.color.blue));
                        sale_payfor.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_dark));
                    } else if (position == 1) {

                        flag = true;
                        sale_payfor.setTextColor(getResources().getColor(R.color.blue));
                        sale_care.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_dark));
                    }
                    offsetIndex = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            return rootView;
        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
            return rootView;
        }
    }

    private void init() {
        Display display = getSherlockActivity().getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        screen1_2 = outMetrics.widthPixels / 2;
        lp = (LinearLayout.LayoutParams) ic_swip_actionbar.getLayoutParams();
        lp.width = screen1_2;
        ic_swip_actionbar.setLayoutParams(lp);
        if (badgeView == null) {
            badgeView = new BadgeView(getSherlockActivity().getBaseContext());
            badgeView.setBadgeCount(7);
            linearLayout.addView(badgeView);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Display display = getSherlockActivity().getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        screen1_2 = outMetrics.widthPixels / 2;
        lp.width = screen1_2;
        if (!flag) {
            lp.leftMargin = 0;
        } else {
            lp.leftMargin = screen1_2;
        }
        ic_swip_actionbar.setLayoutParams(lp);
        super.onConfigurationChanged(newConfig);
    }
}
