package com.kanush_productions.shoppinglist.billing

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import com.google.android.gms.analytics.ecommerce.Product
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList

class BillingManager(val activity: AppCompatActivity) {
    private var billingClient: BillingClient? = null

    init {
        setupBillingClient()
    }

    private fun setupBillingClient(){
        billingClient = BillingClient.newBuilder(activity)
            .setListener(getPurchaseListener())
            .enablePendingPurchases()
            .build()
    }

    private fun savePref(isPurchased: Boolean){
        val pref = activity.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean(REMOVE_AD_KEY, isPurchased)
        editor.apply()
    }

    fun startConnection(){
        billingClient?.startConnection(object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                getItem()
            }

        })
    }

    fun closeConnection(){
        billingClient?.endConnection()
    }

    private fun getItem(){
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .setProductId(REMOVE_AD_ITEM)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        billingClient?.queryProductDetailsAsync(params) {
            bResult, list ->
            run {
                if(bResult.responseCode == BillingClient.BillingResponseCode.OK){
                    if (list != null){
                        if (list.isNotEmpty()){

                            val productDetailsParamsList =
                                listOf(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(list[0])
                                        .build()
                                )
                            val bFlowParams = BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList).build()
                            billingClient?.launchBillingFlow(activity, bFlowParams)

                        }
                    }
                }
            }
        }
    }

    private fun getPurchaseListener(): PurchasesUpdatedListener{
        return PurchasesUpdatedListener {
                bResult, list ->
            run {
                if (bResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    list?.get(0)?.let { nonConsumableItem(it) }
                }

            }
        }
    }

    private fun nonConsumableItem(purchase: Purchase){
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
            if(!purchase.isAcknowledged){
                val ackParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                billingClient?.acknowledgePurchase(ackParams){
                    if (it.responseCode == BillingClient.BillingResponseCode.OK) {
                        savePref(true)
                        Toast.makeText(activity, "Thanks for buying", Toast.LENGTH_LONG).show()
                    } else {
                        savePref(false)
                        Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    companion object{
        const val REMOVE_AD_ITEM = "remove_ad_item_id"
        const val MAIN_PREF = "main_pref"
        const val REMOVE_AD_KEY = "remove_ad_key"
    }

}