package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ProjectEntity
import com.example.ui.components.DeleteProjectDialog
import com.example.ui.components.DescriptionDialog
import com.example.ui.components.EditProjectDialog
import com.example.ui.components.NewProjectDialog
import com.example.ui.components.SettingsSelectionDialog
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.CameraCaptureScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.MainProjectsScreen
import com.example.ui.screens.PhotoResultScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GsciGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.GsciPhotoViewModel

enum class MainTab {
    PROYECTOS, GALERIA, ACERCA_DE
}

enum class SelectionDialogType {
    ALTITUDE_UNIT,
    COORD_FORMAT,
    COORD_SYSTEM,
    UTM_ZONE,
    IMAGE_QUALITY
}

enum class ScreenState {
    MAIN_TABS,
    SETTINGS,
    CAMERA,
    PHOTO_RESULT
}

class MainActivity : ComponentActivity() {
    private val viewModel: GsciPhotoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                GsciPhotoApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GsciPhotoApp(viewModel: GsciPhotoViewModel) {
    var currentScreen by remember { mutableStateOf(ScreenState.MAIN_TABS) }
    var currentTab by remember { mutableStateOf(MainTab.PROYECTOS) }

    var showNewProjectDialog by remember { mutableStateOf(false) }
    var editingProject by remember { mutableStateOf<ProjectEntity?>(null) }
    var deletingProject by remember { mutableStateOf<ProjectEntity?>(null) }
    var showDescriptionDialog by remember { mutableStateOf(false) }
    var activeSelectionDialog by remember { mutableStateOf<SelectionDialogType?>(null) }

    val projects by viewModel.allProjects.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val allPhotos by viewModel.allPhotos.collectAsState()
    val selectedPhoto by viewModel.selectedPhotoForDetail.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val currentDescription by viewModel.currentDescription.collectAsState()
    val telemetryState by viewModel.telemetryState.collectAsState()

    val latestPhoto = allPhotos.firstOrNull()

    // Handle Back Press
    BackHandler(enabled = currentScreen != ScreenState.MAIN_TABS) {
        currentScreen = ScreenState.MAIN_TABS
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            if (currentScreen == ScreenState.MAIN_TABS) {
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 8.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    NavigationBarItem(
                        selected = currentTab == MainTab.PROYECTOS,
                        onClick = { currentTab = MainTab.PROYECTOS },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Proyectos"
                            )
                        },
                        label = {
                            Text(
                                "Proyectos",
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == MainTab.PROYECTOS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GsciGreen,
                            indicatorColor = GsciGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.GALERIA,
                        onClick = { currentTab = MainTab.GALERIA },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Galería"
                            )
                        },
                        label = {
                            Text(
                                "Galería",
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == MainTab.GALERIA) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GsciGreen,
                            indicatorColor = GsciGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )

                    NavigationBarItem(
                        selected = currentTab == MainTab.ACERCA_DE,
                        onClick = { currentTab = MainTab.ACERCA_DE },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Acerca de"
                            )
                        },
                        label = {
                            Text(
                                "Acerca de",
                                fontSize = 11.sp,
                                fontWeight = if (currentTab == MainTab.ACERCA_DE) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = GsciGreen,
                            indicatorColor = GsciGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            when (currentScreen) {
                ScreenState.MAIN_TABS -> {
                    when (currentTab) {
                        MainTab.PROYECTOS -> {
                            MainProjectsScreen(
                                projects = projects,
                                activeProject = activeProject,
                                onSelectProject = { proj -> viewModel.selectActiveProject(proj) },
                                onOpenNewProjectDialog = { showNewProjectDialog = true },
                                onOpenEditProjectDialog = { proj -> editingProject = proj },
                                onOpenSettings = { currentScreen = ScreenState.SETTINGS },
                                onOpenCamera = { currentScreen = ScreenState.CAMERA },
                                onOpenGalleryForProject = { proj ->
                                    viewModel.selectActiveProject(proj)
                                    currentTab = MainTab.GALERIA
                                },
                                onDeleteProject = { proj -> deletingProject = proj }
                            )
                        }
                        MainTab.GALERIA -> {
                            GalleryScreen(
                                photos = allPhotos,
                                onSelectPhoto = { photo ->
                                    viewModel.setSelectedPhoto(photo)
                                    currentScreen = ScreenState.PHOTO_RESULT
                                },
                                onEditPhotoNote = { photo ->
                                    viewModel.setSelectedPhoto(photo)
                                    showDescriptionDialog = true
                                }
                            )
                        }
                        MainTab.ACERCA_DE -> {
                            AboutScreen()
                        }
                    }
                }

                ScreenState.SETTINGS -> {
                    SettingsScreen(
                        settings = settings,
                        onBack = { currentScreen = ScreenState.MAIN_TABS },
                        onToggleFecha = { viewModel.toggleFecha() },
                        onToggleHora = { viewModel.toggleHora() },
                        onToggleGps = { viewModel.toggleGps() },
                        onToggleUtm = { viewModel.toggleUtm() },
                        onToggleAltitud = { viewModel.toggleAltitud() },
                        onToggleAzimut = { viewModel.toggleAzimut() },
                        onToggleDescripcion = { viewModel.toggleDescripcion() },
                        onToggleLogo = { viewModel.toggleLogo() },
                        onToggleNombreProyecto = { viewModel.toggleNombreProyecto() },
                        onToggleNumeroFoto = { viewModel.toggleNumeroFoto() },
                        onToggleSaveOriginal = { viewModel.toggleSaveOriginal() },
                        onSelectAltitudeUnit = { activeSelectionDialog = SelectionDialogType.ALTITUDE_UNIT },
                        onSelectCoordFormat = { activeSelectionDialog = SelectionDialogType.COORD_FORMAT },
                        onSelectCoordSystem = { activeSelectionDialog = SelectionDialogType.COORD_SYSTEM },
                        onSelectUtmZone = { activeSelectionDialog = SelectionDialogType.UTM_ZONE },
                        onSelectQuality = { activeSelectionDialog = SelectionDialogType.IMAGE_QUALITY },
                        onSelectTemplate = { /* Template option */ },
                        onSelectColor = { /* Color option */ }
                    )
                }

                ScreenState.CAMERA -> {
                    CameraCaptureScreen(
                        activeProject = activeProject,
                        latestPhoto = latestPhoto,
                        telemetry = telemetryState,
                        settings = settings,
                        onCapturePhoto = {
                            viewModel.capturePhoto(
                                imagePath = "",
                                drawableRes = R.drawable.img_excavadora_1785933737096
                            )
                            currentScreen = ScreenState.PHOTO_RESULT
                        },
                        onOpenGallery = {
                            currentTab = MainTab.GALERIA
                            currentScreen = ScreenState.MAIN_TABS
                        },
                        onOpenRecentPhoto = { photo ->
                            viewModel.setSelectedPhoto(photo)
                            currentScreen = ScreenState.PHOTO_RESULT
                        },
                        onOpenSettings = { currentScreen = ScreenState.SETTINGS },
                        onOpenDescriptionDialog = { showDescriptionDialog = true }
                    )
                }

                ScreenState.PHOTO_RESULT -> {
                    PhotoResultScreen(
                        photo = selectedPhoto ?: latestPhoto,
                        settings = settings,
                        onBack = {
                            currentScreen = ScreenState.MAIN_TABS
                        },
                        onEditDescription = { showDescriptionDialog = true }
                    )
                }
            }

            // Dialogs
            if (showNewProjectDialog) {
                NewProjectDialog(
                    onDismiss = { showNewProjectDialog = false },
                    onCreate = { projName, category ->
                        viewModel.createProject(projName, category)
                        showNewProjectDialog = false
                    }
                )
            }

            editingProject?.let { projToEdit ->
                EditProjectDialog(
                    project = projToEdit,
                    onDismiss = { editingProject = null },
                    onSave = { updated ->
                        viewModel.updateProject(updated)
                        editingProject = null
                    }
                )
            }

            deletingProject?.let { projToDelete ->
                DeleteProjectDialog(
                    project = projToDelete,
                    onDismiss = { deletingProject = null },
                    onConfirmDelete = {
                        viewModel.deleteProject(projToDelete)
                        deletingProject = null
                    }
                )
            }

            if (showDescriptionDialog) {
                DescriptionDialog(
                    initialDescription = selectedPhoto?.description ?: currentDescription,
                    onDismiss = { showDescriptionDialog = false },
                    onSave = { newDesc ->
                        val targetPhoto = selectedPhoto ?: latestPhoto
                        if (targetPhoto != null) {
                            viewModel.updatePhotoDescription(targetPhoto.id, newDesc)
                        } else {
                            viewModel.updateCurrentDescription(newDesc)
                        }
                        showDescriptionDialog = false
                    }
                )
            }

            activeSelectionDialog?.let { dialogType ->
                val title: String
                val options: List<String>
                val current: String
                val onSelect: (String) -> Unit

                when (dialogType) {
                    SelectionDialogType.ALTITUDE_UNIT -> {
                        title = "Unidad de Altitud"
                        options = listOf("Metros (m)", "Pies (ft)")
                        current = settings.altitudeUnit
                        onSelect = { viewModel.setAltitudeUnit(it) }
                    }
                    SelectionDialogType.COORD_FORMAT -> {
                        title = "Formato de Latitud/Longitud"
                        options = listOf("Grados Decimales (DD)", "Grados, Minutos, Segundos (DMS)")
                        current = settings.coordFormat
                        onSelect = { viewModel.setCoordFormat(it) }
                    }
                    SelectionDialogType.COORD_SYSTEM -> {
                        title = "Sistema de Coordenadas"
                        options = listOf("WGS 84 / UTM", "PSAD56 / UTM", "SIRGAS 2000")
                        current = settings.coordSystem
                        onSelect = { viewModel.setCoordSystem(it) }
                    }
                    SelectionDialogType.UTM_ZONE -> {
                        title = "Zona UTM"
                        options = listOf("18S", "19S", "17S", "18N")
                        current = settings.utmZone
                        onSelect = { viewModel.setUtmZone(it) }
                    }
                    SelectionDialogType.IMAGE_QUALITY -> {
                        title = "Calidad de Imagen"
                        options = listOf("Original (Sin compresión)", "Alta (90%)", "Media (75%)")
                        current = settings.imageQuality
                        onSelect = { viewModel.setImageQuality(it) }
                    }
                }

                SettingsSelectionDialog(
                    title = title,
                    options = options,
                    currentSelected = current,
                    onDismiss = { activeSelectionDialog = null },
                    onSelect = { sel ->
                        onSelect(sel)
                        activeSelectionDialog = null
                    }
                )
            }
        }
    }
}
