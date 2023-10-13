package com.kanush_productions.shoppinglist.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kanush_productions.shoppinglist.R

import com.kanush_productions.shoppinglist.database.MainViewModel
import com.kanush_productions.shoppinglist.database.ShopListItemAdapter
import com.kanush_productions.shoppinglist.databinding.ActivityShopListBinding
import com.kanush_productions.shoppinglist.dialogs.EditListDialog
import com.kanush_productions.shoppinglist.entities.LibraryItem
import com.kanush_productions.shoppinglist.entities.ShopListItem
import com.kanush_productions.shoppinglist.entities.ShopListNameItem
import com.kanush_productions.shoppinglist.utils.ShareHelper

class ShopListActivity : AppCompatActivity(), ShopListItemAdapter.Listener {
    private lateinit var bind: ActivityShopListBinding
    private var shopListNameItem: ShopListNameItem? = null
    private lateinit var saveItem: MenuItem
    private var etItem: EditText? = null
    private var adapter: ShopListItemAdapter? = null
    private lateinit var textWatcher: TextWatcher
    private lateinit var defPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        bind = ActivityShopListBinding.inflate(layoutInflater)

        setContentView(bind.root)
        setSupportActionBar(bind.tool)
        supportActionBar?.title = "Your List"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
        initRcView()
        listItemObserver()
        supportActionBar?.title = shopListNameItem?.name
        val onBackPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveItemCount()
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).dataBase)
    }

    private fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            shopListNameItem = intent.getSerializableExtra(SHOP_LIST_NAME, ShopListNameItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            shopListNameItem = intent.getSerializableExtra(SHOP_LIST_NAME) as ShopListNameItem
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu, menu)
        saveItem = menu?.findItem(R.id.save_item)!!
        val newItem = menu.findItem(R.id.new_item)
        etItem = newItem.actionView?.findViewById(R.id.et_new_shop_item) as EditText
        newItem.setOnActionExpandListener(expandActionView())
        saveItem.isVisible = false
        textWatcher = textWatcher()
        return true
    }

    private fun textWatcher(): TextWatcher{
        return object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, p2: Int,count: Int) {
                mainViewModel.getAllLibraryItems("%$s%")
            }

            override fun afterTextChanged(s: Editable?) {

            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_item -> {
                addNewShopItem(etItem?.text.toString())
                //etItem?.removeTextChangedListener(textWatcher)
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(etItem?.windowToken, 0)
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity)
                invalidateOptionsMenu()
            }
            android.R.id.home -> {
                saveItemCount()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(etItem?.windowToken, 0)
                onBackPressedDispatcher.onBackPressed()
            }
            R.id.delete_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, true)
                finish()
            }
            R.id.clear_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, false)
            }
            R.id.share_list -> {
                startActivity(Intent.createChooser(ShareHelper.shareShoplist(adapter?.currentList!!, shopListNameItem?.name!!), "Share by"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewShopItem(name: String){
        if(name.isEmpty()) return
        val item = ShopListItem(
            null,
            name,
            null,
            false,
            shopListNameItem?.id!!,
            0
        )
        mainViewModel.insertShopItem(item)
        etItem?.setText("")
    }

    private fun listItemObserver(){
        mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).observe(this) {
            adapter?.submitList(it)
            bind.tvEmpty.visibility = if (it.isEmpty()){
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun libraryItemObserver(){
        mainViewModel.libraryItems.observe(this) {
            val tempShopList = ArrayList<ShopListItem>()
            it.forEach{item ->
                val shopItem = ShopListItem(
                    item.id,
                    item.name,
                    "",
                    false,
                    0,
                    1
                )
                tempShopList.add(shopItem)
            }
            adapter?.submitList(tempShopList)
            bind.tvEmpty.visibility = if (it.isEmpty()){
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun initRcView() = with(bind){
        adapter = ShopListItemAdapter(this@ShopListActivity)
        rcView.layoutManager = LinearLayoutManager(this@ShopListActivity)
        rcView.adapter= adapter
    }

    private fun expandActionView(): MenuItem.OnActionExpandListener{
        return object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                saveItem.isVisible = true
                etItem?.addTextChangedListener(textWatcher)
                libraryItemObserver()
                mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).removeObservers(this@ShopListActivity)
                mainViewModel.getAllLibraryItems("%%")
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                saveItem.isVisible = false
                etItem?.removeTextChangedListener(textWatcher)
                invalidateOptionsMenu()
                etItem?.setText("")
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(etItem?.windowToken, 0)
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity)
                listItemObserver()
                return true
            }
        }
    }

    companion object{
        const val SHOP_LIST_NAME = "shop_list_name"
    }

    override fun onClickItem(shopListItem: ShopListItem, state: Int) {
        when(state) {
            ShopListItemAdapter.CHECK_BOX -> {
                mainViewModel.updateListItem(shopListItem)

            }
            ShopListItemAdapter.EDIT -> editListItem(shopListItem)
            ShopListItemAdapter.EDIT_LIBRARY_ITEM -> editLibraryItem(shopListItem)
            ShopListItemAdapter.ADD -> {
                addNewShopItem(shopListItem.name)
                invalidateOptionsMenu()
                saveItemCount()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(etItem?.windowToken, 0)
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity)
                listItemObserver()

            }
            ShopListItemAdapter.DELETE_LIBRARY_ITEM -> {
                mainViewModel.deleteLibraryItem(shopListItem.id!!)
                mainViewModel.getAllLibraryItems("%${etItem?.text.toString()}%")
            }
        }
    }

    private fun editListItem(shopListItem: ShopListItem){
       EditListDialog.showDialog(this, shopListItem, object :EditListDialog.Listener{
           override fun onClick(shopListItem: ShopListItem) {
               mainViewModel.updateListItem(shopListItem)
           }
       })
    }

    private fun editLibraryItem(shopListItem: ShopListItem){
        EditListDialog.showDialog(this, shopListItem, object :EditListDialog.Listener{
            override fun onClick(shopListItem: ShopListItem) {
                mainViewModel.updateLibraryItem(LibraryItem(shopListItem.id, shopListItem.name))
                mainViewModel.getAllLibraryItems("%${etItem?.text.toString()}%")
            }
        })
    }

    private fun saveItemCount(){
        var checkedItemCounter = 0
        adapter?.currentList?.forEach{
            if (it.itemChecked) checkedItemCounter++
        }
        val tempShopListNameItem = shopListNameItem?.copy(
            itemCount = adapter?.itemCount!!,
            checkedItems = checkedItemCounter
        )
        mainViewModel.updateShopListName(tempShopListNameItem!!)
    }
    private fun getSelectedTheme():Int{
        return if (defPref.getString("theme_key", "Light") == "Light") {
            R.style.MyMainStyleNoActionBar
        } else {
            R.style.MyMainStyleNoActionBarDark
        }
    }
}