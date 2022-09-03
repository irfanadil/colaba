package com.rnsoft.colabademo

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout

import com.rnsoft.colabademo.databinding.FirstMortgageLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields

import com.rnsoft.colabademo.utils.NumberTextFormat
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.lang.Exception
import java.lang.NullPointerException


/**
 * Created by Anita Kiran on 9/9/2021.
 */
class RealEstateFirstMortgage : BaseFragment(),View.OnClickListener {

    private lateinit var binding : FirstMortgageLayoutBinding
    var firstMortgageModel= FirstMortgageModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FirstMortgageLayoutBinding.inflate(inflater, container, false)

        val title = arguments?.getString(AppConstant.address).toString()
        title.let {
            if(it != "null")
              binding.borrowerPurpose.setText(title)
        }

        binding.backButton.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.firstMorgtageParentLayout.setOnClickListener(this)
        binding.cbFloodInsurance.setOnClickListener(this)
        binding.cbHomeownwerInsurance.setOnClickListener(this)
        binding.cbPropertyTaxes.setOnClickListener(this)
        binding.switchCreditLimit.setOnClickListener(this)
        binding.rbPaidClosingYes.setOnClickListener(this)
        binding.rbPaidClosingNo.setOnClickListener(this)

        setInputFields()
        getFirstMortgageDetails()
        super.addListeners(binding.root)

        return binding.root
    }

    private fun getFirstMortgageDetails() {
        try {
            firstMortgageModel = arguments?.getParcelable(AppConstant.firstMortgage)!!
            firstMortgageModel?.let {
                it.firstMortgagePayment?.let {
                    binding.edFirstMortgagePayment.setText(Math.round(it).toString())
                    CustomMaterialFields.setColor(
                        binding.layoutFirstPayment,
                        R.color.grey_color_two,
                        requireActivity()
                    )
                }
                it.unpaidFirstMortgagePayment?.let {
                    binding.edUnpaidBalance.setText(Math.round(it).toString())
                    CustomMaterialFields.setColor(
                        binding.layoutUnpaidBalance,
                        R.color.grey_color_two,
                        requireActivity()
                    )
                }
                it.floodInsuranceIncludeinPayment?.let {
                    if (it == true) {
                        binding.cbFloodInsurance.isChecked = true
                        binding.cbFloodInsurance.setTypeface(null, Typeface.BOLD)
                    } else {
                        binding.cbFloodInsurance.isChecked = false
                        binding.cbFloodInsurance.setTypeface(null, Typeface.NORMAL)
                    }
                }
                it.propertyTaxesIncludeinPayment?.let {
                    if (it == true) {
                        binding.cbPropertyTaxes.isChecked = true
                        binding.cbPropertyTaxes.setTypeface(null, Typeface.BOLD)
                    } else {
                        binding.cbPropertyTaxes.isChecked = false
                        binding.cbPropertyTaxes.setTypeface(null, Typeface.NORMAL)
                    }
                }
                it.homeOwnerInsuranceIncludeinPayment?.let {
                    if (it == true) {
                        binding.cbHomeownwerInsurance.isChecked = true
                        binding.cbHomeownwerInsurance.setTypeface(null, Typeface.BOLD)
                    } else {
                        binding.cbHomeownwerInsurance.isChecked = false
                        binding.cbHomeownwerInsurance.setTypeface(null, Typeface.NORMAL)
                    }
                }

                it.isHeloc?.let { bool ->
                    if (bool == true) {
                        binding.switchCreditLimit.isChecked = true
                        binding.tvHeloc.setTypeface(null, Typeface.BOLD)
                        binding.layoutCreditLimit.visibility = View.VISIBLE

                        it.helocCreditLimit?.let {
                            binding.edCreditLimit.setText(Math.round(it).toString())
                            CustomMaterialFields.setColor(
                                binding.layoutCreditLimit,
                                R.color.grey_color_two,
                                requireActivity()
                            )
                        }

                    } else {
                        binding.switchCreditLimit.isChecked = false
                        binding.tvHeloc.setTypeface(null, Typeface.NORMAL)
                    }
                }

                it.paidAtClosing?.let {
                    if (it == true) {
                        binding.rbPaidClosingYes.isChecked = true
                        binding.rbPaidClosingYes.setTypeface(null, Typeface.BOLD)
                    } else {
                        binding.rbPaidClosingNo.isChecked = true
                        binding.rbPaidClosingNo.setTypeface(null, Typeface.BOLD)
                    }
                }
            }
        } catch (e: NullPointerException){

        }
    }

    private fun saveData() {

        // first mortgage
        val firstMortgagePayment = binding.edFirstMortgagePayment.text.toString().trim()
        var newFirstMortgagePayment = if(firstMortgagePayment.length > 0) firstMortgagePayment.replace(",".toRegex(), "") else null

        // second mortgage
        val unpaidBalance = binding.edUnpaidBalance.text.toString().trim()
        var newUnpaidBalance = if(unpaidBalance.length > 0) unpaidBalance.replace(",".toRegex(), "") else null

        val creditLimit = binding.edCreditLimit.text.toString().trim()
        var newCreditLimit = if(creditLimit.length > 0) creditLimit.replace(",".toRegex(), "") else null

        val floodInsurance = if(binding.cbFloodInsurance.isChecked) true else false
        val propertyTax = if(binding.cbPropertyTaxes.isChecked) true else false
        val homeownerInsurance = if(binding.cbHomeownwerInsurance.isChecked) true else false
        val isPaidAtClosing = if(binding.rbPaidClosingYes.isChecked)true else false
        val isHeloc = if(binding.switchCreditLimit.isChecked)true else false

        val firstMortgageDetail = FirstMortgageModel(firstMortgagePayment = newFirstMortgagePayment?.toDouble(),unpaidFirstMortgagePayment = newUnpaidBalance?.toDouble(),
            helocCreditLimit = newCreditLimit?.toDoubleOrNull(), floodInsuranceIncludeinPayment = floodInsurance,propertyTaxesIncludeinPayment = propertyTax,homeOwnerInsuranceIncludeinPayment = homeownerInsurance,
            paidAtClosing = isPaidAtClosing,isHeloc = isHeloc)

        findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.firstMortgage, firstMortgageDetail)
        findNavController().popBackStack()

    }

    override fun onClick(view: View?) {
        when (view?.getId()) {
            R.id.backButton ->  requireActivity().onBackPressed()
            R.id.btn_save ->  saveData()
            R.id.first_morgtage_parentLayout-> {
                HideSoftkeyboard.hide(requireActivity(), binding.firstMorgtageParentLayout)
                super.removeFocusFromAllFields(binding.firstMorgtageParentLayout)
            }
            R.id.cb_flood_insurance ->
                if (binding.cbFloodInsurance.isChecked) {
                    binding.cbFloodInsurance.setTypeface(null, Typeface.BOLD)
                }else{
                    binding.cbFloodInsurance.setTypeface(null, Typeface.NORMAL)
                }

            R.id.cb_property_taxes ->
                if (binding.cbPropertyTaxes.isChecked) {
                    binding.cbPropertyTaxes.setTypeface(null, Typeface.BOLD)
                }else{
                    binding.cbPropertyTaxes.setTypeface(null, Typeface.NORMAL)
                }

            R.id.cb_homeownwer_insurance ->
                if (binding.cbHomeownwerInsurance.isChecked) {
                    binding.cbHomeownwerInsurance.setTypeface(null, Typeface.BOLD)
                }else{
                    binding.cbHomeownwerInsurance.setTypeface(null, Typeface.NORMAL)
                }

            R.id.switch_credit_limit ->
                if(binding.switchCreditLimit.isChecked) {
                    binding.layoutCreditLimit.visibility = View.VISIBLE
                    binding.tvHeloc.setTypeface(null, Typeface.BOLD)
                } else {
                    binding.layoutCreditLimit.visibility = View.GONE
                    binding.tvHeloc.setTypeface(null, Typeface.NORMAL)
                }

            R.id.rb_paid_closing_yes ->
                if (binding.rbPaidClosingYes.isChecked) {
                    binding.rbPaidClosingYes.setTypeface(null, Typeface.BOLD)
                    binding.rbPaidClosingNo.setTypeface(null, Typeface.NORMAL)
                }else{
                    binding.rbPaidClosingYes.setTypeface(null, Typeface.NORMAL)
                }

            R.id.rb_paid_closing_no ->
                if (binding.rbPaidClosingNo.isChecked) {
                    binding.rbPaidClosingNo.setTypeface(null, Typeface.BOLD)
                    binding.rbPaidClosingYes.setTypeface(null, Typeface.NORMAL)
                }else{
                    binding.rbPaidClosingNo.setTypeface(null, Typeface.NORMAL)
                }
        }
    }

    private fun setInputFields(){

        // set lable focus
        binding.edFirstMortgagePayment.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edFirstMortgagePayment, binding.layoutFirstPayment, requireContext()))
        binding.edUnpaidBalance.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edUnpaidBalance, binding.layoutUnpaidBalance, requireContext()))
        binding.edCreditLimit.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edCreditLimit, binding.layoutCreditLimit, requireContext()))

        // set Dollar prifix
        CustomMaterialFields.setDollarPrefix(binding.layoutFirstPayment,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutUnpaidBalance,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutCreditLimit,requireContext())

        binding.edFirstMortgagePayment.addTextChangedListener(NumberTextFormat(binding.edFirstMortgagePayment))
        binding.edUnpaidBalance.addTextChangedListener(NumberTextFormat(binding.edUnpaidBalance))
        binding.edCreditLimit.addTextChangedListener(NumberTextFormat(binding.edCreditLimit))
    }

    fun setError(textInputlayout: TextInputLayout, errorMsg: String) {
        textInputlayout.helperText = errorMsg
        textInputlayout.setBoxStrokeColorStateList(
            AppCompatResources.getColorStateList(requireContext(), R.color.primary_info_stroke_error_color))
    }

    fun clearError(textInputlayout: TextInputLayout) {
        textInputlayout.helperText = ""
        textInputlayout.setBoxStrokeColorStateList(
            AppCompatResources.getColorStateList(
                requireContext(),
                R.color.primary_info_line_color
            )
        )
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
    fun onErrorReceived(event: WebServiceErrorEvent) {
        if(event.isInternetError)
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG )
        else
            if(event.errorResult!=null)
                SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG )
        hideLoader()
    }

    private fun hideLoader(){
        val  activity = (activity as? RealEstateActivity)
        activity?.binding?.loaderRealEstate?.visibility = View.GONE
    }

}