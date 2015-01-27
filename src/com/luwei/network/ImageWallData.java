package com.luwei.network;

import android.os.Handler;
import android.os.Message;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luwei.domain.Images;
import com.luwei.domain.ScrollSelect;
import com.luwei.init.URLs;
import com.luwei.navignation.util.ToastUtil;
import com.luwei.ui.util.PhotoScrollWall;
import com.luwei.util.JsonArrayUTF8Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ImageWallData {
	private static final int GETURL = 1;
	private static final int FAILURE = 2;
	public static void executeDataLoad(final Handler handler, final PhotoScrollWall context,final int raw,final int max) {
		RequestQueue mQueue = Volley.newRequestQueue(context.getContext());
		JsonArrayUTF8Request jsonArray = null;
		try {
			jsonArray = new JsonArrayUTF8Request(URLs.URL_ImageWall,new JSONObject(new Gson().toJson(new ScrollSelect(raw,max))),
					new Response.Listener<JSONArray>() {
						@Override
						public void onResponse(JSONArray response) {
							Type listType = new TypeToken<ArrayList<Images>>() {
							}.getType();
							List<Images> al = new Gson().fromJson(
									response.toString(), listType);
							context.setImages(al);
							Message msg = new Message();
							msg.what = GETURL;
							msg.obj = context;
							handler.sendMessage(msg);
						}
					}, 
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
                            ToastUtil.show(context.getContext(), "网络访问失败");
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
