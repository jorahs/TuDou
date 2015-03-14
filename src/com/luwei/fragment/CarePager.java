package com.luwei.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.luwei.potato.R;

/**
 * Created by Administrator on 2015-2-16.
 */
public class CarePager extends SherlockFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.care_pager, container, false);
        return rootView;
    }
}
