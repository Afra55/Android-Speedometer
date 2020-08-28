package com.afra55.speedometer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * @author Afra55
 * @date 2020/8/20
 * A smile is the best business card.
 * 没有成绩，连呼吸都是错的。
 */
class ViewPager2Indicator :View{

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

    var selectedIndicatorWidth = 0
    var indicatorMargin = 0
    var indicatorColor = Color.WHITE
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var itemCount = 0

    var currentSelectPosition = 0
    var myPositionOffset = 0F
    val viewPager2ChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        /**
         * This method will be invoked when the current page is scrolled, either as part
         * of a programmatically initiated smooth scroll or a user initiated touch scroll.
         *
         * @param position Position index of the first page currently being displayed.
         * Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            currentSelectPosition = position
            myPositionOffset = positionOffset
            invalidate()
        }

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.ViewPager2Indicator, defStyle, 0
        )

        selectedIndicatorWidth = a.getDimensionPixelSize(R.styleable.ViewPager2Indicator_indicatorSelectedWidth, dp2Px(10F).toInt())
        indicatorMargin = a.getDimensionPixelSize(R.styleable.ViewPager2Indicator_indicatorMargin, dp2Px(3F).toInt())
        indicatorColor = a.getColor(R.styleable.ViewPager2Indicator_indicatorColor, indicatorColor)

        a.recycle()

        paint.color = indicatorColor
        paint.style = Paint.Style.FILL


    }

    fun attach(viewPager2: ViewPager2) {
        itemCount = viewPager2.adapter?.itemCount ?:0
        if (itemCount > 0) {
            viewPager2.registerOnPageChangeCallback(viewPager2ChangeCallback)
            post {
                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (itemCount > 0 && width > 0 && height > 0) {
            val paddingLeft = paddingLeft
            val paddingTop = paddingTop
            val paddingRight = paddingRight
            val paddingBottom = paddingBottom

            val contentWidth = width - paddingLeft - paddingRight

            // 高度即作为 indicator 的宽度
            val IndicatorWidth = height - paddingTop - paddingBottom

            val cx = paddingLeft + (contentWidth.toFloat()) / 2
            val cy = paddingTop + (IndicatorWidth.toFloat()) / 2



            var startX = cx - ((itemCount - 1) * (indicatorMargin+ IndicatorWidth ) + selectedIndicatorWidth) / 2
            for (i in 0 until itemCount) {
                val realOffset = selectedIndicatorWidth - IndicatorWidth
                var offset =
                if (currentSelectPosition == i){
                    realOffset * (1 - myPositionOffset)
                } else if (currentSelectPosition + 1 == i) {
                    realOffset * myPositionOffset
                } else {
                    0F
                }
                val left = startX
                val top = paddingTop.toFloat()
                val right = startX + IndicatorWidth + offset
                val bottom = paddingTop + IndicatorWidth + 0F
                canvas?.drawRoundRect(left, top, right, bottom, 90F, 90F, paint)
                startX = right + indicatorMargin
            }
        }
    }


}