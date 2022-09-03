package com.rnsoft.colabademo.activities.addresses.info.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rnsoft.colabademo.R
import com.rnsoft.colabademo.databinding.DialogFragmentDeleteCurrentResidenceBinding
import org.greenrobot.eventbus.EventBus

class DeleteCurrentResidenceDialogFragment : BottomSheetDialogFragment() {

    companion object {
        lateinit var deleteText:String
        fun newInstance(text:String): DeleteCurrentResidenceDialogFragment {
            deleteText = text
            return DeleteCurrentResidenceDialogFragment()
        }
    }
  
    lateinit var binding: DialogFragmentDeleteCurrentResidenceBinding

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.roundedBottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogFragmentDeleteCurrentResidenceBinding.inflate(inflater, container, false)
        binding.crossImageView.setOnClickListener{
            dismiss()
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.roundedBottomSheetDialog)
        binding.tvDeleteText.text= deleteText

        binding.yesBtn.setOnClickListener {
            dismiss()
            EventBus.getDefault().post(SwipeToDeleteEvent(true))
        }

        binding.noBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialog()
    }

    private fun initDialog() {
        requireDialog().window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireDialog().window?.statusBarColor = requireContext().getColor(android.R.color.transparent)
    }

}