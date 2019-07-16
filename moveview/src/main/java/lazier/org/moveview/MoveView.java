package lazier.org.moveview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by :TYK
 * Date: 2019/7/11  9:47
 * Desc:
 */
public class MoveView extends View {

    boolean isClickViewMove = true;//是否需要点击到 view才能拖动
    boolean isClickView = false;//是否点击到了view
    public float currentX = 70;
    public float currentY = 70;
    public float circleR = 60;
    public float circleStock = 10;
    public int colorMin = Color.BLUE;
    public int colorMax = Color.RED;
    int screenHeight;
    int screenWidth;
    int statusBarHeight;
    private Paint mPaint = new Paint(); // 1.创建一个画笔
    private Paint mPaint1 = new Paint(); // 1.创建一个画笔
    private Context context;

    public MoveView(Context context) {
        super(context);
        this.context = context;
        initPaint();
    }

    public MoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(attrs);
        initPaint();
    }

    public MoveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(attrs);
        initPaint();
    }

    private void initView(@Nullable AttributeSet attrs) {


        // 获取屏幕宽高（方法1）
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        screenHeight = wm.getDefaultDisplay().getHeight();
        screenWidth = wm.getDefaultDisplay().getWidth();
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        // 状态栏高度
        statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        if (attrs == null) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MoveView);
        currentX = ta.getInt(R.styleable.MoveView_circleX, 0);
        currentY = ta.getInt(R.styleable.MoveView_circleY, 0);
        circleR = ta.getInt(R.styleable.MoveView_circleR, 60);
        circleStock = ta.getInt(R.styleable.MoveView_circleStock, 10);
        isClickViewMove = ta.getBoolean(R.styleable.MoveView_isClickViewMove, true);
        colorMin = ta.getColor(R.styleable.MoveView_colorMin, Color.RED);
        colorMax = ta.getColor(R.styleable.MoveView_colorMax, Color.BLUE);

    }

    // 2.初始化画笔
    private void initPaint() {
        mPaint.setColor(colorMin);       //设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);  //设置画笔模式为填充
        mPaint.setStrokeWidth(10f);         //设置画笔宽度为10px
        mPaint.setAntiAlias(true);

        mPaint1.setColor(colorMax);
        mPaint1.setStyle(Paint.Style.STROKE);
        mPaint1.setStrokeWidth(10f);
        mPaint1.setAntiAlias(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawCircle(currentX, currentY, circleR - circleStock, mPaint);
        canvas.drawCircle(currentX, currentY, circleR, mPaint1);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isTouch = false;
        if (!isClickViewMove) {
            //修改当前的坐标
            this.currentX = event.getX();
            this.currentY = event.getY();
            //重绘小球
            this.invalidate();
            isTouch = true;
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (currentX - circleR <= event.getRawX() && event.getRawX() <= currentX + circleR
                            && currentY+statusBarHeight - circleR <= event.getRawY() && event.getRawY() <= currentY+statusBarHeight + circleR) {
                        isClickView = true;

                        if (noCrossing(event.getRawX(), event.getRawY())) {
                            //修改当前的坐标
                            this.currentX = event.getX();
                            this.currentY = event.getY();
                            isTouch = true;
                        }
                    } else {
                        isClickView = false;
                        isTouch = false;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isClickView) {
                        if (noCrossing(event.getRawX(), event.getRawY())) {
                            //修改当前的坐标
                            this.currentX = event.getX();
                            this.currentY = event.getY();
                            //重绘小球
                            this.invalidate();
                            isTouch = true;
                        }
                    } else {
                        isTouch = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isClickView) {
                        if (noCrossing(event.getRawX(), event.getRawY())) {
                            //修改当前的坐标
                            this.currentX = event.getX();
                            this.currentY = event.getY();
                            //重绘小球
                            this.invalidate();
                            isTouch = true;
                        }
                    } else {
                        isTouch = false;
                    }
                    break;
            }
        }
        return isTouch;
    }

    /**
     * 滑动位置没有超过屏幕便捷  true  未超过  false 已经超过
     *
     * @return
     */
    public boolean noCrossing(float x, float y) {
        //保证当前的拖动位置在屏幕内
        if (x >= circleR && x <= screenWidth - circleR && y >= circleR+statusBarHeight && y <= screenHeight - circleR) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取X坐标
     *
     * @return
     */
    public float getX() {
        return currentX;
    }

    /**
     * 获取Y坐标
     *
     * @return
     */
    public float getY() {
        return currentY;
    }

    /**
     * 获取半径
     *
     * @return
     */
    public float getR() {
        return circleR;
    }


}
