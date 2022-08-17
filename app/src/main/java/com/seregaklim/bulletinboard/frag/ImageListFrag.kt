package com.seregaklim.bulletinboard.frag

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.act.EditAdsAct
import com.seregaklim.bulletinboard.databinding.ListImageFragBinding
import com.seregaklim.bulletinboard.dialogs.ProgressDialog
import com.seregaklim.bulletinboard.utils.ImageManager
import com.seregaklim.bulletinboard.utils.ImagePicker
import com.seregaklim.bulletinboard.utils.ItemTouchMoveCallback
import kotlinx.coroutines.*


class ImageListFrag(private val fragCloseInterface : FragmentCloseInterface, private val newList :ArrayList<String>?) : Fragment() {
    lateinit var rootElement: ListImageFragBinding
    val adapter =SelectImageRvAdapter()
    val imagePicker  = ImagePicker()
    //класс помогающий перетаскивать картинки
    val  dragCalback = ItemTouchMoveCallback(adapter)
    val touchHelper= ItemTouchHelper(dragCalback)
    //вспогательный класс, закрывающий работу карутин
    private var job: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootElement = ListImageFragBinding.inflate(inflater)
        return rootElement.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //подключаем тулбар
        setUpToolbar()

        //передвикать картинки
        touchHelper.attachToRecyclerView(rootElement.rcViewSelectImage)

        //подключаем адаптер
        rootElement.rcViewSelectImage.layoutManager=LinearLayoutManager(activity)
        rootElement.rcViewSelectImage.adapter=adapter


        if (newList != null) {
            resizeSelectedImages(newList,true)
        }
    }

    //обновляем адаптер, с картинками , которые хотим отредактировать (если есть картинки)
    fun  updateAdapterFromEdit(bitmapList:List<Bitmap>){

        adapter.updateAdapter(bitmapList,true)

    }


    //когда запущен остоединяется от активити , закрываем фрагмент
    override fun onDetach() {
        super.onDetach()
        //передаем данные EditAdsAct
        fragCloseInterface.onFragClose(adapter.mainArray)
        //оставналиваем задачу карутин, если выходим из фрагмента
        job?.cancel()
    }

       //загружаем фото, через курутины с помощью ImageManager
    fun resizeSelectedImages(newList: ArrayList<String>,needClear: Boolean){

        //основной поток (Dispatchers.Main)
        job = CoroutineScope(Dispatchers.Main).launch {

            val dialog=    ProgressDialog.createProgressDialog(activity as Activity)

            val bitmapList = ImageManager.imageResize(newList)

            //закрываем  ProgressDialog
            dialog.dismiss()
            //обновляем адаптер
            adapter.updateAdapter(bitmapList, true)
        }

    }


    //меню бара
    private fun setUpToolbar() {

        rootElement.  tb.inflateMenu(R.menu.menu_choose_image)
        val deleteItem =rootElement. tb.menu.findItem(R.id.id_delete_image)
        val  addImageItem =rootElement. tb.menu.findItem(R.id.id_add_image)
        if(adapter.mainArray.size > 2) addImageItem?.isVisible = false


        rootElement.tb.setNavigationOnClickListener {
            //возращает назад на активити
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            // showInterAd()

        }

        deleteItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(),true)
            addImageItem?.isVisible = true
            true
        }

        addImageItem?.setOnMenuItemClickListener {
            //количество возмжных добавляемых картинок
            val imageCount = imagePicker.MAX_IMAGE_COUNT- adapter.mainArray.size
            imagePicker.getImages(activity as EditAdsAct, imageCount,imagePicker.REQUEST_CODE_GET_IMAGES)
            true
        }

    }

    //добавляем картинку
    fun updateAdapter(newList : ArrayList<String>){
        resizeSelectedImages(newList,false)
    }

    //фукция, для редактирования выбранной картинки (одной)
    fun setSingleImage(uri : String, pos : Int){

        //подглючаем прогресс бар SelectImageRvAdapter
        val pBar = rootElement.rcViewSelectImage[pos].findViewById<ProgressBar>(R.id.pBar)


        //основной поток (Dispatchers.Main)
        job= CoroutineScope(Dispatchers.Main).launch {

            pBar.visibility=View.VISIBLE
            val bitmapList= ImageManager.imageResize(listOf(uri))
            pBar.visibility = View.GONE

            //указываю позицию, которую хочу перезаписать
            adapter.mainArray[pos] = bitmapList[0]
            //обновляем адаптер, для одного конкретного взятого элемента
            adapter.notifyItemChanged(pos)
        }
    }
}



