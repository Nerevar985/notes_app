package com.kanush_productions.shoppinglist.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.kanush_productions.shoppinglist.R
import com.kanush_productions.shoppinglist.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var bind: ActivitySettingsBinding
    private lateinit var defPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        bind = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val title = getString(R.string.settings)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) supportFragmentManager.beginTransaction().replace(R.id.place_holder_for_settings, SettingsFragment()).commit()
    }

    private fun getSelectedTheme():Int{
        return if (defPref.getString("theme_key", "Light") == "Light") {
            R.style.MyMainStyle
        } else {
            R.style.MyMainStyleDark
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}