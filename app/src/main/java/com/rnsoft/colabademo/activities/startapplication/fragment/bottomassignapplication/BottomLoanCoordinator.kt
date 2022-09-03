package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.rnsoft.colabademo.databinding.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class BottomLoanCoordinator : BottomBorrowerBaseFragment() {

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
            val frag: AssignBorrowerBottomDialogFragment? =
                this.parentFragment as AssignBorrowerBottomDialogFragment?
            Timber.e(" what is frag $frag")

            val frag2: BottomBorrowerBaseFragment? =  this.parentFragment as BottomBorrowerBaseFragment?
            Timber.e(" what is frag2 $frag2")


            val frag3: BaseFragment? =  this.parentFragment as BaseFragment?
            Timber.e(" what is frag3 $frag3")

            val whatFrag = this.parentFragment
            Timber.e(" whatFrag  -  $whatFrag")

            EventBus.getDefault().post(OnDismissBottomDialogEvent())
            //frag?.navigateToAssignBorrowerScreen()
        }
    }

    private fun setUpTabs() {}
}