package com.rnsoft.colabademo

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rnsoft.colabademo.database.tables.ActiveLoanTable
import com.rnsoft.colabademo.database.tables.AllLoanTable
import com.rnsoft.colabademo.database.tables.NonActiveLoanTable

@Database(entities = [AllLoanTable::class , ActiveLoanTable::class, NonActiveLoanTable::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    //abstract fun loansDao(): LoansDao
}
