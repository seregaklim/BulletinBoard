package com.seregaklim.bulletinboard.act

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.adapters.ImageAdapter
import com.seregaklim.bulletinboard.databinding.ActivityDescriptionBinding
import com.seregaklim.bulletinboard.model.Ad
import com.seregaklim.bulletinboard.utils.ImageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DescriptionActivity : AppCompatActivity() {
    lateinit var  binding: ActivityDescriptionBinding
    lateinit var  adapter:ImageAdapter
  //  private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init(){
        adapter = ImageAdapter()
        binding.apply {
            viewPager.adapter =adapter
        }
        getIntentFromMainAct()
    }
    //получчаем обьяление
    private fun getIntentFromMainAct(){
        val ad = intent.getSerializableExtra("AD") as Ad
        updateUI(ad)
    }
    //обглвдяем интенрфейс картинкой и текстом
    private fun  updateUI(ad: Ad){
        fillImageArray(ad)
        fillTextViews(ad)
    }

    //заполнение текстовой части
    private fun fillTextViews(ad: Ad) = with(binding){
    tvTitle.text=ad.title
        tvDescription.text=ad.description
        tvPrice.text=ad.price
        tvTel.text=ad.tel
        tvCountry.text=ad.country
        tvCity.text=ad.city
        tvIndex.text=ad.index
        tvWithSent.text =isWithSent(ad.withSent.toBoolean())
    }

    private fun isWithSent(withSent:Boolean):String{
         return if (withSent) "Да" else "Нет"
    }

      //функция будет заполнять наш массив c cсылками
    private fun  fillImageArray(ad: Ad) {
        val  listUris = listOf(ad.mainImage,ad.image2,ad.image3)
        CoroutineScope(Dispatchers.Main).launch {
            //достаем все бипмапы
            val bitMapList =ImageManager.getBitmapFromUris(listUris)
            adapter.update(bitMapList as ArrayList<Bitmap>)
        }
    }

}