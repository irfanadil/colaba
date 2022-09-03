package com.rnsoft.colabademo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rnsoft.colabademo.activities.details.bapplication.RealEstateClickListener

class RealStateAdapter internal constructor(private var realStateDataList: ArrayList<RealStateOwn>,realEstateClickListener: RealEstateClickListener) :
    RecyclerView.Adapter<RealStateAdapter.BaseViewHolder>(){

    private var onRealEstateClick: RealEstateClickListener = realEstateClickListener

    init {
        this.onRealEstateClick = realEstateClickListener
    }

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { abstract fun bind(
        item: RealStateOwn
    ) }


    inner class RealStateViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private var propertyAddress: TextView = itemView.findViewById(R.id.propertyAddress)
        private var propertyType: TextView = itemView.findViewById(R.id.propertyType)
        override fun bind(item: RealStateOwn) {
          item.propertyInfoId.toString()
            item.realStateAddress?.let {
                //propertyAddress.text = it.street+" "+it.unit+"\n"+it.city+" "+it.stateName+" "+it.zipCode+" "+it.countryName
                val builder = StringBuilder()
                it.street?.let { builder.append(it).append(" ") }
                it.unit?.let { builder.append(it).append(",") } ?: run { builder.append(",")}
                it.city?.let {
                    if(it.length >0 )
                        builder.append("\n").append(it).append(",").append(" ")
                    else
                        builder.append("\n")
                } ?: run {builder.append("\n")}

                it.stateName?.let { builder.append(it).append(" ") }
                it.zipCode?.let { builder.append(it) }
                propertyAddress.text = builder
            }
            propertyType.text = item.propertyTypeName

            itemView.setOnClickListener {
                onRealEstateClick.onRealEstateClick(position)
            }
        }

    }

    inner class RealStateFooterViewHolder(itemView: View) : BaseViewHolder(itemView) {
        override fun bind(item: RealStateOwn) {
            itemView.setOnClickListener {
                onRealEstateClick.onRealEstateClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder: BaseViewHolder?
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        holder = if(viewType == R.layout.realstate_footer_horizontal)
            RealStateFooterViewHolder(inflater.inflate(R.layout.realstate_footer_horizontal, parent, false))
        else
            RealStateViewHolder(inflater.inflate(R.layout.realstate_horizontal, parent, false))
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return if(realStateDataList[position].isFooter)
            R.layout.realstate_footer_horizontal
        else
            R.layout.realstate_horizontal
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int){ holder.bind(realStateDataList[position]) }

    override fun getItemCount() = realStateDataList.size







}