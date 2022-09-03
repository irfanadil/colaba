package com.rnsoft.colabademo

interface DocsViewClickListener {
    //fun onItemClick(testLayout: ConstraintLayout)
    fun getCardIndex(position: Int)
    fun navigateTo(position: Int, docName:String)
}

