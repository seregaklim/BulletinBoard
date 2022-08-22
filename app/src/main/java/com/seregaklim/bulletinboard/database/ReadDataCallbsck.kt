package com.seregaklim.bulletinboard.database

import com.seregaklim.bulletinboard.data.Ad

interface ReadDataCallbsck {
  fun readData(list: List<Ad>)
}