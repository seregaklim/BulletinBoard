package com.seregaklim.bulletinboard.act

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.seregaklim.bulletinboard.databinding.ActivityEditAdsBinding
import com.seregaklim.bulletinboard.utils.CityHelper


class EditAdsAct : AppCompatActivity() {

    lateinit var binding: ActivityEditAdsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    val adapter =ArrayAdapter(this,android.R.layout.simple_spinner_item,CityHelper.getAllCountries(this))
   adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
 //  binding.spCountry.adapter=adapter

    }
}












//class EditAdsAct : AppCompatActivity() {
//    lateinit var binding: ActivityEditAdsBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityEditAdsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
////    val adapter =ArrayAdapter(this,android.R.layout.simple_spinner_item,CityHelper.getAllCountries(this))
////   adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
////   binding.spCountry.adapter=adapter
//
//    }
//}