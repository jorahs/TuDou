package com.luwei.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.luwei.activity.PhotoDetailActivity;
import com.luwei.activity.PhotoGrallyActivity;
import com.luwei.domain.GrallyPictureBean;
import com.luwei.potato.R;
import com.luwei.ui.util.ImageDefine;
import com.luwei.ui.util.ImageDefine.OnMeasureListener;
import com.luwei.util.NativeImageLoader;
import com.luwei.util.NativeImageLoader.NativeImageCallBack;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PhotoImageAdapter extends BaseAdapter {
    private Point mPoint = new Point(1, 1);
    private HashMap<String, Boolean> mSelectMap;
    protected LayoutInflater mInflater;
    private TextView photo_preview;
    private PhotoGrallyActivity context;
    List<GrallyPictureBean> gpb;
    private boolean flag = false;

    public PhotoImageAdapter(PhotoGrallyActivity context, List<GrallyPictureBean> gpb, TextView photo_preview, HashMap<String, Boolean> mSelectMap) {
        this.gpb = gpb;
        mInflater = LayoutInflater.from(context);
        this.photo_preview = photo_preview;
        this.context = context;
        this.mSelectMap = mSelectMap;
    }

    @Override
    public int getCount() {
        return gpb.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        final ViewHolder viewHolder;
        String path = gpb.get(position).getPatch();
        String image_id = gpb.get(position).getImage_id();

        Log.d("ImageId", "图片Id" + image_id);

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.grid_grally_item, null);

            viewHolder = new ViewHolder();

            viewHolder.mImageView = (ImageDefine) convertView
                    .findViewById(R.id.child_image);

            viewHolder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.child_checkbox);

            viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImageView
                    .setImageResource(R.drawable.friends_sends_pictures_no);
        }

        // 设置图片点击事件，启动浏览全图的Activity，需要传入当前的List
        viewHolder.mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoDetailActivity.class);
                ArrayList<String> strs = new ArrayList<String>();
                for (GrallyPictureBean grallys : gpb) {
                    strs.add(grallys.getPatch());
                }
                intent.putExtra("patchs",
                        (String[]) strs.toArray(new String[gpb.size()]));
                intent.putExtra("image_position", position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectMap", mSelectMap);
                intent.putExtras(bundle);
                context.startActivityForResult(intent, 0);
            }
        });

        if (flag) {
            viewHolder.mImageView.setTag(image_id);
        }


        viewHolder.mCheckBox
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (!mSelectMap.containsKey(gpb.get(position))
                                || !mSelectMap.get(gpb.get(position))) {
                            addAnimation(viewHolder.mCheckBox);
                        }
                        mSelectMap.put(gpb.get(position).getPatch(), isChecked);
                        int i = getSelectItems().size();
                        if (i > 0) {
                            photo_preview.setText("预览" + "(" + i + ")");
                            photo_preview
                                    .setTextColor(context
                                            .getResources()
                                            .getColor(
                                                    R.color.abs__background_holo_dark));
                            photo_preview.setClickable(true);
                        } else {
                            photo_preview.setClickable(false);
                            photo_preview.setText("预览");
                            photo_preview.setTextColor(context.getResources()
                                    .getColor(R.color.remote));
                        }
                    }
                });

        viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(gpb
                .get(position).getPatch()) ? mSelectMap.get(gpb.get(position).getPatch()) : false);


        if (flag) {
            NativeImageLoader.getInstance().loadNativeImage(context, image_id, path,
                    mPoint,
                    new NativeImageCallBack() {
                        @Override
                        public void onImageLoader(Bitmap bitmap, String image_id) {
                            ImageView mImageView = (ImageView) viewHolder.mImageView
                                    .findViewWithTag(image_id);
                            if (bitmap != null && mImageView != null) {
                                mImageView.setImageBitmap(bitmap);
                            }
                        }
                    });
        }
        flag = true;
        return convertView;
    }

    /**
     * @param view
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f,
                1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }

    /**
     * @return 被选中的Patch集合
     */
    public List<String> getSelectItems() {
        List<String> postionList = new ArrayList<String>();
        for (Iterator<Map.Entry<String, Boolean>> it = mSelectMap.entrySet()
                .iterator(); it.hasNext(); ) {
            Map.Entry<String, Boolean> entry = it.next();
            if (entry.getValue()) {
                postionList.add(entry.getKey());
            }
        }
        return postionList;
    }

    public static class ViewHolder {
        public ImageDefine mImageView;
        public CheckBox mCheckBox;
    }

}
