package com.luwei.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.luwei.potato.R;

/**
 * Created by luwei on 2015-2-7.
 */
public class GesturePartment extends SherlockFragment {
    TextView manager_password;
    TextView forget_password;
    LinearLayout below_text;
    private DisplayMetrics metrics = new DisplayMetrics();
    private SherlockFragment manger = new ManagerPassword();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gesture_lock, container, false);
        manager_password = (TextView) rootView.findViewById(R.id.manager_password);
        forget_password = (TextView) rootView.findViewById(R.id.forget_password);
        below_text = (LinearLayout) rootView.findViewById(R.id.below_text);

        manager_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager_password.setTextColor(R.color.blue);
                android.support.v4.app.FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransitionStyle(R.anim.back_left_in);
                transaction.replace(R.id.ninelock_fragment, manger);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        android.support.v4.app.FragmentManager fragmentManager = getSherlockActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = (fragmentManager.findFragmentById(R.id.ninelock_fragment));
        if (fragment != null) {
            transaction.remove(fragment);
            transaction.commit();
        } else {
            transaction.remove(manger);
            transaction.commit();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        float width, height;
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        if (height > width) {
            below_text.setVisibility(View.VISIBLE);
        } else {
            below_text.setVisibility(View.GONE);
        }
    }


}
