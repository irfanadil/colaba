package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.Nullable
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rnsoft.colabademo.databinding.DialogStandardChecklistBinding
import kotlinx.android.synthetic.main.docs_type_bottom_value_cell.view.*

/**
 * Created by Anita Kiran on 10/4/2021.
 */
class StandardChecklistDialogFragment : BottomSheetDialogFragment() {

    companion object {
        lateinit var dialogTitle:String
        lateinit var dialogValues:ArrayList<Doc>
        fun newInstance(dTitle:String, dValues: ArrayList<Doc>): StandardChecklistDialogFragment {
            dialogTitle = dTitle
            dialogValues = dValues
            return StandardChecklistDialogFragment()
        }
    }

    lateinit var binding: DialogStandardChecklistBinding

    private fun setInitialSelection(){

        binding.dialogTitle.text = dialogTitle

        for(dialogValue in dialogValues){
            val valueCell: LinearLayoutCompat =
                layoutInflater.inflate(R.layout.docs_type_bottom_value_cell, null) as LinearLayoutCompat
            valueCell.textValue.text = dialogValue.docType
            binding.dialogValueContainer.addView(valueCell)
        }

        //binding.recentIcon.setColorFilter(resources.getColor(R.color.grey_color_two, activity?.theme))
        //binding.recentTextView.setTextColor(resources.getColor(R.color.grey_color_two, activity?.theme))

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogStandardChecklistBinding.inflate(inflater, container, false)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.roundedBottomSheetDialog)

        binding.crossImageView.setOnClickListener{
            dismiss()
        }

        setInitialSelection()

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