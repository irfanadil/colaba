package com.rnsoft.colabademo

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rnsoft.colabademo.utils.CustomMaterialFields
import timber.log.Timber

class CustomFocusListenerForEditText(private val mEditText: TextInputEditText, private val mTextInputLayout: TextInputLayout, private val context: Context, private val errorString:String =""):View.OnFocusChangeListener {
    override fun onFocusChange(p0: View?, p1: Boolean) {
        if(p1) {
             setTextInputLayoutHintColor(mTextInputLayout, context = context, R.color.grey_color_two)
        }
        else{
            if (mEditText.text?.length == 0) {
                if(errorString.isNotEmpty())
                    CustomMaterialFields.setError(mTextInputLayout, errorString, context)
                //else
                setTextInputLayoutHintColor(mTextInputLayout, context = context, R.color.grey_color_three )
            }
            else {
                CustomMaterialFields.clearError(mTextInputLayout, context)
                setTextInputLayoutHintColor(mTextInputLayout, context = context, R.color.grey_color_two)
            }
        }
    }

    private fun setTextInputLayoutHintColor(textInputLayout: TextInputLayout, context: Context, @ColorRes colorIdRes: Int) {
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, colorIdRes))
    }
}


class FocusListenerForPhoneNumber(private val mEditText: TextInputEditText, private val mTextInputLayout: TextInputLayout, private val context: Context, private val errorString:String =""):View.OnFocusChangeListener {
    override fun onFocusChange(p0: View?, p1: Boolean) {
        if(p1) {
            setTextInputLayoutHintColor(mTextInputLayout, context = context, R.color.grey_color_two)
        }
        else{
            if (mEditText.text?.length == 0) {
                setTextInputLayoutHintColor(mTextInputLayout, context = context, R.color.grey_color_three )
                    CustomMaterialFields.setError(mTextInputLayout, context.getString(R.string.invalid_phone_num), context)


            } else {
                setTextInputLayoutHintColor(mTextInputLayout, context = context, R.color.grey_color_two )
                if (mEditText.text?.length!! < 14) {
                    CustomMaterialFields.setError(mTextInputLayout, context.getString(R.string.invalid_phone_num), context)
                } else {
                    CustomMaterialFields.clearError(mTextInputLayout , context)
                }
            }

        }
    }

    private fun setTextInputLayoutHintColor(textInputLayout: TextInputLayout, context: Context, @ColorRes colorIdRes: Int) {
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, colorIdRes))
    }
}




class CustomFocusListenerForAutoCompleteTextView(private val materialAutoCompleteTextView: MaterialAutoCompleteTextView, private val mTextInputLayout: TextInputLayout, private val context: Context, private val errorString:String =""):View.OnFocusChangeListener {
    override fun onFocusChange(p0: View?, p1: Boolean) {
        if(p1) {
            setTextInputLayoutHintColor(mTextInputLayout, context = context, R.color.grey_color_two )
        }
        else{
            if (materialAutoCompleteTextView.text?.length == 0) {
                if(errorString.isNotEmpty())
                    CustomMaterialFields.setError(mTextInputLayout, errorString, context)
                else
                    setTextInputLayoutHintColor(mTextInputLayout, context = context, R.color.grey_color_three )
            }
            else {
                CustomMaterialFields.clearError(mTextInputLayout , context)
                setTextInputLayoutHintColor(mTextInputLayout, context = context, R.color.grey_color_two )
            }
        }
    }

    private fun setTextInputLayoutHintColor(textInputLayout: TextInputLayout, context: Context, @ColorRes colorIdRes: Int) {
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, colorIdRes))
    }
}