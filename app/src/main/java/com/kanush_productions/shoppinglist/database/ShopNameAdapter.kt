package com.kanush_productions.shoppinglist.database

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kanush_productions.shoppinglist.R
import com.kanush_productions.shoppinglist.activities.ShopListActivity
import com.kanush_productions.shoppinglist.databinding.ListNameItemBinding
import com.kanush_productions.shoppinglist.entities.ShopListNameItem

class ShopNameAdapter(private val listener: Listener) : ListAdapter<ShopListNameItem, ShopNameAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position), listener)
    }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bind = ListNameItemBinding.bind(view)
        fun setData(shopListNameItem: ShopListNameItem, listener: Listener) = with(bind) {
            tvListName.text = shopListNameItem.name
            tvTime.text = shopListNameItem.time
            progressBar.max = shopListNameItem.itemCount
            progressBar.progress = shopListNameItem.checkedItems
            val colorState = ColorStateList.valueOf(getProgressColorState(shopListNameItem, bind.root.context))
            progressBar.progressTintList = colorState
            //counterCard.backgroundTintList = colorState
            val counterText = "${shopListNameItem.checkedItems}/${shopListNameItem.itemCount}"
            tvCounter.text = counterText
            itemView.setOnClickListener{
                listener.onClickItem(shopListNameItem)

            }
            ibDelete.setOnClickListener {
                listener.deleteItem(shopListNameItem.id!!)
            }
            ibEdit.setOnClickListener {
                listener.editItem(shopListNameItem)
            }
        }

        private fun getProgressColorState(shopListNameItem: ShopListNameItem, context: Context): Int{
            return if (shopListNameItem.checkedItems == shopListNameItem.itemCount) ContextCompat.getColor(context, R.color.green)
            else ContextCompat.getColor(context, R.color.red)
        }

        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.list_name_item, parent, false)
                )
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<ShopListNameItem>() {
        override fun areItemsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {
            return oldItem == newItem
        }

    }
    interface Listener{
        fun deleteItem(id: Int)
        fun editItem(shopListNameItem: ShopListNameItem)
        fun onClickItem(shopListNameItem: ShopListNameItem)
    }



}