package com.rnsoft.colabademo

import android.content.Context

data class SearchModel(
    val id: Int,
    val caseNumber: String,
    val notificationName: String?,
    val notificationTime: String?
)  {



    companion object {
        fun sampleSearchList(context: Context): List<SearchModel> {
            return listOf(
                SearchModel(0, "9202909209",
                    context.getString(R.string.submitted_docs),
                    context.getString(R.string.fifteen_thirty_four)
                ),
                SearchModel(1,"9202909209",
                    context.getString(R.string.title_cricket),
                    context.getString(R.string.submitted_docs)
                ),
                SearchModel(2,"9202909209",
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.submitted_docs)
                ),
                SearchModel(3, "9202909209",
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.submitted_docs)
                ),
                SearchModel(4,"9202909209",
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.submitted_docs)
                ),
                SearchModel(5, "9202909209",
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.submitted_docs)
                ),
                SearchModel(6, "9202909209",
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.submitted_docs)
                ),
                SearchModel(7, "9202909209",
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.submitted_docs)
                ),
                SearchModel(8, "9202909209",
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.submitted_docs)
                ),
                SearchModel(9, "9202909209",
                    context.getString(R.string.title_hockey),
                    context.getString(R.string.submitted_docs)
                ),
                SearchModel(10, "9202909209",
                    context.getString(R.string.title_basketball),
                    context.getString(R.string.submitted_docs)
                )
            )
        }
    }
}



