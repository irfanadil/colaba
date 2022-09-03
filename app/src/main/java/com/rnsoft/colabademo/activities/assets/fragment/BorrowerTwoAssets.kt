package com.rnsoft.colabademo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.*
import kotlinx.android.synthetic.main.assets_bottom_cell.view.*
import kotlinx.android.synthetic.main.assets_middle_cell.view.*
import kotlinx.android.synthetic.main.assets_top_cell.view.*

class BorrowerTwoAssets : BaseFragment() {

    private lateinit var binding: DynamicAssetFragmentLayoutBinding

    private fun getSampleAssets():ArrayList<TestAssetsModelClass>{
        val assetModelCell = TestAssetsModelClass( headerTitle = CATEGORIES.BankAccount.categoryName, headerAmount = "$0" , footerTitle = "Add Bank Account",
            contentCell = arrayListOf(
                //   TestContentCell("Chase", "Checking" ,"$20,000"), TestContentCell("Ally Bank", "Saving", "$6,000")
            ), navigateToBank)

        val assetModelCell2 = TestAssetsModelClass( headerTitle = CATEGORIES.RetirementAccount.categoryName, headerAmount = "$0" , footerTitle = "Add Retirement Account",
            contentCell = arrayListOf(
                // TestContentCell("401K", "Retirement Account" ,"$10,000" )
            ), navigateToRetirement)

        val assetModelCell3 = TestAssetsModelClass( headerTitle = CATEGORIES.StocksBondsOtherFinancialAssets.categoryName, headerAmount = "$0" , footerTitle = "Add Financial Assets",
            contentCell = arrayListOf(
                //TestContentCell("AHC", "Mutual Funds" ,"$200"  )
            ), navigateToStockBonds)


        val assetModelCell4 = TestAssetsModelClass( headerTitle = CATEGORIES.ProceedFromTransaction.categoryName, headerAmount = "$0" , footerTitle = "Add Proceeds From Transaction",
            contentCell = arrayListOf(
                // TestContentCell("Proceeds From Selling Non-Real Es...", "Proceeds From Transaction" ,"$1,200" )
            ), navigateToTransactionAsset)


        val assetModelCell5 = TestAssetsModelClass( headerTitle = CATEGORIES.GiftFunds.categoryName, headerAmount = "$0" , footerTitle = "Add Gifts Account",
            contentCell = arrayListOf(
                //TestContentCell("Relative", "Cash Gifts" ,"$2000" )
            ), navigateToGiftAsset)


        val assetModelCell6 = TestAssetsModelClass( headerTitle = CATEGORIES.Other.categoryName, headerAmount = "$0" , footerTitle = "Add Other Assets",
            contentCell = arrayListOf(
                //TestContentCell("Individual Development Account", "Other" ,"$600" )
            ) , navigateToOtherAsset)



        val modelArrayList:ArrayList<TestAssetsModelClass> = arrayListOf()
        modelArrayList.add(assetModelCell)
        modelArrayList.add(assetModelCell2)
        modelArrayList.add(assetModelCell3)
        modelArrayList.add(assetModelCell4)
        modelArrayList.add(assetModelCell5)
        modelArrayList.add(assetModelCell6)


        return modelArrayList

    }

    private val navigateToBank =  R.id.action_assets_bank_account //View.OnClickListener { findNavController().navigate(R.id.action_assets_bank_account) }
    private val navigateToRetirement = R.id.action_assets_retirement  //View.OnClickListener { findNavController().navigate(R.id.action_assets_retirement) }
    private val navigateToStockBonds =  R.id.action_assets_stocks_bond  // View.OnClickListener { findNavController().navigate(R.id.action_assets_stocks_bond) }
    private val navigateToTransactionAsset = R.id.action_assets_proceeds_transaction  //View.OnClickListener { findNavController().navigate(R.id.action_assets_proceeds_transaction) }
    private val navigateToGiftAsset =  R.id.action_assets_gift  //View.OnClickListener { findNavController().navigate(R.id.action_assets_gift) }
    private val navigateToOtherAsset = R.id.action_assets_other  //View.OnClickListener { findNavController().navigate(R.id.action_assets_other) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DynamicAssetFragmentLayoutBinding.inflate(inflater, container, false)
        setupLayout()
        super.addListeners(binding.root)
        return binding.root
    }

    private fun setupLayout(){

        val sampleAssets = getSampleAssets()

        for (i in 0 until sampleAssets.size) {

            val modelData = sampleAssets[i]
            Log.e("header",modelData.headerTitle )
            Log.e("h-amount",modelData.headerAmount )

            val mainCell: LinearLayoutCompat =
                layoutInflater.inflate(R.layout.asset_top_main_cell, null) as LinearLayoutCompat
            val topCell: View = layoutInflater.inflate(R.layout.assets_top_cell, null)
            topCell.header_title.text =  modelData.headerTitle

            topCell.header_amount.setText(modelData.headerAmount)

            topCell.tag = R.string.asset_top_cell
            mainCell.addView(topCell)


            for (j in 0 until modelData.contentCell.size) {
                val contentCell: View =
                    layoutInflater.inflate(R.layout.assets_middle_cell, null)
                val contentData = modelData.contentCell[j]
                contentCell.content_title.text = contentData.title
                contentCell.content_desc.text = contentData.description
                contentCell.content_amount.text = contentData.contentAmount
                contentCell.visibility = View.GONE
                contentCell.setOnClickListener {
                    findNavController().navigate(modelData.listenerAttached)
                }
                mainCell.addView(contentCell)
            }


            val bottomCell: View = layoutInflater.inflate(R.layout.assets_bottom_cell, null)
            bottomCell.footer_title.text =  modelData.footerTitle
            //bottomCell.tag = R.string.asset_bottom_cell
            bottomCell.visibility = View.GONE
            bottomCell.setOnClickListener {
                findNavController().navigate(modelData.listenerAttached)
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
                toggleContentCells(mainCell , View.GONE)
                //bottomCell.visibility = View.GONE
            }
        }
    }

   private fun toggleContentCells(mainCell: LinearLayoutCompat , display:Int){
        for (j in 0 until mainCell.childCount){
            if(mainCell[j].tag != R.string.asset_top_cell)
                mainCell[j].visibility = display
        }
    }

    private fun hideOtherBoxes(){
        val layout = binding.assetParentContainer
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





}
