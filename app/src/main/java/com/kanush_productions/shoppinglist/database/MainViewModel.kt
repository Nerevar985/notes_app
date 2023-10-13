package com.kanush_productions.shoppinglist.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kanush_productions.shoppinglist.entities.LibraryItem
import com.kanush_productions.shoppinglist.entities.NoteItems
import com.kanush_productions.shoppinglist.entities.ShopListItem
import com.kanush_productions.shoppinglist.entities.ShopListNameItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainViewModel(database: MainDataBase): ViewModel() {
    val dao = database.getDao()
    val libraryItems = MutableLiveData<List<LibraryItem>>()
    val allNotes: LiveData<List<NoteItems>> = dao.getAllNotes().asLiveData()
    val allShopListNameItem: LiveData<List<ShopListNameItem>> = dao.getAllShopListNames().asLiveData()

    fun getAllItemsFromList(listId: Int): LiveData<List<ShopListItem>>{
        return dao.getAllShopListItems(listId).asLiveData()
    }

    fun getAllLibraryItems(name: String) = viewModelScope.launch{
        libraryItems.postValue(dao.getAllLibraryItems(name))
    }

    fun insertNote(note: NoteItems) = viewModelScope.launch {
        dao.insertNote(note)
    }

    fun insertShopListName(listName: ShopListNameItem) = viewModelScope.launch {
        dao.insertShopListName(listName)
    }

    fun insertShopItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.insertItem(shopListItem)
        if (!libraryItemExists(shopListItem.name)) dao.insertLibraryItem(LibraryItem(null, shopListItem.name))
    }

    fun deleteShopList(id: Int, deleteList: Boolean) = viewModelScope.launch {
        if (deleteList){
            dao.deleteShopListName(id)
            dao.deleteShopListItemsByListId(id)
        } else dao.deleteShopListItemsByListId(id)

    }

    fun deleteNote(id: Int) = viewModelScope.launch {
        dao.deleteNote(id)
    }

    fun deleteLibraryItem(id: Int) = viewModelScope.launch {
        dao.deleteLibraryItem(id)
    }

    fun updateNote(note: NoteItems) = viewModelScope.launch {
        dao.updateNote(note)
    }

    fun updateLibraryItem(libraryItem: LibraryItem) = viewModelScope.launch {
        dao.updateLibraryItem(libraryItem)
    }

    fun updateListItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.updateListItem(shopListItem)
    }

    fun updateShopListName(name: ShopListNameItem) = viewModelScope.launch {
        dao.updateShopListName(name)
    }

    private suspend fun libraryItemExists(name: String): Boolean{
        return dao.getAllLibraryItems(name).isNotEmpty()
    }


    class MainViewModelFactory(val database: MainDataBase) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}