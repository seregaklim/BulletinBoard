package com.seregaklim.bulletinboard.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

import java.io.File

object ImageManager {
    private const val WIDTH = 0
    private const val HEIGHT = 1
    private const val MAX_IMAGE_SIZE = 1000
    //функция выдает размер, чтобы потом сжать ее
    fun getImageSize(uri : String,) : List<Int>{

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(uri, options)

        if (imageRotaition(uri) == 90)
        //ширину и высоту меняем местами
            return listOf(options.outHeight, options.outWidth)
        else return listOf(options.outWidth, options.outHeight)

    }

    //угол поворота
    private   fun imageRotaition (uri:String):Int{

        val rotaition:Int
        val imageFile= File(uri)
        //орикнтация (как был повернут фаил)
        val exif = ExifInterface(imageFile.absolutePath)
        val orientation =exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        rotaition= if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270){
            90
        } else {
            0
        }

        return rotaition
    }
    //функция сжатия  ( withContext(Dispatchers.IO)-запускается в фоновом режиме))
  suspend  fun imageResize (uris:List<String>):List<Bitmap> = withContext(Dispatchers.IO){
        //массив с высотой и шириной
        val tempList = ArrayList<List<Int>>()

        val bitmapList = ArrayList<Bitmap>()

        for(n in uris.indices) {
            val size = getImageSize(uris[n])
            //вычисляем пропорцию
            val imageRatio = size[WIDTH].toFloat() / size[HEIGHT].toFloat()
            //значит картинка горизонтальная
            if(imageRatio > 1){

                if(size[WIDTH] > MAX_IMAGE_SIZE) {
                    //уменьшаем размер
                    tempList.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / imageRatio).toInt()))
                }else {
                    tempList.add(listOf(size[WIDTH], size[HEIGHT]))
                }
                //значит картинка вертикальная
                if(size[HEIGHT] > MAX_IMAGE_SIZE){
                    //уменьшаем размер
                    tempList.add(listOf((MAX_IMAGE_SIZE * imageRatio).toInt() , MAX_IMAGE_SIZE))

                } else {

                    tempList.add(listOf(size[WIDTH], size[HEIGHT]))

                }

            }

        }
        for(i in uris.indices){
   ///kotlin.runCatching покажет ошибку
        val e=   kotlin.runCatching {

                bitmapList.add(Picasso.get().load(uris[i]).resize(tempList[i][WIDTH], tempList[i][HEIGHT]).get())
            }
                Log.d("MyLog","Bitmap load done :${e.isSuccess}")
        }
        return@withContext bitmapList
    }

}

