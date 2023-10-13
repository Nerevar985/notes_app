package com.kanush_productions.shoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.kanush_productions.shoppinglist.databinding.EditListItemDialogBinding
import com.kanush_productions.shoppinglist.databinding.NewListDialogBinding
import com.kanush_productions.shoppinglist.entities.ShopListItem

object EditListDialog {
    fun showDialog(context: Context, shopListItem: ShopListItem, listener: Listener)    {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            etName.setText(shopListItem.name)
            etInfo.setText(shopListItem.itemInfo)
            if (shopListItem.itemType == 1) etInfo.visibility = View.GONE
            btnDone.setOnClickListener {
                if(etName.text.toString().isNotEmpty()) {
                    val itemInfo = if(etInfo.text.toString().isEmpty()) null else etInfo.text.toString()
                    listener.onClick(shopListItem.copy(name = etName.text.toString(), itemInfo = itemInfo))
                }
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    interface Listener{
        fun onClick(shopListItem: ShopListItem)
    }
}