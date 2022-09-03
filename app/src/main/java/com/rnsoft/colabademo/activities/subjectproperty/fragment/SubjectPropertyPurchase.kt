package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.SubjectPropertyPurchaseBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import com.rnsoft.colabademo.utils.NumberTextFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.realstate_horizontal.*
import kotlinx.android.synthetic.main.view_placesearch.*
import kotlinx.coroutines.coroutineScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

/**
 * Created by Anita Kiran on 9/8/2021.
 */
@AndroidEntryPoint
class SubjectPropertyPurchase : BaseFragment(), CoBorrowerOccupancyClickListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: SubjectPropertyPurchaseBinding
    private val viewModel : BorrowerApplicationViewModel by activityViewModels()
    private val viewModelSubProperty : SubjectPropertyViewModel by activityViewModels()
    private var propertyTypeList: ArrayList<DropDownResponse> = arrayListOf()
    private var occupancyTypeList:ArrayList<DropDownResponse> = arrayListOf()
    private var purchaseAddress = AddressData()
    private lateinit var adapterCoborrower: CoBorrowerAdapter
    var coborrowerList = ArrayList<CoBorrowerOccupancyData>()
    private var savedViewInstance: View? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (savedViewInstance != null){
            savedViewInstance
        } else {
            binding = SubjectPropertyPurchaseBinding.inflate(inflater, container, false)
            savedViewInstance = binding.root
            super.addListeners(binding.root)

            setupUI()
            setInputFields()
            setDropDownData()
            savedViewInstance

        }
    }

    private fun addObserver(){
        viewModelSubProperty.updatedAddress.observe(viewLifecycleOwner, {
            purchaseAddress = it
            //binding.tvSubPropertyAddress.text = it.street + " " + it.unit + "\n" + it.city + " " + it.stateName + " " + it.zipCode + " " + it.countryName
            val builder = StringBuilder()
            it.street?.let { builder.append(it).append(" ") }
            it.unit?.let { builder.append(it) }
            it.city?.let {builder.append("\n").append(it).append(" ") }
            it.stateName?.let{ builder.append(it).append(" ")}
            it.zipCode?.let { builder.append(it) }
            it.countryName.let { builder.append(" ").append(it)}
            binding.tvSubPropertyAddress.text = builder

            EventBus.getDefault().post(AddressUpdateEvent(it))
        })

        viewModelSubProperty.mixedPropertyDesc.observe(viewLifecycleOwner,{
            binding.radioMixedPropertyYes.isChecked = true
            binding.mixedPropertyExplanation.setText(it)
            binding.layoutMixedPropertyDetail.visibility = View.VISIBLE
        })
    }

    private fun getPurchaseDetails(detailCounter:Int){
        if(detailCounter==2) {
            viewModel.subjectPropertyDetails.observe(viewLifecycleOwner, { details ->
                if (details != null) {
                    details.subPropertyData?.addressData?.let {
                        if (it.street == null && it.unit == null && it.city == null && it.stateName == null && it.countryName == null) {
                            binding.radioSubPropertyTbd.isChecked = true
                            binding.radioSubPropertyTbd.setTypeface(null, Typeface.BOLD)
                        } else {
                            binding.radioSubPropertyAddress.isChecked = true
                            binding.radioTxtPropertyAdd.setTypeface(null, Typeface.BOLD)
                            binding.tvSubPropertyAddress.visibility = View.VISIBLE
                            //binding.tvSubPropertyAddress.text = it.street + " " + it.unit + "\n" + it.city + " " + it.stateName + " " + it.zipCode + " " + it.countryNameopen
                            displayAddress(it)
                            purchaseAddress = it // list for sending data to api
                        }

                    } ?: run {
                        binding.radioSubPropertyTbd.isChecked = true
                        binding.radioSubPropertyTbd.setTypeface(null, Typeface.BOLD)
                    }

                    // property id
                    details.subPropertyData?.propertyTypeId?.let { selectedId ->
                        for (item in propertyTypeList) {
                            if (item.id == selectedId) {
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
                    details.subPropertyData?.occupancyTypeId?.let { occupancyId ->
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
                    // appraised value
                    details.subPropertyData?.appraisedPropertyValue?.let { value ->
                        binding.edAppraisedPropertyValue.setText(Math.round(value).toString())
                        CustomMaterialFields.setColor(
                            binding.layoutAppraisedProperty,
                            R.color.grey_color_two,
                            requireActivity()
                        )

                    }
                    // property tax
                    details.subPropertyData?.propertyTax?.let { value ->
                        binding.edPropertyTax.setText(Math.round(value).toString())
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
                                binding.mixedPropertyExplanation.setText(desc)
                                binding.layoutMixedPropertyDetail.visibility = View.VISIBLE
                            }
                        } else
                            binding.radioMixedPropertyNo.isChecked = true
                    } ?: run {
                        //binding.radioMixedPropertyNo.isChecked = true
                    }
                    if (details.code.equals(AppConstant.RESPONSE_CODE_SUCCESS)) {
                        hideLoader()
                    }
                }
                hideLoader()
            })
        }
    }

    private fun setDropDownData(){
        lifecycleScope.launchWhenStarted {
            coroutineScope {
                var detailCounter:Int = 0
                viewModel.propertyType.observe(viewLifecycleOwner, { properties->
                    if (properties != null && properties.size > 0) {
                        detailCounter++
                        val itemList: ArrayList<String> = arrayListOf()
                        propertyTypeList = arrayListOf()
                        for (item in properties) {
                            itemList.add(item.name)
                            propertyTypeList.add(item)
                        }

                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
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
                                CustomMaterialFields.setColor(binding.layoutPropertyType, R.color.grey_color_two, requireActivity())
                            }
                        }
                        getPurchaseDetails(detailCounter)
                    }
                })

                // occupancy Type spinner
                viewModel.occupancyType.observe(viewLifecycleOwner, { occupancies ->
                    if (occupancies != null && occupancies.size > 0) {
                        detailCounter++
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
                            object : AdapterView.OnItemClickListener {
                                override fun onItemClick(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    CustomMaterialFields.setColor(
                                        binding.layoutOccupancyType,
                                        R.color.grey_color_two,
                                        requireActivity()
                                    )
                                }
                            }
                        getPurchaseDetails(detailCounter)
                    }
                })

                viewModelSubProperty.coBorrowerOccupancyStatus.observe(viewLifecycleOwner, {
                    if(it.occupancyData != null && it.occupancyData.size > 0) {
                        adapterCoborrower = CoBorrowerAdapter(requireContext(), this@SubjectPropertyPurchase)
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

    private fun processSendData(){
        // TBD
        var addressForApi = AddressData()
        var tbd : Boolean? = null
        if(binding.radioSubPropertyTbd.isChecked){
            tbd = true
        } else {
            tbd = false
            addressForApi = purchaseAddress
        }

        // get property id
        val property : String = binding.tvPropertyType.getText().toString().trim()
        val matchedList1 =  propertyTypeList.filter { p -> p.name.equals(property,true)}
        val propertyId = if(matchedList1.size > 0) matchedList1.map { matchedList1.get(0).id }.single() else null

        // get occupancy id
        val occupancy : String = binding.tvOccupancyType.getText().toString().trim()
        val matchedList =  occupancyTypeList.filter { s -> s.name.equals(occupancy,true)}
         var occupancyId = if(matchedList.size > 0)  matchedList.map { matchedList.get(0).id }.single() else null

        // mixed use property
        var isMixedUseProperty: Boolean? = null
        var mixedUsePropertyDesc : String? = null

        if(binding.radioMixedPropertyYes.isChecked) {
            isMixedUseProperty = true
            mixedUsePropertyDesc = binding.mixedPropertyExplanation.text.toString()  // desc
        }

        if(binding.radioMixedPropertyNo.isChecked) {
            isMixedUseProperty = false
        }

        // appraised value
        val appraisedValue = binding.edAppraisedPropertyValue.text.toString().trim()
        val newAppraisedValue = if(appraisedValue.length > 0) appraisedValue.replace(",".toRegex(), "") else "0"

        // property tax
        val propertyTax = binding.edPropertyTax.text.toString().trim()    //.length > 0)  binding.edPropertyTax.text.toString() else null
        val newPropertyTax = if(propertyTax.length > 0) propertyTax.replace(",".toRegex(), "") else "0"

        // home insurance
        val homeInsurance = binding.edHomeownerInsurance.text.toString().trim()
        val newHomeInsurance = if(homeInsurance.length > 0) homeInsurance.replace(",".toRegex(), "") else "0"

        val floodInsurance = binding.edFloodInsurance.text.toString().trim()
        val newFloodInsurance = if(floodInsurance.length > 0) floodInsurance.replace(",".toRegex(), "") else "0"


        lifecycleScope.launchWhenStarted{
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                val activity = (activity as? SubjectPropertyActivity)
                    activity?.loanApplicationId?.let {
                    //Log.e("Loan Application Id", ""+ it)

                    val propertyData = SubPropertyData(loanApplicationId = it,propertyTypeId = propertyId,occupancyTypeId = occupancyId,
                        appraisedPropertyValue = newAppraisedValue?.toDouble(),propertyTax = newPropertyTax?.toDouble(),homeOwnerInsurance=newHomeInsurance?.toDouble(),
                        floodInsurance = newFloodInsurance?.toDouble(),
                        addressData = addressForApi ,isMixedUseProperty= isMixedUseProperty,mixedUsePropertyExplanation=mixedUsePropertyDesc,subjectPropertyTbd = tbd)
                    showLoader()
                    //Log.e("PropertyData", ""+propertyData)
                    viewModelSubProperty.sendSubjectPropertyDetail(authToken,propertyData)
                }
            }
        }
    }

    private fun openAddress(){
        val fragment = SubPropertyAddressFragment()
        val bundle = Bundle()
        bundle.putParcelable(AppConstant.address,purchaseAddress)
        fragment.arguments = bundle
        findNavController().navigate(R.id.action_address, fragment.arguments)
    }

    fun setupUI() {
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
        // close
        binding.backButton.setOnClickListener {
            dismissActivity()
        }
        // back
        requireActivity().onBackPressedDispatcher.addCallback {
            dismissActivity()
        }

        binding.btnSave.setOnClickListener {
            processSendData()
        }

        binding.subpropertyParentLayout.setOnClickListener {
            HideSoftkeyboard.hide(requireActivity(),binding.subpropertyParentLayout)
            super.removeFocusFromAllFields(binding.subpropertyParentLayout)
        }
    }

    private fun gotoMixedUseProperty(){
        var mixedUsePropertyDesc : String? = ""
        mixedUsePropertyDesc = binding.mixedPropertyExplanation.text.toString()
        val fragment = MixedUsePropertyFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.MIXED_USE_PROPERTY_DESC,mixedUsePropertyDesc)
        fragment.arguments = bundle
        findNavController().navigate(R.id.action_mixed_property, fragment.arguments)
    }

    override fun onResume() {
        super.onResume()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<AddressData>(AppConstant.address)?.observe(
            viewLifecycleOwner) { result ->
            purchaseAddress = result
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
            binding.mixedPropertyExplanation.setText(result)
            binding.layoutMixedPropertyDetail.visibility = View.VISIBLE
        }
    }

    private fun displayAddress(it: AddressData){
        val builder = StringBuilder()

        it.street?.let {
            if(it != "null")
               builder.append(it).append(" ")
          }
        it.unit?.let {
            if(it != "null")
                builder.append(it).append(",")
            else
                builder.append(",")
        } ?: run { builder.append(",") }
        it.city?.let {
            if(it != "null")
                builder.append("\n").append(it).append(",").append(" ")
        } ?: run { builder.append("\n") }
        it.stateName?.let {
            if(it !="null") builder.append(it).append(" ")
        }
        it.zipCode?.let {
            if(it != "null")
                builder.append(it)
        }
        binding.tvSubPropertyAddress.text = builder

    }

    private fun setInputFields(){
        // set lable focus
        binding.edAppraisedPropertyValue.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edAppraisedPropertyValue, binding.layoutAppraisedProperty, requireContext()))
        binding.edPropertyTax.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edPropertyTax, binding.layoutPropertyTaxes, requireContext()))
        binding.edHomeownerInsurance.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edHomeownerInsurance, binding.layoutHomeownerInsurance, requireContext()))
        binding.edFloodInsurance.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edFloodInsurance, binding.layoutFloodInsurance, requireContext()))

        // set input format
        binding.edAppraisedPropertyValue.addTextChangedListener(NumberTextFormat(binding.edAppraisedPropertyValue))
        binding.edPropertyTax.addTextChangedListener(NumberTextFormat(binding.edPropertyTax))
        binding.edHomeownerInsurance.addTextChangedListener(NumberTextFormat(binding.edHomeownerInsurance))
        binding.edFloodInsurance.addTextChangedListener(NumberTextFormat(binding.edFloodInsurance))

        // set Dollar prifix
        CustomMaterialFields.setDollarPrefix(binding.layoutAppraisedProperty,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutPropertyTaxes,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutHomeownerInsurance,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutFloodInsurance,requireContext())
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

   /* private fun updateApplicationTab() {
        lifecycleScope.launchWhenStarted{
        sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
             val activity = (activity as? SubjectPropertyActivity)
             activity?.loanApplicationId?.let {
                 detailViewModel.getBorrowerApplicationTabData(token = authToken, loanApplicationId = it)
             }
         }
      }
        detailViewModel.borrowerApplicationTabModel.observe(viewLifecycleOwner, {
            Log.e("nwe data observed", "true")
            dismissActivity()
        })
    } */

    override fun onCoborrowerClick(position: Int, isOccupying: Boolean) {
        lifecycleScope.launchWhenStarted{
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                    //Log.e("frag", ""+ coborrowerList.get(position).borrowerId + " Occupying: " + isOccupying)
                    val data = AddCoBorrowerOccupancy(coborrowerList.get(position).borrowerId,isOccupying)
                    viewModelSubProperty.sendCoBorrowerOccupancy(authToken,data)
            }
        }
    }

}
/*
// filter map syntax
// one line
var propertyId =  propertyTypeList.filter { s -> s.name == property}.map{it.id}.single()
// 2 lines
val matchedList =  occupancyTypeList.filter { s -> s.name == occupancy}
val id = matchedList.map { matchedList.get(0).id }.single()
*/
