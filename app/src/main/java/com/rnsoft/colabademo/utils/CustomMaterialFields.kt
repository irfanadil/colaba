package com.rnsoft.colabademo.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rnsoft.colabademo.R

class CustomMaterialFields() {
    companion object {

        fun setColor(mTextInputLayout:TextInputLayout, color:Int, context: Context) {
            mTextInputLayout.defaultHintTextColor = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context, color
                )
            )
        }

        fun setDollarPrefix(mTextInputLayout:TextInputLayout, context: Context){

            val text = SpannableString("$").apply {
                setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey_color_two)), 0, length, 0) }

            val text2 = SpannableString("  |  ").apply {
                setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colaba_app_border_color)), 0, length, 0) }

            val spannable: Spannable = SpannableStringBuilder().apply {
                append(text, StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(text2, StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            mTextInputLayout.prefixText= spannable
        }

        fun setPercentagePrefix(mTextInputLayout:TextInputLayout, context: Context){

            val text = SpannableString("%").apply {
                setSpan( ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey_color_two)), 0, length, 0)
            }

            val text2 = SpannableString("  |  ").apply {
                setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.colaba_app_border_color)), 0, length, 0)
            }

            val spannable: Spannable = SpannableStringBuilder().apply {
                append(text ,StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                append(text2,StyleSpan(Typeface.BOLD), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE )
            }
            mTextInputLayout.prefixText= spannable
        }


        fun setError(textInputlayout: TextInputLayout, errorMsg: String, context: Context) {
            textInputlayout.helperText = errorMsg
            textInputlayout.setBoxStrokeColorStateList(
                AppCompatResources.getColorStateList(context, R.color.primary_info_stroke_error_color))
        }

        fun clearError(textInputlayout: TextInputLayout, context: Context) {
            textInputlayout.helperText = ""
            textInputlayout.setBoxStrokeColorStateList(AppCompatResources.getColorStateList(
                    context,
                    R.color.primary_info_line_color))

        }

        fun onTextChangedLableColor(context: Context,mEditText: TextInputEditText, textInputLayout: TextInputLayout){
            mEditText.doAfterTextChanged {
                if (mEditText.text.toString().isNotEmpty()) {
                    textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey_color_two))
                }
            }
        }


    }
}