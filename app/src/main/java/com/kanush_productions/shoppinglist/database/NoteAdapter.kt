package com.kanush_productions.shoppinglist.database

import android.content.SharedPreferences
import android.graphics.Paint
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kanush_productions.shoppinglist.R
import com.kanush_productions.shoppinglist.databinding.NoteListItemBinding
import com.kanush_productions.shoppinglist.entities.NoteItems
import com.kanush_productions.shoppinglist.utils.HtmlManager
import com.kanush_productions.shoppinglist.utils.TimeManager


class NoteAdapter(private val listener: Listener, private val defPref: SharedPreferences) : ListAdapter<NoteItems, NoteAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position), listener, defPref)
    }

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val bind = NoteListItemBinding.bind(view)
        fun setData(note: NoteItems, listener: Listener, defPref: SharedPreferences) = with(bind) {
            tvTitle.text = note.title
            tvDescription.text = HtmlManager.getFromHtml(note.content).trim()
            tvTime.text = TimeManager.getTimeFormat(note.time, defPref)
            itemView.setOnClickListener{
                listener.onClickItem(note)
            }
            imDelete.setOnClickListener {
                listener.deleteItem(note.id!!)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.note_list_item, parent, false)
                )
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<NoteItems>() {
        override fun areItemsTheSame(oldItem: NoteItems, newItem: NoteItems): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteItems, newItem: NoteItems): Boolean {
            return oldItem == newItem
        }

    }
    interface Listener{
        fun deleteItem(id: Int)
        fun onClickItem(note: NoteItems)
    }



}