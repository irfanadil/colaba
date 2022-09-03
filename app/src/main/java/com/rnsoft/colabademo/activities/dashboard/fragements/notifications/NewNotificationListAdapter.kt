package com.ecommerce.testapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.rnsoft.colabademo.AppSetting
import com.rnsoft.colabademo.NotificationClickListener
import com.rnsoft.colabademo.NotificationItem
import com.rnsoft.colabademo.R

const val totalItemsToBeDisplayed = 5
const val leftRightPadding = totalItemsToBeDisplayed * 6


class NewNotificationListAdapter internal constructor(
    private var passedList: ArrayList<NotificationItem>,
    private var notificationClickListener: NotificationClickListener
) :  RecyclerView.Adapter<NewNotificationListAdapter.BaseViewHolder>(){



    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { abstract fun bind(
        item: NotificationItem
    ) }

    inner class ContentViewHolder(itemView: View, localClickListener: NotificationClickListener) : BaseViewHolder(
        itemView
    ), View.OnClickListener {

        var notificationName: TextView = itemView.findViewById(R.id.notification_name)
        var notificationTime: TextView = itemView.findViewById(R.id.notification_time)
        var activeCircleIcon: ImageView = itemView.findViewById(R.id.circle_icon)
        var activeBookIcon: ImageView = itemView.findViewById(R.id.activeBookIcon)
        var nonActiveBookIcon: ImageView = itemView.findViewById(R.id.nonActiveBookIcon)
        //var drag_item: FrameLayout = itemView.findViewById(R.id.drag_item)
        //var viewBackground = itemView.findViewById<RelativeLayout>(R.id.view_background)
        var viewForeground = itemView.findViewById<ConstraintLayout>(R.id.view_foreground)

        private var contentClickListener: NotificationClickListener

        init {
            itemView.setOnClickListener(this)
            this.contentClickListener = localClickListener
        }

        override fun onClick(v: View) {
            //Log.e("onClick - ", v.toString())
            val notificationType = passedList[adapterPosition]
            notificationType.status = "Read"
            activeBookIcon.visibility = View.INVISIBLE
            activeCircleIcon.visibility = View.INVISIBLE
            nonActiveBookIcon.visibility = View.VISIBLE

            contentClickListener.onNotificationRead(adapterPosition)
        }

        override fun bind(item: NotificationItem) {
            item.payload?.let{ payload->
                payload.payloadData?.let {  payloadData->
                    payloadData.name?.let {
                        notificationName.text = it
                    }
                    payloadData.dateTime?.let { activityTime->
                        var newTime = AppSetting.getDocumentUploadedDate(activityTime)
                        notificationTime.text =  newTime
                    }

                    var tenantAddress = ""
                    if( payloadData.address!=null ) tenantAddress += payloadData.address
                    if( payloadData.unitNumber!=null ) tenantAddress += payloadData.unitNumber
                    if( payloadData.city!=null ) tenantAddress += payloadData.city

                    if( payloadData.state!=null  && payloadData.state.isNotEmpty())
                        tenantAddress += "\n"+payloadData.state+", "
                    //if( payloadData.countryName!=null ) tenantAddress += payloadData.countryName+" "
                    if( payloadData.zipCode!=null ) tenantAddress += payloadData.zipCode

                }
            }

            if(item.status.equals( "Read", true)) {

                activeBookIcon.visibility = View.INVISIBLE
                activeCircleIcon.visibility = View.INVISIBLE
                nonActiveBookIcon.visibility = View.VISIBLE

            }
            else {
                activeBookIcon.visibility = View.VISIBLE
                activeCircleIcon.visibility = View.VISIBLE
                nonActiveBookIcon.visibility = View.INVISIBLE

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder: BaseViewHolder?
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        holder = ContentViewHolder(
            inflater.inflate(R.layout.notification_view_holder, parent, false),
            notificationClickListener
        )
        return holder
    }



    override fun getItemViewType(position: Int): Int {
        val notificationType = passedList[position]
        //return if(notificationType.isContent)
           return R.layout.notification_view_holder
        //else
          //  R.layout.notification_header_view_holder

    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int)          {
        holder.bind(passedList[position])
        /*(holder as ContentViewHolder).drag_item.setOnLongClickListener(OnLongClickListener {
            showMenu(position)
            true
        })

        if(holder is  ContentViewHolder){
            //Set Menu Actions like:
            //((MenuViewHolder)holder).edit.setOnClickListener(null);
        } */
    }


    override fun getItemCount() = passedList.size

    fun showMenu(position: Int) {
        for (i in 0 until passedList.size) {
            passedList[i].isShowMenu = false
        }
        passedList[position].isShowMenu = true
        notifyItemRemoved(position)
        passedList.removeAt(position)
    }

    fun isMenuShown(): Boolean {
        for (i in 0 until passedList.size) {
            if (passedList[i].isShowMenu) {
                return true
            }
        }
        return false
    }

    fun closeMenu() {
        for (i in 0 until passedList.size) {
            passedList[i].isShowMenu = false
        }
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        passedList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: NotificationItem, position: Int) {
        passedList.add(position, item)
        notifyItemInserted(position)
    }


    /*


    Irfan's time conversion
     /*var newString = activityTime.substring( 0 , activityTime.length-5)
                        newString+="Z"
                        Log.e("newString-",newString)
                        val newTime = AppSetting.returnNotificationTime(newString)
                        Log.e("newTime-",newTime)
                        */

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder: BaseViewHolder?
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
       if(viewType == R.layout.notification_view_holder) {
           holder =  ContentViewHolder(
               inflater.inflate(R.layout.notification_view_holder, parent, false),
               notificationClickListener
           )
       }
        else {
           holder= HeaderViewHolder(
               inflater.inflate(
                   R.layout.notification_header_view_holder,
                   parent,
                   false
               ), notificationClickListener
           )
        }
        return holder
    }

    inner class HeaderViewHolder(
        itemView: View,
        globalOnProductListener: NotificationClickListener
    ) : BaseViewHolder(itemView), View.OnClickListener {
        //val productImage:ImageView = itemView.product_image
        private val notificationName : TextView= itemView.findViewById(R.id.notificationHeaderTextView)

        private var headerClickListener: NotificationClickListener
        init {
            itemView.setOnClickListener(this)
            this.headerClickListener = globalOnProductListener
        }
        override fun bind(item: NotificationItem) {
            notificationName.text = item.notificationName
             //productImage.loadImage("https://via.placeholder.com/150" )
        }
        override fun onClick(v: View) {
            headerClickListener.onItemClick(v)
        }
    }
     */

}