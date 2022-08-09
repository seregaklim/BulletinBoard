package com.seregaklim.bulletinboard.act

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.seregaklim.bulletinboard.databinding.ActivityEditAdsBinding
import com.seregaklim.bulletinboard.dialogs.DialogSpinnerHelper
import com.seregaklim.bulletinboard.utils.CityHelper


class EditAdsAct : AppCompatActivity() {

    lateinit var binding: ActivityEditAdsBinding
private  val dialog= DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

private fun init(){
//
//val listCountry=CityHelper.getAllCountries(this)
//
//dialog.showSpinnerDialog(this,listCountry)
}

    //OnClicks
    fun onClickSelectCountry(view: View){
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, binding.tvCountry)
        if(binding.tvCity.text.toString() != getString(com.seregaklim.bulletinboard.R.string.select_city)){
            binding.tvCity.text = getString(com.seregaklim.bulletinboard.R.string.select_city)
        }
    }

    fun onClickSelectCity(view: View){
        val selectedCountry = binding.tvCountry.text.toString()
        if(selectedCountry != getString(com.seregaklim.bulletinboard.R.string.select_country)){
            val listCity = CityHelper.getAllCities(selectedCountry, this)
            dialog.showSpinnerDialog(this, listCity, binding.tvCity)
        } else {
            Toast.makeText(this, "No country selected", Toast.LENGTH_LONG).show()
        }
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