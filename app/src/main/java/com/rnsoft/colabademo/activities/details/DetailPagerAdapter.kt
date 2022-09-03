package com.rnsoft.colabademo

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val NUM_TABS = 3

class DetailPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> {
                val fragment = BorrowerOverviewFragment()
                return fragment
            }
            1 -> {
                val fragment = BorrowerApplicationFragment()
                return fragment
            }

            2 -> {
                val fragment = BorrowerDocumentFragment()
                return fragment
            }
        }


        return BaseFragment()

    }
}