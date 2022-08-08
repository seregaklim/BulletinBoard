package com.seregaklim.bulletinboard.utils

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


object CityHelper {
        //поиск страны
    fun getAllCountries(context: Context):ArrayList<String>{
        var tempArray = ArrayList<String>()
        try{
            //обрабатывае фаил json
            val inputStream : InputStream = context.assets.open("countriesToCities.json")
            //узнаем сколько байт достуано
            val size:Int = inputStream.available()
            //создаем массив с countriesToCities.json
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
           // превращаем в String
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            //получаем с обекта название страны
            val countriesNames = jsonObject.names()
            //через цикл нахлдим город

            if(countriesNames != null){
                for(n in 0 until countriesNames.length()){
                    tempArray.add(countriesNames.getString(n))
                }

            }

        } catch (e:IOException){

        }
        return tempArray
    }

//поиск города
    fun getAllCities(country:String, context:Context):ArrayList<String>{
        var tempArray = ArrayList<String>()
        try{

            val inputStream : InputStream = context.assets.open("countriesToCities.json")
            val size:Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val cityNames = jsonObject.getJSONArray(country)

            for(n in 0 until cityNames.length()){
                tempArray.add(cityNames.getString(n))
            }



        } catch (e:IOException){

        }
        return tempArray
    }

    fun filterListData(list : ArrayList<String>, searchText : String? ) : ArrayList<String>{
        val tempList = ArrayList<String>()
        tempList.clear()
        if(searchText == null){
            tempList.add("No result")
            return tempList
        }
        for(selection : String in list){
            if(selection.toLowerCase(Locale.ROOT).startsWith(searchText.toLowerCase(Locale.ROOT)))
                tempList.add(selection)
        }
        if(tempList.size == 0)tempList.add("No result")
        return tempList
    }

}