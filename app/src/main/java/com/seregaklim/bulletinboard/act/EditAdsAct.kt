package com.seregaklim.bulletinboard.act


import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

import android.widget.Toast
import com.fxn.pix.Pix

import com.fxn.utility.PermUtil
import com.seregaklim.bulletinboard.adapters.ImageAdapter
import com.seregaklim.bulletinboard.databinding.ActivityEditAdsBinding

import com.seregaklim.bulletinboard.dialogs.DialogSpinnerHelper
import com.seregaklim.bulletinboard.frag.FragmentCloseInterface
import com.seregaklim.bulletinboard.frag.ImageListFrag
import com.seregaklim.bulletinboard.utils.CityHelper
import com.seregaklim.bulletinboard.utils.ImageManager
import com.seregaklim.bulletinboard.utils.ImagePicker


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    //если фрагмент не создавался
    var chooseImageFrag : ImageListFrag? = null
    lateinit var binding: ActivityEditAdsBinding
    private  val dialog= DialogSpinnerHelper()
    lateinit var imageAdapter : ImageAdapter
    val imagePicker = ImagePicker()
    //переменная картики, которой хлтим изменить
    var editImagePos = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }
    //запрос на обработку картинок
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      //  super.onActivityResult(requestCode, resultCode, data)

        //функция, позволяющая взять картинки и сделать фото
        imagePicker.showSelectegImages(resultCode,requestCode,data,this)

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
                    imagePicker.getImages(this,3,imagePicker.REQUEST_CODE_GET_IMAGES)
                } else {
                    //   isImagesPermissionGranted =false
                    Toast.makeText(this,"Включите, разрешение!!",Toast.LENGTH_LONG).show()


                    return
                }

            }
        }
    }


    fun init(){
        imageAdapter = ImageAdapter()
        binding.vpImages.adapter = imageAdapter
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


    //запускаем картинку
    fun onClickGetImages(view: View){
        //если нет фото
        if(imageAdapter.mainArray.size == 0){
            imagePicker.getImages(this, 3,imagePicker.REQUEST_CODE_GET_IMAGES)
//       если есть
        } else {
            openChooseImageFrag(null)

            chooseImageFrag?.updateAdapterFromEdit(imageAdapter.mainArray)
        }

    }

    //при закрытии фрагмента, получаем данные с ImageAdapter
    override fun onFragClose(list:ArrayList<Bitmap>) {
        binding.scroolViewMain.visibility=View.VISIBLE
        imageAdapter.update(list)
        //очищаем данные фрагмента
        chooseImageFrag=null

    }

    //открывает фрагмент
    fun  openChooseImageFrag (newList:ArrayList<String>?){
        chooseImageFrag= ImageListFrag(this,newList)
        binding.scroolViewMain.visibility=View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(com.seregaklim.bulletinboard.R.id.place_holder,chooseImageFrag!! )
        //чтобы эти изминения применились
        fm.commit()
    }


    //запускаем картинку
    //     fun onClickGetImages(view: View){

//       binding.scroolViewMafin.visibility=View.GONE
//        val fm = supportFragmentManager.beginTransaction()
//        fm.replace(com.seregaklim.bulletinboard.R.id.place_holder, ImageListFrag(this))
//        //чтобы эти изминения применились
//        fm.commit()
    //      }

}









