package com.kanush_productions.shoppinglist.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kanush_productions.shoppinglist.entities.LibraryItem
import com.kanush_productions.shoppinglist.entities.NoteItems
import com.kanush_productions.shoppinglist.entities.ShopListItem
import com.kanush_productions.shoppinglist.entities.ShopListNameItem
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {
    @Query("SELECT * FROM Note_items")
    fun getAllNotes(): Flow<List<NoteItems>>

    @Query("SELECT * FROM shopping_list_names")
    fun getAllShopListNames(): Flow<List<ShopListNameItem>>

    @Query("SELECT * FROM shopping_list_items WHERE list_id LIKE :listId")
    fun getAllShopListItems(listId: Int): Flow<List<ShopListItem>>

    @Query("SELECT * FROM library_items WHERE name LIKE :name")
    suspend fun getAllLibraryItems(name: String): List<LibraryItem>

    @Query("DELETE FROM Note_items WHERE id IS :id")
    suspend fun deleteNote(id: Int)

    @Query("DELETE FROM shopping_list_names WHERE id IS :id")
    suspend fun deleteShopListName(id: Int)

    @Query("DELETE FROM shopping_list_items WHERE list_id LIKE :listId")
    suspend fun deleteShopListItemsByListId(listId: Int)

    @Query("DELETE FROM library_items WHERE id IS :id")
    suspend fun deleteLibraryItem(id: Int)


    @Insert
    suspend fun insertNote(note: NoteItems)
    @Insert
    suspend fun insertItem(shopListItem: ShopListItem)
    @Insert
    suspend fun insertShopListName(name: ShopListNameItem)
    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem)


    @Update
    suspend fun updateNote(note: NoteItems)
    @Update
    suspend fun updateLibraryItem(libraryItem: LibraryItem)
    @Update
    suspend fun updateListItem(item: ShopListItem)
    @Update
    suspend fun updateShopListName(name: ShopListNameItem)

}