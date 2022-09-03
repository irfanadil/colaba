package com.rnsoft.colabademo

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.compose.ui.text.capitalize
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.rnsoft.colabademo.databinding.SpouseDetailLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import java.lang.Exception
import kotlin.concurrent.fixedRateTimer


class MarriageDetailFragment : BaseFragment() {

    private lateinit var binding : SpouseDetailLayoutBinding
    var marriage_type : String? = null
    var borrower_name : String? = null
    var loanApplicationId : Int? = null
    var borrowerId : Int?= null
    var coBorrowerCount : Int = 0
    var ownTypeId : Int = 0
    val coborrowerList: ArrayList<CoborrowerList> = arrayListOf()
    private var maritalStatus : MaritalStatus? = null
    var firstName : String? = null
    var lastName : String? = null
    var relationLayout : Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SpouseDetailLayoutBinding.inflate(inflater, container, false)
        super.addListeners(binding.root)

        arguments?.let { arguments ->
            marriage_type = arguments.getString(AppConstant.marriage_type)
            binding.marriageType.text = marriage_type
        }

        val activity = (activity as? BorrowerAddressActivity)
        activity?.firstName?.let {
            firstName = it
        }
        activity?.lastName?.let {
            lastName = it
        }
        activity?.ownTypeId?.let {
            ownTypeId = it
        }

        if(firstName !=null && lastName !=null){
            binding.borrowerName.setText(firstName.plus(" ").plus(lastName))
        }


        initViews()
        setCoBorrowers()

        if(ownTypeId == 1){
            //Log.e("coBoorrowerCount",""+coBorrowerCount)
            if (coBorrowerCount == 0){
                binding.layoutQuestion.visibility = View.GONE // don't ask ques only ask names
                binding.spouseInfoLayout.visibility = View.VISIBLE
                if (marriage_type.equals(AppConstant.married)) {
                    binding.layoutFirstName.hint = "Spouse First Name"
                    binding.layoutMiddleName.hint = "Spouse Middle Name"
                    binding.layoutLastName.hint = "Spouse Last Name"
                }
                if (marriage_type.equals(AppConstant.separated)) {
                    binding.layoutFirstName.hint = "Legal Spouse First Name"
                    binding.layoutMiddleName.hint = "Legal Spouse Middle Name"
                    binding.layoutLastName.hint = "Legal Spouse Last Name"
                }
            } else {
                //Log.e("CoBorrowercount","not 0")
                if (marriage_type.equals(AppConstant.married)) {
                    binding.tvQuestion.text = getString(R.string.married_to_coborower)
                }
                if (marriage_type.equals(AppConstant.separated)) {
                    binding.tvQuestion.text = getString(R.string.coborower_legal_spouse)
                }
            }
        } else {
            //Log.e("Owntype 2","else")
            binding.layoutQuestion.visibility = View.GONE // don't ask ques only ask names
            binding.spouseInfoLayout.visibility = View.VISIBLE
            try {
                setLabels(marriage_type!!)
            } catch (e:Exception){}
        }


        setData()

        return binding.root
    }

    private fun setData(){
        try {
            maritalStatus = arguments?.getParcelable(AppConstant.marital_status)!!
            if(maritalStatus != null){
                 maritalStatus?.spouseBorrowerId?.let { selectedId->
                    for(item in coborrowerList){
                        if (item.borrowerId == selectedId){
                            binding.yesRadioBtn.isChecked = true
                            binding.tvCoborrower.setText(item.coborrowerFirstName.plus( " ").plus(item.coborrowerLastName), false)
                            setColor(binding.layoutCoborrower)
                            break
                        }
                    }
                }
                maritalStatus?.firstName?.let { firstName->
                    binding.noRadioBtn.isChecked = true
                    if(firstName.isNotEmpty() && firstName.isNotBlank()){
                        binding.etFirstName.setText(firstName.capitalize())
                        setColor(binding.layoutFirstName)
                    }
                }
                maritalStatus?.middleName?.let { middleName->
                    binding.noRadioBtn.isChecked = true
                    if(middleName.isNotEmpty() && middleName.isNotBlank()){
                        binding.etMiddleName.setText(middleName.capitalize())
                        setColor(binding.layoutMiddleName)
                    }
                }
                maritalStatus?.lastName?.let { lastName->
                    binding.noRadioBtn.isChecked = true
                    if(lastName.isNotEmpty() && lastName.isNotBlank()){
                        binding.etLastName.setText(lastName.capitalize())
                        setColor(binding.layoutLastName)
                    }
                }

                // if spouse borrower id null && names are also null
                if(ownTypeId == 2) {
                    //maritalStatus?.spouseBorrowerId?.let { spouseBorrowerId->
                   // Log.e("SetData", "" + ownTypeId)
                   // Log.e("MarriageType", "" + marriage_type)
                    if (maritalStatus?.spouseBorrowerId == 0 || maritalStatus?.spouseBorrowerId == null) {
                        binding.layoutCoborrower.visibility = View.GONE
                        binding.layoutQuestion.visibility = View.GONE
                        binding.spouseInfoLayout.visibility = View.VISIBLE

                        if (marriage_type.equals(AppConstant.married)) {
                            binding.layoutFirstName.hint = "Spouse First Name"
                            binding.layoutMiddleName.hint = "Spouse Middle Name"
                            binding.layoutLastName.hint = "Spouse Last Name"
                        }

                        if (marriage_type.equals(AppConstant.separated)) {
                            binding.layoutFirstName.hint = "Legal Spouse First Name"
                            binding.layoutMiddleName.hint = "Legal Spouse Middle Name"
                            binding.layoutLastName.hint = "Legal Spouse Last Name"
                        }
                    } else if (maritalStatus?.spouseBorrowerId != 0 || maritalStatus?.spouseBorrowerId != null) {
                        //Log.e("If-Else", "True")
                        // co borrower and primary borrower relationship
                        if (maritalStatus?.relationWithPrimaryId != 0 && maritalStatus?.relationWithPrimaryId != null)
                            maritalStatus?.spouseBorrowerId?.let {
                                //Log.e("scondtion ", "matched")
                                // get primary borrower name
                                val activity = (activity as? BorrowerAddressActivity)
                                activity?.borrowerInfoList?.let { list ->
                                    for (i in 0 until list.size) {
                                        if (list.get(i).borrowerId == it) {
                                            relationLayout = true
                                            binding.yesRadioBtn.isChecked = true
                                            binding.layoutCoborrower.visibility = View.GONE
                                            binding.spouseInfoLayout.visibility = View.GONE
                                            binding.primaryRelationInfoLayout.visibility =
                                                View.VISIBLE
                                            list.get(i).firstName?.let { fname ->
                                                binding.layoutQuestion.visibility = View.VISIBLE
                                                if (marriage_type.equals(AppConstant.married)) {
                                                    binding.tvQuestion.text =
                                                        "Are you married with ".plus(fname)
                                                            .plus("?")
                                                } else if (marriage_type.equals(AppConstant.separated)) {
                                                    binding.tvQuestion.text = "Is ".plus(fname)
                                                        .plus(" your legal spouse?")
                                                }


                                                binding.etSpouseFirstName.setText(fname.capitalize())
                                                binding.etSpouseFirstName.isEnabled = false
                                                binding.etSpouseMiddleName.isEnabled = false
                                                binding.etSpouseLastName.isEnabled = false
                                                setColor(binding.layoutSpouseFirstName)
                                            }
                                            list.get(i).middleName?.let { mname ->
                                                binding.etSpouseMiddleName.setText(mname.capitalize())
                                                setColor(binding.layoutSpouseMiddleName)
                                            }
                                            list.get(i).lastName?.let { lname ->
                                                binding.etSpouseLastName.setText(lname.capitalize())
                                                setColor(binding.layoutSpouseLastName)
                                            }
                                            break
                                        }
                                    }
                                }
                            }
                    } else {
                        //Log.e("Else", "unexpected")
                    }
                }
            } else {
               //Log.e("marital","Status-null")
            }
        } catch (e:Exception){
            Log.e("Exception",e.toString())
        }

    }

    private fun setCoBorrowers(){
        val activity = (activity as? BorrowerAddressActivity)
        activity?.borrowerInfoList?.let { list->

            if(list.size > 0){
                val itemList: ArrayList<String> = arrayListOf()
                try {
                    for (item in list.indices) {
                        if (list.get(item).owntypeId == 2) {
                            var fName: String? = null
                            var lName: String? = null
                            list.get(item).firstName?.let {
                                fName = it
                            }
                            list.get(item).lastName?.let {
                                lName = it
                            }
                            if (fName !=null && fName !="null" && lName !=null && lName !="null") {
                                itemList.add(list.get(item).firstName + " " + list.get(item).lastName)
                                coborrowerList.add(
                                    CoborrowerList(list.get(item).borrowerId, list.get(item).owntypeId, fName!!, lName!!
                                    )
                                )
                            }
                        }
                    }
                } catch (e:Exception){
                    //Log.e("exception found","setting co-borrowers")
                }
                coBorrowerCount = coborrowerList.size

                //Log.e("coborrowerList",""+coborrowerList)


                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
                binding.tvCoborrower.setAdapter(adapter)

                binding.tvCoborrower.setOnFocusChangeListener { _, _ ->
                    binding.tvCoborrower.showDropDown()
                }
                binding.tvCoborrower.setOnClickListener {
                    binding.tvCoborrower.showDropDown()
                }

                binding.tvCoborrower.onItemClickListener = object :
                    AdapterView.OnItemClickListener {
                    override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                        CustomMaterialFields.setColor(binding.layoutCoborrower, R.color.grey_color_two, requireActivity())
                    }
                }
            }
        }
    }

    private fun setLabels(type: String){
        if(type.equals(AppConstant.married)){
            binding.layoutFirstName.hint = "Spouse First Name"
            binding.layoutMiddleName.hint = "Spouse Middle Name"
            binding.layoutLastName.hint = "Spouse Last Name"
        }
        if (type.equals(AppConstant.separated)) {
            binding.layoutFirstName.hint = "Legal Spouse First Name"
            binding.layoutMiddleName.hint = "Legal Spouse Middle Name"
            binding.layoutLastName.hint = "Legal Spouse Last Name"
        }
    }

    private fun saveData(){
        var isDataEntered : Boolean = false

        if(binding.layoutCoborrower.isVisible){
            val coborrower = binding.tvCoborrower.getText().toString().trim()
            if(coborrower.isEmpty() || coborrower.length == 0){
                isDataEntered = false
                CustomMaterialFields.setError(binding.layoutCoborrower, getString(R.string.error_field_required), requireActivity())
            }
            if(coborrower.length >0){
                isDataEntered = true
                CustomMaterialFields.clearError(binding.layoutCoborrower, requireActivity())
            }
        }

        if(binding.spouseInfoLayout.isVisible){
            isDataEntered = true
        }

        if(binding.primaryRelationInfoLayout.isVisible){
            isDataEntered = true
        }

        if(isDataEntered){
            val activity = (activity as? BorrowerAddressActivity)
            activity?.loanApplicationId?.let { loanId ->
                loanApplicationId = loanId }

            activity?.borrowerId?.let { bId ->
                if(bId != 0 && bId != -1) {
                    borrowerId = bId
                }
            }
            if(loanApplicationId != null){

                var firstName: String? = null
                var lastName: String? = null
                var middleName: String? = null
                var spouseBorrowerId: Int? = null
                if(binding.spouseInfoLayout.isVisible){
                    firstName = if (binding.etFirstName.text.toString().trim().length > 0) binding.etFirstName.text.toString() else null
                    lastName = if (binding.etLastName.text.toString().trim().length > 0) binding.etLastName.text.toString() else null
                    middleName = if (binding.etMiddleName.text.toString().trim().length > 0) binding.etMiddleName.text.toString() else null
                }

                if(binding.layoutCoborrower.isVisible){
                    val coborrowerName : String = binding.tvCoborrower.getText().toString().trim()
                    if(coborrowerName.length > 0 && coborrowerList.size > 0){
                        for(i in coborrowerList.indices){
                            var name: String =  coborrowerList.get(i).coborrowerFirstName.plus(" ").plus(coborrowerList.get(i).coborrowerLastName).trim()
                            if(name.equals(coborrowerName,true)){
                                spouseBorrowerId = coborrowerList.get(i).borrowerId // get borrower id coborrower
                                break
                            }
                        }
                    }
                    //Log.e("SpouseId",  ""+spouseBorrowerId)
                }

                // don't sent   relationWithPrimaryId = null, spouseMaritalStatusId for updated

                var maritalStatusId : Int =  if(marriage_type.equals(AppConstant.married)) 1 else 2

                val maritalStatus = MaritalStatus(
                    loanApplicationId = loanApplicationId!!,
                    borrowerId = if(borrowerId != null) borrowerId else null,
                    firstName = firstName,
                    middleName = middleName ,
                    lastName = lastName,
                    isInRelationship = null,
                    otherRelationshipExplanation = null ,
                    relationFormedStateId = null,
                    relationshipTypeId = null,
                    spouseBorrowerId = spouseBorrowerId,
                    spouseLoanContactId = null,
                    maritalStatusId = maritalStatusId)

                //Log.e("MarriageFrag","$maritalStatus")

                findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.marital_status, maritalStatus)
                findNavController().popBackStack()
            }
        }
    }

    private fun initViews(){
        binding.etFirstName.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.etFirstName, binding.layoutFirstName, requireContext()))
        binding.etMiddleName.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.etMiddleName, binding.layoutMiddleName, requireContext()))
        binding.etLastName.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.etLastName, binding.layoutLastName, requireContext()))

        binding.btnSave.setOnClickListener{
            saveData()
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.yesRadioBtn.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                if(relationLayout){
                    binding.layoutCoborrower.visibility = View.GONE
                    binding.primaryRelationInfoLayout.visibility = View.VISIBLE
                } else {
                    binding.layoutCoborrower.visibility = View.VISIBLE
                    binding.primaryRelationInfoLayout.visibility = View.GONE
                    //binding.tvCoborrower.setText("")
                }
            }
            else {
                binding.layoutCoborrower.visibility = View.GONE
                binding.yesRadioBtn.setTypeface(null, Typeface.NORMAL)
            }
        }

        binding.noRadioBtn.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.spouseInfoLayout.visibility = View.VISIBLE
                binding.primaryRelationInfoLayout.visibility = View.GONE
                binding.noRadioBtn.setTypeface(null, Typeface.BOLD)
            }
            else {
                binding.spouseInfoLayout.visibility = View.GONE
                binding.noRadioBtn.setTypeface(null, Typeface.NORMAL)
            }
        }
    }

    private fun setColor(textInputLayout: TextInputLayout){
        CustomMaterialFields.setColor(textInputLayout, R.color.grey_color_two, requireActivity())
    }

}