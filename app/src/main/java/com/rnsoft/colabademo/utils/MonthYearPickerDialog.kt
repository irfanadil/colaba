package com.rnsoft.colabademo.utils

import androidx.fragment.app.DialogFragment
import android.content.DialogInterface
import android.widget.NumberPicker
import android.os.Bundle
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.rnsoft.colabademo.R
import java.util.*


class MonthYearPickerDialog: DialogFragment() {

    //private val MAX_YEAR = 2099
    private val MIN_YEAR = 1950
    private var listener: OnDateSetListener? = null

    fun setListener(listener: OnDateSetListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        // Get the layout inflater
        val inflater = requireActivity().layoutInflater
        val cal: Calendar = Calendar.getInstance()
        val dialog: View = inflater.inflate(R.layout.month_year_layout, null)
        val monthPicker = dialog.findViewById(R.id.picker_month) as NumberPicker
        val yearPicker = dialog.findViewById(R.id.picker_year) as NumberPicker
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = cal.get(Calendar.MONTH)
        val year: Int = cal.get(Calendar.YEAR)
        yearPicker.minValue = MIN_YEAR
        yearPicker.maxValue = year
        yearPicker.value = year
        builder.setView(dialog) // Add action buttons
            .setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, id ->
                    listener!!.onDateSet(
                        null,
                        yearPicker.value,
                        monthPicker.value,
                        0
                    )
                })
            .setNegativeButton(R.string.cancel,
                DialogInterface.OnClickListener { dialog, id -> this@MonthYearPickerDialog.dialog!!.cancel() })
        return builder.create()
    }

}