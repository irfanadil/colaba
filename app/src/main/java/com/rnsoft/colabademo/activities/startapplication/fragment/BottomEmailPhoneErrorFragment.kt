package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rnsoft.colabademo.databinding.BottomPhoneEmailErrorLayoutBinding
import org.greenrobot.eventbus.EventBus

class BottomEmailPhoneErrorFragment : BottomSheetDialogFragment() {

    lateinit var binding: BottomPhoneEmailErrorLayoutBinding

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.roundedBottomSheetDialog)
    }

    companion object {
        lateinit var email:String
        lateinit var phone:String
        lateinit var borrowerName:String
        fun newInstance(emailParam:String,phoneParam:String?=null,borrowerNameParam:String) :BottomEmailPhoneErrorFragment{
            email = emailParam
            phone = phoneParam ?: ""
            borrowerName = borrowerNameParam
            return BottomEmailPhoneErrorFragment()
        }
    }

    private fun setInitialSelection(){
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomPhoneEmailErrorLayoutBinding.inflate(inflater, container, false)
        binding.crossImageView.setOnClickListener{
            dismiss()
        }

        binding.borrowerName.text = borrowerName
        binding.emailTextView.text = email
        binding.phoneTextView.text = phone


        binding.yesBtn.setOnClickListener{
            EventBus.getDefault().post(AllowDuplicateBorrowerEvent())
            dismiss()
        }
        binding.noBtn.setOnClickListener{
            dismiss()
        }
        setStyle(DialogFragment.STYLE_NORMAL, R.style.roundedBottomSheetDialog)

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