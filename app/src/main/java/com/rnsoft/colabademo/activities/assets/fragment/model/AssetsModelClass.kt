package com.rnsoft.colabademo

import android.view.View


data class AssetsModelClass(
    val headerTitle: String = "Header Title",
    val headerAmount: String = "$0",
    val footerTitle: String  = "Default Footer Title",
    val contentCell:ArrayList<ContentCell>,
    val listenerAttached:View.OnClickListener= View.OnClickListener {  }
    )

data class ContentCell(val title:String="Title", val description:String="detail", val contentAmount:String="0$")


