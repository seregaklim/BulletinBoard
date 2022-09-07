package com.seregaklim.bulletinboard.frag

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.seregaklim.bulletinboard.databinding.ListImageFragBinding
import com.seregaklim.bulletinboard.databinding.SelectImageFragItemBinding
import com.seregaklim.bulletinboard.utils.BillingManager

//для Banner, разгружаем ImageListFrag

open class BaseAdsFrag:Fragment(), InterAdsClose {
    lateinit var adView: AdView
    var  interAd :InterstitialAd? =null
    private var pref: SharedPreferences? = null
    //если пользователь отменил рекламу
    private var isPremiumUser = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //если отменил рекламу
        pref = activity?.getSharedPreferences(BillingManager.MAIN_PREF, AppCompatActivity.MODE_PRIVATE)
        isPremiumUser = pref?.getBoolean(BillingManager.REMOVE_ADS_PREF, false)!!
        //isPremiumUser = true
        if(!isPremiumUser){
            initAds()
            loadInterAd()
        } else {
            adView.visibility = View.GONE
        }
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

    private fun loadInterAd(){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context as Activity, getString( com.seregaklim.bulletinboard.R.string.ad_inter_id), adRequest, object : InterstitialAdLoadCallback(){
            override fun onAdLoaded(ad: InterstitialAd) {
                interAd = ad
            }
        })
    }

    fun showInterAd(){

        if(interAd != null){

            interAd?.fullScreenContentCallback = object : FullScreenContentCallback(){

                override fun onAdDismissedFullScreenContent() {

                    onClose()

                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {

                    onClose()

                }

            }

            interAd?.show(activity as Activity)

        } else {

            onClose()

        }

    }

    override fun onClose() {}
}














