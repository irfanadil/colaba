package com.rnsoft.colabademo.activities.addresses.info.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.rnsoft.colabademo.R
import com.rnsoft.colabademo.RecyclerviewClickListener
import com.rnsoft.colabademo.activities.addresses.info.model.Dependent
import kotlinx.android.synthetic.main.dependent_input_field.view.*
import java.util.*


/**
 * Created by Anita Kiran on 8/24/2021.
 */
class DependentAdapter (val mContext : Context, private val items: ArrayList<Dependent>, clickListner: RecyclerviewClickListener) :
    RecyclerView.Adapter<DependentAdapter.DataViewHolder>() {

    private var deleteClick: RecyclerviewClickListener = clickListner

    init {
        this.deleteClick = clickListner
    }

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(model: Dependent, position: Int) {

            if(model.age > 0){
                itemView.ed_age.setText(model.age.toString())
                itemView.til_dependent.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(itemView.context,R.color.grey_color_two))
            }
            itemView.til_dependent.setHint(model.dependent)

            itemView.ed_age.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus){
                    itemView.til_dependent.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(itemView.context,R.color.grey_color_two))
                    itemView.til_dependent.setEndIconDrawable(R.drawable.ic_delete_dependent)
                    //itemView.til_dependent.ed_age.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_delete_dependent,0)
                }
                else{
                    itemView.til_dependent.setEndIconDrawable(null)
                    if (itemView.ed_age.text?.length == 0){
                        itemView.til_dependent.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(itemView.context,R.color.grey_color_three))
                    } else {
                        itemView.til_dependent.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(itemView.context,R.color.grey_color_two))
                        itemView.til_dependent.helperText = ""
                        itemView.til_dependent.setBoxStrokeColorStateList(AppCompatResources.getColorStateList(itemView.context, R.color.primary_info_line_color))
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DataViewHolder(
        LayoutInflater.from(parent.context).inflate(
            R.layout.dependent_input_field, parent,
            false
        )
    )

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(items.get(position), position)

        holder.itemView.til_dependent.setEndIconOnClickListener {
            if (holder.itemView.til_dependent.endIconDrawable?.isVisible == true)
                deleteClick.onItemClick(position)
        }


        holder.itemView.ed_age.doAfterTextChanged {
            try {
                var value = holder.itemView.ed_age.text.toString().trim()
                if (value.isNotBlank() && value.isNotEmpty() && value.length > 0) {
                    val age = Integer.parseInt(value)
                    if (age > 0) {
                        items.set(position, Dependent(items.get(position).dependent, age))
                    }
                }
            } catch (e: Exception) {
            }
        }


        /*
      var ordinalWord= ns.toWords(item.toInt(),true)
            val upperString: String =
                ordinalWord.substring(0, 1).uppercase(Locale.getDefault()) + ordinalWord.substring(1)
                    .lowercase(Locale.getDefault())

     */
    }

}