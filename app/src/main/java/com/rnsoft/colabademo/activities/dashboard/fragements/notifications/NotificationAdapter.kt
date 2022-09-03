package com.rnsoft.colabademo

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zerobranch.layout.SwipeLayout
import com.zerobranch.layout.SwipeLayout.SwipeActionsListener


class NotificationAdapter
internal constructor(
    passedList: List<NotificationModel>, onNotificationClickListener: NotificationClickListener
) :  RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {


    //: RecyclerView.Adapter<LoansAdapter.LoanViewHolder>() {

    private var notificationList = listOf<NotificationModel>()
    private var clickListener: NotificationClickListener = onNotificationClickListener

    private var swipeLayout: SwipeLayout? = null

    init {
        this.notificationList = passedList
        this.clickListener = onNotificationClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder  {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.notification_view_holder, parent, false)

        return NotificationViewHolder(view)
    }


    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        //holder.customer_name_textfield.text = arrayList[position]
        val singleNotification  = notificationList[position]
       //
        holder.notificationTime.text = singleNotification.notificationTime

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
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var notificationName: TextView = itemView.findViewById(R.id.notification_name)
        var notificationTime: TextView = itemView.findViewById(R.id.notification_time)
        var activeImage: ImageView = itemView.findViewById(R.id.circle_icon)





        //fun bind(item: ConstraintLayout, listener: OnItemClickListener) {
            //itemView.setOnClickListener { listener.onItemClick(item) }
       // }



    }








}