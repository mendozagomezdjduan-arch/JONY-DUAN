package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val projectName: String,
    val photoNumber: Int,
    val imagePath: String,
    val imageDrawableRes: Int? = null,
    val date: String,
    val time: String,
    val coordSystem: String = "WGS 84 / UTM",
    val utmZone: String = "18S",
    val east: String = "278945.321",
    val north: String = "8654321.115",
    val altitudeMeters: String = "1543.25 msnm",
    val azimuthDegrees: Int = 42,
    val azimuthDirection: String = "NE",
    val description: String = "",
    val gpsAccuracyMeters: String = "± 3.2 m",
    val timestamp: Long = System.currentTimeMillis()
)
