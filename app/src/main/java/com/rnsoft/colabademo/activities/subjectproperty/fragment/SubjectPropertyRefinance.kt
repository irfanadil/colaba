package com.rnsoft.colabademo

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.SubPropertyRefinanceBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields

import com.rnsoft.colabademo.utils.MonthYearPickerDialog
import com.rnsoft.colabademo.utils.NumberTextFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.primary_borrower_info_layout.*
import kotlinx.android.synthetic.main.sub_property_refinance.*
import kotlinx.coroutines.coroutineScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.DecimalFormat
import javax.inject.Inject


/**
 * Created by Anita Kiran on 9/9/2021.
 */
@AndroidEntryPoint
class SubjectPropertyRefinance : BaseFragment(), DatePickerDialog.OnDateSetListener, CoBorrowerOccupancyClickListener {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val viewModel : BorrowerApplicationViewModel by activityViewModels()
    private val viewModelSubProperty : SubjectPropertyViewModel by activityViewModels()
    private lateinit var binding: SubPropertyRefinanceBinding
    var addressList :  ArrayList<AddressData> = ArrayList()
    private var propertyTypeList: ArrayList<DropDownResponse> = arrayListOf()
    private var occupancyTypeList:ArrayList<DropDownResponse> = arrayListOf()
    var firstMortgageModel: FirstMortgageModel? =  null
    var secondMortgageModel : SecondMortgageModel? = null
    var refinanceAddressData = AddressData()
    private var savedViewInstance: View? = null
    private lateinit var adapterCoborrower: CoBorrowerAdapter
    var coborrowerList = ArrayList<CoBorrowerOccupancyData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (savedViewInstance != null) {
            savedViewInstance
        } else {
            binding = SubPropertyRefinanceBinding.inflate(inflater, container, false)
            savedViewInstance = binding.root
            super.addListeners(binding.root)

            clicks()
            setInputFields()
            setDropDownData()

            savedViewInstance
        }
    }

    private fun setDropDownData(){
        lifecycleScope.launchWhenStarted {
            coroutineScope {
                var realStateCounter:Int = 0
                viewModel.propertyType.observe(viewLifecycleOwner, { properties->
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
                        //adapter.setNotifyOnChange(true)

//                    binding.tvPropertyType.setOnFocusChangeListener { _, _ ->
//                        binding.tvPropertyType.showDropDown()
//                    }
                        binding.tvPropertyType.setOnClickListener {
                            binding.tvPropertyType.showDropDown()
                        }

                        binding.tvPropertyType.onItemClickListener = object :
                            AdapterView.OnItemClickListener {
                            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                                CustomMaterialFields.setColor(binding.layoutPropertyType, R.color.grey_color_two, requireActivity())
                                showHideRental()
                            }
                        }

                        getRefinanceDetails(realStateCounter)

                    }
                })

                // occupancy Type spinner
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
                            itemList)
                        binding.tvOccupancyType.setAdapter(adapterOccupanycyType)
//                    binding.tvOccupancyType.setOnFocusChangeListener { _, _ ->
//                        binding.tvOccupancyType.showDropDown()
//                    }
                        binding.tvOccupancyType.setOnClickListener {
                            binding.tvOccupancyType.showDropDown()
                        }

                        binding.tvOccupancyType.onItemClickListener =
                            object : AdapterView.OnItemClickListener {
                                override fun onItemClick(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    position: Int,
                                    id: Long) {
                                    CustomMaterialFields.setColor(binding.layoutOccupancyType, R.color.grey_color_two, requireActivity())
                                    showHideRental()
                                }
                            }
                    }
                    getRefinanceDetails(realStateCounter)
                })

                viewModelSubProperty.coBorrowerOccupancyStatus.observe(viewLifecycleOwner, {
                    if(it.occupancyData != null && it.occupancyData.size > 0) {
                        adapterCoborrower = CoBorrowerAdapter(requireContext(), this@SubjectPropertyRefinance)
                        binding.recyclerviewCoBorrower.setHasFixedSize(true)
                        coborrowerList = it.occupancyData
                        adapterCoborrower.setBorrowers(coborrowerList)
                        binding.recyclerviewCoBorrower.adapter = adapterCoborrower
                    } else {
                        binding.coBorrowerHeading.visibility = View.GONE
                    }
                })
            }
        }
    }

    private fun clicks(){

        binding.edDateOfHomePurchase.showSoftInputOnFocus = false
        binding.edDateOfHomePurchase.setOnClickListener { createCustomDialog() }
        //binding.edDateOfHomePurchase.setOnFocusChangeListener { _, _ -> createCustomDialog() }

        // radio subject property TBD
        binding.radioSubPropertyTbd.setOnClickListener {
            binding.radioSubPropertyAddress.isChecked = false
            binding.radioSubPropertyTbd.setTypeface(null,Typeface.BOLD)
            binding.radioTxtPropertyAdd.setTypeface(null,Typeface.NORMAL)
            binding.tvSubPropertyAddress.visibility = View.GONE
        }

        // radio sub property address
        binding.radioSubPropertyAddress.setOnClickListener {
            binding.radioSubPropertyTbd.isChecked = false
            binding.tvSubPropertyAddress.visibility = View.VISIBLE
            binding.radioTxtPropertyAdd.setTypeface(null,Typeface.BOLD)
            binding.radioSubPropertyTbd.setTypeface(null,Typeface.NORMAL)
            openAddress()
        }

        binding.radioTxtPropertyAdd.setOnClickListener {
            binding.radioSubPropertyTbd.isChecked = false
            binding.radioSubPropertyAddress.isChecked = true
            binding.tvSubPropertyAddress.visibility = View.VISIBLE
            binding.radioTxtPropertyAdd.setTypeface(null,Typeface.BOLD)
            binding.radioSubPropertyTbd.setTypeface(null,Typeface.NORMAL)
            openAddress()
        }

        binding.layoutAddress.setOnClickListener {
            openAddress()
        }

        // radio mixed use property click
        binding.radioMixedPropertyYes.setOnClickListener {
            gotoMixedUseProperty()
            binding.layoutMixedPropertyDetail.visibility = View.VISIBLE
        }
        // mixed property detail
        binding.layoutMixedPropertyDetail.setOnClickListener{
            gotoMixedUseProperty()
        }

        // radio btn mixed use property Yes
        binding.radioMixedPropertyYes.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.radioMixedPropertyYes.setTypeface(null, Typeface.BOLD)
                binding.radioMixedPropertyNo.setTypeface(null, Typeface.NORMAL)
            }
        }

        // radio btn mixed use property No
        binding.radioMixedPropertyNo.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.radioMixedPropertyNo.setTypeface(null, Typeface.BOLD)
                binding.radioMixedPropertyYes.setTypeface(null, Typeface.NORMAL)
                binding.layoutMixedPropertyDetail.visibility = View.GONE
            }
        }

        // first mortgage yes
        /*(binding.radioHasFirstMortgageYes.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.radioHasFirstMortgageYes.setTypeface(null, Typeface.BOLD)
                binding.radioHasFirstMortgageNo.setTypeface(null, Typeface.NORMAL)
                binding.layoutFirstMortgageDetail.visibility = View.VISIBLE
                binding.layoutSecondMortgage.visibility = View.VISIBLE
            }
        } */

        binding.radioHasFirstMortgageYes.setOnClickListener {
            onFirstMortgageYes()
            if(firstMortgageModel != null){
                binding.radioHasFirstMortgageYes.setTypeface(null, Typeface.BOLD)
                binding.radioHasFirstMortgageNo.setTypeface(null, Typeface.NORMAL)
                binding.layoutFirstMortgageDetail.visibility = View.VISIBLE
                binding.layoutSecondMortgage.visibility = View.VISIBLE
            } else{
                binding.radioHasFirstMortgageYes.isChecked = false
                binding.radioHasFirstMortgageYes.setTypeface(null, Typeface.NORMAL)
                binding.radioHasFirstMortgageNo.isChecked = true
                binding.radioHasFirstMortgageNo.setTypeface(null, Typeface.BOLD)
                binding.layoutFirstMortgageDetail.visibility = View.GONE
                binding.layoutSecondMortgage.visibility = View.GONE
            }
        }

        binding.layoutFirstMortgageDetail.setOnClickListener { onFirstMortgageYes() }

        // first mortgage no
        binding.radioHasFirstMortgageNo.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.radioHasFirstMortgageNo.setTypeface(null, Typeface.BOLD)
                binding.radioHasFirstMortgageYes.setTypeface(null, Typeface.NORMAL)
                binding.layoutFirstMortgageDetail.visibility = View.GONE
                binding.layoutSecondMortgage.visibility = View.GONE
            }
        }

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
                binding.rbSecMortgageNo.setTypeface(null, Typeface.NORMAL)
                binding.rbSecMortgageYes.setTypeface(null, Typeface.BOLD)
                binding.layoutSecMortgageDetail.visibility = View.VISIBLE
            }
        } */

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

        binding.layoutSecMortgageDetail.setOnClickListener { onSecMortgageYesClick() }

        binding.btnSave.setOnClickListener {
            sendData()
        }

        binding.backButton.setOnClickListener {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }

        binding.refinanceParentLayout.setOnClickListener {
            HideSoftkeyboard.hide(requireActivity(),binding.refinanceParentLayout)
            super.removeFocusFromAllFields(binding.refinanceParentLayout)
        }
    }

    private fun getRefinanceDetails(counter: Int){
        if (counter == 2) {
            viewModel.refinanceDetails.observe(viewLifecycleOwner, { details ->
                if (details != null) {
                    details.subPropertyData?.addressRefinance?.let {
                        if (it.street == null && it.unit == null && it.city == null && it.stateName == null && it.countryName == null) {
                            binding.radioSubPropertyTbd.isChecked = true
                            binding.radioSubPropertyTbd.setTypeface(null, Typeface.BOLD)
                        } else {
                            binding.radioSubPropertyAddress.isChecked = true
                            binding.radioTxtPropertyAdd.setTypeface(null, Typeface.BOLD)
                            binding.tvSubPropertyAddress.visibility = View.VISIBLE
                            //binding.tvSubPropertyAddress.text =
                            //    it.street + " " + it.unit + "\n" + it.city + " " + it.stateName + " " + it.zipCode + " " + it.countryName

                            // list for send data to address fragment
                            displayAddress(it)
                            refinanceAddressData = it // list for sending data to api
                        }
                    } ?: run {
                        binding.radioSubPropertyTbd.isChecked = true
                        binding.radioSubPropertyTbd.setTypeface(null, Typeface.BOLD)
                    }

                    details.subPropertyData?.rentalIncome?.let {
                        binding.edRentalIncome.setText(Math.round(it).toString())
                        binding.layoutRentalIncome.visibility = View.VISIBLE
                        CustomMaterialFields.setColor(
                            binding.layoutRentalIncome,
                            R.color.grey_color_two,
                            requireActivity()
                        )
                    }
                    // property id
                    details.subPropertyData?.propertyTypeId?.let { selectedId ->
                        for (item in propertyTypeList) {
                            if (item.id == selectedId) {
                                binding.tvPropertyType.setText(item.name, false)
                                showHideRental()
                                CustomMaterialFields.setColor(binding.layoutPropertyType, R.color.grey_color_two,
                                    requireActivity()
                                )
                                break
                            }
                        }
                    }
                    // occupancy id
                    details.subPropertyData?.propertyUsageId?.let { selectedId ->

                        for (item in occupancyTypeList) {
                            if (item.id == selectedId) {
                                binding.tvOccupancyType.setText(item.name, false)
                                showHideRental()
                                CustomMaterialFields.setColor(binding.layoutOccupancyType, R.color.grey_color_two, requireActivity())
                                break
                            }
                        }
                    }
                    // date of home purchased
                    details.subPropertyData?.dateAcquired?.let {
                        val date = AppSetting.getMonthAndYear(it, true)
                        binding.edDateOfHomePurchase.setText(date)
                        CustomMaterialFields.setColor(binding.layoutDateOfHomepurchase, R.color.grey_color_two, requireActivity())
                    }
                    // property value
                    details.subPropertyData?.propertyValue?.let { value ->
                        binding.edPropertyValue.setText(Math.round(value).toString())
                        CustomMaterialFields.setColor(binding.layoutPropertyValue, R.color.grey_color_two, requireActivity())
                    }
                    // hoa dues
                    details.subPropertyData?.hoaDues?.let { value ->
                        binding.edAssociation.setText(Math.round(value).toString())
                        CustomMaterialFields.setColor(
                            binding.layoutAssociationDues,
                            R.color.grey_color_two,
                            requireActivity()
                        )
                    }
                    // property tax
                    details.subPropertyData?.propertyTax?.let { value ->
                        binding.edPropertyTaxes.setText(Math.round(value).toString())
                        CustomMaterialFields.setColor(
                            binding.layoutPropertyTaxes,
                            R.color.grey_color_two,
                            requireActivity()
                        )
                    }
                    // home insurance
                    details.subPropertyData?.homeOwnerInsurance?.let { value ->
                        binding.edHomeownerInsurance.setText(Math.round(value).toString())
                        CustomMaterialFields.setColor(
                            binding.layoutHomeownerInsurance,
                            R.color.grey_color_two,
                            requireActivity()
                        )
                    }
                    // flood insurance
                    details.subPropertyData?.floodInsurance?.let { value ->
                        binding.edFloodInsurance.setText(Math.round(value).toString())
                        CustomMaterialFields.setColor(
                            binding.layoutFloodInsurance,
                            R.color.grey_color_two,
                            requireActivity()
                        )
                    }

                    details.subPropertyData?.isMixedUseProperty?.let { value ->
                        if (value) {
                            binding.radioMixedPropertyYes.isChecked = true
                            details.subPropertyData.mixedUsePropertyExplanation?.let { desc ->
                                binding.mixedPropertyDesc.setText(desc)
                                binding.layoutMixedPropertyDetail.visibility = View.VISIBLE
                            }
                        } else
                            binding.radioMixedPropertyNo.isChecked = true
                    } ?: run {
                        //binding.radioMixedPropertyNo.isChecked = true
                    }

                    // has first mortgage 'yes'
                    details.subPropertyData?.hasFirstMortgage?.let { yes ->
                        if (yes) {
                            binding.radioHasFirstMortgageYes.isChecked = true
                            // binding.layoutFirstMortgageDetail.visibility =View.VISIBLE
                            // binding.layoutSecondMortgage.visibility = View.VISIBLE
                            details.subPropertyData.firstMortgageModel?.let { model ->
                                setFirstMorgageDetails(model)
                            }
                        } else {
                            binding.radioHasFirstMortgageNo.isChecked = true
//                        details.subPropertyData.firstMortgageModel?.let{ model->
//                            binding.radioHasFirstMortgageYes.isChecked = true
//                            setFirstMorgageDetails(model)
//                        }
                        }

                    } ?: run {
                        //binding.radioHasFirstMortgageNo.isChecked = true
                    }

                    // has second mortgage 'yes'
                    details.subPropertyData?.hasSecondMortgage?.let { yes ->
                        if (yes) {
                            binding.rbSecMortgageYes.isChecked = true
                            binding.layoutSecMortgageDetail.visibility = View.VISIBLE

                            details.subPropertyData.secondMortgageModel?.let { model ->
                                setSecondMortgageDetails(model)
                            }
                        } else binding.rbSecMortgageNo.isChecked = true
                    } ?: run {
                        //binding.radioHasFirstMortgageNo.isChecked = true
                    }
                    if (details.code.equals(AppConstant.RESPONSE_CODE_SUCCESS)) {
                        hideLoader()
                    }
                }
                hideLoader()
            })
        }
    }

    private fun sendData(){
        // TBD
        var addressForApi :  AddressData? =  null
        var tbd : Boolean? = null
            if(binding.radioSubPropertyTbd.isChecked){
               tbd = true
        } else {
            tbd = false
            addressForApi = refinanceAddressData
        }

        // get property id
        val property : String = binding.tvPropertyType.getText().toString().trim()
        val matchedList1 =  propertyTypeList.filter { p -> p.name.equals(property,true)}
        val propertyId = if(matchedList1.size > 0) matchedList1.map { matchedList1.get(0).id }.single() else null

        // get occupancy id
        val occupancy : String = binding.tvOccupancyType.getText().toString().trim()
        val matchedList =  occupancyTypeList.filter { s -> s.name.equals(occupancy,true)}
        val occupancyId = if(matchedList.size>0) matchedList.map { matchedList.get(0).id }.single() else null

        // mixed use property
        var isMixedUseProperty: Boolean? = null
        var mixedUsePropertyDesc : String? = null

        if(binding.radioMixedPropertyYes.isChecked) {
            isMixedUseProperty = true
            mixedUsePropertyDesc = binding.mixedPropertyDesc.text.toString()  // desc
        }

        if(binding.radioMixedPropertyNo.isChecked) {
            isMixedUseProperty = false
        }

        // property value
        val propertyValue = binding.edPropertyValue.text.toString().trim()
        val newPropertyValue = if(propertyValue.length > 0) propertyValue.replace(",".toRegex(), "") else "0"

        val hoa = binding.edAssociation.text.toString().trim()
        val newHoaDues = if(hoa.length > 0) hoa.replace(",".toRegex(), "") else "0"

        val propertyTax = binding.edPropertyTaxes.text.toString().trim()
        val newPropertyTax = if(propertyTax.length > 0) propertyTax.replace(",".toRegex(), "") else "0"

        val datePurchased = if(binding.edDateOfHomePurchase.text.toString().trim().length > 0) binding.edDateOfHomePurchase.text.toString() else null

        val rentalIncome = binding.edRentalIncome.text.toString().trim()
        var newRentalIncome : String?=null
        if(binding.layoutRentalIncome.isVisible){
            newRentalIncome =  if(rentalIncome.length > 0) rentalIncome.replace(",".toRegex(), "") else "0"
        }

        // home insurance
        val homeInsurance = binding.edHomeownerInsurance.text.toString().trim()
        val newHomeInsurance = if(homeInsurance.length > 0) homeInsurance.replace(",".toRegex(), "") else "0"
        // flood insurance
        val floodInsurance = binding.edFloodInsurance.text.toString().trim()
        val newFloodInsurance = if(floodInsurance.length >0 ) floodInsurance.replace(",".toRegex(), "") else "0"

        var hasFirstMortgage : Boolean? = null
        var hasSecondMortgage : Boolean? = null

        if (binding.radioHasFirstMortgageYes.isChecked) {
            hasFirstMortgage = true
            if (binding.rbSecMortgageYes.isChecked)
                hasSecondMortgage = true

            if (binding.rbSecMortgageNo.isChecked) {
                hasSecondMortgage = false
                secondMortgageModel = null
            }
        }

        if (binding.radioHasFirstMortgageNo.isChecked) {
            hasFirstMortgage = false
            hasSecondMortgage = false
            firstMortgageModel = null
            secondMortgageModel = null
        }

        lifecycleScope.launchWhenStarted{
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                val activity = (activity as? SubjectPropertyActivity)
                activity?.loanApplicationId?.let { loanId->
                    //Log.e("Loan Application Id", ""+ loanId)
                    //Log.e("first Mortgage model before add api", ""+ firstMortgageModel)
                    //Log.e("sec Mortgage model before add api", ""+ secondMortgageModel)

                    val refinanceDetail = SubPropertyRefinanceData(loanApplicationId = loanId,propertyTypeId = propertyId,propertyUsageId = occupancyId,
                        propertyValue = newPropertyValue?.toDouble(),propertyTax = newPropertyTax?.toDouble(),homeOwnerInsurance=newHomeInsurance?.toDouble(),
                        floodInsurance = newFloodInsurance?.toDouble(), hoaDues=newHoaDues?.toDouble(),dateAcquired = datePurchased, hasFirstMortgage = hasFirstMortgage,
                        hasSecondMortgage = hasSecondMortgage,isSameAsPropertyAddress = true,
                        addressRefinance = addressForApi,isMixedUseProperty= isMixedUseProperty,mixedUsePropertyExplanation=mixedUsePropertyDesc,
                        subjectPropertyTbd = tbd,firstMortgageModel = firstMortgageModel,secondMortgageModel = secondMortgageModel,rentalIncome = newRentalIncome?.toDouble())

                        showLoader()
                        //Log.e("RefinanceData", ""+refinanceDetail)
                        viewModelSubProperty.sendRefinanceDetail(authToken,refinanceDetail)
                }
            }
        }
    }

    private fun openAddress(){
        val fragment = SubPropertyAddressFragment()
        val bundle = Bundle()
        bundle.putParcelable(AppConstant.address,refinanceAddressData)
        fragment.arguments = bundle
        findNavController().navigate(R.id.action_address, fragment.arguments)
    }

    private fun gotoMixedUseProperty(){
        var mixedUsePropertyDesc : String? = ""
        mixedUsePropertyDesc = binding.mixedPropertyDesc.text.toString()
        val fragment = MixedUsePropertyFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.MIXED_USE_PROPERTY_DESC,mixedUsePropertyDesc)
        fragment.arguments = bundle
        findNavController().navigate(R.id.action_mixed_property, fragment.arguments)

    }

    override fun onResume() {
        super.onResume()

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressData>(AppConstant.address)?.observe(
            viewLifecycleOwner){ result ->
            refinanceAddressData = result
            displayAddress(result)
            binding.radioSubPropertyAddress.isChecked = true
            binding.radioSubPropertyTbd.isChecked = false
            binding.tvSubPropertyAddress.visibility = View.VISIBLE
            binding.radioTxtPropertyAdd.setTypeface(null,Typeface.BOLD)
            binding.radioSubPropertyTbd.setTypeface(null,Typeface.NORMAL)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(AppConstant.mixedPropertyDetails)?.observe(
            viewLifecycleOwner) { result ->
            binding.radioMixedPropertyYes.isChecked = true
            binding.mixedPropertyDesc.setText(result)
            binding.layoutMixedPropertyDetail.visibility = View.VISIBLE
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<FirstMortgageModel>(AppConstant.firstMortgage)?.observe(viewLifecycleOwner) {
            setFirstMorgageDetails(it)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<SecondMortgageModel>(AppConstant.secMortgage)?.observe(viewLifecycleOwner) { result ->
            setSecondMortgageDetails(result)
        }
    }

    private fun setFirstMorgageDetails(model: FirstMortgageModel){
        val formatter = DecimalFormat("#,###,###")
        firstMortgageModel = model
        binding.radioHasFirstMortgageYes.isChecked = true
        binding.radioHasFirstMortgageYes.setTypeface(null, Typeface.BOLD)
        binding.radioHasFirstMortgageNo.isChecked = false
        binding.radioHasFirstMortgageNo.setTypeface(null, Typeface.NORMAL)

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
            binding.textviewSecMortgagePayment.setText("$" + newPayment)
        } ?: run { binding.textviewSecMortgagePayment.setText("$0") }

        model.unpaidSecondMortgagePayment?.let { balance->
            val newBalance: String = formatter.format(Math.round(balance))
            binding.textviewSecMortgageBalance.setText("$" + newBalance)
        } ?: run {binding.textviewSecMortgageBalance.setText("$0") }
    }

    private fun displayAddress(it: AddressData){
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

        binding.tvSubPropertyAddress.text = builder
    }

    private fun showHideRental(){
        if (binding.tvOccupancyType.text.toString().equals("Investment Property",true)) {
            binding.layoutRentalIncome.visibility = View.VISIBLE

        } else if (binding.tvOccupancyType.text.toString().equals("Primary Residence",true)) {
            var propertyType = binding.tvPropertyType.text.toString()
            if (propertyType.equals("Duplex (2 Unit)") || propertyType.equals("Triplex (3 Unit)",true) || propertyType.equals(
                    "Quadplex (4 Unit)"))
                binding.layoutRentalIncome.visibility = View.VISIBLE
            else
                binding.layoutRentalIncome.visibility = View.GONE

        } else if (binding.tvOccupancyType.text.toString().equals("Second Home",true)) {
            binding.layoutRentalIncome.visibility = View.GONE
        }
    }

    private fun createCustomDialog() {
        val pd = MonthYearPickerDialog()
        pd.setListener(this)
        pd.show(requireActivity().supportFragmentManager, "MonthYearPickerDialog")
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        var stringMonth = p2.toString()
        if (p2 < 10)
            stringMonth = "0$p2"

        val sampleDate = "$stringMonth / $p1"
        binding.edDateOfHomePurchase.setText(sampleDate)
    }

    private fun onFirstMortgageYes() {
        //binding.layoutFirstMortgageDetail.visibility = View.VISIBLE
       // binding.layoutSecondMortgage.visibility = View.VISIBLE
        val fragment = FirstMortgageFragment()
        val bundle = Bundle()
        bundle.putParcelable(AppConstant.firstMortgage,firstMortgageModel)
        fragment.arguments = bundle
        findNavController().navigate(R.id.action_refinance_first_mortgage, fragment.arguments)
    }

    private fun onSecMortgageYesClick(){
        binding.layoutSecondMortgage.visibility = View.VISIBLE
        binding.layoutSecMortgageDetail.visibility = View.VISIBLE
        binding.rbSecMortgageYes.setTypeface(null, Typeface.BOLD)
        binding.rbSecMortgageNo.setTypeface(null, Typeface.NORMAL)
        val fragment = SecondMortgageFragment()
        val bundle = Bundle()
        bundle.putParcelable(AppConstant.secMortgage,secondMortgageModel)
        fragment.arguments = bundle
        findNavController().navigate(R.id.action_refinance_sec_mortgage, fragment.arguments)
    }

    override fun onCoborrowerClick(position: Int, isOccupying: Boolean) {
        lifecycleScope.launchWhenStarted{
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                //Log.e("frag-Refinance", ""+ coborrowerList.get(position).borrowerId + " Occupying: " + isOccupying)
                val data = AddCoBorrowerOccupancy(coborrowerList.get(position).borrowerId,isOccupying)
                viewModelSubProperty.sendCoBorrowerOccupancy(authToken,data)
            }
        }
    }

    private fun setInputFields() {

        // set lable focus
        binding.edRentalIncome.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edRentalIncome, binding.layoutRentalIncome, requireContext()))
        binding.edPropertyValue.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edPropertyValue, binding.layoutPropertyValue, requireContext()))
        binding.edAssociation.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edAssociation, binding.layoutAssociationDues, requireContext()))
        binding.edPropertyTaxes.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edPropertyTaxes, binding.layoutPropertyTaxes, requireContext()))
        binding.edHomeownerInsurance.setOnFocusChangeListener(
            CustomFocusListenerForEditText(
                binding.edHomeownerInsurance,
                binding.layoutHomeownerInsurance,
                requireContext()
            )
        )
        binding.edFloodInsurance.setOnFocusChangeListener(
            CustomFocusListenerForEditText(
                binding.edFloodInsurance,
                binding.layoutFloodInsurance,
                requireContext()))

        // set input format
        binding.edRentalIncome.addTextChangedListener(NumberTextFormat(binding.edRentalIncome))
        binding.edPropertyValue.addTextChangedListener(NumberTextFormat(binding.edPropertyValue))
        binding.edAssociation.addTextChangedListener(NumberTextFormat(binding.edAssociation))
        binding.edPropertyTaxes.addTextChangedListener(NumberTextFormat(binding.edPropertyTaxes))
        binding.edHomeownerInsurance.addTextChangedListener(NumberTextFormat(binding.edHomeownerInsurance))
        binding.edFloodInsurance.addTextChangedListener(NumberTextFormat(binding.edFloodInsurance))
        CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.edDateOfHomePurchase, binding.layoutDateOfHomepurchase)


        // set Dollar prifix
        CustomMaterialFields.setDollarPrefix(binding.layoutRentalIncome, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutPropertyValue, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutAssociationDues, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutPropertyTaxes, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutHomeownerInsurance, requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutFloodInsurance, requireContext())

    }

    private fun hideLoader(){
        val  activity = (activity as? SubjectPropertyActivity)
        activity?.binding?.loaderSubjectProperty?.visibility = View.GONE
    }

    private fun showLoader(){
        val  activity = (activity as? SubjectPropertyActivity)
        activity?.binding?.loaderSubjectProperty?.visibility = View.VISIBLE
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
        if(event.addUpdateDataResponse.code == AppConstant.RESPONSE_CODE_SUCCESS) {
            EventBus.getDefault().postSticky(BorrowerApplicationUpdatedEvent(objectUpdated = true))
            dismissActivity()
        }
        else if(event.addUpdateDataResponse.code == AppConstant.INTERNET_ERR_CODE)
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG )

        else
            if(event.addUpdateDataResponse.message != null)
                SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG )
        hideLoader()
    }

    private fun dismissActivity(){
        requireActivity().finish()
        requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
    }

    /*
    private fun onSecMortgegeNoClick() {
        binding.layoutSecondMortgage.visibility = View.VISIBLE
        binding.layoutSecMortgageDetail.visibility = View.GONE
        binding.rbSecMortgageNo.setTypeface(null, Typeface.BOLD)
        binding.rbSecMortgageYes.setTypeface(null, Typeface.NORMAL)
    } */

    /*private fun mixedPropertyDetailClick() {
      findNavController().navigate(R.id.action_mixed_property)
      //binding.layoutDetails.visibility = View.VISIBLE
      binding.radioMixedPropertyYes.setTypeface(null, Typeface.BOLD)
      //binding.rbMixedPropertyNo.setTypeface(null, Typeface.NORMAL)
  }

  private fun radioSubPropertyClick() {
      //binding.rbSubProperty.isChecked = true
      //binding.rbSubPropertyAddress.isChecked = false
      binding.tvSubPropertyAddress.visibility = View.GONE
      //bold text
      //binding.rbSubProperty.setTypeface(null, Typeface.BOLD)
      binding.radioTxtPropertyAdd.setTypeface(null, Typeface.NORMAL)
  }

  private fun radioAddressClick() {
      //binding.rbSubProperty.isChecked = false
      //binding.rbSubPropertyAddress.isChecked = true
      binding.tvSubPropertyAddress.visibility = View.VISIBLE
      //bold text
      binding.radioTxtPropertyAdd.setTypeface(null, Typeface.BOLD)
      //binding.rbSubProperty.setTypeface(null, Typeface.NORMAL)
      findNavController().navigate(R.id.action_address)
  } */


    /*
      private fun getDropDownData(){
        //lifecycleScope.launchWhenStarted {
        //  viewModel.getPropertyTypes(token)
        viewModel.propertyType.observe(viewLifecycleOwner, {
            //if(it != null && it.size > 0) {

            val itemList:ArrayList<String> = arrayListOf()
            for(item in it){
                itemList.add(item.name)
                if(propertyTypeId == item.id){
                    binding.tvPropertyType.setText(item.name)
                    CustomMaterialFields.setColor(binding.layoutPropertyType,R.color.grey_color_two,requireActivity())
                }
            }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,itemList)
            binding.tvPropertyType.setAdapter(adapter)
            //binding.tvPropertyType.setOnFocusChangeListener { _, _ ->
            //   binding.tvPropertyType.showDropDown()
            //}
            binding.tvPropertyType.setOnClickListener {
                binding.tvPropertyType.showDropDown()
            }
            binding.tvPropertyType.onItemClickListener = object :
                AdapterView.OnItemClickListener {
                override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                    showHideRental()
                }
            }
            //}
        })
        // }

        // occupancy Type spinner
        //lifecycleScope.launchWhenStarted {
        // viewModel.getOccupancyType(token)
        viewModel.occupancyType.observe(viewLifecycleOwner, {occupancyList->

            if(occupancyList != null && occupancyList.size > 0) {
                val itemList: ArrayList<String> = arrayListOf()
                for (item in occupancyList) {
                    itemList.add(item.name)
                    if(occupancyTypeId > 0 && occupancyTypeId == item.id){
                        binding.tvOccupancyType.setText(item.name)
                        CustomMaterialFields.setColor(binding.layoutOccupancyType,R.color.grey_color_two,requireActivity())
                    }
                }

                val adapterOccupanycyType =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,itemList)
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
                        CustomMaterialFields.setColor(binding.layoutOccupancyType,R.color.grey_color_two,requireActivity())
                        showHideRental()
                    }
                }
            }

        })
        // }
    }
     */

}