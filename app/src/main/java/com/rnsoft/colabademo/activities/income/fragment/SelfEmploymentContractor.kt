package com.rnsoft.colabademo

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.activities.addresses.info.fragment.DeleteCurrentResidenceDialogFragment

import com.rnsoft.colabademo.databinding.AppHeaderWithCrossDeleteBinding
import com.rnsoft.colabademo.databinding.SelfEmpolymentContLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields

import com.rnsoft.colabademo.utils.NumberTextFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by Anita Kiran on 9/15/2021.
 */
@AndroidEntryPoint
class SelfEmploymentContractor : BaseFragment(),View.OnClickListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val viewModel : IncomeViewModel by activityViewModels()
    private lateinit var binding: SelfEmpolymentContLayoutBinding
    private lateinit var toolbarBinding: AppHeaderWithCrossDeleteBinding
    private val borrowerApplicationViewModel: BorrowerApplicationViewModel by activityViewModels()
    private var borrowerName: String? = null
    var loanApplicationId: Int? = null
    var incomeInfoId :Int? = null
    var borrowerId :Int? = null
    private var businessAddress = AddressData()
    private var savedViewInstance: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         return if (savedViewInstance != null){
            savedViewInstance
        } else {
             binding = SelfEmpolymentContLayoutBinding.inflate(inflater, container, false)
             savedViewInstance = binding.root
             toolbarBinding = binding.headerIncome
             super.addListeners(binding.root)
             // set Header title
             toolbarBinding.toolbarTitle.setText(getString(R.string.self_employment_contractor))


             arguments?.let { arguments ->
                 loanApplicationId = arguments.getInt(AppConstant.loanApplicationId)
                 borrowerId = arguments.getInt(AppConstant.borrowerId)
                 borrowerName = arguments.getString(AppConstant.borrowerName)
                 arguments.getInt(AppConstant.incomeId).let {
                     if (it > 0)
                         incomeInfoId = it
                 }
             }

             borrowerName?.let {
               //  toolbarBinding.borrowerPurpose.setText(it)
             }

             if (loanApplicationId != null && borrowerId != null) {
                 toolbarBinding.btnTopDelete.visibility = View.VISIBLE
                 toolbarBinding.btnTopDelete.setOnClickListener {
                     DeleteIncomeDialogFragment.newInstance(AppConstant.income_delete_text).show(childFragmentManager, DeleteCurrentResidenceDialogFragment::class.java.canonicalName)
                 }
             }

             if (incomeInfoId == null || incomeInfoId == 0) {
                 toolbarBinding.btnTopDelete.visibility = View.GONE
                 showHideAddress(false,true)
             }

             findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressData>(
                 AppConstant.address
             )?.observe(viewLifecycleOwner) { result ->
                 businessAddress = result
                 displayAddress(result)
             }

             initViews()
             getData()

             savedViewInstance
         }
    }

    private fun initViews(){
        binding.addBusinessAddress.setOnClickListener {
            openAddressFragment()
        }

        binding.layoutAddress.setOnClickListener(this)
        toolbarBinding.btnClose.setOnClickListener(this)
        binding.mainLayoutBusinessCont.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        setInputFields()
    }

    private fun getData(){
        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                if (borrowerId != null && incomeInfoId != null && incomeInfoId !=null) {
                    binding.loaderSelfEmployment.visibility = View.VISIBLE
                    viewModel.getSelfEmploymentDetail(authToken, borrowerId!!, incomeInfoId!!)

                    viewModel.selfEmploymentDetail.observe(viewLifecycleOwner, { data ->
                        data?.selfEmploymentData?.let { info ->
                            info.businessName?.let {
                                binding.editTextBusinessName.setText(it)
                                CustomMaterialFields.setColor(binding.layoutBusinessName, R.color.grey_color_two, requireContext())
                            }
                            info.businessPhone?.let {
                                binding.editTextBusPhnum.setText(it)
                                CustomMaterialFields.setColor(binding.layoutBusPhnum, R.color.grey_color_two, requireContext())
                            }
                            info.startDate?.let {
                                binding.editTextBstartDate.setText(AppSetting.getFullDate1(it))
                            }
                            info.jobTitle?.let {
                                binding.edJobTitle.setText(it)
                                CustomMaterialFields.setColor(
                                    binding.layoutJobTitle,
                                    R.color.grey_color_two,
                                    requireContext()
                                )
                            }
                            info.annualIncome?.let {
                                binding.edNetIncome.setText(Math.round(it).toString())
                                CustomMaterialFields.setColor(
                                    binding.layoutNetIncome,
                                    R.color.grey_color_two,
                                    requireContext()
                                )
                            }

                            info.businessAddress?.let {
                               businessAddress = it
                                displayAddress(it)
                            } ?:run { showHideAddress(false,true)}
                        }
                        binding.loaderSelfEmployment.visibility = View.GONE
                    })
                }

            }
        }
    }


    private fun openAddressFragment(){
        val addressFragment = AddressBusiness()
        val bundle = Bundle()
        bundle.putString(AppConstant.TOOLBAR_TITLE, getString(R.string.business_main_address))
        bundle.putParcelable(AppConstant.address,businessAddress)
        addressFragment.arguments = bundle
        findNavController().navigate(R.id.action_business_address, addressFragment.arguments)
    }

    private fun displayAddress(it: AddressData){
        if(it.street == null && it.unit == null && it.city==null && it.zipCode==null && it.countryName==null)
            showHideAddress(false,true)
        else {
            val builder = StringBuilder()
            it.street?.let { builder.append(it).append(" ") }
            it.unit?.let { builder.append(it).append("\n") }
            it.city?.let { builder.append(it).append(" ") }
            it.stateName?.let { builder.append(it).append(" ") }
            it.zipCode?.let { builder.append(it) }
            it.countryName?.let { builder.append(" ").append(it) }
            binding.textviewBusinessAddress.text = builder
            showHideAddress(true,false)
        }
    }

    override fun onClick(view: View?) {
        when (view?.getId()) {
            R.id.btn_save_change -> checkValidations()
            R.id.layout_address -> openAddressFragment()
            R.id.btn_close -> findNavController().popBackStack()
            R.id.mainLayout_businessCont -> {
                HideSoftkeyboard.hide(requireActivity(), binding.mainLayoutBusinessCont)
                super.removeFocusFromAllFields(binding.mainLayoutBusinessCont)
            }
        }
    }

    private fun setInputFields() {

        // set lable focus
        binding.editTextBusinessName.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextBusinessName, binding.layoutBusinessName, requireContext()))
        binding.editTextBusPhnum.setOnFocusChangeListener(FocusListenerForPhoneNumber(binding.editTextBusPhnum, binding.layoutBusPhnum,requireContext()))
        binding.edJobTitle.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edJobTitle, binding.layoutJobTitle, requireContext()))
        binding.editTextBstartDate.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextBstartDate, binding.layoutBStartDate, requireContext()))
        binding.edNetIncome.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edNetIncome, binding.layoutNetIncome, requireContext()))

        // set input format
        binding.edNetIncome.addTextChangedListener(NumberTextFormat(binding.edNetIncome))
        binding.editTextBusPhnum.addTextChangedListener(PhoneTextFormatter(binding.editTextBusPhnum, "(###) ###-####"))


        // set Dollar prifix
        CustomMaterialFields.setDollarPrefix(binding.layoutNetIncome, requireContext())

        binding.editTextBstartDate.showSoftInputOnFocus = false
        binding.editTextBstartDate.setOnClickListener { openCalendar() }
        binding.editTextBstartDate.doAfterTextChanged {
            if (binding.editTextBstartDate.text?.length == 0) {
                CustomMaterialFields.setColor(binding.layoutBStartDate,R.color.grey_color_three,requireActivity())
            } else {
                CustomMaterialFields.setColor(binding.layoutBStartDate,R.color.grey_color_two,requireActivity())
                CustomMaterialFields.clearError(binding.layoutBStartDate,requireActivity())
            }
        }
    }

    private fun checkValidations(){

        val businessName: String = binding.editTextBusinessName.text.toString()
        //var jobTitle: String? = binding.edJobTitle.text.toString()
        val startDate: String = binding.editTextBstartDate.text.toString()
        val netIncome: String = binding.edNetIncome.text.toString()

        if (businessName.isEmpty() || businessName.length == 0) {
            CustomMaterialFields.setError(binding.layoutBusinessName, getString(R.string.error_field_required),requireActivity())
        }
        if (startDate.isEmpty() || startDate.length == 0) {
            CustomMaterialFields.setError(binding.layoutBStartDate, getString(R.string.error_field_required),requireActivity())
        }
        if (netIncome.isEmpty() || netIncome.length == 0) {
            CustomMaterialFields.setError(binding.layoutNetIncome, getString(R.string.error_field_required),requireActivity())
        }
        if (businessName.isNotEmpty() || businessName.length > 0) {
            CustomMaterialFields.clearError(binding.layoutBusinessName,requireActivity())
        }

        if (startDate.isNotEmpty() || startDate.length > 0) {
            CustomMaterialFields.clearError(binding.layoutBStartDate,requireActivity())
        }
        if (netIncome.isNotEmpty() || netIncome.length > 0) {
            CustomMaterialFields.clearError(binding.layoutNetIncome,requireActivity())
        }

        if (businessName.length > 0 &&  startDate.length > 0 && netIncome.length > 0 ){
            val businessPhone = if( binding.editTextBusPhnum.text.toString().trim().length >0 ) binding.editTextBusPhnum.text.toString().trim() else null
            val newNetIncome = if(netIncome.length > 0) netIncome.replace(",".toRegex(), "") else null

            val jobTitle = if(binding.edJobTitle.text.toString().length > 0) binding.edJobTitle.text.toString() else null


            val selfEmploymentData = SelfEmploymentData(loanApplicationId=loanApplicationId,borrowerId=borrowerId,id= incomeInfoId,
                businessName = businessName, businessPhone = businessPhone,jobTitle = jobTitle,
                startDate = startDate,businessAddress = businessAddress,annualIncome= newNetIncome?.toDouble())

            lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    if(loanApplicationId != null && borrowerId !=null) {
                        //Log.e("Loan Application Id", "" + loanApplicationId + " borrowerId:  " + borrowerId + " income:  " + incomeInfoId)
                        //Log.e("selfEmployment-snding to API", "" + selfEmploymentData)

                        binding.loaderSelfEmployment.visibility = View.VISIBLE
                        viewModel.sendSelfEmploymentData(authToken, selfEmploymentData)
                    }
                }
            }
        }
    }

    private fun showHideAddress(isShowAddress: Boolean, isAddAddress: Boolean){
        if(isShowAddress){
            binding.layoutAddress.visibility = View.VISIBLE
            binding.addBusinessAddress.visibility = View.GONE
        }
        if(isAddAddress){
            binding.layoutAddress.visibility = View.GONE
            binding.addBusinessAddress.visibility = View.VISIBLE
        }
    }
/*
    private fun openCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val newMonth = month + 1

        val dpd = DatePickerDialog(
            requireActivity(), { view, year, monthOfYear, dayOfMonth -> binding.editTextBstartDate.setText("" + newMonth + "/" + dayOfMonth + "/" + year) },
            year,
            month,
            day)
        dpd.show()
    } */

    var maxDate:Long = 0
    var minDate:Long = 0

    private fun openCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        //val newMonth = month + 1

        /*
        val dpd = DatePickerDialog(
            requireActivity(), {
                view, year, monthOfYear, dayOfMonth -> binding.edStartDate.setText("" + newMonth + "/" + dayOfMonth + "/" + year)
                val cal = Calendar.getInstance()
                cal.set(year, newMonth, dayOfMonth)
                val date = DateFormat.format("dd-MM-yyyy", cal).toString()
                maxDate = convertDateToLong(date)
            }, year, month, day)
         */


        val datePickerDialog = DatePickerDialog(
            requireActivity(), R.style.MySpinnerDatePickerStyle,
            {
                    view, selectedYear, monthOfYear, dayOfMonth ->
                binding.editTextBstartDate.setText("" + (monthOfYear+1) + "/" + dayOfMonth + "/" + selectedYear)
                val cal = Calendar.getInstance()
                cal.set(selectedYear, (monthOfYear), dayOfMonth)
                val date = DateFormat.format("dd-MM-yyyy", cal).toString()
                maxDate = convertDateToLong(date)
            }
            , year, month, day
        )
        if(minDate!=0L)
            datePickerDialog.datePicker.maxDate = minDate
        datePickerDialog.show()

    }

    private fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        return df.parse(date).time
    }

    override fun onResume() {
        super.onResume()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressData>(
            AppConstant.address)?.observe(viewLifecycleOwner) { result ->
            businessAddress = result
            //binding.textviewCurrentEmployerAddress.text = result.street + " " + result.unit + "\n" + result.city + " " + result.stateName + " " + result.zipCode + " " + result.countryName
            displayAddress(result)
        }

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
    fun onSentData(event: SendDataEvent) {
        binding.loaderSelfEmployment.visibility = View.GONE
        if(event.addUpdateDataResponse.code == AppConstant.RESPONSE_CODE_SUCCESS){
            updateMainIncome()
            viewModel.resetChildFragmentToNull()
        }
        else if(event.addUpdateDataResponse.code == AppConstant.INTERNET_ERR_CODE) {
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG)
        } else {
            if (event.addUpdateDataResponse.message != null)
                SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onIncomeDeleteReceived(evt: IncomeDeleteEvent){
        if(evt.isDeleteIncome){
            lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    val call = async{  viewModel.deleteIncome(authToken, incomeInfoId!!, borrowerId!!, loanApplicationId!!) }
                    call.await()
                }
                if (loanApplicationId != null && borrowerId != null && incomeInfoId!! > 0) {
                    viewModel.addUpdateIncomeResponse.observe(viewLifecycleOwner, { genericAddUpdateAssetResponse ->
                        val codeString = genericAddUpdateAssetResponse?.code.toString()
                        if(codeString == "400" || codeString == "200"){
                            updateMainIncome()
                            viewModel.resetChildFragmentToNull()
                        }
                    })
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMainIncomeUpdate(evt: OnUpdateMainIncomeReceived){
        if(evt.isMainIncomeUpdateReceived){
            IncomeTabFragment.isStartIncomeTab = false
            val incomeUpdate = IncomeUpdateInfo(AppConstant.income_self_employment,borrowerId!!)
            findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.income_update,incomeUpdate)
            findNavController().popBackStack()
        }
    }

    private fun updateMainIncome(){
        borrowerApplicationViewModel.resetSingleIncomeTab()
        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                borrowerApplicationViewModel.getSingleTabIncomeDetail(authToken, loanApplicationId!!, borrowerId!!) }
        }
    }

}