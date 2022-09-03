package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

data class SendDocRequestModel(
    val loanApplicationId: Int,
    @SerializedName("requests")
    val requests: List<DocRequestDataList>
)

data class DocRequestDataList(
    val email: Email,
    val documents: List<RequestDocument>
)

data class Email(
    val emailTemplateId: String?=null,
    val toAddress: String,
    val fromAddress: String? = null,
    val ccAddress: String? = null,
    val subject: String,
    val emailBody: String? = null
)

data class RequestDocument(
    val docId: String?= null,
    val docMessage: String,
    val docType: String,
    val docTypeId: String?=null,
    val requestId: Int? = null
)