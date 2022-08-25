package com.seregaklim.bulletinboard.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.seregaklim.bulletinboard.model.Ad
import com.seregaklim.bulletinboard.model.DbManager

class FirebaseViewModel: ViewModel() {
    private val dbManager = DbManager()

    //класс следящий за всеси  изменениями , когда нужно обновить View
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()

      //загрузка всех объявлений
      fun loadAllAds(){
        dbManager.getAllAds(object :DbManager.ReadDataCallback{

            override fun readData(list: ArrayList<Ad>) {

                liveAdsData.value =list
            }
        })

    }
    //загрузка всех моих объявлений  (по индификатору)
    fun loadMyAds(){
        dbManager.getMyAds(object :DbManager.ReadDataCallback{

            override fun readData(list: ArrayList<Ad>) {

                liveAdsData.value =list
            }
        })

    }

    //количество просмотров
    fun adViewed(ad: Ad){
        dbManager.adViewed(ad)
    }

    //удаляем
    fun deleteItem(ad: Ad){
        dbManager.deleteAd(ad, object: DbManager.FinishWorkListener{
            override fun onFinish() {
             //берем старый список
               val updatedList = liveAdsData.value
               updatedList?.remove(ad)
               liveAdsData.postValue(updatedList!!)
            }

        })
    }

}