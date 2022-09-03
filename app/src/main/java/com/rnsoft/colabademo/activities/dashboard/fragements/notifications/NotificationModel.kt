package com.rnsoft.colabademo

import android.content.Context

data class NotificationModel(
    val id: Int?,
    val notificationName: String?,
    val notificationTime: String?,
    val notificationActive:Boolean? = false,
    val isContent:Boolean? = true
)  {



    companion object {
        fun sampleNotificationList(context: Context): List<NotificationModel> {
            return listOf(
                NotificationModel(0,
                    context.getString(R.string.today),
                    context.getString(R.string.fifteen_thirty_four),
                    true , false
                ),
                NotificationModel(1,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.time_ago),
                    true, true

                ),
                NotificationModel(2,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    true, true
                ),
                NotificationModel(3,
                    context.getString(R.string.yesterday),
                    context.getString(R.string.submitted_docs),
                    true, false
                ),
                NotificationModel(4,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    true, true
                ),
                NotificationModel(5,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    true, true
                ),
                NotificationModel(6,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    false, true
                ),
                NotificationModel(7,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    false, true
                ),
                NotificationModel(8,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    false, true
                ),
                NotificationModel(9,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    false, true
                ),
                NotificationModel(10,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    false, true
                ),
                NotificationModel(11,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    false, true
                ),
                NotificationModel(12,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    false, true
                ),
                NotificationModel(13,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    false, true
                ),
                NotificationModel(14,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    false, true
                ),
                NotificationModel(15,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    false, true
                ),
                NotificationModel(16,
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four),
                    false, true
                )


            )
        }
    }
}



