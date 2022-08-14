package com.seregaklim.bulletinboard.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.utils.ItemTouchMoveCallback


class ImageListFrag(private val fragCloseInterface : FragmentCloseInterface, private val newList :ArrayList<String>) : Fragment() {

    val adapter =SelectImageRvAdapter()

   //класс помогающий перетаскивать картинки
    val  dragCalback = ItemTouchMoveCallback(adapter)
    val touchHelper= ItemTouchHelper(dragCalback)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_image_frag, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val  back = view.findViewById<Button>(R.id.button)
       val rcView =view.findViewById<RecyclerView>(R.id.rcViewSelectImage)

        //передвикать картинки
      touchHelper.attachToRecyclerView(rcView)

       //подключаем адаптер
        rcView.layoutManager=LinearLayoutManager(activity)
        rcView.adapter=adapter

        //обновляем адаптер
        val updateList=ArrayList<SelectImageItem>()
        for (n in 0 until newList.size){
            updateList.add( SelectImageItem(n.toString(),newList[n]))
        }

        adapter.updatAdapter(updateList)


        back.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
    }
    //когда запущен остоединяется от активити
    override fun onDetach() {
        super.onDetach()
           //передаем данные EditAdsAct
        fragCloseInterface.onFragClose(adapter.mainArray)
    }
}
