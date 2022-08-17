package com.seregaklim.bulletinboard.frag


import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.act.EditAdsAct
import com.seregaklim.bulletinboard.utils.ImagePicker
import com.seregaklim.bulletinboard.utils.ItemTouchMoveCallback


class SelectImageRvAdapter() : RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(),ItemTouchMoveCallback.ItemTouchAdapter {

    val mainArray=ArrayList<Bitmap>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_frag_item,parent,false)
        return ImageHolder(view,parent.context,this)
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


    class ImageHolder(itemView:View, val context:Context,val adapter :SelectImageRvAdapter):RecyclerView.ViewHolder(itemView){
        val imagePicker = ImagePicker()
        lateinit var tvTitle:TextView
        lateinit var image :ImageView
        lateinit var imEditImage :ImageButton
        lateinit var imDeletImage :ImageButton


        fun setData(bitmap: Bitmap){
            tvTitle=itemView.findViewById(R.id.tvTitle)
            image=itemView.findViewById(R.id.imageView)
            imEditImage=itemView.findViewById(R.id.imEditImage)
            imDeletImage=itemView.findViewById(R.id.imDelete)

            //редактируем отдельную фотографию
            imEditImage.setOnClickListener {

                imagePicker.getImages(context as EditAdsAct,1,imagePicker.REQUEST_CODE_GET_SINGLE_IMAGE)
                //на позицию , которую нажали
                context.editImagePos=adapterPosition

            }

            //удаляем выбранную картинку
            imDeletImage.setOnClickListener {

                adapter.mainArray.removeAt(adapterPosition)
                //обновляем адаптер
                adapter.notifyItemRemoved(adapterPosition)
                //происходит плавное удаление с анимацией
                for (n in 0 until adapter.mainArray.size)adapter.notifyItemChanged(n)
            }



            tvTitle.text=context.resources.getStringArray(R.array.title_array)[adapterPosition]
            image.setImageBitmap(bitmap)

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