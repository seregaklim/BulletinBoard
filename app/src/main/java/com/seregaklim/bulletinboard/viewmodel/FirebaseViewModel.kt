package com.seregaklim.bulletinboard.viewmodel


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
    //загрузка всех моих Избраных объявлений  (по индификатору)
    fun loadMyFavs(){
        dbManager.getMyFavs(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }


    //добавление в избранное (лайк, дизлайк)
    fun onFavClick(ad: Ad){
        dbManager.onFavClick(ad, object: DbManager.FinishWorkListener{
            override fun onFinish() {
                //берем старый список
                val updatedList = liveAdsData.value
                //узгаем на какой позиции находится элемент (на которое мы нажали)
                val pos = updatedList?.indexOf(ad)
                if(pos != -1){
                    pos?.let{
                        val favCounter = if(ad.isFav) ad.favCounter.toInt() - 1 else ad.favCounter.toInt() + 1
                        updatedList[pos] = updatedList[pos].copy(isFav = !ad.isFav,favCounter = favCounter.toString())

                    }
                }
                liveAdsData.postValue(updatedList!!)
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