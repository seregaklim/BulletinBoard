package com.seregaklim.bulletinboard.database

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.seregaklim.bulletinboard.data.Ad


class DbManager (val readDataCallbsck: ReadDataCallbsck?) {

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

  //достаем данные с сервера
  fun readDataFromDb(){
    //загружает один раз
    db.addListenerForSingleValueEvent(object : ValueEventListener{

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

}