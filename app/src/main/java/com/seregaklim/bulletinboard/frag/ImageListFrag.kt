package com.seregaklim.bulletinboard.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.seregaklim.bulletinboard.R


class ImageListFrag(private val fragCloseInterface : FragmentCloseInterface) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.list_image_frag, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val  back = view.findViewById<Button>(R.id.button)
        back.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
    }
    //когда запущен остоединяется от активити
    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose()
    }
}
