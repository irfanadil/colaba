package com.rnsoft.colabademo

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.activities.addresses.info.fragment.DeleteCurrentResidenceDialogFragment

import com.rnsoft.colabademo.databinding.AppHeaderWithCrossDeleteBinding
import com.rnsoft.colabademo.databinding.IncomePreviousEmploymentBinding
import com.rnsoft.colabademo.databinding.StockBondsLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields

import com.rnsoft.colabademo.utils.NumberTextFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import okhttp3.internal.wait
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by Anita Kiran on 9/13/2021.
 */
@AndroidEntryPoint
class IncomePreviousEmployment : BaseFragment(),View.OnClickListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: IncomePreviousEmploymentBinding
    private lateinit var toolbar: AppHeaderWithCrossDeleteBinding
    private var savedViewInstance: View? = null
    private val viewModel : IncomeViewModel by activityViewModels()
    private var loanApplicationId: Int? = null
    private var incomeInfoId :Int? = null
    private var borrowerId :Int? = null
    private var borrowerName: String? = null
    private var employerAddress = AddressData()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (savedViewInstance != null) {
            savedViewInstance
        } else {
            binding = IncomePreviousEmploymentBinding.inflate(inflater, container, false)
            savedViewInstance = binding.root
            super.addListeners(binding.root)
            // set Header title
            toolbar = binding.headerIncome
            toolbar.toolbarTitle.setText(getString(R.string.previous_employment))

            arguments?.let { arguments ->
                loanApplicationId = arguments.getInt(AppConstant.loanApplicationId)
                borrowerId = arguments.getInt(AppConstant.borrowerId)
                incomeInfoId = arguments.getInt(AppConstant.incomeId)
                borrowerName = arguments.getString(AppConstant.borrowerName)
            }
            //Log.e("Current Employment-oncreate","Loan Application Id " +loanApplicationId + " borrowerId:  " + borrowerId + " incomeInfoId" + incomeInfoId)
            borrowerName?.let {
               // toolbar.borrowerPurpose.setText(it)
            }

            initViews()
            getEmploymentData()

            if (loanApplicationId != null && borrowerId != null) {
                toolbar.btnTopDelete.visibility = View.VISIBLE
                toolbar.btnTopDelete.setOnClickListener {
                    DeleteIncomeDialogFragment.newInstance(AppConstant.income_delete_text).show(
                        childFragmentManager,
                        DeleteCurrentResidenceDialogFragment::class.java.canonicalName
                    )
                }
            }

            if (incomeInfoId == null || incomeInfoId == 0) {
                toolbar.btnTopDelete.visibility = View.GONE
                showHideAddress(false,true)
            }

            savedViewInstance
        }
    }

    private fun getEmploymentData(){

        if (loanApplicationId != null && incomeInfoId!! > 0  && borrowerId != null) {

            lifecycleScope.launchWhenCreated {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                   // Log.e("getting details", "" + loanApplicationId + " borrowerId:  " + borrowerId + " incomeInfoId: " + incomeInfoId)
                    binding.loaderEmployment.visibility = View.VISIBLE
                    viewModel.getPrevEmploymentDetail(authToken, loanApplicationId!!, borrowerId!!, incomeInfoId!!)
                }
            }
            viewModel.prevEmploymentDetail.observe(viewLifecycleOwner, { data ->

                data?.employmentData?.employmentInfo.let { info ->
                    info?.employerName?.let { binding.editTextEmpName.setText(it)
                        CustomMaterialFields.setColor(binding.layoutEmpName, R.color.grey_color_two, requireContext())
                    }
                    info?.employerPhoneNumber?.let {
                        binding.editTextEmpPhnum.setText(it)
                        CustomMaterialFields.setColor(binding.layoutEmpPhnum, R.color.grey_color_two, requireContext())
                    }
                    info?.jobTitle?.let {
                        binding.editTextJobTitle.setText(it)
                        CustomMaterialFields.setColor(binding.layoutJobTitle, R.color.grey_color_two, requireContext())
                    }
                    info?.startDate?.let {
                        binding.editTextStartDate.setText(AppSetting.getFullDate1(it))
                    }
                    info?.endDate?.let {
                        binding.editTextEndDate.setText(AppSetting.getFullDate1(it))
                    }
                    info?.yearsInProfession?.let {
                        binding.editTextProfYears.setText(it.toString())
                        CustomMaterialFields.setColor(
                            binding.layoutYearsProfession,
                            R.color.grey_color_two,
                            requireContext()
                        )
                    }

                    data?.employmentData?.wayOfIncome?.let {
                        it.employerAnnualSalary?.let {
                            binding.editTextAnnualIncome.setText(
                                Math.round(it).toString()
                            )
                            CustomMaterialFields.setColor(
                                binding.layoutNetIncome,
                                R.color.grey_color_two,
                                requireContext()
                            )
                        }
                    }

                    data?.employmentData?.employerAddress?.let {
                        employerAddress = it
                        displayAddress(it)
                    } ?: run {
                        showHideAddress(false,true) }

                    info?.hasOwnershipInterest?.let {
                        if (it == true) {
                            binding.rbOwnershipYes.isChecked = true
                            binding.layoutOwnershipPercentage.visibility = View.VISIBLE
                            info.ownershipInterest?.let { percentage ->
                                binding.edOwnershipPercent.setText(
                                    Math.round(percentage).toString()
                                )
                                CustomMaterialFields.setColor(
                                    binding.layoutOwnershipPercentage,
                                    R.color.grey_color_two,
                                    requireContext()
                                )
                            }
                        } else {
                            binding.rbOwnershipNo.isChecked = true
                            binding.layoutOwnershipPercentage.visibility = View.GONE
                        }
                    }
                }

                binding.loaderEmployment.visibility = View.GONE
            })
        }
    }

    private fun initViews() {
        binding.addEmployerAddress.setOnClickListener {
            openAddressFragment()
        }

        binding.rbOwnershipYes.setOnClickListener(this)
        binding.rbOwnershipNo.setOnClickListener(this)
        binding.layoutAddress.setOnClickListener(this)
        toolbar.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.mainLayoutPrevEmployment.setOnClickListener(this)

        setInputFields()
    }

    override fun onClick(view: View?) {
        when (view?.getId()) {
            R.id.btn_save_change -> processSendData()
            R.id.rb_ownership_yes -> ownershipInterest()
            R.id.rb_ownership_no -> ownershipInterest()
            R.id.layout_address -> openAddressFragment() //findNavController().navigate(R.id.action_address)
            R.id.btn_close -> findNavController().popBackStack()
            R.id.mainLayout_prev_employment ->  {
                HideSoftkeyboard.hide(requireActivity(),binding.mainLayoutPrevEmployment)
                super.removeFocusFromAllFields(binding.mainLayoutPrevEmployment)
            }
        }
    }

    private fun processSendData(){
        var isDataEntered : Boolean = false
        var ownershipPercentage: String?= null
        val empName: String = binding.editTextEmpName.text.toString()
        val startDate: String = binding.editTextStartDate.text.toString()
        val endDate: String = binding.editTextEndDate.text.toString()
        val netIncome: String = binding.editTextAnnualIncome.text.toString()

        if (empName.isEmpty() || empName.length == 0) {
            isDataEntered = false
            CustomMaterialFields.setError(binding.layoutEmpName, getString(R.string.error_field_required),requireActivity())
        }
        if (startDate.isEmpty() || startDate.length == 0) {
            isDataEntered = false
            CustomMaterialFields.setError(binding.layoutStartDate, getString(R.string.error_field_required),requireActivity())
        }
        if (endDate.isEmpty() || endDate.length == 0) {
            isDataEntered = false
            CustomMaterialFields.setError(binding.layoutEndDate, getString(R.string.error_field_required),requireActivity())
        }

        if (netIncome.isEmpty() || netIncome.length == 0) {
            isDataEntered = false
            CustomMaterialFields.setError(binding.layoutNetIncome, getString(R.string.error_field_required),requireActivity())
        }
        if (empName.isNotEmpty() || empName.length > 0) {
            isDataEntered = true
            CustomMaterialFields.clearError(binding.layoutEmpName,requireActivity())
        }
        if (startDate.isNotEmpty() || startDate.length > 0) {
            isDataEntered = true
            CustomMaterialFields.clearError(binding.layoutStartDate,requireActivity())
        }
        if (endDate.isNotEmpty() || endDate.length > 0) {
            isDataEntered = true
            CustomMaterialFields.clearError(binding.layoutEndDate,requireActivity())
        }
        if (netIncome.isNotEmpty() || netIncome.length > 0) {
            isDataEntered = true
            CustomMaterialFields.clearError(binding.layoutNetIncome,requireActivity())
        }
        if (binding.layoutOwnershipPercentage.isVisible) {
            ownershipPercentage = binding.edOwnershipPercent.text.toString()

            if (ownershipPercentage.length == 0) {
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutOwnershipPercentage, getString(R.string.error_field_required), requireActivity())
            }
            if (ownershipPercentage.length > 0) {
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutOwnershipPercentage, requireActivity())
            }
        }

        if(isDataEntered){

            lifecycleScope.launchWhenStarted{
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    if(loanApplicationId != null && borrowerId !=null) {
                        Log.e("sending", "" +loanApplicationId + " borrowerId:  " + borrowerId+ " incomeInfoId: " + incomeInfoId)

                        val phoneNum = if(binding.editTextEmpPhnum.text.toString().length > 0) binding.editTextEmpPhnum.text.toString() else null
                        var isOwnershipInterest : Boolean ? = null
                        var ownershipPercentage : String? = null
                        if(binding.rbOwnershipYes.isChecked) {
                            isOwnershipInterest = true
                            ownershipPercentage = if(binding.edOwnershipPercent.text.toString().length > 0) binding.edOwnershipPercent.text.toString() else null
                        }
                        if(binding.rbOwnershipNo.isChecked) {
                            isOwnershipInterest = false
                            binding.edOwnershipPercent.setText("")
                        }

                        val profYears = if(binding.editTextProfYears.text.toString().length >0) binding.editTextProfYears.text.toString() else null
                        val jobTitle = if(binding.editTextJobTitle.text.toString().length >0) binding.editTextJobTitle.text.toString() else null

                        val employerInfo = PrevEmploymentInfo(
                            employerName=empName, employerPhoneNumber=phoneNum, jobTitle=jobTitle,startDate=startDate,endDate =endDate, yearsInProfession = profYears?.toInt(),
                            hasOwnershipInterest = isOwnershipInterest, ownershipInterest = ownershipPercentage?.toDouble(),incomeInfoId = incomeInfoId)

                        val annualIncome = binding.editTextAnnualIncome.text.toString().trim()
                        val newAnnualIncome = if(annualIncome.length > 0) annualIncome.replace(",".toRegex(), "") else null

                        val wayOfIncome = WayOfIncomePrevious(employerAnnualSalary=newAnnualIncome?.toDouble())

                        val employmentData = PreviousEmploymentData(
                            loanApplicationId = loanApplicationId,borrowerId= borrowerId, employmentInfo=employerInfo, employerAddress= employerAddress,wayOfIncome = wayOfIncome)
                        Log.e("employmentData-snding to API", "" + employmentData)

                        binding.loaderEmployment.visibility = View.VISIBLE
                        viewModel.sendPrevEmploymentData(authToken, employmentData)
                    }
                }
            }

        }
    }



    private fun setInputFields() {

        // set lable focus
        binding.editTextEmpName.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextEmpName, binding.layoutEmpName, requireContext()))
        binding.editTextEmpPhnum.setOnFocusChangeListener(FocusListenerForPhoneNumber(binding.editTextEmpPhnum, binding.layoutEmpPhnum,requireContext()))
        binding.editTextJobTitle.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextJobTitle, binding.layoutJobTitle, requireContext()))
        binding.editTextProfYears.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextProfYears, binding.layoutYearsProfession, requireContext()))
        binding.edOwnershipPercent.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edOwnershipPercent, binding.layoutOwnershipPercentage, requireContext()))
        binding.editTextAnnualIncome.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextAnnualIncome, binding.layoutNetIncome, requireContext()))

        // set input format
        binding.editTextAnnualIncome.addTextChangedListener(NumberTextFormat(binding.editTextAnnualIncome))
        binding.editTextEmpPhnum.addTextChangedListener(PhoneTextFormatter(binding.editTextEmpPhnum, "(###) ###-####"))


        // set Dollar prifix
        CustomMaterialFields.setPercentagePrefix(binding.layoutOwnershipPercentage, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutNetIncome, requireContext())
        CustomMaterialFields.setPercentagePrefix(binding.layoutOwnershipPercentage, requireContext())

        // start date
        binding.editTextStartDate.showSoftInputOnFocus = false
        binding.editTextStartDate.setOnClickListener { openCalendar() }
        binding.editTextStartDate.doAfterTextChanged {
            if (binding.editTextStartDate.text?.length == 0) {
                CustomMaterialFields.setColor(binding.layoutStartDate,R.color.grey_color_three,requireActivity())
            } else {
                CustomMaterialFields.setColor(binding.layoutStartDate,R.color.grey_color_two,requireActivity())
                CustomMaterialFields.clearError(binding.layoutStartDate,requireActivity())
            }
        }

        // end date
        binding.editTextEndDate.showSoftInputOnFocus = false
        binding.editTextEndDate.setOnClickListener { endDateCalendar() }
        binding.editTextEndDate.doAfterTextChanged {
            if (binding.editTextEndDate.text?.length == 0) {
                CustomMaterialFields.setColor(binding.layoutEndDate,R.color.grey_color_three,requireActivity())
            } else {
                CustomMaterialFields.setColor(binding.layoutEndDate,R.color.grey_color_two,requireActivity())
                CustomMaterialFields.clearError(binding.layoutEndDate,requireActivity())
            }
        }
    }

    private fun showHideAddress(isShowAddress: Boolean, isAddAddress: Boolean){
        if(isShowAddress){
            binding.layoutAddress.visibility = View.VISIBLE
            binding.addEmployerAddress.visibility = View.GONE
        }
        if(isAddAddress){
            binding.layoutAddress.visibility = View.GONE
            binding.addEmployerAddress.visibility = View.VISIBLE
        }
    }

    private fun ownershipInterest(){
        if(binding.rbOwnershipYes.isChecked) {
            binding.rbOwnershipYes.setTypeface(null, Typeface.BOLD)
            binding.rbOwnershipNo.setTypeface(null, Typeface.NORMAL)
            binding.layoutOwnershipPercentage.visibility = View.VISIBLE

        }
        else {
            binding.rbOwnershipNo.setTypeface(null, Typeface.BOLD)
            binding.rbOwnershipYes.setTypeface(null, Typeface.NORMAL)
            binding.layoutOwnershipPercentage.visibility = View.GONE

        }
    }

    private fun openAddressFragment(){
        val addressFragment = AddressPrevEmployment()
        val bundle = Bundle()
        bundle.putString(AppConstant.TOOLBAR_TITLE, getString(R.string.previous_employer_address))
        bundle.putParcelable(AppConstant.address,employerAddress)
        addressFragment.arguments = bundle
        findNavController().navigate(R.id.action_prev_employment_address, addressFragment.arguments)
    }

    override fun onResume() {
        super.onResume()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressData>(
            AppConstant.address)?.observe(viewLifecycleOwner) { result -> employerAddress = result
            //binding.textviewCurrentEmployerAddress.text = result.street + " " + result.unit + "\n" + result.city + " " + result.stateName + " " + result.zipCode + " " + result.countryName
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
            binding.textviewPrevEmploymentAddress.text = builder
            showHideAddress(true,false)
        }
    }

    override fun onStart(){
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop(){
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private val borrowerApplicationViewModel: BorrowerApplicationViewModel by activityViewModels()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSentData(event: SendDataEvent) {
        binding.loaderEmployment.visibility = View.GONE
        if(event.addUpdateDataResponse.code == AppConstant.RESPONSE_CODE_SUCCESS){
            viewModel.resetChildFragmentToNull()
            updateMainIncome()
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
            val incomeUpdate = IncomeUpdateInfo(AppConstant.income_employment,borrowerId!!)
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
                binding.editTextStartDate.setText("" + (monthOfYear+1) + "/" + dayOfMonth + "/" + selectedYear)
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

        //Date().time = cal.time
        // dpd.show()
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
                binding.editTextEndDate.setText("" + (monthOfYear+1) + "/" + dayOfMonth + "/" + selectedYear)
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

    /* private fun processSendData(){
        val empName: String = binding.editTextEmpName.text.toString()
        val jobTitle: String = binding.editTextJobTitle.text.toString()
        val startDate: String = binding.editTextStartDate.text.toString()
        val endDate: String = binding.editTextEndDate.text.toString()
        val profYears: String = binding.editTextProfYears.text.toString()
        val netIncome: String = binding.editTextAnnualIncome.text.toString()

        if (empName.isEmpty() || empName.length == 0) {
            CustomMaterialFields.setError(binding.layoutEmpName, getString(R.string.error_field_required),requireActivity())
        }
        if (jobTitle.isEmpty() || jobTitle.length == 0) {
            CustomMaterialFields.setError(binding.layoutJobTitle, getString(R.string.error_field_required),requireActivity())
        }
        if (startDate.isEmpty() || startDate.length == 0) {
            CustomMaterialFields.setError(binding.layoutStartDate, getString(R.string.error_field_required),requireActivity())
        }
        if (endDate.isEmpty() || endDate.length == 0) {
            CustomMaterialFields.setError(binding.layoutEndDate, getString(R.string.error_field_required),requireActivity())
        }
        if (profYears.isEmpty() || profYears.length == 0) {
            CustomMaterialFields.setError(binding.layoutYearsProfession, getString(R.string.error_field_required),requireActivity())
        }
        if (netIncome.isEmpty() || netIncome.length == 0) {
            CustomMaterialFields.setError(binding.layoutNetIncome, getString(R.string.error_field_required),requireActivity())
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
        if (endDate.isNotEmpty() || endDate.length > 0) {
            CustomMaterialFields.clearError(binding.layoutEndDate,requireActivity())
        }
        if (netIncome.isNotEmpty() || netIncome.length > 0) {
            CustomMaterialFields.clearError(binding.layoutNetIncome,requireActivity())
        }
        if (profYears.isNotEmpty() || profYears.length > 0) {
            CustomMaterialFields.clearError(binding.layoutYearsProfession,requireActivity())
        }
        if (empName.length > 0 && jobTitle.length > 0 &&  startDate.length > 0 && endDate.length > 0 && profYears.length > 0 && netIncome.length > 0){

            lifecycleScope.launchWhenStarted{
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    if(loanApplicationId != null && borrowerId !=null) {
                        //Log.e("sending", "" +loanApplicationId + " borrowerId:  " + borrowerId+ " incomeInfoId: " + incomeInfoId)

                        val phoneNum = if(binding.editTextEmpPhnum.text.toString().length > 0) binding.editTextEmpPhnum.text.toString() else null
                        var isOwnershipInterest : Boolean ? = null
                        var ownershipPercentage : String? = null
                        if(binding.rbOwnershipYes.isChecked) {
                            isOwnershipInterest = true
                            ownershipPercentage = if(binding.edOwnershipPercent.text.toString().length > 0) binding.edOwnershipPercent.text.toString() else null
                        }

                        if(binding.rbOwnershipNo.isChecked) {
                            isOwnershipInterest = false
                            binding.edOwnershipPercent.setText("")
                        }


                        val employerInfo = PrevEmploymentInfo(
                            employerName=empName, employerPhoneNumber=phoneNum, jobTitle=jobTitle,startDate=startDate,endDate =endDate, yearsInProfession = profYears.toInt(),
                            hasOwnershipInterest = isOwnershipInterest, ownershipInterest = ownershipPercentage?.toDouble(),incomeInfoId = incomeInfoId)

                        val annualIncome = binding.editTextAnnualIncome.text.toString().trim()
                        val newAnnualIncome = if(annualIncome.length > 0) annualIncome.replace(",".toRegex(), "") else null

                        val wayOfIncome = WayOfIncomePrevious(employerAnnualSalary=newAnnualIncome?.toDouble())

                        val employmentData = PreviousEmploymentData(
                            loanApplicationId = loanApplicationId,borrowerId= borrowerId, employmentInfo=employerInfo, employerAddress= employerAddress,wayOfIncome = wayOfIncome)
                        Log.e("employmentData-snding to API", "" + employmentData)

                        binding.loaderEmployment.visibility = View.VISIBLE
                        viewModel.sendPrevEmploymentData(authToken, employmentData)
                    }
                }
            }

        }
    } */

}