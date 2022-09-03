package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.UndisclosedBorrowerFundLayoutBinding
import com.rnsoft.colabademo.utils.Common
import com.rnsoft.colabademo.utils.CustomMaterialFields
import com.rnsoft.colabademo.utils.NumberTextFormat
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class UndisclosedBorrowerFundFragment:BaseFragment() {

    companion object{
        const val UndisclosedBorrowerQuestionConstant ="What is the amount of money you’ve borrowed or intend to borrow?"
    }

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var _binding: UndisclosedBorrowerFundLayoutBinding? = null
    private val binding get() = _binding!!
    private var updateGovernmentQuestionByBorrowerId:GovernmentParams? = null
    private var questionId:Int = 0
    private var userName:String? = null
    private var whichBorrowerId:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UndisclosedBorrowerFundLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        arguments?.let {
            questionId = it.getInt(AppConstant.questionId)
            whichBorrowerId = it.getInt(AppConstant.whichBorrowerId)
            updateGovernmentQuestionByBorrowerId = it.getParcelable(AppConstant.addUpdateQuestionsParams)
            userName = it.getString(AppConstant.govtUserName)
        }
        binding.borrowerPurpose.text = userName




        // fill the date with the API values...
        updateGovernmentQuestionByBorrowerId?.let { updateGovernmentQuestionByBorrowerId ->
            for (item in updateGovernmentQuestionByBorrowerId.Questions) {

                item.parentQuestionId?.let { parentQuestionId->
                    if (parentQuestionId == questionId) {
                        item.answerDetail?.let {
                           binding.edDetails.setText(it)
                        }
                        item.answer?.let {
                            binding.annualBaseEditText.setText(it)
                        }
                    }
                }
            }
        }



        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backToGovernmentScreen )
        setUpUI()
        super.addListeners(binding.root)
        return root
    }

    private val backToGovernmentScreen: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }

    private fun setUpUI(){
        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.saveBtn.setOnClickListener {
            val fieldsValidated = checkEmptyFields()
            if(fieldsValidated) {
                clearFocusFromFields()
                val dollarAmount = Common.removeCommas(binding.annualBaseEditText.text.toString())
                updateOwnerShipInterest( dollarAmount)
                //findNavController().popBackStack()
            }
        }
        addFocusOutListenerToFields()
        CustomMaterialFields.setDollarPrefix(binding.annualBaseLayout, requireActivity())
        binding.annualBaseEditText.addTextChangedListener(NumberTextFormat(binding.annualBaseEditText))
    }

    private fun updateOwnerShipInterest(getDetailString:String) {
        updateGovernmentQuestionByBorrowerId?.let { updateGovernmentQuestionByBorrowerId ->
            for (item in updateGovernmentQuestionByBorrowerId.Questions) {
                if (item.id == questionId) {
                    item.answerDetail = getDetailString
                }
            }

            var detailTitle =  binding.edDetails.text.toString()
            if(detailTitle.isEmpty() || detailTitle.isBlank())
                detailTitle = ""
            EventBus.getDefault().post(UndisclosedBorrowerFundUpdateEvent(detailTitle, getDetailString, whichBorrowerId))
            findNavController().popBackStack()
        }
    }



    private fun clearFocusFromFields(){
        binding.annualBaseLayout.clearFocus()
    }

    private fun checkEmptyFields():Boolean{
        var bool =  true

        if(binding.annualBaseEditText.text?.isEmpty() == true || binding.annualBaseEditText.text?.isBlank() == true) {

            CustomMaterialFields.setError(binding.annualBaseLayout, "This field is required." , requireContext())
            bool = false
        }
        else
            CustomMaterialFields.clearError(binding.annualBaseLayout,  requireContext())


        if(binding.edDetails.text?.isEmpty() == true || binding.edDetails.text?.isBlank() == true) {
            CustomMaterialFields.setError(binding.layoutDetail, "This field is required." , requireContext())
            bool = false
        }
        else
            CustomMaterialFields.clearError(binding.layoutDetail,  requireContext())


        return bool
    }


    private  fun addFocusOutListenerToFields(){
        //binding.accountNumberEdittext.setOnFocusChangeListener(CustomFocusListenerForEditText( binding.accountNumberEdittext , binding.accountNumberLayout , requireContext()))
        //binding.accountTypeCompleteView.setOnFocusChangeListener(CustomFocusListenerForAutoCompleteTextView( binding.accountTypeCompleteView , binding.accountTypeInputLayout , requireContext()))
        binding.annualBaseEditText.onFocusChangeListener = CustomFocusListenerForEditText( binding.annualBaseEditText , binding.annualBaseLayout , requireContext())
        binding.edDetails.onFocusChangeListener = CustomFocusListenerForEditText( binding.edDetails , binding.layoutDetail , requireContext())
    }
}