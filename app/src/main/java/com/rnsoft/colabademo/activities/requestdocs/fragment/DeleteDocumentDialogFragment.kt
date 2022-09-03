package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rnsoft.colabademo.databinding.DialogFragmentDeleteDocumentBinding
import org.greenrobot.eventbus.EventBus

/**
 * Created by Anita Kiran on 10/4/2021.
 */
class DeleteDocumentDialogFragment : BottomSheetDialogFragment() {

    companion object {
        lateinit var deleteText:String
        fun newInstance(text:String): DeleteDocumentDialogFragment {
            deleteText = text
            return DeleteDocumentDialogFragment()
        }
    }

    lateinit var binding: DialogFragmentDeleteDocumentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogFragmentDeleteDocumentBinding.inflate(inflater, container, false)
        binding.crossImageView.setOnClickListener{
            dismiss()
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.roundedBottomSheetDialog)
        binding.tvDeleteDocName.text= deleteText

        binding.yesBtn.setOnClickListener {
            EventBus.getDefault().post(DeleteCurrentDocumentEvent(true))
            dismiss()

        }

        binding.noBtn.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.roundedBottomSheetDialog)
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

class DeleteCurrentDocumentEvent(val boolean: Boolean)