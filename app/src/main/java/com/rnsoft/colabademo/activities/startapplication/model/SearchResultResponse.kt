package com.rnsoft.colabademo

class SearchResultResponse : ArrayList<SearchResultResponseItem>()

data class SearchResultResponseItem(
    val contactId: Int?=null,
    val emailAddress: String="",
    val firstName: String="",
    val lastName: String="",
    val midleName: String="",
    val mobileNumber: String=""
)