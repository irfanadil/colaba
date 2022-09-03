package com.rnsoft.colabademo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class IncomePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val tabIds:ArrayList<Int>,private val name :String) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return tabIds.size
    }

    override fun createFragment(position: Int): Fragment {
        val borrowerOneIncome = BorrowerOneIncome()
        val args = Bundle()
        args.putInt(AppConstant.tabBorrowerId, tabIds[position])
        args.putString(AppConstant.borrowerName,name)
        borrowerOneIncome.arguments = args
        return borrowerOneIncome
    }

}