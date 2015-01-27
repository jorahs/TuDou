package com.luwei.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.luwei.adapters.PhotoImageAdapter;
import com.luwei.domain.GrallyPictureBean;
import com.luwei.domain.ImageFloder;
import com.luwei.potato.R;
import com.luwei.ui.util.ImageDefine;
import com.luwei.util.NativeImageLoader;
import com.luwei.util.NativeImageLoader.NativeImageCallBack;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PhotoGrallyActivity extends SherlockActivity {
    private String image_id;
    private HashMap<String, Boolean> mSelectMap = new HashMap<String, Boolean>();
    private TextView photo_preview;
    private Spinner spinner;
    private Point mPoint = new Point(1, 1);
    private final static int SCAN_OK = 1;
    private ProgressDialog mProgressDialog;
    private HashMap<String, ArrayList<GrallyPictureBean>> mGruopMap = new HashMap<String, ArrayList<GrallyPictureBean>>();
    private List<ImageFloder> list = new ArrayList<ImageFloder>();
    private List<GrallyPictureBean> AllPatch = new ArrayList<GrallyPictureBean>();
    private List<GrallyPictureBean> TmpPatch = new ArrayList<GrallyPictureBean>();
    private GridView mGridView;
    private PhotoImageAdapter adapter;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCAN_OK:
                    mProgressDialog.dismiss();
                    list = subGroupOfImage(mGruopMap);
                    if (list.size() == 1 && list.get(0).getTopImagePath() == null) {
                        Toast.makeText(getApplicationContext(), "对不起你还没有图片", Toast.LENGTH_SHORT)
                                .show();
                    }
                    spinnerconf(list);

                    break;
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//	      super.onActivityResult(requestCode, resultCode, data);  
        switch (resultCode) {
            case RESULT_CANCELED:
                Toast.makeText(this, "未正确返回结果.", Toast.LENGTH_LONG).show();
                break;

            case RESULT_OK:
                System.out.println("返回selectmap");
                mSelectMap.clear();
                mSelectMap.putAll((HashMap<String, Boolean>) data.getSerializableExtra("backSelectMap"));
                adapter.notifyDataSetChanged();
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photography);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        photo_preview = (TextView) findViewById(R.id.photo_preview);
        actionBar.setTitle("相册");
        mGridView = (GridView) findViewById(R.id.child_grid);
        spinner = (Spinner) findViewById(R.id.picture_category);
        long start = System.currentTimeMillis();
        getImages();
        Log.d("DEBUG", "获取时间差" + (System.currentTimeMillis() - start) + "");
        previewEvent();

    }

    /**
     * 预览    点击事件
     */
    private void previewEvent() {
        photo_preview.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhotoGrallyActivity.this, PhotoDetailActivity.class);
                HashMap<String, Boolean> newMap = getNewSelectMap();
                intent.putExtra("patchs",
                        (String[]) newMap.keySet().toArray(new String[newMap.size()]));
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectMap", mSelectMap);
                intent.putExtras(bundle);
                PhotoGrallyActivity.this.startActivityForResult(intent, 0);
            }
        });
        photo_preview.setClickable(false);
    }

    /**
     * 获取新的选择集合
     *
     * @return
     */
    private HashMap<String, Boolean> getNewSelectMap() {
        HashMap<String, Boolean> postionList = new HashMap<String, Boolean>();
        for (Iterator<Map.Entry<String, Boolean>> it = mSelectMap.entrySet()
                .iterator(); it.hasNext(); ) {
            Map.Entry<String, Boolean> entry = it.next();
            if (entry.getValue()) {
                postionList.put(entry.getKey(), true);
            }
        }
        return postionList;
    }

    /**
     * 底部的Spinner 适配器
     * <p/>
     * Spinner在得到Activity的图片数据后开始加载GrapView
     *
     * @param list
     */
    private void spinnerconf(final List<ImageFloder> list) {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item) {

            @Override
            public int getCount() {
                return list.size();
            }

            //Spinner 静止的视图
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (null == view) {
                    view = LayoutInflater.from(this.getContext()).inflate(
                            android.R.layout.simple_list_item_1, null);
                    ((TextView) view).setText(list.get(position)
                            .getFolderName());
                }
                final LayoutParams params = new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                view.setLayoutParams(params);
                return view;
            }

            /**
             * 作为Spinner 适配器的缓存视图，其实可以不用。
             */


            //Spinner 的拉动视图
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                ImageView mImageView = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(this.getContext())
                            .inflate(R.layout.dropdownview, null);
                }

                mImageView = (ImageView) convertView
                        .findViewById(R.id.photo_proview_view);


                TextView title = (TextView) convertView
                        .findViewById(R.id.dropdowntext);

                title.setText(list.get(position).getFolderName());

                TextView count = (TextView) convertView
                        .findViewById(R.id.photoCount);

                if (position == 0) {
                    count.setText("");
                    mImageView.setTag("moto");
                    loadImage(list.get(position + 1).getTopImageId(), list.get(position + 1).getTopImagePath(), convertView, position);
                    return convertView;
                } else {
                    mImageView.setTag(list.get(position).getTopImageId());
                    count.setText(list.get(position).getImageCounts() + "张");
                }

                loadImage(list.get(position).getTopImageId(), list.get(position).getTopImagePath(), convertView, position);

                return convertView;
            }

            //在拉动视图中加载图片
            private void loadImage(final String image_id_local, String path_loacl, final View convertView
                    , final int position) {
                ImageView imageView = null;

                if (position != 0) {
                    imageView = (ImageView) convertView.findViewWithTag(image_id_local);
                    int height = imageView.getLayoutParams().height;
                    mPoint.set(height, height);
                    NativeImageLoader.getInstance()
                            .loadNativeImage(PhotoGrallyActivity.this, image_id_local, path_loacl, mPoint,
                                    new NativeImageCallBack() {
                                        @Override
                                        public void onImageLoader(Bitmap bitmap,
                                                                  String image_id) {
                                            ImageView mImageView = (ImageView) convertView.findViewWithTag(image_id);
                                            if (bitmap != null && mImageView != null) {
                                                mImageView.setImageBitmap(bitmap);
                                            }
                                        }
                                    });
                } else {
                    imageView = (ImageView) convertView.findViewWithTag("moto");
                    int height = imageView.getLayoutParams().height;
                    mPoint.set(height, height);
                    NativeImageLoader.getInstance()
                            .loadNativeImage(PhotoGrallyActivity.this, image_id_local, path_loacl, mPoint,
                                    new NativeImageCallBack() {
                                        @Override
                                        public void onImageLoader(Bitmap bitmap,
                                                                  String image_id) {
                                            ImageView mImageView = (ImageView) convertView.findViewWithTag("moto");
                                            if (bitmap != null && mImageView != null) {
                                                mImageView.setImageBitmap(bitmap);
                                            }
                                        }
                                    });
                }
            }

        };

        //将适配器加载
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        //将Activity中的GridView加载适配器
        if (AllPatch.size() != 0) {
            TmpPatch.addAll(AllPatch);
            adapter = new PhotoImageAdapter(PhotoGrallyActivity.this, TmpPatch, photo_preview, mSelectMap);
            mGridView.setAdapter(adapter);
        }


        //Spinner点击事件 用于改变GridView中adapter的数据，并同步。
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (adapter != null) {
                    if (position != 0) {
                        //通过Id获取文件名在通过文件名获取Map中的List集合也就是图片路径
                        ArrayList<GrallyPictureBean> grallyPictureBeans = mGruopMap.get(list.get(
                                position).getFolderName());
                        TmpPatch.clear();
                        TmpPatch.addAll(grallyPictureBeans);
                        adapter.notifyDataSetChanged();
                    } else {
                        TmpPatch.clear();
                        TmpPatch.addAll(AllPatch);
                        adapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }


    /**
     * 获取手机内存中的图片信息
     */
    private void getImages() {
        //判读是否挂载SDcare
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "获得图片", Toast.LENGTH_SHORT).show();
            return;
        }

        //设置进度条，在handler中取消进度条
        mProgressDialog = ProgressDialog.show(this, null, "....");

        //在子线程中进行读取操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //注意Uri为image的EXTRNAL_CONTENT_URI   具体查阅文档
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = PhotoGrallyActivity.this
                        .getContentResolver();

                // content provide 查询参数为image/jpeg 或是image/png的数据
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                //首先获取到图片的文件路径，以及图片的id，获取id目的是获取到图片的缩略图
                while (mCursor.moveToNext()) {
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    image_id = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media._ID));

                    Log.d("_ID", "图片的内存数据库id，为了获取缩略图关联" + image_id);

                    // 获取当前文件的父文件夹的名词，就是文件夹名
                    String parentName = new File(path).getParentFile()
                            .getName();

                    // 如果文件夹存在则将文件路径存储到list中，不存在则添加文件夹名到map中，map包含list.
                    //包含的目的是hashMap可以得出count 用以显示在spinner中。
                    if (!mGruopMap.containsKey(parentName)) {
                        ArrayList<GrallyPictureBean> gpb = new ArrayList<GrallyPictureBean>();
                        gpb.add(new GrallyPictureBean(image_id, path));
                        mGruopMap.put(parentName, gpb);
                    } else {
                        mGruopMap.get(parentName).add(new GrallyPictureBean(image_id, path));
                    }
                }

                mCursor.close();
                // 发送扫描完成的请求
                mHandler.sendEmptyMessage(SCAN_OK);

            }
        }).start();

    }

    /**
     * 目的是将文件内容转换到bean中
     *
     * @param mGruopMap
     * @return
     */
    private List<ImageFloder> subGroupOfImage(
            HashMap<String, ArrayList<GrallyPictureBean>> mGruopMap) {
        List<ImageFloder> mList = new ArrayList<ImageFloder>();

        //防止图库里面没有图片，Spinner item 依旧添加一个空imageFloder
        //当有图片时候，为了第一culmin是"所有图片项"同样添加空imageFloder
        if (mGruopMap.size() == 0) {
            ImageFloder ib = new ImageFloder();
            ib.setFolderName("所有图片");
            mList.add(ib);
            return mList;
        } else {
            ImageFloder ib = new ImageFloder();
            ib.setFolderName("所有图片");
            mList.add(ib);
        }

        Iterator<Map.Entry<String, ArrayList<GrallyPictureBean>>> it = mGruopMap.entrySet()
                .iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<GrallyPictureBean>> entry = it.next();
            ImageFloder mImageFloder = new ImageFloder();
            String key = entry.getKey();
            ArrayList<GrallyPictureBean> value = entry.getValue();
            AllPatch.addAll(value);    //AllPatch init
            mImageFloder.setFolderName(key);
            mImageFloder.setImageCounts(value.size());
            mImageFloder.setTopImagePath(value.get(0).getPatch());
            mImageFloder.setTopImageId(value.get(0).getImage_id());
            mList.add(mImageFloder);
        }

        return mList;
    }

    // -----------------------------------滑动操作
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
