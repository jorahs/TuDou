package com.luwei.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.view.View;

import com.luwei.potato.R;

import java.util.Set;

/**
 * Created by Administrator on 2015-2-3.
 */
public class LockPreview extends View {
    Bitmap small_n, small_s;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Point[][] points = new Point[3][3];
    private int width, height, offsetsX, offsetsY, bitmapOffset;
    private boolean init;
    private OnStateChangeListeren onStateChangeListeren;

    public LockPreview(Context context) {
        super(context);
    }

    public LockPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        if (!init) {
            init();
        }
        drawWithEvent(canvas);
        drawPoint(canvas);
    }

    /**
     * 根据锁的接口，触发重画事件。
     *
     * @param canvas
     */
    private void drawWithEvent(Canvas canvas) {
        if (onStateChangeListeren != null) {
            Set<Integer> pointN = onStateChangeListeren.setPoint();
            if (pointN != null) {
                for (Integer i : pointN) {
                    for (Point[] points1 : points) {
                        for (Point point : points1) {
                            if (point.getIndex() == i) {
                                point.state = Point.GONE;
                                canvas.drawBitmap(small_s, point.width - bitmapOffset, point.height - bitmapOffset, paint);
                            }
                        }
                    }
                }
            } else {
                for (Point[] points1 : points) {
                    for (Point point : points1) {
                        point.state = Point.DISPLAY;
                    }
                }
            }
        }
    }

    private void drawPoint(Canvas canvas) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (point.state == Point.DISPLAY) {
                    canvas.drawBitmap(small_n, point.width - bitmapOffset, point.height - bitmapOffset, paint);
                }
            }
        }
    }


    public void init() {
        init = true;
        width = getWidth();
        height = getHeight();

        //获取偏量
        if (width > height) {
            offsetsX = (width - height) / 6;
            width = height;
        } else {
            offsetsY = (height - width) / 6;
            height = width;
        }

        //确定坐标
        points[0][0] = new Point(offsetsX + width / 4, offsetsY + height / 4);
        points[0][1] = new Point(offsetsX + width / 2, offsetsY + height / 4);
        points[0][2] = new Point(offsetsX + width * 3 / 4, offsetsY + height / 4);

        points[1][0] = new Point(offsetsX + width / 4, offsetsY + height / 2);
        points[1][1] = new Point(offsetsX + width / 2, offsetsY + height / 2);
        points[1][2] = new Point(offsetsX + width * 3 / 4, offsetsY + height / 2);

        points[2][0] = new Point(offsetsX + width / 4, offsetsY + height * 3 / 4);
        points[2][1] = new Point(offsetsX + width / 2, offsetsY + height * 3 / 4);
        points[2][2] = new Point(offsetsX + width * 3 / 4, offsetsY + height * 3 / 4);

        insertSerizer(points);

        small_n = BitmapFactory.decodeResource(getResources(), R.drawable.small_circle_n);
        small_s = BitmapFactory.decodeResource(getResources(), R.drawable.small_circle_s);

        bitmapOffset = small_n.getHeight() / 2;

    }

    public void setOnStateChangeListeren(OnStateChangeListeren onStateChangeListeren) {
        this.onStateChangeListeren = onStateChangeListeren;
    }

    public void insertSerizer(Point[][] points) {
        int i = 1;
        for (Point[] pointOne : points) {
            for (Point point : pointOne) {
                point.index = i;
                i++;
            }
        }
    }


    public interface OnStateChangeListeren {
        public Set<Integer> setPoint();
    }


    public class Point {
        private static final int DISPLAY = 0;
        private static final int GONE = 1;
        private int state = 0;
        private int width;
        private int height;
        private int index = 0;

        public Point(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }
}
