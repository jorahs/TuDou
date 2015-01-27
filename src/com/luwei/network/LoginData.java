package com.luwei.network;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.luwei.activity.FrameActivity;
import com.luwei.domain.UserInfo;
import com.luwei.init.URLs;
import com.luwei.util.JsonUTF8Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 登录界面调用的发送数据到服务器器代码段 所有用到HttpClient的类都在这个包下。 这个类除了发数据还将Bean发给了MainActivity
 * 
 * @author luwei
 * 
 */
public class LoginData {
	private static final String DEBUG = "DEBUG";

	public void login_user(String userId, HashMap<String, Object> userInfo,
			Context context) {
		Gson gson = new Gson();
		Intent intent = new Intent(context, FrameActivity.class);
		Bundle extras = new Bundle();
		RequestQueue mQueue = Volley.newRequestQueue(context);
		final UserInfo loginuser = new UserInfo(); //loginuser设置了3个值。sharepreference中也这样添加
		loginuser.setNick_name(userInfo.get("nickname") + "");
		loginuser.setPreId(userId);
		loginuser.setAvatar_url(userInfo.get("figureurl_qq_2") + "");
        loginuser.setCity(userInfo.get("city")+"");
        loginuser.setProvince(userInfo.get("province")+"");
		JSONObject obj = null;
		try {
			obj = new JSONObject(gson.toJson(loginuser));
		} catch (JSONException e) {
			Log.d("DEBUG", "JSON转换失败");
			e.printStackTrace();
		}

		JsonUTF8Request jsonUTF8Request = new JsonUTF8Request(Method.POST,
				URLs.URL_Login, obj, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("DEBUG", response.toString());
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("DEBUG", error.getMessage(), error);
					}
				}) {

		};

		mQueue.add(jsonUTF8Request);
		extras.putSerializable("UserInfo", loginuser);
		intent.putExtras(extras);
		context.startActivity(intent);

	}

}
