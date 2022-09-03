package com.rnsoft.colabademo

import android.view.View

interface NotificationClickListener {
    fun onItemClick( view: View)
    fun onNotificationRead(position: Int)
    fun onNotificationDelete(position: Int)
}
