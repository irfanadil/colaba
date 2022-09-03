package com.rnsoft.colabademo

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.StockBondsLayoutBinding
import com.rnsoft.colabademo.utils.Common
import com.rnsoft.colabademo.utils.CustomMaterialFields
import com.rnsoft.colabademo.utils.NumberTextFormat
import timber.log.Timber
import java.util.ArrayList



class StockBondsFragment:AssetBaseFragment() {

    private var _binding: StockBondsLayoutBinding? = null
    private val binding get() = _binding!!

    private var dataArray: ArrayList<String> = arrayListOf("Checking Account", "Saving Account")
    private var bankAccounts: ArrayList<DropDownResponse> = arrayListOf()
    private lateinit var bankAdapter:ArrayAdapter<String>

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
            assetName = binding.accountTypeCompleteView.text.toString(),
            assetTypeName =binding.financialEditText.text.toString(),
            assetBorrowerName = assetBorrowerName,
            assetTypeID = assetTypeID,
            assetUniqueId = assetUniqueId,
            assetCategoryId = assetCategoryId,
            assetCategoryName = assetCategoryName,
            listenerAttached = listenerAttached,
            assetAction = assetAction,
            assetValue = Common.removeCommas(binding.annualBaseEditText.text.toString()).toDouble()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StockBondsLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpUI()
        super.addListeners(binding.root)
        arguments?.let { arguments->
            loanApplicationId = arguments.getInt(AppConstant.loanApplicationId)
            loanPurpose = arguments.getString(AppConstant.loanPurpose)
            borrowerId = arguments.getInt(AppConstant.borrowerId)
            assetUniqueId = arguments.getInt(AppConstant.assetUniqueId , -1)
            if(assetUniqueId == -1)
                assetUniqueId = null
            Timber.e("catching unique id in Argument  = $assetUniqueId")
            assetTypeID = arguments.getInt(AppConstant.assetTypeID)
            assetCategoryName = arguments.getString(AppConstant.assetCategoryName , null)
            assetBorrowerName = arguments.getString(AppConstant.assetBorrowerName , null)
            listenerAttached = arguments.getInt(AppConstant.listenerAttached)
            observeStockBondsData()
        }
        assetBorrowerName?.let {
            binding.borrowerPurpose.text = it
        }
        assetUniqueId?.let { assetUniqueId ->
            if (assetUniqueId > 0) {
                binding.topDelImageview.visibility = View.VISIBLE
                binding.topDelImageview.setOnClickListener {
                    showDeleteDialog(
                        returnUpdatedParams(
                            true
                        )
                    )
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backToAssetScreen )
        return root
    }

    private fun setUpUI(){

        bankAdapter = ArrayAdapter(binding.root.context, android.R.layout.simple_list_item_1,  dataArray)
        binding.accountTypeCompleteView.setAdapter(bankAdapter)
        binding.accountTypeCompleteView.setOnFocusChangeListener { _, _ ->
            HideSoftkeyboard.hide(requireContext(),  binding.accountTypeCompleteView)
            binding.accountTypeCompleteView.showDropDown()
        }
        binding.accountTypeCompleteView.setOnClickListener{
            binding.accountTypeCompleteView.showDropDown()
        }

        binding.accountTypeCompleteView.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, id ->
                binding.accountTypeInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey_color_two ))
                binding.accountNumberLayout.helperText?.let{
                    if(it.isNotEmpty())
                        CustomMaterialFields.clearError(binding.accountTypeInputLayout, requireContext())
                }
                //if(position ==dataArray.size-1) { } else{}
            }

        binding.backButton.setOnClickListener { findNavController().popBackStack() }

        binding.saveBtn.setOnClickListener {
           saveStockBonds()
        }

        addFocusOutListenerToFields()

        CustomMaterialFields.setDollarPrefix(binding.annualBaseLayout, requireActivity())
        binding.annualBaseEditText.addTextChangedListener(NumberTextFormat(binding.annualBaseEditText))

        setUpEndIcon()
    }

    private fun saveStockBonds(){
        val fieldsValidated = checkEmptyFields()
        if(fieldsValidated) {
            clearFocusFromFields()

            var accountTypeId:Int?=null
            for(item in bankAccounts){
                if(item.name == binding.accountTypeCompleteView.text.toString())
                    accountTypeId = item.id
            }
            accountTypeId?.let { notNullAccountTypeId->
                loanApplicationId?.let { notNullLoanApplicationId->
                    borrowerId?.let { notNullBorrowerId ->
                        val stocksBondsAddUpdateParams =
                            StocksBondsAddUpdateParams(
                                AssetTypeId = notNullAccountTypeId,
                                LoanApplicationId = notNullLoanApplicationId,
                                BorrowerId = notNullBorrowerId,
                                Id = assetUniqueId,
                                AccountNumber = binding.accountNumberEdittext.text.toString(),
                                Balance = Common.removeCommas(binding.annualBaseEditText.text.toString()).toInt(),
                                InstitutionName = binding.financialEditText.text.toString()
                            )
                        viewModel.addUpdateStockBonds( stocksBondsAddUpdateParams)
                    }
                }

            }


            observeAddUpdateResponse()
        }



    }

    private fun clearFocusFromFields(){
        binding.accountNumberLayout.clearFocus()
        binding.accountTypeInputLayout.clearFocus()
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

        if(binding.accountTypeCompleteView.text?.isEmpty() == true || binding.accountTypeCompleteView.text?.isBlank() == true) {
            CustomMaterialFields.setError(binding.accountTypeInputLayout, "This field is required." , requireContext())
            bool = false
        }
        else
            CustomMaterialFields.clearError(binding.accountTypeInputLayout,  requireContext())

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
        //binding.accountTypeCompleteView.setOnFocusChangeListener(CustomFocusListenerForAutoCompleteTextView( binding.accountTypeCompleteView , binding.accountTypeInputLayout , requireContext()))
        binding.annualBaseEditText.onFocusChangeListener = CustomFocusListenerForEditText( binding.annualBaseEditText , binding.annualBaseLayout , requireContext())
        binding.financialEditText.onFocusChangeListener = CustomFocusListenerForEditText( binding.financialEditText , binding.financialLayout , requireContext())
    }

    private fun observeStockBondsData(){

        lifecycleScope.launchWhenStarted {
            viewModel.allFinancialAsset.observe(viewLifecycleOwner, { allFinancialAsset ->
                if(allFinancialAsset.size>0) {
                    dataArray = arrayListOf()
                    bankAccounts = arrayListOf()
                    for (item in allFinancialAsset) {
                        dataArray.add(item.name)
                        bankAccounts.add(item)
                    }
                    Timber.e("Data Array- $dataArray")
                    Timber.e("BankAccounts- $bankAccounts")

                    bankAdapter = ArrayAdapter(binding.root.context, android.R.layout.simple_list_item_1,  dataArray)
                    binding.accountTypeCompleteView.setAdapter(bankAdapter)
                }
                else
                    SandbarUtils.showError(requireActivity(),AppConstant.WEB_SERVICE_ERR_MSG)
            })
        }
        assetUniqueId?.let { assetUniqueId ->
            if (loanApplicationId != null && borrowerId != null && assetUniqueId > 0) {
                viewModel.financialAssetDetail.observe(viewLifecycleOwner, { financialAssetDetail ->
                    if (financialAssetDetail?.code == AppConstant.RESPONSE_CODE_SUCCESS) {
                        financialAssetDetail.financialAssetData?.let { financialAssetData ->
                            binding.financialEditText.setText(financialAssetData.institutionName)
                            binding.accountNumberEdittext.setText(financialAssetData.accountNumber)
                            binding.annualBaseEditText.setText(financialAssetData.balance.toString())
                            //financialAssetData.id?.let { id = it }
                            financialAssetData.assetTypeId?.let { assetTypeId ->
                                for (item in bankAccounts) {
                                    if (assetTypeId == item.id) {
                                        binding.accountTypeCompleteView.setText(item.name, false)
                                        break
                                    }
                                }
                            }
                        }
                    }
                })

                lifecycleScope.launchWhenStarted {
                    sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                        viewModel.getFinancialAssetDetails(
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

    private fun setUpEndIcon(){
        binding.accountNumberLayout.setEndIconOnClickListener {
            if (binding.accountNumberEdittext.transformationMethod
                    .equals(PasswordTransformationMethod.getInstance())
            ) { //  hide password
                binding.accountNumberEdittext.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.accountNumberLayout.setEndIconDrawable(R.drawable.ic_eye_hide)
            } else {
                binding.accountNumberEdittext.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.accountNumberLayout.setEndIconDrawable(R.drawable.ic_eye_icon_svg)
            }
        }
    }
}