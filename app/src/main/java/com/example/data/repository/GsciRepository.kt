package com.example.data.repository

import com.example.R
import com.example.data.local.PhotoDao
import com.example.data.local.ProjectDao
import com.example.data.model.PhotoEntity
import com.example.data.model.ProjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GsciRepository(
    private val projectDao: ProjectDao,
    private val photoDao: PhotoDao
) {
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val allPhotos: Flow<List<PhotoEntity>> = photoDao.getAllPhotos()

    fun getPhotosForProject(projectId: Long): Flow<List<PhotoEntity>> {
        return photoDao.getPhotosForProject(projectId)
    }

    suspend fun getProjectById(id: Long): ProjectEntity? {
        return projectDao.getProjectById(id)
    }

    suspend fun getPhotoById(id: Long): PhotoEntity? {
        return photoDao.getPhotoById(id)
    }

    suspend fun createProject(name: String, category: String = "Carreteras"): Long {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val newProject = ProjectEntity(
            name = name,
            category = category,
            photoCount = 0,
            updatedDate = currentDate,
            coverImageRes = R.drawable.img_carretera_1785933721070
        )
        return projectDao.insertProject(newProject)
    }

    suspend fun updateProject(project: ProjectEntity) {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        projectDao.updateProject(project.copy(updatedDate = currentDate))
    }

    suspend fun addPhotoToProject(
        projectId: Long,
        imagePath: String,
        imageDrawableRes: Int? = null,
        description: String = "Excavación de cuneta Km 2+540",
        utmZone: String = "18S",
        coordSystem: String = "WGS 84 / UTM",
        east: String = "278945.321",
        north: String = "8654321.115",
        altitudeMeters: String = "1543.25 msnm",
        azimuthDegrees: Int = 42,
        azimuthDirection: String = "NE"
    ): Long {
        val project = projectDao.getProjectById(projectId) ?: return -1
        val newCount = project.photoCount + 1
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        val photo = PhotoEntity(
            projectId = projectId,
            projectName = project.name,
            photoNumber = newCount,
            imagePath = imagePath,
            imageDrawableRes = imageDrawableRes,
            date = currentDate,
            time = currentTime,
            coordSystem = coordSystem,
            utmZone = utmZone,
            east = east,
            north = north,
            altitudeMeters = altitudeMeters,
            azimuthDegrees = azimuthDegrees,
            azimuthDirection = azimuthDirection,
            description = description
        )

        val photoId = photoDao.insertPhoto(photo)
        projectDao.incrementPhotoCount(projectId, currentDate)
        return photoId
    }

    suspend fun updatePhotoDescription(photoId: Long, newDescription: String) {
        val photo = photoDao.getPhotoById(photoId) ?: return
        photoDao.updatePhoto(photo.copy(description = newDescription))
    }

    suspend fun deleteProject(project: ProjectEntity) {
        projectDao.deleteProject(project)
    }

    suspend fun deletePhoto(photo: PhotoEntity) {
        photoDao.deletePhoto(photo)
    }

    suspend fun prepopulateIfEmpty() {
        val currentProjects = allProjects.first()
        if (currentProjects.isEmpty()) {
            val p1Id = projectDao.insertProject(
                ProjectEntity(
                    id = 1,
                    name = "Carretera San Salvador",
                    category = "Carreteras",
                    photoCount = 128,
                    updatedDate = "06/06/2026",
                    coverImageRes = R.drawable.img_carretera_1785933721070
                )
            )
            projectDao.insertProject(
                ProjectEntity(
                    id = 2,
                    name = "Mejoramiento IE 102",
                    category = "Edificaciones",
                    photoCount = 42,
                    updatedDate = "05/06/2026",
                    coverImageRes = R.drawable.img_excavadora_1785933737096
                )
            )
            projectDao.insertProject(
                ProjectEntity(
                    id = 3,
                    name = "Puente Río Grande",
                    category = "Topografía",
                    photoCount = 67,
                    updatedDate = "04/06/2026",
                    coverImageRes = R.drawable.img_carretera_1785933721070
                )
            )
            projectDao.insertProject(
                ProjectEntity(
                    id = 4,
                    name = "Alcantarillado Zona Sur",
                    category = "Hidráulica",
                    photoCount = 23,
                    updatedDate = "03/06/2026",
                    coverImageRes = R.drawable.img_excavadora_1785933737096
                )
            )

            // Insert initial photo for Carretera San Salvador (matches Screen 3 and 4 in design)
            photoDao.insertPhoto(
                PhotoEntity(
                    id = 1,
                    projectId = p1Id,
                    projectName = "Carretera San Salvador",
                    photoNumber = 128,
                    imagePath = "",
                    imageDrawableRes = R.drawable.img_excavadora_1785933737096,
                    date = "06/06/2026",
                    time = "15:42:18",
                    coordSystem = "WGS 84 / UTM",
                    utmZone = "18S",
                    east = "278945.321",
                    north = "8654321.115",
                    altitudeMeters = "1543.25 msnm",
                    azimuthDegrees = 42,
                    azimuthDirection = "NE",
                    description = "Excavación de cuneta Km 2+540",
                    gpsAccuracyMeters = "± 3.2 m"
                )
            )
        }
    }
}
