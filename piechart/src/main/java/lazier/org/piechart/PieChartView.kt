package lazier.org.piechart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.graphics.RectF
import android.text.TextUtils


/**
 * Created by :TYK
 * Date: 2019/7/15  13:49
 * Desc: 饼状图
 */
class PieChartView : View {
    var mcontext: Context? = null
    //参数数据
    var dataList: MutableList<Data> = arrayListOf()
    //画笔
    var paint: Paint? = null

    //控制边线宽度
    var circleStock = 2f
    //是否显示数据
    var isShowData = false
    //背景颜色
    var bgColor = Color.WHITE

    // 饼状图初始绘制角度
    var mStartAngle = 0f

    var mWidth = 0f

    var mHeight = 0f
    //字体大小
    var textSize = 20f

    //饼状图R 控制大小
    var circleR = (Math.min(mWidth, mHeight) / 2 * 0.8).toFloat()

    constructor(context: Context?) : super(context) {
        mcontext = context
        initView(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mcontext = context
        initView(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mcontext = context
        initView(attrs)
    }


    /**
     * 初始化
     */
    fun initView(attrs: AttributeSet?) {
        initPaint()

        //获取到XML文件中的属性
        val typedArray = mcontext?.obtainStyledAttributes(attrs, R.styleable.PieChartView)
//        if (typedArray?.getDimension(R.styleable.PieChartView_circleR, circleR)!!<=circleR){
//            circleR = typedArray.getDimension(R.styleable.PieChartView_circleR, circleR)
//        }
        circleStock = typedArray?.getDimension(R.styleable.PieChartView_circleWidth, 10f)!!
        mWidth = typedArray.getDimension(R.styleable.PieChartView_width, 10f)
        mHeight = typedArray.getDimension(R.styleable.PieChartView_height, 10f)
        textSize = typedArray.getDimension(R.styleable.PieChartView_textSize, textSize)
        mStartAngle = typedArray.getFloat(R.styleable.PieChartView_mStartAngle, 0f)
        isShowData = typedArray.getBoolean(R.styleable.PieChartView_isShowData, isShowData)
        bgColor = typedArray.getColor(R.styleable.PieChartView_circleBackgroundColor, Color.WHITE)

        circleR = (Math.min(mWidth, mHeight) / 2 * 0.8).toFloat()
    }

    /**
     * 初始化画笔
     */
    fun initPaint() {
        paint = Paint()
        paint?.color = bgColor
        paint?.style = Paint.Style.FILL
        paint?.strokeWidth = circleStock
        paint?.isAntiAlias = true
    }

    /**
     * 设置数据
     */
    fun setData(list: MutableList<Data>) {
        dataList = list
        invalidate()//刷新view
    }


    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        var currentAngle = mStartAngle
        // 将画布坐标原点移动到中心位置
        canvas?.translate(mWidth / 2, mHeight / 2)
        val rect = RectF(-circleR, -circleR, circleR, circleR)

        //数据中的总的角度
        var allAngel = 0f


        for (i in 0 until dataList.size) {
            paint?.color = dataList[i].color
            canvas?.drawArc(rect, currentAngle, dataList[i].percentage * 360, true, paint!!)
            //中心点位置角度为  当前划的起始角度+当前划得角度的一半
            if (isShowData) {
                if (TextUtils.isEmpty(dataList[i].des)){//描述未设置的话就 显示 percentage
                    showData(currentAngle + dataList[i].percentage * 180, canvas,dataList[i].percentage.toString())
                }else{
                    showData(currentAngle + dataList[i].percentage * 180, canvas,dataList[i].des)
                }
            }
            currentAngle += dataList[i].percentage * 360
            //当前已经划过的角度总和
            allAngel += dataList[i].percentage * 360
        }
        //将剩下的360-已用角度  绘制成我们所设置的背景色
        paint?.color = bgColor
        canvas?.drawArc(rect, currentAngle, 360 - allAngel, true, paint!!)
        if (isShowData) {
            showData(currentAngle + 180 - allAngel / 2, canvas,"未设置的")
        }
    }

    /**
     * 划线标识出数据
     * 我们这里用划过的角度的一半的中心点为 标注起始点
     * @param angle 当前划过的角度 后的最后一次滑动角度的一半的中心点的坐标绝对角度（相对于坐标系）
     */
    fun showData(angle: Float, canvas: Canvas?,dec:String) {
        var pointX = (circleR * Math.cos(Math.toRadians((angle).toDouble()))).toFloat()
        var pointY = (circleR * Math.sin(Math.toRadians((angle).toDouble()))).toFloat()
        paint?.textSize = textSize
        var abxX = Math.abs(pointX)
        if (pointX >= 0) {//在Y轴右边
            if (pointX <= circleR / 2) {//坐标点的长度小于一半R
                canvas?.drawLine(pointX, pointY, circleR + abxX, pointY, paint!!)
                canvas?.drawText(dec, circleR + abxX, pointY, paint!!)
            } else {
                canvas?.drawLine(pointX, pointY, 2 * circleR - abxX, pointY, paint!!)
                canvas?.drawText(dec, 2 * circleR - abxX, pointY, paint!!)
            }
        } else {//在Y轴左边
            if (Math.abs(pointX) <= circleR / 2) {//坐标点的长度小于一半R
                canvas?.drawLine(pointX, pointY, -circleR - abxX, pointY, paint!!)
                canvas?.drawText(dec, -circleR - abxX, pointY, paint!!)
            } else {
                canvas?.drawLine(pointX, pointY, -2.05f * circleR + abxX, pointY, paint!!)
                canvas?.drawText(dec, -2.1f * circleR + abxX, pointY, paint!!)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    fun setIsShowDate(boolean: Boolean){
        isShowData = boolean
        invalidate()
    }


}