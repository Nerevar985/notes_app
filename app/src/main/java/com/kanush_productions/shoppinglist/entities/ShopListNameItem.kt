package com.kanush_productions.shoppinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "shopping_list_names")
data class ShopListNameItem(
    @PrimaryKey(autoGenerate = true ) val id: Int?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "itemCount") val itemCount: Int,
    @ColumnInfo(name = "checkedItems") val checkedItems: Int,
    @ColumnInfo(name = "itemIDs") val itemIDs: String,
) : Serializable
