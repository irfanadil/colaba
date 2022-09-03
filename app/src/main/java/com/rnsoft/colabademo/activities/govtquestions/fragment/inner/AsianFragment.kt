package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.AsianLayoutBinding
import com.rnsoft.colabademo.databinding.FederalDeptLayoutBinding
import com.rnsoft.colabademo.databinding.PriorityLiensLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AsianFragment:BaseFragment() {

    private var _binding: AsianLayoutBinding? = null
    private val binding get() = _binding!!

    private var asianChildList:ArrayList<DemoGraphicRaceDetail>? = null
    private var userName:String? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = AsianLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        asianChildList = arguments?.getParcelableArrayList(AppConstant.asianChildList)!!
        userName = arguments?.getString(AppConstant.govtUserName)
        binding.borrowerPurpose.text = userName

        setUpUI()
        super.addListeners(binding.root)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, testCallback )
        return root
    }

    private val testCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

            findNavController().popBackStack()
        }
    }

    private fun saveChildList(){
        asianChildList?.let { asianChildList ->
            for (item in asianChildList) {
                if(item.detailId == 7){
                    item.otherRace = binding.edDetails.text.toString()
                }
            }
        }
        findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.selectedAsianChildList, asianChildList)
        findNavController().popBackStack()
    }

    private fun setUpUI() {
        binding.edDetails.onFocusChangeListener = CustomFocusListenerForEditText( binding.edDetails , binding.layoutDetail , requireContext())
        binding.backButton.setOnClickListener {

            findNavController().popBackStack()
        }
        binding.saveBtn.setOnClickListener { saveChildList() }


        // pre selection from the webservice...
        asianChildList?.let { asianChildList->

            for (item in asianChildList) {
                if (item.name == binding.asianIndianCheckBox.text)
                    binding.asianIndianCheckBox.isChecked = true
                else
                if (item.name == binding.chineseCheckBox.text)
                    binding.chineseCheckBox.isChecked = true
                else
                if (item.name == binding.filipinoCheckBox.text)
                    binding.filipinoCheckBox.isChecked = true
                else
                if (item.name == binding.japaneseCheckBox.text)
                    binding.japaneseCheckBox.isChecked = true
                else
                if (item.name == binding.koreanCheckBox.text)
                    binding.koreanCheckBox.isChecked = true
                else
                if (item.name == binding.vietnameseCheckBox.text)
                    binding.vietnameseCheckBox.isChecked = true
                else
                if (item.name == binding.otherAsianCheckBox.text) {
                binding.otherAsianCheckBox.isChecked = true
                    if (item.isOther == true) {
                        item.otherRace?.let { otherRace ->
                        binding.edDetails.setText(otherRace)
                        }
                    }
                }
            }
        }


        binding.asianIndianCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateAsianTypes(1, binding.asianIndianCheckBox.text.toString(), isChecked, false )
        }
        binding.chineseCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateAsianTypes(2, binding.chineseCheckBox.text.toString(), isChecked, false )
        }
        binding.filipinoCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateAsianTypes(3,binding.filipinoCheckBox.text.toString(), isChecked, false )
        }
        binding.japaneseCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateAsianTypes(4, binding.japaneseCheckBox.text.toString(), isChecked, false )
        }
        binding.koreanCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateAsianTypes(5,binding.koreanCheckBox.text.toString(), isChecked, false )
        }
        binding.vietnameseCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateAsianTypes(6,binding.vietnameseCheckBox.text.toString(), isChecked , false )
        }
        binding.otherAsianCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            updateAsianTypes(7, binding.otherAsianCheckBox.text.toString(), isChecked , true, binding.edDetails.text.toString())
        }
    }

    private fun updateAsianTypes(id:Int, fieldName:String, removeFromList:Boolean, isOther: Boolean, otherPlaceHolder: String = "" ){
        asianChildList?.let { asianChildList ->
            if (!removeFromList) {
                for (item in asianChildList) {
                    if (item.detailId == id) {
                        asianChildList.remove(item)
                        break
                    }
                }
            } else
                asianChildList.add(DemoGraphicRaceDetail(detailId = id, name = fieldName, isOther = isOther, otherRace = otherPlaceHolder))
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
}