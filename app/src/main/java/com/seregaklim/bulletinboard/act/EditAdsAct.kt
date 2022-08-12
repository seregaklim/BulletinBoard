package com.seregaklim.bulletinboard.act

import android.R
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.Toast
import com.fxn.pix.Pix

import com.fxn.utility.PermUtil
import com.seregaklim.bulletinboard.databinding.ActivityEditAdsBinding

import com.seregaklim.bulletinboard.dialogs.DialogSpinnerHelper
import com.seregaklim.bulletinboard.frag.FragmentCloseInterface
import com.seregaklim.bulletinboard.frag.ImageListFrag
import com.seregaklim.bulletinboard.utils.CityHelper
import com.seregaklim.bulletinboard.utils.ImagePicker


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {

    lateinit var binding: ActivityEditAdsBinding
    private  val dialog= DialogSpinnerHelper()

   val imagePicker = ImagePicker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }
//запрос на обработку картинок
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_OK && resultCode == imagePicker.REQUEST_CODE_GET_IMAGES){
            if (data !=null){
                val returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                Log.d("Mylog","Image :${returnValue?.get(0)}")
                Log.d("Mylog","Image :${returnValue?.get(1)}")
                Log.d("Mylog","Image :${returnValue?.get(2)}")
            }
        }
    }

    //запрашиваем разрешение досиупа к фотографиям
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //   super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS->{
                if(grantResults.isNotEmpty() && grantResults [0]== PackageManager.PERMISSION_GRANTED){
                   // isImagesPermissionGranted =true
             //количество фотографий
               imagePicker.getImages(this,3)
                } else {
                 //   isImagesPermissionGranted =false
                    Toast.makeText(this,"Включите, разрешение!!",Toast.LENGTH_LONG).show()
                }
                return
            }

        }
    }



    private fun init(){

//val listCountry=CityHelper.getAllCountries(this)
//
//dialog.showSpinnerDialog(this,listCountry)
    }

    //OnClicks
    //  ищем страну в DialogSpinnerHelper
    fun onClickSelectCountry(view: View){
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, binding.tvCountry)
        if(binding.tvCity.text.toString() != getString(com.seregaklim.bulletinboard.R.string.select_city)){
            binding.tvCity.text = getString(com.seregaklim.bulletinboard.R.string.select_city)
        }
    }
    //ищем город в DialogSpinnerHelper
    fun onClickSelectCity(view: View){
        val selectedCountry = binding.tvCountry.text.toString()
        if(selectedCountry != getString(com.seregaklim.bulletinboard.R.string.select_country)){
            val listCity = CityHelper.getAllCities(selectedCountry, this)
            dialog.showSpinnerDialog(this, listCity, binding.tvCity)
        } else {
            Toast.makeText(this, "No country selected", Toast.LENGTH_LONG).show()
        }
    }

//    //запускаем картинку
//    fun onClickGetImages(view: View){
//          imagePicker.getImages(this)
//    }

    //запускаем картинку
    fun onClickGetImages(view: View){

       binding.scroolViewMain.visibility=View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(com.seregaklim.bulletinboard.R.id.place_holder, ImageListFrag(this))
        //чтобы эти изминения применились
        fm.commit()
    }

    override fun onFragClose() {
       binding.scroolViewMain.visibility=View.VISIBLE
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