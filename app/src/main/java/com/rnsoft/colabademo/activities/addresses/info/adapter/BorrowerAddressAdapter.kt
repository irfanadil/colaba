package com.rnsoft.colabademo

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rnsoft.colabademo.activities.addresses.info.AddressClickListener
import com.rnsoft.colabademo.databinding.ResidenceItemBinding
import java.text.DecimalFormat
import java.util.ArrayList

/**
 * Created by Anita Kiran on 11/2/2021.
 */
class BorrowerAddressAdapter(var context: Context) :
    RecyclerView.Adapter<BorrowerAddressAdapter.AddressViewHolder>() {

    var address: List<PreviousAddresses> = arrayListOf()
    val numberFormatter =  DecimalFormat("#,###,###")
   /* private var clickEvent: AddressClickListener = clickListner

    init {
        this.clickEvent = clickListner
    } */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        var binding = ResidenceItemBinding.inflate(LayoutInflater.from(parent.context), parent,
            false)

        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(address.get(position), position)

        /*holder.itemView.setOnClickListener {
            clickEvent.onAddressClick(position)
        } */

    }

    override fun getItemCount() = address.size

    inner class AddressViewHolder(val binding : ResidenceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(address: PreviousAddresses, position: Int) {

            address.addressModel?.let {
                val builder = StringBuilder()
                it.street?.let { builder.append(it).append(" ") }
                it.unit?.let { builder.append(it).append(",") } ?: run { builder.append(",")}
                it.city?.let { builder.append("\n").append(it).append(",").append(" ") } ?: run {builder.append("\n")}
                it.stateName?.let { builder.append(it).append(" ") }
                it.zipCode?.let { builder.append(it) }
                binding.tvAddress.text = builder
            }

            address.monthlyRent?.let {
                if(it > 0) {
                    val value: String = numberFormatter.format(Math.round(it))
                    binding.tvHomerent.text = "Rental $".plus(value)
                    binding.tvHomerent.visibility = View.VISIBLE
                }
            }

            try{
                address.fromDate?.let {
                    if (it.isNotBlank() && it.length > 0){
                    val fromDate = AppSetting.getMonthAndYearValue(it)

                        address.toDate?.let {
                            if (it.isNotBlank() && it.length > 0) {
                                val toDate = AppSetting.getMonthAndYearValue(it)
                                binding.tvResidenceDate.text =
                                    "From ".plus(fromDate).plus(" to ").plus(toDate)
                            }
                        }
                    }
                }
            } catch (e : Exception){
                Log.e("BorrowerAdapter","Exception Caught")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTaskList(addressList: ArrayList<PreviousAddresses>){
        address = addressList
        notifyDataSetChanged()
    }
}