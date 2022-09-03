package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.rnsoft.colabademo.databinding.*
import com.rnsoft.colabademo.utils.Common
import kotlinx.android.synthetic.main.assets_bottom_cell.view.*
import kotlinx.android.synthetic.main.assets_middle_cell.view.*
import kotlinx.android.synthetic.main.assets_top_cell.view.*
import timber.log.Timber
import androidx.navigation.fragment.findNavController


class BorrowerOneAssets : BaseFragment() {

    private lateinit var binding: DynamicAssetFragmentLayoutBinding

    private val borrowerApplicationViewModel: BorrowerApplicationViewModel by activityViewModels()

    private var tabBorrowerId: Int? = null

    private var grandTotalAmount: Double = 0.0

    //private var assetTypeTotal:Double = 0.0

    private fun getSampleAssets(): ArrayList<TestAssetsModelClass> {
        val assetModelCell = TestAssetsModelClass(
            headerTitle = CATEGORIES.BankAccount.categoryName,
            headerAmount = "$0",
            footerTitle = "Add Bank Account",
            contentCell = arrayListOf(
                //   TestContentCell("Chase", "Checking" ,"$20,000"), TestContentCell("Ally Bank", "Saving", "$6,000")
            ),
            navigateToBank
        )

        val assetModelCell2 = TestAssetsModelClass(
            headerTitle = CATEGORIES.RetirementAccount.categoryName,
            headerAmount = "$0",
            footerTitle = "Add Retirement Account",
            contentCell = arrayListOf(
                // TestContentCell("401K", "Retirement Account" ,"$10,000" )
            ),
            navigateToRetirement
        )

        val assetModelCell3 = TestAssetsModelClass(
            headerTitle = CATEGORIES.StocksBondsOtherFinancialAssets.categoryName,
            headerAmount = "$0",
            footerTitle = "Add Financial Assets",
            contentCell = arrayListOf(
                //TestContentCell("AHC", "Mutual Funds" ,"$200"  )
            ),
            navigateToStockBonds
        )


        val assetModelCell4 = TestAssetsModelClass(
            headerTitle = CATEGORIES.ProceedFromTransaction.categoryName,
            headerAmount = "$0",
            footerTitle = "Add Proceeds From Transaction",
            contentCell = arrayListOf(
                // TestContentCell("Proceeds From Selling Non-Real Es...", "Proceeds From Transaction" ,"$1,200" )
            ),
            navigateToTransactionAsset
        )


        val assetModelCell5 = TestAssetsModelClass(
            headerTitle = CATEGORIES.GiftFunds.categoryName,
            headerAmount = "$0",
            footerTitle = "Add Gifts Account",
            contentCell = arrayListOf(
                //TestContentCell("Relative", "Cash Gifts" ,"$2000" )
            ),
            navigateToGiftAsset
        )


        val assetModelCell6 = TestAssetsModelClass(
            headerTitle = CATEGORIES.Other.categoryName,
            headerAmount = "$0",
            footerTitle = "Add Other Assets",
            contentCell = arrayListOf(
                //TestContentCell("Individual Development Account", "Other" ,"$600" )
            ),
            navigateToOtherAsset
        )


        val modelArrayList: ArrayList<TestAssetsModelClass> = arrayListOf()
        modelArrayList.add(assetModelCell)
        modelArrayList.add(assetModelCell2)
        modelArrayList.add(assetModelCell3)
        modelArrayList.add(assetModelCell4)
        modelArrayList.add(assetModelCell5)
        modelArrayList.add(assetModelCell6)


        return modelArrayList

    }

    private val navigateToBank = R.id.action_assets_bank_account //View.OnClickListener { findNavController().navigate(R.id.action_assets_bank_account) }
    private val navigateToRetirement = R.id.action_assets_retirement
    private val navigateToStockBonds = R.id.action_assets_stocks_bond
    private val navigateToTransactionAsset = R.id.action_assets_proceeds_transaction
    private val navigateToGiftAsset = R.id.action_assets_gift
    private val navigateToOtherAsset = R.id.action_assets_other


    //private var totalAmount = 0.0
    //private var savedAssets:ArrayList<Asset> = arrayListOf()
    //companion object {  private lateinit var savedContentCell: View }
    //  private var savedCellTitle:String = ""

    private var classCategoryTotal:Double = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DynamicAssetFragmentLayoutBinding.inflate(inflater, container, false)
        arguments?.let {
            tabBorrowerId = it.getInt(AppConstant.tabBorrowerId)
        }

        borrowerApplicationViewModel.assetsModelDataClass.observe(viewLifecycleOwner, Observer { observableSampleContent ->
                val assetsActivity = (activity as? AssetsActivity)
                assetsActivity?.let { assetsActivity ->
                    //assetsActivity.borrowerTabList?.let { borrowerTabList-> }
                    assetsActivity.binding.assetDataLoader.visibility = View.INVISIBLE
                }

                var shikra = false

                var visibleCategoryName = "TingPing"
                var observerCounter = 0
                var getBorrowerAssets: ArrayList<BorrowerAsset> = arrayListOf()
                var assetBorrowerName = ""
                while (observerCounter < observableSampleContent.size) {
                    val webAssets = observableSampleContent[observerCounter]
                    visibleCategoryName = webAssets.visibleCategoryName
                    if (tabBorrowerId == webAssets.passedBorrowerId) {
                        webAssets.bAssetData?.borrower?.borrowerAssets?.let { webBorrowerAssets ->
                            getBorrowerAssets = webBorrowerAssets
                        }
                        webAssets.bAssetData?.borrower?.borrowerName?.let { webBorrowerName ->
                            assetBorrowerName = webBorrowerName
                        }
                    }
                    observerCounter++
                }
                var borrowerIdToNavigate:Int = -1
                for (tab in observableSampleContent) {
                    borrowerIdToNavigate = tab.updateBorrowerId
                }

                while (observerCounter < observableSampleContent.size) {
                    val webAssets = observableSampleContent[observerCounter]
                    webAssets.visibleCategoryName = "TingPing"
                }


                val sampleAssets = getSampleAssets()
                for (m in 0 until sampleAssets.size) {
                    val modelData = sampleAssets[m]
                    //Timber.e("header", modelData.headerTitle)
                    //Timber.e("h-amount", modelData.headerAmount)
                    val mainCell: LinearLayoutCompat = layoutInflater.inflate(R.layout.asset_top_main_cell, null) as LinearLayoutCompat
                    val topCell: View = layoutInflater.inflate(R.layout.assets_top_cell, null)
                    topCell.header_title.text = modelData.headerTitle
                    topCell.header_amount.text = modelData.headerAmount
                    topCell.tag = R.string.asset_top_cell
                    mainCell.addView(topCell)


                    var totalAmount = 0.0
                    var currentAssetTypeID:Int? = 0
                    for (i in 0 until getBorrowerAssets.size) {
                        val webModelData = getBorrowerAssets[i]
                        webModelData.assetsCategory?.let { assetsCategory ->
                            if (assetsCategory == modelData.headerTitle) {
                                webModelData.assets?.let { it ->
                                    //savedAssets = it
                                    for (j in 0 until it.size) {
                                        val contentCell: View = layoutInflater.inflate(R.layout.assets_middle_cell, null)

                                        val contentData = webModelData.assets[j]

                                        contentCell.tag = R.string.asset_middle_cell
                                        contentData.assetUniqueId.let { uniqueAssetId->
                                            contentCell.content_id_cell.setText( uniqueAssetId.toString())
                                        }
                                        if (contentData.assetName.isNullOrBlank() || contentData.assetName.isNullOrEmpty())
                                            contentCell.content_title.text = contentData.assetTypeName
                                        else
                                            contentCell.content_title.text = contentData.assetName
                                        contentCell.content_desc.text = contentData.assetTypeName
                                        contentData.assetValue?.let { assetValue ->
                                            totalAmount += assetValue
                                            contentCell.content_amount.text =
                                                "$".plus(Common.addNumberFormat(assetValue))
                                        }
                                        if(contentData.assetCategoryName == visibleCategoryName && tabBorrowerId == borrowerIdToNavigate)
                                            contentCell.visibility = View.VISIBLE
                                        else
                                            contentCell.visibility = View.GONE

                                        contentCell.setOnClickListener {
                                            val parentActivity = activity as? AssetsActivity
                                            parentActivity?.let {
                                                val bundle = Bundle()
                                                parentActivity.loanApplicationId?.let { it1 -> bundle.putInt(AppConstant.loanApplicationId, it1) }
                                                tabBorrowerId?.let { it1 -> bundle.putInt(AppConstant.borrowerId, it1) }
                                                contentData.assetUniqueId.let { it1 -> bundle.putInt(AppConstant.assetUniqueId, it1) }
                                                contentData.assetCategoryId?.let { it1 -> bundle.putInt(AppConstant.assetCategoryId, it1) }
                                                bundle.putString(AppConstant.assetBorrowerName, assetBorrowerName)
                                                contentData.assetTypeID?.let { it1->
                                                    currentAssetTypeID = it1
                                                    bundle.putInt(AppConstant.assetTypeID, it1)
                                                }
                                                parentActivity.loanPurpose?.let { it1 -> bundle.putString(AppConstant.loanPurpose, it1) }
                                                contentData.assetCategoryName?.let { it1-> bundle.putString(AppConstant.assetCategoryName, it1) }
                                                Timber.e(" content data - $contentData")
                                                bundle.putInt(AppConstant.listenerAttached, modelData.listenerAttached)
                                                contentData.assetValue?.let { nonNullAssetValue->
                                                    //val assetTypeValue = topCell.header_amount.text.toString()
                                                    //val newValue = Common.removeCommas(assetTypeValue.replace("$","")).toDouble()
                                                    //Timber.e("newValue is $newValue")
                                                    classCategoryTotal = nonNullAssetValue

                                                }

                                                findNavController().navigate(modelData.listenerAttached, bundle)
                                            }
                                        }

                                        mainCell.addView(contentCell)
                                    }
                                }
                            }
                        }
                    }

                    topCell.header_amount.text = "$".plus(Common.addNumberFormat(totalAmount)) //"$"+totalAmount.roundToInt().toString()
                    grandTotalAmount += totalAmount

                    val bottomCell: View = layoutInflater.inflate(R.layout.assets_bottom_cell, null)
                    bottomCell.footer_title.text = modelData.footerTitle
                    //bottomCell.tag = R.string.asset_bottom_cell
                    val parentActivity = activity as? AssetsActivity

                    if (modelData.headerTitle == visibleCategoryName && tabBorrowerId == borrowerIdToNavigate) {
                        shikra = true
                        bottomCell.visibility = View.VISIBLE
                    }
                    else
                        bottomCell.visibility = View.GONE

                    bottomCell.setOnClickListener {
                        val parentActivity = activity as? AssetsActivity
                        parentActivity?.let {
                            val bundle = Bundle()
                            classCategoryTotal = 0.0
                            parentActivity.loanApplicationId?.let { it1 -> bundle.putInt(AppConstant.loanApplicationId, it1) }
                            currentAssetTypeID?.let { it1 -> bundle.putInt(AppConstant.assetTypeID, it1) }
                            tabBorrowerId?.let { it1 -> bundle.putInt(AppConstant.borrowerId, it1) }
                            parentActivity.loanPurpose?.let { it1 -> bundle.putString(AppConstant.loanPurpose, it1) }
                            bundle.putInt(AppConstant.assetUniqueId, -1)
                            bundle.putString(AppConstant.assetCategoryName, modelData.headerTitle)
                            bundle.putInt(AppConstant.listenerAttached, modelData.listenerAttached)
                            bundle.putString(AppConstant.assetBorrowerName, assetBorrowerName)
                            findNavController().navigate(modelData.listenerAttached, bundle)

                        }

                    }

                    mainCell.addView(bottomCell)

                    binding.assetParentContainer.addView(mainCell)

                    topCell.setOnClickListener {
                        hideOtherBoxes() // if you want to hide other boxes....
                        topCell.arrow_up.visibility = View.VISIBLE
                        topCell.arrow_down.visibility = View.GONE
                        toggleContentCells(mainCell, View.VISIBLE)
                        //bottomCell.visibility = View.VISIBLE
                    }

                    topCell.arrow_up.setOnClickListener {
                        topCell.arrow_up.visibility = View.GONE
                        topCell.arrow_down.visibility = View.VISIBLE
                        //contentCell.visibility = View.GONE
                        toggleContentCells(mainCell, View.GONE)
                        //bottomCell.visibility = View.GONE
                    }

                }

                //EventBus.getDefault().post(GrandTotalEvent("$".plus(Common.addNumberFormat(grandTotalAmount))))
                binding.grandTotalTextView.text = "$".plus(Common.addNumberFormat(grandTotalAmount))

                /*
                if(shikra){
                    for (tab in observableSampleContent) {
                        tab.updateBorrowerId = -1
                    }
                    borrowerIdToNavigate = -1
                }

                 */

            })

        if(binding.assetParentContainer== null)
            Timber.e("binding.assetParentContainer = null ")

        Timber.e("total child = "+ binding.assetParentContainer.childCount.toString())
        binding.assetParentContainer.refreshDrawableState()
        binding.scroll.refreshDrawableState()

        super.addListeners(binding.root)
        return binding.root
    }



    private fun toggleContentCells(mainCell: LinearLayoutCompat, display: Int) {
        for (j in 0 until mainCell.childCount) {
            if (mainCell[j].tag != R.string.asset_top_cell)
                mainCell[j].visibility = display
        }
    }

    private fun hideOtherBoxes() {
        val layout = binding.assetParentContainer
        var mainCell: LinearLayoutCompat?
        for (i in 0 until layout.childCount) {
            mainCell = layout[i] as LinearLayoutCompat
            for (j in 0 until mainCell.childCount) {
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
    fun onUpdateDataReceived(evt: AssetUpdateEvent) {

        if(evt.assetReturnParams.assetAction == AppConstant.assetAdded){
            addNewCell(evt.assetReturnParams)
        }
        else {

            val layout = binding.assetParentContainer
            var mainCell: LinearLayoutCompat?
            outerLoop@ for (i in 0 until layout.childCount) {
                mainCell = layout[i] as LinearLayoutCompat // Get the main cell....
                for (j in 0 until mainCell.childCount) {
                    val innerCell = mainCell[j] as ConstraintLayout // Get the main cell children....
                    if(innerCell.tag == R.string.asset_middle_cell)
                    {
                        if(innerCell.content_id_cell.text.toString() == evt.assetReturnParams.assetUniqueId.toString()) {

                            if(evt.assetReturnParams.assetAction == AppConstant.assetDeleted){
                                updateAssetCategoryTotal(evt.assetReturnParams , true )
                                mainCell.removeView(innerCell)
                                break@outerLoop
                            }
                            else {
                                evt.assetReturnParams.assetUniqueId.let { innerCell.content_id_cell.text = it.toString() }
                                evt.assetReturnParams.assetTypeName?.let { innerCell.content_desc.text = it }
                                evt.assetReturnParams.assetName?.let {
                                    if (it.isEmpty() || it.isBlank())
                                        innerCell.content_title.text = evt.assetReturnParams.assetTypeName
                                    else
                                        innerCell.content_title.text = it
                                }
                                evt.assetReturnParams.assetValue?.let {

                                    updateAssetCategoryTotal(evt.assetReturnParams )
                                    innerCell.content_amount.text = "$".plus(Common.addNumberFormat(it))
                                }

                                innerCell.setOnClickListener {
                                    val parentActivity = activity as? AssetsActivity
                                    parentActivity?.let {
                                        val bundle = Bundle()
                                        parentActivity.loanApplicationId?.let { it1 ->
                                            bundle.putInt(AppConstant.loanApplicationId, it1)
                                        }
                                        tabBorrowerId?.let { it1 -> bundle.putInt(AppConstant.borrowerId, it1) }
                                        evt.assetReturnParams.assetId?.let { it1 ->
                                            bundle.putInt(AppConstant.assetUniqueId, it1)
                                        }
                                        evt.assetReturnParams.assetUniqueId?.let { it1 ->
                                            bundle.putInt(AppConstant.assetUniqueId, it1)
                                        }
                                        evt.assetReturnParams.assetCategoryId?.let { it1 ->
                                            bundle.putInt(AppConstant.assetCategoryId, it1)
                                        }
                                        evt.assetReturnParams.assetTypeID?.let { it1 ->
                                            bundle.putInt(AppConstant.assetTypeID, it1)
                                        }
                                        parentActivity.loanPurpose?.let { it1 ->
                                            bundle.putString(AppConstant.loanPurpose, it1)
                                        }
                                        evt.assetReturnParams.assetCategoryName?.let { it1 ->
                                            bundle.putString(AppConstant.assetCategoryName, it1)
                                        }
                                        Timber.e(" content data - $evt.assetReturnParams")



                                        evt.assetReturnParams.listenerAttached?.let { it1 ->
                                            bundle.putInt(AppConstant.listenerAttached, it1)
                                            findNavController().navigate(it1, bundle)
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private fun addNewCell(assetReturnParams:AssetReturnParams ){
        val layout = binding.assetParentContainer
        var mainCell: LinearLayoutCompat? =null
        var foundMainCell = false
        outerLoop@ for (i in 0 until layout.childCount) {
            mainCell = layout[i] as LinearLayoutCompat // Get the main cell....
            for (j in 0 until mainCell.childCount) {
                val innerCell = mainCell[j] as ConstraintLayout // Get the main cell children....
                if(innerCell.tag == R.string.asset_top_cell)
                {
                    if(innerCell.header_title.text.toString() == assetReturnParams.assetCategoryName.toString()) {
                        foundMainCell = true
                        break@outerLoop
                    }
                }
            }
        }
        if(foundMainCell) {
            val contentCell: View = layoutInflater.inflate(R.layout.assets_middle_cell, null)
            contentCell.tag = R.string.asset_middle_cell

            if (assetReturnParams.assetName.isNullOrBlank() || assetReturnParams.assetName.isNullOrEmpty())
                contentCell.content_title.text = assetReturnParams.assetTypeName
            else
                contentCell.content_title.text = assetReturnParams.assetName

            assetReturnParams.assetUniqueId.let { contentCell.content_id_cell.text = it.toString() }

            contentCell.content_desc.text = assetReturnParams.assetTypeName
            assetReturnParams.assetValue?.let { assetValue ->

                updateAssetCategoryTotal(assetReturnParams)
                contentCell.content_amount.text = "$".plus(Common.addNumberFormat(assetValue))
            }
            contentCell.visibility = View.VISIBLE
            contentCell.setOnClickListener {
                val parentActivity = activity as? AssetsActivity
                parentActivity?.let {
                    val bundle = Bundle()
                    parentActivity.loanApplicationId?.let { it1 ->
                        bundle.putInt(AppConstant.loanApplicationId, it1)
                    }
                    tabBorrowerId?.let { it1 -> bundle.putInt(AppConstant.borrowerId, it1) }
                    assetReturnParams.assetId?.let { it1 ->
                        bundle.putInt(AppConstant.assetUniqueId, it1)
                    }
                    assetReturnParams.assetUniqueId?.let { it1 ->
                        bundle.putInt(AppConstant.assetUniqueId, it1)
                    }
                    assetReturnParams.assetCategoryId?.let { it1 ->
                        bundle.putInt(AppConstant.assetCategoryId, it1)
                    }
                    assetReturnParams.assetTypeID?.let { it1 ->
                        bundle.putInt(AppConstant.assetTypeID, it1)
                    }
                    parentActivity.loanPurpose?.let { it1 ->
                        bundle.putString(AppConstant.loanPurpose, it1)
                    }
                    assetReturnParams.assetCategoryName?.let { it1 ->
                        bundle.putString(AppConstant.assetCategoryName, it1)
                    }

                    assetReturnParams.assetValue?.let { nonNullAssetValue->
                        classCategoryTotal = nonNullAssetValue

                    }
                    assetReturnParams.assetBorrowerName?.let { nonNullAssetName ->
                        bundle.putString(AppConstant.assetBorrowerName, nonNullAssetName)
                    }

                    Timber.e(" content data - $assetReturnParams")

                    var listenerAttached:Int? = null
                    assetReturnParams.listenerAttached?.let { it1 ->
                        bundle.putInt(AppConstant.listenerAttached, it1)
                        Timber.e("navigation = $it1")
                        listenerAttached = it1
                    }
                    if(listenerAttached!=null)
                        findNavController().navigate(listenerAttached!!, bundle)
                }
            }

            mainCell?.addView(contentCell , mainCell.childCount-1)


        }

    }

    private fun updateAssetCategoryTotal(assetReturnParams:AssetReturnParams, needSubtraction:Boolean = false ){

        val layout = binding.assetParentContainer
        var foundLayout:ConstraintLayout? = null
        var mainCell: LinearLayoutCompat?
        outerLoop@ for (i in 0 until layout.childCount) {
            mainCell = layout[i] as LinearLayoutCompat // Get the main cell....
            for (j in 0 until mainCell.childCount) {
                val innerCell = mainCell[j] as ConstraintLayout // Get the main cell children....
                if (innerCell.tag == R.string.asset_top_cell && innerCell.header_title.text == assetReturnParams.assetCategoryName) {
                    foundLayout = innerCell
                    break@outerLoop
                }
            }
        }

        foundLayout?.let{
            val assetTypeValue = foundLayout.header_amount.text.toString()
            Timber.e("assetTypeValue = $assetTypeValue")

            var displayedValue = Common.removeCommas(assetTypeValue.replace("$","")).toDouble()
            assetReturnParams.assetValue?.let{ assetDoubleValue->
                if(needSubtraction) {
                    displayedValue -= assetDoubleValue
                    grandTotalAmount -= assetDoubleValue
                }
                else {
                    displayedValue -= classCategoryTotal
                    grandTotalAmount -= displayedValue
                    displayedValue += assetDoubleValue
                    grandTotalAmount += displayedValue
                }
                foundLayout.header_amount.text = "$".plus(Common.addNumberFormat(displayedValue))
                binding.grandTotalTextView.text = "$".plus(Common.addNumberFormat(grandTotalAmount))
            }
           // grandTotalAmount += newValue
            //EventBus.getDefault()
               // .post(GrandTotalEvent("$".plus(Common.addNumberFormat(grandTotalAmount))))


        }
    }

     */



}



/*
    private fun addListenerToNewOrModifiedCell(assetReturnParams:AssetReturnParams , contentCell:View){
        contentCell.setOnClickListener {
            val parentActivity = activity as? AssetsActivity
            parentActivity?.let {
                val bundle = Bundle()
                parentActivity.loanApplicationId?.let { it1 -> bundle.putInt(AppConstant.loanApplicationId, it1) }
                tabBorrowerId?.let { it1 -> bundle.putInt(AppConstant.borrowerId, it1) }
                assetReturnParams.assetUniqueId?.let { it1 -> bundle.putInt(AppConstant.assetUniqueId, it1) }
                assetReturnParams.assetCategoryId?.let { it1 -> bundle.putInt(AppConstant.assetCategoryId, it1) }
                assetReturnParams.assetTypeID?.let { it1 -> bundle.putInt(AppConstant.assetTypeID, it1) }
                parentActivity.loanPurpose?.let { it1 -> bundle.putString(AppConstant.loanPurpose, it1) }
                assetReturnParams.assetCategoryName?.let { it1-> bundle.putString(AppConstant.assetCategoryName, it1) }
                assetReturnParams.listenerAttached?.let { it1->
                    bundle.putInt(AppConstant.listenerAttached, assetReturnParams.listenerAttached)
                    findNavController().navigate(it1, bundle)
                }
            }
        }
    }
 */