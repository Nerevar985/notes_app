package com.kanush_productions.shoppinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Shopping_list_items")
data class ShopListItem(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "item_info") val itemInfo: String?,
    @ColumnInfo(name = "itemChecked") val itemChecked: Boolean = false,
    @ColumnInfo(name = "list_id") val listID: Int,
    @ColumnInfo(name = "item_type") val itemType: Int = 0
)
