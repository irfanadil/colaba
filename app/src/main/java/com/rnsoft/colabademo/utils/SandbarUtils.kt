package com.rnsoft.colabademo

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout


object SandbarUtils {
    fun showRegular(activity: Activity, textMessage: String , layout_id:Int = R.layout.toast_notify_layout) {
        show(activity, textMessage, layout_id)
    }

    fun showSuccess(activity: Activity, textMessage: String , layout_id:Int = R.layout.toast_ok_layout) {
        show(activity, textMessage, layout_id)
    }

    fun showError(activity: Activity, textMessage: String , layout_id:Int = R.layout.toast_error_layout ) {
        show(activity, textMessage, layout_id)
    }

    //fun showWarning(activity: Activity, textMessage: String) {
      //  show(activity, textMessage, Color.argb(255, 255, 165, 0))
    //}

    private fun show(
        activity: Activity,
        textMessage: String,
        layout_id: Int = R.layout.toast_notify_layout
    ) {
        SimpleCustomSandbar.make(activity, textMessage, layout_id)?.show()
    }
}

class SimpleCustomSandbar(parent: ViewGroup, content: View) :
    BaseTransientBottomBar<SimpleCustomSandbar>(
        parent,
        content,
        object : com.google.android.material.snackbar.ContentViewCallback {
            override fun animateContentIn(delay: Int, duration: Int) {}
            override fun animateContentOut(delay: Int, duration: Int) {}
        }) {

    init {


        getView().apply {
            setBackgroundColor(ContextCompat.getColor(view.context, android.R.color.transparent))
            setPadding(0, 0, 0, 0)


        }
    }

    companion object {
        private fun findSuitableParent(view: View?): ViewGroup? {
            if (view == null) {
                return null
            }

            var viewInProcess = view
            val viewGroup = viewInProcess
            var fallback: ViewGroup? = null
            do {
                if (viewInProcess is CoordinatorLayout) {
                    return viewGroup as ViewGroup
                } else if (viewInProcess is FrameLayout) {
                    if (viewGroup.id == R.id.content) {
                        return viewInProcess
                    }
                    fallback = viewGroup as ViewGroup?
                }

                if (viewInProcess != null) {
                    val parent = viewInProcess.parent
                    viewInProcess = if (parent is View) parent as ViewGroup else null
                }
            } while (viewInProcess != null)
            return fallback
        }

        fun make(
            activity: Activity,
            message: String,
            layout_id: Int = R.layout.toast_notify_layout,
            duration: Int = LENGTH_LONG
        ): SimpleCustomSandbar? {
            val parent =
                findSuitableParent(activity.findViewById(android.R.id.content)) ?: return null

            try {
                val toastView: View = LayoutInflater.from(activity)
                    .inflate(layout_id, parent, false)
                (toastView.findViewById<View>(android.R.id.message) as TextView).text = message
                //toastView.setBackgroundColor(backgroundColor)
                val snackbar = SimpleCustomSandbar(
                    parent,
                    toastView
                ).setDuration(duration)



                //val sbView: View = snackbar.getView()
                /*
                val sbView = snackbar.getView() as FrameLayout
                val params = sbView.getChildAt(0).layoutParams
                params.setMargins(
                    params.leftMargin,
                    params.topMargin,
                    params.rightMargin,
                    params.bottomMargin + ScreenUtils.getNavigationBarHeight(activity)
                )

                sbView.getChildAt(0).setLayoutParams(params)

                val layout = snackbar.getView() as SnackbarLayout

                sbView.minimumHeight = 400
                  */


                setCorrectAnimationAndPosition(snackbar)
                return snackbar
            } catch (e: Exception) {
                println(e.message)
            }
            return null
        }

        private fun setCorrectAnimationAndPosition(sandbar: SimpleCustomSandbar) {
            val params = sandbar.view.layoutParams
            if (params is CoordinatorLayout.LayoutParams) {
                params.gravity = Gravity.BOTTOM
            } else if (params is FrameLayout.LayoutParams) {
                params.gravity = Gravity.BOTTOM
            }

            sandbar.view.layoutParams = params

            sandbar.animationMode = ANIMATION_MODE_FADE
        }
    }
}