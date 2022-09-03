package com.rnsoft.colabademo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class RequestPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object{
        const val DOCS_TYPES_TABS = 2 // this number will decide, how many tabs and fragments will be displayed...
    }

    override fun getItemCount(): Int {
        return DOCS_TYPES_TABS
    }

    override fun createFragment(position: Int): Fragment {

        when (position) {
            0 -> {
                return DocsTemplateFragment()
            }
            1 -> {
                return DocsListFragment()
            }
        }
        return BaseFragment()
    }
}