package com.luwei.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.luwei.potato.R;
import com.luwei.ui.util.LockPreview;
import com.luwei.ui.util.NineLockView;

import java.util.Set;

/**
 * Created by luwei on 2015-1-28.
 */
public class ScreenLock extends SherlockFragment {
    NineLockView ninePoin;
    TextView title;
    LockPreview preview_gesture_lock;
    TextView manager_password;
    TextView forget_password;
    LinearLayout below_text;
    private DisplayMetrics metrics = new DisplayMetrics();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getSherlockActivity().setTheme(R.style.Sherlock___Theme_DarkActionBar);
        metrics = this.getResources().getDisplayMetrics();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nine_points, container, false);
        manager_password = (TextView) rootView.findViewById(R.id.manager_password);
        forget_password = (TextView) rootView.findViewById(R.id.forget_password);
        ninePoin = (NineLockView) rootView.findViewById(R.id.gesture_lock);
        title = (TextView) rootView.findViewById(R.id.gesture_lock_title);
        preview_gesture_lock = (LockPreview) rootView.findViewById(R.id.preview_gesture_lock);
        below_text = (LinearLayout) rootView.findViewById(R.id.below_text);
        final StringBuffer str = new StringBuffer();

        ninePoin.setPassWordChange(new NineLockView.OnPassWordSetListeren() {
            @Override
            public void onChange(final Set<Integer> secures) {
                if (secures == null) {
                    title.setText("连线过短");
                } else {
                    title.setText("连线正确");
                    preview_gesture_lock.setOnStateChangeListeren(new LockPreview.OnStateChangeListeren() {
                        @Override
                        public Set<Integer> setPoint() {
                            Log.d("checkSet", secures.toString() + "----------------");
                            return secures;
                        }
                    });
                    preview_gesture_lock.postInvalidate();
                }
            }
        });

        ninePoin.setResetListeren(new NineLockView.OnResetListeren() {
            @Override
            public void resetState() {
                title.setText("正在连线中");
                preview_gesture_lock.setOnStateChangeListeren(new LockPreview.OnStateChangeListeren() {
                    @Override
                    public Set<Integer> setPoint() {
                        return null;
                    }
                });
                preview_gesture_lock.postInvalidate();
            }
        });

        ninePoin.setSizeListeren(new NineLockView.OnSizeListeren() {

            @Override
            public void setSize(NineLockView.Point[][] points) {
                float offsetsY, offsetsX;
                /* 获取宽高 */
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                if (width > height) {
                    offsetsY = -height / 8;
                    offsetsX = (width - height) / 2;
                    width = height;
                    title.setVisibility(View.GONE);
                    preview_gesture_lock.setVisibility(View.GONE);
                    below_text.setVisibility(View.GONE);
                } else {
                    offsetsX = 0;
                    offsetsY = 0;
                    height = width;
                    title.setVisibility(View.VISIBLE);
                    preview_gesture_lock.setVisibility(View.VISIBLE);
                    below_text.setVisibility(View.VISIBLE);
                }

                //确定坐标

                points[0][0].setAttr(offsetsX + width / 4, offsetsY + height / 4);
                points[0][1].setAttr(offsetsX + width / 2, offsetsY + height / 4);
                points[0][2].setAttr(offsetsX + width * 3 / 4, offsetsY + height / 4);

                points[1][0].setAttr(offsetsX + width / 4, offsetsY + height / 2);
                points[1][1].setAttr(offsetsX + width / 2, offsetsY + height / 2);
                points[1][2].setAttr(offsetsX + width * 3 / 4, offsetsY + height / 2);

                points[2][0].setAttr(offsetsX + width / 4, offsetsY + height * 3 / 4);
                points[2][1].setAttr(offsetsX + width / 2, offsetsY + height * 3 / 4);
                points[2][2].setAttr(offsetsX + width * 3 / 4, offsetsY + height * 3 / 4);
            }
        });
        return rootView;
    }

}
