package com.luwei.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.luwei.potato.R;
import com.luwei.ui.util.ZoomImageView;

public class PhotoDetailActivity extends SherlockActivity implements
        OnPageChangeListener {

    /**
     * 用于管理图片的滑动
     */
    private ViewPager viewPager;

    /**
     * 显示当前图片的页数
     */
    private TextView pageText;

    private String[] patchs;

    HashMap<String, Boolean> selectMap;

    CheckBox check;

    String imagePath;

    int currentPage = 0;

    float screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.photo_grally_wall);
        int imagePosition = getIntent().getIntExtra("image_position", 0);
        patchs = getIntent().getStringArrayExtra("patchs");
        Bundle bundle = getIntent().getExtras();
        selectMap = (HashMap<String, Boolean>) bundle.get("selectMap");
        check = (CheckBox) findViewById(R.id.select_state);
        check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                System.out.println(currentPage + "-----" + isChecked);
                selectMap.put(patchs[currentPage], isChecked);
            }
        });

        pageText = (TextView) findViewById(R.id.current_pager_count);
        viewPager = (ViewPager) findViewById(R.id.photo_wall_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(imagePosition);
        viewPager.setOnPageChangeListener(this);
//		viewPager.setEnabled(false);
        String str = patchs[imagePosition];
        currentPage = imagePosition;
        check.setChecked(selectMap.get(str) != null && selectMap.get(str));
        pageText.setText((imagePosition + 1) + "/" + patchs.length);
    }

    /**
     * ViewPager的适配器
     *
     * @author guolin
     */
    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            imagePath = patchs[position];
            Bitmap bitmap = getCompressPic(imagePath);
            //添加压缩代码
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getResources(),
                        R.drawable.empty_photo);
            }
            View view = LayoutInflater.from(PhotoDetailActivity.this).inflate(
                    R.layout.zoom_image_layout, null);
            ZoomImageView zoomImageView = (ZoomImageView) view
                    .findViewById(R.id.zoom_image_view);
            zoomImageView.setImageBitmap(bitmap);
            container.addView(view);
            System.out.println(viewPager.getCurrentItem());
            return view;
        }

        @Override
        public int getCount() {
            return patchs.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        private Bitmap getCompressPic(String path) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            float density = dm.density;      // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
            float densityDPI = dm.densityDpi;     // 屏幕密度（每寸像素：120/160/240/320）
            screenWidth = dm.xdpi;

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            int simple = options.outWidth / (int) screenWidth;

            if (simple < 1) {
                simple = 1;
            }

            Log.d("simple", "width=" + screenWidth + "," + "outWith=" + options.outWidth);

            options.inSampleSize = simple;
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(path, options);

            if (bitmap == null)
                return null;

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            int size = 100;

            while (bos.toByteArray().length / 1024 > 200) {
                bos.reset();
                bitmap.compress(Bitmap.CompressFormat.JPEG, size, bos);
                size -= 10;
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

            Bitmap image = BitmapFactory.decodeStream(bis);

            return image;
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * ViewPager选择监听
     *
     * @param currentPage
     */
    @Override
    public void onPageSelected(int currentPage) {
        String str = patchs[currentPage];
        this.currentPage = currentPage;
        check.setChecked(selectMap.get(str) != null && selectMap.get(str));
        System.out.println(currentPage);
        pageText.setText((currentPage + 1) + "/" + patchs.length);
    }


    /**
     * @return 经过选择的图片集
     */
    private HashMap<String, Boolean> getNewSelectMap() {
        HashMap<String, Boolean> postionList = new HashMap<String, Boolean>();
        for (Iterator<Map.Entry<String, Boolean>> it = selectMap.entrySet()
                .iterator(); it.hasNext(); ) {
            Map.Entry<String, Boolean> entry = it.next();
            if (entry.getValue()) {
                postionList.put(entry.getKey(), true);
            }
        }
        return postionList;
    }

    /**
     * @param item 菜单项
     * @return 退出activity
     */
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

    /**
     * @param keyCode
     * @param event
     * @return 向上一级Activity返回bundle
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("backSelectMap", getNewSelectMap());
            intent.putExtras(bundle);
            // 设置返回值，将intent对象作为数据返回到源Activity
            setResult(RESULT_OK, intent);
            this.finish(); // finish activity
            overridePendingTransition(R.anim.back_left_in,
                    R.anim.back_right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
