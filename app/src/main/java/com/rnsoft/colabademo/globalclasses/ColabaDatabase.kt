package com.rnsoft.colabademo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext

/*
@Database(entities = [DatabaseItem::class, CartItem::class ], version = 1, exportSchema = false)
abstract class ProductsDatabase : RoomDatabase() {

    abstract fun databaseItemDao(): DatabaseItemDao

    abstract fun cartItemsDao(): CartItemsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ProductsDatabase? = null

        fun getDatabase(context: Context): ProductsDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    ProductsDatabase::class.java,
                    "room_database"
                ).fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

 */