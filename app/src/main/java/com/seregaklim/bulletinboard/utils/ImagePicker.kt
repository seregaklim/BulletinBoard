package com.seregaklim.bulletinboard.utils


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.act.EditAdsAct
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.PixEventCallback.Status.*
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImagePicker {
    val MAX_IMAGE_COUNT = 3
    //все картинки
    val REQUEST_CODE_GET_IMAGES=999
    //для выбранной картинки
    val REQUEST_CODE_GET_SINGLE_IMAGE = 998

   private fun  getOptions(imageCounter:Int): Options {
       val options = Options().apply {
           count = imageCounter //количество картино
           isFrontFacing = false
           mode = Mode.Picture
           path = "/pix/images"
       }

    return options
    }

    //открыввает фрагмент от библиотеки Pix, и передаем кол-во картинок
    fun getMultiImages(edAct: EditAdsAct, imageCounter: Int) {

        edAct.addPixToActivity(R.id.place_holder, getOptions(imageCounter)){ result->
            when (result.status) {
                PixEventCallback.Status.SUCCESS ->{
                    getMultiSelectedImages (edAct,result.data)

                }

                else -> {}
            }
        }
    }


    fun addImages(edAct: EditAdsAct, imageCounter: Int){
        val f =edAct.chooseImageFrag
        edAct.addPixToActivity(R.id.place_holder, getOptions(imageCounter)){ result->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    edAct.chooseImageFrag =f
                    openChooseImageFrag(edAct,f!!)
                    edAct.chooseImageFrag?.updateAdapter(result.data as ArrayList<Uri>,edAct)
                }
                else -> {}
            }
        }
    }

    //если выбрали одну картинку
    fun getSingleImage(edAct: EditAdsAct){
       val f =edAct.chooseImageFrag
        edAct.addPixToActivity(R.id.place_holder, getOptions(1)){ result->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    edAct.chooseImageFrag =f
                    openChooseImageFrag(edAct,f!!)
                    //хотим обновить наш фрагмент
                    singleImage(edAct, result.data[0])
                }
                else -> {}
            }
        }
    }

    private fun openChooseImageFrag(edAct: EditAdsAct,f:Fragment){
        edAct.supportFragmentManager.beginTransaction().replace(R.id.place_holder, f).commit()
    }

    private  fun closePixFrag(edAct: EditAdsAct){
        val fList = edAct.supportFragmentManager.fragments
        fList.forEach {
            if (it.isVisible )edAct.supportFragmentManager.beginTransaction().remove(it).commit()
        }

    }

    // обрабатывает сколько картинок было выбрано, и что нужно сделать
    fun getMultiSelectedImages (edAct: EditAdsAct,uris:List<Uri>) {
        if (uris.size>1 && edAct.chooseImageFrag==null){
            edAct.openChooseImageFrag(uris as ArrayList<Uri>)

            // если не было картинок  и выбрал только одну картинку
        } else if (uris.size ==1 && edAct.chooseImageFrag == null) {
            CoroutineScope(Dispatchers.Main).launch{
                //прогрессбар для одной картинки
                edAct.binding.pBarLoad.visibility=View.VISIBLE
                val  bitMapArray =ImageManager.imageResize(uris as ArrayList<Uri>,edAct) as ArrayList<Bitmap>
                edAct.binding.pBarLoad.visibility=View.GONE
                edAct.imageAdapter.update(bitMapArray)
                closePixFrag(edAct)
            }
        }
    }


    //функция, для одрной картинки которую мы выбрали
    private fun singleImage(edAct: EditAdsAct, uri: Uri){
        edAct.chooseImageFrag?.setSingleImage(uri, edAct.editImagePos)
    }

}
