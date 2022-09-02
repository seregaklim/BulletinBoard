package com.seregaklim.bulletinboard.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.seregaklim.bulletinboard.act.EditAdsAct


class DbManager {

  val db = Firebase.database.getReference(MAIN_NODE)
  val auth = Firebase.auth
  val dbStorage = Firebase.storage.getReference(MAIN_NODE)

  companion object{
    //объявления
    const val AD_NODE = "ad"
    const val FILTER_NODE = "adFilter"
    //счетчик
    const val INFO_NODE = "info"
    const val MAIN_NODE = "main"
    //избранные
    const val FAVS_NODE = "favs"
    //лимит объявлений
    const val ADS_LIMIT = 2
  }


  //отправляем на сервер
  fun publishAd(ad: Ad,finishListener :FinishWorkListener,) {
    //записываем  если
    if (auth.uid !=null)
    //записываем в базу по сгенерированному ключу,ксли null, тогда "no key"
      db.child(ad.key?:"no key")
        .child(auth.uid!!)
        //записываем в узел "ad"
        .child(AD_NODE)
        //addOnCompleteListener сообщает об окончании загрузки на сервер
        .setValue(ad).addOnCompleteListener {
          if (it.isSuccessful) finishListener.onFinish()

      //  }else{
    }
  }
  // счетчик просмотров
  fun adViewed(ad: Ad){
    //количество просмотров
    var counter =  ad.viewsCounter.toInt()
    counter++

    if(auth.uid != null)db.child(ad.key ?: "no_key")
      .child(INFO_NODE).setValue(InfoItem(counter.toString(), ad.emailCounter, ad.callsCounter))
  }

  //удаляем
  fun deleteAd(ad: Ad, finishListener: FinishWorkListener) {
    if (ad.key == null || ad.uid == null) return

    //находим по ключу
    db.child(ad.key).child(ad.uid).removeValue().addOnCompleteListener {
      if (it.isSuccessful) finishListener.onFinish()

    }
  }
  //проверяем лайк или дизлак(избранное)
  fun onFavClick(ad: Ad, listener: FinishWorkListener){
    if(ad.isFav){
      removeFromFavs(ad, listener)
    } else {
      addToFavs(ad, listener)
      Log.d("MyLog","Click favs to add")
    }
  }

 //избранные обьявления добавления (дизлайк)
  private fun addToFavs(ad: Ad, listener: FinishWorkListener){
    ad.key?.let {
     //мой индификатор
      auth.uid?.let {
          uid ->
        db.child(it)
          .child(FAVS_NODE)
          .child(uid)
          //в узел записываю свой инд (setValue(uid))
          .setValue(uid).addOnCompleteListener {

          if(it.isSuccessful) listener.onFinish()
        }
      }
    }
  }

  //избранные обьявления удаления (лайк)
  private fun removeFromFavs (ad: Ad, listener: FinishWorkListener){
    ad.key?.let {
      //мой индификатор
      auth.uid?.let {
          uid ->
        db.child(it)
          .child(FAVS_NODE)
          .child(uid)
          //в узле удаляю  свой инд (setValue(uid))
          .removeValue().addOnCompleteListener {

            if(it.isSuccessful) listener.onFinish()
          }
      }
    }
  }



  //достаем объявления все подряд
  fun getAllAds(lastTime :String ,readDataCallback: ReadDataCallback?){
    //фильтруем  по времени "/ad/uid" равен моему индмфикатуру аккаунта auth.uid
    val query = db.orderByChild(auth.uid  + "/ad/time").startAfter(lastTime)
      //указываем кол-во объяв.для загрузки по порциям (привязан к времени)
      .limitToFirst(ADS_LIMIT)
    //выдает все объявления с индификатором
    readDataFromDb(query, readDataCallback)
  }


  ///достаем избранные объявления по моему индификатору
  fun getMyFavs(readDataCallback: ReadDataCallback?){
    //фильтруем  "/ad/uid" равен моему индмфикатуру аккаунта auth.uid
    val query = db.orderByChild( "/favs/${auth.uid}").equalTo(auth.uid)
    //выдает все объявления с индификатором
    readDataFromDb(query, readDataCallback)
  }


  ///достаем объявления по моему индификатору
  fun getMyAds(readDataCallback: ReadDataCallback?){
       //фильтруем  "/ad/uid" равен моему индмфикатуру аккаунта auth.uid
    val query = db.orderByChild(auth.uid  + "/ad/uid").equalTo(auth.uid)
    //выдает все объявления с индификатором
    readDataFromDb(query, readDataCallback)
  }

  //достаем данные с сервера (query: Query -специальный класс умеющий фильтровать)
private  fun readDataFromDb(query: Query, readDataCallbsck: ReadDataCallback?){
    //загружает один раз
    query.addListenerForSingleValueEvent(object : ValueEventListener{

      override fun onDataChange(snapshot: DataSnapshot) {
        val adArray=ArrayList<Ad>()
        //c помощью цикла достаю объявления
        for (item in snapshot.children) {
          var ad:Ad ?=null
          //c помощью цикла из объявления достаю узлы
          item.children.forEach{
            //cчитываю обьяыления
            if (ad==null) ad = it.child(AD_NODE).getValue(Ad::class.java)

          }

          //считываю счетчик лайков
          val favCounter=item.child(FAVS_NODE).childrenCount
        //получаю свой индификатор
          val isFav = auth.uid?.let { item.child(FAVS_NODE).child(it).getValue(String::class.java) }
          //проверяю в избранном или нет
          ad?.isFav = isFav !=null
          ad?.favCounter = favCounter.toString()


          //cчитываю счетчик (info)
          val infoItem =item.child(INFO_NODE).getValue(InfoItem::class.java)
          //добавляю полученную инфо (счетчик) в ad
          ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
          ad?.emailCounter = infoItem?.emailsCounter ?: "0"
          ad?.callsCounter = infoItem?.callsCounter ?: "0"

          if (ad!=null) adArray.add(ad!!)
          // Log.d("MyLog", "Data:$ad")

        }
        //передаем список в адаптер
        readDataCallbsck?.readData(adArray)
      }


      override fun onCancelled(error: DatabaseError) {


      }

    })

  }

  interface ReadDataCallback {
    fun readData(list: ArrayList<Ad>)
  }

  interface FinishWorkListener{
   //загрузку закончили
    fun onFinish()
  }

}