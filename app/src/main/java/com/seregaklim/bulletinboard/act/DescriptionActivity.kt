package com.seregaklim.bulletinboard.act

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
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
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.fbTel.setOnClickListener { call() }
        binding.fbEmail.setOnClickListener { sendEmail() }
    }

    private fun init(){
        adapter = ImageAdapter()
        binding.apply {
            viewPager.adapter =adapter
        }
        getIntentFromMainAct()
        imageChangeCounter()
    }
    //получчаем обьяление
    private fun getIntentFromMainAct(){
       ad = intent.getSerializableExtra("AD") as Ad
        if(ad != null)updateUI(ad!!)

    }
    //обглвдяем интенрфейс картинкой и текстом
    private fun  updateUI(ad: Ad){
     ImageManager.fillImageArray(ad,adapter)
        fillTextViews(ad)
    }

    //заполнение текстовой части
    private fun fillTextViews(ad: Ad) = with(binding){
    tvTitle.text=ad.title
        tvDescription.text=ad.description
        tvEmail.text=ad.email
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


    //звонить
    private fun call(){
        val callUri = "tel:${ad?.tel}"
       //для звонка
        val iCall = Intent(Intent.ACTION_DIAL)
        iCall.data = callUri.toUri()
        startActivity(iCall)
    }
    // отправляем сообщение по мылу
    private fun sendEmail(){
        //для отправки
        val iSendEmail = Intent(Intent.ACTION_SEND)
        iSendEmail.type = "message/rfc822"
        iSendEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ad?.email))
            putExtra(Intent.EXTRA_SUBJECT, "Объявление")
            putExtra(Intent.EXTRA_TEXT, "Меня интересует ваше объявление!")
        }
        try{
           //выбираем чем можно отправить
            startActivity(Intent.createChooser(iSendEmail, "Открыть с"))
        }catch (e: ActivityNotFoundException){

        }
    }

   //счетчик фотографий
    private fun imageChangeCounter(){
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position + 1}/${binding.viewPager.adapter?.itemCount}"
                binding.tvImageCounter.text = imageCounter
            }
        })
    }
}