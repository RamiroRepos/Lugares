package com.lugares.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lugares.model.Lugar

@Dao
interface LugarDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addLugar(Lugar: Lugar)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateLugar(Lugar: Lugar)

    @Delete
    suspend fun deleteLugar(Lugar: Lugar)

    @Query("SELECT * FROM LUGAR")
    fun getAllData() : LiveData<List<Lugar>>
}