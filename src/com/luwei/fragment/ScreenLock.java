package com.luwei.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.luwei.potato.R;
import com.luwei.ui.util.NinePointView;

/**
 * Created by luwei on 2015-1-28.
 */
public class ScreenLock extends SherlockFragment {
    NinePointView ninePoin;
    TextView title;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nine_points,container,false);
        ninePoin = (NinePointView) rootView.findViewById(R.id.ninePointLock);
        title = (TextView) rootView.findViewById(R.id.passwordNotify);
        ninePoin.setPassWordChange(new NinePointView.OnPassWordSetListeren() {
            @Override
            public void onChange(String str) {
                if (str == null){
                    title.setText("输入的连线过短");
                }else{
                    title.setText("密码是:"+str);
                }
            }
        });
        return rootView;
    }
}
