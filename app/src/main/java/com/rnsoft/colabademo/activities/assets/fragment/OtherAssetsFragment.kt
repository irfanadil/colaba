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
import com.rnsoft.colabademo.databinding.OtherAssetsLayoutBinding
import com.rnsoft.colabademo.utils.Common
import com.rnsoft.colabademo.utils.CustomMaterialFields
import com.rnsoft.colabademo.utils.NumberTextFormat
import timber.log.Timber
import java.util.ArrayList


class OtherAssetsFragment:AssetBaseFragment() {

    private var _binding: OtherAssetsLayoutBinding? = null
    private val binding get() = _binding!!

    private var otherAssetArray: ArrayList<String> = arrayListOf("Trust Account", "Bridge Loan Proceeds", "Individual Development Account (IDA)", "Cash Value of Life Insurance", "Employer Assistance", "Relocation Funds", "Rent Credit", "Lot Equity", "Sweat Equity", "Trade Equity", "Other")
    private lateinit var otherAssetAdapter : ArrayAdapter<String>
    private var otherAssetTypesByList: ArrayList<GetAssetTypesByCategoryItem> = arrayListOf()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = OtherAssetsLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpUI()
        super.addListeners(binding.root)
        arguments?.let { arguments->
            loanApplicationId = arguments.getInt(AppConstant.loanApplicationId)
            loanPurpose = arguments.getString(AppConstant.loanPurpose)
            borrowerId = arguments.getInt(AppConstant.borrowerId)
            assetUniqueId = arguments.getInt(AppConstant.assetUniqueId , -1)
            if(assetUniqueId == 0)
                assetUniqueId = null
            Timber.e("catching unique id in Argument  = $assetUniqueId")
            assetCategoryId = arguments.getInt(AppConstant.assetCategoryId , 7)
            assetTypeID = arguments.getInt(AppConstant.assetTypeID)
            assetCategoryName = arguments.getString(AppConstant.assetCategoryName , null)
            assetBorrowerName = arguments.getString(AppConstant.assetBorrowerName , null)
            listenerAttached = arguments.getInt(AppConstant.listenerAttached)
            getOtherAssets()
        }
        assetBorrowerName?.let {
            binding.borrowerPurpose.text = it
        }
        assetUniqueId?.let { nonNullAssetUniqueId ->
            if (nonNullAssetUniqueId > 0) {
                binding.topDelImageview.visibility = View.VISIBLE
                binding.topDelImageview.setOnClickListener {
                    showDeleteDialog(returnUpdatedParams(true))
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backToAssetScreen )
        return root
    }

    private fun getOtherAssets(){

    viewModel.assetTypesByCategoryItemList.observe(
        viewLifecycleOwner,
        { assetTypesByCategoryItemList ->
            if (assetTypesByCategoryItemList != null && assetTypesByCategoryItemList.size > 0) {
                otherAssetTypesByList = assetTypesByCategoryItemList
                otherAssetArray = arrayListOf()
                for (item in otherAssetTypesByList) {
                    item.displayName?.let {
                        otherAssetArray.add(it)
                    }
                }
                getOtherDetailData()
            }
        })


        lifecycleScope.launchWhenStarted {
           sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
               if (loanApplicationId != null)
                   viewModel.fetchAssetTypesByCategoryItemList(authToken, assetCategoryId, loanApplicationId!!)
           }
        }


    }

    private fun getOtherDetailData(){
        assetUniqueId?.let { nonNullAssetUniqueId ->
            if (loanApplicationId != null && borrowerId != null && nonNullAssetUniqueId > 0) {
                lifecycleScope.launchWhenStarted {
                    viewModel.otherAssetDetail.observe(viewLifecycleOwner, { otherAssetDetail ->
                        if (otherAssetDetail?.code == AppConstant.RESPONSE_CODE_SUCCESS) {
                            otherAssetDetail?.otherAssetData?.let { otherAssetData ->
                                var bool = false
                                otherAssetData.assetTypeName?.let {
                                    for (item in otherAssetArray)
                                        if (item == otherAssetData.assetTypeName) {
                                            binding.accountTypeCompleteView.setText(item, false)
                                            visibleOtherFields(otherAssetArray.indexOf(item))
                                            bool = true
                                            break
                                        }

                                }
                                if (bool) {
                                    otherAssetData.otherUniqueId?.let { assetUniqueId = it }
                                    otherAssetData.institutionName?.let {
                                        binding.financialEditText.setText(it)
                                    }
                                    otherAssetData.accountNumber?.let {
                                        binding.accountNumberEdittext.setText(it)
                                    }
                                    otherAssetData.assetValue?.let {
                                        binding.annualBaseEditText.setText(
                                            it.toString()
                                        )
                                    }
                                }
                            }
                        }
                    })

                    sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                        viewModel.getOtherAssetDetails(
                            authToken,
                            loanApplicationId!!,
                            borrowerId!!,
                            nonNullAssetUniqueId
                        )
                    }
                }
            }
        }


    }

    private fun setUpUI(){

        otherAssetAdapter = ArrayAdapter(binding.root.context, android.R.layout.simple_list_item_1,  otherAssetArray)
        binding.accountTypeCompleteView.setAdapter(otherAssetAdapter)
        binding.accountTypeCompleteView.setOnFocusChangeListener { _, _ ->
            HideSoftkeyboard.hide(requireContext(),  binding.accountTypeCompleteView)
            binding.accountTypeCompleteView.showDropDown()
        }
        binding.accountTypeCompleteView.setOnClickListener{
            binding.accountTypeCompleteView.showDropDown()
        }

        binding.accountTypeCompleteView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ -> visibleOtherFields(position) }
        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.saveBtn.setOnClickListener {
            saveOtherAssets()
        }
        addFocusOutListenerToFields()
        CustomMaterialFields.setDollarPrefix(binding.annualBaseLayout, requireActivity())
        binding.annualBaseEditText.addTextChangedListener(NumberTextFormat(binding.annualBaseEditText))
        setUpEndIcon()
    }

    private fun saveOtherAssets() {
        val fieldsValidated = checkEmptyFields()
        if (fieldsValidated) {
            clearFocusFromFields()
            lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    var accountTypeId: Int? = null
                    for (item in otherAssetTypesByList) {
                        if (item.name == binding.accountTypeCompleteView.text.toString())
                            accountTypeId = item.id
                    }

                    accountTypeId?.let { notNullAccountTypeId ->
                        loanApplicationId?.let { notNullLoanApplicationId ->
                            borrowerId?.let { notNullBorrowerId ->
                                val otherAssetAddUpdateParams =
                                    OtherAssetAddUpdateParams(
                                        AssetTypeId = notNullAccountTypeId,
                                        GiftSourceId = notNullLoanApplicationId,
                                        BorrowerId = notNullBorrowerId,
                                        AssetId = assetUniqueId,
                                        Description =  binding.edDetails.text.toString(),
                                        AccountNumber = Common.removeCommas(binding.accountNumberEdittext.text.toString()),
                                        Value = Common.removeCommas(binding.annualBaseEditText.text.toString()).toInt(),
                                        InstitutionName = binding.financialEditText.text.toString()
                                    )
                                viewModel.addUpdateOtherAsset(authToken, otherAssetAddUpdateParams)
                            }
                        }

                    }

                }
            }
            observeAddUpdateResponse()
        }
    }

    private fun returnUpdatedParams(assetDeleteBoolean:Boolean = false): AssetReturnParams {
        var assetAction = AppConstant.assetAdded
        if(assetDeleteBoolean)
            assetAction = AppConstant.assetDeleted
        else {
            assetUniqueId?.let { nonNullAssetUniqueId ->
                if (nonNullAssetUniqueId > 0)
                    assetAction = AppConstant.assetUpdated
            }
        }

        Timber.e("catching unique id in returnUpdatedParams  = $assetUniqueId")
        assetUniqueId?.let { notNullAssetUniqueId->
            if(notNullAssetUniqueId<=0)
                assetUniqueId = null
        }

        return AssetReturnParams(
            assetName = binding.accountTypeCompleteView.text.toString(),
            assetTypeName = binding.financialEditText.text.toString(),
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

    private fun visibleOtherFields(position:Int){
        binding.accountTypeInputLayout.defaultHintTextColor = ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), R.color.grey_color_two ))
        binding.accountNumberLayout.helperText?.let{
            if(it.isNotEmpty())
                CustomMaterialFields.clearError(binding.accountTypeInputLayout, requireContext())
        }
        binding.accountNumberLayout.visibility = View.GONE
        binding.financialLayout.visibility = View.GONE
        binding.annualBaseLayout.visibility = View.GONE
        binding.layoutDetail.visibility = View.GONE

        if(position<4) {
            binding.accountNumberLayout.visibility = View.VISIBLE
            binding.financialLayout.visibility = View.VISIBLE
            binding.annualBaseLayout.visibility = View.VISIBLE
            binding.layoutDetail.visibility = View.GONE
        }
        else
            if(position == 4 || position == 5) {
                binding.annualBaseLayout.hint = "Cash Value"
                binding.annualBaseLayout.visibility = View.VISIBLE
                binding.layoutDetail.visibility = View.GONE
            }
            else
                if(position > 5 && position < otherAssetArray.size-1) {
                    binding.annualBaseLayout.hint = "Market Value of Equity"
                    binding.annualBaseLayout.visibility = View.VISIBLE
                    binding.layoutDetail.visibility = View.GONE
                }
                else{
                    binding.annualBaseLayout.hint = "Cash or Market Value"
                    binding.annualBaseLayout.visibility = View.VISIBLE
                    binding.layoutDetail.visibility = View.VISIBLE
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

    private fun clearFocusFromFields(){
        binding.accountNumberLayout.clearFocus()
        binding.accountTypeInputLayout.clearFocus()
        binding.annualBaseLayout.clearFocus()
        binding.financialLayout.clearFocus()
    }

    private fun checkEmptyFields():Boolean{
        var bool =  true

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

        /*

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

         */
        return bool
    }

    private  fun addFocusOutListenerToFields(){
        binding.accountNumberEdittext.onFocusChangeListener = CustomFocusListenerForEditText( binding.accountNumberEdittext , binding.accountNumberLayout , requireContext())
        //binding.accountTypeCompleteView.setOnFocusChangeListener(CustomFocusListenerForAutoCompleteTextView( binding.accountTypeCompleteView , binding.accountTypeInputLayout , requireContext()))
        binding.annualBaseEditText.onFocusChangeListener = CustomFocusListenerForEditText( binding.annualBaseEditText , binding.annualBaseLayout , requireContext())
        binding.financialEditText.onFocusChangeListener = CustomFocusListenerForEditText( binding.financialEditText , binding.financialLayout , requireContext())
    }
}