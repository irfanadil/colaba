package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.rnsoft.colabademo.databinding.GovtQuestionTabLayoutBinding


private val govtTabArray = arrayOf(
        "",
        "",
        "",
        "",
        ""
)

@AndroidEntryPoint
class GovtQuestionsTabFragment : GovtQuestionBaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var _binding: GovtQuestionTabLayoutBinding? = null
    private val binding get() = _binding!!
    private var selectedPosition:Int = 0
    private lateinit var pageAdapter:GovtQuestionPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout:TabLayout
    private val borrowerApplicationViewModel: BorrowerApplicationViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = GovtQuestionTabLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        borrowerApplicationViewModel.governmentQuestionsModelClassList.observe(
            viewLifecycleOwner,
            Observer { observableSampleContent ->
                var index =0
                val tabIds:ArrayList<Int> = arrayListOf()
                for(tab in observableSampleContent) {
                    tab.passedBorrowerId?.let {
                        tabIds.add(it)
                        tab.questionData?.get(0)?.firstName?.let { borrowerName ->
                            govtTabArray[index] = borrowerName
                            index++
                        }
                    }
                }

                viewPager = binding.assetViewPager
                tabLayout = binding.bTabLayout
                pageAdapter = GovtQuestionPagerAdapter(requireActivity().supportFragmentManager, lifecycle , tabIds )
                viewPager.adapter = pageAdapter

                viewPager.isUserInputEnabled = false
                viewPager.setPageTransformer(null)
                viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                    }
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        Log.e("Selected_Page", position.toString())
                        selectedPosition = position
                    }
                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                    }
                })

                tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        tab?.let {
                            viewPager.adapter
                            viewPager.currentItem
                        }
                    }
                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })
                TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = govtTabArray[position] }.attach()
        })

        binding.backButton.setOnClickListener {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }

        super.addListeners(binding.root)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

