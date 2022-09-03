package com.rnsoft.colabademo.activities.addresses.info

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.text.capitalize
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.rnsoft.colabademo.*
import com.rnsoft.colabademo.AppConstant.MARITAL_STATUS_MARRIED
import com.rnsoft.colabademo.AppConstant.MARITAL_STATUS_SEPARATED
import com.rnsoft.colabademo.AppConstant.MARITAL_STATUS_UNMARRIED
import com.rnsoft.colabademo.AppConstant.SECONDARY_BORROWER_ID
import com.rnsoft.colabademo.activities.addresses.info.*
import com.rnsoft.colabademo.activities.addresses.info.adapter.DependentAdapter
import com.rnsoft.colabademo.activities.addresses.info.fragment.DeleteCurrentResidenceDialogFragment
import com.rnsoft.colabademo.activities.addresses.info.fragment.SwipeToDeleteEvent
import com.rnsoft.colabademo.activities.addresses.info.model.Dependent
import com.rnsoft.colabademo.databinding.*
import com.rnsoft.colabademo.utils.CustomMaterialFields
import com.rnsoft.colabademo.utils.RecyclerTouchListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.notification_view_holder.*
import kotlinx.android.synthetic.main.sub_layout_military.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * Created by Anita Kiran on 8/23/2021.
 */

// remove address click listener
@AndroidEntryPoint
class PrimaryBorrowerInfoFragment : BaseFragment(), RecyclerviewClickListener, View.OnClickListener, AddressClickListener {

    val numberFormatter =  DecimalFormat("#,###,###")
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val viewModel : PrimaryBorrowerViewModel by activityViewModels()
    lateinit var bi: PrimaryBorrowerInfoLayoutBinding
    lateinit var msBinding: SublayoutMaritalStatusBinding
    lateinit var citizenshipBinding: SublayoutCitizenshipBinding
    lateinit var bindingMilitary: SubLayoutMilitaryBinding
    private var savedViewInstance: View? = null
    private var touchListener: RecyclerTouchListener? = null
    var count : Int = 0
    var selectedPosition : Int?=null
    val listItems = ArrayList<Dependent>()
    lateinit var adapter: BorrowerAddressAdapter
    lateinit var dependentAdapter: DependentAdapter
    var reserveEverActivated : Boolean? = null
    var listAddress: ArrayList<PreviousAddresses> = ArrayList()
    private var loanApplicationId: Int? = null
    private var borrowerId :Int? = null
    private var currentAddressModel = AddressModel()
    private var currentAddressFullDetail : CurrentAddress? = null
    private var maritalStatus : MaritalStatus? = null
    private var citizenship : BorrowerCitizenship? = null // make separate class for only two parameters of inner screen later
    private var citizenshipForApi : BorrowerCitizenship? = null
    private var militaryServiceDate : String? =null
    var militaryAffliation: ArrayList<MilitaryServiceDetail> = ArrayList()
    private var isAddCoBorrower: Boolean = false
    var ownTypeId : Int? =null
    var firstName : String? = null
    var lastName : String? = null
    var middleName : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (savedViewInstance != null) {
            savedViewInstance
        } else {
            bi = PrimaryBorrowerInfoLayoutBinding.inflate(inflater, container, false)
            savedViewInstance = bi.root
            super.addListeners(savedViewInstance as ViewGroup)
            msBinding = bi.layoutMaritalStatus
            citizenshipBinding = bi.layoutCitizenship
            bindingMilitary = bi.layoutMilitaryService

            setViews()
            setValues()
            bi.tvResidence.setText(requireContext().getString(R.string.add_current_address))
            adapter = BorrowerAddressAdapter(requireActivity())

            setData()

            savedViewInstance
        }
    }

    private fun setValues(){
        val activity = (activity as? BorrowerAddressActivity)
        activity?.loanApplicationId?.let {
            loanApplicationId = it
        }
        activity?.borrowerId?.let {
            if(it != -1 && it != 0){
                borrowerId = it
            }
        }
        activity?.ownTypeId?.let {
            ownTypeId = it
        }
        activity?.firstName?.let {
            firstName = it
        }
        activity?.lastName?.let {
            lastName = it
        }
        activity?.middleName?.let {
            middleName = it
        }
        activity?.isAddBorrower?.let{
            isAddCoBorrower= it
            if(isAddCoBorrower)
                ownTypeId = SECONDARY_BORROWER_ID
        }

        if(ownTypeId == SECONDARY_BORROWER_ID) bi.borrowerType.text= getString(R.string.heading_borrower_secondary) else bi.borrowerType.text= getString(R.string.heading_borrower_primary)

        try {
//                if (firstName != null && lastName != null) {
//                    var name = firstName!!.capitalize().plus(" ").plus(lastName!!.capitalize())
//                    if (name.isNotEmpty() && name.isNotBlank() && name.length > 0) {
//                        bi.name.setText(name)
//                        bindingMilitary.tvQues.text =
//                            "Was ".plus(name) + " ever activated during their tour of duty?"
//                    }
//                }

            val builder = StringBuilder()
            if(firstName!!.isNotEmpty() && firstName != "null"){
                builder.append(firstName!!.capitalize())
            }

            if(lastName!!.isNotEmpty() && lastName != "null"){
                builder.append(" ".plus(lastName!!.capitalize()))
            }

            if(builder.length > 0){
                bi.name.setText(builder.toString())
                bindingMilitary.tvQues.text =
                    "Was ".plus(builder) + " ever activated during their tour of duty?"
            }
        } catch(e:Exception){}
    }

    private fun setData(){
        viewModel.borrowerDetail.observe(viewLifecycleOwner, { detail ->
            if (detail != null){
                try {
                    detail.borrowerData?.currentAddress?.let { currentAddress ->
                        setCurrentAddressDetails(currentAddress)
                    }
                        ?: run { bi.tvResidence.setText(requireContext().getString(R.string.add_current_address)) }

                    detail.borrowerData?.previousAddresses?.let { prevAdd ->
                        for (i in 0 until prevAdd.size) {
                            val fromDate =
                                if (prevAdd.get(i).fromDate != null) prevAdd.get(i).fromDate else null
                            val toDate =
                                if (prevAdd.get(i).toDate != null) prevAdd.get(i).toDate else null
                            val preId = if (prevAdd.get(i).id != null) prevAdd.get(i).id else null
                            val housingStatus =
                                if (prevAdd.get(i).housingStatusId != null) prevAdd.get(i).housingStatusId else null
                            val rent =
                                if (prevAdd.get(i).monthlyRent != null) prevAdd.get(i).monthlyRent else null
                            var prevAddressModel: AddressModel? = null
                            prevAdd.get(i).addressModel?.let { model ->
                                prevAddressModel = model
                            }
                            //val desc = address.street + " " + address.unit + "\n" + address.city + " " + address.stateName + " " + address.zipCode + " " + address.countryName
                            listAddress.add(
                                PreviousAddresses(
                                    id = preId,
                                    housingStatusId = housingStatus,
                                    monthlyRent = rent,
                                    fromDate = fromDate,
                                    toDate = toDate,
                                    addressModel = prevAddressModel
                                )
                            )
                        }

                        if (listAddress.size > 0) {
                            setResidence()
                        }
                    }

                    detail.borrowerData?.borrowerBasicDetails?.let {
                        it.firstName?.let {
                            if(it.isNotEmpty() && it !="null") {
                                bi.edFirstName.setText(it.capitalize())
                                CustomMaterialFields.setColor(bi.layoutFirstName, R.color.grey_color_two, requireContext())
                            }
                        }

                        it.middleName?.let {
                            if(it.isNotEmpty() && it !="null") {
                                bi.edMiddleName.setText(it.capitalize())
                                CustomMaterialFields.setColor(bi.layoutMiddleName, R.color.grey_color_two, requireContext())
                            }
                        }

                        it.lastName?.let {
                            if(it.isNotEmpty() && it !="null") {
                                bi.edLastName.setText(it.capitalize())
                                CustomMaterialFields.setColor(bi.layoutLastName, R.color.grey_color_two, requireContext())
                            }
                        }

                        it.suffix?.let {
                            if(it.isNotEmpty() && it !="null") {
                                bi.edSuffix.setText(it)
                                CustomMaterialFields.setColor(
                                    bi.layoutSuffix,
                                    R.color.grey_color_two,
                                    requireContext()
                                )
                            }
                        }

                        it.emailAddress?.let {
                            if(it.isNotEmpty() && it !="null"){
                                bi.edEmail.setText(it)
                                CustomMaterialFields.setColor(bi.layoutEmail, R.color.grey_color_two, requireContext())
                            }
                        }

                        it.homePhone?.let {
                            if(it.isNotEmpty() && it !="null") {
                                if (it.isNotEmpty()) {
                                    bi.edHomeNumber.setText(it)
                                    CustomMaterialFields.setColor(bi.layoutHomeNum, R.color.grey_color_two, requireContext())
                                }
                            }
                        }

                        it.workPhone?.let {
                            if(it.isNotEmpty() && it !="null") {
                                bi.edWorkNum.setText(it)
                                CustomMaterialFields.setColor(bi.layoutWorkNum, R.color.grey_color_two, requireContext())
                            }

                        }

                        it.workPhoneExt?.let {
                            if (it.isNotEmpty() && it != "null") {
                                bi.edExtNum.setText(it)
                                CustomMaterialFields.setColor(bi.layoutExtNum, R.color.grey_color_two, requireContext())
                            }
                        }

                        it.cellPhone?.let {
                            if(it.isNotEmpty() && it !="null") {
                                bi.edCellNum.setText(it)
                                CustomMaterialFields.setColor(bi.layoutCellNum, R.color.grey_color_two, requireContext())
                            }
                        }
                    }

                    detail.borrowerData?.maritalStatus?.let { maritalData->
                        maritalStatus =maritalData
                        if(ownTypeId == SECONDARY_BORROWER_ID){
                            maritalData.relationWithPrimaryId?.let { relationPrimaryId->
                               if(relationPrimaryId == 1 && maritalData.spouseMaritalStatusId != null && maritalData.spouseMaritalStatusId >0){
                                   if(maritalData.spouseMaritalStatusId == MARITAL_STATUS_MARRIED)
                                       msBinding.rbMarried.isChecked = true

                                   if(maritalData.spouseMaritalStatusId == MARITAL_STATUS_SEPARATED)
                                       msBinding.rbSeparated.isChecked = true

                               } else {
                                   if (maritalData.maritalStatusId == MARITAL_STATUS_MARRIED)
                                       msBinding.rbMarried.isChecked = true
                                   if (maritalData.maritalStatusId == MARITAL_STATUS_SEPARATED)
                                       msBinding.rbSeparated.isChecked = true
                               }
                           } ?:run {
                                if (maritalData.maritalStatusId == MARITAL_STATUS_MARRIED)
                                    msBinding.rbMarried.isChecked = true
                                if (maritalData.maritalStatusId == MARITAL_STATUS_SEPARATED)
                                    msBinding.rbSeparated.isChecked = true
                            }
                        }
                        else { // owntype id 1
                            if (maritalData.maritalStatusId == MARITAL_STATUS_MARRIED)
                                msBinding.rbMarried.isChecked = true
                            if (maritalData.maritalStatusId == MARITAL_STATUS_SEPARATED)
                                msBinding.rbSeparated.isChecked = true
                        }

                        if (maritalData.maritalStatusId == MARITAL_STATUS_UNMARRIED) {
                            msBinding.rbUnmarried.isChecked = true
                            msBinding.rbUnmarried.setTypeface(null, Typeface.BOLD)
                            setMaritalStatus(maritalData)
                        }
                    }

                    detail.borrowerData?.borrowerCitizenship?.let {
                        //Timber.e("residency type id" + it.residencyTypeId)
                        citizenship = it
                        //citizenshipForApi = it
                        it.ssn?.let { ssn ->
                            bi.edSecurityNum.setText(ssn)
                            CustomMaterialFields.setColor(bi.layoutSecurityNum, R.color.grey_color_two, requireContext())
                        }
                        it.dobUtc?.let { dob ->
                            bi.edDateOfBirth.setText(AppSetting.getFullDate1(dob))
                            CustomMaterialFields.setColor(bi.layoutDateOfBirth, R.color.grey_color_two, requireContext())
                        }
                        if (it.residencyTypeId == 1)
                            citizenshipBinding.rbUsCitizen.isChecked = true
                        if (it.residencyTypeId == 2)
                            citizenshipBinding.rbPr.isChecked = true
                        if (it.residencyTypeId == 3) {
                            citizenshipBinding.rbNonPrOther.isChecked = true
                            citizenshipBinding.visaStatusDesc.text = it.residencyStatusExplanation
                            citizenshipBinding.layoutVisaStatusOther.visibility = View.VISIBLE
                        }

                        it.dependentCount?.let { count ->
                            bi.tvDependentCount.setText(count.toString())

                            it.dependentAges?.let {
                                if (it.isNotBlank() && it.isNotEmpty() && !it.equals("null", true)) {
                                    val strs = it.split(",").toList()
                                    //Timber.e("$strs")
                                    for (i in 0 until strs.size) {
                                        var ordinal = getOrdinal(listItems.size + 1)
                                        listItems.add(Dependent(ordinal.plus(" Dependent Age (Years)"), strs.get(i).toInt()))
                                    }
                                    bi.rvDependents.adapter = dependentAdapter
                                    dependentAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }

                    // military
                    detail.borrowerData?.militaryServiceDetails?.let {
                        it.details?.let {
                            for (i in 0 until it.size) {
                                if (it.get(i).militaryAffiliationId == 4) {
                                    bindingMilitary.chbDutyPersonel.isChecked = true
                                    it.get(i).expirationDateUtc?.let { it1 ->
                                        if (it1.isNotBlank() &&it1.isNotEmpty() && !it1.equals("null", true)) {
                                            var serviceDate = AppSetting.getMonthAndYear(it1,true)
                                            bindingMilitary.serviceDate.text = serviceDate
                                            militaryServiceDate = serviceDate
                                        }
                                    }
                                    bindingMilitary.layoutActivePersonnel.visibility = View.VISIBLE
                                }
                                if (it.get(i).militaryAffiliationId == 3) {
                                    bindingMilitary.chbResNationalGuard.isChecked = true
                                    it.get(i).reserveEverActivated?.let { it2 ->
                                        reserveEverActivated = it2
                                        if (reserveEverActivated == true) {
                                            bindingMilitary.resNationalGuardAns.text = "Yes"
                                        } else {
                                            bindingMilitary.resNationalGuardAns.text = "No"
                                        }
                                    }
                                }

                                if (it.get(i).militaryAffiliationId == 2)
                                    bindingMilitary.chbVeteran.isChecked = true

                                if (it.get(i).militaryAffiliationId == 1)
                                    bindingMilitary.chbSurvivingSpouse.isChecked = true
                            }
                        }
                    }

                    if (detail.code.equals(AppConstant.RESPONSE_CODE_SUCCESS)){
                        hideLoader()
                    }

                } catch (e: Exception){
                    //Log.e("Exception-InfoFrag",e.toString())
                }
            } // details

            hideLoader()
            })
    }

    private fun sendBorrowerData() {
        // borrower basic details
        var isDataEntered = true
        val firstName: String = bi.edFirstName.text.toString()
        val lastName: String = bi.edLastName.text.toString()
        val email: String = bi.edEmail.text.toString().trim()
        val homeNum: String = bi.edHomeNumber.text.toString().trim()

        val middleName = if (bi.edMiddleName.text.toString().trim().length > 0) bi.edMiddleName.text.toString() else null // get middle name
        val suffix = if (bi.edSuffix.text.toString().trim().length > 0) bi.edSuffix.text.toString() else null  // suffix
        val workPhoneNumber = if (bi.edWorkNum.text.toString().trim().length > 0) bi.edWorkNum.text.toString() else null // work number
        val workExt = if (bi.edExtNum.text.toString().trim().length > 0) bi.edExtNum.text.toString() else null
        val cellPhone = if (bi.edCellNum.text.toString().trim().length > 0) bi.edCellNum.text.toString() else null


        if (email.isNotEmpty() && email.length > 0){
            if (!isValidEmailAddress(email.trim())) {
                setError(bi.layoutEmail, getString(R.string.invalid_email))
                isDataEntered = false
            } else {
                clearError(bi.layoutEmail)
            }
        }

        if (homeNum.isNotEmpty() && homeNum.isNotBlank()) {
            if (homeNum.length < 14) {
                isDataEntered = false
                setError(bi.layoutHomeNum, getString(R.string.invalid_phone_num))
            } else {
                clearError(bi.layoutHomeNum)
            }
        }

        if(!bi.tvDependentCount.text.equals("0")){

            var textInputLayout : TextInputLayout
            for(item in 0 until bi.rvDependents.childCount){

                textInputLayout = bi.rvDependents.layoutManager?.findViewByPosition(item)?.findViewById<TextInputLayout>(R.id.til_dependent)!!
                var text =  textInputLayout.editText?.text.toString()
                if(text.isEmpty() || text.isBlank()){
                    textInputLayout.helperText = getString(R.string.error_field_required)
                    textInputLayout.setBoxStrokeColorStateList(AppCompatResources.getColorStateList(requireContext(), R.color.primary_info_stroke_error_color))
                    isDataEntered = false

                } else if(text.length>0){
                    clearError(textInputLayout)
                    isDataEntered =true
                }
            }
        }


        // citizenship
        if(isDataEntered) {
            var residencyTypeId: Int? = null
            var residencyStatusId: Int? = null
            var visaStatusDesc: String? = null

            if (citizenshipBinding.rbUsCitizen.isChecked) {
                residencyTypeId = 1
            }

            if (citizenshipBinding.rbPr.isChecked) {
                residencyTypeId = 2
            }

            if (citizenshipBinding.rbNonPrOther.isChecked) {
                residencyTypeId = 3
            }

            citizenship?.let {
                residencyStatusId = it.residencyStatusId
                visaStatusDesc = it.residencyStatusExplanation
            }

            // try {
            val dob = if (bi.edDateOfBirth.text.toString()
                    .trim().length > 0
            ) bi.edDateOfBirth.text.toString() else null
            val securityNum = if (bi.edSecurityNum.text.toString()
                    .trim().length > 0
            ) bi.edSecurityNum.text.toString() else null
            val dependentCount = bi.tvDependentCount.text.toString()
            val builder = StringBuilder()
            if (listItems.size > 0) {
                for (i in 0 until listItems.size) {
                    builder.append(listItems.get(i).age)
                    if (i + 1 < listItems.size) builder.append(",")
                }
            }

            var dependentAges: String? = null
            dependentAges = if (builder.toString().length > 0) builder.toString() else null

            citizenshipForApi = BorrowerCitizenship(
                loanApplicationId = loanApplicationId,
                borrowerId = if (borrowerId != null) borrowerId else null,
                residencyTypeId = residencyTypeId,
                residencyStatusId = residencyStatusId,
                residencyStatusExplanation = visaStatusDesc,
                dobUtc = dob,
                ssn = securityNum,
                dependentCount = dependentCount.toInt(),
                dependentAges = dependentAges.toString()
            )

            // military
            if (bindingMilitary.chbDutyPersonel.isChecked) {
                militaryAffliation.add(
                    MilitaryServiceDetail(
                        militaryAffiliationId = 4,
                        expirationDateUtc = militaryServiceDate,
                        reserveEverActivated = null
                    )
                )
            }

            if (bindingMilitary.chbResNationalGuard.isChecked) {
                if (reserveEverActivated == null) {
                    reserveEverActivated = false
                }
                militaryAffliation.add(
                    MilitaryServiceDetail(
                        militaryAffiliationId = 3,
                        expirationDateUtc = null,
                        reserveEverActivated = reserveEverActivated
                    )
                )
            }

            if (bindingMilitary.chbVeteran.isChecked) {
                militaryAffliation.add(
                    MilitaryServiceDetail(
                        militaryAffiliationId = 2,
                        expirationDateUtc = null,
                        reserveEverActivated = null
                    )
                )
            }

            if (bindingMilitary.chbSurvivingSpouse.isChecked) {
                militaryAffliation.add(
                    MilitaryServiceDetail(
                        militaryAffiliationId = 1,
                        expirationDateUtc = null,
                        reserveEverActivated = null
                    )
                )
            }

            val militaryServiceDetail =
                MilitaryServiceDetails(details = militaryAffliation, isVaEligible = true)

            if (maritalStatus == null) {
                maritalStatus = MaritalStatus(loanApplicationId = loanApplicationId!!)
            }

            lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    val basicDetails = BorrowerBasicDetails(
                        loanApplicationId = loanApplicationId!!,
                        borrowerId = if (borrowerId != null) borrowerId else null,
                        firstName = firstName,
                        lastName = lastName,
                        middleName = middleName,
                        suffix = suffix,
                        emailAddress = email,
                        homePhone = homeNum,
                        workPhone = workPhoneNumber,
                        workPhoneExt = workExt,
                        cellPhone = cellPhone,
                        ownTypeId = ownTypeId
                    )

                    //Log.e("currentAddressBeforeApi",""+currentAddressFullDetail)
                    val responseBody = PrimaryBorrowerData(
                        loanApplicationId = loanApplicationId!!,
                        borrowerId = if (borrowerId != null) borrowerId else null,
                        borrowerBasicDetails = basicDetails,
                        currentAddress = currentAddressFullDetail,
                        previousAddresses = if (listAddress.size > 0) listAddress else null,
                        borrowerCitizenship = citizenshipForApi,
                        maritalStatus = maritalStatus,
                        militaryServiceDetails = militaryServiceDetail
                    )

                    //Log.e("AddResponseBody","$responseBody")
                    viewModel.addUpdateBorrowerInfo(authToken, responseBody)

                    /* empty parameter validation
                   current and previous address will be null
                  citizenship and marital status all parameters can be null accept loan application id
                  military Serive (details=[], isVaEligible=true)
             */
                }
            }

        }
    }

    override fun onResume(){
        super.onResume()
        touchListener?.let { bi.recyclerview.addOnItemTouchListener(it) }

        // current address
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<CurrentAddress>(AppConstant.current_address)?.observe(
            viewLifecycleOwner) { result ->
                setCurrentAddressDetails(result)
        }

        // observe previous add
        viewModel.updatedAddress.observe(viewLifecycleOwner, { result ->
          //Log.e("Frag Info onResume","observedAddress")

            result?.let {

                //Log.e("result","found")
                try {
                    val listPosition = it.position
                    //Log.e("listAdd size", "" + listAddress.size)
                   // Log.e("position", "" + listPosition)

                    if (listAddress.size > 0){

                        if(listPosition!! < listAddress.size ){
                            listAddress.removeAt(listPosition!!)
                        }

                        var fromDate: String? = null
                        result.fromDate?.let {
                            if (it.length > 0)
                                fromDate = AppSetting.reverseDateFormat(it)
                        }
                        var toDate: String? = null
                        result.toDate?.let {
                            if (it.length > 0)
                                toDate = AppSetting.reverseDateFormat(it)
                        }

                        //val toDate = if(result.toDate != null) result.toDate else null
                        val prevId = if (result.id != null) result.id else null
                        val housingStatus =
                            if (result.housingStatusId != null) result.housingStatusId else null
                        val rent = if (result.monthlyRent != null) result.monthlyRent else null
                        var prevAddressModel: AddressModel? = null
                        result.addressModel?.let { model ->
                            prevAddressModel = model
                        }

                       // Log.e("listAdd  modified item", "" + listAddress)


                        listAddress.add(
                            listPosition!!,
                            PreviousAddresses(
                                id = prevId,
                                housingStatusId = housingStatus,
                                monthlyRent = rent,
                                fromDate = fromDate,
                                toDate = toDate,
                                addressModel = prevAddressModel
                            )
                        )
                        adapter.setTaskList(listAddress)
                        viewModel.emptyAddress()
                    } // if

                    else {

                       // Log.e("address is ","null-true")
                        var fromDate: String? = null
                        result.fromDate?.let {
                            if (it.length > 0)
                                fromDate = AppSetting.reverseDateFormat(it)
                        }
                        var toDate: String? = null
                        result.toDate?.let {
                            if (it.length > 0)
                                toDate = AppSetting.reverseDateFormat(it)
                        }

                        //val toDate = if(result.toDate != null) result.toDate else null
                        val prevId = if (result.id != null) result.id else null
                        val housingStatus =
                            if (result.housingStatusId != null) result.housingStatusId else null
                        val rent = if (result.monthlyRent != null) result.monthlyRent else null
                        var prevAddressModel: AddressModel? = null
                        result.addressModel?.let { model ->
                            prevAddressModel = model
                        }

                       // Log.e("listAdd  add new ", "" + listAddress)
                        listAddress.add(listPosition!!,
                            PreviousAddresses(id = prevId, housingStatusId = housingStatus, monthlyRent = rent, fromDate = fromDate, toDate = toDate, addressModel = prevAddressModel))
                        setResidence()
                        viewModel.emptyAddress()
                    }
                }
                catch (e:Exception){
                    //Log.e("prev add exception",e.toString())
                }
            }

        })

        // previous address
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<PreviousAddresses>(AppConstant.previous_address)?.observe(
            viewLifecycleOwner) { result ->
            //Log.e("Previous address receive","true")
            result?.let {

                try {
                    val listPosition = it.position
                    //Log.e("listAdd size", "" + listAddress.size)
                   // Log.e("position", "" + listPosition)

                    if (listAddress.size > 0) {

                        if(listAddress.size < listPosition!!) {
                            listAddress.removeAt(listPosition!!)
                        }

                        var fromDate: String? = null
                        result.fromDate?.let {
                            if (it.length > 0)
                                fromDate = AppSetting.reverseDateFormat(it)
                        }
                        var toDate: String? = null
                        result.toDate?.let {
                            if (it.length > 0)
                                toDate = AppSetting.reverseDateFormat(it)
                        }

                        //val toDate = if(result.toDate != null) result.toDate else null
                        val prevId = if (result.id != null) result.id else null
                        val housingStatus =
                            if (result.housingStatusId != null) result.housingStatusId else null
                        val rent = if (result.monthlyRent != null) result.monthlyRent else null
                        var prevAddressModel: AddressModel? = null
                        result.addressModel?.let { model ->
                            prevAddressModel = model
                        }

                        Log.e("listAdd  modified item", "" + listAddress)


                        listAddress.add(
                            listPosition!!,
                            PreviousAddresses(
                                id = prevId,
                                housingStatusId = housingStatus,
                                monthlyRent = rent,
                                fromDate = fromDate,
                                toDate = toDate,
                                addressModel = prevAddressModel
                            )
                        )
                        adapter.setTaskList(listAddress)
                    } // if

                    else {
                        var fromDate: String? = null
                        result.fromDate?.let {
                            if (it.length > 0)
                                fromDate = AppSetting.reverseDateFormat(it)
                        }
                        var toDate: String? = null
                        result.toDate?.let {
                            if (it.length > 0)
                                toDate = AppSetting.reverseDateFormat(it)
                        }

                        //val toDate = if(result.toDate != null) result.toDate else null
                        val prevId = if (result.id != null) result.id else null
                        val housingStatus =
                            if (result.housingStatusId != null) result.housingStatusId else null
                        val rent = if (result.monthlyRent != null) result.monthlyRent else null
                        var prevAddressModel: AddressModel? = null
                        result.addressModel?.let { model ->
                            prevAddressModel = model
                        }

                        Log.e("listAdd  add new ", "" + listAddress)
                        listAddress.add(listPosition!!,
                            PreviousAddresses(id = prevId, housingStatusId = housingStatus, monthlyRent = rent, fromDate = fromDate, toDate = toDate, addressModel = prevAddressModel))
                        setResidence()




                    }
                }
                 catch (e:Exception){
                    Log.e("prev add exception",e.toString())
                }
           }
        }

        // marital status
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<MaritalStatus>(AppConstant.marital_status)?.observe(
            viewLifecycleOwner){ result ->
            setMaritalStatus(result)
        }

        // active duty
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(AppConstant.service_date)?.observe(
            viewLifecycleOwner) { result ->
            try {
                if(result.isNotBlank() && result.isNotEmpty() && result.length > 0){
                    //val updatedDate = AppSetting.reverseDateFormat("01/"+result)
                    //val serviceDate1 = AppSetting.getMonthAndYearValue(updatedDate)
                    militaryServiceDate = result
                    bindingMilitary.serviceDate.text = result
                    bindingMilitary.layoutActivePersonnel.visibility = View.VISIBLE
                    bindingMilitary.chbDutyPersonel.setTypeface(null, Typeface.BOLD)

                }
            } catch (e:Exception){}
        }

        //reserve
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(AppConstant.RESERVE_ACTIVATED)?.observe(
            viewLifecycleOwner) { result ->
            if(result.isNotBlank() && result.isNotEmpty() && result.length >0 ){
                bindingMilitary.chbResNationalGuard.isChecked = true
                if(result =="Yes")
                    reserveEverActivated = true
                else
                    reserveEverActivated = false

                bindingMilitary.resNationalGuardAns.text = result

            }
        }

        // visa status  other
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BorrowerCitizenship>(AppConstant.borrower_citizenship)?.observe(
            viewLifecycleOwner) { result ->
            result?.let {
                citizenship = it
                result.residencyStatusId?.let {
                    citizenshipBinding.layoutVisaStatusOther.visibility = View.VISIBLE
                    if(it==5){
                        citizenshipBinding.visaStatusDesc.setText(AppConstant.visa_status_other)
                    }
                    if(it==4){
                        citizenshipBinding.visaStatusDesc.setText(AppConstant.visa_status_temp_worker)
                    }
                    if(it==3){
                        citizenshipBinding.visaStatusDesc.setText(AppConstant.visa_status_work_visa)
                    }
                }

                /*result.residencyStatusExplanation?.let {
                    citizenshipBinding.visaStatusDesc.setText(it)
                    citizenshipBinding.layoutVisaStatusOther.visibility = View.VISIBLE
                } */
            }
        }

        //delete Previous address
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(AppConstant.delete_previous_address)?.observe(
            viewLifecycleOwner) { result ->
            result?.let {
               // Log.e("OnResume","fire Event")
                EventBus.getDefault().post(SwipeToDeleteEvent(true))
            }
        }

    }

    private fun setCurrentAddressDetails(currentAddress : CurrentAddress){
        try {
            currentAddressFullDetail = currentAddress
            currentAddress.addressModel?.let { address ->
                currentAddressModel = address
                if (address.street != null && address.city !=null){
                    bi.currentAddressLayout.visibility = View.VISIBLE
                    displayAddress(address)

                    currentAddress.fromDate?.let {
                        if(it != "null" && it.isNotEmpty() && it.isNotBlank())
                            bi.tvResidenceDate.text = "From ".plus(AppSetting.getMonthAndYearValue(it))
                    }

                    currentAddress.monthlyRent?.let {
                        try {
                            if (it > 0) {
                                val value: String = numberFormatter.format(Math.round(it))
                                bi.textviewRent.text = "Rental $".plus(value)
                                bi.textviewRent.visibility = View.VISIBLE
                            }
                        } catch(e:Exception){}
                    }
                    bi.tvResidence.setText(requireContext().getString(R.string.add_previous_address))
            }
            } ?: run { bi.tvResidence.setText(requireContext().getString(R.string.add_current_address)) }

        } catch (e : Exception){
            Log.e("Exception-InfoFrag","setCurrentAddress")
        }
    }

    private fun setMaritalStatus(maritalStatusModel: MaritalStatus){
        maritalStatus = maritalStatusModel
         maritalStatusModel?.let {
            it.isInRelationship?.let { isInRelation->
                msBinding.unmarriedAddendum.visibility = View.VISIBLE
                msBinding.rbUnmarried.isChecked = true
                msBinding.rbUnmarried.setTypeface(null, Typeface.BOLD)

                if(isInRelation){
                    msBinding.tvIsInRelationship.text = "Yes"
                } else{
                    msBinding.tvIsInRelationship.text = "No"
                }
            }
        }
    }

    private fun addEmptyDependentField(){
        if(listItems.size < 99) {
            if (listItems.size > 0) {
                var ordinal = getOrdinal(listItems.size + 1)
                listItems.add(Dependent(ordinal.plus(" Dependent Age (Years)"), 0))

            } else {
                listItems.clear()
                var ordinal = getOrdinal(1)
                listItems.add(Dependent(ordinal.plus(" Dependent Age (Years)"), 0))
            }
            bi.rvDependents.adapter = dependentAdapter
            dependentAdapter.notifyDataSetChanged()
            bi.tvDependentCount.setText(listItems.size.toString())
        }
    }

    override fun onItemClick(position: Int) {
        listItems.removeAt(position)
        //update list/ recreate again
        for(i in 0 until listItems.size){
            if (i + 1 <= listItems.size) {
                var ordinal = getOrdinal(i + 1)
                var age = listItems.get(i).age
                listItems[i]= Dependent((ordinal.plus(" Dependent Age (Years)")),age)
            }
        }
        bi.rvDependents.adapter = dependentAdapter
        dependentAdapter.notifyDataSetChanged()
        bi.tvDependentCount.setText(listItems.size.toString())
        hideSoftKeyboard()
    }

    override fun onClick(view: View?) {
        //val checked = (view as RadioButton).isChecked
        when (view?.getId()) {
            R.id.rb_unmarried -> if (msBinding.rbUnmarried.isChecked) maritalStatusClick(true, false, false)
            R.id.rb_married -> if (msBinding.rbMarried.isChecked) maritalStatusClick(false, true, false)
            R.id.rb_separated -> if (msBinding.rbSeparated.isChecked) maritalStatusClick(false, false, true)
            R.id.rb_us_citizen -> if (citizenshipBinding.rbUsCitizen.isChecked) setCitizenship(true, false, false)
            R.id.rb_pr -> if (citizenshipBinding.rbPr.isChecked) setCitizenship(false, true, false)
            R.id.rb_non_pr_other -> if (citizenshipBinding.rbNonPrOther.isChecked) setCitizenship(false, false, true)
            R.id.chb_duty_personel -> militaryActivePersonel()
            //R.id.chb_res_national_guard -> militaryNationalGuard()
            R.id.chb_veteran -> militaryVeteran()
            R.id.chb_surviving_spouse -> militarySurvivingSpouse()
        }
    }

    private fun setViews() {

        msBinding.rbUnmarried.setOnClickListener(this)
        msBinding.rbMarried.setOnClickListener(this)
        msBinding.rbSeparated.setOnClickListener(this)
        citizenshipBinding.rbUsCitizen.setOnClickListener(this)
        citizenshipBinding.rbNonPrOther.setOnClickListener(this)
        citizenshipBinding.rbPr.setOnClickListener(this)
        bindingMilitary.chbDutyPersonel.setOnClickListener(this)
        bindingMilitary.chbResNationalGuard.setOnClickListener(this)
        bindingMilitary.chbVeteran.setOnClickListener(this)
        bindingMilitary.chbSurvivingSpouse.setOnClickListener(this)
        bi.addDependentClick.setOnClickListener(this)
        bi.addPrevAddress.setOnClickListener(this)
        bi.edDateOfBirth.setOnClickListener(this)

        // initialize recyclerview
        dependentAdapter = DependentAdapter(requireActivity(),listItems,this@PrimaryBorrowerInfoFragment)
        bi.rvDependents.adapter = dependentAdapter

        setupUI()
        setSingleItemFocus()
        setEndIconClicks()
        setNumberFormts()

        msBinding.unmarriedAddendum.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(AppConstant.marital_status,maritalStatus)
            findNavController().navigate(R.id.action_info_unmarried_addendum,bundle)
        }

        bindingMilitary.layoutActivePersonnel.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(AppConstant.service_date,militaryServiceDate)
            findNavController().navigate(R.id.action_info_active_duty,bundle)
        }
        //bindingMilitary.layoutNationalGuard.setOnClickListener { findNavController().navigate(R.id.action_info_reserve) }
        citizenshipBinding.layoutVisaStatusOther.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(AppConstant.borrower_citizenship,citizenship)
            findNavController().navigate(R.id.action_info_non_pr,bundle)
        }

        // clicks
        /*msBinding.rbUnmarried.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                msBinding.rbUnmarried.setTypeface(null, Typeface.BOLD)
                //msBinding.rbMarried.setTypeface(null, Typeface.NORMAL)
                //msBinding.rbSeparated.setTypeface(null, Typeface.NORMAL)
            } else {
                msBinding.rbUnmarried.setTypeface(null, Typeface.NORMAL)
                msBinding.unmarriedAddendum.visibility = View.GONE
            }
        } */
    }

    private fun setupUI(){

        bi.currentAddressLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(AppConstant.current_address,currentAddressFullDetail)
            findNavController().navigate(R.id.action_info_current_address,bundle)
        }

        bi.btnSaveInfo.setOnClickListener { sendBorrowerData() }

        bi.backButtonInfo.setOnClickListener {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold,R.anim.slide_out_left)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }

        bi.mainConstraintLayout.setOnClickListener {
            HideSoftkeyboard.hide(requireActivity(),   bi.mainConstraintLayout)
            super.removeFocusFromAllFields(bi.mainConstraintLayout)
        }

        bi.addDependentClick.setOnClickListener{ addEmptyDependentField() }

        bi.addPrevAddress.setOnClickListener{
            if (bi.tvResidence.text.equals(getString(R.string.add_current_address))){
                findNavController().navigate(R.id.action_info_current_address)
            } else {
                val bundle = Bundle()
                bundle.putInt("position",listAddress.size)
                bundle.putString("type","add_new")
                bundle.putParcelable(AppConstant.previous_address,null)
                findNavController().navigate(R.id.action_info_previous_address,bundle)
            }
        }

        //married
        msBinding.rbMarried.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                msBinding.rbMarried.setTypeface(null, Typeface.BOLD)
            else
                msBinding.rbMarried.setTypeface(null, Typeface.NORMAL)
        }
        //separated
        msBinding.rbSeparated.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                msBinding.rbSeparated.setTypeface(null, Typeface.BOLD)
            else
                msBinding.rbSeparated.setTypeface(null, Typeface.NORMAL)
        }
        //us citizen
        citizenshipBinding.rbUsCitizen.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                citizenshipBinding.rbUsCitizen.setTypeface(null, Typeface.BOLD)
            else
                citizenshipBinding.rbUsCitizen.setTypeface(null, Typeface.NORMAL)
        }


        // non permanent residence
        citizenshipBinding.rbNonPrOther.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                citizenshipBinding.rbNonPrOther.setTypeface(null, Typeface.BOLD)
            else
                citizenshipBinding.rbNonPrOther.setTypeface(null, Typeface.NORMAL)
        }
        //permanent residence
        citizenshipBinding.rbPr.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                citizenshipBinding.rbPr.setTypeface(null, Typeface.BOLD)
            else
                citizenshipBinding.rbPr.setTypeface(null, Typeface.NORMAL)
        }
        // active duty personel
        bindingMilitary.chbDutyPersonel.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                bindingMilitary.chbDutyPersonel.setTypeface(null, Typeface.BOLD)
            else
                bindingMilitary.chbDutyPersonel.setTypeface(null, Typeface.NORMAL)
        }


        //reserve or national guard
        bindingMilitary.chbResNationalGuard.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                bindingMilitary.chbResNationalGuard.setTypeface(null, Typeface.BOLD)
                bindingMilitary.layoutNationalGuard.visibility = View.VISIBLE
            }
            else {
                bindingMilitary.chbResNationalGuard.setTypeface(null, Typeface.NORMAL)
                bindingMilitary.layoutNationalGuard.visibility = View.GONE
            }
        }
        // veteran
        bindingMilitary.chbVeteran.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                bindingMilitary.chbVeteran.setTypeface(null, Typeface.BOLD)
            else
                bindingMilitary.chbVeteran.setTypeface(null, Typeface.NORMAL)
        }

        //surviving spouse
        bindingMilitary.chbSurvivingSpouse.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                bindingMilitary.chbSurvivingSpouse.setTypeface(null, Typeface.BOLD)
            else
                bindingMilitary.chbSurvivingSpouse.setTypeface(null, Typeface.NORMAL)
        }

        bindingMilitary.chbResNationalGuard.setOnClickListener {
            if (bindingMilitary.chbResNationalGuard.isChecked) {
                val bundle = Bundle()
                reserveEverActivated?.let { bundle.putBoolean(AppConstant.RESERVE_ACTIVATED, it) }
                findNavController().navigate(R.id.action_info_reserve,bundle)
                bindingMilitary.layoutNationalGuard.visibility = View.VISIBLE
            }
            else {
                bindingMilitary.layoutNationalGuard.visibility = View.GONE
                bindingMilitary.chbResNationalGuard.setTypeface(null, Typeface.NORMAL)
            }

            bindingMilitary.layoutNationalGuard.setOnClickListener {
                val bundle = Bundle()
                reserveEverActivated?.let { bundle.putBoolean(AppConstant.RESERVE_ACTIVATED, it) }
                findNavController().navigate(R.id.action_info_reserve,bundle)
            }
        }
    }

    fun setError(textInputlayout: TextInputLayout, errorMsg: String) {
        textInputlayout.helperText = errorMsg
        textInputlayout.setBoxStrokeColorStateList(
            AppCompatResources.getColorStateList(requireContext(), R.color.primary_info_stroke_error_color))
    }

    fun clearError(textInputlayout: TextInputLayout){
        textInputlayout.helperText = ""
        textInputlayout.setBoxStrokeColorStateList(
            AppCompatResources.getColorStateList(requireContext(), R.color.primary_info_line_color))
    }

    private fun setEndIconClicks(){
        bi.layoutDateOfBirth.setEndIconOnClickListener(View.OnClickListener {
            openCalendar()
        })
        // click for security number
        bi.layoutSecurityNum.setEndIconOnClickListener(View.OnClickListener {
            if (bi.edSecurityNum.getTransformationMethod()
                    .equals(PasswordTransformationMethod.getInstance())
            ) { //  hide password
                bi.edSecurityNum.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                bi.layoutSecurityNum.setEndIconDrawable(R.drawable.ic_eye_hide)
            } else {
                bi.edSecurityNum.setTransformationMethod(PasswordTransformationMethod.getInstance())
                bi.layoutSecurityNum.setEndIconDrawable(R.drawable.ic_eye_icon_svg)
            }
        })

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setResidence() {
        // set btn text

        //bi.recyclerview.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        //bi.recyclerview.hasFixedSize()

        adapter.setTaskList(listAddress)
        bi.recyclerview.setAdapter(adapter)

        touchListener = RecyclerTouchListener(requireActivity(), bi.recyclerview)
        touchListener!!
            .setClickable(object : RecyclerTouchListener.OnRowClickListener {
                override fun onRowClicked(position: Int){
                    /*if(listAddress.get(position).isCurrentAddress){
                        addressBtnText = getString(R.string.previous_address)
                        val bundle = Bundle()
                        bundle.putString(AppConstant.showData,AppConstant.showData)
                        findNavController().navigate(R.id.action_info_current_address,bundle)
                    }
                    else {
                        if(listAddress.get(0).isCurrentAddress){
                            addressBtnText = getString(R.string.previous_address)
                        } else {
                            addressBtnText = getString(R.string.current_address)
                        }
                        val bundle = Bundle()
                        bundle.putInt(AppConstant.address,position)
                        findNavController().navigate(R.id.action_info_previous_address,bundle)
                    } */

                    val bundle = Bundle()
                    bundle.putInt("position",position)
                    //bundle.putString("type","idontknow")
                    selectedPosition = position
                    bundle.putParcelable(AppConstant.previous_address,listAddress.get(position))
                    //Log.e("Prevaddress",""+listAddress.get(position))
                    findNavController().navigate(R.id.action_info_previous_address,bundle)
                }
                override fun onIndependentViewClicked(independentViewID: Int, position: Int) {}
            })
            .setSwipeOptionViews(R.id.delete_task)
            .setSwipeable(R.id.rowFG, R.id.rowBG, object :
                RecyclerTouchListener.OnSwipeOptionsClickListener {

                override fun onSwipeOptionClicked(viewID: Int, position: Int) {
                    //var text = getString(R.string.delete_prev_address)
                    selectedPosition = position
                    var message : String = "Are you sure you want to delete Previous Residence?"
                    if(firstName != null) {
                        message = "Are you sure you want to delete ".plus(firstName).plus("'s Previous Residence?")
                    }

                    DeleteCurrentResidenceDialogFragment.newInstance(message).show(childFragmentManager, DeleteCurrentResidenceDialogFragment::class.java.canonicalName)
                }
            })
        bi.recyclerview.addOnItemTouchListener(touchListener!!)


        // disable touch from recyclerview
        bi.recyclerview.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                bi.scrollPrimaryInfo.requestDisallowInterceptTouchEvent(true)
                return false
            }
        })
    }

    private fun setSingleItemFocus(){

        // check first name
        bi.edFirstName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setTextInputLayoutHintColor(bi.layoutFirstName, R.color.grey_color_two )
            } else {
                if (bi.edFirstName.text?.length == 0) {
                    setTextInputLayoutHintColor(bi.layoutFirstName,R.color.grey_color_three)
                } else {
                    setTextInputLayoutHintColor(bi.layoutFirstName,R.color.grey_color_two)
                    clearError(bi.layoutFirstName)
                }
            }
        }

        // last name
        bi.edLastName.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setTextInputLayoutHintColor(bi.layoutLastName, R.color.grey_color_two )
            } else {
                if (bi.edLastName.text?.length == 0) {
                    setTextInputLayoutHintColor(bi.layoutLastName,R.color.grey_color_three)
                } else {
                    setTextInputLayoutHintColor(bi.layoutLastName,R.color.grey_color_two)
                    clearError(bi.layoutLastName)
                }
            }
        }

        // home number
        bi.edHomeNumber.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setTextInputLayoutHintColor(bi.layoutHomeNum, R.color.grey_color_two )
            } else {
                if (bi.edHomeNumber.text?.length == 0) {
                    setTextInputLayoutHintColor(bi.layoutHomeNum,R.color.grey_color_three)
                } else {
                    setTextInputLayoutHintColor(bi.layoutHomeNum,R.color.grey_color_two)
                    if (bi.edHomeNumber.text?.length!! < 14) {
                        setError(bi.layoutHomeNum, getString(R.string.invalid_phone_num))
                    } else {
                        clearError(bi.layoutHomeNum)
                    }
                }
            }
        }

        // email
        bi.edEmail.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                setTextInputLayoutHintColor(bi.layoutEmail, R.color.grey_color_two)
            } else {
                if (bi.edEmail.text?.length == 0) {
                    setTextInputLayoutHintColor(bi.layoutEmail, R.color.grey_color_three)
                } else {
                    setTextInputLayoutHintColor(bi.layoutEmail, R.color.grey_color_two)

                    if (!isValidEmailAddress(bi.edEmail.text.toString().trim())) {
                        setError(bi.layoutEmail, getString(R.string.invalid_email))
                    } else {
                        clearError(bi.layoutEmail)
                    }
                }
            }
        }

        bi.edDateOfBirth.showSoftInputOnFocus = false
        bi.edDateOfBirth.setOnClickListener { openCalendar() }
        bi.edDateOfBirth.setOnFocusChangeListener{ _ , _ ->  openCalendar() }

        bi.edMiddleName.setOnFocusChangeListener(CustomFocusListenerForEditText(bi.edMiddleName, bi.layoutMiddleName, requireContext()))
        bi.edSuffix.setOnFocusChangeListener(CustomFocusListenerForEditText(bi.edSuffix, bi.layoutSuffix, requireContext()))
        bi.edWorkNum.setOnFocusChangeListener(CustomFocusListenerForEditText(bi.edWorkNum,bi.layoutWorkNum, requireContext()))
        bi.edExtNum.setOnFocusChangeListener(CustomFocusListenerForEditText(bi.edExtNum, bi.layoutExtNum, requireContext()))
        bi.edCellNum.setOnFocusChangeListener(CustomFocusListenerForEditText(bi.edCellNum, bi.layoutCellNum, requireContext()))
        bi.edSecurityNum.setOnFocusChangeListener(CustomFocusListenerForEditText(bi.edSecurityNum,bi.layoutSecurityNum, requireContext()))
        bi.edDateOfBirth.setOnFocusChangeListener(CustomFocusListenerForEditText(bi.edDateOfBirth, bi.layoutDateOfBirth, requireContext()))

    }

    private fun checkDependentData(){

        var textInputLayout : TextInputLayout
        for(item in 0 until bi.rvDependents.childCount){

            textInputLayout = bi.rvDependents.layoutManager?.findViewByPosition(item)?.findViewById<TextInputLayout>(R.id.til_dependent)!!
            var text =  textInputLayout.editText?.text.toString()
            if(text.isEmpty() || text.isBlank()){
                textInputLayout.helperText = getString(R.string.error_field_required)
                textInputLayout.setBoxStrokeColorStateList(AppCompatResources.getColorStateList(requireContext(), R.color.primary_info_stroke_error_color))

            } else if(text.length>0){
                clearError(textInputLayout)
            }
        }
    }

    private fun setTextInputLayoutHintColor(textInputLayout: TextInputLayout, @ColorRes colorIdRes: Int){
        textInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), colorIdRes))
    }

    private fun maritalStatusClick(isUnmarried: Boolean, isMarried: Boolean, isSeparated: Boolean){
        val bundle = Bundle()

        if (isUnmarried) {
            /*msBinding.rbUnmarried.setTypeface(null, Typeface.BOLD)
            msBinding.rbMarried.setTypeface(null, Typeface.NORMAL)
            msBinding.rbSeparated.setTypeface(null, Typeface.NORMAL) */

               maritalStatus?.let {
                   //maritalStatus = it
                   if (it.maritalStatusId == 1)
                       msBinding.rbMarried.isChecked = true
                   if (it.maritalStatusId == 2)
                       msBinding.rbSeparated.isChecked = true
                   if (it.maritalStatusId == 9){
                       msBinding.rbUnmarried.isChecked = true
                       setMaritalStatus(it)
                   }
               }

               bundle.putParcelable(AppConstant.marital_status,maritalStatus)
            findNavController().navigate(R.id.action_info_unmarried_addendum,bundle)
        }
        if (isMarried) {
            msBinding.unmarriedAddendum.visibility = View.GONE
            msBinding.rbUnmarried.setTypeface(null, Typeface.NORMAL)
            msBinding.rbMarried.setTypeface(null, Typeface.BOLD)
            msBinding.rbSeparated.setTypeface(null, Typeface.NORMAL)
            bundle.putString(AppConstant.marriage_type,AppConstant.married)
            bundle.putParcelable(AppConstant.marital_status,maritalStatus)
            findNavController().navigate(R.id.action_marriage_info,bundle)
        }
        if (isSeparated) {
            msBinding.unmarriedAddendum.visibility = View.GONE
            msBinding.rbUnmarried.setTypeface(null, Typeface.NORMAL)
            msBinding.rbMarried.setTypeface(null, Typeface.NORMAL)
            msBinding.rbSeparated.setTypeface(null, Typeface.BOLD)
            bundle.putString(AppConstant.marriage_type,AppConstant.separated)
            bundle.putParcelable(AppConstant.marital_status,maritalStatus)
            findNavController().navigate(R.id.action_marriage_info,bundle)
        }
    }

    private fun setCitizenship(usCitizen: Boolean, PR: Boolean, nonPR: Boolean){
        if (usCitizen) {
            citizenshipBinding.layoutVisaStatusOther.visibility = View.GONE
            citizenshipBinding.rbUsCitizen.setTypeface(null, Typeface.BOLD)
            citizenshipBinding.rbPr.setTypeface(null, Typeface.NORMAL)
            citizenshipBinding.rbNonPrOther.setTypeface(null, Typeface.NORMAL)
        }
        if (PR) {
            citizenshipBinding.layoutVisaStatusOther.visibility = View.GONE
            citizenshipBinding.rbUsCitizen.setTypeface(null, Typeface.NORMAL)
            citizenshipBinding.rbPr.setTypeface(null, Typeface.BOLD)
            citizenshipBinding.rbNonPrOther.setTypeface(null, Typeface.NORMAL)
        }
        if (nonPR) {
            //citizenshipBinding.layoutVisaStatusOther.visibility = View.VISIBLE
            citizenshipBinding.rbUsCitizen.setTypeface(null, Typeface.NORMAL)
            citizenshipBinding.rbPr.setTypeface(null, Typeface.NORMAL)
            citizenshipBinding.rbNonPrOther.setTypeface(null, Typeface.BOLD)

            val bundle = Bundle()
            bundle.putParcelable(AppConstant.borrower_citizenship,citizenship)
            findNavController().navigate(R.id.action_info_non_pr,bundle)
        }
    }

    private fun militaryActivePersonel() {

        if(bindingMilitary.chbDutyPersonel.isChecked) {
            bindingMilitary.layoutActivePersonnel.visibility = View.VISIBLE
            bindingMilitary.chbDutyPersonel.setTypeface(null, Typeface.BOLD)
            val bundle = Bundle()
            bundle.putString(AppConstant.service_date,militaryServiceDate)
            findNavController().navigate(R.id.action_info_active_duty,bundle)
        } else {
            bindingMilitary.layoutActivePersonnel.visibility = View.GONE
            bindingMilitary.chbDutyPersonel.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun militaryVeteran() {
        if (bindingMilitary.chbVeteran.isChecked) {
            bindingMilitary.chbVeteran.setTypeface(null, Typeface.BOLD)

        } else {
            bindingMilitary.chbVeteran.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun militarySurvivingSpouse() {
        if (bindingMilitary.chbSurvivingSpouse.isChecked) {
            bindingMilitary.chbSurvivingSpouse.setTypeface(null, Typeface.BOLD)

        } else {
            bindingMilitary.chbSurvivingSpouse.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun setNumberFormts(){
        bi.edHomeNumber.addTextChangedListener(PhoneTextFormatter(bi.edHomeNumber, "(###) ###-####"))
        bi.edWorkNum.addTextChangedListener(PhoneTextFormatter(bi.edWorkNum, "(###) ###-####"))
        bi.edCellNum.addTextChangedListener(PhoneTextFormatter(bi.edCellNum, "(###) ###-####"))

        // security number format
        bi.edSecurityNum.addTextChangedListener(object : TextWatcher {
            var beforeLength = 0
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                beforeLength = bi.edSecurityNum.length()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val digits: Int = bi.edSecurityNum.getText().toString().length
                if (beforeLength < digits && (digits == 3 || digits == 6)) {
                    bi.edSecurityNum.append("-")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

    }

    private fun displayAddress(address: AddressModel){
        val builder = StringBuilder()
        address.street?.let {
            if(it != "null")
                builder.append(it).append(" ")
        }
        address.unit?.let {
            if(it != "null")
                builder.append(it).append(",")
            else
                builder.append(",")

        } ?: run { builder.append(",") }

        address.city?.let {
            if(it != "null")
                builder.append("\n").append(it).append(",").append(" ")
        } ?: run { builder.append("\n") }

        address.stateName?.let {
            if(it !="null") builder.append(it).append(" ")
        }
        address.zipCode?.let {
            if(it != "null")
                builder.append(it)
        }
        bi.textviewCurrentAddress.text = builder

    }

    private fun isValidEmailAddress(email: String?): Boolean {
        val ePattern =
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,3}))$"
        val p = Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()
    }

    private fun openCalendar(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // New Style Calendar Added....
        val datePickerDialog = DatePickerDialog(
            requireActivity(), R.style.MySpinnerDatePickerStyle, {
                    view, selectedYear, monthOfYear, dayOfMonth -> bi.edDateOfBirth.setText("" + (monthOfYear+1) + "/" + dayOfMonth + "/" + selectedYear) },
            year, month, day
        )
        datePickerDialog.datePicker.maxDate= System.currentTimeMillis()
        datePickerDialog.show()
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
    fun onSwipeDeleteReceivedEvent(event: SwipeToDeleteEvent){
        if(event.boolean){
            selectedPosition?.let {
               // bi.tvResidence.setText(getString(R.string.previous_address))
            /*if(listAddress.get(selectedPosition!!).isCurrentAddress) {
                    bi.tvResidence.setText(getString(R.string.current_address))
                } else {
                    if (listAddress.get(0).isCurrentAddress) {
                        bi.tvResidence.setText(getString(R.string.previous_address))
                    } else {
                        bi.tvResidence.setText(getString(R.string.current_address))
                    }
                } */
            }

            viewModel.addUpdateDeleteResponse.observe(viewLifecycleOwner, { response ->
                val codeString = response.code.toString()
                if(codeString == "400" || codeString == "200"){
                    //EventBus.getDefault().postSticky(BorrowerApplicationUpdatedEvent(objectUpdated = true)) //requireActivity().finish()
                       //Log.e("Delete Event","observed")
                    }
                })
            try {
                lifecycleScope.launchWhenStarted {
                    sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                        listAddress.get(selectedPosition!!).id?.let {
                            if (loanApplicationId != null) {
                                //Log.e("call delete api",""+ selectedPosition)
                                viewModel.deletePreviousAddress(authToken, loanApplicationId!!, it)
                            }
                        }
                    }
                }

                listAddress.removeAt(selectedPosition!!)
                adapter.setTaskList(listAddress)

            } catch (e:java.lang.Exception){}

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSentData(event: SendDataEvent){
        bi.loaderBorrowerInfo.visibility = View.GONE
        if(event.addUpdateDataResponse.code == AppConstant.RESPONSE_CODE_SUCCESS){
            EventBus.getDefault().postSticky(BorrowerApplicationUpdatedEvent(objectUpdated = true))
            requireActivity().finish()
        }
        else if(event.addUpdateDataResponse.code == AppConstant.INTERNET_ERR_CODE)
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG)
        else
            if (event.addUpdateDataResponse.message != null)
                SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG)


        requireActivity().finish()
    }

    fun getOrdinal(i: Int): String? {
        val mod100 = i % 100
        val mod10 = i % 10
        if (mod10 == 1 && mod100 != 11) {
            return i.toString() + "st"
        } else if (mod10 == 2 && mod100 != 12) {
            return i.toString() + "nd"
        } else if (mod10 == 3 && mod100 != 13) {
            return i.toString() + "rd"
        } else {
            return  i.toString() + "th"
        }
    }

    private fun hideSoftKeyboard(){
        val imm = view?.let { ContextCompat.getSystemService(it.context, InputMethodManager::class.java) }
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onAddressClick(position: Int) {
       // Log.e("callback", "here")
    }

    private fun hideLoader(){
        val  activity = (activity as? BorrowerAddressActivity)
        activity?.binding?.loaderInfo?.visibility = View.GONE
    }


    // done button click of add dependents
    /*bi.edDependents.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            addDependentField()
            requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }
        false
    } */


    /* removed validations
       val firstName: String = bi.edFirstName.text.toString()
        val lastName: String = bi.edLastName.text.toString()
        val email: String = bi.edEmail.text.toString().trim()
        val homeNum: String = bi.edHomeNumber.text.toString().trim()

        if (firstName.isEmpty() || firstName.length == 0) {
            setError(bi.layoutFirstName, getString(R.string.error_field_required))
        }
        if (lastName.isEmpty() || lastName.length == 0) {
            setError(bi.layoutLastName, getString(R.string.error_field_required))
        }
        if (email.isEmpty() || email.length == 0) {
            setError(bi.layoutEmail, getString(R.string.error_field_required))
        }
        if (homeNum.isEmpty() || homeNum.length == 0) {
            setError(bi.layoutHomeNum, getString(R.string.error_field_required))
        }
        if (firstName.isNotEmpty() && firstName.length > 0) {
            clearError(bi.layoutFirstName)
        }
        if (lastName.isNotEmpty() && lastName.length > 0) {
            clearError(bi.layoutLastName)
        }
        if (email.isNotEmpty() && email.length > 0){
            if (!isValidEmailAddress(email.trim())) {
                setError(bi.layoutEmail, getString(R.string.invalid_email))
            } else {
                clearError(bi.layoutEmail)
            }
        }

        if (homeNum.isNotEmpty() && homeNum.isNotBlank()) {
            if (homeNum.length < 14) {
                setError(bi.layoutHomeNum, getString(R.string.invalid_phone_num))
            } else {
                clearError(bi.layoutHomeNum)
            }
        }
        if(!bi.tvDependentCount.text.equals("0")){
            checkDependentData()
        }

        if(firstName.length >0 && lastName.length > 0 && homeNum.length > 0 && isValidEmailAddress(email.trim()) && loanApplicationId !=null) {

     */


}