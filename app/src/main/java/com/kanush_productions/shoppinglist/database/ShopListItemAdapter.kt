package com.kanush_productions.shoppinglist.database


import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kanush_productions.shoppinglist.R
import com.kanush_productions.shoppinglist.activities.ShopListActivity
import com.kanush_productions.shoppinglist.databinding.ShopLibraryItemBinding
import com.kanush_productions.shoppinglist.databinding.ShopListItemBinding
import com.kanush_productions.shoppinglist.entities.ShopListItem


class ShopListItemAdapter(private val listener: Listener) : ListAdapter<ShopListItem, ShopListItemAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if (viewType == 0) ItemHolder.createShopItem(parent)
        else ItemHolder.createLibraryItem(parent)

    }


    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (getItem(position).itemType == 0) holder.setItemData(getItem(position), listener)
        else holder.setLibraryData(getItem(position), listener)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }


    class ItemHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun setItemData(shopListItem: ShopListItem, listener: Listener){
            val bind = ShopListItemBinding.bind(view)
            bind.apply {
                tvName.text = shopListItem.name
                tvInfo.text = shopListItem.itemInfo

                tvInfo.visibility = infoVisibility(shopListItem)
                checkbox.isChecked = shopListItem.itemChecked
                setPaintFlagAndColor(bind)
                checkbox.setOnClickListener {
                    listener.onClickItem(shopListItem.copy(itemChecked = checkbox.isChecked), CHECK_BOX)
                }
                ibEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT)
                }
            }
        }
        fun setLibraryData(shopListItem: ShopListItem, listener: Listener) {
            val bind = ShopLibraryItemBinding.bind(view)
            bind.apply {
                tvName.text = shopListItem.name
                ibEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT_LIBRARY_ITEM)
                }
                ibDeleteLibrary.setOnClickListener {
                    listener.onClickItem(shopListItem, DELETE_LIBRARY_ITEM)
                }
                itemView.setOnClickListener {
                    listener.onClickItem(shopListItem, ADD)
                }
            }
        }
        private fun setPaintFlagAndColor(binding: ShopListItemBinding){
            binding.apply{
                if (checkbox.isChecked){
                    tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
//                    tvName.setTextColor(ContextCompat.getColor(tvName.context, R.color.grey))
//                    tvInfo.setTextColor(ContextCompat.getColor(tvName.context, R.color.grey))
//                      val colorChecked = ContextCompat.getColor(listItemCard.context, R.color.light_grey)
//                      listItemCard.setBackgroundColor(colorChecked)


                }else {
                    tvName.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
//                    val colorUnChecked = ContextCompat.getColor(listItemCard.context, R.color.white)
//                    listItemCard.setBackgroundColor(colorUnChecked)
//                    tvName.setTextColor(ContextCompat.getColor(tvName.context, R.color.black))
//                    tvInfo.setTextColor(ContextCompat.getColor(tvName.context, R.color.black))

                }
            }
        }
        private fun infoVisibility(shopListItem: ShopListItem): Int{
            return if (shopListItem.itemInfo.isNullOrEmpty()){
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        companion object {
            fun createShopItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shop_list_item, parent, false)
                )
            }
            fun createLibraryItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shop_library_item, parent, false)
                )
        }
    }
    }

    class ItemComparator : DiffUtil.ItemCallback<ShopListItem>() {
        override fun areItemsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
            return oldItem == newItem
        }

    }

    interface Listener{
        fun onClickItem(shopListItem: ShopListItem, state: Int)
    }

    companion object{
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY_ITEM = 2
        const val DELETE_LIBRARY_ITEM = 3
        const val ADD = 4
    }

}

