package com.rnsoft.colabademo

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.iterator
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.*
import kotlinx.android.synthetic.main.common_govt_content_layout.view.*
import timber.log.Timber


class BorrowerTwoQuestions : GovtQuestionBaseFragment() {

    private lateinit var binding: BorrowerOneQuestionsLayoutBinding

    private var innerLayoutHashMap = HashMap<AppCompatTextView, ConstraintLayout>(0)
    private var openDetailBoxHashMap = HashMap<AppCompatRadioButton, ConstraintLayout>(0)
    private var closeDetailBoxHashMap = HashMap<AppCompatRadioButton, ConstraintLayout>(0)
    //private lateinit var test:ConstraintLayout

    private val bAppViewModel: BorrowerApplicationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BorrowerOneQuestionsLayoutBinding.inflate(inflater, container, false)
        setupLayout()
        //test = binding.root.findViewById<ConstraintLayout>(R.id.asian_inner_constraint_layout)
        super.addListeners(binding.root)
        return binding.root
    }

    private fun setupLayout(){
        setUpDynamicTabs()
        //setUpTabs()
    }

    private fun setUpDynamicTabs(){
        bAppViewModel.governmentQuestionsModelClass.observe(viewLifecycleOwner, Observer {
            it.questionData?.let{ questionData->
                for(qData in questionData) {
                    qData.headerText?.let { tabTitle->
                        val appCompactTextView = createAppCompactTextView(tabTitle, 0)
                        binding.horizontalTabs.addView(appCompactTextView)
                        val contentView = createContentLayoutForTab(qData)
                        innerLayoutHashMap.put(appCompactTextView, contentView)
                        binding.parentContainer.addView(contentView)
                        appCompactTextView.setOnClickListener(scrollerTab)
                        horizontalTabArrayList.add(appCompactTextView)
                    }
                }
            }

        })
    }

    fun convertDpToPixel(dp: Float, context: Context): Int {
        return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }

    private fun createAppCompactTextView(tabTitle:String, tabIndex:Int):AppCompatTextView{
        //val appCompactTextView = AppCompatTextView(requireContext())

        val appCompactTextView: AppCompatTextView =
            layoutInflater.inflate(R.layout.govt_text_view, null) as AppCompatTextView

        //appCompactTextView.setBackgroundColor(R.drawable.blue_white_style_filter)
        //appCompactTextView.setPadding(12,0,12,0)
        //appCompactTextView.height = 40

        val textParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            convertDpToPixel(30.0f,requireContext()),
            1.0f
        )

        textParam.setMargins( convertDpToPixel(8.0f,requireContext()), 0, 0, 0)

        appCompactTextView.setLayoutParams(textParam)

        // appCompactTextView.setTextColor(resources.getColor(R.color.doc_filter_text_color_selector, activity?.theme ))
        //appCompactTextView.gravity = Gravity.CENTER
        //appCompactTextView.setTextSize(13, 13F)
        //appCompactTextView.isAllCaps = false
        //appCompactTextView.id = tabIndex
        appCompactTextView.tag = tabTitle
        appCompactTextView.setText(tabTitle)
        return appCompactTextView
    }

    private fun createContentLayoutForTab(questionData:QuestionData):ConstraintLayout{
        val contentCell: ConstraintLayout =
            layoutInflater.inflate(R.layout.common_govt_content_layout, null) as ConstraintLayout
        contentCell.visibility = View.GONE
        Timber.e(" questionData.question "+questionData.question)
        contentCell.govt_question.text =  questionData.question
        questionData.answerDetail?.let {
            contentCell.detail_text.text = it
        }
        var headerTitle = ""
        questionData.headerText?.let {
            contentCell.govt_detail_box.tag = it
            contentCell.ans_yes.tag = it
            headerTitle = it
        }

        if(questionData.answer==null)
            contentCell.ans_no.isChecked = true
        else {
            if(questionData.answer.equals("no",true))
                contentCell.ans_no.isChecked = true
            else {
                contentCell.ans_yes.isChecked = true
                contentCell.govt_detail_box.visibility = View.VISIBLE
            }
        }
        contentCell.ans_no.setOnClickListener { contentCell.govt_detail_box.visibility = View.GONE}
        contentCell.ans_yes.setOnClickListener {
            contentCell.govt_detail_box.visibility = View.VISIBLE
            openDetailBoxNew(headerTitle)
        }
        contentCell.govt_detail_box.setOnClickListener {
            contentCell.govt_detail_box.visibility = View.VISIBLE
            openDetailBoxNew(headerTitle)
        }
        return contentCell
    }

    private fun openDetailBoxNew(stringForSpecificFragment:String){
        when(stringForSpecificFragment) {
            "Undisclosed Borrowered Funds" ->{  findNavController().navigate(R.id.action_undisclosed_borrowerfund)}
            "Amount to Borrow?" ->{}
            "Ownership Interest in Property" ->{  findNavController().navigate(R.id.action_ownership_interest)}
            "Own Property Type" ->{}
            "Debt Co-Signer or Guarantor" ->{  findNavController().navigate(R.id.action_debt_co)}
            "Outstanding Judgements" ->{  findNavController().navigate(R.id.action_outstanding)}
            "Federal Debt Deliquency" ->{ findNavController().navigate(R.id.action_federal_debt)}
            "Party to Lawsuit" ->{ findNavController().navigate(R.id.action_party_to) }
            "Bankruptcy " ->{    findNavController().navigate(R.id.action_bankruptcy) }
            "Child Support, Alimony, etc." ->{ findNavController().navigate(R.id.action_child_support) }
            "Type" ->{}
            "Foreclosured Property" ->{ findNavController().navigate(R.id.action_fore_closure_property) }
            "Pre-Foreclosureor Short Sale" ->{ findNavController().navigate(R.id.action_pre_for_closure) }
            "Title Conveyance" ->{ findNavController().navigate(R.id.action_title_conveyance)}
            "Property Title" ->{}
            "Family or Business affiliation" ->{}
            else->{
                Timber.e(" not matching with header title...")
            }
        }
    }

    private fun setUpTabs(){

        // add yes button listeners
        binding.debtCoYes.setOnClickListener(openDetailBox)
        binding.outstandingJudgementYes.setOnClickListener(openDetailBox)
        binding.federalDeptYes.setOnClickListener(openDetailBox)
        binding.partyToLawsuitYes.setOnClickListener(openDetailBox)
        binding.ownershipInterestYes.setOnClickListener(openDetailBox)
        binding.titleConveyanceYes.setOnClickListener(openDetailBox)
        binding.preForeclosureShortSaleYes.setOnClickListener(openDetailBox)
        binding.foreclosuredPropertyYes.setOnClickListener(openDetailBox)
        binding.bankruptcyYes.setOnClickListener(openDetailBox)
        binding.childSupportYes.setOnClickListener(openDetailBox)
        //binding.btnDemographicInfo.setOnClickListener(openDetailBox)
        binding.undisclosedYes.setOnClickListener(openDetailBox)
        binding.undisclosedCreditYes.setOnClickListener(openDetailBox)
        binding.undisclosedMortgageYes.setOnClickListener(openDetailBox)
        binding.priorityLiensYes.setOnClickListener(openDetailBox)


        openDetailBoxHashMap = hashMapOf((binding.debtCoYes to binding.debtCoDetailBox), (binding.childSupportYes to binding.childSupportDetailBox),
            (binding.partyToLawsuitYes to binding.partyToLawsuitDetailBox),
            (binding.ownershipInterestYes to binding.ownershipInterestDetailBox),
            (binding.outstandingJudgementYes to binding.outstandingJudgementDetailBox), (binding.federalDeptYes to binding.federalDeptDetailBox),

            (binding.titleConveyanceYes to binding.titleConveyanceDetailBox), (binding.preForeclosureShortSaleYes to binding.preForeclosureShortSaleDetailBox),
            (binding.foreclosuredPropertyYes to binding.foreclosuredPropertyDetailBox), (binding.bankruptcyYes  to binding.bankruptcyDetailBox),
            (binding.undisclosedYes to binding.undisclosedDetailBox), (binding.undisclosedCreditYes to binding.undisclosedCreditDetailBox),
            (binding.undisclosedMortgageYes to binding.undisclosedMortgageDetailBox), (binding.priorityLiensYes to binding.priorityLiensDetailBox)
        )


        binding.debtCoNo.setOnClickListener(closeDetailBox)
        binding.outstandingJudgementNo.setOnClickListener(closeDetailBox)
        binding.federalDeptNo.setOnClickListener(closeDetailBox)
        binding.partyToLawsuitNo.setOnClickListener(closeDetailBox)
        binding.ownershipInterestNo.setOnClickListener(closeDetailBox)
        binding.titleConveyanceNo.setOnClickListener(closeDetailBox)
        binding.preForeclosureShortSaleNo.setOnClickListener(closeDetailBox)
        binding.foreclosuredPropertyNo.setOnClickListener(closeDetailBox)
        binding.bankruptcyNo.setOnClickListener(closeDetailBox)
        binding.childSupportNo.setOnClickListener(closeDetailBox)
        //binding.btnDemographicInfo.setOnClickListener(closeDetailBox)
        binding.undisclosedNo.setOnClickListener(closeDetailBox)
        binding.undisclosedCreditNo.setOnClickListener(closeDetailBox)
        binding.undisclosedMortgageNo.setOnClickListener(closeDetailBox)
        binding.priorityLiensNo.setOnClickListener(closeDetailBox)

        closeDetailBoxHashMap = hashMapOf((binding.debtCoNo to binding.debtCoDetailBox), (binding.childSupportNo to binding.childSupportDetailBox),
            (binding.partyToLawsuitNo to binding.partyToLawsuitDetailBox),
            (binding.ownershipInterestNo to binding.ownershipInterestDetailBox),
            (binding.outstandingJudgementNo to binding.outstandingJudgementDetailBox), (binding.federalDeptNo to binding.federalDeptDetailBox),

            (binding.titleConveyanceNo to binding.titleConveyanceDetailBox), (binding.preForeclosureShortSaleNo to binding.preForeclosureShortSaleDetailBox),
            (binding.foreclosuredPropertyNo to binding.foreclosuredPropertyDetailBox), (binding.bankruptcyNo  to binding.bankruptcyDetailBox),
            (binding.undisclosedNo to binding.undisclosedDetailBox), (binding.undisclosedCreditNo to binding.undisclosedCreditDetailBox),
            (binding.undisclosedMortgageNo to binding.undisclosedMortgageDetailBox), (binding.priorityLiensNo to binding.priorityLiensDetailBox)
        )

        //binding.btnUndisclosedBorrowed.performClick()




    }

    private val openDetailBox = object:View.OnClickListener{
        override fun onClick(p0: View) {
            openDetailBoxHashMap.getValue(p0 as AppCompatRadioButton).visibility = View.VISIBLE
            when(p0) {
                binding.childSupportYes -> {
                    findNavController().navigate(R.id.action_child_support)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_child_support)
                    }
                }
                binding.bankruptcyYes -> {
                    findNavController().navigate(R.id.action_bankruptcy)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_bankruptcy)
                    }
                }
                binding.priorityLiensYes -> {
                    findNavController().navigate(R.id.action_priority_liens)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_priority_liens)
                    }
                }
                binding.ownershipInterestYes -> {
                    findNavController().navigate(R.id.action_ownership_interest)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_ownership_interest)
                    }
                }
                binding.undisclosedYes -> {
                    findNavController().navigate(R.id.action_undisclosed_borrowerfund)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_undisclosed_borrowerfund)
                    }
                }
                binding.debtCoYes-> {
                    findNavController().navigate(R.id.action_debt_co)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_debt_co)
                    }
                }
                binding.outstandingJudgementYes-> {
                    findNavController().navigate(R.id.action_outstanding)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_outstanding)
                    }
                }
                binding.federalDeptYes-> {
                    findNavController().navigate(R.id.action_federal_debt)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_federal_debt)
                    }
                }
                binding.partyToLawsuitYes-> {
                    findNavController().navigate(R.id.action_party_to)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_party_to)
                    }
                }

                binding.titleConveyanceYes-> {
                    findNavController().navigate(R.id.action_title_conveyance)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_title_conveyance)
                    }
                }
                binding.preForeclosureShortSaleYes-> {
                    findNavController().navigate(R.id.action_pre_for_closure)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_pre_for_closure)
                    }
                }
                binding.foreclosuredPropertyYes-> {
                    findNavController().navigate(R.id.action_fore_closure_property)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_fore_closure_property)
                    }
                }

                binding.undisclosedCreditYes-> {
                    findNavController().navigate(R.id.action_undisclosed_credit)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_undisclosed_credit)
                    }
                }
                binding.undisclosedMortgageYes-> {
                    findNavController().navigate(R.id.action_undisclosed_mortgage)
                    openDetailBoxHashMap.getValue(p0).setOnClickListener{
                        findNavController().navigate(R.id.action_undisclosed_mortgage)
                    }
                }


                else -> {
                }
            }
        }
    }

    private val closeDetailBox = object:View.OnClickListener{
        override fun onClick(p0: View) {
            closeDetailBoxHashMap.getValue(p0 as AppCompatRadioButton).visibility = View.GONE
        }
    }

    private val scrollerTab = object:View.OnClickListener{
        override fun onClick(p0: View) {
            if(p0 is AppCompatTextView){
                for(item in horizontalTabArrayList){
                    item.isActivated = false
                    item.setTextColor(resources.getColor(R.color.doc_filter_txt_color, requireActivity().theme))
                }
                p0.isActivated = true
                p0.setTextColor(resources.getColor(R.color.colaba_primary_color, requireActivity().theme))
                for ((key, value) in innerLayoutHashMap) {
                    if(key == p0)
                        value.visibility = View.VISIBLE
                    else
                        value.visibility = View.GONE
                }
            }
        }
    }



}