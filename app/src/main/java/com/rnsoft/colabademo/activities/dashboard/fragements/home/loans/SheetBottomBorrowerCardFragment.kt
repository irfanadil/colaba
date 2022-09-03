package com.rnsoft.colabademo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.Nullable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rnsoft.colabademo.databinding.DialogFragmentBorrowerCardSheetBinding

class SheetBottomBorrowerCardFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = SheetBottomBorrowerCardFragment()
    }


    lateinit var binding: DialogFragmentBorrowerCardSheetBinding

    private var borrowerParcelObject:LoanItem? =null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogFragmentBorrowerCardSheetBinding.inflate(inflater, container, false)
        borrowerParcelObject =  arguments?.getParcelable<LoanItem>(AppConstant.borrowerParcelObject)
        borrowerParcelObject?.let {
            binding.borrowerName.text = it.firstName

        }

        binding.crossImageView.setOnClickListener{
            dismiss()
        }

        binding.bottomEmailConstraintLayout.setOnClickListener {
            borrowerParcelObject?.let { loanItem->
                loanItem.email?.let {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "plain/text"
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(it))
                    intent.putExtra(Intent.EXTRA_SUBJECT, "")
                    intent.putExtra(Intent.EXTRA_TEXT, "")
                    startActivity(Intent.createChooser(intent, ""))
                }
            }
        }

        binding.bottomMessageConstraintLayout.setOnClickListener {
            borrowerParcelObject?.let { loanItem->
                loanItem.cellNumber?.let {
                    val smsIntent = Intent(Intent.ACTION_VIEW)
                    smsIntent.type = "vnd.android-dir/mms-sms"
                    smsIntent.putExtra("address", it)
                    //smsIntent.putExtra("sms_body", "Colaba info message")
                    startActivity(smsIntent)
                }
            }
        }

        binding.bottomPhoneConstraintLayout.setOnClickListener {
            borrowerParcelObject?.let { loanItem->
                loanItem.cellNumber?.let {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:$it")
                    startActivity(intent)
                }
            }
        }

        binding.docLayout.setOnClickListener{
            dismiss()
            navigateToBorrowerScreen(AppConstant.borrowerDocScreen)
        }

        binding.appLayout.setOnClickListener{
            dismiss()
            navigateToBorrowerScreen(AppConstant.borrowerAppScreen)
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


    private fun navigateToBorrowerScreen(innerScreenName:String) {
        borrowerParcelObject?.let { loanItem->
            val borrowerDetailIntent = Intent(requireActivity(), DetailActivity::class.java)
            borrowerDetailIntent.putExtra(AppConstant.loanApplicationId, loanItem.loanApplicationId)
            borrowerDetailIntent.putExtra(AppConstant.loanPurpose,  loanItem.loanPurpose)
            borrowerDetailIntent.putExtra(AppConstant.firstName, loanItem.firstName)
            borrowerDetailIntent.putExtra(AppConstant.lastName, loanItem.lastName)
            borrowerDetailIntent.putExtra(AppConstant.bPhoneNumber,  loanItem.cellNumber)
            borrowerDetailIntent.putExtra(AppConstant.bEmail,  loanItem.email)
            borrowerDetailIntent.putExtra(AppConstant.innerScreenName,  innerScreenName)
            startActivity(borrowerDetailIntent)
        }
    }
}