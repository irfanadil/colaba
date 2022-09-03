package com.rnsoft.colabademo

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.rnsoft.colabademo.databinding.IncomeTabLayoutBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList


private val incomeTabArray = arrayOf(
    "",
    "",
    "",
    "",
    ""
)

@AndroidEntryPoint
class IncomeTabFragment : BaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var _binding: IncomeTabLayoutBinding? = null
    private val binding get() = _binding!!
    private var selectedPosition:Int = 0
    private lateinit var pageAdapter: IncomePagerAdapter
    //private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout:TabLayout
    private var savedViewInstance: View? = null
    private lateinit var viewPager: ViewPager2

    companion object{
        var isStartIncomeTab : Boolean = true
    }

    private val borrowerApplicationViewModel: BorrowerApplicationViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    : View {
        _binding = IncomeTabLayoutBinding.inflate(inflater, container, false)
           val root: View = binding.root

              // if(isStartIncomeTab) {
        borrowerApplicationViewModel.incomeTabDetails.observe(
            viewLifecycleOwner,
            Observer { observableSampleContent ->
                var index = 0
                var name: String = ""
                val tabIds: ArrayList<Int> = arrayListOf()
                for (tab in observableSampleContent) {
                    tab.passedBorrowerId?.let {
                        tabIds.add(it)
                        tab.incomeData?.borrower?.borrowerName?.let { borrowerName ->
                            incomeTabArray[index] = borrowerName
                            name = borrowerName
                            index++
                        }
                    }
                }

                binding.viewpagerLine.visibility = View.VISIBLE
                viewPager = binding.assetViewPager
                tabLayout = binding.assetTabLayout
                pageAdapter = IncomePagerAdapter(
                    requireActivity().supportFragmentManager,
                    lifecycle,
                    tabIds,
                    name
                )
                viewPager.adapter = pageAdapter
                viewPager.setPageTransformer(null)
                viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                        super.onPageScrolled(position, positionOffset,
                            positionOffsetPixels)
                    }

                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        //Log.e("Selected_Page", position.toString())
                        selectedPosition = position
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                    }
                })

                tabLayout.addOnTabSelectedListener(object :
                    TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.let {
                            viewPager.adapter
                            viewPager.currentItem
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = incomeTabArray[position]
                }.attach()
            })




        binding.backButton.setOnClickListener {
                EventBus.getDefault()
                    .postSticky(BorrowerApplicationUpdatedEvent(objectUpdated = true))
                requireActivity().finish()
                requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
            }

            requireActivity().onBackPressedDispatcher.addCallback {
                EventBus.getDefault().postSticky(BorrowerApplicationUpdatedEvent(objectUpdated = true))
                requireActivity().finish()
                requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
            }

            super.addListeners(binding.root)
            return root
          //  savedViewInstance

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isStartIncomeTab = true
    }


    override fun onResume() {
        super.onResume()
        //Log.e("IncomeTabFragment","OnResume")

    }
/*
    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGrandTotalAmountReceived(event: GrandTotalEvent) {
        binding.grandTotalTextView.text = event.totalAmount
    } */

}

