package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.CustomDocLayoutBinding

class CreateCustomDocumentFragment : DocsTypesBaseFragment() {

    private lateinit var binding : CustomDocLayoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = CustomDocLayoutBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI(){

        binding.etDocName.onFocusChangeListener = CustomFocusListenerForEditText(binding.etDocName, binding.layoutDocName, requireContext())
        binding.etMsg.onFocusChangeListener = CustomFocusListenerForEditText(binding.etMsg, binding.msgLayout, requireContext())


        binding.btnNext.setOnClickListener {
            val docName = binding.etDocName.text.toString()
            val docMessage = binding.etMsg.text.toString()
            if(docName.isNotEmpty() && docName.isNotBlank() && docMessage.isNotBlank() && docMessage.isNotEmpty()){
                combineDocList.add(Doc(docTypeId = null, docType = docName, docMessage = docMessage))
                findNavController().navigate(R.id.action_selected_doc_fragment)
            }
            else {

            }

        }
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}