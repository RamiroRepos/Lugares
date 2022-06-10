package com.lugares.repository

import androidx.lifecycle.LiveData
import com.lugares.data.LugarDao
import com.lugares.model.Lugar

class LugarRepository (private val lugarDao: LugarDao) {
    //Se implementan las funciones de la interface

    //Se crea un objeto que continene el arrayList de los registros de la tabla lugar... cubiertos por LiveData
    val getAllDAta: LiveData<List<Lugar>> = lugarDao.getAllData()

    //Se define la funcion para insertar un Lugar en la tabla Lugar
    suspend fun addLugar(lugar:Lugar){
        lugarDao.addLugar(lugar)
    }

    //Se define la funcion para actualizar un Lugar en la tabla Lugar
    suspend fun updateLugar(lugar:Lugar){
        lugarDao.updateLugar(lugar)
    }

    //Se define la funcion para elimninar un Lugar en la tabla Lugar
    suspend fun deleteLugar(lugar:Lugar){
        lugarDao.deleteLugar(lugar)
    }

}