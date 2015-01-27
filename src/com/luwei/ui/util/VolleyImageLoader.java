package com.luwei.ui.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by luwei on 2014-12-24.
 */
public class VolleyImageLoader {
    public static com.android.volley.toolbox.ImageLoader LoadImage(Context context) {
        RequestQueue mQueue = Volley.newRequestQueue(context);
        LruImageCache lruImageCache = LruImageCache.instance();
        return new ImageLoader(mQueue, lruImageCache);
    }
}
