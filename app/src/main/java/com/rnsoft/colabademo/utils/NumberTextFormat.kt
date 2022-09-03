package com.rnsoft.colabademo.utils

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


/**
 * Created by Anita Kiran on 9/6/2021.
 */
class NumberTextFormat (private val mEditText: TextInputEditText) : TextWatcher{

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        try {
            var input = s.toString();
            if(!input.isEmpty()) {
                input = input.replace(",", "")
                val format =  DecimalFormat("#,###,###")
                val newPrice = format.format(input.toDouble())
                mEditText.removeTextChangedListener(this) //To Prevent from Infinite Loop
                mEditText.setText(newPrice);
                mEditText.setSelection(newPrice.length); //Move Cursor to end of String
                mEditText.addTextChangedListener(this)
            }
        } catch (nfe: NumberFormatException) {
            nfe.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun afterTextChanged(s: Editable) {}
}