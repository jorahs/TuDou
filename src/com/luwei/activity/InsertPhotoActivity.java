package com.luwei.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.luwei.potato.R;

public class InsertPhotoActivity extends SherlockActivity {
	String nickName;
	String avatarUrl;
	InsertPhotoActivity context = this;
	NetworkImageView imageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("添加内容");
		
		SharedPreferences setting = getSharedPreferences("profile", 0);
		if (setting.contains("nickName")) {
			nickName = setting.getString("nickName", "");
			avatarUrl = setting.getString("avatarUrl", "");
		}
		setContentView(R.layout.custom_wall_add);
		imageView = (NetworkImageView) findViewById(R.id.custome_wall_add_avatar);
		imageView.setDefaultImageResId(R.drawable.avatar_male);
		imageView.setErrorImageResId(R.drawable.avatar_male);
		if (nickName != null) {
			TextView custome_wall_name = (TextView) findViewById(R.id.custome_wall_name);
			custome_wall_name.setText(nickName);
			drawImage(avatarUrl);
		}
		ImageView iv = (ImageView) findViewById(R.id.add_custom_pictures);
		iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				context.startActivity(new Intent(context,PhotoGrallyActivity.class));
				context.overridePendingTransition(R.anim.push_right_in,
						R.anim.push_left_out);
			}
		});
	}

	private void drawImage(String avatarUrl) {
		RequestQueue mQueue = Volley.newRequestQueue(this);  
		ImageLoader imageLoader = new ImageLoader(mQueue, new ImageCache() {  
		    @Override  
		    public void putBitmap(String url, Bitmap bitmap) {  
		    }  
		  
		    @Override  
		    public Bitmap getBitmap(String url) {  
		        return null;  
		    }  
		}); 
		imageView.setImageUrl(avatarUrl, imageLoader);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.back_left_in,
					R.anim.back_right_out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish(); // finish activity
			overridePendingTransition(R.anim.back_left_in,
					R.anim.back_right_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
