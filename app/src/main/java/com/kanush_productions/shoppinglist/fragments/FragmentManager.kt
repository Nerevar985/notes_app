package com.kanush_productions.shoppinglist.fragments

import androidx.appcompat.app.AppCompatActivity
import com.kanush_productions.shoppinglist.R

object FragmentManager {
    var currentFragment: BaseFragment? = null

    fun setFragment(newFragment: BaseFragment, activity: AppCompatActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_holder, newFragment)
        transaction.commit()
        currentFragment = newFragment
    }
}