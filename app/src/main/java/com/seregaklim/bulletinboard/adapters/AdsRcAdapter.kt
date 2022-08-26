package com.seregaklim.bulletinboard.adapters


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.seregaklim.bulletinboard.MainActivity
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.act.EditAdsAct
import com.seregaklim.bulletinboard.model.Ad
import com.seregaklim.bulletinboard.databinding.AdListItemBinding
import kotlin.collections.ArrayList

class AdsRcAdapter(val act:MainActivity) : RecyclerView.Adapter<AdsRcAdapter.AdHolder>() {
    val adArray = ArrayList<Ad>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdHolder(binding,act)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adArray[position])
    }

    override fun getItemCount(): Int {
        return adArray.size
    }

    fun updateAdapter(newList: List<Ad>){
        //считывает старый список и новый (обновляет плавно с анимацией)
        val  diffResult =DiffUtil.calculateDiff(DiffUtilHelper(adArray,newList))
        //обновляем весь адаптер
        diffResult.dispatchUpdatesTo(this)
        adArray.clear()
        adArray.addAll(newList)

    }

    class AdHolder(val binding: AdListItemBinding,val act: MainActivity) : RecyclerView.ViewHolder(binding.root) {
        fun setData(ad: Ad) = with(binding){

            tvDescription.text = ad.description
            tvPrice.text = ad.price
            tvTitle.text=ad.title
            tvViewCounter.text=ad.viewsCounter
            tvFavCounter.text=ad.favCounter

            isFav(ad)
            showEditPanel(isOwner(ad))

            //редактируем
            ibEditAd.setOnClickListener(onClickEdit(ad))

            //удаляем
            ibDeleteAd.setOnClickListener{
            act.onDeleteItem(ad)
            }

            //счетчик просмотров
            itemView.setOnClickListener {
                act.onAdViewed(ad)
            }

            //избранные
            ibFav.setOnClickListener{
            //если пользователь зарегистрированн
            if (act.mAuth.currentUser?.isAnonymous ==false) act.onFavClicked(ad)
            }

        }
        //лайк -дизлайк
        private fun isFav(ad: Ad){
            if(ad.isFav) {
                binding.ibFav.setImageResource(R.drawable.ic_fav_pressed)
            } else {
                binding.ibFav.setImageResource(R.drawable.ic_fav_normal)
            }
        }

        //редактируем
        private fun onClickEdit(ad: Ad):View.OnClickListener{

            return View.OnClickListener {

                val editIntent = Intent(act,EditAdsAct::class.java).apply {

                    putExtra(MainActivity.EDIT_STATE,true)
                    // для передчи data класса добавь Serializable в data класс (чтобы не передавать по отдельности все элементы)
                    putExtra(MainActivity.ADS_DATA,ad)
                }
                act.startActivity(editIntent)
            }
        }

        private fun isOwner (ad: Ad):Boolean{
            //индификатор объявления, равен инд. акаунта
            return ad.uid == act.mAuth.uid
        }

        //показывает панель владельцу объявления
        private fun showEditPanel(isOwner:Boolean){
            if(isOwner){
                binding.editPanel.visibility =View.VISIBLE
            }else{
                //прячим
                binding.editPanel.visibility=View.GONE
            }

        }

    }

    interface Listener{
        fun onDeleteItem(ad: Ad)
        fun onAdViewed(ad: Ad)
        fun onFavClicked(ad: Ad)
    }
}