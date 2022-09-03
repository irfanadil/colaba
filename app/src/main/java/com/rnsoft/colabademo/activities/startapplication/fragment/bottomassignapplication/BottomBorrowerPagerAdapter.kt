package com.rnsoft.colabademo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class BottomBorrowerPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object{
        const val BOTTOM_BORROWER_TABS = 4 // this number will decide, how many tabs and fragments will be displayed...
    }

    override fun getItemCount(): Int {
        return BOTTOM_BORROWER_TABS
    }

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> {
                return BottomLoanOfficer()
            }
            1 -> {
                return BottomLoanCoordinator()
            }

            2 -> {
                return BottomPreProcessor()
            }

            3 -> {
                return BottomLoanProcessor()
            }
        }


        return BaseFragment()

    }
}