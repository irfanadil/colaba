package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.NativeHawaiianLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NativeHawaiFragment:BaseFragment() {

    private var _binding: NativeHawaiianLayoutBinding? = null
    private val binding get() = _binding!!

    private var nativeHawaiianChildList:ArrayList<DemoGraphicRaceDetail>? = null
    private var userName:String? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NativeHawaiianLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        nativeHawaiianChildList = arguments?.getParcelableArrayList(AppConstant.nativeHawaianChildList)!!
        userName = arguments?.getString(AppConstant.govtUserName)
        binding.borrowerPurpose.text = userName

        setUpUI()
        super.addListeners(binding.root)
        return root
    }



    private fun setUpUI() {
        binding.edDetails.setOnFocusChangeListener(CustomFocusListenerForEditText( binding.edDetails , binding.layoutDetail , requireContext()))
        binding.backButton.setOnClickListener {  findNavController().popBackStack() }
        binding.saveBtn.setOnClickListener {
            backToCallingScreen()
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, testCallback )

        // pre selection from the webservice...
        nativeHawaiianChildList?.let { nativeHawaiianChildList ->
            for (item in nativeHawaiianChildList) {

                if (item.name == binding.nativeHawaiianCheckBox.text)
                    binding.nativeHawaiianCheckBox.isChecked = true
                else
                if (item.name == binding.guamanianCheckbox.text)
                    binding.guamanianCheckbox.isChecked = true
                else
                if (item.name == binding.samoanCheckBox.text)
                    binding.samoanCheckBox.isChecked = true
                else
                if (item.name == binding.otherPacificIslanderCheckBox.text) {
                    binding.otherPacificIslanderCheckBox.isChecked = true
                    if (item.isOther == true) {
                        item.otherRace?.let { otherRace ->
                            binding.edDetails.setText(otherRace)
                        }
                    }
                }

            }
        }

        binding.nativeHawaiianCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateTypes(8, binding.nativeHawaiianCheckBox.text.toString(), isChecked , false)
        }
        binding.guamanianCheckbox.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateTypes(9, binding.guamanianCheckbox.text.toString(), isChecked, false)
        }
        binding.samoanCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateTypes(10,binding.samoanCheckBox.text.toString(), isChecked , false)
        }
        binding.otherPacificIslanderCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateTypes(11, binding.otherPacificIslanderCheckBox.text.toString(), isChecked , true, binding.edDetails.text.toString())
        }

    }

    private fun updateTypes(id:Int, fieldName:String, removeFromList:Boolean , isOther: Boolean, otherPlaceHolder: String = "" ){
        nativeHawaiianChildList?.let { nativeHawaiianChildList ->
            if (!removeFromList) {
                for (item in nativeHawaiianChildList) {
                    if (item.detailId == id) {
                        nativeHawaiianChildList.remove(item)
                        break
                    }
                }
            } else
                nativeHawaiianChildList.add(DemoGraphicRaceDetail(detailId = id, name = fieldName, isOther = isOther, otherRace = otherPlaceHolder))
        }
    }

    private val testCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

            findNavController().popBackStack()
        }
    }

    private fun backToCallingScreen(){
        nativeHawaiianChildList?.let { nativeHawaiianChildList ->
            for (item in nativeHawaiianChildList) {
                if(item.detailId == 11){
                    item.otherRace = binding.edDetails.text.toString()
                }
            }
        }
        findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.selectedNativeHawaianChildList, nativeHawaiianChildList)
        findNavController().popBackStack()
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
}