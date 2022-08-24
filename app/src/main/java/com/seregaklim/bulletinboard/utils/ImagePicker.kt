package com.seregaklim.bulletinboard.utils


import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.seregaklim.bulletinboard.act.EditAdsAct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImagePicker {
    val MAX_IMAGE_COUNT = 3
    //все картинки
    val REQUEST_CODE_GET_IMAGES=999
    //для выбранной картинки
    val REQUEST_CODE_GET_SINGLE_IMAGE = 998

   private fun  getOptions(imageCounter:Int):Options {
        val options = Options.init()
            .setCount(imageCounter) //количество фотографий
            .setFrontfacing(false)
            .setCount(3)

            .setMode(Options.Mode.Picture)
            .setScreenOrientation(Options.SCREEN_ORIENTATION_REVERSE_PORTRAIT)
            .setPath ("/pix/images")

    return options
    }

    //делаем проверку на обработку картинок (запрос)
    fun launcher(edAct: EditAdsAct, launcher: ActivityResultLauncher<Intent>?, imageCounter: Int) {
        PermUtil.checkForCamaraWritePermissions(edAct) {

            val intent = Intent(edAct, Pix::class.java).apply {
                putExtra("options", getOptions(imageCounter  ))
            }
            launcher?.launch(intent)
        }
    }

    //ланчер позволяющий запускать несколько картинок
  fun getLaunherForMultiSelectImages(edAct: EditAdsAct): ActivityResultLauncher<Intent> {
        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result:ActivityResult ->

            if (result.resultCode == AppCompatActivity.RESULT_OK ){
                if (result.data !=null){
                    val returnValues = result.data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
//                Log.d("Mylog","Image :${returnValues?.get(0)}")
//                Log.d("Mylog","Image :${returnValues?.get(1)}")
//                Log.d("Mylog","Image :${returnValues?.get(2)}")

                    //если больше 1 картинки
                    if (returnValues?.size!!>1 && edAct.chooseImageFrag==null){
                        edAct.openChooseImageFrag(returnValues)

                        //значит пользователь уже выбирал картинки
                    }else if (  edAct.chooseImageFrag !=null){
                        //показывает картинки , которые были в фрагменте
                        edAct. chooseImageFrag?.updateAdapter(returnValues)

                        // если не было картинок  и выбрал только одну картинку
                    } else if (returnValues.size ==1 && edAct.chooseImageFrag == null) {

                        CoroutineScope(Dispatchers.Main).launch{
                            //прогрессбар для одной картинки
                            edAct.binding.pBarLoad.visibility=View.VISIBLE
                            val  bitMapArray =ImageManager.imageResize(returnValues) as ArrayList<Bitmap>
                            edAct.binding.pBarLoad.visibility=View.GONE
                            edAct.imageAdapter.update(bitMapArray)
                        }
                    }
                }
            }
        }
    }



    //функция, позволяющая взять картинки и сделать фото
    fun  getLauncherForSingleImage(edAct:EditAdsAct): ActivityResultLauncher<Intent> {

        return edAct.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                if (result.data != null) {
                    // ссылка на выбранную картирку
                    val uris =result. data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                    edAct.chooseImageFrag?.setSingleImage(uris?.get(0)!!, edAct.editImagePos,)
                }

            }
        }
    }
}