package com.seregaklim.bulletinboard.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class DbManager {

  val db = Firebase.database.getReference("main")
  val auth = Firebase.auth

  //отправляем на сервер
  fun publishAd(ad: Ad) {
    //записываем  если
    if (auth.uid !=null)
    //записываем в базу по сгенерированному ключу,ксли null, тогда "no key"
      db.child(ad.key?:"no key")
        .child(auth.uid!!)
        //записываем в узел "ad"
        .child("ad")
        .setValue(ad)

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
          val ad=item.children.iterator().next().child("ad").getValue(Ad::class.java)
          if (ad!=null) adArray.add(ad)
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
}