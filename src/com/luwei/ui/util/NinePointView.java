package com.luwei.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.luwei.potato.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luwei on 2015-1-28.
 * 9宫格解锁视图
 */
public class NinePointView extends View {
    private Matrix matrix = new Matrix();
    private Point[][] points = new Point[3][3];
    private boolean isInit, isSelect, isEnd, movingNoPoint;
    private float height, width, offsetsX, offsetsY, bitmapOffsetWidth, bitmapOffsetHeight;
    private Bitmap pointNarmal, pointPress, pointErrer, line_normal, line_press;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<Point> pointList = new ArrayList<Point>();
    private float motionX, motionY;
    OnPassWordSetListeren onPassWordSetInterface;   //设置监听接口

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
        //画点
        points2Canvas(canvas);

        //画线
        if (pointList.size() > 0) {
            Point a = pointList.get(0);
            for (int i = 0; i < pointList.size(); i++) {
                Point b = pointList.get(i);
                lineCanvas(canvas, a, b);
                a = b;
            }
            //绘制鼠标点
            if (movingNoPoint) {
                lineCanvas(canvas, a, new Point(motionX, motionY));
            }
        }
    }

    /**
     * 将点绘制到画布
     *
     * @param canvas
     */
    private void points2Canvas(Canvas canvas) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (point.state == Point.STATE_NORMAL) {
                    canvas.drawBitmap(pointNarmal, point.x - bitmapOffsetWidth, point.y - bitmapOffsetHeight, paint);
                } else if (point.state == Point.STATE_PRESS) {
                    canvas.drawBitmap(pointPress, point.x - bitmapOffsetWidth, point.y - bitmapOffsetHeight, paint);
                } else {
                    canvas.drawBitmap(pointErrer, point.x - bitmapOffsetWidth, point.y - bitmapOffsetHeight, paint);
                }
            }
        }

    }

    /**
     * 画线
     *
     * @param canvas
     * @param a      第一个点
     * @param b      第二个点
     */
    private void lineCanvas(Canvas canvas, Point a, Point b) {
        float lineLenght = (float) Point.distance(a, b);
        float degress = getDegress(a, b);
        canvas.rotate(degress, a.x, a.y);
        if (a.state == Point.STATE_PRESS) {
            //计算缩放量
            matrix.setScale(lineLenght / line_press.getWidth(), 1);
            //进行平移 目的是在drawBitmap时候，X,Y不会随缩放而改变原点位置。
            matrix.postTranslate(a.x, a.y - line_normal.getHeight() / 2);
            canvas.drawBitmap(line_press, matrix, paint);
        } else {
            //计算缩放量
            matrix.setScale(lineLenght / line_normal.getWidth(), 1);
            //进行平移 目的是在drawBitmap时候，X,Y不会随缩放而改变原点位置。
            matrix.postTranslate(a.x, a.y - line_normal.getHeight() / 2);
            canvas.drawBitmap(line_normal, matrix, paint);
        }
        canvas.rotate(-degress, a.x, a.y);

    }

    private float getDegress(Point a, Point b) {
        float ax = a.x;
        float bx = b.x;
        float ay = a.y;
        float by = b.y;
        float degress = 0;
        if (ax == bx) {  //Y轴平行，90 or 270
            if (by > ay) {
                degress = 90;
            } else if (by < ay) {
                degress = 270;
            }
        } else if (by == ay) {
            if (bx > ax) {
                degress = 0;
            } else if (bx < ax) {
                degress = 180;
            }
        } else {
            if (bx > ax) {    //在y轴的右侧 1，4 象限内
                if (by > ay) //4象限
                {
                    degress = 0;
                    degress = degress + switchDegress(ax, ay, bx, by);
                } else if (by < ay) { //在1象限
                    degress = 360;
                    degress = degress - switchDegress(ax, ay, bx, by);
                }
            } else if (bx < ax) { // 在Y轴左侧 2，3象限
                if (by > ay) { //3象限
                    degress = 90;
                    degress = degress + switchDegress(ay, ax, by, bx);
                } else if (by < ay) { //2象限
                    degress = 180;
                    degress = degress + switchDegress(ax, ay, bx, by);
                }
            }

        }

        return degress;
    }

    /**
     * 计算角度*
     */
    private float switchDegress(float ax, float ay, float bx, float by) {
        double value = Math.atan2(Math.abs(by - ay), Math.abs(bx - ax)) * 180 / Math.PI;
        return (float) value;
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

        //图片偏移量修正

        bitmapOffsetHeight = pointNarmal.getHeight() / 2;

        bitmapOffsetWidth = pointNarmal.getWidth() / 2;

        int initDex = 1;
        //设置密码
        for (Point[] points : this.points) {
            for (Point point : points) {
                point.index = initDex;
                initDex++;
            }
        }
        isInit = true;

    }

    /**
     * 触摸事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        motionX = event.getX();
        motionY = event.getY();
        movingNoPoint = false;
        isEnd = false;
        Point point = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetPoint();
                point = checkSelectPoint();
                if (point != null) {
                    isSelect = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isEnd = true;
                isSelect = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isSelect) {
                    point = checkSelectPoint();
                    if (point == null) {
                        movingNoPoint = true;
                    }
                }
                break;
        }
        //选中重复检查
        if (!isEnd && isSelect && point != null) {
            //如果是交叉点
            if (crossPoint(point)) {
                movingNoPoint = true;
            } else {
                //如果是新点
                point.state = Point.STATE_PRESS;
                pointList.add(point);
            }
        }

        //绘制结束
        if (isEnd) {
            //绘制取消
            if (pointList.size() <= 1) {
                resetPoint();
                //绘制错误
            } else if (pointList.size() <= 5 && pointList.size() >= 2) {
                errorPoint();
                onPassWordSetInterface.onChange(null);
            } else {
                String password = "";
                for(Point mPoint : pointList){
                    password = password+(mPoint.index+" ");
                }
                onPassWordSetInterface.onChange(password);
            }

        }

        //刷新View  这里会重复调用onDraw 方法
        postInvalidate();
        return true;
    }

    private boolean crossPoint(Point point) {
        if (pointList.contains(point)) {
            return true;
        } else
            return false;
    }

    private void resetPoint() {
        for (Point point : pointList) {
            point.state = Point.STATE_NORMAL;
        }
        pointList.clear();
    }

    private void errorPoint() {
        for (Point point : pointList) {
            point.state = Point.STATE_ERROR;
        }
    }


    /**
     * 检查是否在9个点中 *
     */
    private Point checkSelectPoint() {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (Point.with(point.x, point.y, bitmapOffsetWidth, motionX, motionY)) {
                    return point;
                }
            }
        }
        return null;
    }

//    @Override
//    protected void onConfigurationChanged(Configuration newConfig) {
//        this.onConfigurationChanged(newConfig);
//    }

    /**
     * 内部类
     * 绘制点
     * 包含两点距离
     * 是否重合
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

        /**
         * 求两点间的距离
         *
         * @param a pointa 的坐标
         * @param b pointb 的坐标
         * @return
         */
        public static double distance(Point a, Point b) {
            return Math.sqrt(Math.abs(a.x - b.x) * Math.abs(a.x - b.x) + Math.abs(a.y - b.y) * Math.abs(a.y - b.y));
        }


        /**
         * 判断是否重合
         *
         * @param pointX  点的X坐标
         * @param pointY  点的Y坐标
         * @param r       图片半径
         * @param movingX 当前X坐标
         * @param movingY 当前Y坐标
         * @return
         */
        public static boolean with(float pointX, float pointY, float r, float movingX, float movingY) {
            //开方
            return Math.sqrt((pointX - movingX) * (pointX - movingX) + (pointY - movingY) * (pointY - movingY)) < r;
        }
    }

    public interface OnPassWordSetListeren {
        public void onChange(String str);
    }

    public void setPassWordChange(OnPassWordSetListeren listeren) {
        if (this.onPassWordSetInterface == null) {
            this.onPassWordSetInterface = listeren;
        }
    }

}
