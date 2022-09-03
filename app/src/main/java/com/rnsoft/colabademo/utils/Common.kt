package com.rnsoft.colabademo.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.rnsoft.colabademo.R
import java.text.DecimalFormat

/**
 * Created by Anita Kiran on 10/28/2021.
 */
class Common {
    companion object{

         fun addNumberFormat(value: Double) : String {
            val convertedString: String = DecimalFormat("#,###,###").format(value)
            return convertedString
        }

        fun removeCommas(value:String):String{
            return value.replace(",","")
        }

        fun ImageView.loadImage(uri: String?) {
            val options = RequestOptions()
                .placeholder(R.drawable.email_vector)
                .circleCrop()
                .error(R.drawable.image3)
            Glide.with(this.context)
                .setDefaultRequestOptions(options)
                .load(uri)
                .into(this)
        }

        private val gson = Gson()

        fun isJSONValid(jsonInString: String?): Boolean {
            return try {
                gson.fromJson(jsonInString, Any::class.java)
                true
            } catch (ex: JsonSyntaxException) {
                false
            }
        }

    }





}