package com.seregaklim.bulletinboard.frag


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.utils.ItemTouchMoveCallback
import java.net.URI


class SelectImageRvAdapter() : RecyclerView.Adapter<SelectImageRvAdapter.ImageHolder>(),ItemTouchMoveCallback.ItemTouchAdapter {

    val mainArray=ArrayList<SelectImageItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.select_image_frag_item,parent,false)
        return ImageHolder(view)
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
    val  titleStart=mainArray[targetPos].title
    mainArray[targetPos].title=targetItem.title
     mainArray[startPos] = targetItem
     mainArray[targetPos].title = titleStart
     notifyItemMoved(startPos, targetPos)
    }

    override fun onClear() {
       //обновляет адаптер
        notifyDataSetChanged()
    }


    class ImageHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        lateinit var tvTitle:TextView
        lateinit var image :ImageView

        fun setData(item: SelectImageItem){
            tvTitle=itemView.findViewById(R.id.tvTitle)
            image=itemView.findViewById(R.id.imageView)
            tvTitle.text=item.title
            image.setImageURI(Uri.parse(item.imageUri))

        }

    }
    fun updatAdapter(newlist:List<SelectImageItem>){
        //очищаем данные
        mainArray.clear()
      //перезаписываем
        mainArray.addAll(newlist)
        //сооющаем адаптеру об изменении
        notifyDataSetChanged()
    }


}