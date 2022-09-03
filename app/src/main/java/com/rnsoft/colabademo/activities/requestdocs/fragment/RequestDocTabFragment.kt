package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rnsoft.colabademo.databinding.RequestDocsTabLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private val docsTypeTabArray = arrayOf(
    "Document Templates",
    "Document List"
)

@AndroidEntryPoint
class RequestDocsTabFragment : BaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var _binding: RequestDocsTabLayoutBinding? = null
    private val binding get() = _binding!!
    private var selectedPosition:Int = 0
    private lateinit var pageAdapter:RequestPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout:TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = RequestDocsTabLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewPager = binding.reqDocsViewPager
        tabLayout = binding.reqDocsTabLayout
        pageAdapter = RequestPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        viewPager.adapter = pageAdapter

        val requestDocsActivity = (activity as? RequestDocsActivity)
        requestDocsActivity?.let { requestDocsActivity-> }

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
        TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = docsTypeTabArray[position] }.attach()

        binding.backButton.setOnClickListener {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_selected_doc_fragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }

        super.addListeners(binding.root)

        loadDocsRequestWebServices()

        return root
    }

    private val requestDocsViewModel: RequestDocsViewModel by activityViewModels()

    private fun loadDocsRequestWebServices(){
        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                requestDocsViewModel.getTemplates(token = authToken)
                requestDocsViewModel.getCategoryDocumentMcu(token = authToken)
                //requestDocsViewModel.getCategoryDocumentMcu(token = authToken)
                //requestDocsViewModel.getEmailTemplates(token = authToken)
            }
        }
        //observeInSearchCase()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}

