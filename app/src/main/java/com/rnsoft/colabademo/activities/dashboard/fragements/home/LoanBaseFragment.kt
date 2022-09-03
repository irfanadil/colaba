package com.rnsoft.colabademo

import androidx.fragment.app.activityViewModels

open class LoanBaseFragment: BaseFragment() {

    companion object {
        var globalAssignToMe: Boolean = false
        var globalOrderBy:Int = 1
    }

    protected val loanViewModel: LoanViewModel by activityViewModels()

    //open fun setOrderId(orderBy: Int) {}
    //open fun setAssignToMe(assignToMe:Boolean) {}
}
