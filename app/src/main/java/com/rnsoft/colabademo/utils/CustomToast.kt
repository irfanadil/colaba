package com.rnsoft.colabademo



import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast


class CustomToast(private val context: Context) {

    private var inflater: LayoutInflater = (context as Activity).layoutInflater

    fun convertToToast(){

        val viewGroup :ViewGroup=
            ((context as Activity).findViewById<View>(R.id.toast_layout_root) as ViewGroup)

        val layout: View = inflater.inflate(R.layout.toast_error_layout, viewGroup)

        val image: ImageView = layout.findViewById(R.id.image) as ImageView
        image.setImageResource(R.drawable.dollar_bag)
        val textField = layout.findViewById(R.id.text) as TextView
        textField.text = "Hello! This is a custom toast!"


        val toast = Toast(context.applicationContext)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_LONG
        toast.setView(layout)
        toast.show()

    }

}

//private fun showToast(toastMessage: String) = Toast.makeText(requireActivity().applicationContext, toastMessage, Toast.LENGTH_LONG).show()