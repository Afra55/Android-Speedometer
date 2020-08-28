package com.afra55.speedometer

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * @author Afra55
 * @date 2020/8/19
 * A smile is the best business card.
 * 没有成绩，连呼吸都是错的。
 */
class SquareFrameByHeightLayout : FrameLayout {

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

    fun init(attrs: AttributeSet?, defStyle: Int) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec)
    }
}