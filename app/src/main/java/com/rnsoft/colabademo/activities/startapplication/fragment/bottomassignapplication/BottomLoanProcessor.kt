package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.rnsoft.colabademo.databinding.*
import org.greenrobot.eventbus.EventBus

class BottomLoanProcessor : BottomBorrowerBaseFragment() {

    private lateinit var binding: BottomBorrowerCommonLayoutBinding


    private lateinit var test: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomBorrowerCommonLayoutBinding.inflate(inflater, container, false)
        setupLayout()

        super.addListeners(binding.root)
        return binding.root
    }


    private fun setupLayout() {
        setUpTabs()
        binding.fourthLoImage.setOnClickListener {
            EventBus.getDefault().post(OnDismissBottomDialogEvent())
        }
    }

    private fun setUpTabs() {}
}