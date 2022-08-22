package com.seregaklim.bulletinboard.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.seregaklim.bulletinboard.data.Ad
import com.seregaklim.bulletinboard.databinding.AdListItemBinding
import kotlin.collections.ArrayList

class AdsRcAdapter : RecyclerView.Adapter<AdsRcAdapter.AdHolder>() {
    val adArray = ArrayList<Ad>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdHolder(binding)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adArray[position])
    }

    override fun getItemCount(): Int {
        return adArray.size
    }

    fun updateAdapter(newList: List<Ad>){

        adArray.clear()
        adArray.addAll(newList)
        notifyDataSetChanged()
    }



    class AdHolder(val binding: AdListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(ad: Ad) = with(binding) {

            tvDescription.text = ad.description
            tvPrice.text = ad.price
            tvTitle.text=ad.title

        }

    }
}