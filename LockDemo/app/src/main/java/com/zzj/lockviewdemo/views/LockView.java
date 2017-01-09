package com.zzj.lockviewdemo.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zzj.lockviewdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zzj on 2016/11/15.
 */
public class  LockView extends View {

    private String TAG = "lockview---";
    //九宫格点的数组
    private Point[][] points = new Point[3][3];
    private boolean isInit, isSelectPoint, isFinsh,moveNoPint;//是否初始化点

    private float width, height, offsetsX, offsetsY, bitmapR, eventX, eventY;
    //画笔
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //矩阵  用来控制缩放
    private Matrix matrix = new Matrix();
    //存放连线后放入的点
    private List<Point> pointList = new ArrayList<>();

    private Bitmap pointNormal, pointPressed, pointError, linePressed, lineError;

    private onPatternChangeListener onPatternChangeListener;
    public LockView(Context context) {
        super(context);
    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写ondraw
     *
     * @param canvas 画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initPoints();//初始化图案的点
        }
        canvasPoints(canvas);//绘制点到画布
        //绘制九宫格内的点的连线
        if(pointList.size()>0){
            //绘制九宫格的坐标点
            Point a = pointList.get(0);
            for(int i = 0  ;i<pointList.size();i++){
                Point b = pointList.get(i);
                lineCanvas(canvas,a,b);
                a = b;
            }

            //如果连线时，手势超出了九宫格的范围，线还是要继续画的
            if(moveNoPint){
               lineCanvas(canvas,a,new Point(eventX,eventY));
            }
        }
    }


    /**
     * 画线
     * @param canvas 画布
     * 画线需要两点一线，
     * @param a 点a
     * @param b 点 b
     */
    private void lineCanvas(Canvas canvas,Point a,Point b){
        //计算线的长度
        float lineLenght = (float) Point.distance(a,b);
        Log.d(TAG,"两点间的距离："+lineLenght);
        float degrees = getDegrees(a,b);//获取旋转的角度
        Log.d(TAG,"两点间的角度："+degrees);
//        //通过画布设置旋转的角度
        canvas.rotate(degrees,a.x,a.y);
        Log.d(TAG,"画线的点A   ----X:"+a.x+"----Y:"+a.y);
        if(a.state == Point.STATE_PRESSED){
            //缩放的比例
            matrix.setScale(lineLenght/linePressed.getWidth(),1);
            //设置矩阵偏移量
            matrix.postTranslate(a.x-linePressed.getWidth()/2,a.y-linePressed.getHeight()/2);
            //画线
            canvas.drawBitmap(linePressed,matrix,paint);
        }else {
            //缩放的比例
            matrix.setScale(lineLenght/lineError.getWidth(),1);
            //设置矩阵偏移量
            matrix.postTranslate(a.x-lineError.getWidth()/2,a.y-lineError.getHeight()/2);
            //画线
            canvas.drawBitmap(lineError,matrix,paint);
        }
        canvas.rotate(-degrees,a.x,a.y);
    }
    /**
     * 绘制点到画布
     *
     * @param canvas 画布
     */
    private void canvasPoints(Canvas canvas) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (point.state == Point.STATE_NORMAL) {//正常状态
                    //调整画布的位置，因为在点的右下边开始画的图案，图案的中心并没有和点对齐，将图案向上和左偏移图案的半径（pointNormal.getwidth()/2）
                    canvas.drawBitmap(pointNormal, point.x - bitmapR, point.y - bitmapR, paint);
                } else if (point.state == Point.STATE_PRESSED) {//选中状态
                    canvas.drawBitmap(pointPressed, point.x-bitmapR, point.y- bitmapR, paint);
                } else {//错误状态
                    canvas.drawBitmap(pointError, point.x- bitmapR, point.y- bitmapR, paint);
                }
            }
        }
    }

    /**
     * 重写view的ontouchEvent的方法
     * 监听手势的触摸状态
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取手势的坐标
        eventX = event.getX();
        eventY = event.getY();
        moveNoPint = false;
        //当此方法执行就没有结束，只有松开的状态下才算完成
        isFinsh = false;
        Point point = null;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://按下的状态

                if(onPatternChangeListener!=null){
                    //当重新绘制时，设置监听
                    onPatternChangeListener.onPatternStart(true);
                }
                //每次按下清空点的集合
                resetPoint();
                point = checkSelectPoint();
                if (point != null) {//如果此点不为null，通过标记设置它为选中状态
                    isSelectPoint = true;
                }
                break;
            case MotionEvent.ACTION_MOVE://移动状态
                //判断手势移动到的点是否重复选中
                if (isSelectPoint) {
                    point = checkSelectPoint();

                    if(point == null){
                        moveNoPint = true;
                    }else {
                    }
                }
                break;
            case MotionEvent.ACTION_UP://松开后的状态
                //当手指抬起，判断绘制是否完成
                isFinsh = true;
                isSelectPoint = false;

                break;


        }
        //判断选中的点是否重复选择
        if (!isFinsh && isSelectPoint && point != null) {

            if (crossPoint(point)) {
                //如果重复,不添加到集合中，但绘制并没有结束，添加标记,此标记在重复选择时为true，在九宫格范围外为true
                moveNoPint = true;
                Log.d("crossPoint","点集合的数据有几个：+"+pointList.size()+"----"+pointList.toString());
                Log.d("log","--------这里走了吗-----");
            } else {
//                如果是新点，将新点的状态设置为按下状态，添加进集合,
                point.state = Point.STATE_PRESSED;
                Log.d(TAG,"滑到的点的位置：----X:"+point.x+"-----Y:" + point.y);
                pointList.add(point);
                Log.d(TAG,"点集合的数据有几个：+"+pointList.size()+"----"+pointList.toString());
            }
        }
        //如果绘制完成，判断绘制选中点的结果
        if (isFinsh) {
            if (pointList.size() == 1) {
                //如果只选中一个点，条件不成立，将集合清空
                resetPoint();
            } else if (pointList.size() > 1 && pointList.size() < 4) {
                //绘制错误的状态，提示图案太简单
                errorPoint();
                if(onPatternChangeListener!=null){
                    //图案太简单,返回null
                    onPatternChangeListener.onPatternChange(null);
                }
            } else if (pointList.size() >= 4) {
                //绘制完成
                String password = "";
                if(onPatternChangeListener!=null){
                    //遍历绘制点的集合
                    for(int i = 0;i<pointList.size();i++){
                        //密码拼接
                      password = password+pointList.get(i).index;
                    }
                    onPatternChangeListener.onPatternChange(password);
                }
            }
        }
        //每次刷新view
        postInvalidate();
        return true;
    }

    /**
     * 获取角度的方法
     * @param
     * @return 角度
     */
        public float getDegrees(Point pointA, Point pointB) {

        return (float) Math.toDegrees(Math.atan2(pointB.y - pointA.y, pointB.x - pointA.x));

    }
    /**
     * 交叉点的判断
     *
     * @param point 滑动到的点
     * @return
     */
    private boolean crossPoint(Point point) {
        if (pointList.contains(point)) {
            //如果集合中包含此点，返回true 否着返回flase
            return true;
        } else {
            return false;
        }
    }

    /**
     * 条件不成立清空集合
     */
    public void resetPoint() {
        for(int i = 0;i<pointList.size();i++){
            Point point = pointList.get(i);
            point.state = Point.STATE_NORMAL;
        }
        pointList.clear();
    }

    /**
     * 绘制错误的方法
     */
    public void errorPoint() {
        //遍历点的集合，将选中的点设置成错误的图案
        for (Point point : pointList) {
            point.state = Point.STATE_ERROR;
        }
    }

    /**
     * 点击了图案后将点返回
     *
     * @return
     */
    private Point checkSelectPoint() {

        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
//                手势是否滑到了图案的范围内
                Log.d(TAG,"手势的位置-----X："+eventX+"---Y:"+eventY+"点的位置----X："+point.x+"----Y:"+point.y);
                if (point.isCoincide(point.x, point.y, bitmapR, eventX, eventY)) {

                    return point;
                }
            }
        }
        return null;
    }

    /**
     * 初始化点
     */
    private void initPoints() {
        //获取布局的宽高
        width = getWidth();
        height = getHeight();

        //判断屏幕横竖屏状态
        if (width > height) {//横屏状态
            offsetsX = (width - height) / 2;
            width = height;
        } else {//竖屏
            offsetsY = (height - width) / 2;
            height = width;
        }

        //计算点的位置
        points[0][0] = new Point(offsetsX + width / 4, offsetsY + width / 4);
        points[0][1] = new Point(offsetsX + width / 2, offsetsY + width / 4);
        points[0][2] = new Point(offsetsX + width - width / 4, offsetsY + width / 4);
        points[1][0] = new Point(offsetsX + width / 4, offsetsY + width / 2);
        points[1][1] = new Point(offsetsX + width / 2, offsetsY + width / 2);
        points[1][2] = new Point(offsetsX + width - width / 4, offsetsY + width / 2);
        points[2][0] = new Point(offsetsX + width / 4, offsetsY + width - width / 4);
        points[2][1] = new Point(offsetsX + width / 2, offsetsY + width - width / 4);
        points[2][2] = new Point(offsetsX + width - width / 4, offsetsY + width - width / 4);

        //添加图案资源
        pointNormal = BitmapFactory.decodeResource(getResources(), R.mipmap.point_normal);
        pointPressed = BitmapFactory.decodeResource(getResources(), R.mipmap.point_pressed);
        pointError = BitmapFactory.decodeResource(getResources(), R.mipmap.point_error);
        linePressed = BitmapFactory.decodeResource(getResources(), R.mipmap.error_line);
        lineError = BitmapFactory.decodeResource(getResources(), R.mipmap.normal_line);
        //图案的半径
        bitmapR = pointNormal.getWidth() / 2;

        //设置密码
        int index = 1;
        for(Point[] points : this.points){
            for(Point point : points){
                point.index = index;
                index++;
            }
        }

        //初始化完成
        isInit = true;
    }

    /**
     * 自定义的点
     */
    public static class Point {
        //正常状态
        public static int STATE_NORMAL = 0;
        //        选中状态
        public static int STATE_PRESSED = 1;
        //        错误状态
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
         * 判断连线和图案是否重合
         *
         * @param pointX  参考点的x坐标
         * @param pointY  参考点的y坐标
         * @param bitmapR 图案的半径
         * @param eventX  手势的x坐标
         * @param eventY  手势的y坐标
         * @return 是否在图案的范围内
         */
        public static boolean isCoincide(float pointX, float pointY, float bitmapR, float eventX, float eventY) {
            return Math.sqrt((pointX - eventX) * (pointX - eventX) + (pointY - eventY) * (pointY - eventY)) < bitmapR;
        }

        /**
         * 两点间的距离
         * @param a 点 a
         * @param b 点 b
         * @return 距离
         */
        public static double distance(Point a,Point b){
            //x轴差的平方加上y轴差的平方，相加开方
            return Math.sqrt(Math.abs(a.x-b.x)*Math.abs(a.x-b.x)+Math.abs(a.y-b.y)*Math.abs(a.y-b.y));
        }

    }

    /**
     * 图案监听器
     */
    public static interface onPatternChangeListener{
        /**
         * 绘制图案的密码
         * @param passwordstr 密码
         */
        void onPatternChange(String passwordstr);

        /**
         * 监听是否重新绘制
         * @param isClick
         */
        void onPatternStart(boolean isClick);
    }

    /**
     * 设置监听
     * @param onPatternChangeListener
     */
    public void setPatternChangeListener(onPatternChangeListener onPatternChangeListener){
        if(onPatternChangeListener!=null){
            this.onPatternChangeListener = onPatternChangeListener;
        }
    }
}
