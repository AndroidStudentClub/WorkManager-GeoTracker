package ru.androidschool.geotracker_workmanager_demo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

const val TABLE_NAME = "geo_info"

@Entity(tableName = TABLE_NAME)
class GeoInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val date: Long
)