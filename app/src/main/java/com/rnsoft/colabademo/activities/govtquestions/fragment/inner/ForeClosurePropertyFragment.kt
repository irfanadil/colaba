package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.ForeClosurePropertyLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields



class ForeClosurePropertyFragment: GovtDetailBaseFragment() {

    private var _binding: ForeClosurePropertyLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = ForeClosurePropertyLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        super.addListeners(binding.root)
        arguments?.let {
            questionId = it.getInt(AppConstant.questionId)
            whichBorrowerId = it.getInt(AppConstant.whichBorrowerId)
            updateGovernmentQuestionByBorrowerId = it.getParcelable(AppConstant.addUpdateQuestionsParams)
            userName = it.getString(AppConstant.govtUserName)
        }
        binding.borrowerPurpose.text = userName
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, backToGovernmentScreen )
        fillWithData(binding.edDetails)
        setUpUI()
        return root
    }

    private fun setUpUI() {
        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.saveBtn.setOnClickListener {
            updateGovernmentAndSaveData(binding.edDetails.text.toString())
            //if(checkEmptyFields()) findNavController().popBackStack()
        }
    }

    private fun checkEmptyFields():Boolean{
        var bool = true
        if(binding.edDetails.text?.isEmpty() == true || binding.edDetails.text?.isBlank() == true) {
            CustomMaterialFields.setError(
                binding.layoutDetail,
                "This field is required.",
                requireContext()
            )
            bool = false
        }
        else
            CustomMaterialFields.clearError(binding.layoutDetail, requireContext())

        return bool
    }


}