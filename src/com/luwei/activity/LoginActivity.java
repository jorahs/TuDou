package com.luwei.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

import com.luwei.network.LoginData;
import com.luwei.potato.R;

public class LoginActivity extends Activity implements Callback,
		OnClickListener, PlatformActionListener {
	private static final int MSG_USERID_FOUND = 1;
	private static final int MSG_LOGIN = 2;
	private static final int MSG_AUTH_CANCEL = 3;
	private static final int MSG_AUTH_ERROR = 4;
	private static final int MSG_AUTH_COMPLETE = 5;
	private static final String TAG = "TAG";
	private HashMap<String, Object> hm;
	private String userId;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShareSDK.initSDK(this);
		setContentView(R.layout.third_party_login_page);
		init();
	}

	/**
	 * Animation效果
	 */
	public void init() {
		ImageView belowiv = (ImageView) findViewById(R.id.below_anim);
		findViewById(R.id.tvWeibo).setOnClickListener(this);
		findViewById(R.id.tvQq).setOnClickListener(this);
		findViewById(R.id.logout).setOnClickListener(this);
		Animation below = AnimationUtils.loadAnimation(this, R.anim.below_set);
		belowiv.startAnimation(below);

		ImageView frontiv = (ImageView) findViewById(R.id.front_anim);
		Animation front = AnimationUtils.loadAnimation(this, R.anim.front_set);
		frontiv.startAnimation(front);

		ImageView tvQq = (ImageView) findViewById(R.id.tvQq);
		Animation qq = AnimationUtils.loadAnimation(this, R.anim.welcome_alpha);
		tvQq.startAnimation(qq);

		ImageView tvWeibo = (ImageView) findViewById(R.id.tvWeibo);
		Animation weibo = AnimationUtils.loadAnimation(this,
				R.anim.welcome_alpha);
		tvWeibo.startAnimation(weibo);

		ImageView logout = (ImageView) findViewById(R.id.logout);
		Animation out = AnimationUtils
				.loadAnimation(this, R.anim.welcome_alpha);
		logout.startAnimation(out);
	}

	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}

	/**
	 * 登录图标的2个点击事件。
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tvWeibo: {
			authorize(new SinaWeibo(this));
		}
			break;

		case R.id.tvQq: {
			authorize(new QZone(this));
		}
			break;
		// 这里只是测试方便用下而已。
		case R.id.logout: {
			Log.d("DEBUG", "直接进入");
			Intent intent = new Intent(this, FrameActivity.class);
			startActivity(intent);
			onDestroy();
			finish();
		}
			break;
		}
	}

	private void authorize(Platform plat) {
		// 已经授权过的状态
		if (plat.isValid()) {
			userId = plat.getDb().getUserId(); // 这里的userId是返回回来的值。可以基于这个id创建本地账户
			// 目的是获取用户资料
			if (userId != null) {
				UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
				plat.setPlatformActionListener(this); // 必须执行的操作
				plat.showUser(null);
				return;
			}
		}

		// 没有授权，进行授权
		plat.setPlatformActionListener(this); // 必须执行的操作
		plat.SSOSetting(true); // 不使用sso授权方式.
//		plat.authorize(); //功能为导向
		plat.showUser(null); // 数据为导向 @param null 访问用户自身数据

	}

	/**
	 * ShareSDK的回调。如果网络访问失败会花销10秒才调用失败的代码
	 */
	/**
	 * 授权成功的回调
	 */
	@Override
	public void onComplete(Platform platform, int action,
			HashMap<String, Object> res) {
		Log.d("DEBUG", "--------callback success ----------" + res.toString());
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
			login(platform.getName(), platform.getDb().getUserId(), res);
		}
	}

	/**
	 * 授权失败的回调
	 */
	@Override
	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
		}
		t.printStackTrace();
	}

	/**
	 * 授权取消的回调
	 */
	@Override
	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
		}
	}

	/**
	 * 授权成功调用登录代码，进入MainActivity以及将数据发送给服务器
	 * 
	 * @param plat
	 * @param userId
	 * @param userInfo
	 */
	private void login(String plat, String userId,
			HashMap<String, Object> userInfo) {
		// Log.d(DEBUG, info);
		hm = userInfo;
		Message msg = new Message();
		msg.what = MSG_LOGIN;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	/**
	 * OverRaid的Handle，注意一个Activity出现2个Handle的话，会有异常,估计是线程堵塞。
	 */
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_USERID_FOUND: {
			Toast.makeText(this, R.string.userid_found, Toast.LENGTH_SHORT)
					.show();
		}
			break;
		case MSG_LOGIN: {
			String text = getString(R.string.logining, msg.obj);
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
			LoginData lu = new LoginData();
			lu.login_user(userId, hm, this);
			lu = null;
			onDestroy();
			finish();
		}
			break;
		case MSG_AUTH_CANCEL: {
			Toast.makeText(this, R.string.auth_cancel, Toast.LENGTH_SHORT)
					.show();
		}
			break;
		case MSG_AUTH_ERROR: {
			Toast.makeText(this, R.string.auth_error, Toast.LENGTH_SHORT)
					.show();
		}
			break;
		case MSG_AUTH_COMPLETE: {
			Toast.makeText(this, R.string.auth_complete, Toast.LENGTH_SHORT)
					.show();
		}
			break;
		}
		return false;
	}

}
