package com.seregaklim.bulletinboard.act

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.databinding.ActivityFilterBinding
import com.seregaklim.bulletinboard.dialogs.DialogSpinnerHelper
import com.seregaklim.bulletinboard.utils.CityHelper

class FilterActivity : AppCompatActivity() {
    lateinit var binding: ActivityFilterBinding
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)

        setContentView(binding.root)
        actionBarSettings()
        onClickSelectCountry()
        onClickSelectCity()
        onClickDone()
        getFilter()
        onClickClear()
    }

    private fun getFilter() = with(binding){
        val filter = intent.getStringExtra(FILTER_KEY)
        if(filter != null && filter != "empty"){
            //превращаем в массив по нижнему подчеркиванию
            val filterArray = filter.split("_")
            if(filterArray[0] != "empty") tvCountry.text = filterArray[0]
            if(filterArray[1] != "empty") tvCity.text = filterArray[1]
            if(filterArray[2] != "empty") edIndex.setText(filterArray[2])
            checkBoxWithSend.isChecked = filterArray[3].toBoolean()
        }
    }

    //назад
     fun actionBarSettings(){
        val  ab=supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
     }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }


    private fun onClickSelectCountry() = with(binding){
        tvCountry.setOnClickListener {
            val listCountry = CityHelper.getAllCountries(this@FilterActivity)
            dialog.showSpinnerDialog(this@FilterActivity, listCountry, tvCountry)
            if(binding.tvCity.text.toString() != getString(R.string.select_city)){
                tvCity.text = getString(R.string.select_city)
            }
        }
    }

    private fun onClickSelectCity() = with(binding){
        tvCity.setOnClickListener {
            val selectedCountry = tvCountry.text.toString()
            if(selectedCountry != getString(R.string.select_country)){
                val listCity = CityHelper.getAllCities(selectedCountry, this@FilterActivity)
                dialog.showSpinnerDialog(this@FilterActivity, listCity, tvCity)
            } else {
                Toast.makeText(this@FilterActivity, "No country selected", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun onClickDone() = with(binding){
        btDone.setOnClickListener {
            // Log.d("MyLog","Filter:${createFilter()}")
            val i = Intent().apply {
                putExtra(FILTER_KEY, createFilter())
            }
            setResult(RESULT_OK, i)
            finish()
        }
    }
    //очищаем фильтр
    private fun onClickClear() = with(binding){
        btClear.setOnClickListener {
            tvCountry.text = getString(R.string.select_country)
            tvCity.text = getString(R.string.select_city)
            edIndex.setText("")
            checkBoxWithSend.isChecked = false
            setResult(RESULT_CANCELED)
        }
    }

    private fun createFilter(): String = with(binding){
        val sBuilder = StringBuilder()
        val arrayTempFilter = listOf(
            tvCountry.text,
            tvCity.text,
            edIndex.text,
            checkBoxWithSend.isChecked.toString()
        )
        for((i, s) in arrayTempFilter.withIndex()){
            if(s != getString(R.string.select_country) && s != getString(R.string.select_city) && s.isNotEmpty()){
                sBuilder.append(s)
                if(i != arrayTempFilter.size - 1)sBuilder.append("_")
            } else {
                //те слова, которые не выбрали при фильтрации
                sBuilder.append("empty")
                if(i != arrayTempFilter.size - 1)sBuilder.append("_")
            }
        }
        return sBuilder.toString()
    }


    companion object{
        const val FILTER_KEY = "filter_key"
    }

}