package com.luwei.adapters;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.luwei.domain.FittingMainifest;
import com.luwei.potato.R;
import com.luwei.ui.util.LruImageCache;
import com.luwei.ui.util.RectNetworkImageView;

import java.util.ArrayList;

public class FittingAdapter extends BaseAdapter {
    ArrayList<FittingMainifest> al;
    Context context;
    LayoutInflater mInflater;
    ImageLoader imageLoader;

    public FittingAdapter(ArrayList<FittingMainifest> al, Context context) {
        super();
        this.al = al;
        this.context = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        RequestQueue mQueue = Volley.newRequestQueue(context);

        LruImageCache lruImageCache = LruImageCache.instance();
        imageLoader = new ImageLoader(mQueue, lruImageCache);
    }

    @Override
    public int getCount() {
        return al.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0x2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 ){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int TYPE = getItemViewType(position);
            FittingMainifest fm = al.get(position);
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = mInflater.inflate(R.layout.fitting_mainifest, parent, false);
                holder.product_count_fitting = (TextView) convertView.findViewById(R.id.product_count_fitting);
                holder.product_name_fitting = (TextView) convertView.findViewById(R.id.product_name_fitting);
                holder.product_fitting_time = (TextView) convertView.findViewById(R.id.product_fitting_time);
                holder.product_fitting_more = (ImageView) convertView.findViewById(R.id.product_fitting_more);
                holder.product_function_fitting = (TextView) convertView.findViewById(R.id.product_function_fitting);
                holder.vn = (RectNetworkImageView) convertView.findViewById(R.id.fitting_img);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.vn.setDefaultImageResId(R.drawable.prepare_loading);
            holder.vn.setErrorImageResId(R.drawable.prepare_loading);
            holder.vn.setImageUrl(fm.getImage_url(), imageLoader);

            holder.product_count_fitting.setText("剩余数量:" + fm.getCount() + "");
            holder.product_fitting_time.setText(fm.getTime()+"出品");
            holder.product_name_fitting.setText("型号:" + fm.getName());
            holder.product_function_fitting.setText(fm.getFunction());
            return convertView;

    }

    private static class Holder {
        RectNetworkImageView vn;
        TextView product_count_fitting;
        TextView product_name_fitting;
        TextView product_fitting_time;
        TextView product_function_fitting;
        ImageView product_fitting_more;
    }
}
