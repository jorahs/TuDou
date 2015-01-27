package com.luwei.network;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luwei.domain.MainImage;
import com.luwei.domain.ScrollSelect;
import com.luwei.init.URLs;
import com.luwei.util.JsonArrayUTF8Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import  com.luwei.navignation.util.ToastUtil;
/**
 * 通过handler执行异步数据加载
 * 
 * @author luwei
 * 
 */
public class ImageListData {
	public static int SUCCESS = 0;
	public static int FAILURE = 1;
	public static int UPDATE = 2;
	public static int ADD = 3;

	public static void executeDataLoad(final Handler handler, final Context context,final int raw,final int max) {
		RequestQueue mQueue = Volley.newRequestQueue(context);
		JsonArrayUTF8Request jsonArray = null;
		try {
			jsonArray = new JsonArrayUTF8Request(URLs.URL_MainImage,new JSONObject(new Gson().toJson(new ScrollSelect(raw,max))),
					new Response.Listener<JSONArray>() {
						@Override
						public void onResponse(JSONArray response) {
							Type listType = new TypeToken<ArrayList<MainImage>>() {
							}.getType();
							List<MainImage> al = new Gson().fromJson(
									response.toString(), listType);
							Message msg = new Message();
							msg.what = SUCCESS;
							msg.obj = al;
							handler.sendMessage(msg);
						}
					}, 
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.d("DEBUG", "访问失败");
                            ToastUtil.show(context,"无法加载数据");
                            Message msg = new Message();
							msg.what = FAILURE;
							handler.sendMessage(msg);
						}
					});
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mQueue.add(jsonArray);
	}
	
	public static void executeUpdate(final Handler handler, final Context context,final int raw,final int max) {
		RequestQueue mQueue = Volley.newRequestQueue(context);
		JsonArrayUTF8Request jsonArray = null;
		try {
			jsonArray = new JsonArrayUTF8Request(URLs.URL_MainImage,new JSONObject(new Gson().toJson(new ScrollSelect(raw,max))),
					new Response.Listener<JSONArray>() {
						@Override
						public void onResponse(JSONArray response) {
							Type listType = new TypeToken<ArrayList<MainImage>>() {
							}.getType();
							List<MainImage> al = new Gson().fromJson(
									response.toString(), listType);
							Message msg = new Message();
							msg.what = UPDATE;
							msg.obj = al;
							handler.sendMessage(msg);
						}
					}, 
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.d("DEBUG", "访问失败");
                            ToastUtil.show(context,"无法加载数据");
							Message msg = new Message();
							msg.what = FAILURE;
							handler.sendMessage(msg);
						}
					});
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mQueue.add(jsonArray);
	}
	
	public static void executeLoadMore(final Handler handler, final Context context,final int raw,final int max) {
		RequestQueue mQueue = Volley.newRequestQueue(context);
		JsonArrayUTF8Request jsonArray = null;
		try {
			jsonArray = new JsonArrayUTF8Request(URLs.URL_MainImage,new JSONObject(new Gson().toJson(new ScrollSelect(raw,max))),
					new Response.Listener<JSONArray>() {
						@Override
						public void onResponse(JSONArray response) {
							Type listType = new TypeToken<ArrayList<MainImage>>() {
							}.getType();
							List<MainImage> al = new Gson().fromJson(
									response.toString(), listType);
							Message msg = new Message();
							msg.what = ADD;
							msg.obj = al;
							handler.sendMessage(msg);
						}
					}, 
					new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Log.d("DEBUG", "访问失败");
                            ToastUtil.show(context,"无法加载数据");
							Message msg = new Message();
							msg.what = FAILURE;
							handler.sendMessage(msg);
						}
					});
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mQueue.add(jsonArray);
	}
}
