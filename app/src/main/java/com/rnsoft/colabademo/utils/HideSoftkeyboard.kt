package com.rnsoft.colabademo

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by Anita Kiran on 9/9/2021.
 */
object HideSoftkeyboard {

    fun hide(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}