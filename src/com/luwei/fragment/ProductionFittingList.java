package com.luwei.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luwei.adapters.FittingAdapter;
import com.luwei.domain.FittingMainifest;
import com.luwei.domain.ScrollSelect;
import com.luwei.init.URLs;
import com.luwei.potato.R;
import com.luwei.util.CustomListView;
import com.luwei.util.CustomListView.OnLoadMoreListener;
import com.luwei.util.CustomListView.OnRefreshListener;
import com.luwei.util.JsonArrayUTF8Request;

public class ProductionFittingList extends SherlockFragment {
	public ArrayList<FittingMainifest> al = new ArrayList<FittingMainifest>();
	public ProgressBar pb;
	public CustomListView mListView;
	private final int SUCCESS = 0;
	private final int FAIL = 1;
	private final int REFRESH = 2;
	private final int LOADMORE = 3;
	public  FittingAdapter fa = null;
	int mCount = 30;
	int mScroll = 1;
    private View rootView;//缓存Fragment view

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				pb.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
				al.addAll((ArrayList<FittingMainifest>) msg.obj);
				fa.notifyDataSetChanged();
				break;

			case FAIL:
				break;
				
			case REFRESH:
				mScroll = 1;
				al.clear();
				al.addAll((ArrayList<FittingMainifest>) msg.obj);
				fa.notifyDataSetChanged();
				mListView.onRefreshComplete();
				break;
				
			case LOADMORE:
				
				RequestQueue mQueue = Volley.newRequestQueue(getSherlockActivity());
				JsonArrayUTF8Request jsonArray = null;
				try {
					jsonArray = new JsonArrayUTF8Request(URLs.URL_Fittings, new JSONObject(new Gson().toJson(new ScrollSelect(mScroll,5))), new Listener<JSONArray>() {

						@Override
						public void onResponse(JSONArray response) {
							Type listType = new TypeToken<ArrayList<FittingMainifest>>() {
							}.getType();
							List<FittingMainifest> temp = new Gson().fromJson(
									response.toString(), listType);
							if(temp.size()!=5||mListView.getCount()>=mCount){
								al.addAll(temp);
								fa.notifyDataSetChanged();
								mListView.onLoadMoreComplete();
							}else{
								al.addAll(temp);
								fa.notifyDataSetChanged();
								mScroll++;
							}
						}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Message msg = new Message();
							msg.what = FAIL;
							handler.sendMessage(msg);
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mQueue.add(jsonArray);
				
				break;
			default:
				break;
			}
		}
		
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        if (rootView==null){
            rootView = inflater.inflate(R.layout.home_fitting, container,
                    false);

            mListView = (CustomListView) rootView.findViewById(R.id.fitting_listview);

            pb = (ProgressBar) rootView.findViewById(R.id.fitting_progress_header);

            fa = new FittingAdapter(al, getSherlockActivity());
            mListView.setAdapter(fa);
            init();
            getFittings(0,5,true);

            return rootView;
        }else{
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
            return rootView;
        }

	}
	
	public void init(){
		
		//下拉刷新监听，记得重置mScroll。
		mListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				getFittings(0,5,false);
			}
		});
		
		//加载更多监听，设置变量翻页次数，以及每页最大数量，注意这数量需要后后台SQL语句一致
		mListView.setOnLoadListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				Message msg = new Message();
				msg.what = LOADMORE;
				handler.sendMessage(msg);
			}
		});
		
	}
	
	public void getFittings(int raw,int max,final boolean flag){
		RequestQueue mQueue = Volley.newRequestQueue(getSherlockActivity());
		JsonArrayUTF8Request jsonArray = null;
		try {
			jsonArray = new JsonArrayUTF8Request(URLs.URL_Fittings, new JSONObject(new Gson().toJson(new ScrollSelect(raw,max))), new Listener<JSONArray>() {

				@Override
				public void onResponse(JSONArray response) {
					Type listType = new TypeToken<ArrayList<FittingMainifest>>() {
					}.getType();
					List<FittingMainifest> fms = new Gson().fromJson(
							response.toString(), listType);
					Message msg = new Message();
					if(flag){
						msg.what = SUCCESS;
					}else{
						msg.what = REFRESH;
					}
					msg.obj = fms;
					handler.sendMessage(msg);
				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Message msg = new Message();
					msg.what = FAIL;
					handler.sendMessage(msg);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mQueue.add(jsonArray);
	}
	
	public void getMore(int raw , int max){
		RequestQueue mQueue = Volley.newRequestQueue(getSherlockActivity());
		JsonArrayUTF8Request jsonArray = null;
		try {
			jsonArray = new JsonArrayUTF8Request(URLs.URL_Fittings, new JSONObject(new Gson().toJson(new ScrollSelect(raw,max))), new Listener<JSONArray>() {

				@Override
				public void onResponse(JSONArray response) {
					Type listType = new TypeToken<ArrayList<FittingMainifest>>() {
					}.getType();
					List<FittingMainifest> fms = new Gson().fromJson(
							response.toString(), listType);
					Message msg = new Message();
					msg.what = LOADMORE;
					msg.obj = fms;
					handler.sendMessage(msg);
				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Message msg = new Message();
					msg.what = FAIL;
					handler.sendMessage(msg);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mQueue.add(jsonArray);
	}
	
}
