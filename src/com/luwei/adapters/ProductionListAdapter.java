package com.luwei.adapters;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.luwei.activity.CommitActivity;
import com.luwei.activity.InsertPhotoActivity;
import com.luwei.domain.MainImage;
import com.luwei.potato.R;
import com.luwei.ui.util.CircledNetworkImageView;
import com.luwei.ui.util.VolleyImageLoader;

import java.util.ArrayList;

public class ProductionListAdapter extends BaseAdapter {
    ArrayList<MainImage> imageWall;
    LayoutInflater mInflater;
    SherlockFragmentActivity context;
    ImageLoader imageLoader;
    private final int TYPE0 = 0;
    private final int TYPE1 = 1;
    ViewGroup.LayoutParams para;
    int screenWidth;

    public ProductionListAdapter(ArrayList<MainImage> imageWall, SherlockFragmentActivity context) {
        super();
        this.imageWall = imageWall;
        this.context = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        imageLoader = VolleyImageLoader.LoadImage(context);
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;

    }

    public static class Holder {
        CircledNetworkImageView avatar;
        NetworkImageView background;
        TextView time;
        TextView name;
        ImageView goodjob;
        ImageView commit;
        ImageView share;
        TextView comment_special;
    }

    public static class HolderTitle {
        ImageView custom_wall_load_id;
        ImageView custom_wall_share_id;
        ImageView custom_wall_camtera_id;

    }

    @Override
    public int getCount() {
        return imageWall.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE0;
        } else {
            return TYPE1;
        }

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        Holder holder = null;
        HolderTitle holderT = null;

        switch (type) {
            case TYPE0:
                if (convertView == null) {
                    holderT = new HolderTitle();
                    convertView = mInflater.inflate(R.layout.custom_wall_title_bar,
                            parent, false);
                    holderT.custom_wall_load_id = (ImageView) convertView
                            .findViewById(R.id.custom_wall_load_id);
                    holderT.custom_wall_share_id = (ImageView) convertView
                            .findViewById(R.id.custom_wall_share_id);
                    holderT.custom_wall_camtera_id = (ImageView) convertView
                            .findViewById(R.id.custom_wall_catera_id);
                    convertView.setTag(holderT);
                } else {
                    holderT = (HolderTitle) convertView.getTag();
                }
                break;

            case TYPE1:
                if (convertView == null) {
                    System.out.println("所在行" + position);
                    convertView = mInflater.inflate(R.layout.production_list_view, parent,
                            false);
                    holder = new Holder();
                    holder.avatar = (CircledNetworkImageView) convertView
                            .findViewById(R.id.custom_avatar);
                    holder.background = (NetworkImageView) convertView
                            .findViewById(R.id.custom_background);
                    holder.name = (TextView) convertView
                            .findViewById(R.id.custom_name);
                    holder.time = (TextView) convertView
                            .findViewById(R.id.custom_time);
                    holder.goodjob = (ImageView) convertView
                            .findViewById(R.id.custom_goodjob);
                    holder.commit = (ImageView) convertView
                            .findViewById(R.id.custom_commit);
                    holder.share = (ImageView) convertView
                            .findViewById(R.id.custom_share);
                    holder.comment_special = (TextView) convertView.findViewById(R.id.product_comment);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                break;
            default:
                break;
        }

        switch (type) {
            case TYPE0:
                holderT.custom_wall_load_id
                        .setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(context,
                                        InsertPhotoActivity.class));
                                context.overridePendingTransition(R.anim.push_right_in,
                                        R.anim.push_left_out);
                            }
                        });
                break;

            case TYPE1:
                final MainImage mi = imageWall.get(position);
                mi.setScreenWidth(screenWidth);
                holder.avatar.setDefaultImageResId(R.drawable.circle_avatar);
                holder.avatar.setErrorImageResId(R.drawable.circle_avatar);
                holder.avatar.setImageUrl(mi.getAvatar_url(), imageLoader);

                holder.background
                        .setDefaultImageResId(R.drawable.loading_background);
                holder.background.setErrorImageResId(R.drawable.loading_background);
                holder.background.setImageUrl(mi.getImage_url(), imageLoader);

                para = holder.background.getLayoutParams();
                para.width = screenWidth;
                para.height = screenWidth;
                holder.background.setLayoutParams(para);
                holder.comment_special.setText("点评  "+mi.getContext());
                holder.time.setText(mi.getTime());

                if (mi.getName() == null) {
                    holder.name.setText("用户名");
                    mi.setName("未知用户");
                } else {
                    holder.name.setText(mi.getName());
                }

                final Holder finalHolder = holder;
                holder.goodjob.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalHolder.goodjob.setImageResource(R.drawable.custom_love_n);
                        System.out.println("点击了1号");
                    }
                });
                holder.commit.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("MainImage", mi);
                        context.startActivity(new Intent(context,
                                CommitActivity.class).putExtras(bundle));
                    }
                });
                holder.share.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("点击了3号");
                    }
                });
                break;
            default:
                break;
        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

}
