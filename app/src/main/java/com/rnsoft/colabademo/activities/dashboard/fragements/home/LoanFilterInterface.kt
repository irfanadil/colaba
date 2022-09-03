package com.rnsoft.colabademo

interface LoanFilterInterface {
    var testProperty: Int
        get() = 1
        set(value) = TODO()

    fun setOrderId(orderBy: Int)
    fun setAssignToMe(assignToMe:Boolean)
}