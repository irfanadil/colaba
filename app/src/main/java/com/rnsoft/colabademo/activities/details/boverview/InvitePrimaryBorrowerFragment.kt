package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.InvitePrimaryBorrowerLayoutBinding


import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class InvitePrimaryBorrowerFragment : BaseFragment() {

    private var _binding: InvitePrimaryBorrowerLayoutBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    lateinit var rootTestView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = InvitePrimaryBorrowerLayoutBinding.inflate(inflater, container, false)
        rootTestView = binding.root

        (activity as DetailActivity).hideFabIcons()

        //binding.detailTextView.movementMethod = LinkMovementMethod.getInstance()

        binding.backButtonImageView.setOnClickListener{
           findNavController().popBackStack()
        }
        super.addListeners(binding.root)
        return rootTestView
    }


}