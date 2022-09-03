package com.rnsoft.colabademo.activities.startapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.sourceInformationMarkerEnd
import androidx.recyclerview.widget.RecyclerView
import com.rnsoft.colabademo.RecyclerviewClickListener
import com.rnsoft.colabademo.SearchResultResponseItem
import com.rnsoft.colabademo.databinding.ContactListItemBinding
import java.util.HashMap

class ContactsAdapter(var context: Context,clickListner: RecyclerviewClickListener) :    //, var contact: List<Contacts> = arrayListOf()
    RecyclerView.Adapter<ContactsAdapter.EpisodeViewHolder>() {

    private var searchResultResponseItemList: List<SearchResultResponseItem> = arrayListOf()
    private var clickEvent: RecyclerviewClickListener = clickListner

    private var searchKeyword = "richard"

    init {
        this.clickEvent = clickListner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        var binding = ContactListItemBinding.inflate(LayoutInflater.from(parent.context), parent,
            false)


        return EpisodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.bind(searchResultResponseItemList.get(position), position)

        holder.itemView.setOnClickListener {
            clickEvent.onItemClick(position)
        }

    }

    override fun getItemCount() = searchResultResponseItemList.size

    inner class EpisodeViewHolder(val binding :ContactListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: SearchResultResponseItem, position: Int) {


            binding.contactName.text = (contact.firstName+" "+contact.lastName)
            binding.contactNum.text = (contact.mobileNumber)
            binding.contactEmail.text = (contact.emailAddress)


            var sentence = binding.contactName.text.toString()
            var startIndex = sentence.indexOf(searchKeyword, 0, true)
            var endIndex = startIndex + searchKeyword.length
            var str = SpannableStringBuilder(sentence)
            if (startIndex >= 0) {
                str.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            binding.contactName.text = str


            sentence = binding.contactNum.text.toString()
            startIndex = sentence.indexOf(searchKeyword, 0, true)
            endIndex = startIndex + searchKeyword.length
            str = SpannableStringBuilder(sentence)
            if (startIndex >= 0) {
                str.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            binding.contactNum.text = str



            sentence = binding.contactEmail.text.toString()
            startIndex = sentence.indexOf(searchKeyword, 0, true)
            endIndex = startIndex + searchKeyword.length
            str = SpannableStringBuilder(sentence)
            if (startIndex >= 0) {
                str.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            binding.contactEmail.text = str

        }

    }

    private fun boldSearchKeyword(sentence:String):String{
        if(sentence.isNullOrBlank() || sentence.isNullOrEmpty())
            return sentence
         else
        {
            val startIndex = sentence.indexOf(searchKeyword, 0, true)
            val endIndex = startIndex + searchKeyword.length
            val str = SpannableStringBuilder(sentence)
            if (startIndex >= 0) {
                str.setSpan(
                    StyleSpan(Typeface.BOLD),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            return str.toString()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun showResult(contact: ArrayList<SearchResultResponseItem> , searchKey:String) {
        searchKeyword = searchKey
        this.searchResultResponseItemList = contact
        notifyDataSetChanged()
    }
}