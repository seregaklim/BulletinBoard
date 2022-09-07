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
import com.seregaklim.bulletinboard.utils.FilterManager



class DbManager {

  val db = Firebase.database.getReference(MAIN_NODE)
  val auth = Firebase.auth
  val dbStorage = Firebase.storage.getReference(MAIN_NODE)

  companion object{
    //объявления
    const val AD_NODE ="ad"
    const val  AD_FILTER="/adFilter/time"
    const val FILTER_NODE ="adFilter"
    const val ALL_AD_CAT_FILTER="/adFilter/cat_time"
    //счетчик
    const val INFO_NODE ="info"
    const val MAIN_NODE ="main"
    //избранные
    const val FAVS_NODE ="favs"
    //лимит объявлений
    const val ADS_LIMIT = 2
  }


  //отправляем на сервер
  fun publishAd(ad: Ad, finishListener: FinishWorkListener) {
    //записываем  если
    if (auth.uid != null)
    //записываем в базу по сгенерированному ключу,ксли null, тогда "no key"
      db.child(ad.key ?: "no key")
        .child(auth.uid!!)
        //записываем в узел "ad"
        .child(AD_NODE)
        //addOnCompleteListener сообщает об окончании загрузки на сервер
        .setValue(ad).addOnCompleteListener {

          //передаем в adFilter
          val adFilter = FilterManager.createFilter(ad)
          //записываем в базу по сгенерированному ключу,ксли null, тогда "no key"
          db.child(ad.key ?: "no key")
            //записываем в узел "adFilter"
            .child(FILTER_NODE)
            //addOnCompleteListener сообщает об окончании загрузки на сервер
            .setValue(adFilter).addOnCompleteListener {
              if (it.isSuccessful) finishListener.onFinish()

            }
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


    //достаем объявления разные -первая страничка
    fun getAllAdsFirstPage(filter: String ,readDataCallback: ReadDataCallback?){
        //если фильтр пустой
        val query = if (filter.isEmpty()) {
            // "/ad/uid" равен моему индмфикатуру аккаунта auth.uid
            db.orderByChild(AD_FILTER)
                //указываем кол-во объяв.для загрузки по порциям  limitToLast -свежие сверху
                .limitToLast(ADS_LIMIT)
            //если запрос фильтра
        }else {
            getAllAdsByFilterFirstPage(filter)
        }
        //выдает все объявления с индификатором
        readDataFromDb(query, readDataCallback)
    }


    //достаем Отфильипрванные объявления ВСЕ -первая страничка
    fun getAllAdsByFilterFirstPage(tempFilter :String):Query{
       val orderBy=tempFilter.split("|")[0]
        val filter=tempFilter.split("|")[1]

        //фильтруем   "/ad/uid" узел категория ,где начинается
        return db.orderByChild("/adFilter/$orderBy").startAt(filter)
            //указываем , где заканчивается
            .endAt(filter + "\uf8ff")
            //указываем кол-во объяв.для загрузки по порциям limitToLast -свежие сверху
            .limitToLast(ADS_LIMIT)
    }

    //достаем Отфильипрванные объявления По КАТЕГОРИЯМ первая страничка
    fun getAllAdsFromCategoriaByFilterFirstPage(cat: String,tempFilter :String,):Query{
       //вначале указываем путь "cat_" и весь фильтр
        val orderBy= "cat_"  + tempFilter.split("|")[0]
       //добавляем категорию
        val filter=cat + "_"  +  tempFilter.split("|")[1]

        //фильтруем   "/ad/uid" узел категория ,где начинается
        return db.orderByChild("/adFilter/$orderBy").startAt(filter)
            //указываем , где заканчивается
            .endAt(filter + "\uf8ff")
            //указываем кол-во объяв.для загрузки по порциям limitToLast -свежие сверху
            .limitToLast(ADS_LIMIT)
    }



    //достаем объявления разные -для последующих страниц -берем последние элементы по времни(свежие)
    fun getAllAdsNextPage(time: String, filter: String, readDataCallback: ReadDataCallback?){
        //если фильтр пустой
        if(filter.isEmpty()){
            // "/ad/uid" равен моему индмфикатуру аккаунта auth.uid
            val query = db.orderByChild( "/adFilter/time").endBefore(time).limitToLast(ADS_LIMIT)
            readDataFromDb(query, readDataCallback)
            //если запрос фильтра
        } else {
            getAllAdsByFilterNextPage(filter, time, readDataCallback)
        }

    }

    //достаем Отфильипрванные объявления ВСЕ -по времени
    private fun getAllAdsByFilterNextPage(tempFilter: String, time: String, readDataCallback: ReadDataCallback?){
        val orderBy = tempFilter.split("|")[0]
        val filter = tempFilter.split("|")[1]

        //фильтруем   "/ad/uid" узел категория ,где начинается
        val query =  db.orderByChild( "/adFilter/$orderBy")
            .endBefore(filter + "_$time")
            //указываем кол-во объяв.для загрузки по порциям limitToLast -свежие сверху
            .limitToLast(ADS_LIMIT)

        readNextPageFromDb(query, filter, orderBy, readDataCallback)
    }



  //достаем объявления по категориям -первая страничка
  fun getAllAdsFromCategogiaFirstPage(cat :String,filter: String, readDataCallback: ReadDataCallback?){
    //фильтруем   ,если нет фильтра, то просто категория   "/ad/uid" узел категория ,где начинается
    val query =if (filter.isEmpty()){
        db.orderByChild(  ALL_AD_CAT_FILTER).startAt(cat)
            //указываем , где заканивается
            .endAt(cat + "_\uf8ff")
            //указываем кол-во объяв.для загрузки по порциям limitToLast -свежие сверху
            .limitToLast(ADS_LIMIT)
   //если есть фильтр
    }  else{
        getAllAdsFromCategoriaByFilterFirstPage(cat,filter)
    }
    //выдает все объявления с индификатором
    readDataFromDb(query, readDataCallback)
  }

    //достаем объявления по категориям -первая страничка - берем последние элементы по времни(свежие)
    fun getAllAdsFromCatNextPage(cat: String,time :String,filter: String, readDataCallback: ReadDataCallback?){
       if (filter.isEmpty()){
           //фильтруем   "/ad/uid" узел категория
           val query = db.orderByChild(  ALL_AD_CAT_FILTER)
               // фильтруем  по времени endBefore(time) (cat+time разделили , чтобы ставить фильтр)
               .endBefore(cat+ "_" + time)
               //указываем кол-во объяв.для загрузки по порциям  limitToLast -свежие сверху
               .limitToLast(ADS_LIMIT)
           //выдает все объявления с индификатором
           readDataFromDb(query, readDataCallback)

       } else{
           //фильтруем по времени и категории
           getAllAdsFromCategoriaByFilterNextPage(cat,time,filter,readDataCallback)
       }
    }

    fun getAllAdsFromCategoriaByFilterNextPage(cat: String,time: String, tempFilter :String,readDataCallback: ReadDataCallback?){
        //вначале указываем путь "cat_" и весь фильтр
        val orderBy= "cat_"  + tempFilter.split("|")[0]
        //добавляем категорию
        val filter=cat + "_"  +  tempFilter.split("|")[1]

        //фильтруем   "/ad/uid" узел категория ,где начинается
       val query= db.orderByChild("/adFilter/$orderBy")
            .endBefore(filter +"_"+time)
            //указываем кол-во объяв.для загрузки по порциям limitToLast -свежие сверху
            .limitToLast(ADS_LIMIT)

   //позволяет отсортировать не нужные объявления
        readNextPageFromDb(query,filter,orderBy,readDataCallback)
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
private  fun readDataFromDb(query: Query, readDataCallback: ReadDataCallback?){
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
          readDataCallback?.readData(adArray)
      }


      override fun onCancelled(error: DatabaseError) {


      }

    })

  }


     //позволяет отсортировать не нужные объявления
    private fun readNextPageFromDb(query: Query, filter: String, orderBy: String, readDataCallback: ReadDataCallback?){
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
                   //считываю фильтр child(orderBy)
                    val filterNodeValue = item.child(FILTER_NODE).child(orderBy).value.toString()
                   // Log.d("MyLog","Filter value : $filterNodeValue")

                    //добавляю полученную инфо (счетчик) в ad
                    ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
                    ad?.emailCounter = infoItem?.emailsCounter ?: "0"
                    ad?.callsCounter = infoItem?.callsCounter ?: "0"

                    //и есть фильр данные
                    if(ad != null && filterNodeValue.startsWith(filter))adArray.add(ad!!)
                    // Log.d("MyLog", "Data:$ad")

                }
                //передаем список в адаптер
                readDataCallback?.readData(adArray)
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