package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.BankruptcyLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class BankruptcyFragment:BaseFragment() {

    private var _binding: BankruptcyLayoutBinding? = null
    private val binding get() = _binding!!
    private  var answerData:BankruptcyAnswerData = BankruptcyAnswerData()
    private var userName:String? = null
    private var whichBorrowerId:Int = 0
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BankruptcyLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpUI()
        super.addListeners(binding.root)
        arguments?.let { arguments->
            answerData = arguments.getParcelable(AppConstant.bankruptcyAnswerData)!!
            userName = arguments.getString(AppConstant.govtUserName)
            whichBorrowerId = arguments.getInt(AppConstant.whichBorrowerId)
        }
        binding.borrowerPurpose.text = userName
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backToGovernmentScreen )

        binding.chapter7.setOnClickListener{
            binding.errorField.visibility = View.INVISIBLE
        }
        binding.chapter11.setOnClickListener{
            binding.errorField.visibility = View.INVISIBLE
        }
        binding.chapter12.setOnClickListener{
            binding.errorField.visibility = View.INVISIBLE
        }
        binding.chapter13.setOnClickListener{
            binding.errorField.visibility = View.INVISIBLE
        }

        fillGlobalData()
        return root
    }

    private val backToGovernmentScreen: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findNavController().popBackStack()
        }
    }

    private fun fillGlobalData(){
        if(answerData.`1`)
            binding.chapter7.isChecked = true
        if(answerData.`2`)
            binding.chapter11.isChecked = true
        if(answerData.`3`)
            binding.chapter12.isChecked = true
        if(answerData.`4`)
            binding.chapter13.isChecked = true

        binding.edDetails.setText(answerData.extraDetail)
    }

    private fun setUpUI() {
        binding.backButton.setOnClickListener{ findNavController().popBackStack() }
        binding.saveBtn.setOnClickListener {
            val selectedValues = returnSelectedValues()
            if(selectedValues.isNotBlank() && selectedValues.isNotEmpty()) {
                answerData.extraDetail = binding.edDetails.text.toString()
                EventBus.getDefault().post(BankruptcyUpdateEvent(detailDescription = selectedValues, bankruptcyAnswerData = answerData , whichBorrowerId = whichBorrowerId ))
                findNavController().popBackStack()
            }
            else
                binding.errorField.visibility = View.VISIBLE

        }
    }

    private fun returnSelectedValues():String{
        var bool = false

        answerData.`1` = false
        answerData.`2` = false
        answerData.`3` = false
        answerData.`4` = false

        var displayedString = ""
        if(binding.chapter7.isChecked) {
            bool = true
            answerData.`1` = true
            displayedString = "Chapter 7,"
        }
        if(binding.chapter11.isChecked) {
            bool = true
            answerData.`2` = true
            displayedString = "$displayedString Chapter 11,"
        }
        if(binding.chapter12.isChecked) {
            bool = true
            answerData.`3` = true
            displayedString = "$displayedString Chapter 12,"
        }
        if(binding.chapter13.isChecked) {
            bool = true
            answerData.`4` = true
            displayedString = "$displayedString Chapter 13"
        }
        return displayedString
    }
}