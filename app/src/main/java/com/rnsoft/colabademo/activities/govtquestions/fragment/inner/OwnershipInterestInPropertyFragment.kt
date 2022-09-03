package com.rnsoft.colabademo
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.rnsoft.colabademo.R
import com.rnsoft.colabademo.databinding.OwnershipInterestInPropertyLayoutBinding
import com.rnsoft.colabademo.utils.Common
import com.rnsoft.colabademo.utils.CustomMaterialFields

import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject


@AndroidEntryPoint
class OwnershipInterestInPropertyFragment : BaseFragment() {

    private var _binding: OwnershipInterestInPropertyLayoutBinding? = null
    private val binding get() = _binding!!
    private var ownerShipInnerScreenParams:ArrayList<String> = arrayListOf()
    private var whichBorrowerId:Int = 0
    companion object{
        const val ownershipQuestionOne =    "What type of property did you own?"
        const val ownershipQuestionTwo =    "How did you hold title to the property?"
    }

     private var updateGovernmentQuestionByBorrowerId:GovernmentParams? = null
    private var questionId:Int = 0

    private val dataArray: ArrayList<String> = arrayListOf("Primary Residence",   "Second Home", "Investment Property",)

    private val dataArray2: ArrayList<String> = arrayListOf("By Yourself", "Jointly with your spouse", "Jointly with another person")

    private val array1 = HashMap<String, Int>()

    private val array2 = HashMap<String, Int>()

    private var userName:String? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = OwnershipInterestInPropertyLayoutBinding.inflate(inflater, container, false)
        arguments?.let { arguments->
            ownerShipInnerScreenParams = arguments.getStringArrayList(AppConstant.ownerShipGlobalData)!!
            questionId = arguments.getInt(AppConstant.questionId)
            whichBorrowerId = arguments.getInt(AppConstant.whichBorrowerId)
            updateGovernmentQuestionByBorrowerId = arguments.getParcelable(AppConstant.addUpdateQuestionsParams)
            userName = arguments.getString(AppConstant.govtUserName)
        }
        binding.borrowerPurpose.text = userName
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backToGovernmentScreen )
        val root: View = binding.root
        setUpUI()
        super.addListeners(binding.root)
        return root
    }

    private val backToGovernmentScreen: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }

    private fun setUpUI(){

        array1.put(dataArray[0] , 1 )
        array1.put( dataArray[1] , 3)
        array1.put(dataArray[2] , 4 )


        array2.put( dataArray2[0], 1)
        array2.put( dataArray2[1] ,2)
        array2.put( dataArray2[2], 4)


        val stateNamesAdapter = ArrayAdapter(binding.root.context, android.R.layout.simple_list_item_1,  dataArray)
        binding.transactionAutoCompleteTextView.setAdapter(stateNamesAdapter)
        binding.transactionAutoCompleteTextView.setOnFocusChangeListener { _, _ ->
            HideSoftkeyboard.hide(requireContext(),  binding.transactionAutoCompleteTextView)
            binding.transactionAutoCompleteTextView.showDropDown()
        }
        binding.transactionAutoCompleteTextView.setOnClickListener{
            binding.transactionAutoCompleteTextView.showDropDown()
        }

        binding.transactionAutoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, position, id ->
                binding.transactionTextInputLayout.defaultHintTextColor = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.grey_color_two))

                binding.transactionTextInputLayout.helperText?.let{
                    if(it.isNotEmpty())
                        CustomMaterialFields.clearError(binding.transactionTextInputLayout, requireContext())
                }

                removeErrorFromFields()
                clearFocusFromFields()
            }


        val dataArrayAdapter2 = ArrayAdapter(binding.root.context, android.R.layout.simple_list_item_1,  dataArray2)
        binding.whichAssetsCompleteView.setAdapter(dataArrayAdapter2)
        binding.whichAssetsCompleteView.setOnFocusChangeListener { _, _ ->
            HideSoftkeyboard.hide(requireContext(),  binding.whichAssetsCompleteView)
            binding.whichAssetsCompleteView.showDropDown()
        }
        binding.whichAssetsCompleteView.setOnClickListener{
            binding.whichAssetsCompleteView.showDropDown()
        }

        binding.whichAssetsCompleteView.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                binding.whichAssetInputLayout.defaultHintTextColor = ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.grey_color_two))

                binding.whichAssetInputLayout.helperText?.let{
                    if(it.isNotEmpty())
                        CustomMaterialFields.clearError(binding.whichAssetInputLayout, requireContext())
                }
                if(position==dataArray2.size-1) {

                }
                else{

                }
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.saveBtn.setOnClickListener {
            val fieldsValidated = checkEmptyFields()
            if(fieldsValidated) {
                clearFocusFromFields()
                updateOwnershipInterest()
                findNavController().popBackStack()
            }
        }

        fillWithGlobalData()

    }

    private fun updateOwnershipInterest(){
        updateGovernmentQuestionByBorrowerId?.let { updateGovernmentQuestionByBorrowerId ->
            lifecycleScope.launchWhenStarted {
                sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->

                    val answer1 = binding.transactionAutoCompleteTextView.text.toString()
                    val answer2 = binding.whichAssetsCompleteView.text.toString()

                    EventBus.getDefault().post(
                        OwnershipInterestUpdateEvent(ownershipQuestionOne, answer1 , array1.getValue(answer1),
                            ownershipQuestionTwo, answer2,  array2.getValue(answer2), whichBorrowerId )

                    )

                    Timber.e(" Now = "+array1.getValue(answer1) + " = "+array2.getValue(answer2))


                }
            }
        }
    }


    private fun fillWithGlobalData(){
        if(ownerShipInnerScreenParams.size>0)
            binding.transactionAutoCompleteTextView.setText(ownerShipInnerScreenParams[0], false)
        if(ownerShipInnerScreenParams.size>1)
            binding.whichAssetsCompleteView.setText(ownerShipInnerScreenParams[1], false)
    }

    private fun checkEmptyFields():Boolean{
        var bool =  true
        if(binding.transactionAutoCompleteTextView.text?.isEmpty() == true || binding.transactionAutoCompleteTextView.text?.isBlank() == true) {
            CustomMaterialFields.setError(binding.transactionTextInputLayout, "This field is required." , requireContext())
            bool = false
        }
        else
            CustomMaterialFields.clearError(binding.transactionTextInputLayout,  requireContext())

        if(binding.whichAssetsCompleteView.text?.isEmpty() == true || binding.whichAssetsCompleteView.text?.isBlank() == true) {
            CustomMaterialFields.setError(binding.whichAssetInputLayout, "This field is required." , requireContext())
            bool = false
        }
        else
            CustomMaterialFields.clearError(binding.whichAssetInputLayout,  requireContext())

        return bool
    }

    private fun removeErrorFromFields(){
       // CustomMaterialFields.clearError(binding.annualBaseLayout,  requireContext())
        CustomMaterialFields.clearError(binding.transactionTextInputLayout,  requireContext())
        CustomMaterialFields.clearError(binding.whichAssetInputLayout,  requireContext())
    }

    private fun clearFocusFromFields(){
        //binding.annualBaseLayout.clearFocus()
        binding.whichAssetInputLayout.clearFocus()
        binding.transactionTextInputLayout.clearFocus()
    }


}
