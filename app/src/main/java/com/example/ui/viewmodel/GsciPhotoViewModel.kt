package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.location.LocationCompassManager
import com.example.data.location.TelemetryState
import com.example.data.model.PhotoEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.SettingsState
import com.example.data.repository.GsciRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GsciPhotoViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = GsciRepository(database.projectDao(), database.photoDao())

    private val locationCompassManager = LocationCompassManager(application)
    val telemetryState: StateFlow<TelemetryState> = locationCompassManager.telemetryState

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allPhotos: StateFlow<List<PhotoEntity>> = repository.allPhotos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _activeProject = MutableStateFlow<ProjectEntity?>(null)
    val activeProject: StateFlow<ProjectEntity?> = _activeProject.asStateFlow()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    private val _currentDescription = MutableStateFlow("Excavación de cuneta Km 2+540")
    val currentDescription: StateFlow<String> = _currentDescription.asStateFlow()

    private val _selectedPhotoForDetail = MutableStateFlow<PhotoEntity?>(null)
    val selectedPhotoForDetail: StateFlow<PhotoEntity?> = _selectedPhotoForDetail.asStateFlow()

    init {
        // Start real-time GPS & Compass telemetry
        locationCompassManager.startLocationAndCompassUpdates()

        viewModelScope.launch {
            repository.prepopulateIfEmpty()
            // Default active project
            val projects = repository.allProjects.stateIn(viewModelScope).value
            if (projects.isNotEmpty()) {
                _activeProject.value = projects.first()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationCompassManager.stopLocationAndCompassUpdates()
    }

    fun selectActiveProject(project: ProjectEntity) {
        _activeProject.value = project
    }

    fun setSelectedPhoto(photo: PhotoEntity?) {
        _selectedPhotoForDetail.value = photo
    }

    fun updateCurrentDescription(desc: String) {
        _currentDescription.value = desc
    }

    fun createProject(name: String, category: String = "Carreteras") {
        viewModelScope.launch {
            val newId = repository.createProject(name, category)
            val newProj = repository.getProjectById(newId)
            if (newProj != null) {
                _activeProject.value = newProj
            }
        }
    }

    fun updateProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.updateProject(project)
            if (_activeProject.value?.id == project.id) {
                _activeProject.value = project
            }
        }
    }

    fun capturePhoto(imagePath: String, drawableRes: Int? = null) {
        val proj = _activeProject.value ?: return
        val currentTelemetry = telemetryState.value
        val currentSettings = settingsState.value

        viewModelScope.launch {
            val photoId = repository.addPhotoToProject(
                projectId = proj.id,
                imagePath = imagePath,
                imageDrawableRes = drawableRes,
                description = _currentDescription.value,
                utmZone = currentTelemetry.utmZone,
                coordSystem = currentSettings.coordSystem,
                east = currentTelemetry.easting,
                north = currentTelemetry.northing,
                altitudeMeters = currentTelemetry.altitudeMeters,
                azimuthDegrees = currentTelemetry.azimuthDegrees,
                azimuthDirection = currentTelemetry.azimuthDirection
            )
            val capturedPhoto = repository.getPhotoById(photoId)
            if (capturedPhoto != null) {
                _selectedPhotoForDetail.value = capturedPhoto
            }
            // Update active project instance to reflect new photo count
            val updatedProj = repository.getProjectById(proj.id)
            if (updatedProj != null) {
                _activeProject.value = updatedProj
            }
        }
    }

    fun updatePhotoDescription(photoId: Long, newDesc: String) {
        viewModelScope.launch {
            repository.updatePhotoDescription(photoId, newDesc)
            val updated = repository.getPhotoById(photoId)
            if (updated != null && _selectedPhotoForDetail.value?.id == photoId) {
                _selectedPhotoForDetail.value = updated
            }
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
            if (_activeProject.value?.id == project.id) {
                _activeProject.value = allProjects.value.firstOrNull { it.id != project.id }
            }
        }
    }

    // Toggle Settings Switches
    fun toggleFecha() { _settingsState.update { it.copy(showFecha = !it.showFecha) } }
    fun toggleHora() { _settingsState.update { it.copy(showHora = !it.showHora) } }
    fun toggleGps() { _settingsState.update { it.copy(showGps = !it.showGps) } }
    fun toggleUtm() { _settingsState.update { it.copy(showUtm = !it.showUtm) } }
    fun toggleAltitud() { _settingsState.update { it.copy(showAltitud = !it.showAltitud) } }
    fun toggleAzimut() { _settingsState.update { it.copy(showAzimut = !it.showAzimut) } }
    fun toggleDescripcion() { _settingsState.update { it.copy(showDescripcion = !it.showDescripcion) } }
    fun toggleLogo() { _settingsState.update { it.copy(showLogo = !it.showLogo) } }
    fun toggleNombreProyecto() { _settingsState.update { it.copy(showNombreProyecto = !it.showNombreProyecto) } }
    fun toggleNumeroFoto() { _settingsState.update { it.copy(showNumeroFoto = !it.showNumeroFoto) } }
    fun toggleSaveOriginal() { _settingsState.update { it.copy(saveOriginal = !it.saveOriginal) } }

    fun setAltitudeUnit(unit: String) { _settingsState.update { it.copy(altitudeUnit = unit) } }
    fun setCoordFormat(format: String) { _settingsState.update { it.copy(coordFormat = format) } }
    fun setCoordSystem(system: String) { _settingsState.update { it.copy(coordSystem = system) } }
    fun setUtmZone(zone: String) { _settingsState.update { it.copy(utmZone = zone) } }
    fun setImageQuality(quality: String) { _settingsState.update { it.copy(imageQuality = quality) } }
    fun setTemplateInfo(template: String) { _settingsState.update { it.copy(templateInfo = template) } }
    fun setTemplateColor(color: String) { _settingsState.update { it.copy(templateColor = color) } }
}

