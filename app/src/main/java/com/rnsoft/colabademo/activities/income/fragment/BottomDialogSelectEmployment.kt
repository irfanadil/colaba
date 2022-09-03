package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rnsoft.colabademo.databinding.DialogAddEmplymentBinding
import org.greenrobot.eventbus.EventBus

/**
 * Created by Anita Kiran on 9/13/2021.
 */
class BottomDialogSelectEmployment : BottomSheetDialogFragment() {

    companion object {
        //lateinit var userMessage:String
        fun newInstance(): BottomDialogSelectEmployment {
            //userMessage    =   message
            return BottomDialogSelectEmployment()
        }
    }

    lateinit var binding: DialogAddEmplymentBinding

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.roundedBottomSheetDialog)

    }

    private fun setInitialSelection(){

        binding.recentIcon.setColorFilter(resources.getColor(R.color.grey_color_two, activity?.theme))
        binding.recentTextView.setTextColor(resources.getColor(R.color.grey_color_two, activity?.theme))

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogAddEmplymentBinding.inflate(inflater, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.roundedBottomSheetDialog)

        binding.crossImageView.setOnClickListener{
            dismiss()
        }
        binding.addCurrentEmployment.setOnClickListener {
            EventBus.getDefault().post(EventAddEmployment(true))
            dismiss()
        }

        binding.addPrevEmplyment.setOnClickListener {
            EventBus.getDefault().post(EventAddEmployment(false))
            dismiss()
        }

        setInitialSelection()

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