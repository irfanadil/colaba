package com.rnsoft.colabademo

import android.view.View


data class DocTypeModelClass(
    val headerTitle: String = "Header Title",
    val totalSelected: String = "",
    val footerTitle: String  = "",
    val contentCell:ArrayList<DocTypeContentCell>,
    val contentListenerAttached:View.OnClickListener= View.OnClickListener {  }
    )

data class DocTypeContentCell(val checkboxContent:String="Title", val description:String="",  val isChecked:Boolean=false)

