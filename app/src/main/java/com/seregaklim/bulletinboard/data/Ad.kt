package com.seregaklim.bulletinboard.data

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
    //генерируем ключ
    val key: String? = null,
)
