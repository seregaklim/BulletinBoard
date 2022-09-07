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
import com.seregaklim.bulletinboard.act.DescriptionActivity
import com.seregaklim.bulletinboard.act.EditAdsAct
import com.seregaklim.bulletinboard.model.Ad
import com.seregaklim.bulletinboard.databinding.AdListItemBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdsRcAdapter(val act:MainActivity) : RecyclerView.Adapter<AdsRcAdapter.AdHolder>() {
    val adArray = ArrayList<Ad>()

    private var timeFormatter: SimpleDateFormat? = null

    init {
        timeFormatter = SimpleDateFormat("dd/MM/yyyy - hh:mm", Locale.getDefault())
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdHolder(binding,act,timeFormatter!!)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adArray[position])
    }

    override fun getItemCount(): Int {
        return adArray.size
    }



    fun updateAdapter(newList: List<Ad>){
        val tempArray =ArrayList<Ad>()
        //добавляем старый список
        tempArray.addAll(adArray)
        // добавляем новый список
        tempArray.addAll(newList)
        //считывает старый список и новый (обновляет плавно с анимацией)
        val  diffResult =DiffUtil.calculateDiff(DiffUtilHelper(adArray,tempArray))
        //обновляем весь адаптер
        diffResult.dispatchUpdatesTo(this)
        //стираем старый список
        adArray.clear()
        //добавляем новый
        adArray.addAll(tempArray)

    }
   //загружаются первые объявления
 fun   updateAdapterWithClear(newList: List<Ad>){
        //считывает  новый (обновляет плавно с анимацией)
        val  diffResult =DiffUtil.calculateDiff(DiffUtilHelper(adArray, newList ))
        //обновляем весь адаптер
        diffResult.dispatchUpdatesTo(this)
        //стираем старый список
        adArray.clear()
        //добавляем новый
        adArray.addAll(newList)

    }



    class AdHolder(val binding: AdListItemBinding,val act: MainActivity,val formatterDate :SimpleDateFormat) : RecyclerView.ViewHolder(binding.root) {
        fun setData(ad: Ad) = with(binding){

            tvDescription.text = ad.description
            tvPrice.text = ad.price
            tvTitle.text=ad.title
            tvViewCounter.text=ad.viewsCounter
            tvFavCounter.text=ad.favCounter

            val publishTime = "Время публикации: ${getTimeFromMillis(ad.time)}"
            tvPublishTime.text = publishTime

            Picasso.get().load(ad.image3).into(mainImage)
//            Picasso.get().load(ad.image2).into(mainImage)
//            Picasso.get().load(ad.mainImage).into(mainImage)
            isFav(ad)
            showEditPanel(isOwner(ad))
            mainOnClick(ad)
        }

        private fun getTimeFromMillis(timeMillis: String): String{
            val c = Calendar.getInstance()
            c.timeInMillis = timeMillis.toLong()
            return formatterDate.format(c.time)
        }



        private fun mainOnClick(ad: Ad) = with(binding){
            //избранные
            ibFav.setOnClickListener{
                //если пользователь зарегистрированн
                if (act.mAuth.currentUser?.isAnonymous ==false) act.onFavClicked(ad)
            }
            //счетчик просмотров
            itemView.setOnClickListener {
                act.onAdViewed(ad)
            }
            //редактируем
            ibEditAd.setOnClickListener(onClickEdit(ad))

            //удаляем
            ibDeleteAd.setOnClickListener{
                act.onDeleteItem(ad)

                act.onDeleteItem(ad)
            }

            //счетчик просмотров, переход на страничку
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