package com.rnsoft.colabademo

import android.content.SharedPreferences
import com.google.gson.Gson
import com.rnsoft.colabademo.database.tables.AllLoanTable
import java.lang.Exception
import javax.inject.Inject

class LoanRepo @Inject constructor(
    private val loanDataSource: LoanDataSource
    //private val loansDao: LoansDao,
    //private val preferenceEditor: SharedPreferences.Editor
) {



    suspend fun getAllLoans(token: String, dateTime:String,
                            pageNumber:Int, pageSize:Int,
                            loanFilter:Int, orderBy:Int,
                            assignedToMe:Boolean)
    :Result<ArrayList<LoanItem>>
    {

        val loansResult = loanDataSource.loadAllLoans(token = token ,  dateTime = dateTime,
            pageNumber = pageNumber, pageSize = pageSize, loanFilter = loanFilter,
            orderBy = orderBy, assignedToMe = assignedToMe)

        //if(loansResult is Result.Success)
            //storeLoansResultToRoom(loansResult.data , loanFilter)

        return loansResult
    }



    /*
    private fun storeLoansResultToRoom(loanResult: ArrayList<LoanItem>, repoLoanFilter: Int){
        if(repoLoanFilter == 0 && !AppSetting.hasLoanApiDataLoaded) {
            val loanResultString = Gson().toJson(loanResult)
            preferenceEditor.putString(AppConstant.oldLoans, loanResultString).apply()
            AppSetting.hasLoanApiDataLoaded = true
        }
        else if(repoLoanFilter == 1 && !AppSetting.hasActiveLoanApiDataLoaded) {
            val loanResultString = Gson().toJson(loanResult)
            preferenceEditor.putString(AppConstant.oldActiveLoans, loanResultString).apply()
            AppSetting.hasActiveLoanApiDataLoaded = true
        }
        else if(repoLoanFilter == 2 && !AppSetting.hasNonActiveLoanApiDataLoaded) {
            val loanResultString = Gson().toJson(loanResult)
            preferenceEditor.putString(AppConstant.oldNonActiveLoans, loanResultString).apply()
            AppSetting.hasNonActiveLoanApiDataLoaded = true
        }

    }

    private suspend fun fillLoanTable(loanResult:ArrayList<LoanItem>, repoLoanFilter:Int) :Boolean{
        try {
            // first delete all loans w.r.t loan filter then store latest ones to cache....
            loansDao.deleteAllLoans(repoLoanFilter)

            val tableLoanItemList: ArrayList<AllLoanTable> = ArrayList()
            for (temp in loanResult) {
                val tableLoanItem = AllLoanTable()
                tableLoanItem.loanFilter = repoLoanFilter
                tableLoanItem.activityTime = temp.activityTime
                tableLoanItem.cellNumber = temp.cellNumber
                tableLoanItem.coBorrowerCount = temp.coBorrowerCount
                temp.detail?.let {
                    tableLoanItem.loanAmount = it.loanAmount
                    tableLoanItem.propertyValue = it.propertyValue
                    it.address?.let { address ->
                        tableLoanItem.city = address.city
                        tableLoanItem.countryId = address.countryId
                        tableLoanItem.countryName = address.countryName
                        tableLoanItem.stateId = address.stateId
                        tableLoanItem.stateName = address.stateName
                        tableLoanItem.street = address.street
                        tableLoanItem.unit = address.unit
                        tableLoanItem.zipCode = address.zipCode
                    }
                }
                tableLoanItem.documents = temp.documents
                tableLoanItem.email = temp.email
                tableLoanItem.firstName = temp.firstName
                tableLoanItem.lastName = temp.lastName
                tableLoanItem.loanApplicationId = temp.loanApplicationId
                tableLoanItem.loanPurpose = temp.loanPurpose
                tableLoanItem.milestone = temp.milestone

                tableLoanItemList.add(tableLoanItem)
            }

            loansDao.insertAllLoans(tableLoanItemList)

        }
        catch (exception:Exception){
            return false
        }

        return true
    }

    fun getLoanDataFromRoom(loanFilter: Int):ArrayList<LoanItem>{
        val tableLoans = loansDao.getAllLoans(loanFilter)

        val loanItemList: ArrayList<LoanItem> = ArrayList()

        for (tableLoanItem in tableLoans) {
            val loanItem = LoanItem()
            loanItem.activityTime =  tableLoanItem.activityTime
            loanItem.cellNumber = tableLoanItem.cellNumber
            loanItem.coBorrowerCount = tableLoanItem.coBorrowerCount
            loanItem.detail?.loanAmount = tableLoanItem.loanAmount
            loanItem.detail?.propertyValue = tableLoanItem.propertyValue

            loanItem.detail?.address?.city = tableLoanItem.city
            loanItem.detail?.address?.countryId = tableLoanItem.countryId
            loanItem.detail?.address?.countryName = tableLoanItem.countryName
            loanItem.detail?.address?.stateId = tableLoanItem.stateId
            loanItem.detail?.address?.stateName = tableLoanItem.stateName
            loanItem.detail?.address?.street = tableLoanItem.street
            loanItem.detail?.address?.unit = tableLoanItem.unit
            loanItem.detail?.address?.zipCode = tableLoanItem.zipCode
            loanItem.documents = tableLoanItem.documents
            loanItem.email = tableLoanItem.email
            loanItem.firstName = tableLoanItem.firstName
            loanItem.lastName = tableLoanItem.lastName
            loanItem.loanApplicationId = tableLoanItem.loanApplicationId
            loanItem.loanPurpose = tableLoanItem.loanPurpose
            loanItem.milestone = tableLoanItem.milestone

            loanItemList.add(loanItem)
        }

        return  loanItemList
    }

     */





}