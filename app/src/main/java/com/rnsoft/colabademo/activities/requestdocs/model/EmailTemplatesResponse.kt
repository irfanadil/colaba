package com.rnsoft.colabademo

import com.google.gson.annotations.SerializedName

/**
 * Created by Anita Kiran on 12/20/2021.
 */
data class EmailTemplatesResponse(
    @SerializedName("id")
    val id: String,
    val tenantId: Int,
    val templateName: String,
    val templateDescription: String,
    val fromAddress: String,
    val toAddress: String?,
    val ccAddress: String?,
    val subject: String?,
    val emailBody: String?,
    val sortOrder: Int?
)


