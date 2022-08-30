package com.seregaklim.bulletinboard.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

import java.io.File
import java.io.InputStream

object ImageManager {
    private const val WIDTH = 0
    private const val HEIGHT = 1
    private const val MAX_IMAGE_SIZE = 1000

    //функция выдает размер, чтобы потом сжать ее
    fun getImageSize(uri : Uri,act:Activity) : List<Int>{
        //доступ к файлам
        val inStream =act.contentResolver.openInputStream(uri)

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inStream,null, options)
     return listOf(options.outWidth, options.outHeight)
    }


    //позволяет узнавать, картинка горизонтальная или вертикалиная, после чего картинка расширяется
    fun chooseScaleType(im: ImageView, bitMap: Bitmap){
        //горизонтальная
        if(bitMap.width > bitMap.height){
             //обрезаем
            im.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            im.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    //функция сжатия  ( withContext(Dispatchers.IO)-запускается в фоновом режиме))
  suspend  fun imageResize (uris: List<Uri>, act:Activity):List<Bitmap> = withContext(Dispatchers.IO){
        //массив с высотой и шириной
        val tempList = ArrayList<List<Int>>()

        val bitmapList = ArrayList<Bitmap>()

        for(n in uris.indices) {
            val size = getImageSize(uris[n], act)
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

