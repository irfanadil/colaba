package com.rnsoft.colabademo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
class RequestDocsViewModel @Inject constructor(private val requestDocsRepo: RequestDocsRepo) : ViewModel() {

    private val _getTemplatesResponse : MutableLiveData<GetTemplatesResponse?> =   MutableLiveData()
    val getTemplatesResponse: LiveData<GetTemplatesResponse?> get() = _getTemplatesResponse

    private val _getCategoryDocuments : MutableLiveData<CategoryDocsResponse> =   MutableLiveData()
    val getCategoryDocuments: LiveData<CategoryDocsResponse> get() = _getCategoryDocuments

    private val _emailTemplates : MutableLiveData<ArrayList<EmailTemplatesResponse>?> =   MutableLiveData()
    val emailTemplates: MutableLiveData<ArrayList<EmailTemplatesResponse>?> get() = _emailTemplates

    private val _emailTemplateBody : MutableLiveData<EmailTemplatesResponse?> =   MutableLiveData()
    val emailTemplateBody: MutableLiveData<EmailTemplatesResponse?> get() = _emailTemplateBody

    private var webServiceRunning:Boolean = false

    suspend fun sendDocRequest(token: String,data: SendDocRequestModel){
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = requestDocsRepo.sendDocRequest(token = token, data)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success){
                    EventBus.getDefault().post(SendDocRequestEvent(ResponseStatus(200,"OK","")))
                } else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG){
                     EventBus.getDefault().post(SendDocRequestEvent(ResponseStatus(500,"OK","")))
                }

            }
        }
    }

    suspend fun getEmailTemplates(token:String) {
        viewModelScope.launch (Dispatchers.IO) {
            val responseResult = requestDocsRepo.getEmailTemplates(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    _emailTemplates.value = null
                    _emailTemplates.postValue(null) // refresh variable to avoid duplicate data
                    _emailTemplates.value = (responseResult.data)
                }
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getCategoryDocumentMcu(token:String) {
        viewModelScope.launch (Dispatchers.IO) {
            val responseResult = requestDocsRepo.getCategoryDocumentMcu(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _getCategoryDocuments.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getTemplates(token:String) {
        viewModelScope.launch (Dispatchers.IO) {
            val responseResult = requestDocsRepo.getTemplates(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    _getTemplatesResponse.value = (responseResult.data)
                }
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getEmailTemplateBody(token:String, loanApplicationId : Int, templateId: String ) {


        viewModelScope.launch (Dispatchers.IO) {
            val responseResult = requestDocsRepo.getEmailBody(token = token, loanApplicationId, templateId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success) {
                    _emailTemplateBody.value = null
                    _emailTemplateBody.postValue(null)
                    _emailTemplateBody.value = (responseResult.data)
                }
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }


}