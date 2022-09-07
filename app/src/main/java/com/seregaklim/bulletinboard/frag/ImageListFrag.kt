package com.seregaklim.bulletinboard.frag

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.act.EditAdsAct
import com.seregaklim.bulletinboard.databinding.ListImageFragBinding
import com.seregaklim.bulletinboard.dialogs.ProgressDialog
import com.seregaklim.bulletinboard.utils.AdapterCallback
import com.seregaklim.bulletinboard.utils.ImageManager
import com.seregaklim.bulletinboard.utils.ImagePicker
import com.seregaklim.bulletinboard.utils.ItemTouchMoveCallback
import kotlinx.coroutines.*


class ImageListFrag(private val fragCloseInterface : FragmentCloseInterface) : BaseAdsFrag(),
AdapterCallback{

    val adapter =SelectImageRvAdapter(this)
    val imagePicker  = ImagePicker()
    //класс помогающий перетаскивать картинки
    val  dragCalback = ItemTouchMoveCallback(adapter)
    val touchHelper= ItemTouchHelper(dragCalback)
    //вспогательный класс, закрывающий работу карутин
    private var job: Job? = null
    lateinit var binding: ListImageFragBinding

    private var addImageItem:MenuItem? =null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ListImageFragBinding.inflate(layoutInflater, container, false)
        adView = binding.adView
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //подключаем тулбар
        setUpToolbar()

        binding.apply {
        //передвикать картинки
        touchHelper.attachToRecyclerView(rcViewSelectImage)

        //подключаем адаптер
        rcViewSelectImage.layoutManager=LinearLayoutManager(activity)
        rcViewSelectImage.adapter=adapter
        }
    }

    override fun onItemDelete() {
        addImageItem?.isVisible = true
    }


    //обновляем адаптер, с картинками , которые хотим отредактировать (если есть картинки)
    fun  updateAdapterFromEdit(bitmapList:List<Bitmap>){

        adapter.updateAdapter(bitmapList,true)

    }

    //когда запущен остоединяется от активити , закрываем фрагмент
    override fun onDetach() {
        super.onDetach()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@ImageListFrag)?.commit()
        //передаем данные EditAdsAct
        fragCloseInterface.onFragClose(adapter.mainArray)
        //оставналиваем задачу карутин, если выходим из фрагмента
        job?.cancel()


    }

       //загружаем фото, через курутины с помощью ImageManager
    fun resizeSelectedImages(newList: ArrayList<Uri>,needClear: Boolean,activity: Activity){

        //основной поток (Dispatchers.Main)
        job = CoroutineScope(Dispatchers.Main).launch {

            val dialog=    ProgressDialog.createProgressDialog(activity)
            val bitmapList = ImageManager.imageResize(newList,activity )

            //закрываем  ProgressDialog
            dialog.dismiss()
            //обновляем адаптер
            adapter.updateAdapter(bitmapList, needClear)

            //если больше 2 кнопку с добавлением прячим
          if (adapter.mainArray.size >2) addImageItem?.isVisible=false
        }

    }

    //меню бара
    private fun setUpToolbar() {
        binding.apply {

            tb.inflateMenu(R.menu.menu_choose_image)
            val deleteItem = tb.menu.findItem(R.id.id_delete_image)
            addImageItem = tb.menu.findItem(R.id.id_add_image)

        if(adapter.mainArray.size > 2) addImageItem?.isVisible = false


            tb.setNavigationOnClickListener {
                //возращает назад на активити
                activity?.supportFragmentManager?.beginTransaction()?.remove(this@ImageListFrag)?.commit()
                // showInterAd()

            }

            deleteItem.setOnMenuItemClickListener {
                adapter.updateAdapter(ArrayList(), true)
                //включаем кнопку добаления
                addImageItem?.isVisible = true
                true
            }

            addImageItem?.setOnMenuItemClickListener {
                //количество возмжных добавляемых картинок
                val imageCount = imagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
                imagePicker.addImages(activity as EditAdsAct,imageCount)

                true
            }
        }
    }

    //добавляем картинку
    fun updateAdapter(newList : ArrayList<Uri>,activity: Activity){
        resizeSelectedImages(newList,false,activity )
    }

    //фукция, для редактирования выбранной картинки (одной)
    fun setSingleImage(uri : Uri, pos : Int){

        //подглючаем прогресс бар SelectImageRvAdapter
        val pBar =binding.rcViewSelectImage[pos].findViewById<ProgressBar>(R.id.pBar)

        //основной поток (Dispatchers.Main)
        job= CoroutineScope(Dispatchers.Main).launch {

            pBar.visibility=View.VISIBLE
            val bitmapList= ImageManager.imageResize(arrayListOf(uri), activity as Activity)
            pBar.visibility = View.GONE

            //указываю позицию, которую хочу перезаписать
            adapter.mainArray[pos] = bitmapList[0]
            //обновляем адаптер, для одного конкретного взятого элемента
            adapter.notifyItemChanged(pos)
        }
    }


}



