package com.rnsoft.colabademo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.HashMap

private const val NUM_TABS = 2

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object{
        //var currentlyOpenedFragment:LoanFilterInterface? = null
        val hashMap = HashMap<Int, Fragment>()

        fun initialize(){
            val fragment = AllLoansFragment()
            hashMap.put(0, fragment)

            val fragment2 = ActiveLoansFragment()
            hashMap.put(1, fragment2)

            val fragment3 = NonActiveLoansFragment()
            hashMap.put(2, fragment3)
        }
    }




    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {

        when (position) {

            0 -> {

                //currentlyOpenedFragment = fragment // You never know when this block will be executed.
                return hashMap.getValue(0)
            }
            1 -> {

                //currentlyOpenedFragment = fragment  // You never know when this block will be executed.
                return hashMap.getValue(1)
            }

            2 -> {
                //val fragment = NonActiveLoansFragment()
                //hashMap.put(2, fragment)
                //currentlyOpenedFragment = fragment  // You never know when this block will be executed.
                return hashMap.getValue(2)
            }
        }


        return BaseFragment()

    }
}