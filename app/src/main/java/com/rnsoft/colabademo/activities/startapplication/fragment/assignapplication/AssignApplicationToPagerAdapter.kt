package com.rnsoft.colabademo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class AssignApplicationToPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object{
        const val ASSIGN_APP_TABS = 4 // this number will decide, how many tabs and fragments will be displayed...
    }

    override fun getItemCount(): Int {
        return ASSIGN_APP_TABS
    }

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> {
                return AssignLoanOfficerFragment()
            }
            1 -> {
                return AssignLoanCoordinatorFragment()
            }

            2 -> {
                return AssignPreProcessorFragment()
            }

            3 -> {
                return AssignLoanProcessorFragment()
            }
        }


        return BaseFragment()

    }
}