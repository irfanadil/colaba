package com.rnsoft.colabademo

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.RetirementLayoutBinding
import com.rnsoft.colabademo.utils.Common
import com.rnsoft.colabademo.utils.CustomMaterialFields
import com.rnsoft.colabademo.utils.NumberTextFormat
import timber.log.Timber


class RetirementFragment:AssetBaseFragment() {

    private var _binding: RetirementLayoutBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = RetirementLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpUI()
        super.addListeners(binding.root)
        Timber.e("RetirementFragment has been loaded....")
        arguments?.let { arguments->
            loanApplicationId = arguments.getInt(AppConstant.loanApplicationId)
            loanPurpose = arguments.getString(AppConstant.loanPurpose)
            borrowerId = arguments.getInt(AppConstant.borrowerId)
            assetUniqueId = arguments.getInt(AppConstant.assetUniqueId , -1)
            if(assetUniqueId == -1)
                assetUniqueId = null
            Timber.e("catching unique id in Argument  = $assetUniqueId")
            assetCategoryName = arguments.getString(AppConstant.assetCategoryName , null)
            assetBorrowerName = arguments.getString(AppConstant.assetBorrowerName , null)
            listenerAttached = arguments.getInt(AppConstant.listenerAttached)
            observeRetirementData()
        }
        assetBorrowerName?.let {
            binding.borrowerPurpose.text = it
        }
        assetUniqueId?.let { assetUniqueId ->
            if (assetUniqueId > 0) {
                binding.topDelImageview.visibility = View.VISIBLE
                binding.topDelImageview.setOnClickListener {
                    showDeleteDialog(returnUpdatedParams(true))
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backToAssetScreen )
        return root
    }

    private fun setUpUI(){

        CustomMaterialFields.setDollarPrefix(binding.annualBaseLayout, requireActivity())
        binding.annualBaseEditText.addTextChangedListener(NumberTextFormat(binding.annualBaseEditText))

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.saveBtn.setOnClickListener { saveRetirement() }

        addFocusOutListenerToFields()

        setUpEndIcon()
    }

    private fun saveRetirement(){

        val fieldsValidated = checkEmptyFields()
        if(fieldsValidated) {
            clearFocusFromFields()
             lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->

                    loanApplicationId?.let { notNullLoanApplicationId->
                        borrowerId?.let { notNullBorrowerId ->
                            val retirementAddUpdateParams =
                                RetirementAddUpdateParams(
                                    BorrowerId = notNullBorrowerId,
                                    LoanApplicationId = notNullLoanApplicationId,
                                    AccountNumber = binding.accountNumberEdittext.text.toString(),
                                    InstitutionName = binding.financialEditText.text.toString(),
                                    Value = Common.removeCommas(binding.annualBaseEditText.text.toString()).toInt(),
                                    Id = assetUniqueId
                                )
                            viewModel.addUpdateRetirement(authToken , retirementAddUpdateParams)
                        }
                    }
                }
            }

            observeAddUpdateResponse()
        }



    }

    private fun setUpEndIcon(){
        binding.accountNumberLayout.setEndIconOnClickListener(View.OnClickListener {
            if (binding.accountNumberEdittext.getTransformationMethod()
                    .equals(PasswordTransformationMethod.getInstance())
            ) { //  hide password
                binding.accountNumberEdittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                binding.accountNumberLayout.setEndIconDrawable(R.drawable.ic_eye_hide)
            } else {
                binding.accountNumberEdittext.setTransformationMethod(PasswordTransformationMethod.getInstance())
                binding.accountNumberLayout.setEndIconDrawable(R.drawable.ic_eye_icon_svg)
            }
        })
    }

    private fun clearFocusFromFields(){
        binding.accountNumberLayout.clearFocus()
        binding.annualBaseLayout.clearFocus()
        binding.financialLayout.clearFocus()
    }

    private fun checkEmptyFields():Boolean{
        var bool =  true
        if(binding.accountNumberEdittext.text?.isEmpty() == true || binding.accountNumberEdittext.text?.isBlank() == true) {
            CustomMaterialFields.setError(binding.accountNumberLayout, "This field is required." , requireContext())
            bool = false
        }
        else
            CustomMaterialFields.clearError(binding.accountNumberLayout,  requireContext())

        if(binding.annualBaseEditText.text?.isEmpty() == true || binding.annualBaseEditText.text?.isBlank() == true) {

            CustomMaterialFields.setError(binding.annualBaseLayout, "This field is required." , requireContext())
            bool = false
        }
        else
            CustomMaterialFields.clearError(binding.annualBaseLayout,  requireContext())

        if(binding.financialEditText.text?.isEmpty() == true || binding.financialEditText.text?.isBlank() == true) {
            CustomMaterialFields.setError(binding.financialLayout, "This field is required." , requireContext())
            bool = false
        }
        else
            CustomMaterialFields.clearError(binding.financialLayout,  requireContext())
        return bool
    }

    private  fun addFocusOutListenerToFields(){
        binding.accountNumberEdittext.onFocusChangeListener = CustomFocusListenerForEditText( binding.accountNumberEdittext , binding.accountNumberLayout , requireContext())
        binding.annualBaseEditText.onFocusChangeListener = CustomFocusListenerForEditText( binding.annualBaseEditText , binding.annualBaseLayout , requireContext())
        binding.financialEditText.onFocusChangeListener = CustomFocusListenerForEditText( binding.financialEditText , binding.financialLayout , requireContext())
    }

    private fun observeRetirementData(){
        assetUniqueId?.let { assetUniqueId ->
            if (loanApplicationId != null && borrowerId != null && assetUniqueId > 0) {
                viewModel.retirementAccountDetail.observe(
                    viewLifecycleOwner,
                    { observeRetirementData ->
                        if (observeRetirementData?.code == AppConstant.RESPONSE_CODE_SUCCESS) {
                            observeRetirementData.retirementAccountData?.let { retirementAccountData ->
                                //retirementAccountData.id?.let { id = it }
                                binding.financialEditText.setText(retirementAccountData.institutionName)
                                binding.accountNumberEdittext.setText(retirementAccountData.accountNumber)
                                val value = retirementAccountData.value.toString()
                                binding.annualBaseEditText.setText(value)
                            }
                        }
                    })

                lifecycleScope.launchWhenStarted {
                    sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                        viewModel.getRetirementAccountDetails(
                            authToken,
                            loanApplicationId!!,
                            borrowerId!!,
                            assetUniqueId
                        )
                    }
                }
            }
        }
    }

    private fun returnUpdatedParams(assetDeleteBoolean:Boolean = false): AssetReturnParams {
        var assetAction = AppConstant.assetAdded
        if(assetDeleteBoolean)
            assetAction = AppConstant.assetDeleted
        else
            assetUniqueId?.let { assetUniqueId ->
                if (assetUniqueId > 0)
                    assetAction = AppConstant.assetUpdated
            }

        Timber.e("catching unique id in returnUpdatedParams  = $assetUniqueId")
        assetUniqueId?.let { notNullAssetUniqueId->
            if(notNullAssetUniqueId<=0)
                assetUniqueId = null
        }

        return AssetReturnParams(
            assetName = binding.financialEditText.text.toString(),
            assetTypeName =assetCategoryName,
            assetBorrowerName = assetBorrowerName,
            assetTypeID = assetTypeID,
            assetUniqueId = assetUniqueId,
            assetCategoryId = assetCategoryId,
            assetCategoryName = assetCategoryName,
            listenerAttached = listenerAttached,
            assetAction = assetAction,
            assetValue = (Common.removeCommas(binding.annualBaseEditText.text.toString())).toDouble()
        )
    }



}