package com.seregaklim.bulletinboard.model

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class DbManager {

  val db = Firebase.database.getReference(MAIN_NODE)
  val auth = Firebase.auth

  companion object{
    const val AD_NODE = "ad"
    const val FILTER_NODE = "adFilter"
    const val INFO_NODE = "info"
    const val MAIN_NODE = "main"
    const val FAVS_NODE = "favs"
    const val ADS_LIMIT = 2
  }


  //отправляем на сервер
  fun publishAd(ad: Ad,finishListener :FinishWorkListener,act:Context) {
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

        }else{  Toast.makeText( act,"Servers try again later", Toast.LENGTH_LONG).show()
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


  //достаем объявления все подряд
  fun getAllAds(readDataCallback: ReadDataCallback?){
    //фильтруем  "/ad/uid" равен моему индмфикатуру аккаунта auth.uid
    val query = db.orderByChild(auth.uid  + "/ad/price")
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
          //cчитываю счетчик (info)
          val infoItem =item.child(INFO_NODE).getValue(InfoItem::class.java)

          //добавляю полученную инфо (счетчик) в ad
          ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
          ad?.emailCounter = infoItem?.emailsCounter ?: "0"
          ad?.callsCounter = infoItem?.callsCounter ?: "0"

          if (ad!=null) adArray.add(ad!!)
          // Log.d("MyLog", "Data:$ad")

        }
        //передаем список
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