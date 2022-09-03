package com.rnsoft.colabademo.utils

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rnsoft.colabademo.R

/**
 * Created by Anita Kiran on 9/3/2021.
 */


class MaterialTextviewFocusListener(private val mTextView: MaterialAutoCompleteTextView, private val mTextInputLayout: TextInputLayout, private val context: Context):
    View.OnFocusChangeListener {
    override fun onFocusChange(p0: View?, p1: Boolean) {
        if (p1) {
            setTextInputLayoutHintColor(mTextInputLayout, context = context, R.color.grey_color_two)
        } else {
            if (mTextView.text?.length == 0) {
                setTextInputLayoutHintColor(
                    mTextInputLayout,
                    context = context,
                    R.color.grey_color_three
                )
            } else {
                setTextInputLayoutHintColor(
                    mTextInputLayout,
                    context = context,
                    R.color.grey_color_two
                )
            }
        }
    }

    private fun setTextInputLayoutHintColor(
        textInputLayout: TextInputLayout,
        context: Context,
        @ColorRes colorIdRes: Int
    ) {
        textInputLayout.defaultHintTextColor =
            ColorStateList.valueOf(ContextCompat.getColor(context, colorIdRes))
    }

}