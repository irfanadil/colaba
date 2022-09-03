package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rnsoft.colabademo.databinding.DetailTabLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private val detailTabArray = arrayOf(
    "Overview",
    "Application",
    "Documents"
)


@AndroidEntryPoint
class DetailTabFragment : BaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var _binding: DetailTabLayoutBinding? = null
    private val binding get() = _binding!!
    private var selectedPosition:Int = 0
    private lateinit var pageAdapter:DetailPagerAdapter

    private val detailViewModel: DetailViewModel by activityViewModels()

    private lateinit var viewPager: ViewPager2

    private lateinit var tabLayout:TabLayout

    private var displayBadge = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = DetailTabLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewPager = binding.detailViewPager
        tabLayout = binding.detailTabLayout
        pageAdapter = DetailPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        viewPager.adapter = pageAdapter


        // navigate to the relevant fragment screen....
        val detailActivity = (activity as? DetailActivity)
        detailActivity?.let { detailActivity->
            detailActivity.innerScreenName?.let {
                viewPager.visibility = View.INVISIBLE
                //Log.e("tabFrag = ", it)
                if (it == AppConstant.borrowerAppScreen)
                    viewPager.currentItem = 1
                if (it == AppConstant.borrowerDocScreen)
                    viewPager.currentItem = 2
            }
            detailActivity.innerScreenName = null
        }

        viewPager.setPageTransformer(null)

        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
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

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewPager.adapter
                    viewPager.currentItem
                 }
                binding.mAppbar.setExpanded(true,true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.backButton.setOnClickListener{
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }

        loadDetailWebservices()

        if(viewPager.visibility == View.INVISIBLE)
            viewPager.postDelayed({
                viewPager.visibility = View.VISIBLE
                TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = detailTabArray[position] }.attach()
            }, 600)
        else
            TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = detailTabArray[position] }.attach()

        observeFileReadChanges()
        super.addListeners(binding.root)
        return root
    }


    private fun observeFileReadChanges(){
        detailViewModel.borrowerDocsModelList.observe(viewLifecycleOwner, { docsArrayList ->
            for (doc in docsArrayList) {
                doc.subFiles?.let { subFiles->
                    if (subFiles.size > 0) {
                        for (subFile in subFiles) {
                            subFile.isRead?.let {
                                if (!it)
                                    displayBadge = true
                            }

                        }
                    }
                }
            }
            addBadgeForUnreadFile()
        })
    }

    private fun loadDetailWebservices(){
        lifecycleScope.launchWhenStarted {
            val detailActivity = (activity as? DetailActivity)
            detailActivity?.let {
                fillHeaderValues()
                val testLoanId = it.loanApplicationId
                testLoanId?.let { loanId->
                    sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                        detailViewModel.getBorrowerOverview(token = authToken, loanApplicationId = loanId)
                        detailViewModel.getBorrowerDocuments(token = authToken, loanApplicationId = loanId)
                        detailViewModel.getBorrowerApplicationTabData(token = authToken, loanApplicationId = loanId)
                    }
                }
            }
        }

        observeInSearchCase()
    }

    private fun observeInSearchCase(){
        detailViewModel.borrowerOverviewModel.observe(viewLifecycleOwner, {  overviewModel->
            if(overviewModel!=null) {
                binding.borrowerPurpose.text = overviewModel.loanPurpose
            }
            else
                Log.e("should-stop"," here....")

        })
    }

    private fun fillHeaderValues(){
        val getParentActivity =  activity as DetailActivity
        val borrowerCompleteName = getParentActivity.borrowerFirstName + " "+getParentActivity.borrowerLastName
        //var greetingString = AppSetting.returnGreetingString()
        //greetingString = "$greetingString, $borrowerCompleteName"
        binding.borrowerNameGreeting.text = borrowerCompleteName
        binding.borrowerPurpose.text = getParentActivity.borrowerLoanPurpose


    }

    override fun onResume() {
        super.onResume()

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun addBadgeForUnreadFile(){
        val testBadge = tabLayout.getTabAt(2)?.orCreateBadge
        testBadge?.let {
            if (displayBadge) {
                testBadge.isVisible = true
                //testBadge.number = 1
                testBadge.verticalOffset = -16
                testBadge.backgroundColor = getColor(requireContext(), R.color.colaba_red_color)
                //testBadge.badgeTextColor = getColor(requireContext(), R.color.white)
            }
            else
                testBadge.isVisible = false
        }
    }


}

