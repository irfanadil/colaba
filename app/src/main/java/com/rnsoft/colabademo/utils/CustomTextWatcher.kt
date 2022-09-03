package com.rnsoft.colabademo.utils

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rnsoft.colabademo.R

class CustomTextWatcher(private val mEditText: TextInputEditText, private val mTextInputLayout: TextInputLayout , private val context: Context) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable) {
        if(mEditText.text?.length == 0){
            Log.e("Len", "is zero")
            val states2 = arrayOf(intArrayOf())
            val colors2 = intArrayOf(
                R.color.biometric_error_color
            )
            val myList2 = ColorStateList(states2, colors2)
            //mTextInputLayout.defaultHintTextColor = myList
            //mTextInputLayout.hintTextColor = myList2
            mTextInputLayout.defaultHintTextColor = myList2
            // Sets the text color used by the hint in both the collapsed and expanded states
            //mTextInputLayout.setDefaultHintTextColor(myList2)

        //Sets the collapsed hint text color
            //mTextInputLayout.setHintTextColor(myList2)

            setTextInputLayoutHintColor(mTextInputLayout, context = context,
                R.color.biometric_error_color
            )

        }
        else {
            Log.e("Len", "when not zero")
            //mTextInputLayout.boxStrokeColor = Color.RED

            // Sets the text color used by the hint in both the collapsed and expanded states
            val states = arrayOf(intArrayOf(0))
            val colors = intArrayOf(
                R.color.colaba_primary_color
            )
            val myList = ColorStateList(states, colors)
            mTextInputLayout.hintTextColor = myList
            //mTextInputLayout.defaultHintTextColor = myList

            setTextInputLayoutHintColor(mTextInputLayout, context = context,
                R.color.colaba_primary_color
            )

            //setUpperHintColor(Color.CYAN, mTextInputLayout)
        }

    }


    private fun setTextInputLayoutHintColor(textInputLayout: TextInputLayout, context: Context, @ColorRes colorIdRes: Int) {
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, colorIdRes))
    }


}