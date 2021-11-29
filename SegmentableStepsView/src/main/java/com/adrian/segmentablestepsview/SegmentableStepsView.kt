package com.adrian.segmentablestepsview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.IntDef
import androidx.annotation.Nullable
import kotlin.math.max

//                       _ooOoo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                       O\ = /O
//                   ____/`---'\____
//                 .   ' \\| |// `.
//                  / \\||| : |||// \
//                / _||||| -:- |||||- \
//                  | | \\\ - /// | |
//                | \_| ''\---/'' | |
//                 \ .-\__ `-` ___/-. /
//              ______`. .' /--.--\ `. . __
//           ."" '< `.___\_<|>_/___.' >'"".
//          | | : `- \`.;`\ _ /`;.`/ - ` : | |
//            \ \ `-. \_ __\ /__ _/ .-` / /
//    ======`-.____`-.___\_____/___.-`____.-'======
//                       `=---='
//
//    .............................................
//             佛祖保佑             永无BUG
/**
 * Author:RanQing
 * Date:2021/11/12 3:04 下午
 * Description: 可分段步骤进度控件
 */

class SegmentableStepsView @JvmOverloads constructor(
    context: Context,
    @Nullable attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        // 环形
        const val STYLE_RING = 0

        // 横线
        const val STYLE_LINE_HORIZONTAL = 1

        // 竖线
        const val STYLE_LINE_VERTICAL = 2

        // 扇形
        const val STYLE_CIRCLE = 3

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(STYLE_RING, STYLE_LINE_HORIZONTAL, STYLE_LINE_VERTICAL, STYLE_CIRCLE)
        annotation class StepStyle

        //常规字体
        const val TEXT_STYLE_NORMAL = Typeface.NORMAL
        //粗体
        const val TEXT_STYLE_BOLD = Typeface.BOLD
        //斜体
        const val TEXT_STYLE_ITALIC = Typeface.ITALIC
        //加粗斜体
        const val TEXT_STYLE_BOLD_ITALIC = Typeface.BOLD_ITALIC

        @Retention(AnnotationRetention.SOURCE)
        @IntDef(TEXT_STYLE_NORMAL, TEXT_STYLE_BOLD, TEXT_STYLE_ITALIC, TEXT_STYLE_BOLD_ITALIC)
        annotation class TextStyle
    }

    //最大步骤数
    var maxSteps = 5
        set(value) {
            field = if (value < 1) 1 else value
            invalidate()
        }

    //当前步骤数
    var stepIndex = 3
        set(value) {
            field = if (value < 0) 0 else if (value > maxSteps) maxSteps else value
            stepChangeListener?.invoke(field)
            invalidate()
        }

    //各个步骤片段颜色数组
    var colorsArray: IntArray =
        intArrayOf(Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA)
        set(value) {
            field = value
            invalidate()
        }

    //步骤背景颜色
    @ColorInt
    var stepBgColor = Color.GRAY
        set(value) {
            field = value
            invalidate()
        }

    //圆环时中间部分颜色
    @ColorInt
    var stepRingCenterColor = Color.TRANSPARENT
        set(value) {
            field = value
            invalidate()
        }

    //步骤起止片段圆角度数
//    var stepCornerRadius = 5f
//        set(value) {
//            field = value
//            invalidate()
//        }

    //环形或者圆形时外半径
    @Dimension
    var stepOutsideRadius = 10f
        set(value) {
            field = value
            stepInsideRadius =
                if (stepOutsideRadius > stepStrokeWidth) stepOutsideRadius - stepStrokeWidth else stepOutsideRadius
            invalidate()
        }

    //环形内半径
    @Dimension
    private var stepInsideRadius = 5f

    //宽度
    @Dimension
    var stepStrokeWidth = 5f
        set(value) {
            field = if (value > 0) value else 1f
            stepInsideRadius =
                if (stepOutsideRadius > stepStrokeWidth) stepOutsideRadius - stepStrokeWidth else stepOutsideRadius
            invalidate()
        }

    //步骤样式:环形、水平、垂直、圆形、扇形
    @StepStyle
    var stepStyle = STYLE_LINE_HORIZONTAL
        set(value) {
            field = value
            progressPaint.style = if (field == STYLE_RING) Paint.Style.STROKE else Paint.Style.FILL
            invalidate()
        }

    //环状时居中图片
    var ringCenterImage: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }

    //环状时居中文字
    var ringCenterText: String? = null
        set(value) {
            field = value
            invalidate()
        }

    //环状时居中文字大小,当设置为可自动调节时，为最大字体
    @Dimension
    var ringCenterTextSize = 20f
        set(value) {
            field = value
            textPaint.textSize = field
            invalidate()
        }

    //环状时居中文字颜色
    @ColorInt
    var ringCenterTextColor = Color.BLACK
        set(value) {
            field = value
            textPaint.color = field
            invalidate()
        }

    //环状时居中文字样式
    @TextStyle
    var ringCenterTextStyle = TEXT_STYLE_NORMAL
        set(value) {
            field = value
            textPaint.typeface = Typeface.defaultFromStyle(field)
            invalidate()
        }

    //环状时是否自动调节居中文本大小
    var ringAutoAdjustTextSize = false
        set(value) {
            field = value
            invalidate()
        }

    private val textPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = ringCenterTextSize
            textAlign = Paint.Align.CENTER
        }
    }

    private val progressPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
    }

    var stepChangeListener:((step: Int) -> Unit)? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.SegmentableStepsView)?.also {
//            stepCornerRadius =
//                it.getDimension(R.styleable.SegmentableStepsView_step_corner_radius, 10f)
            maxSteps = it.getInt(R.styleable.SegmentableStepsView_max_steps, 5)
            stepBgColor =
                it.getColor(R.styleable.SegmentableStepsView_step_background_color, Color.GRAY)
            stepRingCenterColor =
                it.getColor(
                    R.styleable.SegmentableStepsView_step_ring_center_color,
                    Color.TRANSPARENT
                )
            stepOutsideRadius =
                it.getDimension(R.styleable.SegmentableStepsView_step_outside_radius, 10f)
            stepIndex = it.getInt(R.styleable.SegmentableStepsView_step_index, 3)
            it.getResourceId(R.styleable.SegmentableStepsView_step_colors_array, 0)
                .takeIf { arrayId ->
                    arrayId > 0
                }?.apply {
                    context.resources.getIntArray(this).also { residArray ->
                        val colors = IntArray(residArray.size)
                        residArray.forEachIndexed { index, color ->
                            colors[index] = color
                        }
                        colorsArray = colors
                    }
                }
            stepStyle = it.getInt(R.styleable.SegmentableStepsView_step_style, STYLE_RING)
            stepStrokeWidth =
                it.getDimension(R.styleable.SegmentableStepsView_step_stroke_width, 5f)
            ringCenterImage =
                it.getDrawable(R.styleable.SegmentableStepsView_step_ring_center_image)
            ringCenterText = it.getString(R.styleable.SegmentableStepsView_step_ring_center_text)
            ringCenterTextColor = it.getColor(R.styleable.SegmentableStepsView_step_ring_center_textColor, Color.BLACK)
            ringCenterTextSize = it.getDimension(R.styleable.SegmentableStepsView_step_ring_center_textSize, 20f)
            ringCenterTextStyle = it.getInt(R.styleable.SegmentableStepsView_step_ring_center_textStyle, TEXT_STYLE_NORMAL)
            ringAutoAdjustTextSize = it.getBoolean(R.styleable.SegmentableStepsView_step_ring_auto_adjust_textSize, false)
        }.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(getWidthSize(widthMeasureSpec), getHeightSize(heightMeasureSpec))
    }

    private fun getWidthSize(measureSpec: Int): Int {
        var specMode = MeasureSpec.getMode(measureSpec)
        var specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.EXACTLY -> {
                specSize
            }
            MeasureSpec.AT_MOST -> {
                val strokeW = when (stepStyle) {
                    STYLE_LINE_HORIZONTAL -> 0
                    STYLE_LINE_VERTICAL -> stepStrokeWidth
                    STYLE_RING -> stepOutsideRadius * 2
                    else -> 0
                }
                paddingStart + paddingEnd + strokeW.toInt()
            }
            MeasureSpec.UNSPECIFIED -> {
                max(suggestedMinimumWidth, specSize)
            }
            else -> suggestedMinimumWidth
        }
    }

    private fun getHeightSize(measureSpec: Int): Int {
        var specMode = MeasureSpec.getMode(measureSpec)
        var specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.EXACTLY -> {
                specSize
            }
            MeasureSpec.AT_MOST -> {
                val strokeH = when (stepStyle) {
                    STYLE_LINE_HORIZONTAL -> stepStrokeWidth
                    STYLE_LINE_VERTICAL -> 0
                    STYLE_RING -> stepOutsideRadius * 2
                    else -> 0
                }
                paddingTop + paddingBottom + strokeH.toInt()
            }
            MeasureSpec.UNSPECIFIED -> {
                max(suggestedMinimumHeight, specSize)
            }
            else -> suggestedMinimumHeight
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        stepInsideRadius =
//            if (stepOutsideRadius > stepStrokeWidth) stepOutsideRadius - stepStrokeWidth else stepOutsideRadius
        val sweepAngle = 360f / maxSteps
        when (stepStyle) {
            STYLE_LINE_HORIZONTAL, STYLE_LINE_VERTICAL -> {
                canvas?.drawRect(
                    paddingStart.toFloat(),
                    paddingTop.toFloat(),
                    if (stepStyle == STYLE_LINE_HORIZONTAL) width.toFloat() - paddingRight else stepStrokeWidth + paddingLeft,
                    if (stepStyle == STYLE_LINE_HORIZONTAL) stepStrokeWidth + paddingTop else height.toFloat() - paddingBottom,
                    progressPaint.also {
                        it.style = Paint.Style.FILL
                        it.color = stepBgColor
                        it.strokeWidth = stepStrokeWidth
                    })
            }
            STYLE_RING -> {
                canvas?.drawArc(paddingStart + stepStrokeWidth / 2,
                    paddingTop + stepStrokeWidth / 2,
                    paddingStart + stepOutsideRadius * 2 - stepStrokeWidth / 2,
                    paddingTop + stepOutsideRadius * 2 - stepStrokeWidth / 2,
                    0f,
                    360f,
                    false,
                    progressPaint.also {
                        it.style = Paint.Style.STROKE
                        it.color = stepBgColor
                        it.strokeWidth = stepStrokeWidth
                    })
                val cx = paddingStart + stepOutsideRadius
                val cy = paddingTop + stepOutsideRadius
                canvas?.drawCircle(
                    cx, cy, stepInsideRadius, progressPaint.also {
                        it.style = Paint.Style.FILL
                        it.color = stepRingCenterColor
                    }
                )
                ringCenterImage?.let { img ->
                    canvas?.drawBitmap(
                        img.toBitmap().toCircle(),
                        Rect(0, 0, img.intrinsicWidth, img.intrinsicHeight),
                        Rect(
                            paddingStart + stepStrokeWidth.toInt(),
                            paddingTop + stepStrokeWidth.toInt(),
                            paddingStart + stepOutsideRadius.toInt() + stepInsideRadius.toInt(),
                            paddingTop + stepOutsideRadius.toInt() + stepInsideRadius.toInt()
                        ), progressPaint.apply {
                            color = Color.WHITE
                        }
                    )
                }
                ringCenterText?.let { text ->
                    var textWidth = textPaint.apply { textSize = ringCenterTextSize }.measureText(text)
//                    val textRect = Rect().also { r ->
//                        textPaint.getTextBounds(text, 0, text.length, r)
//                    }
                    val maxRect = RectF(paddingStart+stepStrokeWidth, paddingTop+stepStrokeWidth, paddingStart+stepOutsideRadius+stepInsideRadius, paddingTop+stepOutsideRadius+stepInsideRadius)
//                    "width".logE("txtW1:$textWidth, txtW2:${textRect.width()}, maxW:${maxRect.width()}")
                    if (maxRect.width() >= textWidth) {
                        val fontMetrics = textPaint.fontMetrics
                        val x = paddingStart + stepOutsideRadius - stepInsideRadius / 2
                        val y =
                            paddingTop + stepOutsideRadius + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
                        canvas?.drawText(text, maxRect.centerX(), y, textPaint)
                    } else if (ringAutoAdjustTextSize) {
                        var tmp = ringCenterTextSize
                        do {
                            --tmp
                        } while (textPaint.apply { textSize = tmp }.measureText(text)>maxRect.width())
                        val fontMetrics = textPaint.fontMetrics
                        val x = paddingStart + stepOutsideRadius - stepInsideRadius / 2
                        val y =
                            paddingTop + stepOutsideRadius + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent
                        canvas?.drawText(text, maxRect.centerX(), y, textPaint)
                    } else {
                        ringCenterText = text.substring(0, text.length-1)
                    }
                }
            }
            else -> {
                val cx = paddingStart + stepOutsideRadius
                val cy = paddingTop + stepOutsideRadius
                canvas?.drawCircle(
                    cx, cy, stepOutsideRadius, progressPaint.also {
                        it.style = Paint.Style.FILL
                        it.color = stepBgColor
                    })
            }
        }

        if (stepIndex > 0) {
//            stepIndex = min(stepIndex, maxSteps)
            val validW = width.toFloat() - paddingLeft - paddingRight
            val validH = height.toFloat() - paddingTop - paddingBottom
            for (index in 1..stepIndex) {
                val color = if (index <= colorsArray.size) {
                    colorsArray[index - 1]
                } else {
                    colorsArray.last()
                }
                when (stepStyle) {
                    STYLE_LINE_HORIZONTAL, STYLE_LINE_VERTICAL -> {
                        canvas?.drawRect(
                            if (stepStyle == STYLE_LINE_HORIZONTAL) (index - 1f) / maxSteps * validW + paddingLeft else paddingStart.toFloat(),
                            if (stepStyle == STYLE_LINE_HORIZONTAL) paddingTop.toFloat() else validH * (maxSteps - index) / maxSteps + paddingTop,
                            if (stepStyle == STYLE_LINE_HORIZONTAL) 1f * index / maxSteps * validW + paddingLeft else stepStrokeWidth + paddingLeft,
                            if (stepStyle == STYLE_LINE_HORIZONTAL) stepStrokeWidth + paddingTop else validH * (maxSteps - index + 1) / maxSteps + paddingTop,
                            progressPaint.also {
                                it.style = Paint.Style.FILL
                                it.color = color
                            })
                    }
                    STYLE_RING -> {
                        val halfStroke = stepStrokeWidth / 2
                        canvas?.drawArc(paddingStart + halfStroke,
                            paddingTop + halfStroke,
                            paddingStart + stepOutsideRadius * 2 - halfStroke,
                            paddingTop + stepOutsideRadius * 2 - halfStroke,
                            (index - 1f) / maxSteps * 360 - 90,
                            sweepAngle,
                            false,
                            progressPaint.also {
                                it.style = Paint.Style.STROKE
                                it.color = color
                                it.strokeWidth = stepStrokeWidth
                            })
                    }
                    else -> {
//                        val cx = paddingStart + stepOutsideRadius
//                        val cy = paddingTop + stepOutsideRadius
                        canvas?.drawArc(paddingStart.toFloat(),
                            paddingTop.toFloat(),
                            paddingStart + stepOutsideRadius * 2,
                            paddingTop + stepOutsideRadius * 2,
                            (index - 1f) / maxSteps * 360 - 90,
                            sweepAngle,
                            true,
                            progressPaint.also {
                                it.style = Paint.Style.FILL
                                it.color = color
                                it.strokeWidth = stepStrokeWidth
                            })
//                        canvas?.drawCircle(
//                            cx,
//                            cy,
//                            stepInsideRadius,
//                            progressPaint.also {
//                                it.color = stepRingCenterColor
//                                it.strokeWidth = 0f
//                            }
//                        )
                    }
                }

            }
        }
    }

    private fun Drawable.toBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, if (opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        draw(Canvas(bitmap))
        return bitmap
//        return toBitmap(intrinsicWidth, intrinsicHeight, if (opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
    }

    private fun Bitmap.toCircle() = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).let {
        Canvas(it).also { canvas ->
            Paint(Paint.ANTI_ALIAS_FLAG).also { paint ->
                paint.color = Color.WHITE
                canvas.drawARGB(0, 0 , 0, 0)
                val rect = Rect(0, 0, width, height)
                canvas.drawOval(RectF(rect), paint)
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                canvas.drawBitmap(this, rect, rect, paint)
            }
        }
        it
    }

}

fun String.logE(msg: String) = Log.e(this, msg)