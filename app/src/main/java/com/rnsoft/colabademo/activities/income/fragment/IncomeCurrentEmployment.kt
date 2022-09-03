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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.activities.addresses.info.fragment.DeleteCurrentResidenceDialogFragment

import com.rnsoft.colabademo.databinding.AppHeaderWithCrossDeleteBinding
import com.rnsoft.colabademo.databinding.IncomeCurrentEmploymentBinding
import com.rnsoft.colabademo.databinding.IncomeRetirementLayoutBinding
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
import kotlin.collections.ArrayList


/**
 * Created by Anita Kiran on 9/13/2021.
 */
@AndroidEntryPoint
class IncomeCurrentEmployment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: IncomeCurrentEmploymentBinding
    private lateinit var toolbar: AppHeaderWithCrossDeleteBinding
    private var savedViewInstance: View? = null
    private val viewModel : IncomeViewModel by activityViewModels()
    private var loanApplicationId: Int? = null
    private var incomeInfoId :Int? = null
    private var borrowerId :Int? = null
    private var borrowerName: String? = null
    private var employerAddress = AddressData()
    val incomeListForApi : ArrayList<EmploymentOtherIncomes> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (savedViewInstance != null){
            savedViewInstance
        } else {
            binding = IncomeCurrentEmploymentBinding.inflate(inflater, container, false)
            savedViewInstance = binding.root
            toolbar = binding.headerIncome
            super.addListeners(binding.root)
            // set Header title
            toolbar.toolbarTitle.text = getString(R.string.current_employment)
            arguments?.let { arguments ->
                loanApplicationId = arguments.getInt(AppConstant.loanApplicationId)
                borrowerId = arguments.getInt(AppConstant.borrowerId)
                incomeInfoId = arguments.getInt(AppConstant.incomeId)
                borrowerName = arguments.getString(AppConstant.borrowerName)
            }

            //Log.e("Current Employment-oncreate", "Loan Application Id " + loanApplicationId + " borrowerId:  " + borrowerId + " incomeInfoId" + incomeInfoId)

            borrowerName?.let {
                //toolbar.borrowerPurpose.setText(it)
            }

            if (loanApplicationId != null && borrowerId != null){
                toolbar.btnTopDelete.visibility = View.VISIBLE
                toolbar.btnTopDelete.setOnClickListener {
                    DeleteIncomeDialogFragment.newInstance(AppConstant.income_delete_text).show(childFragmentManager, DeleteCurrentResidenceDialogFragment::class.java.canonicalName)
                }
            }
            if (incomeInfoId == null || incomeInfoId == 0) {
                toolbar.btnTopDelete.visibility = View.GONE
                showHideAddress(false,true)
            }

            initViews()
            setInputFields()
            getEmploymentData()

            savedViewInstance
        }
    }

     private fun getEmploymentData(){
         if (loanApplicationId != null && borrowerId!=null && incomeInfoId!! > 0) {

             lifecycleScope.launchWhenStarted {
                 sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                     //Log.e("getting-current-details", "" +loanApplicationId + " borrowerId:  " + borrowerId+ " incomeInfoId: " + incomeInfoId)
                     binding.loaderEmployment.visibility = View.VISIBLE
                     viewModel.getEmploymentDetail(authToken, loanApplicationId!!, borrowerId!!, incomeInfoId!!)
                 }
             }
             viewModel.employmentDetail.observe(viewLifecycleOwner, { data ->

                 data?.employmentData?.employmentInfo.let { info ->
                     info?.employerName?.let {
                         binding.editTextEmpName.setText(it)
                         CustomMaterialFields.setColor(
                             binding.layoutEmpName,
                             R.color.grey_color_two,
                             requireContext()
                         )
                     }
                     info?.employerPhoneNumber?.let {
                         binding.editTextEmpPhnum.setText(it)
                         CustomMaterialFields.setColor(
                             binding.layoutEmpPhnum,
                             R.color.grey_color_two,
                             requireContext()
                         )
                     }
                     info?.jobTitle?.let {
                         binding.editTextJobTitle.setText(it)
                         CustomMaterialFields.setColor(
                             binding.layoutJobTitle,
                             R.color.grey_color_two,
                             requireContext()
                         )
                     }
                     info?.startDate?.let {
                         binding.editTextStartDate.setText(AppSetting.getFullDate1(it))
                     }
                     info?.yearsInProfession?.let {
                         binding.editTextProfYears.setText(it.toString())
                         CustomMaterialFields.setColor(
                             binding.layoutYearsProfession,
                             R.color.grey_color_two,
                             requireContext()
                         )
                     }

                     info?.employedByFamilyOrParty?.let {
                         if (it == true)
                             binding.rbEmployedByFamilyYes.isChecked = true
                         else {
                             binding.rbEmployedByFamilyNo.isChecked = true
                         }
                     }

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

                     data?.employmentData?.employerAddress?.let {
                         employerAddress = it
                         displayAddress(it)
                         //binding.textviewCurrentEmployerAddress.text = it.street + " " + it.unit + "\n" + it.city + " " + it.stateName + " " + it.zipCode + " " + it.countryName
                     } ?:run{
                         showHideAddress(false,true)
                     }

                     data?.employmentData?.wayOfIncome?.let { salary ->
                         salary.isPaidByMonthlySalary?.let {
                             if (it == true) {
                                 binding.paytypeSalary.isChecked = true
                                 payTypeClicked()
                                 salary.employerAnnualSalary?.let {
                                     binding.edAnnualSalary.setText(
                                         Math.round(it).toString()
                                     )
                                     CustomMaterialFields.setColor(
                                         binding.layoutBaseSalary,
                                         R.color.grey_color_two,
                                         requireContext()
                                     )
                                 }
                             } else {
                                 binding.paytypeHourly.isChecked = true
                                 payTypeClicked()
                                 salary.hourlyRate?.let {
                                     binding.edHourlyRate.setText(Math.round(it).toString())
                                     CustomMaterialFields.setColor(
                                         binding.layoutHourlyRate,
                                         R.color.grey_color_two,
                                         requireContext()
                                     )
                                 }
                                 salary.hoursPerWeek?.let {
                                     binding.editTextWeeklyHours.setText(it.toString())
                                     CustomMaterialFields.setColor(
                                         binding.layoutWeeklyHours,
                                         R.color.grey_color_two,
                                         requireContext()
                                     )
                                 }
                             }
                         }
                     }

                     data?.employmentData?.employmentOtherIncome?.let {
                         for (i in 0 until it.size) {
                             // otherIncomeList.add(EmploymentOtherIncome(
                             //        it.get(i).incomeTypeId, // maintainging list for matching ids for sending data to api
                             //      it.get(i).name, it.get(i).displayName, it.get(i).annualIncome))

                             it.get(i).displayName?.let { name ->
                                 if (name.equals(AppConstant.INCOME_BONUS, true)) {
                                     it.get(i).annualIncome?.let {
                                         binding.editTextBonusIncome.setText(
                                             Math.round(it).toString()
                                         )
                                         binding.cbBonus.isChecked = true
                                     }
                                 }
                                 if (name.equals(AppConstant.INCOME_COMMISSION, true)) {
                                     it.get(i).annualIncome?.let {
                                         binding.editTextCommission.setText(
                                             Math.round(it).toString()
                                         )
                                         binding.cbCommission.isChecked = true
                                     }
                                 }
                                 if (name.equals(AppConstant.INCOME_OVERTIME, true)) {
                                     it.get(i).annualIncome?.let {
                                         binding.editTextOvertimeIncome.setText(
                                             Math.round(
                                                 it
                                             ).toString()
                                         )
                                         binding.cbOvertime.isChecked = true
                                     }
                                 }
                             }
                         }
                     }
                 }

                 binding.loaderEmployment.visibility = View.GONE
             })
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

    private fun openAddressFragment(){
        val addressFragment = AddressCurrentEmployment()
        val bundle = Bundle()
        bundle.putString(AppConstant.TOOLBAR_TITLE, getString(R.string.current_employer_address))
        bundle.putParcelable(AppConstant.address,employerAddress)
        addressFragment.arguments = bundle
        findNavController().navigate(R.id.action_current_employment_address, addressFragment.arguments)
    }

    private fun displayAddress(it: AddressData) {
        //Log.e("displayAddress", ""+ it)
        if(it.street == null && it.city==null && it.zipCode==null)
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

            binding.textviewCurrentEmployerAddress.text = builder
            showHideAddress(true,false)
        }
    }

  /*  private fun processSendData(){

        var isDataEntered : Boolean = true
        var ownershipPercentage: String?= null
        val empName: String = binding.editTextEmpName.text.toString()
        val jobTitle: String = binding.editTextJobTitle.text.toString()
        val startDate: String = binding.editTextStartDate.text.toString()
        val profYears: String = binding.editTextProfYears.text.toString()

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

        if (empName.isEmpty() || empName.length == 0) {
            isDataEntered = false
            CustomMaterialFields.setError(binding.layoutEmpName, getString(R.string.error_field_required),requireActivity())
        }
        if (startDate.isEmpty() || startDate.length == 0) {
            isDataEntered = false
            CustomMaterialFields.setError(binding.layoutStartDate, getString(R.string.error_field_required),requireActivity())
        }

        if (empName.isNotEmpty() || empName.length > 0) {
            isDataEntered = true
            CustomMaterialFields.clearError(binding.layoutEmpName,requireActivity())
        }
        if (startDate.isNotEmpty() || startDate.length > 0) {
            isDataEntered = true
            CustomMaterialFields.clearError(binding.layoutStartDate,requireActivity())
        }

        if(binding.cbBonus.isChecked){
            if(binding.layoutBonusIncome.isVisible){
                val incomeBonus = binding.editTextBonusIncome.text.toString()
                if(incomeBonus.length ==0){
                    isDataEntered = false
                    CustomMaterialFields.setError(binding.layoutBonusIncome, getString(R.string.error_field_required), requireActivity())
                }
                if(incomeBonus.length > 0){
                    isDataEntered = true
                    CustomMaterialFields.clearError(binding.layoutBonusIncome, requireActivity())
                }
            }
        }

        if(binding.cbCommission.isChecked){
            if(binding.layoutCommIncome.isVisible){
                val incomeCommission = binding.editTextCommission.text.toString()
                if(incomeCommission.length ==0){
                    isDataEntered = false
                    CustomMaterialFields.setError(binding.layoutCommIncome, getString(R.string.error_field_required), requireActivity())
                }
                if(incomeCommission.length > 0){
                    isDataEntered = true
                    CustomMaterialFields.clearError(binding.layoutCommIncome, requireActivity())
                }
            }
        }

        if(binding.cbOvertime.isChecked){
            if(binding.layoutOvertimeIncome.isVisible){
                val incomeOvertime = binding.editTextOvertimeIncome.text.toString()
                if(incomeOvertime.length ==0){
                    isDataEntered = false
                    CustomMaterialFields.setError(binding.layoutOvertimeIncome, getString(R.string.error_field_required), requireActivity())
                }
                if(incomeOvertime.length > 0){
                    isDataEntered = true
                    CustomMaterialFields.clearError(binding.layoutOvertimeIncome, requireActivity())
                }
            }
        }

        if(binding.paytypeSalary.isChecked){
            val salary = binding.edAnnualSalary.text.toString().trim()
            if(salary.length == 0){
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutBaseSalary, getString(R.string.error_field_required), requireActivity())
            }
            if(salary.length >0 ){
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutBaseSalary, requireActivity())
            }
        }
        if(binding.paytypeSalary.isChecked){
            val salary = binding.edAnnualSalary.text.toString().trim()
            if(salary.length == 0){
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutBaseSalary, getString(R.string.error_field_required), requireActivity())
            }
            if(salary.length >0 ){
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutBaseSalary, requireActivity())
            }
        }

        if(binding.paytypeHourly.isChecked){
            if(binding.edHourlyRate.text.toString().trim().length == 0){
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutHourlyRate, getString(R.string.error_field_required), requireActivity())
            }
            if(binding.edHourlyRate.text.toString().trim().length >0 ){
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutHourlyRate, requireActivity())
            }

            if(binding.edHourlyRate.text.toString().trim().length == 0){
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutHourlyRate, getString(R.string.error_field_required), requireActivity())
            }
            if(binding.edHourlyRate.text.toString().trim().length >0 ){
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutHourlyRate, requireActivity())
            }

            if(binding.editTextWeeklyHours.text.toString().trim().length == 0){
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutWeeklyHours, getString(R.string.error_field_required), requireActivity())
            }
            if(binding.editTextWeeklyHours.text.toString().trim().length >0 ){
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutWeeklyHours, requireActivity())
            }
        }

        if (isDataEntered){
            lifecycleScope.launchWhenStarted{
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    if(loanApplicationId != null && borrowerId !=null) {
                        //Log.e("Loan Application Id", "" +loanApplicationId + " borrowerId:  " + borrowerId)

                        val phoneNum = if(binding.editTextEmpPhnum.text.toString().length > 0) binding.editTextEmpPhnum.text.toString() else null
                        var isOwnershipInterest : Boolean ? = null
                        if(binding.rbOwnershipYes.isChecked)
                            isOwnershipInterest = true

                        if(binding.rbOwnershipNo.isChecked)
                            isOwnershipInterest = false

                        var isEmployedByFamily : Boolean ? = null
                        if(binding.rbEmployedByFamilyYes.isChecked)
                            isEmployedByFamily = true

                        if(binding.rbEmployedByFamilyNo.isChecked)
                            isEmployedByFamily = false

                        //val ownershipPercentage = if(binding.edOwnershipPercent.text.toString().length > 0) binding.edOwnershipPercent.text.toString() else null

                        val employerInfo = EmploymentInfo(
                            borrowerId = borrowerId,incomeInfoId= incomeInfoId, employerName=empName, employerPhoneNumber=phoneNum, jobTitle=jobTitle,startDate=startDate,endDate =null, yearsInProfession = profYears.toInt(),
                            hasOwnershipInterest = isOwnershipInterest, ownershipInterest = ownershipPercentage?.toDouble(),employedByFamilyOrParty = isEmployedByFamily)

                        // check values for wasys of income
                        var isPaidByMonthlySalary : Boolean? = null
                        var annualSalary : String = "0"
                        var hourlyRate : String = "0"
                        var avgHourWeeks : String = "0"

                        if(binding.paytypeSalary.isChecked) {
                            isPaidByMonthlySalary = true
                            val salary = binding.edAnnualSalary.text.toString().trim()
                            annualSalary = if(salary.length > 0) salary.replace(",".toRegex(), "") else "0"
                        }

                        if(binding.paytypeHourly.isChecked){
                            isPaidByMonthlySalary = false
                            val value = binding.edHourlyRate.text.toString().trim()
                            hourlyRate = if(value.length > 0) value.replace(",".toRegex(), "") else "0"
                            avgHourWeeks = if(binding.editTextWeeklyHours.text.toString().trim().length >0) binding.editTextWeeklyHours.text.toString() else "0"
                        }

                        val wayOfIncome = WayOfIncome(isPaidByMonthlySalary=isPaidByMonthlySalary,employerAnnualSalary=annualSalary?.toDouble(),hourlyRate= hourlyRate?.toDouble(),hoursPerWeek=avgHourWeeks.toInt())

                        // get other income types
                        if (binding.cbBonus.isChecked) {
                            val bonus = binding.editTextBonusIncome.text.toString().trim()
                            val newIncomeBonus = if (bonus.length > 0) bonus.replace(",".toRegex(), "") else null
                            incomeListForApi.add(EmploymentOtherIncomes(incomeTypeId = 2, annualIncome = newIncomeBonus?.toDouble()))
                        }
                        if (binding.cbCommission.isChecked){
                            val commission = binding.editTextCommission.text.toString().trim()
                            val newIncomeCommission = if (commission.isNotEmpty() || commission.length > 0) commission.replace(",".toRegex(), "")  else null
                            incomeListForApi.add(EmploymentOtherIncomes(incomeTypeId = 3, annualIncome = newIncomeCommission?.toDouble()))
                        }
                        if (binding.cbOvertime.isChecked) {
                            val overtime = binding.editTextOvertimeIncome.text.toString().trim()
                            val newIncomeOvertime = if (overtime.length > 0) overtime.replace(",".toRegex(), "") else null
                            incomeListForApi.add(EmploymentOtherIncomes(incomeTypeId = 1, annualIncome = newIncomeOvertime?.toDouble()))
                        }

                        val employmentData = AddCurrentEmploymentModel(
                            loanApplicationId = loanApplicationId,borrowerId= borrowerId, employmentInfo=employerInfo, employerAddress= employerAddress,wayOfIncome = wayOfIncome,employmentOtherIncomes = incomeListForApi)
                        //Log.e("incomeOther", "" + incomeListForApi)
                        Log.e("employmentData-snding to API", "" + employmentData)

                        binding.loaderEmployment.visibility = View.VISIBLE
                       // viewModel.sendCurrentEmploymentData(authToken, employmentData)
                    }
                }
            }
        }
    } */

    private fun initViews() {
        binding.addEmployerAddress.setOnClickListener {
            openAddressFragment()
        }

        binding.rbEmployedByFamilyYes.setOnClickListener(this)
        binding.rbEmployedByFamilyNo.setOnClickListener(this)
        binding.rbOwnershipYes.setOnClickListener(this)
        binding.rbOwnershipNo.setOnClickListener(this)
        binding.paytypeHourly.setOnClickListener(this)
        binding.paytypeSalary.setOnClickListener(this)
        //binding.cbBonus.setOnClickListener(this)
        //binding.cbOvertime.setOnClickListener(this)
        //binding.cbCommission.setOnClickListener(this)
        binding.layoutAddress.setOnClickListener(this)
        toolbar.btnClose.setOnClickListener(this)
        binding.btnSaveChange.setOnClickListener(this)
        binding.currentEmpLayout.setOnClickListener(this)


        binding.rbEmployedByFamilyYes.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                binding.rbEmployedByFamilyYes.setTypeface(null, Typeface.BOLD)
            else
                binding.rbEmployedByFamilyYes.setTypeface(null, Typeface.NORMAL)
        }

        binding.rbEmployedByFamilyNo.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                binding.rbEmployedByFamilyNo.setTypeface(null, Typeface.BOLD)
            else
                binding.rbEmployedByFamilyNo.setTypeface(null, Typeface.NORMAL)
        }

        binding.rbOwnershipYes.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                binding.rbOwnershipYes.setTypeface(null, Typeface.BOLD)
            else
                binding.rbOwnershipYes.setTypeface(null, Typeface.NORMAL)
        }

        binding.rbOwnershipNo.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                binding.rbOwnershipNo.setTypeface(null, Typeface.BOLD)
            else
                binding.rbOwnershipNo.setTypeface(null, Typeface.NORMAL)
        }

        binding.cbBonus.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.cbBonus.setTypeface(null, Typeface.BOLD)
                binding.layoutBonusIncome.visibility = View.VISIBLE
            } else {
                binding.cbBonus.setTypeface(null, Typeface.NORMAL)
                binding.layoutBonusIncome.visibility = View.GONE
            }
        }

        binding.cbCommission.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.cbCommission.setTypeface(null, Typeface.BOLD)
                binding.layoutCommIncome.visibility = View.VISIBLE
            } else {
                binding.cbCommission.setTypeface(null, Typeface.NORMAL)
                binding.layoutCommIncome.visibility = View.GONE
            }
        }

        binding.cbOvertime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.cbOvertime.setTypeface(null, Typeface.BOLD)
                binding.layoutOvertimeIncome.visibility = View.VISIBLE
            } else {
                binding.cbOvertime.setTypeface(null, Typeface.NORMAL)
                binding.layoutOvertimeIncome.visibility = View.GONE
            }
        }

    }

    override fun onClick(view: View?) {
        when (view?.getId()) {
            R.id.btn_save_change -> processSendData()
            R.id.rb_employed_by_family_yes -> quesOneClicked()
            R.id.rb_employed_by_family_no -> quesOneClicked()
            R.id.rb_ownership_yes -> quesTwoClicked()
            R.id.rb_ownership_no -> quesTwoClicked()
            R.id.paytype_hourly -> payTypeClicked()
            R.id.paytype_salary ->payTypeClicked()
            R.id.layout_address -> openAddressFragment() //findNavController().navigate(R.id.action_address)
            R.id.btn_close -> findNavController().popBackStack()
            R.id.current_emp_layout -> {
                HideSoftkeyboard.hide(requireActivity(), binding.currentEmpLayout)
                super.removeFocusFromAllFields(binding.currentEmpLayout)
            }
        }
    }

    private fun processSendData(){

        var isDataEntered : Boolean = false
        var ownershipPercentage: String?= null
        val empName: String = binding.editTextEmpName.text.toString()
        val startDate: String = binding.editTextStartDate.text.toString()

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

        if (empName.isEmpty() || empName.length == 0) {
            isDataEntered = false
            CustomMaterialFields.setError(binding.layoutEmpName, getString(R.string.error_field_required),requireActivity())
        }

        if (empName.isNotEmpty() || empName.length > 0) {
            isDataEntered = true
            CustomMaterialFields.clearError(binding.layoutEmpName,requireActivity())
        }
        if (startDate.isEmpty() || startDate.length == 0) {
            isDataEntered = false
            CustomMaterialFields.setError(binding.layoutStartDate, getString(R.string.error_field_required),requireActivity())
        }

        if (startDate.isNotEmpty() || startDate.length > 0) {
            isDataEntered = true
            CustomMaterialFields.clearError(binding.layoutStartDate,requireActivity())
        }

        if(binding.cbBonus.isChecked){
            if(binding.layoutBonusIncome.isVisible){
                val incomeBonus = binding.editTextBonusIncome.text.toString()
                if(incomeBonus.length ==0){
                    isDataEntered = false
                    CustomMaterialFields.setError(binding.layoutBonusIncome, getString(R.string.error_field_required), requireActivity())
                }
                if(incomeBonus.length > 0){
                    isDataEntered = true
                    CustomMaterialFields.clearError(binding.layoutBonusIncome, requireActivity())
                }
            }
        }

        if(binding.cbCommission.isChecked){
            if(binding.layoutCommIncome.isVisible){
                val incomeCommission = binding.editTextCommission.text.toString()
                if(incomeCommission.length ==0){
                    isDataEntered = false
                    CustomMaterialFields.setError(binding.layoutCommIncome, getString(R.string.error_field_required), requireActivity())
                }
                if(incomeCommission.length > 0){
                    isDataEntered = true
                    CustomMaterialFields.clearError(binding.layoutCommIncome, requireActivity())
                }
            }
        }

        if(binding.cbOvertime.isChecked){
            if(binding.layoutOvertimeIncome.isVisible){
                val incomeOvertime = binding.editTextOvertimeIncome.text.toString()
                if(incomeOvertime.length ==0){
                    isDataEntered = false
                    CustomMaterialFields.setError(binding.layoutOvertimeIncome, getString(R.string.error_field_required), requireActivity())
                }
                if(incomeOvertime.length > 0){
                    isDataEntered = true
                    CustomMaterialFields.clearError(binding.layoutOvertimeIncome, requireActivity())
                }
            }
        }

        if(binding.paytypeSalary.isChecked){
            val salary = binding.edAnnualSalary.text.toString().trim()
            if(salary.length == 0){
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutBaseSalary, getString(R.string.error_field_required), requireActivity())
            }
            if(salary.length >0 ){
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutBaseSalary, requireActivity())
            }
        }
        if(binding.paytypeSalary.isChecked){
            val salary = binding.edAnnualSalary.text.toString().trim()
            if(salary.length == 0){
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutBaseSalary, getString(R.string.error_field_required), requireActivity())
            }
            if(salary.length >0 ){
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutBaseSalary, requireActivity())
            }
        }

        if(binding.paytypeHourly.isChecked){
            if(binding.edHourlyRate.text.toString().trim().length == 0){
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutHourlyRate, getString(R.string.error_field_required), requireActivity())
            }
            if(binding.edHourlyRate.text.toString().trim().length >0 ){
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutHourlyRate, requireActivity())
            }

            if(binding.edHourlyRate.text.toString().trim().length == 0){
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutHourlyRate, getString(R.string.error_field_required), requireActivity())
            }
            if(binding.edHourlyRate.text.toString().trim().length >0 ){
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutHourlyRate, requireActivity())
            }

            if(binding.editTextWeeklyHours.text.toString().trim().length == 0){
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutWeeklyHours, getString(R.string.error_field_required), requireActivity())
            }
            if(binding.editTextWeeklyHours.text.toString().trim().length >0 ){
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutWeeklyHours, requireActivity())
            }
        }

        if (isDataEntered){
            lifecycleScope.launchWhenStarted{
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    if(loanApplicationId != null && borrowerId !=null) {
                        //Log.e("Loan Application Id", "" +loanApplicationId + " borrowerId:  " + borrowerId)

                        val phoneNum = if(binding.editTextEmpPhnum.text.toString().length > 0) binding.editTextEmpPhnum.text.toString() else null
                        var isOwnershipInterest : Boolean ? = null
                        if(binding.rbOwnershipYes.isChecked)
                            isOwnershipInterest = true

                        if(binding.rbOwnershipNo.isChecked)
                            isOwnershipInterest = false

                        var isEmployedByFamily : Boolean ? = null
                        if(binding.rbEmployedByFamilyYes.isChecked)
                            isEmployedByFamily = true

                        if(binding.rbEmployedByFamilyNo.isChecked)
                            isEmployedByFamily = false

                        val jobTitle = if(binding.editTextJobTitle.text.toString().length > 0) binding.editTextJobTitle.text.toString() else null
                        val profYears = if(binding.editTextProfYears.text.toString().length > 0) binding.editTextProfYears.text.toString() else null

                        val employerInfo = EmploymentInfo(
                            borrowerId = borrowerId,incomeInfoId= incomeInfoId, employerName=empName, employerPhoneNumber=phoneNum, jobTitle=jobTitle,startDate=startDate,endDate =null, yearsInProfession = profYears?.toInt(),
                            hasOwnershipInterest = isOwnershipInterest, ownershipInterest = ownershipPercentage?.toDouble(),employedByFamilyOrParty = isEmployedByFamily)

                        // check values for wasys of income
                        var isPaidByMonthlySalary : Boolean? = null
                        var annualSalary : String = "0"
                        var hourlyRate : String = "0"
                        var avgHourWeeks : String= "0"

                        if(binding.paytypeSalary.isChecked) {
                            isPaidByMonthlySalary = true
                            val salary = binding.edAnnualSalary.text.toString().trim()
                            annualSalary = if(salary.length > 0) salary.replace(",".toRegex(), "") else "0"
                        }

                        if(binding.paytypeHourly.isChecked){
                            isPaidByMonthlySalary = false
                            val value = binding.edHourlyRate.text.toString().trim()
                            hourlyRate = if(value.length > 0) value.replace(",".toRegex(), "") else "0"
                            avgHourWeeks = if(binding.editTextWeeklyHours.text.toString().trim().length >0) binding.editTextWeeklyHours.text.toString() else "0"
                        }

                        val wayOfIncome = WayOfIncome(isPaidByMonthlySalary=isPaidByMonthlySalary,employerAnnualSalary=annualSalary?.toDouble(),hourlyRate= hourlyRate?.toDouble(),hoursPerWeek=avgHourWeeks.toInt())

                        // get other income types
                        if (binding.cbBonus.isChecked) {
                            val bonus = binding.editTextBonusIncome.text.toString().trim()
                            val newIncomeBonus = if (bonus.length > 0) bonus.replace(",".toRegex(), "") else null
                            incomeListForApi.add(EmploymentOtherIncomes(incomeTypeId = 2, annualIncome = newIncomeBonus?.toDouble()))
                        }
                        if (binding.cbCommission.isChecked){
                            val commission = binding.editTextCommission.text.toString().trim()
                            val newIncomeCommission = if (commission.length > 0) commission.replace(",".toRegex(), "") else null
                            incomeListForApi.add(EmploymentOtherIncomes(incomeTypeId = 3, annualIncome = newIncomeCommission?.toDouble()))
                        }
                        if (binding.cbOvertime.isChecked) {
                            val overtime = binding.editTextOvertimeIncome.text.toString().trim()
                            val newIncomeOvertime = if (overtime.length > 0) overtime.replace(",".toRegex(), "") else null
                            incomeListForApi.add(EmploymentOtherIncomes(incomeTypeId = 1, annualIncome = newIncomeOvertime?.toDouble()))
                        }

                        val employmentData = AddCurrentEmploymentModel(
                            loanApplicationId = loanApplicationId,borrowerId= borrowerId, employmentInfo=employerInfo, employerAddress= employerAddress,wayOfIncome = wayOfIncome,employmentOtherIncomes = incomeListForApi)
                        //Log.e("incomeOther", "" + incomeListForApi)
                        Log.e("employmentData-snding to API", "" + employmentData)

                        binding.loaderEmployment.visibility = View.VISIBLE
                        viewModel.sendCurrentEmploymentData(authToken, employmentData)
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressData>(
            AppConstant.address)?.observe(viewLifecycleOwner) { result -> employerAddress = result
            //binding.textviewCurrentEmployerAddress.text = result.street + " " + result.unit + "\n" + result.city + " " + result.stateName + " " + result.zipCode + " " + result.countryName
            displayAddress(result)
        }
    }

    private fun quesOneClicked(){
        if(binding.rbEmployedByFamilyYes.isChecked) {
            binding.rbEmployedByFamilyYes.setTypeface(null, Typeface.BOLD)
            binding.rbEmployedByFamilyNo.setTypeface(null, Typeface.NORMAL)
        }
        else {
            binding.rbEmployedByFamilyNo.setTypeface(null, Typeface.BOLD)
            binding.rbEmployedByFamilyYes.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun quesTwoClicked(){
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

    private fun payTypeClicked(){
        if(binding.paytypeSalary.isChecked) {
            binding.paytypeSalary.setTypeface(null, Typeface.BOLD)
            binding.paytypeHourly.setTypeface(null, Typeface.NORMAL)
            binding.layoutHourlyRate.visibility= View.GONE
            binding.layoutWeeklyHours.visibility = View.GONE
            binding.layoutBaseSalary.visibility = View.VISIBLE
        }
        else {
            binding.paytypeSalary.setTypeface(null, Typeface.NORMAL)
            binding.paytypeHourly.setTypeface(null, Typeface.BOLD)
            binding.layoutHourlyRate.visibility= View.VISIBLE
            binding.layoutWeeklyHours.visibility = View.VISIBLE
            binding.layoutBaseSalary.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
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
        else if(event.addUpdateDataResponse.code == AppConstant.INTERNET_ERR_CODE){
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


    private fun setInputFields() {

        // set lable focus
        binding.editTextEmpName.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextEmpName, binding.layoutEmpName, requireContext()))
        binding.editTextEmpPhnum.setOnFocusChangeListener(FocusListenerForPhoneNumber(binding.editTextEmpPhnum, binding.layoutEmpPhnum,requireContext()))
        binding.editTextJobTitle.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextJobTitle, binding.layoutJobTitle, requireContext()))
        binding.editTextProfYears.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextProfYears, binding.layoutYearsProfession, requireContext()))
        binding.edOwnershipPercent.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edOwnershipPercent, binding.layoutOwnershipPercentage, requireContext()))
        binding.edAnnualSalary.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edAnnualSalary, binding.layoutBaseSalary, requireContext()))
        binding.editTextBonusIncome.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextBonusIncome, binding.layoutBonusIncome, requireContext()))
        binding.editTextOvertimeIncome.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextOvertimeIncome, binding.layoutOvertimeIncome, requireContext()))
        binding.editTextCommission.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextCommission, binding.layoutCommIncome, requireContext()))
        binding.edHourlyRate.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edHourlyRate, binding.layoutHourlyRate, requireContext()))
        binding.editTextWeeklyHours.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.editTextWeeklyHours, binding.layoutWeeklyHours, requireContext()))

        // set input format
        binding.edAnnualSalary.addTextChangedListener(NumberTextFormat(binding.edAnnualSalary))
        binding.edHourlyRate.addTextChangedListener(NumberTextFormat(binding.edHourlyRate))
        binding.editTextBonusIncome.addTextChangedListener(NumberTextFormat(binding.editTextBonusIncome))
        binding.editTextCommission.addTextChangedListener(NumberTextFormat(binding.editTextCommission))
        binding.editTextOvertimeIncome.addTextChangedListener(NumberTextFormat(binding.editTextOvertimeIncome))
        binding.editTextEmpPhnum.addTextChangedListener(PhoneTextFormatter(binding.editTextEmpPhnum, "(###) ###-####"))

        // set Dollar prefix
        CustomMaterialFields.setPercentagePrefix(binding.layoutOwnershipPercentage, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutBaseSalary, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutCommIncome, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutOvertimeIncome, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutBonusIncome, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutHourlyRate, requireContext())

        // calendar
        binding.editTextStartDate.showSoftInputOnFocus = false
        binding.editTextStartDate.setOnClickListener { openCalendar()}
        //binding.edStartDate.setOnFocusChangeListener { _, _ -> openCalendar() }

        binding.editTextStartDate.doAfterTextChanged {
            if (binding.editTextStartDate.text?.length == 0) {
                CustomMaterialFields.setColor(binding.layoutStartDate,R.color.grey_color_three,requireActivity())
            } else {
                CustomMaterialFields.setColor(binding.layoutStartDate,R.color.grey_color_two,requireActivity())
                CustomMaterialFields.clearError(binding.layoutStartDate,requireActivity())
            }
        }

    }

    /*private fun openCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val newMonth = month + 1

        val dpd = DatePickerDialog(
            requireActivity(),
            { view, year, monthOfYear, dayOfMonth -> binding.editTextStartDate.setText("" + newMonth + "/" + dayOfMonth + "/" + year) },
            year,
            month,
            day
        )
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

    }

    private fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        return df.parse(date).time
    }

}