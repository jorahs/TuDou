package com.luwei.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.luwei.domain.MainImage;
import com.luwei.potato.R;
import com.luwei.ui.util.CircledNetworkImageView;
import com.luwei.ui.util.VolleyImageLoader;

public class CommitActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        setContentView(R.layout.custom_commit_walll);
        Bundle bundle = getIntent().getExtras();
        UIinit(bundle);
    }

    /*
    对评论UI进行初始化操作。
     */
    private void UIinit(Bundle bundle) {
        ImageLoader imageLoader = VolleyImageLoader.LoadImage(this);
        MainImage mi = (MainImage) bundle.get("MainImage");
        String avatar_url = mi.getAvatar_url();
        String image_url = mi.getImage_url();
        CircledNetworkImageView avatar = (CircledNetworkImageView) findViewById(R.id.commit_avatar);
        NetworkImageView background = (NetworkImageView) findViewById(R.id.commit_background);
        if (avatar_url != null) {
            avatar.setImageUrl(avatar_url, imageLoader);
        }
        if (image_url != null) {
            background.setImageUrl(image_url, imageLoader);
            ViewGroup.LayoutParams para = background.getLayoutParams();
            para.height = para.width = mi.getScreenWidth();
            background.setLayoutParams(para);
        }
        TextView name = (TextView) findViewById(R.id.commit_name);
        TextView time = (TextView) findViewById(R.id.commit_time);
        if (time != null) {
            time.setText(mi.getTime());
        }
        if (name != null) {
            name.setText(mi.getName()); //姓名UI
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
