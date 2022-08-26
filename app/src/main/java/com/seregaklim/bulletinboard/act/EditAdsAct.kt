package com.seregaklim.bulletinboard.act

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.seregaklim.bulletinboard.MainActivity
import com.seregaklim.bulletinboard.adapters.ImageAdapter
import com.seregaklim.bulletinboard.model.Ad
import com.seregaklim.bulletinboard.model.DbManager
import com.seregaklim.bulletinboard.databinding.ActivityEditAdsBinding
import com.seregaklim.bulletinboard.dialogs.DialogSpinnerHelper
import com.seregaklim.bulletinboard.frag.FragmentCloseInterface
import com.seregaklim.bulletinboard.frag.ImageListFrag
import com.seregaklim.bulletinboard.utils.CityHelper
import com.seregaklim.bulletinboard.utils.ImagePicker


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {
    //если фрагмент не создавался
    var chooseImageFrag : ImageListFrag? = null
    lateinit var binding: ActivityEditAdsBinding
    private  val dialog= DialogSpinnerHelper()
    lateinit var imageAdapter : ImageAdapter
    val imagePicker = ImagePicker()
    private val dbManager = DbManager()
    //переменная картики, которой хлтим изменить
    var editImagePos = 0
   //редактирование
    private var isEditState = false
    private var ad:Ad?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        checkEditState()
    }

    //функция проверки редактирования объявления
    private fun  checkEditState() {

        isEditState =isEditState()

        if (isEditState) {

            ad = intent.getSerializableExtra(MainActivity.ADS_DATA) as Ad

          if (ad !=null)  fillViews(ad!!)

        }
    }

    //проверяет состояние, зашли для создания нового объявления или редактирования
  private   fun isEditState():Boolean{
return intent.getBooleanExtra(MainActivity.EDIT_STATE,false)
    }

    //заполняем при редактировании объявления
  private   fun fillViews(ad: Ad)= with(binding){

        tvCountry.text=ad.country
        tvCity.text=ad.city
        editTel.setText(ad.tel)
        edIndex.setText(ad.index)
        checkBoxWithSend.isChecked=ad.withSent.toBoolean()
        tvCat.text=ad.category
        edTitle.setText(ad.title)
        edPrice.setText(ad.price)
        edDescription.setText(ad.description)

    }




    fun init(){
        imageAdapter = ImageAdapter()
        binding.vpImages.adapter = imageAdapter


    }

    //OnClicks
    // кнопка -выбираем страну в DialogSpinnerHelper
    fun onClickSelectCountry(view: View){
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, binding.tvCountry)
        if(binding.tvCity.text.toString() != getString(com.seregaklim.bulletinboard.R.string.select_city)){
            binding.tvCity.text = getString(com.seregaklim.bulletinboard.R.string.select_city)
        }
    }
    // кнопка -выбираем город в DialogSpinnerHelper
    fun onClickSelectCity(view: View){
        val selectedCountry = binding.tvCountry.text.toString()
        if(selectedCountry != getString(com.seregaklim.bulletinboard.R.string.select_country)){
            val listCity = CityHelper.getAllCities(selectedCountry, this)
            dialog.showSpinnerDialog(this, listCity, binding.tvCity)
        } else {
            Toast.makeText(this, "No country selected", Toast.LENGTH_LONG).show()
        }
    }
    // кнопка -выбираем категорию объявления
    fun onClickSelectCat(view: View){
        val listCity = resources.getStringArray(com.seregaklim.bulletinboard.R.array.category).toMutableList() as ArrayList
        dialog.showSpinnerDialog(this, listCity, binding.tvCat)
    }


    //запускаем картинку
    fun onClickGetImages(view: View){
        //если нет фото
        if(imageAdapter.mainArray.size == 0){
            imagePicker.getMultiImages(this, 3 )
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
    fun  openChooseImageFrag (newList:ArrayList<Uri>?){
        chooseImageFrag= ImageListFrag(this)
       if (newList !=null) chooseImageFrag?.resizeSelectedImages(newList,true,this)
        binding.scroolViewMain.visibility=View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(com.seregaklim.bulletinboard.R.id.place_holder,chooseImageFrag!! )
        //чтобы эти изминения применились
        fm.commit()
    }

    //возвращаем заполненноее объявление
    private fun fillAd():Ad {
        val ad: Ad
        binding.apply {
            ad = Ad(tvCountry.text.toString(),
                tvCity.text.toString(),
                editTel.text.toString(),
                edIndex.text.toString(),
                checkBoxWithSend.isChecked.toString(),
                tvCat.text.toString(),
                edTitle.text.toString(),
                edPrice.text.toString(),
                edDescription.text.toString(),
                //генерируем уникальный ключ
                dbManager.db.push().key,
                // юзер индификатор
                dbManager.auth.uid
            )
        }
        return ad
    }

    //публикуем
    fun onClickPublish(view: View){
        val adEdit = fillAd()

        //если редактирование
        if(isEditState) {

            dbManager.publishAd(adEdit.copy(key = ad?.key),onPublishFinish(),this)
        }else {

            dbManager.publishAd(adEdit,onPublishFinish(),this)


        }
    }

    //окончание загрузки на сервер
    private fun onPublishFinish():DbManager.FinishWorkListener{
        return object :DbManager.FinishWorkListener{
            override fun onFinish() {
                //закрываем активити
                finish()
            }

        }
    }
}









