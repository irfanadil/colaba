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
import com.rnsoft.colabademo.databinding.IncomeMilitaryPayBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields

import com.rnsoft.colabademo.utils.NumberTextFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by Anita Kiran on 9/15/2021.
 */
@AndroidEntryPoint
class  MilitaryIncomeFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: IncomeMilitaryPayBinding
    private lateinit var toolbarBinding: AppHeaderWithCrossDeleteBinding
    private var savedViewInstance: View? = null
    private val viewModel : IncomeViewModel by activityViewModels()
    private var incomeInfoId :Int? = null
    private var borrowerId :Int? = null
    private var loanApplicationId: Int? = null
    private var borrowerName: String? = null
    private var militaryAddress = AddressData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (savedViewInstance != null) {
            savedViewInstance
        } else {
            binding = IncomeMilitaryPayBinding.inflate(inflater, container, false)
            savedViewInstance = binding.root
            toolbarBinding = binding.headerIncome
            super.addListeners(binding.root)
            // set Header title
            toolbarBinding.toolbarTitle.setText(getString(R.string.military_pay))

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
                toolbarBinding.borrowerPurpose.setText(it)
            }

            /*findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressData>(AppConstant.address)?.observe(viewLifecycleOwner){ result ->
                militaryAddress = result
                displayAddress(result)
            } */

            initViews()
            getData()

            if (loanApplicationId != null && borrowerId != null) {
                toolbarBinding.btnTopDelete.visibility = View.VISIBLE
                toolbarBinding.btnTopDelete.setOnClickListener { DeleteIncomeDialogFragment.newInstance(AppConstant.income_delete_text).show(childFragmentManager,
                        DeleteCurrentResidenceDialogFragment::class.java.canonicalName)
                }
            }

            if (incomeInfoId == null || incomeInfoId == 0) {
                toolbarBinding.btnTopDelete.visibility = View.GONE
                showHideAddress(false,true)
            }

         savedViewInstance

        }
    }

    private fun getData(){

        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                if(borrowerId != null && incomeInfoId != null){
                    binding.loaderMilitary.visibility = View.VISIBLE
                    viewModel.getMilitaryIncome(authToken,borrowerId!!,incomeInfoId!!)

                    viewModel.militaryIncomeData.observe(viewLifecycleOwner, { data ->
                        data?.militaryIncomeData?.let { info ->
                            info.employerName?.let {
                                binding.editTextEmpName.setText(it)
                                CustomMaterialFields.setColor(binding.layoutEmpName, R.color.grey_color_two, requireContext())
                            }
                            info.jobTitle?.let {
                                binding.edJobTitle.setText(it)
                                CustomMaterialFields.setColor(binding.layoutJobTitle, R.color.grey_color_two, requireContext())
                            }
                            info.startDate?.let {
                                binding.edStartDate.setText(AppSetting.getFullDate1(it))
                            }
                            info.jobTitle?.let {
                                binding.edJobTitle.setText(it)
                                CustomMaterialFields.setColor(binding.layoutJobTitle, R.color.grey_color_two, requireContext())
                            }
                            info.yearsInProfession?.let {
                                binding.edProfYears.setText(it.toString())
                                CustomMaterialFields.setColor(binding.layoutYearsProfession, R.color.grey_color_two, requireContext())
                            }
                            info.monthlyBaseSalary?.let {
                                binding.editTextBaseSalary.setText(Math.round(it).toString())
                                CustomMaterialFields.setColor(binding.layoutBaseSalary, R.color.grey_color_two, requireContext())
                            }
                            info.militaryEntitlements?.let {
                                binding.editTextEntitlement.setText(Math.round(it).toString())
                                CustomMaterialFields.setColor(binding.layoutEntitlement, R.color.grey_color_two, requireContext())
                            }
                            info.address?.let {
                                militaryAddress = it
                                displayAddress(it)
                            } ?: run {
                                showHideAddress(false,true) }
                        }
                        binding.loaderMilitary.visibility = View.GONE
                    })
                }
            }
        }
    }

    private fun initViews() {

        binding.addMilitaryAddress.setOnClickListener {
            openAddressFragment()
        }
        binding.layoutAddress.setOnClickListener(this)
        toolbarBinding.btnClose.setOnClickListener(this)
        binding.mainLayoutMilitaryPay.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)

        setInputFields()
    }

    override fun onClick(view: View?) {
        when (view?.getId()) {
            R.id.btn_save_change -> processSendData()
            R.id.layout_address -> openAddressFragment()
            R.id.btn_close -> findNavController().popBackStack()
            R.id.mainLayout_military_pay -> {
                HideSoftkeyboard.hide(requireActivity(),binding.mainLayoutMilitaryPay)
                super.removeFocusFromAllFields(binding.mainLayoutMilitaryPay)
            }
        }
    }

    private fun showHideAddress(isShowAddress: Boolean, isAddAddress: Boolean){
        if(isShowAddress){
            binding.layoutAddress.visibility = View.VISIBLE
            binding.addMilitaryAddress.visibility = View.GONE
        }
        if(isAddAddress){
            binding.layoutAddress.visibility = View.GONE
            binding.addMilitaryAddress.visibility = View.VISIBLE
        }
    }

    private fun openAddressFragment(){
        val addressFragment = AddressMilitaryService()
        val bundle = Bundle()
        bundle.putString(AppConstant.TOOLBAR_TITLE, getString(R.string.service_location_add))
        bundle.putParcelable(AppConstant.address,militaryAddress)
        addressFragment.arguments = bundle
        findNavController().navigate(R.id.action_military_address, addressFragment.arguments)
    }

    private fun setInputFields() {
        // set lable focus
        binding.editTextEmpName.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextEmpName, binding.layoutEmpName, requireContext()))
        binding.edJobTitle.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edJobTitle, binding.layoutJobTitle, requireContext()))
        binding.edProfYears.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edProfYears, binding.layoutYearsProfession, requireContext()))
        binding.editTextBaseSalary.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextBaseSalary, binding.layoutBaseSalary, requireContext()))
        binding.editTextEntitlement.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextEntitlement, binding.layoutEntitlement, requireContext()))

        // set input format
        binding.editTextBaseSalary.addTextChangedListener(NumberTextFormat(binding.editTextBaseSalary))
        binding.editTextEntitlement.addTextChangedListener(NumberTextFormat(binding.editTextEntitlement))

        // set Dollar prifix
        CustomMaterialFields.setDollarPrefix(binding.layoutBaseSalary, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutEntitlement, requireContext())

        // start date
        binding.edStartDate.showSoftInputOnFocus = false
        binding.edStartDate.setOnClickListener { openCalendar() }

        binding.edStartDate.doAfterTextChanged {
            if (binding.edStartDate.text?.length == 0) {
                CustomMaterialFields.setColor(binding.layoutStartDate,R.color.grey_color_three,requireActivity())
            } else {
                CustomMaterialFields.setColor(binding.layoutStartDate,R.color.grey_color_two,requireActivity())
                CustomMaterialFields.clearError(binding.layoutStartDate,requireActivity())
            }
        }

    }

    private fun processSendData(){
        val empName: String = binding.editTextEmpName.text.toString()
        val jobTitle: String = binding.edJobTitle.text.toString()
        val profYears: String = binding.edProfYears.text.toString()
        val startDate: String = binding.edStartDate.text.toString()
        val baseSalary: String = binding.editTextBaseSalary.text.toString()
        val entitlement: String = binding.editTextEntitlement.text.toString()


        if (empName.isEmpty() || empName.length == 0) {
            CustomMaterialFields.setError(binding.layoutEmpName, getString(R.string.error_field_required),requireActivity())
        }
        if (jobTitle.isEmpty() || jobTitle.length == 0) {
            CustomMaterialFields.setError(binding.layoutJobTitle, getString(R.string.error_field_required),requireActivity())
        }
        if (startDate.isEmpty() || startDate.length == 0) {
            CustomMaterialFields.setError(binding.layoutStartDate, getString(R.string.error_field_required),requireActivity())
        }
        if (profYears.isEmpty() || profYears.length == 0) {
            CustomMaterialFields.setError(binding.layoutYearsProfession, getString(R.string.error_field_required),requireActivity())
        }
        if (baseSalary.isEmpty() || baseSalary.length == 0) {
            CustomMaterialFields.setError(binding.layoutBaseSalary, getString(R.string.error_field_required),requireActivity())
        }
        if (entitlement.isEmpty() || entitlement.length == 0) {
            CustomMaterialFields.setError(binding.layoutEntitlement, getString(R.string.error_field_required),requireActivity())
        }
        if (empName.isNotEmpty() || empName.length > 0) {
            CustomMaterialFields.clearError(binding.layoutEmpName,requireActivity())
        }
        if (jobTitle.isNotEmpty() || jobTitle.length > 0) {
            CustomMaterialFields.clearError(binding.layoutJobTitle,requireActivity())
        }
        if (startDate.isNotEmpty() || startDate.length > 0) {
            CustomMaterialFields.clearError(binding.layoutStartDate,requireActivity())
        }
        if (profYears.isNotEmpty() || profYears.length > 0) {
            CustomMaterialFields.clearError(binding.layoutYearsProfession,requireActivity())
        }
        if (baseSalary.isNotEmpty() || baseSalary.length > 0) {
            CustomMaterialFields.clearError(binding.layoutBaseSalary,requireActivity())
        }
        if (entitlement.isNotEmpty() || entitlement.length > 0) {
            CustomMaterialFields.clearError(binding.layoutEntitlement,requireActivity())
        }
        if (empName.length > 0 && jobTitle.length > 0 &&  startDate.length > 0 && profYears.length > 0 && baseSalary.length >0 && entitlement.length>0 ){

            lifecycleScope.launchWhenStarted{
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    if(loanApplicationId != null && borrowerId !=null) {

                        val monthlyBaseSalary = binding.editTextBaseSalary.text.toString().trim()
                        val newBaseSalary = if(monthlyBaseSalary.length > 0) monthlyBaseSalary.replace(",".toRegex(), "") else null

                        val militaryEntitlement = binding.editTextEntitlement.text.toString().trim()
                        val newEntitlement = if(militaryEntitlement.length > 0) militaryEntitlement.replace(",".toRegex(), "") else null
                        
                        val data = MilitaryIncomeData(
                            loanApplicationId = loanApplicationId,borrowerId= borrowerId, employerName = empName, jobTitle= jobTitle,yearsInProfession= profYears.toInt(),
                            startDate=startDate,monthlyBaseSalary= newBaseSalary?.toDouble(),address = militaryAddress,id=incomeInfoId,militaryEntitlements = newEntitlement?.toDouble())

                       // Log.e("businessDate-snding to API", "" + data)

                        binding.loaderMilitary.visibility = View.VISIBLE
                        viewModel.sendMilitaryIncomeData (authToken,data)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressData>(
            AppConstant.address)?.observe(viewLifecycleOwner) { result ->
            militaryAddress = result
            displayAddress(result)
        }
    }

    private fun displayAddress(it: AddressData){
        if(it.street == null && it.unit == null && it.city==null && it.zipCode==null && it.countryName==null)
            showHideAddress(false,true)
        else {
            val builder = StringBuilder()
            it.street?.let {
                if(it != "null") builder.append(it).append(" ") }
            it.unit?.let {
                if(it != "null") builder.append(it).append(",") } ?: run { builder.append(",") }
            it.city?.let {
                if(it != "null") builder.append("\n").append(it).append(",").append(" ") } ?: run { builder.append("\n") }
            it.stateName?.let {
                if(it !="null") builder.append(it).append(" ") }
            it.zipCode?.let {
                if(it != "null") builder.append(it) }
            binding.textviewMilitaryAddress.text = builder
            showHideAddress(true,false)
        }
    }

   /* private fun openCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val newMonth = month + 1

        val dpd = DatePickerDialog(
            requireActivity(),
            { view, year, monthOfYear, dayOfMonth -> binding.edStartDate.setText("" + newMonth + "/" + dayOfMonth + "/" + year) },
            year,
            month,
            day
        )
        dpd.show()
    } */

    private val borrowerApplicationViewModel: BorrowerApplicationViewModel by activityViewModels()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSentData(event: SendDataEvent) {
        binding.loaderMilitary.visibility = View.GONE
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
            val incomeUpdate = IncomeUpdateInfo(AppConstant.income_military,borrowerId!!)
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


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)

    }

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
                binding.edStartDate.setText("" + (monthOfYear+1) + "/" + dayOfMonth + "/" + selectedYear)
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

    private fun endDateCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        //val newMonth = month + 1

        /*
        val dpd = DatePickerDialog(
            requireActivity(),
            { view, year, monthOfYear, dayOfMonth -> binding.edEndDate.setText("" + newMonth + "/" + dayOfMonth + "/" + year) },
            year,
            month,
            day
        )
        dpd.show()
         */

        val datePickerDialog = DatePickerDialog(
            requireActivity(), R.style.MySpinnerDatePickerStyle,
            {
                    view, selectedYear, monthOfYear, dayOfMonth ->
                binding.edStartDate.setText("" + (monthOfYear+1) + "/" + dayOfMonth + "/" + selectedYear)
                val cal = Calendar.getInstance()
                cal.set(selectedYear, monthOfYear, dayOfMonth)
                val date = DateFormat.format("dd-MM-yyyy", cal).toString()
                minDate = convertDateToLong(date)
            }
            , year, month, day
        )
        if(maxDate!=0L)
            datePickerDialog.datePicker.minDate = maxDate
        datePickerDialog.show()


    }
}