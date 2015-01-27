package com.luwei.util;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 本地图片线程加载
 */
public class NativeImageLoader {
    private LruCache<String, Bitmap> mMemoryCache;
    private static NativeImageLoader mInstance;
    private Handler mPoolThreadHander;
    private ExecutorService mThreadPool;
    private Thread mPoolThread;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTasks;
    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
     */
    private volatile Semaphore mSemaphore = new Semaphore(0);

    /**
     * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
     */
    private volatile Semaphore mPoolSemaphore;

//    private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(1);

    /**
     * 队列的调度方式
     *
     * @author zhy
     */
    public enum Type {
        FIFO, LIFO
    }

//    //构造一个lurcache缓存
//    private NativeImageLoader() {
//
//        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//
//        final int cacheSize = maxMemory / 4;
//
//        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
//
//            @Override
//            protected int sizeOf(String key, Bitmap bitmap) {
//                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
//            }
//        };
//    }

    /**
     * 懒汉模式创建实例
     *
     * @return mInstance
     */
    public static NativeImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (NativeImageLoader.class) {
                mInstance = new NativeImageLoader(1, Type.LIFO);
            }
            return mInstance;
        }
        return mInstance;
    }


    /**
     * 重构
     *
     * @return
     */
    public static NativeImageLoader getInstance(int threadCount, Type type) {

        if (mInstance == null) {
            synchronized (NativeImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new NativeImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    private NativeImageLoader(int threadCount, Type type) {
        init(threadCount, type);
    }

    private void init(int threadCount, Type type) {
        // loop thread
        mPoolThread = new Thread() {
            @Override
            public void run() {
                Looper.prepare();

                mPoolThreadHander = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        mThreadPool.execute(getTask());
                        try {
                            mPoolSemaphore.acquire();
                        } catch (InterruptedException e) {
                        }
                    }
                };
                // 释放一个信号量
                mSemaphore.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mPoolSemaphore = new Semaphore(threadCount);
        mTasks = new LinkedList<Runnable>();
        mType = type == null ? Type.LIFO : type;

    }

    /**
     * 添加一个任务
     *
     * @param runnable
     */
    private synchronized void addTask(Runnable runnable) {
        try {
            // 请求信号量，防止mPoolThreadHander为null
            if (mPoolThreadHander == null)
                mSemaphore.acquire();
        } catch (InterruptedException e) {
        }
        mTasks.add(runnable);

        mPoolThreadHander.sendEmptyMessage(0x110);
    }

    /**
     * 取出一个任务
     *
     * @return
     */
    private synchronized Runnable getTask() {
        if (mType == Type.FIFO) {
            return mTasks.removeFirst();
        } else if (mType == Type.LIFO) {
            return mTasks.removeLast();
        }
        return null;
    }


    /**
     * 读取本地图片
     * 如果在Lru缓存中存在bitmap 则调用缓存数据，通过handle 发送并回调将bitmap显示到imageview上
     * 如果不存在则执行ThumbBitmap获取缩略图。同样执行上一步操作。
     * handle 中调用接口，不需要在传入view
     *
     * @param image_id
     * @param path
     * @param mPoint
     * @param mCallBack
     * @return
     */
    public void loadNativeImage(final Context context, final String image_id, final String path, final Point mPoint,
                                final NativeImageCallBack mCallBack) {

        Bitmap bitmap = getBitmapFromMemCache(image_id);

        final Handler mHander = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mCallBack.onImageLoader((Bitmap) msg.obj, image_id);
            }

        };


        /**
         * 如果bitmap没有在Lur中则开启获取图片线程
         */
        if (bitmap != null) {
            Message msg = mHander.obtainMessage();
            msg.obj = bitmap;
            mHander.sendMessage(msg);
        } else {
//            Log.d("exect", "print" + image_id);
//            mImageThreadPool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    long startTime = System.currentTimeMillis();
//                    Bitmap mBitmap = null;
//                    mBitmap = decodeThumbBitmapForFile(context, image_id, path,
//                            mPoint == null ? 0 : mPoint.x, mPoint == null ? 0
//                                    : mPoint.y);
//                    Message msg = mHander.obtainMessage();
//                    msg.obj = mBitmap;
//                    mHander.sendMessage(msg);
//                    addBitmapToMemoryCache(image_id, mBitmap); //获取图片bitmap后，存入Lur
//                    Log.d("Spend", "耗时" + (System.currentTimeMillis() - startTime));
//                }
//            });
            addTask(new Runnable() {
                @Override
                public void run() {
                    Bitmap mBitmap = null;
                    mBitmap = decodeThumbBitmapForFile(context, image_id, path,
                            mPoint == null ? 0 : mPoint.x, mPoint == null ? 0
                                    : mPoint.y);
                    Message msg = mHander.obtainMessage();
                    msg.obj = mBitmap;
                    mHander.sendMessage(msg);
                    addBitmapToMemoryCache(image_id, mBitmap); //获取图片bitmap后，存入Lur
                    mPoolSemaphore.release();
                }
            });

        }

//        return bitmap;

    }

    /**
     * 添加bitmap到缓存中
     *
     * @param key
     * @param bitmap
     */
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从缓存中获取bitmap
     *
     * @param key
     * @return
     */
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }


    /**
     * 进行缩放
     *
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    private Bitmap decodeThumbBitmapForFile(Context context, String image_id, String path, int viewWidth,
                                            int viewHeight) {

        ContentResolver cr = context.getContentResolver();

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

//        options.inSampleSize = computeScale(options, viewWidth, viewHeight);

        options.inJustDecodeBounds = false;

//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        long startTime = System.currentTimeMillis();

//        String[] projection = {MediaStore.Images.Thumbnails.DATA };
//
//         cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
//                projection,
//                MediaStore.Images.Thumbnails.IMAGE_ID+"=?", new String[]{image_id}, null);
//        Bitmap bitmap = BitmapFactory.decodeFile(projection[0], options);

        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, Integer.valueOf(image_id), MediaStore.Images.Thumbnails.MINI_KIND, options);

        long lastTime = System.currentTimeMillis();
        long finallyTime = lastTime - startTime;
        Log.d("Time", "耗费时间:" + (finallyTime));

        if (bitmap == null) {
            options.inSampleSize = computeScale(options, viewWidth, viewHeight);
            bitmap = BitmapFactory.decodeFile(path, options);
            if (bitmap == null) {   //防止出现内存中坏损图片的导致的空指针
                return null;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int option = 100;
            while (baos.toByteArray().length / 1024 > 20) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                bitmap.compress(Bitmap.CompressFormat.JPEG, option, baos);//这里压缩options%，把压缩后的数据存放到baos中
                option -= 10;//每次都减少10
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
            bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片

            Log.d("Image Size", "图片大小: " + baos.toByteArray().length + "");
        }

        if (bitmap == null) {   //防止出现内存中坏损图片的导致的空指针
            return null;
        }

        Bitmap image = ThumbnailUtils.extractThumbnail(bitmap, viewWidth, viewHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        return image;
    }


    /**
     * ThumbnailUtils这个工具类实现了剪切,所以这个方法可以不用
     * 按正方形裁切图片
     */
    public static Bitmap ImageCrop(Bitmap bitmap) {
        Log.d("CAIJIAN", "进行裁剪图片");
        if (bitmap == null) {
            Log.d("CAIJIAN", "出现错误内存图片加载");
            return null;
        }
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        //下面这句是关键
        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }

    /**
     * 具体的缩放比例
     *
     * @param options
     * @param viewWidth
     * @param viewHeight
     */
    private int computeScale(BitmapFactory.Options options, int viewWidth,
                             int viewHeight) {
        int inSampleSize = 1;
        if (viewWidth == 0 || viewWidth == 0) {
            return inSampleSize;
        }
        int WidthScale = options.outWidth / viewWidth;
        int HeightScale = options.outHeight / viewHeight;
        if (WidthScale < HeightScale) {
            inSampleSize = WidthScale;
        } else {
            inSampleSize = HeightScale;
        }
        return inSampleSize;
    }


    /**
     * ImageView的回调接口
     */
    public interface NativeImageCallBack {
        /**
         * @param bitmap
         * @param image_id
         */
        public void onImageLoader(Bitmap bitmap, String image_id);
    }
}
