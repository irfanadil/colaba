package com.rnsoft.colabademo

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView


class DocumentListAdapter
internal constructor(
    passedDocsList: ArrayList<SubFiles>, onDocsViewClickListener: DocsViewClickListener
) : RecyclerView.Adapter<DocumentListAdapter.DocInnerListViewHolder>() {

    private var docsList = ArrayList<SubFiles>()
    private var classScopedItemClickListener: DocsViewClickListener = onDocsViewClickListener

    init {
        this.docsList = passedDocsList
        this.classScopedItemClickListener = onDocsViewClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocInnerListViewHolder {
        val holder: DocInnerListViewHolder
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        holder = DocInnerListViewHolder(
            inflater.inflate(
                R.layout.detail_list_inner_view_holder,
                parent,
                false
            )
        )
        return holder
    }

    inner class DocInnerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var docName: TextView = itemView.findViewById(R.id.doc_name)
        var tvDocUploadedTime: TextView = itemView.findViewById(R.id.doc_uploaded_time)
        var docImage: ImageView = itemView.findViewById(R.id.doc_image)
        var fileCardView: CardView = itemView.findViewById(R.id.fileCardView)

        init {
            fileCardView.setOnClickListener {
                if(docName.text.isNotBlank() && docName.text.isNotEmpty())
                    classScopedItemClickListener.navigateTo(adapterPosition , docName = docName.text.toString())
            }
        }

    }

    override fun onBindViewHolder(holder: DocInnerListViewHolder, position: Int) {
        val doc = docsList[position]
        // set doc name
        val docName =
            if (doc.clientName.isEmpty() || doc.clientName.isBlank()) doc.mcuName else doc.clientName
        holder.docName.text = docName
        val docType = getDocType(docName)
        setDocImage(docType, holder.docImage)
        //Log.e("READ", docName + "  " + doc.isRead)
        if ((!doc.isRead)) {
            holder.docName.setTypeface(null, Typeface.BOLD)
        }

        // set doc time
        //var uploadedTime = AppSetting.returnLongTimeNow(doc.fileUploadedOn)
        var newFormattedDateTime = AppSetting.documentDetailDateTimeFormat(doc.fileUploadedOn)
        holder.tvDocUploadedTime.text = newFormattedDateTime

    }

    override fun getItemCount(): Int = docsList.size

    private fun getDocType(docType: String): String {
        val doctype = docType.split(".").toTypedArray()
        val type = doctype[1]
        return type
    }

    private fun setDocImage(docType: String, imageView: ImageView) {
        if (docType.equals(AppConstant.file_format_png, ignoreCase = true)) {
            imageView.setImageResource(R.drawable.ic_png)
        } else if (docType.equals(AppConstant.file_format_pdf, ignoreCase = true)) {
            imageView.setImageResource(R.drawable.ic_pdf)
        } else if (docType.equals(
                AppConstant.file_format_jpg,
                ignoreCase = true
            ) || (docType.equals(
                AppConstant.file_format_jpeg,
                ignoreCase = true
            ))
        ) {
            imageView.setImageResource(R.drawable.ic_jpg)
        }
    }


}