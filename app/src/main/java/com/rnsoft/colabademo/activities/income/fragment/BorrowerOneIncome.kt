package com.rnsoft.colabademo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.*
import com.rnsoft.colabademo.utils.Common
import kotlinx.android.synthetic.main.assets_bottom_cell.view.*
import kotlinx.android.synthetic.main.assets_middle_cell.view.content_amount
import kotlinx.android.synthetic.main.assets_middle_cell.view.content_desc
import kotlinx.android.synthetic.main.assets_middle_cell.view.content_title
import kotlinx.android.synthetic.main.assets_top_cell.view.*
import kotlinx.android.synthetic.main.income_middle_cell.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.roundToInt

class BorrowerOneIncome : IncomeBaseFragment() {

    private lateinit var binding: DynamicIncomeFragmentLayoutBinding
    private val viewModel: BorrowerApplicationViewModel by activityViewModels()
    private  var tabBorrowerId:Int? = null
    private var borrowerName : String = ""
    private var grandTotalAmount:Double = 0.0
    val Employment = "EmploymentInfo"
    var showUpdatedIncomeCell : String =""
    var updatedTabId : Int? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DynamicIncomeFragmentLayoutBinding.inflate(inflater, container, false)

        arguments?.let {
            tabBorrowerId = it.getInt(AppConstant.tabBorrowerId)
            borrowerName = it.getString(AppConstant.borrowerName).toString()
        }


        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<IncomeUpdateInfo>(AppConstant.income_update)?.observe(viewLifecycleOwner){ result ->
            showUpdatedIncomeCell = result.incomeUpdateBox
            updatedTabId = result.incomeUpdateTab
            updateSingleTab()
        }

        if(IncomeTabFragment.isStartIncomeTab){
            //Log.e("set up layout", "CALLING FROM HERE 111 ")
            setupLayout()
        }

        return binding.root
    }

    private fun updateSingleTab(){
        //Log.e("Updated Single","tab SUCCESS")
        binding.incomeParentContainer.removeAllViews()

        if(updatedTabId !=null && tabBorrowerId == updatedTabId){
            //Log.e("updatedTabId " + updatedTabId , "BorrowerTabId: " + tabBorrowerId)
           // Log.e("matced","matched")
            updatedTabId = null
            lifecycleScope.launchWhenStarted {
                viewModel.singleIncomeDetail.observe(viewLifecycleOwner, { observableSampleContent ->
                    observableSampleContent?.incomeData?.borrower?.borrowerIncomes?.let { webBorrowerIncome ->
                        var getBorrowerIncome = webBorrowerIncome
                        //Log.e("BorrrowerIncomeSize",""+getBorrowerIncome.size)

                            val sampleIncome = getSampleIncome()
                            for (m in 0 until sampleIncome.size) {
                                val modelData = sampleIncome[m]
                                //Timber.e("header", modelData.headerTitle)
                                //Timber.e("h-amount", modelData.headerAmount)
                                val mainCell: LinearLayoutCompat = layoutInflater.inflate(R.layout.income_main_cell, null) as LinearLayoutCompat
                                val topCell: View = layoutInflater.inflate(R.layout.income_top_cell, null)
                                topCell.header_title.text = modelData.headerTitle
                                topCell.header_amount.setText(modelData.headerAmount)
                                topCell.tag = R.string.asset_top_cell
                                mainCell.addView(topCell)

                                var totalAmount = 0.0
                                for (i in 0 until getBorrowerIncome.size) {
                                    val webModelData = getBorrowerIncome[i]
                                    webModelData.incomeCategory?.let { incomeCategory ->
                                        if (incomeCategory == modelData.headerTitle) {
                                            webModelData.incomes?.let {
                                                for (j in 0 until it.size) {
                                                   // Log.e("list-size", "" + it.size)
                                                    val contentCell: View = layoutInflater.inflate(R.layout.income_middle_cell, null)
                                                    val contentData = webModelData.incomes[j]
                                                    contentCell.content_title.text =
                                                        contentData.incomeName
                                                    contentCell.content_desc.text =
                                                        contentData.jobTitle
                                                    var startDate: String = ""
                                                    var endDate: String = ""
                                                    contentData.startDate?.let {
                                                        startDate = "From ".plus(
                                                            AppSetting.getMonthAndYearValue(it)
                                                        )
                                                        contentCell.tenureTextView.setText(startDate)
                                                    }
                                                    contentData.endDate?.let {
                                                        endDate = AppSetting.getMonthAndYearValue(it)
                                                        contentCell.tenureTextView.setText(startDate.plus(" To " + endDate))
                                                    }

                                                    contentData.incomeValue?.let { incomeValue ->
                                                        totalAmount += incomeValue
                                                        // val formattedValue = Common.addNumberFormat(incomeValue)
                                                        contentCell.content_amount.text = "$".plus(Common.addNumberFormat(incomeValue))
                                                    }
                                                   // Log.e("contentData.incomeTypeDisplayName", "" + contentData.incomeTypeDisplayName)

                                                    if (showUpdatedIncomeCell.length > 0 && modelData.headerTitle == showUpdatedIncomeCell) {
                                                       // Log.e("updating list", showUpdatedIncomeCell + " Tab Id" + tabBorrowerId)
                                                        contentCell.visibility = View.VISIBLE
                                                        topCell.arrow_up.visibility = View.VISIBLE
                                                        topCell.arrow_down.visibility = View.GONE
                                                    } else {
                                                        contentCell.visibility = View.GONE
                                                    }


                                                    val parentActivity = activity as? IncomeActivity
                                                    val bundle = Bundle()
                                                    parentActivity?.let {
                                                        parentActivity.loanApplicationId?.let { it1 ->
                                                            bundle.putInt(AppConstant.loanApplicationId, it1)
                                                        }
                                                        tabBorrowerId?.let { it1 ->
                                                            bundle.putInt(AppConstant.borrowerId, it1)
                                                        }
                                                        contentData.incomeId?.let { it1 ->
                                                            bundle.putInt(AppConstant.incomeId, it1)
                                                        }
                                                        contentData.employmentCategory?.categoryId?.let { it1 ->
                                                            bundle.putInt(
                                                                AppConstant.incomeCategoryId,
                                                                it1
                                                            )
                                                        }
                                                        contentData.incomeTypeId?.let { it1 ->
                                                            bundle.putInt(AppConstant.incomeTypeID, it1)
                                                            borrowerName.let { name ->
                                                                bundle.putString(AppConstant.borrowerName, name)
                                                            }
                                                        }
                                                        //Timber.e(" content data - " + contentData)
                                                    }
                                                    if (contentData.endDate == null && contentData.incomeTypeDisplayName == Employment) {
                                                        contentCell.setOnClickListener { findNavController().navigate(R.id.action_current_employement, bundle)
                                                        }
                                                    } else
                                                        contentCell.setOnClickListener {
                                                            findNavController().navigate(
                                                                modelData.listenerAttached,
                                                                bundle
                                                            )
                                                        }
                                                    mainCell.addView(contentCell)
                                                }
                                            }
                                        }
                                    }
                                }

                                topCell.header_amount.text =
                                    "$".plus(Common.addNumberFormat(totalAmount)).plus("/mo")
                                grandTotalAmount += totalAmount

                                val bottomCell: View =
                                    layoutInflater.inflate(R.layout.income_bottom_cell, null)
                                bottomCell.footer_title.text = modelData.footerTitle
                                bottomCell.visibility = View.GONE
                                showUpdatedIncomeCell?.let { cellName ->
                                    if (modelData.headerTitle.equals(cellName, true))
                                        bottomCell.visibility = View.VISIBLE
                                }
                                if (modelData.footerTitle == AppConstant.footerAddEmployment) {
                                    bottomCell.setOnClickListener(bottomEmploymentListener)
                                } else
                                    bottomCell.setOnClickListener {
                                        val parentActivity = activity as? IncomeActivity
                                        parentActivity?.let {
                                            val bundle = Bundle()
                                            parentActivity.loanApplicationId?.let { it1 ->
                                                bundle.putInt(AppConstant.loanApplicationId, it1)
                                            }
                                            tabBorrowerId?.let { it1 ->
                                                bundle.putInt(AppConstant.borrowerId, it1)
                                            }
                                            borrowerName.let { name ->
                                                bundle.putString(AppConstant.borrowerName, name)
                                            }

                                            findNavController().navigate(modelData.listenerAttached, bundle)
                                        }
                                    }

                                mainCell.addView(bottomCell)
                                binding.incomeParentContainer.addView(mainCell)

                                topCell.setOnClickListener {
                                    hideOtherBoxes() // if you want to hide other boxes....
                                    topCell.arrow_up.visibility = View.VISIBLE
                                    topCell.arrow_down.visibility = View.GONE
                                    toggleContentCells(mainCell, View.VISIBLE)
                                }

                                topCell.arrow_up.setOnClickListener {
                                    topCell.arrow_up.visibility = View.GONE
                                    topCell.arrow_down.visibility = View.VISIBLE
                                    toggleContentCells(mainCell, View.GONE)
                                }
                            }

                            //EventBus.getDefault().post(GrandTotalEvent("$"+grandTotalAmount.roundToInt().toString()))
                            binding.grandTotalTextView.text =
                                "$".plus(Common.addNumberFormat(grandTotalAmount))
                        }
                    })
            }


        }

        else {
           // Log.e("NOT","NOt matched 2222")
            setupLayout()
        }
    }

    private fun setupLayout(){
       // binding.incomeParentContainer.removeAllViews()
        lifecycleScope.launchWhenStarted{
            viewModel.incomeDetails.observe(viewLifecycleOwner, { observableSampleContent ->
                val incomeActivity = (activity as? IncomeActivity)
                incomeActivity?.let { incomeActivity->
                    incomeActivity.binding.incomeDataLoader.visibility = View.INVISIBLE
                }

                    var observerCounter = 0
                    var getBorrowerIncome: ArrayList<BorrowerIncome> = arrayListOf()
                    while (observerCounter < observableSampleContent.size) {
                        val webIncome = observableSampleContent[observerCounter]
                        if (tabBorrowerId == webIncome.passedBorrowerId) {
                            webIncome.incomeData?.borrower?.borrowerIncomes?.let { webBorrowerIncome ->
                                getBorrowerIncome = webBorrowerIncome
                                //Log.e("BorrrowerIncomeSize",""+getBorrowerIncome.size)
                            }
                        }
                        observerCounter++
                    }
                    val sampleIncome = getSampleIncome()
                    for(m in 0 until sampleIncome.size){
                        val modelData = sampleIncome[m]
                        //Timber.e("header", modelData.headerTitle)
                        //Timber.e("h-amount", modelData.headerAmount)
                        val mainCell: LinearLayoutCompat = layoutInflater.inflate(R.layout.income_main_cell, null) as LinearLayoutCompat
                        val topCell: View = layoutInflater.inflate(R.layout.income_top_cell, null)
                        topCell.header_title.text = modelData.headerTitle
                        topCell.header_amount.setText(modelData.headerAmount)
                        topCell.tag = R.string.asset_top_cell
                        mainCell.addView(topCell)

                        var totalAmount = 0.0
                        for (i in 0 until getBorrowerIncome.size){

                            val webModelData = getBorrowerIncome[i]
                            webModelData.incomeCategory?.let { incomeCategory->
                                if(incomeCategory == modelData.headerTitle){
                                    webModelData.incomes?.let {
                                        for(j in 0 until it.size) {
                                            //Log.e("list-size",""+it.size)
                                            val contentCell: View = layoutInflater.inflate(R.layout.income_middle_cell, null)
                                            val contentData = webModelData.incomes[j]
                                            contentCell.content_title.text = contentData.incomeName
                                            contentCell.content_desc.text = contentData.jobTitle
                                            var startDate : String = ""
                                            var endDate : String = ""
                                            contentData.startDate?.let{
                                                startDate = "From ".plus(AppSetting.getMonthAndYearValue(it))
                                                contentCell.tenureTextView.setText(startDate)
                                            }
                                            contentData.endDate?.let {
                                                endDate = AppSetting.getMonthAndYearValue(it)
                                                contentCell.tenureTextView.setText(startDate.plus(" To " + endDate))
                                            }

                                            contentData.incomeValue?.let{ incomeValue->
                                                totalAmount += incomeValue
                                               // val formattedValue = Common.addNumberFormat(incomeValue)
                                                contentCell.content_amount.text = "$".plus(Common.addNumberFormat(incomeValue))
                                            }
                                            //Log.e("contentData.incomeTypeDisplayName",""+contentData.incomeTypeDisplayName)

                                            /*if(showUpdatedIncomeCell.length > 0 && modelData.headerTitle == showUpdatedIncomeCell){
                                                //Log.e("@@@@@@","BorrowerID" + tabBorrowerId + "Current Tab : "+ IncomeTabFragment.viewPager.currentItem + " Passed Tab Id " + updatedTabId )
                                                Log.e("updating list",showUpdatedIncomeCell + " Tab Id" + tabBorrowerId)
                                                Log.e("Id ","matched")
                                                //Log.e("tabId", ""+IncomeTabFragment.viewPager.currentItem)
                                                    contentCell.visibility = View.VISIBLE
                                                    topCell.arrow_up.visibility = View.VISIBLE
                                                    topCell.arrow_down.visibility = View.GONE
                                            }
                                            else {
                                                contentCell.visibility = View.GONE
                                            } */

                                            contentCell.visibility = View.GONE

                                            val parentActivity = activity as? IncomeActivity
                                            val bundle = Bundle()
                                            parentActivity?.let {
                                                parentActivity.loanApplicationId?.let { it1 -> bundle.putInt(AppConstant.loanApplicationId, it1) }
                                                tabBorrowerId?.let { it1 -> bundle.putInt(AppConstant.borrowerId, it1) }
                                                contentData.incomeId?.let { it1 -> bundle.putInt(AppConstant.incomeId, it1) }
                                                contentData.employmentCategory?.categoryId?.let { it1 -> bundle.putInt(AppConstant.incomeCategoryId, it1) }
                                                contentData.incomeTypeId?.let { it1 -> bundle.putInt(AppConstant.incomeTypeID, it1)
                                                borrowerName.let { name -> bundle.putString(AppConstant.borrowerName,name) }
                                                }
                                                //Timber.e(" content data - " + contentData)
                                            }
                                            if(contentData.endDate == null && contentData.incomeTypeDisplayName == Employment){
                                                contentCell.setOnClickListener {
                                                    findNavController().navigate(R.id.action_current_employement, bundle)
                                                }
                                            }
                                            else
                                            contentCell.setOnClickListener {
                                                findNavController().navigate(modelData.listenerAttached , bundle)

                                            }
                                            mainCell.addView(contentCell)
                                        }
                                    }
                                }
                            }
                        }

                        topCell.header_amount.text = "$".plus(Common.addNumberFormat(totalAmount)).plus("/mo")
                        grandTotalAmount += totalAmount

                        val bottomCell: View = layoutInflater.inflate(R.layout.income_bottom_cell, null)
                        bottomCell.footer_title.text = modelData.footerTitle
                        bottomCell.visibility = View.GONE
                        /*showUpdatedIncomeCell?.let { cellName ->
                            if(modelData.headerTitle.equals(cellName,true))
                                bottomCell.visibility = View.VISIBLE
                        } */
                        if(modelData.footerTitle == AppConstant.footerAddEmployment) {
                            bottomCell.setOnClickListener(bottomEmploymentListener)
                        }
                        else
                            bottomCell.setOnClickListener{
                                val parentActivity = activity as? IncomeActivity
                                parentActivity?.let {
                                    val bundle = Bundle()
                                    parentActivity.loanApplicationId?.let { it1 -> bundle.putInt(AppConstant.loanApplicationId, it1) }
                                    tabBorrowerId?.let { it1 -> bundle.putInt(AppConstant.borrowerId, it1) }
                                    borrowerName.let { name -> bundle.putString(AppConstant.borrowerName,name) }

                                    findNavController().navigate(modelData.listenerAttached, bundle)
                                }
                            }

                        mainCell.addView(bottomCell)
                        binding.incomeParentContainer.addView(mainCell)

                        topCell.setOnClickListener {
                            hideOtherBoxes() // if you want to hide other boxes....
                            topCell.arrow_up.visibility = View.VISIBLE
                            topCell.arrow_down.visibility = View.GONE
                            toggleContentCells(mainCell, View.VISIBLE)
                        }

                        topCell.arrow_up.setOnClickListener {
                            topCell.arrow_up.visibility = View.GONE
                            topCell.arrow_down.visibility = View.VISIBLE
                            toggleContentCells(mainCell, View.GONE)
                        }
                    }

                //EventBus.getDefault().post(GrandTotalEvent("$"+grandTotalAmount.roundToInt().toString()))
                binding.grandTotalTextView.text = "$".plus(Common.addNumberFormat(grandTotalAmount))

                /*if(updatedTabId != null && showUpdatedIncomeCell.length >0){
                    Log.e("****TabIdSelected***", ""+updatedTabId)
                   // IncomeTabFragment.selectTab(updatedTabId!!)
                } */
            })
        }
    }

    val currentEmploymentListener:View.OnClickListener= View.OnClickListener {}

    val bottomEmploymentListener:View.OnClickListener= View.OnClickListener {
        BottomDialogSelectEmployment.newInstance().show(childFragmentManager, BottomDialogSelectEmployment::class.java.canonicalName)
    }

    private fun toggleContentCells(mainCell: LinearLayoutCompat , display:Int){
        for (j in 0 until mainCell.childCount){
            if(mainCell[j].tag != R.string.asset_top_cell) {
                mainCell[j].visibility = display
                //Log.e("display", "" + display + " tag" + mainCell[j].tag)
            }
        }
    }

    private fun hideOtherBoxes(){
        val layout = binding.incomeParentContainer
        var mainCell: LinearLayoutCompat?
        for (i in 0 until layout.childCount) {
            mainCell = layout[i] as LinearLayoutCompat
            for(j in 0 until mainCell.childCount) {
                val innerCell = mainCell[j] as ConstraintLayout
                    if (innerCell.tag == R.string.asset_top_cell) {
                        innerCell.arrow_up.visibility = View.GONE
                        innerCell.arrow_down.visibility = View.VISIBLE
                    } else {
                        innerCell.visibility = View.GONE
                    }
            }

            /*
            val topCell = mainCell.getTag(R.string.asset_top_cell) as ConstraintLayout
            val middleCell = mainCell.getTag(R.string.asset_middle_cell) as ConstraintLayout
            val bottomCell = mainCell.getTag(R.string.asset_bottom_cell) as ConstraintLayout
            topCell.arrow_up.visibility = View.GONE
            topCell.arrow_down.visibility = View.VISIBLE
            middleCell.visibility = View.GONE
            bottomCell.visibility = View.GONE
             */
        }
    }

    override fun onResume() {
        super.onResume()

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
    fun onAddEmploymentEvent(eventAddEmployment: EventAddEmployment){
        val parentActivity = activity as? IncomeActivity
        val bundle = Bundle()
        //var incomeInfoId : Int? = null
        parentActivity?.let {
            parentActivity.loanApplicationId?.let { it1 -> bundle.putInt(AppConstant.loanApplicationId, it1) }
            tabBorrowerId?.let { it1 -> bundle.putInt(AppConstant.borrowerId, it1) }
            borrowerName.let { name -> bundle.putString(AppConstant.borrowerName,name) }
        }

        if(eventAddEmployment.boolean) {
            findNavController().navigate(R.id.action_add_current_employement, bundle)
        }
        else {
            findNavController().navigate(R.id.action_add_prev_employment , bundle)
        }
    }
}

/*


        else if(isUpdateSingleTab){
            Log.e("Updated Single2","tab SUCCESS")
            updateSingleTab()
            isUpdateSingleTab = false

        }
 */
