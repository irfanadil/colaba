package com.rnsoft.colabademo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import android.os.Bundle

class AssetsPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val tabIds:ArrayList<Int>) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return tabIds.size
    }

    override fun createFragment(position: Int): Fragment {
        val borrowerOneAssets = BorrowerOneAssets()
        val args = Bundle()
        args.putInt(AppConstant.tabBorrowerId, tabIds[position])
        borrowerOneAssets.arguments = args
        return borrowerOneAssets
    }

}