package com.rnsoft.colabademo

import android.content.SharedPreferences
import javax.inject.Inject

class SearchRepo @Inject constructor(private val searchDataSource: SearchDataSource,
                                     private val preferenceEditor: SharedPreferences.Editor)
{
    suspend fun getSearchResult(token:String, pageNumber:Int, pageSize:Int, searchTerm:String)
    : Result<ArrayList<SearchItem>>
    {
        val searchResult = searchDataSource.searchByText(token = token ,
            pageNumber = pageNumber, pageSize = pageSize, searchTerm = searchTerm)

        if(searchResult is Result.Success)
            storeSearchResult()

        return searchResult
    }

    private fun storeSearchResult(){

    }
}