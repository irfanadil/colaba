package com.rnsoft.colabademo

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.AppHeaderWithBackNavBinding
import com.rnsoft.colabademo.databinding.LetterOndemandBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import com.rnsoft.colabademo.utils.NumberTextFormat
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

/**
 * Created by Anita Kiran on 12/14/2021.
 */

@AndroidEntryPoint
class LetterOnDemandFragment : BaseFragment(){

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: LetterOndemandBinding
    private lateinit var bindingToolbar: AppHeaderWithBackNavBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LetterOndemandBinding.inflate(inflater, container, false)
        bindingToolbar = binding.headerLoanPurchase
        super.addListeners(binding.root)

        // set Header title
        bindingToolbar.headerTitle.setText(getString(R.string.set_letter_ondemand))

        initViews()

        return binding.root
    }

    private fun initViews(){

        // add prefix
        CustomMaterialFields.setDollarPrefix(binding.layoutMaxLoanAmount,requireContext())
        CustomMaterialFields.setDollarPrefix(binding.layoutMaxDownPayment,requireContext())

        // number formats
        binding.etDownPayment.addTextChangedListener(NumberTextFormat(binding.etDownPayment))
        binding.etMaxLoanAmount.addTextChangedListener(NumberTextFormat(binding.etMaxLoanAmount))

        binding.etDownPayment.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.etDownPayment, binding.layoutMaxDownPayment, requireContext()))
        binding.etMaxLoanAmount.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.etMaxLoanAmount, binding.layoutMaxLoanAmount, requireContext()))
        CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.etExpiryDate, binding.layoutExpiryDate)

        binding.etExpiryDate.showSoftInputOnFocus = false

        binding.etExpiryDate.setOnClickListener {
            openCalendar()
        }

        bindingToolbar.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.saveLetterOnDemand.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.letterOndemandLayout.setOnClickListener {
            HideSoftkeyboard.hide(requireActivity(),binding.letterOndemandLayout)
            super.removeFocusFromAllFields(binding.letterOndemandLayout)
        }

    }

    private fun openCalendar(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(), R.style.MySpinnerDatePickerStyle, {
                    view, selectedYear, monthOfYear, dayOfMonth -> binding.etExpiryDate.setText("" + (monthOfYear+1) + "/" + dayOfMonth + "/" + selectedYear) },
            year, month, day
        )
        //datePickerDialog.datePicker.maxDate= System.currentTimeMillis()
        datePickerDialog.show()
    }

}