package com.rnsoft.colabademo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.MixedUsePropertyBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Anita Kiran on 9/8/2021.
 */
@AndroidEntryPoint
class MixedUsePropertyFragment : BaseFragment() {

    private lateinit var binding : MixedUsePropertyBinding
    var mixedPropertyDesc : String? =""
    private val viewModel : SubjectPropertyViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MixedUsePropertyBinding.inflate(inflater, container, false)


        mixedPropertyDesc = arguments?.getString(AppConstant.MIXED_USE_PROPERTY_DESC)
        mixedPropertyDesc?.let {
            binding.editTextDesc.setText(it)
        }


        binding.backButton.setOnClickListener {
            //  findNavController().popBackStack(R.id.action_back_from_mixedproperty_toPurchase,false)
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            val details: String = binding.editTextDesc.text.toString().trim()
            if (details.isEmpty() || details.length == 0) {
                CustomMaterialFields.setError(binding.layoutDetail,getString(R.string.error_field_required),requireActivity())
            } else{
                //viewModel.updateMixUsePropertyDesc(details)
                //viewModel.updateMixUsePropertyRefinance(details)
                findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.mixedPropertyDetails, details)
                findNavController().popBackStack()
            }
        }
        binding.editTextDesc.doAfterTextChanged {
            it.let {
                CustomMaterialFields.clearError(binding.layoutDetail,requireActivity())
            }
        }
        super.addListeners(binding.root)
        return binding.root
    }
}