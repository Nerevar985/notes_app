package com.kanush_productions.shoppinglist.utils

import android.content.Intent
import com.kanush_productions.shoppinglist.entities.ShopListItem
import java.lang.StringBuilder

object ShareHelper {
    fun shareShoplist(shopList: List<ShopListItem>, listName: String): Intent{
        val intent = Intent(Intent.ACTION_SEND)
        intent.type ="text/plane"
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, makeShareText(shopList, listName))
        }
        return intent
    }
    private fun makeShareText(shopList: List<ShopListItem>, listName: String): String {
        val sBuilder = StringBuilder()
        sBuilder.append("<<$listName>>")
        sBuilder.append("\n")
        var counter = 0
        shopList.forEach() {
            if (it.itemInfo == null) {
                sBuilder.append("${++counter} - ${it.name}")
                sBuilder.append("\n")
            } else {
                sBuilder.append("${++counter} - ${it.name} (${it.itemInfo})")
                sBuilder.append("\n")
            }

        }
        return sBuilder.toString()
    }
}