package com.seregaklim.bulletinboard.database

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class DbManager {

  val db = Firebase.database.reference

    fun publishAd() {
db.setValue("чарвытавтысптарваптататав")



}
}