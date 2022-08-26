package com.seregaklim.bulletinboard.frag

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.seregaklim.bulletinboard.databinding.ListImageFragBinding
import com.seregaklim.bulletinboard.databinding.SelectImageFragItemBinding

//для Banner, разгружаем ImageListFrag
open class BaseSelectImageFrag:Fragment() {
    lateinit var adView: AdView
   open lateinit var binding: ListImageFragBinding
    var interAd: InterstitialAd? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ListImageFragBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAds()

    }

    override fun onResume() {
        super.onResume()

        adView.resume()

    }

    override fun onPause() {
        super.onPause()

        adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    //загружаю рекламу
    private  fun initAds(){
        MobileAds.initialize(activity as Activity)
        val adRequest = AdRequest.Builder().build()

        adView.loadAd(adRequest)
    }
}