package com.rnsoft.colabademo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class GovtQuestionPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle , private val tabIds:ArrayList<Int>) :
        FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return tabIds.size
    }

    override fun createFragment(position: Int): Fragment {
        val borrowerOneQuestions = BorrowerOneQuestions()
        val args = Bundle()
        args.putInt(AppConstant.tabBorrowerId, tabIds[position])
        borrowerOneQuestions.arguments = args
        return borrowerOneQuestions
    }



    /*

     override fun getItemCount(): Int {
        return GOVT_QUESTIONS_TABS
     }

     override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> {
                return BorrowerOneQuestions()
            }
            1 -> {
                return BorrowerTwoQuestions()
            }

            2 -> {
                return BaseFragment()
            }

            3 -> {
                return BaseFragment()
            }
        }


        return BaseFragment()

    }


     */
}