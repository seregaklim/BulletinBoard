package com.seregaklim.bulletinboard.adapters


import androidx.recyclerview.widget.DiffUtil
import com.seregaklim.bulletinboard.model.Ad

class DiffUtilHelper(val oldList: List<Ad>, val newList: List<Ad>) : DiffUtil.Callback() {


    override fun getOldListSize(): Int {
        return oldList.size
    }


    override fun getNewListSize(): Int {
        return newList.size
    }
//сравниваем два списка (старый и новый) по ключу
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].key == newList[newItemPosition].key
    }
//сравниваем подность класс , если изменения
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]== newList[newItemPosition]
    }

}