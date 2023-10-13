package com.kanush_productions.shoppinglist.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.kanush_productions.shoppinglist.R
import com.kanush_productions.shoppinglist.databinding.NewListDialogBinding

object NewListDialog {
    fun showDialog(context: Context, listener: Listener, name: String)    {
        var dialog: AlertDialog? = null

        val builder = AlertDialog.Builder(context)

        val binding = NewListDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            etNewListName.setText(name)
            if(name.isNotEmpty()) btnCreateNewList.text = context.getString(R.string.button_update)
            btnCreateNewList.setOnClickListener{
                val listName = etNewListName.text.toString()
                if (listName.isNotEmpty()) {
                    listener.onClick(listName)
                }
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    interface Listener{
        fun onClick(name:String)
    }
}