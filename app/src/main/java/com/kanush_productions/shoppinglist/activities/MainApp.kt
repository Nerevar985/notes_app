package com.kanush_productions.shoppinglist.activities

import android.app.Application
import android.util.Log

import com.kanush_productions.shoppinglist.database.MainDataBase

class MainApp : Application() {
    val dataBase by lazy { MainDataBase.getDataBase(this) }
    override fun onCreate() {
        super.onCreate()
//        MobileAds.initialize(this) {
//            Log.d("MyTag", "Ads initialized")
//        }
    }
}