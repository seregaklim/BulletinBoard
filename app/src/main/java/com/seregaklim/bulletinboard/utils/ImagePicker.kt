package com.seregaklim.bulletinboard.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.fxn.pix.Options
import com.fxn.pix.Pix

class ImagePicker {
    val REQUEST_CODE_GET_IMAGES=999
    fun  getImages(context: AppCompatActivity,imageCounter:Int) {
        val options = Options.init()
            .setRequestCode(REQUEST_CODE_GET_IMAGES)
            .setCount(imageCounter) //количество фотографий
            .setFrontfacing(false)
            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_REVERSE_PORTRAIT)
           .setPath ("/pix/images")

    Pix.start(context,options)
    }

}