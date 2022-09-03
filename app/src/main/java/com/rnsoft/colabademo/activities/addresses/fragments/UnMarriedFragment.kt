package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.rnsoft.colabademo.databinding.UnmarriedLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.activities.model.StatesModel
import com.rnsoft.colabademo.databinding.SubjectPropertyPurchaseBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import kotlinx.android.synthetic.main.non_permenant_resident_layout.*
import kotlinx.android.synthetic.main.unmarried_layout.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception

@AndroidEntryPoint
class UnMarriedFragment : BaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: UnmarriedLayoutBinding
    private val viewModel : PrimaryBorrowerViewModel by activityViewModels()
    private val commonViewModel : CommonViewModel by activityViewModels()
    private var stateList: ArrayList<StatesModel> = arrayListOf()
    private var relationshipTypeList: ArrayList<DropDownResponse> = arrayListOf()
    var loanApplicationId : Int? = null
    var borrowerId : Int?= null
    private var maritalStatus : MaritalStatus? = null
    var firstName : String? = null
    var lastName : String? = null


    //private val relationshipTypes = listOf("Civil Unions", "Domestic Partners", "Registered Reciprocal", "Other")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UnmarriedLayoutBinding.inflate(inflater, container, false)
        super.addListeners(binding.root)

        val activity = (activity as? BorrowerAddressActivity)
        activity?.loanApplicationId?.let { loanId ->
            loanApplicationId = loanId }

        activity?.borrowerId?.let { bId ->
            borrowerId = bId
        }

        activity?.firstName?.let {
            firstName = it
        }
        activity?.lastName?.let {
            lastName = it
        }

        if(firstName !=null && lastName !=null){
            binding.borrowerName.setText(firstName.plus(" ").plus(lastName))
        }

        binding.radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.yesRadioBtn -> {
                    binding.yesRadioBtn.setTypeface(null, Typeface.BOLD)
                    binding.noRadioBtn.setTypeface(null, Typeface.NORMAL)
                    binding.relationshipContainer.visibility = View.VISIBLE
                }
                R.id.noRadioBtn -> {
                    binding.yesRadioBtn.setTypeface(null, Typeface.NORMAL)
                    binding.noRadioBtn.setTypeface(null, Typeface.BOLD)
                    binding.relationshipContainer.visibility = View.GONE
                }
                else -> {
                }
            }
        })

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            saveData()
        }

        setInputFields()
        setRelationShipField()


        return binding.root
    }

    private fun setData(counter:Int){
        if(counter==2) {
            try {
                maritalStatus = arguments?.getParcelable(AppConstant.marital_status)!!
                Log.e("umarriedFra-SetData","$maritalStatus")
                if (maritalStatus != null) {
                    maritalStatus?.isInRelationship?.let { isInRelation ->
                        if (isInRelation)
                            binding.yesRadioBtn.isChecked = true
                        else
                            binding.noRadioBtn.isChecked = true
                    }

                    maritalStatus?.relationshipTypeId?.let { relationshipId ->
                        for (item in relationshipTypeList) {
                            if (item.id == relationshipId) {
                                binding.tvRelationship.setText(item.name, false)
                                CustomMaterialFields.setColor(
                                    binding.relationTypeLayout,
                                    R.color.grey_color_two,
                                    requireActivity()
                                )
                                break
                            }
                        }
                    }

                    maritalStatus?.relationFormedStateId?.let { stateId ->
                        for (item in stateList) {
                            if (item.id == stateId) {
                                binding.tvState.setText(item.name, false)
                                CustomMaterialFields.setColor(
                                    binding.layoutState,
                                    R.color.grey_color_two,
                                    requireActivity()
                                )
                                break
                            }
                        }
                    }
                    maritalStatus?.otherRelationshipExplanation?.let {
                        if (it.isNotEmpty() && it.isNotBlank())
                            binding.tvDesc.setText(it)
                        binding.layouDesc.visibility = View.VISIBLE
                    }

                }
            } catch (e: Exception){
            }
        }

    }

    private fun saveData(){
        if(binding.relationshipContainer.isVisible){
            val relation = binding.tvRelationship.getText().toString().trim()
            val desc = binding.tvDesc.getText().toString()

            if(relation.isEmpty() || relation.length == 0){
                CustomMaterialFields.setError(binding.relationTypeLayout, getString(R.string.error_field_required), requireActivity())
            }
            if(relation.length >0){
                CustomMaterialFields.clearError(binding.relationTypeLayout, requireActivity())
            }

            val state = binding.tvState.getText().toString().trim()
            if(state.isEmpty() || state.length == 0){
                CustomMaterialFields.setError(binding.layoutState, getString(R.string.error_field_required), requireActivity())
            }
            if(state.length >0){
                CustomMaterialFields.clearError(binding.layoutState, requireActivity())
            }
            if(binding.layouDesc.isVisible && desc.length == 0){
                CustomMaterialFields.setError(binding.layouDesc, getString(R.string.error_field_required), requireActivity())
            }
            if(binding.layouDesc.isVisible && desc.length >0){
                CustomMaterialFields.clearError(binding.layouDesc, requireActivity())
            }

            if(relation.length > 0 && state.length > 0){
                var isDataEntered: Boolean = true
                if(binding.layouDesc.isVisible && desc.length == 0){
                    isDataEntered = false
                }
                if(isDataEntered){
                     try {
                         //Log.e("relationshipList","$relationshipTypeList")
                         val matchedList = relationshipTypeList.filter { p -> p.name.equals(relation, true) }
                         val relationshipTypeId = if (matchedList.size > 0) matchedList.map { matchedList.get(0).id }.single() else null

                         val stateName: String = binding.tvState.getText().toString()
                         val matchedState = stateList.filter { p -> p.name.equals(stateName, true) }
                         val stateId = if (matchedState.size > 0) matchedState.get(0).id else null

                         var isInRelationship: Boolean? = null
                         if (yesRadioBtn.isChecked)
                             isInRelationship = true
                         if (noRadioBtn.isChecked)
                             isInRelationship = false


                         val maritalStatus = MaritalStatus(
                             loanApplicationId = loanApplicationId!!,
                             borrowerId = if (borrowerId != null) borrowerId else null,
                             firstName = null,
                             middleName = null,
                             lastName = null,
                             isInRelationship = isInRelationship,
                             otherRelationshipExplanation = if (desc.length > 0) desc else null,
                             relationFormedStateId = stateId,
                             relationshipTypeId = relationshipTypeId,
                             spouseBorrowerId = null,
                             spouseLoanContactId = null,
                             maritalStatusId = 9
                         )

                         Log.e("UnMarriedFrag", "" + maritalStatus)

                         findNavController().previousBackStackEntry?.savedStateHandle?.set(
                             AppConstant.marital_status,
                             maritalStatus
                         )
                         findNavController().popBackStack()

                     } catch (e:Exception){
                         //Log.e("exception",e.toString())
                     }
                }
            }
        } else {

            var isInRelationship : Boolean? = false
            if(yesRadioBtn.isChecked)
                isInRelationship = true
            if(noRadioBtn.isChecked)
                isInRelationship = false


            val maritalStatus = MaritalStatus(
                loanApplicationId = loanApplicationId!!,
                borrowerId = if(borrowerId != null) borrowerId else null,
                firstName = null,
                middleName = null,
                lastName = null,
                isInRelationship = isInRelationship,
                otherRelationshipExplanation = null,
                relationFormedStateId = null,
                relationshipTypeId = null,
                spouseBorrowerId = null,
                spouseLoanContactId = null,
                maritalStatusId = 9)

            Log.e("UnMarriedFrag",""+maritalStatus)
            findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.marital_status, maritalStatus)
            findNavController().popBackStack()


        }
    }

    private fun setRelationShipField(){
        lifecycleScope.launchWhenStarted {
            var dataCounter: Int = 0
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                binding.loader.visibility = View.VISIBLE
                coroutineScope {


                    commonViewModel.getStates(authToken)

                    viewModel.getRelationshipTypes(authToken)
                }

                lifecycleScope.launchWhenStarted {
                    commonViewModel.states.observe(viewLifecycleOwner, { states ->
                            if (states != null && states.size > 0) {
                                val itemList: ArrayList<String> = arrayListOf()
                                for (item in states) {
                                    itemList.add(item.name)
                                    stateList.add(item)
                                }
                                dataCounter++

                                val stateAdapter = ArrayAdapter(
                                        requireContext(),
                                        R.layout.autocomplete_text_view,
                                        itemList
                                    )
                                binding.tvState.setAdapter(stateAdapter)

                                /* binding.tvState.setOnFocusChangeListener { _, _ ->
                                binding.tvState.showDropDown()
                                HideSoftkeyboard.hide(requireActivity(), binding.layoutState)
                            }
                            binding.tvState.setOnClickListener {
                                binding.tvState.showDropDown()
                                HideSoftkeyboard.hide(requireActivity(), binding.layoutState)
                            } */

                                binding.tvState.onItemClickListener =
                                    object : AdapterView.OnItemClickListener {
                                        override fun onItemClick(
                                            p0: AdapterView<*>?,
                                            p1: View?,
                                            position: Int,
                                            id: Long
                                        ) {
                                            binding.layoutState.defaultHintTextColor =
                                                ColorStateList.valueOf(
                                                    ContextCompat.getColor(
                                                        requireContext(),
                                                        R.color.grey_color_two
                                                    )
                                                )
                                            HideSoftkeyboard.hide(requireActivity(), binding.layoutState)
                                        }
                                    }
                            }

                        })

                    viewModel.relationships.observe(viewLifecycleOwner, { list ->
                            if (list != null && list.size > 0) {
                                dataCounter++
                                val itemList: ArrayList<String> = arrayListOf()
                                for (item in list) {
                                    itemList.add(item.name)
                                    relationshipTypeList.add(item)
                                }
                                val relationshipAdapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_list_item_1,
                                    itemList
                                )
                                binding.tvRelationship.setAdapter(relationshipAdapter)

                                binding.tvRelationship.setOnClickListener {
                                    binding.tvRelationship.showDropDown()
                                }
                                binding.tvRelationship.onItemClickListener =
                                    object : OnItemClickListener {
                                        override fun onItemClick(
                                            p0: AdapterView<*>?,
                                            p1: View?,
                                            position: Int,
                                            id: Long
                                        ) {
                                            binding.relationTypeLayout.defaultHintTextColor =
                                                ColorStateList.valueOf(
                                                    ContextCompat.getColor(
                                                        requireContext(),
                                                        R.color.grey_color_two
                                                    )
                                                )

                                            val item = binding.tvRelationship.text.toString()
                                            if (item.equals("Other", true)) {
                                                binding.layouDesc.visibility = View.VISIBLE
                                            } else {
                                                binding.layouDesc.visibility = View.GONE
                                            }
                                        }
                                    }
                            }

                        setData(dataCounter)
                        })

                    binding.loader.visibility = View.INVISIBLE
                }

            }
        }


    }

    private fun setInputFields(){
        binding.tvState.setOnFocusChangeListener { p0: View?, hasFocus: Boolean ->
            if (hasFocus) {
                //binding.tvState.showDropDown()
                //binding.tvState.addTextChangedListener(stateTextWatcher)
                CustomMaterialFields.setColor(binding.layoutState, R.color.grey_color_two, requireActivity())

            } else {
                //binding.tvState.removeTextChangedListener(stateTextWatcher)
                val state: String = binding.tvState.text.toString()
                if (state.length == 0) {
                    CustomMaterialFields.setError(binding.layoutState,getString(R.string.error_field_required),requireActivity())
                    CustomMaterialFields.setColor(binding.layoutState, R.color.grey_color_three, requireActivity())
                } else {
                    CustomMaterialFields.clearError(binding.layoutState,requireActivity())
                    CustomMaterialFields.setColor(binding.layoutState, R.color.grey_color_two, requireActivity())
                }
            }
        }

        binding.tvRelationship.setOnFocusChangeListener { p0: View?, hasFocus: Boolean ->
            if (hasFocus) {
                binding.tvRelationship.showDropDown()
                CustomMaterialFields.setColor(binding.relationTypeLayout, R.color.grey_color_two, requireActivity())
            } else {
                val value: String = binding.tvRelationship.text.toString()
                if (value.length == 0) {
                    CustomMaterialFields.setError(binding.relationTypeLayout,getString(R.string.error_field_required),requireActivity())
                    CustomMaterialFields.setColor(binding.relationTypeLayout, R.color.grey_color_three, requireActivity())
                } else {
                    CustomMaterialFields.clearError(binding.relationTypeLayout,requireActivity())
                    CustomMaterialFields.setColor(binding.relationTypeLayout, R.color.grey_color_two, requireActivity())
                }
            }
        }

        binding.tvDesc.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.tvDesc, binding.layouDesc, requireContext(),getString(R.string.error_field_required)))

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
    fun onErrorEvent(webEvent: WebServiceErrorEvent) {
        binding.loader.visibility = View.INVISIBLE
        SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG)
        findNavController().popBackStack()
    }

    /*private fun setStateField(){
        val stateNamesAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,  AppSetting.states)
        binding.stateCompleteTextView.setAdapter(stateNamesAdapter)
        binding.stateCompleteTextView.setOnFocusChangeListener { _, _ ->
            binding.stateCompleteTextView.showDropDown()
        }
        binding.stateCompleteTextView.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                binding.stateCompleteTextInputLayout.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey_color_two ))
            }
        }
    } */

}