package com.luwei.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.luwei.adapters.ProductionListAdapter;
import com.luwei.domain.MainImage;
import com.luwei.network.ImageListData;
import com.luwei.potato.R;
import com.luwei.swipe.SwipeRefreshLayout;
import com.luwei.swipe.SwipeRefreshLayout.OnRefreshListener;

import java.util.ArrayList;

public class ProductionListFragment extends SherlockFragment implements
        OnRefreshListener {
    public static int SUCCESS = 0;
    public static int FAILURE = 1;
    public static int UPDATE = 2;
    public static int ADD = 3;
    SwipeRefreshLayout swipeRefreshLayout;
    ProductionListAdapter adapter;
    ProgressBar pd;
    int totalCount;
    int scrollCount = 1;
    LinearLayout footerview;
    ListView lv;
    ArrayList<MainImage> al;
    boolean flag = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            swipeRefreshLayout.setRefreshing(false);

            if (msg.what == SUCCESS) {
                pd.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                al = new ArrayList<MainImage>();
                al.add(new MainImage(0, null, null, null, null, null, null, 0));
                al.addAll((ArrayList<MainImage>) msg.obj);
                adapter = new ProductionListAdapter(al, getSherlockActivity());
                lv.setAdapter(adapter);
                flag = true;
            }

            if (msg.what == UPDATE) {
                al.clear();
                al.add(new MainImage(0, null, null, null, null, null, null, 0));
                al.addAll((ArrayList<MainImage>) msg.obj);
                adapter.notifyDataSetChanged();
                System.out.println("数量" + lv.getFooterViewsCount());
                if (lv.getFooterViewsCount() == 0) {
                    scrollCount = 1;
                    lv.addFooterView((LinearLayout) View.inflate(getSherlockActivity(),
                            R.layout.footer_view, null));
                }
            }

            if (msg.what == ADD) {
                if (((ArrayList<MainImage>) msg.obj).size() != 5
                        || adapter.getCount() >= totalCount) {
                    al.addAll((ArrayList<MainImage>) msg.obj);
                    adapter.notifyDataSetChanged();
                    lv.removeFooterView(footerview);
                }
                scrollCount++;
                al.addAll((ArrayList<MainImage>) msg.obj);
                adapter.notifyDataSetChanged();
                flag = true;
            }
        }
    };
    int lastItem;
    private View rootView;//缓存Fragment view

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            totalCount = 30;
            rootView = inflater.inflate(R.layout.home_imagebutton, container,
                    false);
            swipeRefreshLayout = (SwipeRefreshLayout) rootView
                    .findViewById(R.id.swip);
            swipeRefreshLayout.setOnRefreshListener(this);
            swipeRefreshLayout.setColorSchemeRes(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            pd = (ProgressBar) rootView.findViewById(R.id.progress_header);
            lv = (ListView) rootView.findViewById(R.id.lt_product);

            ImageListData.executeDataLoad(handler, getSherlockActivity(), 0, 5); // 读取数据
            footerview = (LinearLayout) View.inflate(getSherlockActivity(),
                    R.layout.footer_view, null);

            lv.addFooterView(footerview);

            lv.setOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                    switch (scrollState) {
                        case OnScrollListener.SCROLL_STATE_IDLE: // 不在滑动状态下
                            // 断定迁移转变到底部
                            if (lastItem == adapter.getCount()
                                    && lv.getLastVisiblePosition() == (lv.getCount() - 1)
                                    && flag) {
                                flag = false;
                                ImageListData.executeLoadMore(handler,
                                        getSherlockActivity(), scrollCount, 5);
                            }
                            break;
                    }

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    lastItem = firstVisibleItem + visibleItemCount - 1;
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

    @Override
    public void onRefresh() {
        ImageListData.executeUpdate(handler, getSherlockActivity(), 0, 5);
        handler.sendEmptyMessageDelayed(1, 5000);
    }

}
