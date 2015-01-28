package com.luwei.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.luwei.potato.R;

/**
 * Created by luwei on 2015-1-28.
 * 9宫格解锁视图
 */
public class NinePointView extends View {

    private Point[][] points = new Point[3][3];
    private boolean isInit = false;
    private float height, width, offsetsX, offsetsY;
    ;
    private Bitmap pointNarmal, pointPress, pointErrer, line_normal, line_press;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public NinePointView(Context context) {
        super(context);
    }

    public NinePointView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NinePointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initPoint();
        }
        points2Canvas(canvas);
    }

    /**
     * 将点绘制到画布
     *
     * @param canvas
     */
    private void points2Canvas(Canvas canvas) {
        for (int i =0 ; i < points.length;i++){
            for (int j = 0; j < points[i].length;j++){
                Point point = points[i][j];
                if(point.state == Point.STATE_NORMAL){
                    canvas.drawBitmap(pointNarmal,point.x,point.y,paint);
                }else if (point.state == Point.STATE_PRESS){
                    canvas.drawBitmap(pointPress,point.x,point.y,paint);
                }else{
                    canvas.drawBitmap(pointErrer,point.x,point.y,paint);
                }
            }
        }

    }

    /**
     * 初始化点
     */
    private void initPoint() {
        /* 获取宽高 */
        width = getWidth();
        height = getHeight();

        //获取宽高
        if (width > height) {
            offsetsX = (width - height) / 2;
            width = height;
        } else {
            offsetsY = (height - width) / 2;
            height = width;
        }

        //图片资源

        pointNarmal = BitmapFactory.decodeResource(getResources(), R.drawable.circle_n);
        pointPress = BitmapFactory.decodeResource(getResources(), R.drawable.circle_p);
        pointErrer = BitmapFactory.decodeResource(getResources(), R.drawable.circle_r);
        line_press = BitmapFactory.decodeResource(getResources(), R.drawable.line_n);
        line_normal = BitmapFactory.decodeResource(getResources(), R.drawable.line_p);

        points[0][0] = new Point(offsetsX + width / 4, offsetsY + height / 4);
        points[0][1] = new Point(offsetsX + width / 2, offsetsY + height / 4);
        points[0][2] = new Point(offsetsX + width * 3 / 4, offsetsY + height / 4);

        points[1][0] = new Point(offsetsX + width / 4, offsetsY + height / 2);
        points[1][1] = new Point(offsetsX + width / 2, offsetsY + height / 2);
        points[1][2] = new Point(offsetsX + width * 3 / 4, offsetsY + height / 2);

        points[2][0] = new Point(offsetsX + width / 4, offsetsY + height * 3 / 4);
        points[2][1] = new Point(offsetsX + width / 2, offsetsY + height * 3 / 4);
        points[2][2] = new Point(offsetsX + width * 3 / 4, offsetsY + height * 3 / 4);

    }

    /**
     * 绘制点
     */
    private static class Point {
        public static int STATE_NORMAL = 0;
        public static int STATE_PRESS = 1;
        public static int STATE_ERROR = 2;

        public float x, y;
        public int index = 0, state = 0;

        public Point() {
        }

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }


}
