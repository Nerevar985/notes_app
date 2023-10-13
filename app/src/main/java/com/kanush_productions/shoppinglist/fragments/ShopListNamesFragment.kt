package com.kanush_productions.shoppinglist.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kanush_productions.shoppinglist.activities.MainApp
import com.kanush_productions.shoppinglist.activities.ShopListActivity
import com.kanush_productions.shoppinglist.database.MainViewModel
import com.kanush_productions.shoppinglist.database.ShopNameAdapter
import com.kanush_productions.shoppinglist.databinding.FragmentNoteBinding
import com.kanush_productions.shoppinglist.databinding.FragmentShopListNamesBinding
import com.kanush_productions.shoppinglist.dialogs.DeleteDialog
import com.kanush_productions.shoppinglist.dialogs.NewListDialog
import com.kanush_productions.shoppinglist.entities.ShopListNameItem
import com.kanush_productions.shoppinglist.utils.MyTouchListener
import com.kanush_productions.shoppinglist.utils.TimeManager
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.common.AdRequest


class ShopListNamesFragment : BaseFragment(), ShopNameAdapter.Listener{
    private lateinit var bind: FragmentShopListNamesBinding
    private lateinit var adapter: ShopNameAdapter


    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).dataBase)
    }
    override fun onClickNew() {
        NewListDialog.showDialog(activity as AppCompatActivity, object: NewListDialog.Listener{
            override fun onClick(name: String) {
                val shopListName = ShopListNameItem(
                    null,
                    name,
                    TimeManager.getCurrentTime(),
                    0,
                    0,
                    ""
                )
                mainViewModel.insertShopListName(shopListName)
            }
        }, "")
    }

    private fun fabListener(){
        bind.fab.setOnClickListener {
            onClickNew()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentShopListNamesBinding.inflate(inflater, container, false)
        bind.banner.setAdUnitId("R-M-2492467-2")
        bind.banner.setAdSize(AdSize.stickySize(350))
        val adRequest = AdRequest.Builder().build()
        //bind.banner.loadAd(adRequest)
        return bind.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
        fabListener()
        bind.rcView.attachFab(bind.fab)
    }

    private fun RecyclerView.attachFab(fab : FloatingActionButton) {
        this.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab.isShown)
                    fab.hide()
                else if (dy < 0 && !fab.isShown)
                    fab.show()
            }
        })
    }

    private fun initRcView() = with(bind){
        rcView.layoutManager = LinearLayoutManager(activity)
        adapter = ShopNameAdapter(this@ShopListNamesFragment)
        rcView.adapter = adapter
    }
    private fun observer() {
        mainViewModel.allShopListNameItem.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            bind.emptyList.visibility = if (it.isEmpty()){
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShopListNamesFragment()
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object: DeleteDialog.Listener{
            override fun onClick() {
                mainViewModel.deleteShopList(id, true)
            }

        })
    }

    override fun editItem(shopListNameItem: ShopListNameItem) {
        NewListDialog.showDialog(activity as AppCompatActivity, object: NewListDialog.Listener{
            override fun onClick(name: String) {
                mainViewModel.updateShopListName(shopListNameItem.copy(name = name))
            }
        }, shopListNameItem.name)
    }

    override fun onClickItem(shopListNameItem: ShopListNameItem) {
        val i = Intent(activity, ShopListActivity::class.java). apply {
            putExtra(ShopListActivity.SHOP_LIST_NAME, shopListNameItem)
        }
        startActivity(i)
    }




}