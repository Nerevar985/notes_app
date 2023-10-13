package com.kanush_productions.shoppinglist.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.preference.Preference
import androidx.preference.PreferenceManager

import com.yandex.mobile.ads.common.AdRequest
import com.kanush_productions.shoppinglist.R
import com.kanush_productions.shoppinglist.billing.BillingManager
import com.kanush_productions.shoppinglist.databinding.ActivityMainBinding
import com.kanush_productions.shoppinglist.dialogs.NewListDialog
import com.kanush_productions.shoppinglist.fragments.FragmentManager
import com.kanush_productions.shoppinglist.fragments.NoteFragment
import com.kanush_productions.shoppinglist.fragments.ShopListNamesFragment
import com.kanush_productions.shoppinglist.settings.SettingsActivity
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData

import com.yandex.mobile.ads.common.MobileAds

import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener



class MainActivity : AppCompatActivity(), NewListDialog.Listener {
    private lateinit var bind: ActivityMainBinding
    private var currentMenuItemId = R.id.shop_list
    private lateinit var defPref: SharedPreferences
    private var currentTheme = ""
    private var iAd: InterstitialAd? = null
    private lateinit var pref: SharedPreferences
    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        MobileAds.initialize(this){
            Log.d("MyTag", "Ads initialized")
        }

        currentTheme = defPref.getString("theme_key", "Light").toString()
        FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
        bottomNavListener()
        //loadInterAd()
    }

    private fun loadInterAd(){
        val request = AdRequest.Builder().build()
        iAd = InterstitialAd(this)
        iAd!!.setAdUnitId("R-M-DEMO-interstitial")
        iAd!!.loadAd(request)
        iAd!!.setInterstitialAdEventListener(object : InterstitialAdEventListener{
            override fun onAdLoaded() {
                Log.d("MyTag", "Ad loaded")
            }

            override fun onAdFailedToLoad(p0: AdRequestError) {
                Log.d("MyTag", "failed to load ad")
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }

            override fun onAdShown() {
            }

            override fun onAdDismissed() {

                iAd?.loadAd(request)
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }

            override fun onAdClicked() {
            }

            override fun onLeftApplication() {
            }

            override fun onReturnedToApplication() {
            }

            override fun onImpression(p0: ImpressionData?) {
            }
        })
    }

    private fun showInterAd(){

        if (iAd?.isLoaded == true && counter > 10) {
            counter = 0
            iAd!!.show()
        } else {
            Log.d("MyTag", "The interstitial ad wasn't ready yet.")
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
        }
    }

    private fun bottomNavListener() {
        bind.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    counter++
                    Log.d("MyTag", "$counter")
                        showInterAd()
                }

                R.id.notes -> {
                    counter++
                    Log.d("MyTag", "$counter")
                    currentMenuItemId = R.id.notes
                    supportActionBar?.title = getString(R.string.notes)
                    FragmentManager.setFragment(NoteFragment.newInstance(), this)
                }

                R.id.shop_list -> {
                    counter++
                    Log.d("MyTag", "$counter")
                    currentMenuItemId = R.id.shop_list
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
                    supportActionBar?.title = getString(R.string.shopping_list)
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       if(item.itemId == R.id.menu_main_add) {
            FragmentManager.currentFragment?.onClickNew()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        bind.bottomNav.selectedItemId = currentMenuItemId
        if (defPref.getString("theme_key", "Light") != currentTheme) recreate()
    }

    private fun getSelectedTheme():Int{
        return if (defPref.getString("theme_key", "Light") == "Light") {
            R.style.MyMainStyle
        } else {
            R.style.MyMainStyleDark
        }
    }

    override fun onClick(name: String) {
    }


}