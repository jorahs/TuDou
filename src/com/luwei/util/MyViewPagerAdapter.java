package com.luwei.util;

/**
 * Created by Administrator on 2015-2-16.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * 添加ViewPager的FragmentAdapater
 * 本来可以写成内部类的，但用接口方式返回对象，做个实验
 */
public class MyViewPagerAdapter extends FragmentPagerAdapter {
    OnStateChangeListeren myStateChangeListeren;

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (myStateChangeListeren != null) {
            if (position == 0) {
                return myStateChangeListeren.onPositonChange(position);
            } else
                return myStateChangeListeren.onPositonChange(position);
        } else {
            return null;
        }
    }

    /**
     * 注意返回值是固定写的
     *
     * @return ViewPager的个数
     */
    @Override
    public int getCount() {
        return 2;
    }

    public void setStateChangeListeren(OnStateChangeListeren myStateChangeListeren) {
        if (this.myStateChangeListeren == null) {
            this.myStateChangeListeren = myStateChangeListeren;
        }
    }

    public interface OnStateChangeListeren {
        public SherlockFragment onPositonChange(int position);
    }
}
