package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import com.rnsoft.colabademo.databinding.DocRequestSentLayoutBinding

/**
 * Created by Anita Kiran on 10/6/2021.
 */
class RequestSentFragment : BaseFragment() {
     private lateinit var binding : DocRequestSentLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DocRequestSentLayoutBinding.inflate(inflater, container, false)

        setupUI()

        val activity = (activity as? RequestDocsActivity)
        activity?.fullName?.let {
            binding.emailBorrowerName.setText(it)
        }

        return binding.root

    }

    private fun setupUI(){
        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }

        binding.btnBackToDoc.setOnClickListener {
            requireActivity().finish()
            requireActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_left)
        }

    }
}