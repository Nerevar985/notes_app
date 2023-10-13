package com.kanush_productions.shoppinglist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kanush_productions.shoppinglist.entities.LibraryItem
import com.kanush_productions.shoppinglist.entities.NoteItems
import com.kanush_productions.shoppinglist.entities.ShopListItem
import com.kanush_productions.shoppinglist.entities.ShopListNameItem


@Database(
    entities = [LibraryItem::class, NoteItems::class, ShopListItem::class, ShopListNameItem::class],
    version = 1
)
abstract class MainDataBase : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: MainDataBase? = null

        fun getDataBase(context: Context): MainDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDataBase::class.java,
                    "Shopping_List.db"
                ).build()
                instance
            }

        }
    }
}