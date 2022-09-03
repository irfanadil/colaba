package com.rnsoft.colabademo


import retrofit2.Response
import javax.inject.Inject


class RequestDocsRepo  @Inject constructor(private val requestDocsDataSource: RequestDocsDataSource) {

    suspend fun getEmailTemplates(token: String): Result<ArrayList<EmailTemplatesResponse>> {
        return requestDocsDataSource.getEmailTemplates(token = token )
    }

    suspend fun getCategoryDocumentMcu(token: String): Result<CategoryDocsResponse> {
        return requestDocsDataSource.getCategoryDocumentMcu(token = token )
    }

    suspend fun getTemplates(token: String): Result<GetTemplatesResponse>{
        return requestDocsDataSource.getTemplates(token = token )
    }

    suspend fun getEmailBody(token: String,loanApplicaitonId : Int, templateId : String): Result<EmailTemplatesResponse>{
        return requestDocsDataSource.getEmailBody(token = token,loanApplicaitonId,templateId)
    }

    suspend fun sendDocRequest(token: String, data: SendDocRequestModel): Result<Response<Unit>> {
        return requestDocsDataSource.sendDocRequest(token,data)
    }



}