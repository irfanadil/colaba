package com.rnsoft.colabademo

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.activities.addresses.info.fragment.DeleteCurrentResidenceDialogFragment

import com.rnsoft.colabademo.databinding.AppHeaderWithCrossDeleteBinding
import com.rnsoft.colabademo.databinding.IncomeBusinessLayoutBinding
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
class BusinessIncomeFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: IncomeBusinessLayoutBinding
    private lateinit var toolbarBinding: AppHeaderWithCrossDeleteBinding
    private var savedViewInstance: View? = null
    //private val businessTypeArray = listOf("Partnership (e.g. LLC, LP, or GP","Corporation (e.g. C-Corp, S-Corp, or LLC")
    private val viewModel : IncomeViewModel by activityViewModels()
    var incomeInfoId :Int? = null
    var borrowerId :Int? = null
    private var loanApplicationId: Int? = null
    private var businessTypes: ArrayList<DropDownResponse> = arrayListOf()
    private var businessAddress = AddressData()
    private var borrowerName: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
          return if (savedViewInstance != null){
            savedViewInstance
        } else {
            binding = IncomeBusinessLayoutBinding.inflate(inflater, container, false)
              savedViewInstance = binding.root
              toolbarBinding = binding.headerIncome
              super.addListeners(binding.root)

              // set Header title
              toolbarBinding.toolbarTitle.setText(getString(R.string.business))

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
                  //toolbarBinding.borrowerPurpose.setText(it)
              }

              initViews()
              observeBusinesstIncomeTypes()

              if (loanApplicationId != null && borrowerId != null){
                  toolbarBinding.btnTopDelete.visibility = View.VISIBLE
                  toolbarBinding.btnTopDelete.setOnClickListener {
                      DeleteIncomeDialogFragment.newInstance(AppConstant.income_delete_text).show(childFragmentManager, DeleteCurrentResidenceDialogFragment::class.java.canonicalName)
                  }
              }

              if (incomeInfoId == null || incomeInfoId == 0) {
                  toolbarBinding.btnTopDelete.visibility = View.GONE
                  showHideAddress(false,true)
              }

              savedViewInstance
          }
    }


    private fun initViews(){

        binding.addBusinessAddress.setOnClickListener {
            openAddressFragment()
        }

        binding.layoutAddress.setOnClickListener(this)
        toolbarBinding.btnClose.setOnClickListener(this)
        binding.mainLayoutBusiness.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)

        setInputFields()
        setBusinessType()
        observeBusinesstIncomeTypes()

    }

    private fun observeBusinesstIncomeTypes(){
        lifecycleScope.launchWhenStarted {
            viewModel.businessTypes.observe(viewLifecycleOwner, { types ->
                if(types.size > 0) {
                    val itemList:ArrayList<String> = arrayListOf()
                    businessTypes = arrayListOf()
                    for (item in types) {
                        itemList.add(item.name)
                        businessTypes.add(item)
                    }
                    //Timber.e("itemList- $itemList")
                    //Timber.e("BusinessTypes- $businessTypes")
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
                    binding.tvBusinessType.setAdapter(adapter)
                }
                else
                    findNavController().popBackStack()

                getBusinessDetails()

            })
        }

    }

    private fun getBusinessDetails(){
        //Timber.e( "borrowerId:  " + borrowerId + "incomeInfoId: " + incomeInfoId )
        lifecycleScope.launchWhenStarted{
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                if (borrowerId != null && incomeInfoId != null && incomeInfoId !=null) {
                    binding.loaderIncomeBusiness.visibility = View.VISIBLE
                    viewModel.getIncomeBusiness(authToken, borrowerId!!, incomeInfoId!!)

                    viewModel.businessIncome.observe(viewLifecycleOwner, { data ->
                        data?.businessData?.let { info ->
                            info.businessName?.let {
                                binding.edBusinessName.setText(it)
                                CustomMaterialFields.setColor(binding.layoutBusinessName, R.color.grey_color_two, requireContext())
                            }
                            info.businessPhone?.let {
                                binding.edBusPhoneNum.setText(it)
                                CustomMaterialFields.setColor(binding.layoutBusPhnum, R.color.grey_color_two, requireContext())
                            }
                            info.startDate?.let {
                                binding.edBstartDate.setText(AppSetting.getFullDate1(it))
                            }
                            info.jobTitle?.let {
                                binding.edJobTitle.setText(it)
                                CustomMaterialFields.setColor(binding.layoutJobTitle, R.color.grey_color_two, requireContext())
                            }
                            info.ownershipPercentage?.let {
                                binding.edOwnershipPercent.setText(Math.round(it).toString())
                                CustomMaterialFields.setColor(binding.layoutOwnershipPercentage, R.color.grey_color_two, requireContext())
                            }
                            info.annualIncome?.let {
                                binding.editTextAnnualIncome.setText(Math.round(it).toString())
                                CustomMaterialFields.setColor(binding.layoutNetIncome, R.color.grey_color_two, requireContext())
                            }

                            info.address?.let {
                                businessAddress = it
                                displayAddress(it)
                            } ?: run {showHideAddress(false,true) }

                            info.incomeTypeId?.let { id ->
                                for(item in businessTypes)
                                    if(id == item.id){
                                        binding.tvBusinessType.setText(item.name, false)
                                        CustomMaterialFields.setColor(binding.layoutBusinessType, R.color.grey_color_two, requireActivity())
                                        break
                                    }
                            }
                        }
                        binding.loaderIncomeBusiness.visibility = View.GONE
                    })

                }
            }
        }
    }

    override fun onClick(view: View?){
        when (view?.getId()) {
            R.id.btn_save_change -> processSendData()
            R.id.layout_address -> openAddressFragment()
            R.id.btn_close -> findNavController().popBackStack()
            R.id.mainLayout_business -> {
                HideSoftkeyboard.hide(requireActivity(),binding.mainLayoutBusiness)
                super.removeFocusFromAllFields(binding.mainLayoutBusiness)
            }
        }
    }

    private fun processSendData(){
        val businessType: String = binding.tvBusinessType.text.toString()
        val businessName: String = binding.edBusinessName.text.toString()
        val startDate: String = binding.edBstartDate.text.toString()
        val percentage: String = binding.edOwnershipPercent.text.toString()
        val netIncome: String = binding.editTextAnnualIncome.text.toString()

        if (businessType.isEmpty() || businessType.length == 0) {
            CustomMaterialFields.setError(binding.layoutBusinessType, getString(R.string.error_field_required),requireActivity())
        }
        if (businessName.isEmpty() || businessName.length == 0) {
            CustomMaterialFields.setError(binding.layoutBusinessName, getString(R.string.error_field_required),requireActivity())
        }
        if (startDate.isEmpty() || startDate.length == 0) {
            CustomMaterialFields.setError(binding.layoutBStartDate, getString(R.string.error_field_required),requireActivity())
        }
        if (netIncome.isEmpty() || netIncome.length == 0) {
            CustomMaterialFields.setError(binding.layoutNetIncome, getString(R.string.error_field_required),requireActivity())
        }
        if (percentage.isEmpty() || percentage.length == 0) {
            CustomMaterialFields.setError(binding.layoutOwnershipPercentage, getString(R.string.error_field_required),requireActivity())
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
        if (percentage.isNotEmpty() || percentage.length > 0) {
            CustomMaterialFields.clearError(binding.layoutOwnershipPercentage,requireActivity())
        }
        if (businessType.length > 0 && businessName.length > 0  &&  startDate.length > 0 && netIncome.length > 0  && percentage.length > 0){

            lifecycleScope.launchWhenStarted{
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    if(loanApplicationId != null && borrowerId !=null) {
                        //Log.e("sending", "" +loanApplicationId + " borrowerId:  " + borrowerId+ " incomeInfoId: " + incomeInfoId)
                        // get business type id
                        val type : String = binding.tvBusinessType.getText().toString().trim()
                        val matchedType =  businessTypes.filter { p -> p.name.equals(type,true)}
                        val businessTypeId = if(matchedType.size > 0) matchedType.map { matchedType.get(0).id }.single() else null
                        //Log.e("businesId",""+businessTypeId)
                        val jobTitle = if(binding.edJobTitle.text.toString().length > 0) binding.edJobTitle.text.toString() else null
                        val phoneNum = if(binding.edBusPhoneNum.text.toString().length > 0) binding.edBusPhoneNum.text.toString() else null
                        val ownershipPercentage = if(binding.edOwnershipPercent.text.toString().length > 0) binding.edOwnershipPercent.text.toString() else null
                        val annualIncome = binding.editTextAnnualIncome.text.toString().trim()
                        val newAnnualIncome = if(annualIncome.length > 0) annualIncome.replace(",".toRegex(), "") else null

                        val businessData = BusinessData(
                            loanApplicationId = loanApplicationId,borrowerId= borrowerId,businessName=businessName,businessPhone=phoneNum,startDate=startDate,
                            jobTitle = jobTitle,ownershipPercentage = ownershipPercentage?.toDouble(),annualIncome=newAnnualIncome?.toDouble(),address = businessAddress,id=incomeInfoId,
                            incomeTypeId =businessTypeId)

                        Log.e("businessDate-snding to API", "" + businessData)

                        binding.loaderIncomeBusiness.visibility = View.VISIBLE
                        viewModel.sendBusinessData(authToken, businessData)
                    }
                }
            }
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
            binding.textviewBusinessAddress.text = builder
            showHideAddress(true,false)
        }
    }

    private fun openAddressFragment(){
        val addressFragment = AddressBusiness()
        val bundle = Bundle()
        bundle.putString(AppConstant.TOOLBAR_TITLE, getString(R.string.business_main_address))
        bundle.putParcelable(AppConstant.address,businessAddress)
        addressFragment.arguments = bundle
        findNavController().navigate(R.id.action_address, addressFragment.arguments)
    }

    private fun setInputFields() {

        binding.edBusinessName.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edBusinessName, binding.layoutBusinessName, requireContext()))
        binding.edBusPhoneNum.setOnFocusChangeListener(FocusListenerForPhoneNumber(binding.edBusPhoneNum, binding.layoutBusPhnum,requireContext()))
        binding.edBstartDate.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edBstartDate, binding.layoutBStartDate, requireContext()))
        binding.edJobTitle.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edJobTitle, binding.layoutJobTitle, requireContext()))
        binding.editTextAnnualIncome.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextAnnualIncome, binding.layoutNetIncome, requireContext()))
        binding.edOwnershipPercent.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edOwnershipPercent, binding.layoutOwnershipPercentage, requireContext()))


        // set input format
        binding.editTextAnnualIncome.addTextChangedListener(NumberTextFormat(binding.editTextAnnualIncome))
        binding.edBusPhoneNum.addTextChangedListener(PhoneTextFormatter(binding.edBusPhoneNum, "(###) ###-####"))


        // set Dollar prifix
        CustomMaterialFields.setDollarPrefix(binding.layoutNetIncome, requireContext())
        CustomMaterialFields.setPercentagePrefix(binding.layoutOwnershipPercentage, requireContext())

       // start date
        binding.edBstartDate.showSoftInputOnFocus = false
        binding.edBstartDate.setOnClickListener { openCalendar() }

        binding.edBstartDate.doAfterTextChanged {
            if (binding.edBstartDate.text?.length == 0) {
                CustomMaterialFields.setColor(binding.layoutBStartDate,R.color.grey_color_three,requireActivity())
            } else {
                CustomMaterialFields.setColor(binding.layoutBStartDate,R.color.grey_color_two,requireActivity())
                CustomMaterialFields.clearError(binding.layoutBStartDate,requireActivity())
            }
        }

    }

    private fun setBusinessType(){
        //val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, businessTypeArray)
        //binding.tvBusinessType.setAdapter(adapter)
        binding.tvBusinessType.setOnFocusChangeListener { _, _ ->
            binding.tvBusinessType.showDropDown()
        }
        binding.tvBusinessType.setOnClickListener {
            binding.tvBusinessType.showDropDown()
        }
        binding.tvBusinessType.onItemClickListener = object :
            AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                binding.layoutBusinessType.defaultHintTextColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(), R.color.grey_color_two
                    )
                )

                if (binding.tvBusinessType.text.isNotEmpty() && binding.tvBusinessType.text.isNotBlank()) {
                    CustomMaterialFields.clearError(binding.layoutBusinessType,requireActivity())
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

    override fun onResume(){
        super.onResume()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressData>(
            AppConstant.address)?.observe(viewLifecycleOwner) { result -> businessAddress = result
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

    private val borrowerApplicationViewModel: BorrowerApplicationViewModel by activityViewModels()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSentData(event: SendDataEvent){
        binding.loaderIncomeBusiness.visibility = View.GONE
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
        //Log.e("received","updtedRecieved")
        IncomeTabFragment.isStartIncomeTab = false
        if(evt.isMainIncomeUpdateReceived){
            val incomeUpdate = IncomeUpdateInfo(AppConstant.income_business,borrowerId!!)
            findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.income_update,incomeUpdate)
            findNavController().navigateUp()
        }
    }

    private fun updateMainIncome(){
        //borrowerApplicationViewModel.resetIncomeModelClass()
        borrowerApplicationViewModel.resetSingleIncomeTab()
            lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->

                 //val call = async{
                     borrowerApplicationViewModel.getSingleTabIncomeDetail(authToken, loanApplicationId!!, borrowerId!!) }
                    //call.await() }

                /*borrowerApplicationViewModel.singleIncomeDetail.observe(viewLifecycleOwner, { observableSampleContent ->
                    //IncomeTabFragment.isStartIncomeTab = false
                    val incomeUpdate = IncomeUpdateInfo(AppConstant.income_business,borrowerId!!)
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.income_update,incomeUpdate)
                    findNavController().navigateUp()
                }) */
            }

    }


    var maxDate:Long = 0
    var minDate:Long = 0

    private fun openCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(), R.style.MySpinnerDatePickerStyle,
            {
                    view, selectedYear, monthOfYear, dayOfMonth ->
                binding.edBstartDate.setText("" + (monthOfYear+1) + "/" + dayOfMonth + "/" + selectedYear)
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
/*
    private fun openCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val newMonth = month + 1

        val dpd = DatePickerDialog(
            requireActivity(),
            { view, year, monthOfYear, dayOfMonth -> binding.edBstartDate.setText("" + newMonth + "/" + dayOfMonth + "/" + year) },
            year,
            month,
            day
        )
        dpd.show()
    } */

    /*private fun processSendData(){

     val businessType: String = binding.tvBusinessType.text.toString()
     val businessName: String = binding.edBusinessName.text.toString()
     val jobTitle: String = binding.edJobTitle.text.toString()
     val startDate: String = binding.edBstartDate.text.toString()
     val percentage: String = binding.edOwnershipPercent.text.toString()
     val netIncome: String = binding.editTextAnnualIncome.text.toString()

     if (businessType.isEmpty() || businessType.length == 0) {
         CustomMaterialFields.setError(binding.layoutBusinessType, getString(R.string.error_field_required),requireActivity())
     }
     if (businessName.isEmpty() || businessName.length == 0) {
         CustomMaterialFields.setError(binding.layoutBusinessName, getString(R.string.error_field_required),requireActivity())
     }
     if (jobTitle.isEmpty() || jobTitle.length == 0) {
         CustomMaterialFields.setError(binding.layoutJobTitle, getString(R.string.error_field_required),requireActivity())
     }
     if (startDate.isEmpty() || startDate.length == 0) {
         CustomMaterialFields.setError(binding.layoutBStartDate, getString(R.string.error_field_required),requireActivity())
     }
     if (netIncome.isEmpty() || netIncome.length == 0) {
         CustomMaterialFields.setError(binding.layoutNetIncome, getString(R.string.error_field_required),requireActivity())
     }
     if (percentage.isEmpty() || percentage.length == 0) {
         CustomMaterialFields.setError(binding.layoutOwnershipPercentage, getString(R.string.error_field_required),requireActivity())
     }
     if (businessName.isNotEmpty() || businessName.length > 0) {
         CustomMaterialFields.clearError(binding.layoutBusinessName,requireActivity())
     }
     if (jobTitle.isNotEmpty() || jobTitle.length > 0) {
         CustomMaterialFields.clearError(binding.layoutJobTitle,requireActivity())
     }
     if (startDate.isNotEmpty() || startDate.length > 0) {
         CustomMaterialFields.clearError(binding.layoutBStartDate,requireActivity())
     }
     if (netIncome.isNotEmpty() || netIncome.length > 0) {
         CustomMaterialFields.clearError(binding.layoutNetIncome,requireActivity())
     }
     if (percentage.isNotEmpty() || percentage.length > 0) {
         CustomMaterialFields.clearError(binding.layoutOwnershipPercentage,requireActivity())
     }
     if (businessType.length > 0 && businessName.length > 0 && jobTitle.length > 0 &&  startDate.length > 0 && netIncome.length > 0  && percentage.length > 0){

         lifecycleScope.launchWhenStarted{
             sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                 if(loanApplicationId != null && borrowerId !=null) {
                     //Log.e("sending", "" +loanApplicationId + " borrowerId:  " + borrowerId+ " incomeInfoId: " + incomeInfoId)
                     // get business type id
                     val type : String = binding.tvBusinessType.getText().toString().trim()
                     val matchedType =  businessTypes.filter { p -> p.name.equals(type,true)}
                     //Log.e("matchedType",""+matchedType)
                     val businessTypeId = if(matchedType.size > 0) matchedType.map { matchedType.get(0).id }.single() else null
                    // Log.e("businesId",""+businessTypeId)

                     val phoneNum = if(binding.edBusPhoneNum.text.toString().length > 0) binding.edBusPhoneNum.text.toString() else null
                     val ownershipPercentage = if(binding.edOwnershipPercent.text.toString().length > 0) binding.edOwnershipPercent.text.toString() else null
                     val annualIncome = binding.editTextAnnualIncome.text.toString().trim()
                     val newAnnualIncome = if(annualIncome.length > 0) annualIncome.replace(",".toRegex(), "") else null


                     val businessData = BusinessData(
                         loanApplicationId = loanApplicationId,borrowerId= borrowerId,businessName=businessName,businessPhone=phoneNum,startDate=startDate,
                         jobTitle = jobTitle,ownershipPercentage = ownershipPercentage?.toDouble(),annualIncome=newAnnualIncome?.toDouble(),address = businessAddress,id=incomeInfoId,
                         incomeTypeId =businessTypeId)

                     Log.e("businessDate-snding to API", "" + businessData)

                     binding.loaderIncomeBusiness.visibility = View.VISIBLE
                     viewModel.sendBusinessData(authToken, businessData)
                 }
             }
         }
     }
 } */
}