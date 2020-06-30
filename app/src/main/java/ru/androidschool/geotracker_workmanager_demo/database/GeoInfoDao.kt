package ru.androidschool.geotracker_workmanager_demo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GeoInfoDao {

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAllInfo(): List<GeoInfo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(info: GeoInfo)

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll()
}
