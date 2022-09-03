package com.rnsoft.colabademo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rnsoft.colabademo.database.tables.ActiveLoanTable
import com.rnsoft.colabademo.database.tables.AllLoanTable
import com.rnsoft.colabademo.database.tables.NonActiveLoanTable

@Dao
 interface LoansDao {

    @Query("SELECT * FROM allLoanTable  WHERE allLoanTable.loanFilter=:loanFilter")
    fun getAllLoans(loanFilter:Int): List<AllLoanTable>

    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    //suspend fun insertSingleLoan(allLoan: AllLoanTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insertAllLoans(allLoanList: List<AllLoanTable>)

   @Query("DELETE FROM allLoanTable WHERE allLoanTable.loanFilter=:loanFilter")
   suspend fun deleteAllLoans(loanFilter:Int)


   /////////////////////////////////////////////////////////////////////////////////////////////////////////

   /*

    @Query("SELECT * FROM activeLoanTable")
    fun getActiveLoans(): List<ActiveLoanTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleActiveLoan(activeLoan: ActiveLoanTable)

    @Insert
    fun insertActiveLoans(activeLoanList : List<ActiveLoanTable>)


   @Query("DELETE FROM activeLoanTable")
   suspend fun deleteActiveLoans()

   /////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Query("SELECT * FROM nonActiveLoanTable")
    fun getNonActiveLoans(): List<NonActiveLoanTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleNonActiveLoan(nonActiveLoan: NonActiveLoanTable)

    @Insert
    fun insertNonActiveLoans(nonActiveList: List<NonActiveLoanTable>)

   @Query("DELETE FROM nonActiveLoanTable")
   suspend fun deleteNonActiveLoans()

    */

}