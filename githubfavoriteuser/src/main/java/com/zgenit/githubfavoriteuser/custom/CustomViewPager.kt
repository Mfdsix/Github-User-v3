package com.zgenit.githubfavoriteuser.custom

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class CustomViewPager : ViewPager {
    /**
     * Constructor
     *
     * @param context the context
     */
    constructor(context: Context) : super(context)

    /**
     * Constructor
     *
     * @param context the context
     * @param attrs the attribute set
     */
    constructor(context: Context, attrs: AttributeSet) : super(
        context,
        attrs
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var mHeightMeasureSpec = heightMeasureSpec
        var height = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val h = child.measuredHeight
            if (h > height) height = h
        }
        if (height != 0) {
            mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)
    }

}