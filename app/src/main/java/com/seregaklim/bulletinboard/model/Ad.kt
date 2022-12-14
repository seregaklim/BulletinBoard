package com.seregaklim.bulletinboard.model

import java.io.Serializable

data class Ad(
    val country: String? = null,
    val city: String? = null,
    val tel: String? = null,
    val index: String? = null,
    val withSent: String? = null,
    val category: String? = null,
    val title: String? = null,
    val price: String? = null,
    val description: String? = null,
    val email: String? = null,
    //картинки
    var mainImage: String = "empty",
    val image2: String = "empty",
    val image3: String = "empty",
    //генерируем ключ
    val key: String? = null,
    // юзер индификатор
    val uid: String? = null,
    val time: String = "0",
    //избранные(лайк)
    var isFav: Boolean = false,
    var favCounter: String = "0",
    //*****счетчики********
    var viewsCounter: String = "0",
    var emailCounter: String = "0",
    var callsCounter: String = "0",
    //*********************



    // класс Serializable превращает в байты (для передачи Intenta)
    ):Serializable
