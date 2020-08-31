package com.afra55.speedometer

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.graphics.drawable.toBitmap
import androidx.core.text.isDigitsOnly
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class SpeedometerDialog : View {

    /**
     * 刻度数字颜色
     */
    private var meterNumberTextColor: Int = Color.RED

    /**
     * 中心速度数字颜色
     */
    private var meterCenterTextColor: Int = Color.RED

    /**
     * 中心描述文字颜色
     */
    private var meterCenterDescTextColor: Int = Color.RED

    /**
     * 指针转过区域的刻度颜色
     */
    private var meterNumberSelectedTextColor: Int = Color.RED

    /**
     * 超过限速值的刻度颜色
     */
    private var meterNumberLimitTextColor: Int = Color.RED

    /**
     * 刻度字体大小
     */
    private var meterNumberTextSize: Float = 9f

    /**
     * 刻度数字距离边界的距离
     */
    private var meterNumberMargin: Int = 0

    /**
     * 当前速度，指针指向的数字
     */
    private var currentNumber = 0F

    /**
     * 绕 X 轴旋转
     */
    private var meterRotateX = 0F

    /**
     * Z轴平移
     */
    private var meterTranslateZ = 0

    /**
     * Y轴平移
     */
    private var meterTranslateY = 0

    /**
     * 0 km/s
     * 1 公里/s
     */
    var meterMeasureType = 0

    /**
     * 是否隐藏中心数字
     */
    var meterHideCenterNumber = false

    /**
     * 是否隐藏刻度
     */
    var meterHideDividerNumber = false

    /**
     * 表盘特殊类型
     * 0: 默认　
     * 1：只有指针指向的刻度变颜色，中间数字在描述下面
     * 2： 隐藏指针，隐藏刻度, 背景和蒙板图片都绘制到 mask 上
     */
    private var meterType = 0

    /**
     * 刻度最大值
     * 0 - maxNumber
     */
    private var maxNumber = 180F

    /**
     * 速度表底部 0 和 最大刻度值之间的夹角
     */
    private var bottomEmptyAngle = 90

    /**
     * 测试用虚线，只在编辑模式展示
     */
    private var dashCirclePaint: Paint? = null

    /**
     * 起始角度，刻度0与垂直线的夹角
     */
    private var startAngle = bottomEmptyAngle / 2F

    /**
     *  限速值，如果大于这个值，表盘开始闪烁
     */
    private var limitNumber = 120

    /**
     * 动画，指针旋转动画
     */
    var valueAnimator: ValueAnimator? = null

    /**
     * 更改透明度动画
     */
    var alphaAnimator: ValueAnimator? = null

    private var meterNumberTextPaint: TextPaint? = null
    private var meterNumberSelectedTextPaint: TextPaint? = null


    /**
     * 中心数字文字 TextPaint
     */
    private var meterCenterNumberTextPaint: TextPaint? = null

    /**
     * 中心文字Desc TextPaint
     */
    private var meterCenterDescTextPaint: TextPaint? = null

    /**
     * 中心描述文字
     */
    var meterCenterDesc: String? = null

    /**
     * 中心数字文字高度　
     */
    var centerNumberTextHeight: Float = 0F

    /**
     * 中心文字描述高度　
     */
    var centerDescTextHeight: Float = 0F

    /**
     * 中心描述文字大小
     */
    var meterCenterDescTextSize: Float = 9F

    /**
     * 中心数字文字大小
     */
    var meterCenterNumberTextSize: Float = 9F

    /**
     * 中心文字字体
     */
    var meterCenterFontAssetPath: String? = null

    /**
     * 数字文字字体
     */
    var meterNumberFontAssetPath: String? = null


    /**
     * 表盘背景
     */
    var meterBg: Drawable? = null

    /**
     * 表盘MaskBg
     */
    var meterMaskBg: Drawable? = null

    /**
     * 表盘中心覆盖的 icon
     */
    var meterCenterIcon: Drawable? = null

    /**
     * mask point
     */
    var translateMaskPoint: Paint? = null

    /**
     * 表盘指针
     */
    var meterPointer: Drawable? = null

    /**
     * 表盘等分区域数量
     */
    private var dividerAreaNumber = 9

    /**
     * 表盘等分刻度
     */
    var dividerNumberList = mutableListOf<MeterNumber>()

    /**
     * 需要回收的 bitmap　
     */
    var needRecyclerBitmapList = mutableListOf<Bitmap>()


    fun setLimitNumber(number: Int) {
        val tempNumber = when {
//            number > maxNumber -> {
//                maxNumber.toInt()
//            }
            number < 0 -> {
                0
            }
            else -> {
                number
            }
        }
        limitNumber = tempNumber

        if (translateMaskPoint != null && width > 0 && height > 0) {
            drawMask(width, height)
        }
    }

    fun getLimitNumber(): Int {
        return limitNumber
    }

    fun setMaxNumber(maxNumber: Float) {
        this.maxNumber = maxNumber
        invalidateTextPaintAndMeasurements()
        if (translateMaskPoint != null && width > 0 && height > 0) {
            drawMask(width, height)
        }
        invalidate()
    }

    fun setCenterDesc(str: String?) {
        meterCenterDesc = str
        if (width > 0 && height > 0) {
            invalidate()
        }
    }

    fun setCurrentNumber(currentNumber: Float) {
        if (currentNumber == this.currentNumber) {
            return
        }

        val tempNumber = when {
//            currentNumber > maxNumber -> {
//                maxNumber
//            }
            currentNumber < 0 -> {
                0F
            }
            else -> {
                currentNumber
            }
        }
        resetAnimator(this.currentNumber, tempNumber)
    }

    fun getCurrentNumber(): Float {
        return currentNumber
    }

    /**
     * 速度数字动画
     */
    fun resetAnimator(oldNumber: Float, nextNumber: Float) {
        valueAnimator?.cancel()
//        try {
//            ValueAnimator::class.java.getMethod("setDurationScale", Float::class.javaPrimitiveType).invoke(null, 1f)
//        } catch (e: Exception) {
//        }
        valueAnimator = ValueAnimator.ofFloat(oldNumber, nextNumber)
        valueAnimator?.duration = 500L
        valueAnimator?.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator?.addUpdateListener {
            try {
                val v = it.animatedValue as Float
                currentNumber = v
                postInvalidate()
                if (limitNumber > 0 && currentNumber > limitNumber) {
                    if (alphaAnimator == null || !alphaAnimator!!.isRunning) {
                        alphaAnimator()
                    }
                } else {
                    if (alphaAnimator?.isRunning == true) {
                        alphaAnimator?.removeAllUpdateListeners()
                        alphaAnimator?.pause()
                        alphaAnimator?.cancel()
                        alpha = 1F
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        valueAnimator?.start()
    }

    /**
     * 透明度动画
     */
    fun alphaAnimator() {

        alphaAnimator?.pause()
        alphaAnimator?.cancel()
        alphaAnimator = ValueAnimator.ofFloat(1F, 0.7F)
        alphaAnimator?.repeatCount = INFINITE
        alphaAnimator?.repeatMode = REVERSE
        alphaAnimator?.duration = 500L
        alphaAnimator?.addUpdateListener {
            try {
                alpha = it.animatedValue as Float
            } catch (e: Exception) {
            }
        }
        alphaAnimator?.start()
    }

    fun setDividerAreaNumber(areaNumber: Int) {
        if (areaNumber > 0) {
            dividerAreaNumber = areaNumber
            invalidateTextPaintAndMeasurements()
            invalidate()
        }
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        keepScreenOn = true
        // Load attributes
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.SpeedometerDialog, defStyle, 0
        )

        meterNumberTextColor = a.getColor(
            R.styleable.SpeedometerDialog_meterNumberTextColor,
            meterNumberTextColor
        )
        meterCenterTextColor = a.getColor(
            R.styleable.SpeedometerDialog_meterCenterTextColor,
            meterCenterTextColor
        )
        meterCenterDescTextColor = a.getColor(
            R.styleable.SpeedometerDialog_meterCenterDescTextColor,
            meterCenterTextColor
        )
        meterHideCenterNumber = a.getBoolean(
            R.styleable.SpeedometerDialog_meterHideCenterNumber,
            meterHideCenterNumber
        )
        meterHideDividerNumber = a.getBoolean(
            R.styleable.SpeedometerDialog_meterHideDividerNumber,
            meterHideDividerNumber
        )
        meterNumberSelectedTextColor = a.getColor(
            R.styleable.SpeedometerDialog_meterNumberSelectedTextColor,
            meterNumberSelectedTextColor
        )
        meterNumberLimitTextColor = a.getColor(
            R.styleable.SpeedometerDialog_meterNumberLimitTextColor,
            meterNumberLimitTextColor
        )
        bottomEmptyAngle = a.getInt(
            R.styleable.SpeedometerDialog_meterBottomEmptyAngle,
            bottomEmptyAngle
        )
        meterType = a.getInt(
            R.styleable.SpeedometerDialog_meterType,
            meterType
        )
        meterRotateX = a.getFloat(
            R.styleable.SpeedometerDialog_meterRotateX,
            meterRotateX
        )
        meterTranslateZ = a.getDimensionPixelSize(
            R.styleable.SpeedometerDialog_meterTranslateZ,
            meterTranslateZ
        )
        meterTranslateY = a.getDimensionPixelSize(
            R.styleable.SpeedometerDialog_meterTranslateY,
            meterTranslateY
        )
        dividerAreaNumber =
            a.getInt(R.styleable.SpeedometerDialog_meterDividerAreaNumber, dividerAreaNumber)
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        meterNumberTextSize = a.getDimension(
            R.styleable.SpeedometerDialog_meterNumberTextSize,
            meterNumberTextSize
        )      // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        meterCenterDescTextSize = a.getDimension(
            R.styleable.SpeedometerDialog_meterCenterDescTextSize,
            meterCenterDescTextSize
        )
        meterCenterNumberTextSize = a.getDimension(
            R.styleable.SpeedometerDialog_meterCenterNumberTextSize,
            meterCenterNumberTextSize
        )
        meterCenterFontAssetPath =
            a.getString(R.styleable.SpeedometerDialog_meterCenterFontAssetPath)
        meterNumberFontAssetPath =
            a.getString(R.styleable.SpeedometerDialog_meterNumberFontAssetPath)
        meterCenterDesc = a.getString(R.styleable.SpeedometerDialog_meterCenterDesc)
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        meterNumberMargin = a.getDimensionPixelSize(
            R.styleable.SpeedometerDialog_meterNumberMargin,
            meterNumberMargin
        )

        if (a.hasValue(R.styleable.SpeedometerDialog_meterBg)) {
            meterBg = a.getDrawable(
                R.styleable.SpeedometerDialog_meterBg
            )
            meterBg?.callback = this
        }

        if (a.hasValue(R.styleable.SpeedometerDialog_meterMaskBg)) {
            meterMaskBg = a.getDrawable(
                R.styleable.SpeedometerDialog_meterMaskBg
            )
            meterMaskBg?.callback = this
        }
        if (a.hasValue(R.styleable.SpeedometerDialog_meterCenterIc)) {
            meterCenterIcon = a.getDrawable(
                R.styleable.SpeedometerDialog_meterCenterIc
            )
            meterCenterIcon?.callback = this
        }
        if (a.hasValue(R.styleable.SpeedometerDialog_meterPointer)) {
            meterPointer = a.getDrawable(
                R.styleable.SpeedometerDialog_meterPointer
            )
            meterPointer?.callback = this
        }

        a.recycle()

        startAngle = bottomEmptyAngle / 2F

        dashCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        dashCirclePaint!!.color = Color.GREEN
        dashCirclePaint!!.style = Paint.Style.STROKE
        dashCirclePaint!!.strokeWidth = dp2Px(2F)
        val dashWidth = dp2Px(2F)
        dashCirclePaint!!.pathEffect = DashPathEffect(floatArrayOf(dashWidth, dashWidth), 0F)
        // Set up a default TextPaint object
        meterNumberTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }

        meterCenterNumberTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
        }
        meterCenterDescTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.CENTER
        }
        meterNumberSelectedTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
        }


        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            drawMask(w, h)
        }
    }

    /**
     * 创建指针划过区域的遮照
     * 这里使用的是 xfermode 的 SRC_OVER 模式
     * PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
     */
    private fun drawMask(w: Int, h: Int) {
        translateMaskPoint = Paint()
        translateMaskPoint?.apply {

            for (i in needRecyclerBitmapList) {
                i.recycle()
            }
            needRecyclerBitmapList.clear()

            flags = Paint.ANTI_ALIAS_FLAG

            var ca: Canvas? = null
            var bitmap: Bitmap? = null
            if (meterType == 2) { // meterType 如是是 2，表盘背景会通过指针扫过区域展示, 先绘制表盘背景到遮照上
                if (meterBg != null) {
                    meterBg!!.setBounds(
                        paddingLeft, paddingTop,
                        paddingLeft + w, paddingTop + h
                    )
                    bitmap = meterBg!!.toBitmap(w, h, Bitmap.Config.ARGB_8888)
                    ca = Canvas(bitmap)
                    needRecyclerBitmapList.add(bitmap)
                }
            }

            if (meterMaskBg != null) {
                // 绘制遮照图
                meterMaskBg!!.setBounds(
                    paddingLeft, paddingTop,
                    paddingLeft + w, paddingTop + h
                )
                val bitmapMaskBg = meterMaskBg!!.toBitmap(w, h, Bitmap.Config.ARGB_8888)
                if (ca != null) {
                    // 如果遮照已经绘制了表盘背景
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {

                        shader = bitmapMaskBg.let {
                            needRecyclerBitmapList.add(it)
                            BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                        }

                        // SRC_OVER 模式
                        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

                        // 对 limit number 的限制　
                        var limitNumber1 = limitNumber
                        if (limitNumber1 > maxNumber) {
                            limitNumber1 = maxNumber.toInt()
                        }
                        // 绘制遮照图的范围，即表盘中心与大于限速刻度到最大刻度值的一个 arc 区域
                        val fl =
                            if (limitNumber1 > 0) startAngle + limitNumber1 / maxNumber * (360 - bottomEmptyAngle) else 360F
                        ca?.drawArc(
                            paddingLeft.toFloat(),
                            paddingTop.toFloat(),
                            (paddingLeft + w).toFloat(),
                            (paddingTop + h).toFloat(),
                            90F,
                            fl,
                            true,
                            this
                        )
                    }

                } else {
                    // 如果表盘没有绘制在遮照上，则遮照即是设置的 meterMaskBg
                    bitmap = bitmapMaskBg
                }

            } else {
                // 如果没有设置遮照，则创建一个空白图
                if (ca == null) {
                    bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                }
            }


            if (ca == null) {
                ca = Canvas(bitmap!!)
            }
            if (meterNumberSelectedTextColor != meterNumberTextColor) {
                // 绘制指针划过区域的刻度数字
                drawNumber(w, w / 2F, h / 2F, meterNumberSelectedTextPaint!!, ca, true)
            }
            shader = bitmap?.let {
                needRecyclerBitmapList.add(it)
                BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }
            // 给遮照 Paint 设置 SRC_OVER xfermode 模式
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        // 这是个正方形的表盘
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

    }


    /**
     * 初始化文字相关的 TextPaint
     */
    private fun invalidateTextPaintAndMeasurements() {
        meterNumberTextPaint?.let {// 刻度数字Paint
            it.textSize = meterNumberTextSize
            it.color = meterNumberTextColor

            if (!isInEditMode) {
                meterNumberFontAssetPath?.let { fontPath ->
                    it.typeface = Typeface.createFromAsset(context.assets, fontPath)
                }
            }

            // 获取所有刻度数字
            dividerNumberList.clear()
            val oneAreaNumber = maxNumber / dividerAreaNumber
            // 拿到最宽的字符，数字画出来更好看一点
            var maxWidth = it.measureText((dividerAreaNumber * oneAreaNumber).toInt().toString())
            for (i in 0..dividerAreaNumber) {
                val str = (i * oneAreaNumber).toInt().toString()
                val measureText = it.measureText(str)
                if (measureText > maxWidth) {
                    maxWidth = measureText
                }
                val textWidth = maxWidth
                val textHeight = it.fontMetrics.bottom

                dividerNumberList.add(MeterNumber(str, textWidth, textHeight))
            }
        }
        meterNumberSelectedTextPaint?.let { // 指针扫过的刻度数字Paint
            it.textSize = meterNumberTextSize
            it.color = meterNumberSelectedTextColor
            if (!isInEditMode) {
                meterNumberFontAssetPath?.let { fontPath ->
                    it.typeface = Typeface.createFromAsset(context.assets, fontPath)
                }
            }
        }
        meterCenterNumberTextPaint?.let {// 中心速度数字Paint
            it.textSize = meterCenterNumberTextSize
            it.color = meterCenterTextColor
            if (!isInEditMode) {
                meterCenterFontAssetPath?.let { fontPath ->
                    it.typeface = Typeface.createFromAsset(context.assets, fontPath)
                }
            }
            centerNumberTextHeight = it.getCapHeight().toFloat()
        }
        meterCenterDescTextPaint?.let {// 中心描述数字Paint
            it.textSize = meterCenterDescTextSize
            it.color = meterCenterDescTextColor
            if (!isInEditMode) {
                meterCenterFontAssetPath?.let { fontPath ->
                    it.typeface = Typeface.createFromAsset(context.assets, fontPath)
                }
            }
            centerDescTextHeight = it.getCapHeight().toFloat()
        }
    }


    var camera: Camera? = null
    val cameraMatrix = Matrix()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        try {

            canvas.save()
            val paddingLeft = paddingLeft
            val paddingTop = paddingTop
            val paddingRight = paddingRight
            val paddingBottom = paddingBottom

            val contentWidth = width - paddingLeft - paddingRight
            val contentHeight = height - paddingTop - paddingBottom

            val cx = paddingLeft + (contentWidth.toFloat()) / 2
            val cy = paddingTop + (contentHeight.toFloat()) / 2

            if (meterRotateX != 0F) {
                // 如果设置了旋转，先将画布旋转

                if (camera == null) {
                    camera = Camera()
                }
                camera?.let {

                    it.save()
                    it.rotateX(meterRotateX);
                    it.rotateY(0F);
                    it.rotateZ(0F); // Rotate around Z access (similar to canvas.rotate)
                    it.translate(0F, meterTranslateY.toFloat(), meterTranslateZ.toFloat())


                    it.getMatrix(cameraMatrix);

                    cameraMatrix.preTranslate(-cx, -cy);

                    cameraMatrix.postTranslate(cx, cy);
                    it.restore();

                    canvas.concat(cameraMatrix);
                }
            }
            if (meterType != 2) { // 如果 meterType 是 2 则表盘背景不绘制在画布上，它在遮照层里面
                meterBg?.let {
                    it.setBounds(
                        paddingLeft, paddingTop,
                        paddingLeft + contentWidth, paddingTop + contentHeight
                    )
                    it.draw(canvas)
                }
                drawNumber(contentWidth, cx, cy, meterNumberTextPaint!!, canvas)
            }
            canvas.save()
            // 遮照层 start


            // 当前指针指向的角度
            val needDegreesBuyCurrentNumber = getNeedDegreesBuyCurrentNumber()
            translateMaskPoint?.let {
                if (meterType == 1) { // meterType 是1 则遮照的展示区域是指针的上下 15度
                    canvas.drawArc(
                        paddingLeft.toFloat(),
                        paddingTop.toFloat(),
                        (paddingLeft + contentWidth).toFloat(),
                        (paddingTop + contentHeight).toFloat(),
                        needDegreesBuyCurrentNumber + 90F - 15,
                        30F,
                        true,
                        it
                    )
                } else {
                    // 默认情况下，遮照是从最底下绘制，顺时针到指针指向的角度
                    canvas.drawArc(
                        paddingLeft.toFloat(),
                        paddingTop.toFloat(),
                        (paddingLeft + contentWidth).toFloat(),
                        (paddingTop + contentHeight).toFloat(),
                        90F,
                        needDegreesBuyCurrentNumber,
                        true,
                        it
                    )
                }
            }

            // 遮照层 end
            canvas.restore()


            if (meterType != 2) {
                // 默认情况下， 绘制指针
                canvas.save()

                meterPointer?.let {
                    it.setBounds(
                        paddingLeft, paddingTop,
                        paddingLeft + contentWidth, paddingTop + contentHeight
                    )
                    canvas.rotate(
                        needDegreesBuyCurrentNumber,
                        (width / 2).toFloat(), (height / 2).toFloat()
                    )
                    it.draw(canvas)
                }
                canvas.restore()

            }

            // 绘制中心数字的背景
            meterCenterIcon?.let {
                it.setBounds(
                    paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight
                )
                it.draw(canvas)
            }


            canvas.restore()

            if (meterNumberTextPaint != null && !meterHideCenterNumber) {
                // 绘制中心数字
                val currentNumberString = currentNumber.toInt().toString()

                val dividerHeight = dp2Px(6F) / 2
                val offset =
                    if (centerDescTextHeight > 0) centerNumberTextHeight - (centerNumberTextHeight + centerDescTextHeight) / 2F else centerNumberTextHeight / 2F
                var numberY = cy + offset - dividerHeight
                var descY = (cy + centerDescTextHeight) + offset + dividerHeight
                if (meterType == 1) {
                    // meterType 是 1 的时候，desc 绘制在数字上面
                    numberY = cy + centerNumberTextHeight - offset + dividerHeight
                    descY = cy - offset - dividerHeight
                } else {
                    // meterType 是 0 的时候，desc 绘制在数字下面

                }

                if (isInEditMode) {
                    dashCirclePaint?.let { canvas.drawLine(cx - 100, cy, cx + 100, cy, it) }
                }

                meterCenterNumberTextPaint?.let {
                    canvas.drawText(
                        currentNumberString,
                        (width / 2F).toFloat(),
                        numberY,
                        it
                    )
                }
                if (!meterCenterDesc.isNullOrEmpty()) {
                    meterCenterDescTextPaint?.let {
                        canvas.drawText(
                            meterCenterDesc!!,
                            (width / 2F).toFloat(),
                            descY,
                            it
                        )
                    }
                }


            }

            if (isInEditMode) {
                // 编辑模式下，绘制一个圈来看表盘的位置
                canvas.drawCircle(cx, cy, contentWidth / 2 - 10F, dashCirclePaint!!)
            }


        } catch (e: Throwable) {
        }
    }

    /**
     * 指针指向的角度
     */
    private fun getNeedDegreesBuyCurrentNumber(): Float {

        var fl = currentNumber / maxNumber
        if (fl > 1) {
            fl = 1F
        }
        return startAngle + fl * (360 - bottomEmptyAngle)
    }

    /**
     * 绘制刻度数字
     */
    private fun drawNumber(
        contentWidth: Int,
        cx: Float,
        cy: Float,
        textPaint: TextPaint,
        canvas1: Canvas,
        showLimitNumberColor: Boolean = false
    ) {
        if (meterHideDividerNumber) {
            return
        }
        if (meterNumberTextPaint != null) {
            val perAngle = (360 - bottomEmptyAngle) / dividerAreaNumber.toDouble()
            val textRadius = contentWidth / 2F - meterNumberMargin


            if (isInEditMode) {
                dashCirclePaint?.let {
                    canvas1.drawCircle(
                        cx,
                        cy,
                        textRadius,
                        it
                    )
                }
            }

            dividerNumberList.forEachIndexed { i, action ->
                val thisAngle = startAngle + i * perAngle
                val realAngle = Math.toRadians(thisAngle)
                val mathSin = abs(sin(realAngle))
                val mathCos = abs(cos(realAngle))
                val dx = textRadius * mathSin
                val dy = textRadius * mathCos
                var strCX: Double
                var strCY: Double
                if (thisAngle < 90) {
                    strCX = (cx - dx)
                    strCY = (cy + dy)
                } else if (thisAngle >= 90 && thisAngle < 180) {
                    strCX = (cx - dx)
                    strCY = (cy - dy)
                } else if (thisAngle >= 180 && thisAngle < 270) {
                    strCX = (cx + dx)
                    strCY = (cy - dy)
                } else {
                    strCX = (cx + dx)
                    strCY = (cy + dy)
                }

                val str = action.str
                val textColor = textPaint.color
                if (showLimitNumberColor && str.isDigitsOnly()) {
                    if (limitNumber > 0 && str.toInt() >= limitNumber) {
                        textPaint.color = meterNumberLimitTextColor
                    }
                }
                canvas1.drawText(
                    str,
                    (strCX - action.textWidth / 2).toFloat(),
                    (strCY + action.textHeight / 2).toFloat(),
                    textPaint
                )
                textPaint.color = textColor
//                                canvas1.drawCircle(
//                                    strCX.toFloat(),
//                                    strCY.toFloat(),
//                                    dp2Px(2F),
//                                    dashCirclePaint!!
//                                )
            }
        }
    }

    override fun onDetachedFromWindow() {
        release()
        super.onDetachedFromWindow()
    }

    /**
     * 释放动画
     */
    fun release() {
        try {
            for (i in needRecyclerBitmapList) {
                i.recycle()
            }
            needRecyclerBitmapList.clear()
            valueAnimator?.pause()
            valueAnimator?.cancel()
            alphaAnimator?.pause()
            alphaAnimator?.cancel()
        } catch (e: Exception) {
        }
    }

}

data class MeterNumber(val str: String, val textWidth: Float, val textHeight: Float)

fun View.dp2Px(dip: Float): Float {
    return this.resources.dp2Px(dip)
}

fun Context.dp2Px(dip: Float): Float {
    return this.resources.dp2Px(dip)
}

fun Resources.dp2Px(dip: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, this.displayMetrics)
}

fun TextPaint.getTextBound(str: String): Rect {
    val rect = Rect()
    getTextBounds(str, 0, str.length, rect)
    return rect
}

fun TextPaint.getCapHeight(): Int {
    // 获得数字高度
    return getTextBound("1234567890").height()
}