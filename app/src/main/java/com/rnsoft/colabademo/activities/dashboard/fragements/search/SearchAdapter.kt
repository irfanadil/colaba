package com.rnsoft.colabademo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView



class SearchAdapter
internal constructor(
    passedSearchList: ArrayList<SearchItem>, onSearchClickListener: SearchFragment) :  RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var searchList = ArrayList<SearchItem>()
    private var clickListener: SearchClickListener = onSearchClickListener

    init {
        this.searchList = passedSearchList
        this.clickListener = onSearchClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder  {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.search_view_holder, parent, false)
        return SearchViewHolder(view)
    }


    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {

        val tenant:SearchItem  = searchList[position]

        holder.borrowerName.text = tenant.firstName+" "+tenant.lastName
        holder.borrowerId.text = tenant.loanNumber
        holder.borrowerStatus.text = tenant.status

        var tenantAddress = ""
        if( tenant.streetAddress!=null ) tenantAddress += tenant.streetAddress
        if( tenant.unitNumber!=null ) tenantAddress += tenant.unitNumber
        if( tenant.cityName!=null ) tenantAddress += tenant.cityName

        if( tenant.stateAbbreviation!=null  && tenant.stateAbbreviation.isNotEmpty())
            tenantAddress += "\n"+tenant.stateAbbreviation+", "
        if( tenant.countryName!=null ) tenantAddress += tenant.countryName+" "
        if( tenant.zipCode!=null ) tenantAddress += tenant.zipCode
        holder.borrowerAddress.text = tenantAddress

        holder.searchContainer.setOnClickListener {
            clickListener.navigateToBorrowerScreen(position)
        }

        //holder.notificationTime.text = singleNotification.notificationTime
        /*
        val word1 =  SpannableString("documents")
        val word2 =  SpannableString("has submitted");
        val fullMessage = singleNotification.notificationName

        val docsIndex = singleNotification.notificationName?.indexOf("documents",0, ignoreCase = true)

        fullMessage?.let { demoMessage->
            docsIndex?.let {
                //val extractName = SpannableString (demoMessage.substring(0,it-1))
                //extractName.setSpan( ForegroundColorSpan(Color.BLUE), 0, extractName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        word1.setSpan( ForegroundColorSpan(Color.BLUE), 0, word1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        word2.setSpan( ForegroundColorSpan(Color.RED), 0, word2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val gatherString = fullMessage+word1+word2

        holder.notificationName.text = gatherString

         */
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val searchContainer: ConstraintLayout = itemView.findViewById(R.id.search_view_holder_container)
        var borrowerName: TextView = itemView.findViewById(R.id.searchBorrowerName)
        var borrowerAddress: TextView = itemView.findViewById(R.id.searchBorrowerAddress)
        var borrowerId: TextView = itemView.findViewById(R.id.searchBorrowerId)
        var borrowerStatus: TextView = itemView.findViewById(R.id.searchBorrowerStatus)

    }

    fun clearData(){
        searchList.clear()
        notifyDataSetChanged()
    }

    interface SearchClickListener {
        fun onSearchItemClick( view:View)
        fun navigateToBorrowerScreen(position: Int)
    }


}