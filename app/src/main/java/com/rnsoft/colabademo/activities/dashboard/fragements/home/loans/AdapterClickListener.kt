package com.rnsoft.colabademo

interface AdapterClickListener {
    //fun onItemClick(testLayout: ConstraintLayout)
    fun getSingleItemIndex(position: Int)
    fun navigateTo(position: Int)
}

