package com.rnsoft.colabademo

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BorrowerDocumentAdapter
internal constructor(
    passedDocsList: ArrayList<BorrowerDocsModel>, onAdapterClickListener: AdapterClickListener , downloadClickListener:DownloadClickListener
) : RecyclerView.Adapter<BorrowerDocumentAdapter.DocsViewHolder>() {

    private lateinit var context : Context
    lateinit var holder: DocsViewHolder
    private var docsList = ArrayList<BorrowerDocsModel>()
    private var classScopedItemClickListener: AdapterClickListener = onAdapterClickListener

    private var classScopedDownloadClickListener: DownloadClickListener = downloadClickListener

    init {
        this.docsList = passedDocsList
        this.classScopedItemClickListener = onAdapterClickListener
        this.classScopedDownloadClickListener = downloadClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocsViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        this.context = parent.context
        holder = DocsViewHolder(inflater.inflate(R.layout.doc_view_holder_layout, parent, false))
        return holder
    }

    inner class DocsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var containsThreeChild: ConstraintLayout = itemView.findViewById(R.id.containsThreeChild)
        var containsNoChild: ConstraintLayout = itemView.findViewById(R.id.containsNoChild)

        var docType: TextView = itemView.findViewById(R.id.doc_type)
        var tvDocUploadedTime: TextView = itemView.findViewById(R.id.doc_uploaded_time)

        var docOneLayout: ConstraintLayout = itemView.findViewById(R.id.doc_one)
        var docOneName: TextView = itemView.findViewById(R.id.doc_one_name)
        var docOneImage: ImageView = itemView.findViewById(R.id.doc_one_image)

        var docTwoLayout: ConstraintLayout = itemView.findViewById(R.id.doc_two)
        var docTwoName: TextView = itemView.findViewById(R.id.doc_two_name)
        var docTwoImage: ImageView = itemView.findViewById(R.id.doc_two_image)

        var docThreeLayout: ConstraintLayout = itemView.findViewById(R.id.doc_three)
        var docThreeName: TextView = itemView.findViewById(R.id.doc_three_name)

        var docCardView: CardView = itemView.findViewById(R.id.docCardView)

        var docFilter: TextView = itemView.findViewById(R.id.doc_status)
        var imageFilterCirle: ImageView = itemView.findViewById(R.id.image_filter_circle)


        init {
            docCardView.setOnClickListener {
                classScopedItemClickListener.navigateTo(adapterPosition)
            }



        }
    }

    override fun onBindViewHolder(holder: DocsViewHolder, position: Int) {
        val doc = docsList[position]

        // set Doc name
        holder.docType.text = doc.docName
        doc.subFiles?.let { subFiles ->
            if (doc.subFiles.size > 0) {
                for (file in doc.subFiles) {
                    //Log.e("READ", doc.docName + "  " + file.isRead)
                    if ((!file.isRead)) {
                        holder.docType.setTypeface(null, Typeface.BOLD)
                    }
                }
            }
        }
        /*doc.createdOn.let { activityTime ->
            //val newString = AppSetting.returnLongTimeNow(activityTime!!)
            //holder.docUploadedTime.text = newString
        } */
        //Log.e("fileUploadedOn", fileUploadOn)


        // set filter
        holder.docFilter.text = doc.status
        setDocFilterColor(doc.status!!)

        doc.subFiles?.let { subFiles ->
            if (doc.subFiles.isEmpty()) {
                holder.containsNoChild.visibility = View.VISIBLE
                holder.containsThreeChild.visibility = View.GONE
                holder.tvDocUploadedTime.visibility = View.GONE
            } else
                if (doc.subFiles.isNotEmpty()) {
                    holder.containsThreeChild.visibility = View.VISIBLE
                    holder.containsNoChild.visibility = View.GONE
                    holder.tvDocUploadedTime.visibility = View.VISIBLE

                    val fileOne = doc.subFiles[0]
                    if (fileOne.clientName.isNotEmpty() && fileOne.clientName.isNotBlank()) {
                        holder.docOneName.text = fileOne.clientName
                        holder.docOneImage.visibility = View.VISIBLE

                        val docType = getDocType(fileOne.clientName)
                        setDocImage(docType, holder.docOneImage)

                        holder.docOneLayout.setOnClickListener {
                            classScopedDownloadClickListener.fileClicked(
                                fileOne.clientName,
                                fileOne.id,
                                position
                            )
                        }


                    } else {
                        holder.docOneName.text = fileOne.mcuName
                        holder.docOneImage.visibility = View.VISIBLE
                        val docType = getDocType(fileOne.mcuName)
                        setDocImage(docType, holder.docOneImage)
                    }

                    var fileTwo: SubFiles? = null
                    if (doc.subFiles.size > 1)
                        fileTwo = doc.subFiles[1]

                    if (fileTwo != null) {
                        if (fileTwo.clientName.isNotEmpty() && fileTwo.clientName.isNotBlank()) {
                            holder.docTwoLayout.visibility = View.VISIBLE
                            holder.docTwoName.text = fileTwo.clientName
                            holder.docTwoImage.visibility = View.VISIBLE
                            val docType = getDocType(fileTwo.clientName)
                            setDocImage(docType, holder.docTwoImage)
                            holder.docTwoLayout.setOnClickListener {
                                classScopedDownloadClickListener.fileClicked(
                                    fileTwo.clientName,
                                    fileTwo.id,
                                    position
                                )
                            }
                        } else {
                            holder.docTwoLayout.visibility = View.INVISIBLE
                        }
                    }

                    if (doc.subFiles.size > 2) {
                        holder.docThreeLayout.visibility = View.VISIBLE
                        holder.docThreeName.text = "+" + (doc.subFiles.size.minus(2)).toString()
                    } else
                        holder.docThreeLayout.visibility = View.INVISIBLE

                    // set time
                    var fileUploadOn = doc.subFiles[0].fileUploadedOn
                    var time = AppSetting.getDocumentUploadedDate(fileUploadOn)
                    holder.tvDocUploadedTime.text = time

                }
        }

    }
    private fun setDocFilterColor(filter: String) {
        if (filter.equals(AppConstant.filter_inDraft, ignoreCase = true)) {
            holder.imageFilterCirle.setColorFilter(ContextCompat.getColor(context, R.color.filter_blue));
        }
        else if (filter.equals(AppConstant.filter_borrower_todo, ignoreCase = true)) {
            holder.imageFilterCirle.setColorFilter(ContextCompat.getColor(context, R.color.colaba_red_color));
        }
        else if (filter.equals(AppConstant.filter_started, ignoreCase = true)) {
            holder.imageFilterCirle.setColorFilter(ContextCompat.getColor(context, R.color.filter_yellow));
        }
        else if (filter.equals(AppConstant.filter_pending, ignoreCase = true) ||
            filter.equals(AppConstant.filter_pending_review, ignoreCase = true)) {
            holder.imageFilterCirle.setColorFilter(ContextCompat.getColor(context, R.color.colaba_apptheme_blue));
        }
        else if (filter.equals(AppConstant.filter_completed,ignoreCase = true)) {
            holder.imageFilterCirle.setColorFilter(ContextCompat.getColor(context, R.color.filter_green));
        }
        else if (filter.equals(AppConstant.filter_manuallyAdded, ignoreCase = true)) {
            holder.imageFilterCirle.setColorFilter(ContextCompat.getColor(context, R.color.filter_grey))
        }
    }

    private fun getDocType(docType: String): String {
        val doctype = docType.split(".").toTypedArray()
        val type = doctype[1]
        return type
    }

    private fun setDocImage(docType: String, imageView: ImageView) {
        if (docType.equals(AppConstant.file_format_png,ignoreCase = true)) {
            imageView.setImageResource(R.drawable.ic_png)
        } else if (docType.equals(AppConstant.file_format_pdf,ignoreCase = true)) {
            imageView.setImageResource(R.drawable.ic_pdf)
        } else if (docType.equals(AppConstant.file_format_jpg,ignoreCase = true) || (docType.equals(AppConstant.file_format_jpeg,ignoreCase = true))) {
            imageView.setImageResource(R.drawable.ic_jpg)
        }
    }

    override fun getItemCount(): Int = docsList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun returnDocCreatedTime(input: String): String {
        var lastSeen = input
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        val oldDate: Date? = formatter.parse(input)
        val oldMillis = oldDate?.time

        oldMillis?.let {
            lastSeen = AppSetting.lastseen(oldMillis)
        }
        return lastSeen
    }
}