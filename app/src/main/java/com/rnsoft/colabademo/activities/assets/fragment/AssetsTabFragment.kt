package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.os.HandlerCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.rnsoft.colabademo.databinding.AssetsTabLayoutBinding
import kotlinx.android.synthetic.main.assets_middle_cell.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


private val assetsTabArray = arrayOf(
    "",
    "",
    "",
    "",
    ""
)

@AndroidEntryPoint
class AssetsTabFragment : BaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var _binding: AssetsTabLayoutBinding? = null
    private val binding get() = _binding!!
    private var selectedPosition:Int = 0
    private lateinit var pageAdapter:AssetsPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout:TabLayout

    private  var borrowerIdToNavigate = -1

    private val borrowerApplicationViewModel: BorrowerApplicationViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = AssetsTabLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val assetsActivity = (activity as? AssetsActivity)
        assetsActivity?.let { assetsActivity->
            //assetsActivity.borrowerTabList?.let { borrowerTabList-> }
        }



        borrowerApplicationViewModel.assetsModelDataClass.observe(
            viewLifecycleOwner,
            Observer { observableSampleContent ->

                borrowerIdToNavigate = -1

               if(observableSampleContent.size>0){
                   for (tab in observableSampleContent) {
                       borrowerIdToNavigate = tab.updateBorrowerId
                   }
                   //for (tab in observableSampleContent) { tab.updateBorrowerId = -1 }
               }





                    var index = 0
                    val tabIds: ArrayList<Int> = arrayListOf()
                    for (tab in observableSampleContent) {
                        tab.passedBorrowerId?.let {

                            tabIds.add(it)
                            tab.bAssetData?.borrower?.borrowerName?.let { borrowerName ->
                                assetsTabArray[index] = borrowerName
                                index++
                            }
                        }
                    }

                    binding.viewpagerLine.visibility = View.VISIBLE
                    viewPager = binding.assetViewPager
                    tabLayout = binding.assetTabLayout
                    Timber.e("tabIds in AssetTab", tabIds.size)
                    pageAdapter = AssetsPagerAdapter(requireActivity().supportFragmentManager, lifecycle, tabIds)
                    viewPager.adapter = pageAdapter
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
                    TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = assetsTabArray[position] }.attach()


                if(tabIds.size>0) {
                   if(borrowerIdToNavigate>0) {
                       viewPager.currentItem = tabIds.indexOf(borrowerIdToNavigate)
                       binding.viewpagerLine.postDelayed({
                           for (tab in observableSampleContent) {
                               tab.updateBorrowerId = -1
                               tab.visibleCategoryName = "tingPing"
                           }
                       },1000)
                   }
                }


            })

        binding.backButton.setOnClickListener {
            requireActivity().finish()
            EventBus.getDefault().postSticky(BorrowerApplicationUpdatedEvent(objectUpdated = true))
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }



        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()
            EventBus.getDefault().postSticky(BorrowerApplicationUpdatedEvent(objectUpdated = true))
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }

        super.addListeners(binding.root)

        return root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Timber.e("onViewCreated Asset Tab fragment the screen...")

        val navController = findNavController()
        // We use a String here, but any type that can be put in a Bundle is supported

        val params = navController.currentBackStackEntry?.savedStateHandle?.get<AssetReturnParams>(AppConstant.assetReturnParams)
        if (params != null) {
            if (params.assetName.isNullOrBlank() || params.assetName.isNullOrEmpty())
                Timber.e("something null...")
            else
                Timber.e(params.assetName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGrandTotalAmountReceived(event: GrandTotalEvent) {
        binding.grandTotalTextView.text = event.totalAmount
    }

     */

    /*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        // We use a String here, but any type that can be put in a Bundle is supported

        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<AssetReturnParams>(AppConstant.assetReturnParams)?.observe(viewLifecycleOwner) { returnParams ->

            if (returnParams.assetName.isNullOrBlank() || returnParams.assetName.isNullOrEmpty())
               Timber.e("something null...")
            else
                Timber.e(returnParams.assetName)
            //savedContentCell.content_desc.text = returnParams.assetTypeName
            returnParams.assetValue?.let { assetValue ->
                // totalAmount += assetValue
                //contentCell.content_amount.text = "$".plus(Common.addNumberFormat(assetValue))
            }
        }
    }

     */


}

