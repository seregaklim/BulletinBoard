package com.seregaklim.bulletinboard.utils


import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*


@Suppress("DEPRECATION")
class BillingManager(val act: AppCompatActivity) {
    private var billingClient: BillingClient? = null

    init {
        setUpBillingClient()
    }
    //встроенные покупки
    private fun setUpBillingClient(){                //слушатель покупок
        billingClient = BillingClient.newBuilder(act).setListener(getPurchaseListener())
            .enablePendingPurchases().build()
    }
    //сохраняем покупку
    private fun savePurchase(isPurchased: Boolean){
        //сохраняем в таблице
        val pref = act.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE)
        //создаем редактор, с помощью которой записываем в таблицу
        val editor = pref.edit()
       //создаем  ключевое слово под которым сохраняем и самое значение
        editor.putBoolean(REMOVE_ADS_PREF, isPurchased)
        editor.apply()
    }
     //подключает плеймаркет
    fun startConnection(){
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(result: BillingResult) {
               //запуск диалога оплаты
                getItem()
            }

        })
    }
     //создаем свой продукт для оплаты
    private fun getItem() {
       //список покуаок (даже если один продукт)
        val skuList = ArrayList<String>()
        //добавляем свой продукт
        skuList.add(REMOVE_ADS)
       //игформация о покупке
        val skuDetails = SkuDetailsParams.newBuilder()
         //передаем все покупки, которые есть и что за покупка (встроенная покупка или подписка)
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
       //отправляем наш запрос о покупках
        billingClient?.querySkuDetailsAsync(skuDetails.build()){
                result, list ->
            run {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    if(!list.isNullOrEmpty()){
                        val billingFlowParams = BillingFlowParams
                            .newBuilder().setSkuDetails(list[0]).build()

                        billingClient?.launchBillingFlow(act, billingFlowParams)
                    }
                }
            }
        }
    }
    //подтверждение покупки, инф. о подтверждении покупки
    private fun nonConsumableItem(purchase: Purchase){
           //если продукт куплен
        if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
            if(!purchase.isAcknowledged){
                val acParams = AcknowledgePurchaseParams.newBuilder()
                    //индификатор покупки
                    .setPurchaseToken(purchase.purchaseToken).build()
                billingClient?.acknowledgePurchase(acParams){ result ->
                     //если подтвержден
                    if(result.responseCode == BillingClient.BillingResponseCode.OK){
                       //сохраняем
                        savePurchase(true)
                        Toast.makeText(act, "Спасибо за покупку!", Toast.LENGTH_SHORT).show()
                    } else {
                        savePurchase(false)
                        Toast.makeText(act, "Не удалось реализовать покупку покупку!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    //слушатель, который слушает, что происходит с покупками
    private fun getPurchaseListener(): PurchasesUpdatedListener{
        return PurchasesUpdatedListener{
              //лист с покупками
                result, list ->
            run {

                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    //если одна покупка 0 (нулевая позиция) -подтверждение покупки
                    list?.get(0)?.let{ nonConsumableItem(it) }
                }
            }
        }
    }

    fun closeConnection(){
        billingClient?.endConnection()
    }

    companion object{
        const val REMOVE_ADS = "remove_ads"
        //название таблицы где сохряняю
        const val MAIN_PREF = "main_pref"
        //название элемента, где сохряняю
        const val REMOVE_ADS_PREF = "remove_ads_pref"
    }
}