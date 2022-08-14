package com.seregaklim.bulletinboard.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


//класс помогающий перетаскивать (картинки)
class ItemTouchMoveCallback(val adapter : ItemTouchAdapter) : ItemTouchHelper.Callback() {

    //cообщает адаптеру об изменении
    interface ItemTouchAdapter{
        fun onMove(startPos : Int, targetPos : Int)
      //каждый раз , когда перетащитли Item, обновлялся адаптер
       fun onClear()
    }


    //передвигаем вверх, вниз
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlag, 0)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
      adapter.onMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }
   //функция, при нажатии на картинку, меняет цвет (прозрачность)
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE)viewHolder?.itemView?.alpha = 0.5f
        super.onSelectedChanged(viewHolder, actionState)
    }
    //функция, при нажатии на картинку, после того как перетщили, возвращает цвет (прозрачность)
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        viewHolder.itemView.alpha = 1.0f
       adapter.onClear()
        super.clearView(recyclerView, viewHolder)
    }


}