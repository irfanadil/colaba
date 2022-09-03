package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.rnsoft.colabademo.databinding.AppHeaderWithCrossDeleteBinding
import com.rnsoft.colabademo.databinding.RealEstateOwnedLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields

import com.rnsoft.colabademo.utils.NumberTextFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject


/**
 * Created by Anita Kiran on 9/16/2021.
 */
@AndroidEntryPoint
class RealEstateOwnedFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: RealEstateOwnedLayoutBinding
    private lateinit var toolbar: AppHeaderWithCrossDeleteBinding
    private val viewModel : RealEstateViewModel by activityViewModels()
    private var savedViewInstance: View? = null
    var realEstateAddress = AddressData()
    var addressHeading: String? = null
    var firstMortgageModel: FirstMortgageModel? =  null
    var secondMortgageModel : SecondMortgageModel? = null
    private var propertyTypeList: ArrayList<DropDownResponse> = arrayListOf()
    private var occupancyTypeList:ArrayList<DropDownResponse> = arrayListOf()
    private var propertyStatusList:ArrayList<DropDownResponse> = arrayListOf()
    var propertyInfoId: Int? = null
    var borrowerId: Int? = null
    var borrowerPropertyId :Int? = null
    private var loanApplicationId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         return if (savedViewInstance != null) {
            savedViewInstance
        } else {
             binding = RealEstateOwnedLayoutBinding.inflate(inflater, container, false)
             toolbar = binding.headerRealestate
             savedViewInstance = binding.root
             super.addListeners(binding.root)

             // set Header title
             toolbar.toolbarTitle.setText(getString(R.string.real_estate_owned))

             val activity = (activity as? RealEstateActivity)
             activity?.loanApplicationId?.let { loanId -> loanApplicationId = loanId }
             activity?.borrowerPropertyId?.let {
                 if (it > 0)
                     borrowerPropertyId = it
             }
             activity?.borrowerId?.let { borrowerId = it }
             activity?.propertyInfoId?.let {
                 if (it > 0)
                     propertyInfoId = it
             }
             activity?.borrowerName?.let {
                 toolbar.borrowerPurpose.setText(it)
             }

             //Log.e("fragment","loanApplicatioId: " + loanApplicationId + " borrowerPropertyId:" + borrowerPropertyId + " borrowerId: " + borrowerId)

             if (borrowerPropertyId == null || borrowerPropertyId == 0) {
                 toolbar.btnTopDelete.visibility = View.GONE
                 showHideAddress(false, true)
             } else {
                 //Log.e("loanApplicationId: ", ""+ loanApplicationId + " borrwerId:" + borrowerId)
                 toolbar.btnTopDelete.visibility = View.VISIBLE
                 toolbar.btnTopDelete.setOnClickListener {
                     DeleteRealEstateDialogFragment.newInstance(AppConstant.real_estate_delete_text).show(childFragmentManager, DeleteRealEstateDialogFragment::class.java.canonicalName)
                 }
             }

             initViews()
             setDropDownData()

           savedViewInstance
         }
    }

    private fun getRealEstateDetails(counter:Int){
        hideLoader()
        if(counter == 3) {
            viewModel.realEstateDetails.observe(viewLifecycleOwner, {
                if (propertyTypeList.size > 0 && occupancyTypeList.size > 0 && propertyStatusList.size > 0) {
                    if (it != null) {
                        //Log.e("Details",""+it.data)
                        it.data?.address?.let {
                            //binding.tvPropertyAddress.text = it.street+" "+it.unit+"\n"+it.city+" "+it.stateName+" "+it.zipCode+" "+it.countryName
                            addressHeading = it.street
                            realEstateAddress = it
                            displayAddress(it)
                        } ?: run { showHideAddress(false, true) }

                        it.data?.rentalIncome?.let {
                            binding.edRentalIncome.setText(it.toString())
                            binding.layoutRentalIncome.visibility = View.VISIBLE
                            CustomMaterialFields.setColor(
                                binding.layoutRentalIncome,
                                R.color.grey_color_two,
                                requireActivity()
                            )
                        }

                        it.data?.propertyTypeId?.let { propertyId ->
                            //Log.e("details", "propertyType" + propertyTypeList.size)
                            for (item in propertyTypeList) {
                                if (item.id == propertyId) {
                                    binding.tvPropertyType.setText(item.name, false)
                                    CustomMaterialFields.setColor(
                                        binding.layoutPropertyType,
                                        R.color.grey_color_two,
                                        requireActivity()
                                    )
                                    break
                                }
                            }
                        }
                        // occupancy id
                        it.data?.occupancyTypeId?.let { occupancyId ->
                            for (item in occupancyTypeList) {
                                if (item.id == occupancyId) {
                                    binding.tvOccupancyType.setText(item.name, false)
                                    CustomMaterialFields.setColor(
                                        binding.layoutOccupancyType,
                                        R.color.grey_color_two,
                                        requireActivity()
                                    )
                                    break
                                }
                            }
                        }
                        // property Status
                        it.data?.propertyStatus?.let { statusId ->
                            for (item in propertyStatusList) {
                                if (item.id == statusId) {
                                    binding.tvPropertyStatus.setText(item.name, false)
                                    CustomMaterialFields.setColor(
                                        binding.layoutPropertyStatus,
                                        R.color.grey_color_two,
                                        requireActivity()
                                    )
                                    break
                                }
                            }
                        }

                        // hoa dues
                        it.data?.hoaDues?.let { value ->
                            binding.edAssociationDues.setText(Math.round(value).toString())
                            CustomMaterialFields.setColor(
                                binding.layoutAssociationDues,
                                R.color.grey_color_two,
                                requireActivity()
                            )
                        }
                        // property value
                        it.data?.appraisedPropertyValue?.let { value ->
                            binding.edPropertyValue.setText(Math.round(value).toString())
                            CustomMaterialFields.setColor(
                                binding.layoutPropertyValue,
                                R.color.grey_color_two,
                                requireActivity()
                            )
                        }
                        // property tax
                        it.data?.propertyTax?.let { value ->
                            binding.edPropertyTax.setText(Math.round(value).toString())
                            CustomMaterialFields.setColor(
                                binding.layoutPropertyTaxes,
                                R.color.grey_color_two,
                                requireActivity()
                            )
                        }
                        // home owner insurance
                        it.data?.homeOwnerInsurance?.let { value ->
                            binding.edHomeownerInsurance.setText(Math.round(value).toString())
                            CustomMaterialFields.setColor(
                                binding.layoutHomeownerInsurance,
                                R.color.grey_color_two,
                                requireActivity()
                            )
                        }
                        //  flood insurance
                        it.data?.floodInsurance?.let { value ->
                            binding.edFloodInsurance.setText(Math.round(value).toString())
                            CustomMaterialFields.setColor(
                                binding.layoutFloodInsurance,
                                R.color.grey_color_two,
                                requireActivity()
                            )
                        }

                        //Log.e("firstMortage",""+ it.data?.firstMortgageModel)
                        // has first mortgage 'yes'
                        if (it.data?.hasFirstMortgage != null) {
                            if (it.data.hasFirstMortgage) {
                                binding.rbFirstMortgageYes.isChecked = true
                                binding.layoutFirstMortgageDetail.visibility = View.VISIBLE
                                binding.layoutSecondMortgage.visibility = View.VISIBLE

                                it.data.firstMortgageModel?.let { model ->
                                    setFirstMorgageDetails(model)
                                }

                                // check for second mortgage
                                it.data?.hasSecondMortgage?.let { isSecMortgage ->
                                    if (isSecMortgage) {
                                        binding.rbSecMortgageYes.isChecked = true
                                        it.data.secondMortgageModel?.let { model ->
                                            setSecondMortgageDetails(model)
                                        }
                                    }
                                    if (!isSecMortgage) {
                                        binding.rbSecMortgageNo.isChecked = true
                                    }
                                }

                            } else if (!it.data.hasFirstMortgage) {
                                binding.rbFirstMortgageNo.isChecked = true
                                binding.rbFirstMortgageNo.performClick()
                            }
                        }

                        // has second mortgage 'yes'
                        /*if (it.data?.hasSecondMortgage != null){
                    if (it.data.hasSecondMortgage){
                        binding.rbSecMortgageYes.isChecked = true
                        binding.layoutSecMortgageDetail.visibility = View.VISIBLE

                        it.data.secondMortgageModel?.let { model ->
                            setSecondMortgageDetails(model)
                        }
                    }
                    else if(!it.data.hasSecondMortgage){
                        binding.rbSecMortgageNo.isChecked = true
                        binding.rbSecMortgageNo.performClick()
                    }
                } */
                    }
                }
            })
            hideLoader()
        }

    }

    private fun setFirstMorgageDetails(model: FirstMortgageModel){
        val formatter = DecimalFormat("#,###,###")
        firstMortgageModel = model

        binding.rbFirstMortgageYes.isChecked = true
        binding.rbFirstMortgageYes.setTypeface(null, Typeface.BOLD)
        binding.rbFirstMortgageNo.isChecked = false
        binding.rbFirstMortgageNo.setTypeface(null, Typeface.NORMAL)

        binding.layoutFirstMortgageDetail.visibility = View.VISIBLE
        binding.layoutSecondMortgage.visibility = View.VISIBLE

        model.firstMortgagePayment?.let { payment->
            val newPayment: String = formatter.format(Math.round(payment))
            binding.firstMortgagePayment.setText("$" +newPayment)
        } ?: run {
            binding.firstMortgagePayment.setText("$0")
        }
        model.unpaidFirstMortgagePayment?.let{ balance ->
            val newBalance: String = formatter.format(Math.round(balance))
            binding.firstMortgageBalance.setText("$" + newBalance)
        } ?: run{
            binding.firstMortgageBalance.setText("$0")
        }
    }

    private fun setSecondMortgageDetails(model: SecondMortgageModel){
        val formatter = DecimalFormat("#,###,###")
        secondMortgageModel = model
        binding.rbSecMortgageYes.isChecked = true
        binding.rbSecMortgageYes.setTypeface(null, Typeface.BOLD)
        binding.rbSecMortgageNo.isChecked = false
        binding.rbSecMortgageNo.setTypeface(null, Typeface.NORMAL)
        binding.layoutSecMortgageDetail.visibility = View.VISIBLE

        model.secondMortgagePayment?.let { payment->
            val newPayment: String = formatter.format(Math.round(payment))
            binding.secMortgagePayment.setText("$".plus(newPayment))
        } ?: run { binding.secMortgagePayment.setText("$0") }

        model.unpaidSecondMortgagePayment?.let { balance->
            val newBalance: String = formatter.format(Math.round(balance))
            binding.secMortgageBalance.setText("$".plus(newBalance))
        } ?: run { binding.secMortgageBalance.setText("$0")}

    }

    private fun checkValidations() {
        val propertyType: String = binding.tvPropertyType.text.toString().trim()
        val occupancyType: String = binding.tvOccupancyType.text.toString().trim()
        val propertyUsage: String = binding.tvPropertyStatus.text.toString().trim()
        val hoaDues: String = binding.edAssociationDues.text.toString().trim()
        val proValue: String = binding.edPropertyValue.text.toString().trim()
        val annualPropertyTax: String = binding.edPropertyTax.text.toString().trim()
        val homeInsur: String = binding.edHomeownerInsurance.text.toString().trim()
        val floodIns: String = binding.edFloodInsurance.text.toString().trim()
        val rentalIncome: String = binding.edRentalIncome.text.toString().trim()
        var isRentalVisible: Boolean = false
        if (binding.layoutRentalIncome.isVisible) {
            isRentalVisible = true
            if (rentalIncome.length == 0) {
                CustomMaterialFields.setError(binding.layoutRentalIncome, getString(R.string.error_field_required), requireActivity())
            }
            if (rentalIncome.length > 0) {
                CustomMaterialFields.clearError(binding.layoutRentalIncome, requireActivity())
            }
        }

        if(!binding.layoutRentalIncome.isVisible){
            isRentalVisible = false
        }

        if (binding.tvOccupancyType.text.toString().isEmpty() || binding.tvOccupancyType.text.toString().length == 0) {
            CustomMaterialFields.setError(binding.layoutOccupancyType, getString(R.string.error_field_required), requireActivity())
        }
        if (binding.tvPropertyType.text.toString().isEmpty() || binding.tvPropertyType.text.toString().length == 0) {
            CustomMaterialFields.setError(binding.layoutPropertyType, getString(R.string.error_field_required), requireActivity())
        }
        if (binding.tvPropertyStatus.text.toString().isEmpty() || binding.tvPropertyStatus.text.toString().length == 0) {
            CustomMaterialFields.setError(
                binding.layoutPropertyStatus,
                getString(R.string.error_field_required),
                requireActivity()
            )
        }

        if (binding.edAssociationDues.text.toString().isEmpty() || binding.edAssociationDues.text.toString().length == 0) {
            CustomMaterialFields.setError(binding.layoutAssociationDues, getString(R.string.error_field_required), requireActivity())
        }
        if (binding.edPropertyValue.text.toString().isEmpty() || binding.edPropertyValue.text.toString().length == 0) {
            CustomMaterialFields.setError(binding.layoutPropertyValue, getString(R.string.error_field_required), requireActivity())
        }
        if (binding.edPropertyTax.text.toString().isEmpty() || binding.edPropertyTax.text.toString().length == 0) {
            CustomMaterialFields.setError(binding.layoutPropertyTaxes, getString(R.string.error_field_required), requireActivity())
        }
        if (binding.edHomeownerInsurance.text.toString().isEmpty() || binding.edHomeownerInsurance.text.toString().length == 0) {
            CustomMaterialFields.setError(binding.layoutHomeownerInsurance, getString(R.string.error_field_required), requireActivity())
        }
        if (binding.edFloodInsurance.text.toString().isEmpty() || binding.edFloodInsurance.text.toString().length == 0) {
            CustomMaterialFields.setError(binding.layoutFloodInsurance, getString(R.string.error_field_required), requireActivity())
        }
        if (binding.tvOccupancyType.text.toString().isNotEmpty() || binding.tvOccupancyType.text.toString().length > 0) {
            CustomMaterialFields.clearError(binding.layoutOccupancyType, requireActivity())
        }
        if (binding.tvPropertyType.text.toString().isNotEmpty() || binding.tvPropertyType.text.toString().length > 0) {
            CustomMaterialFields.clearError(binding.layoutPropertyType, requireActivity())
        }
        if (binding.tvPropertyStatus.text.toString().isNotEmpty() || binding.tvPropertyStatus.text.toString().length > 0) {
            CustomMaterialFields.clearError(binding.layoutPropertyStatus, requireActivity())
        }
        if (binding.edPropertyValue.text.toString().isNotEmpty() || binding.edPropertyValue.text.toString().length > 0) {
            CustomMaterialFields.clearError(binding.layoutPropertyValue, requireActivity())
        }
        if (binding.edAssociationDues.text.toString().isNotEmpty() || binding.edAssociationDues.text.toString().length > 0) {
            CustomMaterialFields.clearError(binding.layoutAssociationDues, requireActivity())
        }
        if (binding.edPropertyTax.text.toString().isNotEmpty() || binding.edPropertyTax.text.toString().length > 0) {
            CustomMaterialFields.clearError(binding.layoutPropertyTaxes, requireActivity())
        }
        if (binding.edHomeownerInsurance.text.toString().isNotEmpty() || binding.edHomeownerInsurance.text.toString().length > 0) {
            CustomMaterialFields.clearError(binding.layoutHomeownerInsurance, requireActivity())
        }
        if (binding.edFloodInsurance.text.toString()
                .isNotEmpty() || binding.edFloodInsurance.text.toString().length > 0
        ) {
            CustomMaterialFields.clearError(binding.layoutFloodInsurance, requireActivity())
        }
        if(isRentalVisible){
            if (propertyType.length > 0 && propertyUsage.length > 0 && occupancyType.length > 0 && hoaDues.length > 0 && proValue.length > 0 && annualPropertyTax.length > 0
                && homeInsur.length > 0 && floodIns.length > 0 && rentalIncome.length > 0)
                sendDataToServer()
        } else {
            if (propertyType.length > 0 && propertyUsage.length > 0 && occupancyType.length > 0 && hoaDues.length > 0 && proValue.length > 0 && annualPropertyTax.length > 0
                && homeInsur.length > 0 && floodIns.length > 0)
                sendDataToServer()
        }
    }

    private fun sendDataToServer(){
         // get property id
        val property: String = binding.tvPropertyType.getText().toString().trim()
        val matchedList1 = propertyTypeList.filter { p -> p.name == property }
        val propertyId = if (matchedList1.size > 0) matchedList1.map { matchedList1.get(0).id }.single() else null

        // get occupancy id
        val occupancy: String = binding.tvOccupancyType.getText().toString().trim()
        val matchedList = occupancyTypeList.filter { s -> s.name.equals(occupancy, true) }
        val occupancyId = if (matchedList.size > 0) matchedList.map { matchedList.get(0).id }.single() else null

        // get property status
        val propertyStatus: String = binding.tvPropertyStatus.getText().toString()
        val matchedStatus = propertyStatusList.filter { s -> s.name.equals(propertyStatus, true) }
         val propertyStatusId = if (matchedStatus.size > 0) matchedStatus.map { matchedStatus.get(0).id }.single() else null

        // property value
        val propertyValue = binding.edPropertyValue.text.toString().trim()
        val newPropertyValue = if (propertyValue.length > 0) propertyValue.replace(",".toRegex(), "") else null

        // home insurance
        val homeInsurance = binding.edHomeownerInsurance.text.toString().trim()
        val newHomeInsurance = if (homeInsurance.length > 0) homeInsurance.replace(",".toRegex(), "") else null

        val hoa = binding.edAssociationDues.text.toString().trim()
        val newHoaDues = if (hoa.length > 0) hoa.replace(",".toRegex(), "") else null

        val propertyTax = binding.edPropertyTax.text.toString().trim()
        val newPropertyTax = if (propertyTax.length > 0) propertyTax.replace(",".toRegex(), "") else null

        val rentalIncome = binding.edRentalIncome.text.toString().trim()
        var newRentalIncome: String? = null
        if(binding.layoutRentalIncome.isVisible) {
            newRentalIncome =  if (rentalIncome.length > 0) rentalIncome.replace(",".toRegex(), "") else null
        }

        // flood insurance
        val floodInsurance = binding.edFloodInsurance.text.toString().trim()
        val newFloodInsurance = if (floodInsurance.length > 0) floodInsurance.replace(",".toRegex(), "") else null

        var hasFirstMortgage : Boolean? = null
        var hasSecondMortgage : Boolean? = null

        if(binding.rbFirstMortgageYes.isChecked) {
            hasFirstMortgage = true
            if (binding.rbSecMortgageYes.isChecked)
                hasSecondMortgage = true

            if (binding.rbSecMortgageNo.isChecked) {
                hasSecondMortgage = false
                secondMortgageModel = null
            }
        }

        if (binding.rbFirstMortgageNo.isChecked) {
            hasFirstMortgage = false
            hasSecondMortgage = false
            firstMortgageModel = null
            secondMortgageModel = null
        }

        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->

               //Log.e("realEstateIds","Loan Application Id" + loanApplicationId + " borrowerPropertyId" + borrowerPropertyId+ " borrowerId" + borrowerId + " propertyInfoID " + propertyInfoId)
               //Log.e("first Mortgage model before add api", ""+ firstMortgageModel)
              // Log.e("sec Mortgage model before add api", ""+ secondMortgageModel)

                val data = AddRealEstateResponse(
                    loanApplicationId = loanApplicationId, propertyTypeId = propertyId, occupancyTypeId = occupancyId,
                    propertyStatus = propertyStatusId, appraisedPropertyValue = newPropertyValue?.toDouble(), propertyTax = newPropertyTax?.toDouble(), homeOwnerInsurance = newHomeInsurance?.toDouble(),
                    floodInsurance = newFloodInsurance?.toDouble(), hoaDues = newHoaDues?.toDouble(),
                    hasFirstMortgage = hasFirstMortgage, hasSecondMortgage = hasSecondMortgage, address = realEstateAddress,
                    firstMortgageModel = firstMortgageModel, secondMortgageModel = secondMortgageModel, rentalIncome = newRentalIncome?.toDouble(),
                    borrowerPropertyId = borrowerPropertyId, borrowerId = borrowerId, propertyInfoId = propertyInfoId)

                //Log.e("RealEstateDataApi", "" + data)
                binding.loaderRealEstate.visibility = View.VISIBLE
                viewModel.sendRealEstate(authToken,data)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<FirstMortgageModel>(AppConstant.firstMortgage)?.observe(viewLifecycleOwner) { result ->
            setFirstMorgageDetails(result)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<SecondMortgageModel>(AppConstant.secMortgage)?.observe(viewLifecycleOwner) { result ->
            setSecondMortgageDetails(result)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressData>(AppConstant.address)?.observe(viewLifecycleOwner) { result -> realEstateAddress = result
            displayAddress(result)
        }
    }

    private fun displayAddress(it: AddressData){
        if(it.street == null && it.unit == null && it.city==null && it.zipCode==null)
           showHideAddress(false,true)
        else {
            val builder = StringBuilder()
            it.street?.let { builder.append(it).append(" ") }
            it.unit?.let { builder.append(it).append(",") } ?: run { builder.append(",")}
            it.city?.let { builder.append("\n").append(it).append(",").append(" ") } ?: run {builder.append("\n")}
            it.stateName?.let { builder.append(it).append(" ") }
            it.zipCode?.let { builder.append(it) }
            binding.tvPropertyAddress.text = builder
            showHideAddress(true,false)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRealEstateDeleteReceived(evt: RealEstateDeleteEvent){
        if (loanApplicationId != null && borrowerId != null && borrowerPropertyId!! > 0) {
            viewModel.addUpdateDeleteResponse.observe(viewLifecycleOwner, { response ->
                val codeString = response.code.toString()
                if(codeString == "400" || codeString == "200"){
                    EventBus.getDefault().postSticky(BorrowerApplicationUpdatedEvent(objectUpdated = true))
                   requireActivity().finish()
                }
            })
                lifecycleScope.launchWhenStarted {
                    sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                        if(borrowerPropertyId != null && borrowerPropertyId !=0)
                           viewModel.deleteRealEstate(authToken, borrowerPropertyId!!)
                    }
                }
            }

    }

    private fun hideLoader(){
        val  activity = (activity as? RealEstateActivity)
        activity?.binding?.loaderRealEstate?.visibility = View.GONE
    }

    private fun initViews(){
        binding.realestateMainlayout.setOnClickListener(this)
        toolbar.btnClose.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.layoutSecMortgageDetail.setOnClickListener(this)
        binding.layoutAddress.setOnClickListener(this)

        binding.addPropertyAddress.setOnClickListener {
            openAddressFragment()
        }

        setInputFields()

        binding.rbFirstMortgageYes.setOnClickListener {
            onFirstMortgageYes()
            if(firstMortgageModel != null){
                binding.rbFirstMortgageYes.setTypeface(null, Typeface.BOLD)
                binding.rbFirstMortgageNo.setTypeface(null, Typeface.NORMAL)
                binding.layoutFirstMortgageDetail.visibility = View.VISIBLE
                binding.layoutSecondMortgage.visibility = View.VISIBLE
            } else{
                binding.rbFirstMortgageYes.isChecked = false
                binding.rbFirstMortgageYes.setTypeface(null, Typeface.NORMAL)
                binding.rbFirstMortgageNo.isChecked = true
                binding.rbFirstMortgageNo.setTypeface(null, Typeface.BOLD)
                binding.layoutFirstMortgageDetail.visibility = View.GONE
                binding.layoutSecondMortgage.visibility = View.GONE
            }
        }

        binding.layoutFirstMortgageDetail.setOnClickListener { onFirstMortgageYes() }

        // first mortgage no
        binding.rbFirstMortgageNo.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.rbFirstMortgageNo.setTypeface(null, Typeface.BOLD)
                binding.rbFirstMortgageYes.setTypeface(null, Typeface.NORMAL)
                binding.layoutFirstMortgageDetail.visibility = View.GONE
                binding.layoutSecondMortgage.visibility = View.GONE
            }
        }

        binding.rbSecMortgageYes.setOnClickListener{
            onSecMortgageYesClick()
            if(secondMortgageModel != null){
                binding.rbSecMortgageYes.setTypeface(null, Typeface.BOLD)
                binding.rbSecMortgageNo.setTypeface(null, Typeface.NORMAL)
                binding.layoutSecMortgageDetail.visibility = View.VISIBLE
            } else{
                binding.rbSecMortgageYes.isChecked = false
                binding.rbSecMortgageYes.setTypeface(null, Typeface.NORMAL)
                binding.rbSecMortgageNo.isChecked = true
                binding.rbSecMortgageNo.setTypeface(null, Typeface.BOLD)
                binding.layoutSecMortgageDetail.visibility = View.GONE
            }
        }

        binding.layoutSecMortgageDetail.setOnClickListener { onSecMortgageYesClick()}

        // sec mortgage no
        binding.rbSecMortgageNo.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.rbSecMortgageNo.setTypeface(null, Typeface.BOLD)
                binding.rbSecMortgageYes.setTypeface(null, Typeface.NORMAL)
                binding.layoutSecMortgageDetail.visibility = View.GONE
            }
        }

        /*binding.rbSecMortgageYes.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.layoutSecondMortgage.visibility = View.VISIBLE
                binding.layoutSecMortgageDetail.visibility = View.VISIBLE
                binding.rbSecMortgageYes.setTypeface(null, Typeface.BOLD)
                binding.rbSecMortgageNo.setTypeface(null, Typeface.NORMAL)

            }
        }

        binding.rbSecMortgageYes.setOnClickListener {
            val fragment = RealEstateSecondMortgage()
            val bundle = Bundle()
            bundle.putString(AppConstant.address, addressHeading)
            bundle.putParcelable(AppConstant.secMortgage,secondMortgageModel)
            fragment.arguments = bundle
            findNavController().navigate(R.id.action_realestate_second_mortgage,bundle)
        } */

        /*binding.rbSecMortgageNo.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.layoutSecondMortgage.visibility = View.VISIBLE
                binding.layoutSecMortgageDetail.visibility = View.GONE
                binding.rbSecMortgageNo.setTypeface(null, Typeface.BOLD)
                binding.rbSecMortgageYes.setTypeface(null, Typeface.NORMAL)
            }
        } */

        binding.layoutSecMortgageDetail.setOnClickListener {
            val fragment = RealEstateSecondMortgage()
            val bundle = Bundle()
            bundle.putString(AppConstant.address, addressHeading)
            bundle.putParcelable(AppConstant.secMortgage,secondMortgageModel)
            fragment.arguments = bundle
            findNavController().navigate(R.id.action_realestate_second_mortgage,bundle)
        }
    }

    private fun onSecMortgageYesClick(){
        val fragment = RealEstateSecondMortgage()
        val bundle = Bundle()
        bundle.putString(AppConstant.address, addressHeading)
        bundle.putParcelable(AppConstant.secMortgage,secondMortgageModel)
        fragment.arguments = bundle
        findNavController().navigate(R.id.action_realestate_second_mortgage,bundle)
    }

    override fun onClick(view: View?) {
        when (view?.getId()) {
            R.id.realestate_mainlayout -> {
                HideSoftkeyboard.hide(requireActivity(),binding.realestateMainlayout)
                super.removeFocusFromAllFields(binding.realestateMainlayout)
            }
            R.id.btn_close -> requireActivity().finish()
            R.id.btn_save -> checkValidations()
            //R.id.rb_first_mortgage_yes -> onFirstMortgageYes()
            //R.id.rb_first_mortgage_no -> onFirstMortgegeNoClick()
            //R.id.layout_first_mortgage_detail -> onFirstMortgageYes()
            R.id.layout_address-> openAddressFragment()
        }
    }

    private fun setInputFields(){

        // set lable focus
        binding.edRentalIncome.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edRentalIncome, binding.layoutRentalIncome, requireContext()))
        binding.edAssociationDues.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edAssociationDues, binding.layoutAssociationDues, requireContext()))
        binding.edPropertyValue.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edPropertyValue, binding.layoutPropertyValue, requireContext()))
        binding.edPropertyTax.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edPropertyTax, binding.layoutPropertyTaxes, requireContext()))
        binding.edHomeownerInsurance.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edHomeownerInsurance, binding.layoutHomeownerInsurance, requireContext()))
        binding.edFloodInsurance.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edFloodInsurance, binding.layoutFloodInsurance, requireContext()))

        /*
        binding.tvPropertyType.setOnFocusChangeListener{ _, hasFocus: Boolean ->
            if(!hasFocus){
                if (binding.tvPropertyType.text.toString().length == 0) {
                    CustomMaterialFields.setColor(binding.layoutPropertyType, R.color.grey_color_three, requireActivity())
                    //CustomMaterialFields.setError(binding.layoutState,getString(R.string.error_field_required),requireActivity())
                } else {
                    CustomMaterialFields.setColor(binding.layoutPropertyType, R.color.grey_color_two, requireActivity())
                    CustomMaterialFields.clearError(binding.layoutPropertyType, requireActivity())
                }
            }
        }

        binding.tvOccupancyType.setOnFocusChangeListener{ _, hasFocus: Boolean ->
            if(!hasFocus){
                if (binding.tvOccupancyType.text.toString().length == 0){
                    CustomMaterialFields.setColor(binding.layoutOccupancyType, R.color.grey_color_three, requireActivity())
                    //CustomMaterialFields.setError(binding.layoutState,getString(R.string.error_field_required),requireActivity())
                } else {
                    CustomMaterialFields.setColor(binding.layoutOccupancyType, R.color.grey_color_two, requireActivity())
                    CustomMaterialFields.clearError(binding.layoutOccupancyType, requireActivity())
                }
            }
        }

        binding.tvPropertyStatus.setOnFocusChangeListener{ _, hasFocus: Boolean ->
            if(!hasFocus){
                if (binding.tvPropertyStatus.text.toString().length == 0){
                    CustomMaterialFields.setColor(binding.layoutPropertyStatus, R.color.grey_color_three, requireActivity())
                } else {
                    CustomMaterialFields.setColor(binding.layoutPropertyStatus, R.color.grey_color_two, requireActivity())
                    CustomMaterialFields.clearError(binding.layoutPropertyStatus, requireActivity())
                }
            }
        } */

        // set input format
        binding.edRentalIncome.addTextChangedListener(NumberTextFormat(binding.edRentalIncome))
        binding.edAssociationDues.addTextChangedListener(NumberTextFormat(binding.edAssociationDues))
        binding.edPropertyValue.addTextChangedListener(NumberTextFormat(binding.edPropertyValue))
        binding.edPropertyTax.addTextChangedListener(NumberTextFormat(binding.edPropertyTax))
        binding.edHomeownerInsurance.addTextChangedListener(NumberTextFormat(binding.edHomeownerInsurance))
        binding.edFloodInsurance.addTextChangedListener(NumberTextFormat(binding.edFloodInsurance))

        // set Dollar prifix
        CustomMaterialFields.setDollarPrefix(binding.layoutRentalIncome,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutAssociationDues,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutPropertyValue,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutPropertyTaxes,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutHomeownerInsurance,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutFloodInsurance,requireContext())

    }

    private fun setDropDownData() {
        lifecycleScope.launchWhenStarted {
            coroutineScope {

                var realStateCounter:Int = 0

                viewModel.propertyType.observe(viewLifecycleOwner, { properties ->

                    if (properties != null && properties.size > 0) {
                        realStateCounter++
                        val itemList: ArrayList<String> = arrayListOf()
                        propertyTypeList = arrayListOf()
                        for (item in properties) {
                            itemList.add(item.name)
                            propertyTypeList.add(item)
                        }

                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
                        binding.tvPropertyType.setAdapter(adapter)
                        adapter.setNotifyOnChange(true)

//                    binding.tvPropertyType.setOnFocusChangeListener { _, _ ->
//                        binding.tvPropertyType.showDropDown()
//                    }
                        binding.tvPropertyType.setOnClickListener {
                            binding.tvPropertyType.showDropDown()
                        }

                        binding.tvPropertyType.onItemClickListener =
                            AdapterView.OnItemClickListener { p0, p1, position, id ->
                                CustomMaterialFields.setColor(binding.layoutPropertyType, R.color.grey_color_two, requireActivity())
                                CustomMaterialFields.clearError(binding.layoutPropertyType, requireActivity())
                                showHideRental()
                            }

                        getRealEstateDetails(realStateCounter)
                    }
                })

                viewModel.occupancyType.observe(viewLifecycleOwner, { occupancies ->

                    if (occupancies != null && occupancies.size > 0) {
                        realStateCounter++
                        val itemList: ArrayList<String> = arrayListOf()
                        for (item in occupancies) {
                            itemList.add(item.name)
                            occupancyTypeList.add(item)

                        }

                        val adapterOccupanycyType = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            itemList
                        )
                        binding.tvOccupancyType.setAdapter(adapterOccupanycyType)
//                    binding.tvOccupancyType.setOnFocusChangeListener { _, _ ->
//                        binding.tvOccupancyType.showDropDown()
//                    }
                        binding.tvOccupancyType.setOnClickListener {
                            binding.tvOccupancyType.showDropDown()
                        }

                        binding.tvOccupancyType.onItemClickListener =
                            AdapterView.OnItemClickListener { p0, p1, position, id ->
                                CustomMaterialFields.setColor(binding.layoutOccupancyType, R.color.grey_color_two, requireActivity())
                                CustomMaterialFields.clearError(binding.layoutOccupancyType, requireActivity())

                                showHideRental()
                            }
                        getRealEstateDetails(realStateCounter)
                    }
                })

                viewModel.propertyStatus.observe(viewLifecycleOwner, {
                    if (it != null && it.size > 0) {
                        realStateCounter++

                        val itemList: ArrayList<String> = arrayListOf()
                        for (item in it) {
                            itemList.add(item.name)
                            propertyStatusList.add(item)
                        }

                        val adapterPropertyStatus = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
                        binding.tvPropertyStatus.setAdapter(adapterPropertyStatus)
                        binding.tvPropertyStatus.setOnFocusChangeListener { _, _ ->
                            binding.tvPropertyStatus.showDropDown()
                        }
                        binding.tvPropertyStatus.setOnClickListener {
                            binding.tvPropertyStatus.showDropDown()
                        }
                        binding.tvPropertyStatus.onItemClickListener =
                            AdapterView.OnItemClickListener { p0, p1, position, id ->
                                CustomMaterialFields.setColor(binding.layoutPropertyStatus, R.color.grey_color_two, requireActivity())
                                CustomMaterialFields.clearError(binding.layoutPropertyStatus, requireActivity())
                                //showHideRental()
                            }

                        getRealEstateDetails(realStateCounter)
                    }
                })
            }
       }
    }

    private fun setSpinnerData() {

        val propertyList = arrayListOf<String>(*resources.getStringArray(R.array.array_property_type))
        val occupancyList = arrayListOf<String>(*resources.getStringArray(R.array.array_occupancy_type))
        val statusList = arrayListOf<String>(*resources.getStringArray(R.array.array_property_status))

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,propertyList )
        binding.tvPropertyType.setAdapter(adapter)
        binding.tvPropertyType.setOnFocusChangeListener { _, _ ->
            binding.tvPropertyType.showDropDown()
        }
        binding.tvPropertyType.setOnClickListener {
            binding.tvPropertyType.showDropDown()
        }
        binding.tvPropertyType.onItemClickListener = object :
            AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                binding.layoutPropertyType.defaultHintTextColor = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.grey_color_two))
                showHideRental()
            }
        }

        // set occupancy type spinner

        val adapterOccupanycyType =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, occupancyList)
        binding.tvOccupancyType.setAdapter(adapterOccupanycyType)
        binding.tvOccupancyType.setOnFocusChangeListener { _, _ ->
            binding.tvOccupancyType.showDropDown()
        }
        binding.tvOccupancyType.setOnClickListener {
            binding.tvOccupancyType.showDropDown()
        }
        binding.tvOccupancyType.onItemClickListener = object :
            AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                binding.layoutOccupancyType.defaultHintTextColor = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(),R.color.grey_color_two))
                showHideRental()

            }
        }

        val adapterStatus =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, statusList)
        binding.tvPropertyStatus.setAdapter(adapterStatus)
        binding.tvPropertyStatus.setOnFocusChangeListener { _, _ -> binding.tvPropertyStatus.showDropDown() }
        binding.tvPropertyStatus.setOnClickListener {
            binding.tvPropertyStatus.showDropDown()
        }
        binding.tvPropertyStatus.onItemClickListener = object :
            AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                binding.layoutPropertyStatus.defaultHintTextColor = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(),R.color.grey_color_two))
                showHideRental()

            }
        }
    }

    private fun showHideRental(){
        if(binding.tvOccupancyType.text.toString().equals("Investment Property",true)) {
            binding.layoutRentalIncome.visibility = View.VISIBLE

        } else if (binding.tvOccupancyType.text.toString().equals("Primary Residence",true)) {
            val propertyType = binding.tvPropertyType.text.toString()
            if (propertyType.equals("Duplex (2 Unit)",true) || propertyType.equals("Triplex (3 Unit)",true) || propertyType.equals("Quadplex (4 Unit)",true))
                binding.layoutRentalIncome.visibility = View.VISIBLE
            else
                binding.layoutRentalIncome.visibility = View.GONE

        } else if (binding.tvOccupancyType.text.toString().equals("Second Home",true)){
            binding.layoutRentalIncome.visibility = View.GONE
        }
    }

    private fun openAddressFragment(){
        val addressFragment = RealEstateAddressFragment()
        val bundle = Bundle()
        bundle.putParcelable(AppConstant.address, realEstateAddress)
        addressFragment.arguments = bundle
        findNavController().navigate(R.id.action_realestate_address, addressFragment.arguments)
    }

    private fun onFirstMortgageYes(){
        /*if(binding.rbFirstMortgageYes.isChecked) {
            binding.layoutFirstMortgageDetail.visibility = View.VISIBLE
            binding.layoutSecondMortgage.visibility = View.VISIBLE
            binding.rbFirstMortgageYes.setTypeface(null, Typeface.BOLD)
            binding.rbFirstMortgageNo.setTypeface(null, Typeface.NORMAL) */

            val fragment = RealEstateFirstMortgage()
            val bundle = Bundle()
            bundle.putString(AppConstant.address,addressHeading)
            bundle.putParcelable(AppConstant.firstMortgage,firstMortgageModel)
            fragment.arguments = bundle
            findNavController().navigate(R.id.action_realestate_first_mortgage,bundle)
    }

    private fun showHideAddress(isShowAddress: Boolean, isAddAddress: Boolean){
        if(isShowAddress){
            binding.layoutAddress.visibility = View.VISIBLE
            binding.addPropertyAddress.visibility = View.GONE
        }
        if(isAddAddress){
            binding.layoutAddress.visibility = View.GONE
            binding.addPropertyAddress.visibility = View.VISIBLE
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
    fun onSentData(event: SendDataEvent){
        if(event.addUpdateDataResponse.code == AppConstant.RESPONSE_CODE_SUCCESS){
            EventBus.getDefault().postSticky(BorrowerApplicationUpdatedEvent(objectUpdated = true))
            binding.loaderRealEstate.visibility = View.GONE
        }

        else if(event.addUpdateDataResponse.code == AppConstant.INTERNET_ERR_CODE)
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG)
        else
            if (event.addUpdateDataResponse.message != null)
                SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG)

        binding.loaderRealEstate.visibility = View.GONE
        requireActivity().finish()
    }

}