package com.seregaklim.bulletinboard.frag


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.act.EditAdsAct
import com.seregaklim.bulletinboard.databinding.SelectImageFragItemBinding
import com.seregaklim.bulletinboard.utils.AdapterCallback
import com.seregaklim.bulletinboard.utils.ImageManager
import com.seregaklim.bulletinboard.utils.ImagePicker
import com.seregaklim.bulletinboard.utils.ItemTouchMoveCallback


class SelectImageRvAdapter(val adapterCallback :AdapterCallback) : RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(),ItemTouchMoveCallback.ItemTouchAdapter {

    val mainArray=ArrayList<Bitmap>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {

        val viewBinding =SelectImageFragItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return ImageHolder(viewBinding,parent.context,this)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainArray[position])

    }
    override fun getItemCount(): Int {
        return mainArray.size
    }
    //интефейс меняющий позицию (картинку
    override fun onMove(startPos: Int, targetPos: Int) {
        val targetItem = mainArray[targetPos]
        mainArray[targetPos]=mainArray[startPos]
        //текс оставляем на прежнем месте
        //   val  titleStart=mainArray[targetPos].title
        //  mainArray[targetPos].title=targetItem.title
        mainArray[startPos] = targetItem
        //   mainArray[targetPos].title = titleStart
        notifyItemMoved(startPos, targetPos)
    }

    override fun onClear() {
        //обновляет адаптер
        notifyDataSetChanged()
    }


    class ImageHolder(private val viewBinding: SelectImageFragItemBinding, val context:Context,val adapter :SelectImageRvAdapter):RecyclerView.ViewHolder(viewBinding.root){

        fun setData(bitmap: Bitmap){
            val imagePicker= ImagePicker()
            val imageManager = ImageManager


//            //редактируем отдельную фотографию
           viewBinding.  imEditImage.setOnClickListener {

               imagePicker.launcher(context as EditAdsAct,context.launcherSingleSelectImage, imageCounter = 1)

               //на позицию , которую нажали
               context.editImagePos=adapterPosition
           }

            //удаляем выбранную картинку
            viewBinding.imDelete.setOnClickListener {

                adapter.mainArray.removeAt(adapterPosition)
                //обновляем адаптер
                adapter.notifyItemRemoved(adapterPosition)
                //происходит плавное удаление с анимацией
                for (n in 0 until adapter.mainArray.size)adapter.notifyItemChanged(n)

                adapter.adapterCallback.onItemDelete()
            }
            viewBinding. tvTitle.text=context.resources.getStringArray(R.array.title_array)[adapterPosition]
            //проверяем вертикальная картинка или горизонтальная и обрезаем
            imageManager.chooseScaleType(viewBinding.imageView,bitmap)
            viewBinding.imageView.setImageBitmap(bitmap)

        }
    }

    fun updateAdapter(newlist:List<Bitmap>, needClear : Boolean){
        //очищаем данные
        if(needClear) mainArray.clear()
        //перезаписываем
        mainArray.addAll(newlist)
        //сооющаем адаптеру об изменении
        notifyDataSetChanged()
    }
}