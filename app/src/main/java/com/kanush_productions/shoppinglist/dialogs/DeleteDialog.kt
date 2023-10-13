package com.kanush_productions.shoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.kanush_productions.shoppinglist.databinding.DeleteDialogBinding
import com.kanush_productions.shoppinglist.databinding.NewListDialogBinding

object DeleteDialog {
    fun showDialog(context: Context, listener: Listener)    {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = DeleteDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            btnConfirmDelete.setOnClickListener{
                listener.onClick()
                dialog?.dismiss()
            }
            btnCancelDelete.setOnClickListener {
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
    interface Listener{
        fun onClick()
    }
}