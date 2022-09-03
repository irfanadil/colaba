package com.rnsoft.colabademo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rnsoft.colabademo.databinding.AssignBorrowerModalBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

private val bottomBorrowerTabArray = arrayOf(
    "Loan Officer",
    "Loan Coordinator",
    "Pre Processor",
    "Loan Processor"
)

@AndroidEntryPoint
class AssignBorrowerBottomDialogFragment : BottomSheetDialogFragment() {

    lateinit var binding: AssignBorrowerModalBottomSheetBinding
    private var selectedPosition:Int = 0
    private lateinit var pageAdapter:BottomBorrowerPagerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout


    companion object {
         lateinit var startNewApplicationFragment: StartNewApplicationFragment

        fun newInstance(topFragmentLoan: StartNewApplicationFragment): AssignBorrowerBottomDialogFragment {
            startNewApplicationFragment    =   topFragmentLoan
            return AssignBorrowerBottomDialogFragment()
        }

        //fun newInstance() = CustomFilterBottomSheetDialogFragment()
    }


    private fun setInitialSelection(){
        viewPager = binding.bottomBorrowerViewPager
        tabLayout = binding.bottomBorrowerTabLayout
        pageAdapter = BottomBorrowerPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        viewPager.adapter = pageAdapter

        //val governmentQuestionActivity = (activity as? GovtQuestionActivity)
        //governmentQuestionActivity?.let { governmentQuestionActivity-> }
        //viewPager.isUserInputEnabled = false
        //viewPager.setPageTransformer(null)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
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
        TabLayoutMediator(tabLayout, viewPager) { tab, position -> tab.text = bottomBorrowerTabArray[position] }.attach()

        binding.searchEditTextField.clearFocus()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = AssignBorrowerModalBottomSheetBinding.inflate(inflater, container, false)
        //binding.crossImageView.setOnClickListener{ dismiss() }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.roundedBottomSheetDialog)

        binding.searchIconImageView.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.navigateToAssignApplicationToFragment)

        }

        binding.searchEditTextField.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.searchEditTextField.clearFocus()
                binding.searchEditTextField.hideKeyboard()
                val searchWord = binding.searchEditTextField.text.toString()
                if(searchWord.isNotEmpty() && searchWord.isNotBlank())
                    performSearch()
                return@OnEditorActionListener true
            }
            false
        })

        setInitialSelection()

        return binding.root
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun performSearch(){
        navigateToAssignBorrowerScreen()
    }

    private fun navigateToAssignBorrowerScreen(){
        dismiss()
        findNavController().navigate(R.id.navigateToAssignApplicationToFragment)
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.roundedBottomSheetDialog)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()
    }

    private fun initDialog() {
        requireDialog().window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireDialog().window?.statusBarColor = requireContext().getColor(android.R.color.transparent)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDismissEvent(evt: OnDismissBottomDialogEvent) {
        navigateToAssignBorrowerScreen()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun dismissBottomBoxEvent(evt:DismissBoxEvent){
        dismiss()
    }
}