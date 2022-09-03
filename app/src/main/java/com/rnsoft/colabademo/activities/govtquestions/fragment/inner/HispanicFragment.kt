package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.HispanicLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HispanicFragment:BaseFragment() {

    private var _binding: HispanicLayoutBinding? = null
    private val binding get() = _binding!!

    private var ethnicityChildList:ArrayList<EthnicityDetailDemoGraphic>? = null
    private var userName:String? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = HispanicLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        ethnicityChildList = arguments?.getParcelableArrayList(AppConstant.ethnicityChildList)!!
        userName = arguments?.getString(AppConstant.govtUserName)
        binding.borrowerPurpose.text = userName
        setUpUI()
        super.addListeners(binding.root)
        return root
    }


    private fun setUpUI() {
        binding.edDetails.onFocusChangeListener = CustomFocusListenerForEditText( binding.edDetails , binding.layoutDetail , requireContext())
        binding.backButton.setOnClickListener {   findNavController().popBackStack() }
        binding.saveBtn.setOnClickListener {
            backToCallingScreen()

        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, testCallback )

        ethnicityChildList?.let { ethnicityChildList ->
            for (item in ethnicityChildList) {
                if (item.detailId == 1) {
                    binding.mexican.isChecked = true
                } else if (item.detailId == 2) {
                    binding.puertoRican.isChecked = true
                } else if (item.detailId == 3) {
                    binding.cuban.isChecked = true
                } else if (item.detailId == 4 && item.isOther == true) {
                    binding.otherHispanicOrLatino.performClick()
                    item.otherEthnicity?.let {
                        binding.edDetails.setText(it)
                    }
                }
            }
        }

        binding.mexican.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateTypes(1, binding.mexican.text.toString(), isChecked,false)
        }
        binding.puertoRican.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateTypes(2, binding.puertoRican.text.toString(),  isChecked,false)
        }
        binding.cuban.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateTypes(3,binding.cuban.text.toString(), isChecked, false)
        }
        binding.otherHispanicOrLatino.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateTypes(4, binding.otherHispanicOrLatino.text.toString(), isChecked , true, binding.edDetails.text.toString() )
        }
    }

    private fun updateTypes(id:Int, fieldName:String, removeFromList:Boolean, isOther: Boolean, otherPlaceHolder: String = "" ){
        ethnicityChildList?.let { ethnicityChildList ->
            if (!removeFromList) {
                for (item in ethnicityChildList) {
                    if (item.detailId == id) {
                        ethnicityChildList.remove(item)
                        break
                    }
                }
            } else
                ethnicityChildList.add(EthnicityDetailDemoGraphic(detailId = id, name = fieldName, isOther = isOther, otherEthnicity  = otherPlaceHolder))
        }
    }

    private fun checkEmptyFields():Boolean{
        var bool = true
        if(binding.edDetails.text?.isEmpty() == true || binding.edDetails.text?.isBlank() == true) {
            CustomMaterialFields.setError(binding.layoutDetail, "This field is required." , requireContext())
            bool = false
        }
        else
            CustomMaterialFields.clearError(binding.layoutDetail,  requireContext())

        return bool
    }

    private val testCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }

    private fun backToCallingScreen(){
        ethnicityChildList?.let { ethnicityChildList ->
            for (item in ethnicityChildList) {
                if(item.detailId == 4){
                    item.otherEthnicity = binding.edDetails.text.toString()
                }
            }
        }
        findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.selectedEthnicityChildList, ethnicityChildList)
        findNavController().popBackStack()
    }
}