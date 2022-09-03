package com.rnsoft.colabademo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rnsoft.AssetTypesByCategory
import com.rnsoft.colabademo.activities.assets.fragment.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject



@HiltViewModel
class AssetViewModel @Inject constructor(private val assetsRepo: AssetsRepo) : ViewModel() {

    private val _bankAccountType: MutableLiveData<ArrayList<DropDownResponse>> = MutableLiveData()
    val bankAccountType: LiveData<ArrayList<DropDownResponse>> get() = _bankAccountType

    private val _allFinancialAsset: MutableLiveData<ArrayList<DropDownResponse>> = MutableLiveData()
    val allFinancialAsset: LiveData<ArrayList<DropDownResponse>> get() = _allFinancialAsset

    private val _assetByCategory: MutableLiveData<ArrayList<AssetTypesByCategory>> = MutableLiveData()
    val assetByCategory: LiveData<ArrayList<AssetTypesByCategory>> get() = _assetByCategory

    private val _bankAccountDetails: MutableLiveData<BankAccountResponse?> = MutableLiveData()
    val bankAccountDetails: LiveData<BankAccountResponse?> get() = _bankAccountDetails



    private var _genericAddUpdateAssetResponse: MutableLiveData<GenericAddUpdateAssetResponse?> = MutableLiveData()
    val genericAddUpdateAssetResponse: LiveData<GenericAddUpdateAssetResponse?> get() = _genericAddUpdateAssetResponse

    private val _retirementAccountDetail: MutableLiveData<RetirementAccountResponse?> = MutableLiveData()
    val retirementAccountDetail: LiveData<RetirementAccountResponse?> get() = _retirementAccountDetail

    fun resetChildFragmentToNull(){
        _bankAccountDetails.value = null
        _bankAccountDetails.postValue(null)

        _retirementAccountDetail.value = null
        _retirementAccountDetail.postValue(null)

        _financialAssetDetail.value = null
        _financialAssetDetail.postValue(null)

        _giftAssetDetail.value = null
        _giftAssetDetail.postValue(null)

        _otherAssetDetail.value = null
        _otherAssetDetail.postValue(null)

        _proceedFromLoanModel.value = null
        _proceedFromLoanModel.postValue(null)

        _genericAddUpdateAssetResponse.value  = null
        _genericAddUpdateAssetResponse.postValue(null)

    }

    private val _financialAssetDetail: MutableLiveData<FinancialAssetResponse?> = MutableLiveData()
    val financialAssetDetail: LiveData<FinancialAssetResponse?> get() = _financialAssetDetail

    private val _loanNonRealEstate: MutableLiveData<AssetsRealEstateResponse> = MutableLiveData()
    val loanNonRealEstate: LiveData<AssetsRealEstateResponse> get() = _loanNonRealEstate

    private val _loanRealEstate: MutableLiveData<AssetsRealEstateResponse> = MutableLiveData()
    val loanRealEstate: LiveData<AssetsRealEstateResponse> get() = _loanRealEstate

    private val _allGiftResources: MutableLiveData<ArrayList<GiftSourcesResponse>> = MutableLiveData()
    val allGiftResources: LiveData<ArrayList<GiftSourcesResponse>> get() = _allGiftResources

    private var _giftAssetDetail: MutableLiveData<GiftAssetResponse?> = MutableLiveData()
    val giftAssetDetail: LiveData<GiftAssetResponse?> get() = _giftAssetDetail

    private val _otherAssetDetail: MutableLiveData<OtherAssetResponse?> = MutableLiveData()
    val otherAssetDetail: LiveData<OtherAssetResponse?> get() = _otherAssetDetail

    private val _assetTypesByCategoryItemList: MutableLiveData<ArrayList<GetAssetTypesByCategoryItem>> = MutableLiveData()
    val assetTypesByCategoryItemList: LiveData<ArrayList<GetAssetTypesByCategoryItem>> get() = _assetTypesByCategoryItemList

    private val _proceedFromLoanModel: MutableLiveData<ProceedFromLoanModel?> = MutableLiveData()
    val proceedFromLoanModel: LiveData<ProceedFromLoanModel?> get() = _proceedFromLoanModel


    suspend fun fetchAssetTypesByCategoryItemList(token: String , categoryId:Int, loanPurposeId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.fetchAssetTypesByCategoryItemList( token , categoryId, loanPurposeId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _assetTypesByCategoryItemList.value = (responseResult.data  )
            }
        }
    }

    suspend fun getProceedsFromLoan(token: String , loanApplicationId:Int, borrowerId:Int, assetTypeID:Int, borrowerAssetId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getProceedsFromLoan( token , loanApplicationId, borrowerId, assetTypeID, borrowerAssetId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _proceedFromLoanModel.value = (responseResult.data  )
            }
        }
    }

    suspend fun getProceedsFromNonRealEstateDetail(token: String , loanApplicationId:Int, borrowerId:Int, assetTypeID:Int, borrowerAssetId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getProceedsFromNonRealEstateDetail( token , loanApplicationId, borrowerId, assetTypeID, borrowerAssetId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _proceedFromLoanModel.value = (responseResult.data  )
            }
        }
    }

    suspend fun getProceedsFromRealEstateDetail(token: String , loanApplicationId:Int, borrowerId:Int, assetTypeID:Int, borrowerAssetId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getProceedsFromRealEstateDetail( token , loanApplicationId, borrowerId, assetTypeID, borrowerAssetId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _proceedFromLoanModel.value = (responseResult.data  )
            }
        }
    }


    suspend fun getBankAccountType(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getBankAccountType(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _bankAccountType.value = (responseResult.data)
                /*else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                        EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                     else if (responseResult is Result.Error)
                        EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
                    */

            }
        }
    }

    suspend fun getBankAccountDetails(token: String, loanApplicationId: Int, borrowerId: Int, borrowerAssetId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getBankAccountDetails(
                token = token,
                loanApplicationId = loanApplicationId,
                borrowerId, borrowerAssetId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _bankAccountDetails.value = (responseResult.data)

                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }


    suspend fun addUpdateBankDetails(token: String,  bankAddUpdateParams: BankAddUpdateParams) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.addUpdateBankDetails(token = token, bankAddUpdateParams = bankAddUpdateParams)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _genericAddUpdateAssetResponse.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun addUpdateOtherAsset(token: String,  otherAssetAddUpdateParams: OtherAssetAddUpdateParams) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.addUpdateOtherAsset(token = token, otherAssetAddUpdateParams = otherAssetAddUpdateParams)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _genericAddUpdateAssetResponse.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun addUpdateGift(token: String,  giftAddUpdateParams: GiftAddUpdateParams) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.addUpdateGift(token = token, giftAddUpdateParams = giftAddUpdateParams)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _genericAddUpdateAssetResponse.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }



    suspend fun addUpdateRetirement(token: String, retirementAddUpdateParams: RetirementAddUpdateParams) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.addUpdateRetirement(token = token, retirementAddUpdateParams )
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _genericAddUpdateAssetResponse.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }


    fun addUpdateStockBonds( stocksBondsAddUpdateParams:StocksBondsAddUpdateParams) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.addUpdateStockBonds( stocksBondsAddUpdateParams = stocksBondsAddUpdateParams)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _genericAddUpdateAssetResponse.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }


    suspend fun addUpdateProceedFromLoan(token: String,  addUpdateProceedLoanParams: AddUpdateProceedLoanParams) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.addUpdateProceedFromLoan(token = token, addUpdateProceedLoanParams = addUpdateProceedLoanParams)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _genericAddUpdateAssetResponse.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun addUpdateProceedFromLoanOther(token: String, addUpdateProceedFromLoanOtherParams: AddUpdateProceedFromLoanOtherParams) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.addUpdateProceedFromLoanOther(token = token, addUpdateProceedFromLoanOtherParams = addUpdateProceedFromLoanOtherParams)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _genericAddUpdateAssetResponse.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }


    suspend fun addUpdateAssetsRealStateOrNonRealState(token: String, addUpdateRealStateParams: AddUpdateRealStateParams) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.addUpdateAssetsRealStateOrNonRealState(token = token, addUpdateRealStateParams = addUpdateRealStateParams)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _genericAddUpdateAssetResponse.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }



    suspend fun deleteAsset(token: String, assetId:Int, borrowerId:Int, loanApplicationId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.deleteAsset(token = token, assetId = assetId, borrowerId = borrowerId, loanApplicationId = loanApplicationId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _genericAddUpdateAssetResponse.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }





    suspend fun getRetirementAccountDetails(token: String, loanApplicationId: Int, borrowerId: Int, borrowerAssetId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getRetirementAccountDetails(
                token = token,
                loanApplicationId = loanApplicationId,
                borrowerId, borrowerAssetId)
                withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _retirementAccountDetail.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getFinancialAssetDetails(token: String, loanApplicationId: Int, borrowerId: Int, borrowerAssetId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getFinancialAssetDetail(
                token = token,
                loanApplicationId = loanApplicationId,
                borrowerId, borrowerAssetId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _financialAssetDetail.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getAllFinancialAsset(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getAllFinancialAsset(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _allFinancialAsset.value = (responseResult.data)
                /*else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                        EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                     else if (responseResult is Result.Error)
                        EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
                    */

            }
        }
    }

    suspend fun getFromLoanNonRealEstateDetail(token: String, loanApplicationId: Int, borrowerId: Int, assetTypeId: Int, borrowerAssetId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getFromLoanNonRealEstateDetail(
                token = token,
                loanApplicationId = loanApplicationId,
                borrowerId, assetTypeId, borrowerAssetId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _loanNonRealEstate.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getFromLoanRealEstateDetail(token: String, loanApplicationId: Int, borrowerId: Int, assetTypeId: Int, borrowerAssetId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getFromLoanRealEstateDetail(
                token = token,
                loanApplicationId = loanApplicationId,
                borrowerId, assetTypeId, borrowerAssetId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _loanRealEstate.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }

    suspend fun getAllGiftSources(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getAllGiftSources(token = token)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _allGiftResources.value = (responseResult.data)
                /*else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                        EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                     else if (responseResult is Result.Error)
                        EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
                    */

            }
        }
    }

    suspend fun getGiftAssetDetails(token: String, loanApplicationId: Int, borrowerId: Int,borrowerAssetId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getGiftAsset(
                token = token,
                loanApplicationId = loanApplicationId,
                borrowerId, borrowerAssetId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _giftAssetDetail.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }


    suspend fun getOtherAssetDetails(token: String, loanApplicationId: Int, borrowerId: Int, borrowerAssetId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseResult = assetsRepo.getOtherAsset(token = token, loanApplicationId = loanApplicationId, borrowerId, borrowerAssetId)
            withContext(Dispatchers.Main) {
                if (responseResult is Result.Success)
                    _otherAssetDetail.value = (responseResult.data)
                else if (responseResult is Result.Error && responseResult.exception.message == AppConstant.INTERNET_ERR_MSG)
                    EventBus.getDefault().post(WebServiceErrorEvent(null, true))
                else if (responseResult is Result.Error)
                    EventBus.getDefault().post(WebServiceErrorEvent(responseResult))
            }
        }
    }




}