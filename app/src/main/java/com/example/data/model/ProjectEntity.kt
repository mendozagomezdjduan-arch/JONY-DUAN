package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = "General",
    val photoCount: Int = 0,
    val updatedDate: String,
    val coverImageRes: Int? = null,
    val coverImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
