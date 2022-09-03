package com.rnsoft.colabademo

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView


/**
 * Created by Anita Kiran on 10/8/2021.
 */


class CustomScrollView : ScrollView {
    private var enableScrolling = true
    fun isEnableScrolling(): Boolean {
        return enableScrolling
    }

    fun setEnableScrolling(enableScrolling: Boolean) {
        this.enableScrolling = enableScrolling
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?) : super(context) {}

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return if (isEnableScrolling()) {
            super.onInterceptTouchEvent(ev)
        } else {
            false
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return if (isEnableScrolling()) {
            super.onTouchEvent(ev)
        } else {
            false
        }
    }
}