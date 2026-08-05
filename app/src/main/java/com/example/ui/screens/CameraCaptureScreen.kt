package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.camera.core.ImageCapture
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.example.R
import com.example.data.location.TelemetryState
import com.example.data.model.PhotoEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.SettingsState
import com.example.ui.components.CompassDialView
import com.example.ui.components.LiveWatermarkOverlayCard
import com.example.ui.theme.GsciGreen

enum class CameraFlashMode(val cameraXMode: Int, val label: String) {
    OFF(ImageCapture.FLASH_MODE_OFF, "FLASH OFF"),
    ON(ImageCapture.FLASH_MODE_ON, "FLASH ON"),
    AUTO(ImageCapture.FLASH_MODE_AUTO, "FLASH AUTO")
}

@Composable
fun CameraCaptureScreen(
    activeProject: ProjectEntity?,
    latestPhoto: PhotoEntity?,
    telemetry: TelemetryState = TelemetryState(),
    settings: SettingsState = SettingsState(),
    onCapturePhoto: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenRecentPhoto: (PhotoEntity) -> Unit = {},
    onOpenSettings: () -> Unit,
    onOpenDescriptionDialog: () -> Unit
) {
    var flashMode by remember { mutableStateOf(CameraFlashMode.OFF) }
    var aspectRatio by remember { mutableStateOf("4:3") }
    var isHdrOn by remember { mutableStateOf(true) }
    var showLiveWatermarkOverlay by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Live Background (Shows excavator trench site image representing real camera preview)
        val imageRes = latestPhoto?.imageDrawableRes ?: R.drawable.img_excavadora_1785933737096
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Camera View",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Rule of Thirds Grid Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val gridColor = Color(0x33FFFFFF)

            // Vertical grid lines
            drawLine(gridColor, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = 1f)
            drawLine(gridColor, Offset(2 * w / 3f, 0f), Offset(2 * w / 3f, h), strokeWidth = 1f)

            // Horizontal grid lines
            drawLine(gridColor, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = 1f)
            drawLine(gridColor, Offset(0f, 2 * h / 3f), Offset(w, 2 * h / 3f), strokeWidth = 1f)
        }

        // Top Control Overlay Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x77000000))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flash Mode Control (OFF -> ON -> AUTO) mapped to CameraX ImageCapture modes
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (flashMode != CameraFlashMode.OFF) GsciGreen else Color(0x33FFFFFF))
                    .clickable {
                        flashMode = when (flashMode) {
                            CameraFlashMode.OFF -> CameraFlashMode.ON
                            CameraFlashMode.ON -> CameraFlashMode.AUTO
                            CameraFlashMode.AUTO -> CameraFlashMode.OFF
                        }
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = when (flashMode) {
                            CameraFlashMode.OFF -> Icons.Default.FlashOff
                            CameraFlashMode.ON -> Icons.Default.FlashOn
                            CameraFlashMode.AUTO -> Icons.Default.FlashAuto
                        },
                        contentDescription = "Modo Flash CameraX",
                        tint = if (flashMode != CameraFlashMode.OFF) Color.Black else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = flashMode.label,
                        color = if (flashMode != CameraFlashMode.OFF) Color.Black else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isHdrOn) GsciGreen else Color(0x33FFFFFF))
                    .clickable { isHdrOn = !isHdrOn }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "HDR",
                    color = if (isHdrOn) Color.Black else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (showLiveWatermarkOverlay) GsciGreen else Color(0x33FFFFFF))
                    .clickable { showLiveWatermarkOverlay = !showLiveWatermarkOverlay }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (showLiveWatermarkOverlay) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Marca de agua previa",
                        tint = if (showLiveWatermarkOverlay) Color.Black else Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showLiveWatermarkOverlay) "SELLO ON" else "SELLO OFF",
                        color = if (showLiveWatermarkOverlay) Color.Black else Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                    .clickable {
                        aspectRatio = if (aspectRatio == "4:3") "16:9" else "4:3"
                    }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = aspectRatio,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración",
                    tint = Color.White
                )
            }
        }

        // Top Content Badges (Active Project & Compass)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Active Project Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xDD121815))
                        .border(1.dp, GsciGreen, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Column {
                        Text(
                            text = "Proyecto Activo:",
                            color = Color(0xFFA0B0A8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activeProject?.name ?: "Sin Proyecto",
                            color = GsciGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Live Compass Widget
                CompassDialView(
                    degrees = telemetry.azimuthDegrees,
                    direction = telemetry.azimuthDirection
                )
            }
        }

        // Bottom Area (Telemetry Strip + Shutter HUD)
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            // Live Watermark Overlay (Displays real-time GPS, Altitude, Azimuth, Project metadata stamp preview)
            if (showLiveWatermarkOverlay) {
                LiveWatermarkOverlayCard(
                    projectName = activeProject?.name ?: "PROYECTO GENERAL",
                    telemetry = telemetry,
                    settings = settings,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Live Telemetry Ribbon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xDD0F1311))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // UTM
                Column {
                    Text(
                        text = "UTM ${telemetry.utmZone}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "E: ${telemetry.easting}",
                        color = Color(0xFFA0B0A8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "N: ${telemetry.northing}",
                        color = Color(0xFFA0B0A8),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Altitude
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Altitud",
                        color = Color(0xFFA0B0A8),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = telemetry.altitudeMeters,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // GPS Accuracy
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "GPS ",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.SignalCellular4Bar,
                            contentDescription = "GPS Signal",
                            tint = GsciGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = telemetry.gpsAccuracyMeters,
                        color = GsciGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottom Shutter Controls HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1311))
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Recent Photo Thumbnail Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        if (latestPhoto != null) {
                            onOpenRecentPhoto(latestPhoto)
                        } else {
                            onOpenGallery()
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF212925))
                            .border(
                                width = 1.5.dp,
                                color = if (latestPhoto != null) GsciGreen else Color(0xFF33423A),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (latestPhoto != null) {
                            val imgRes = latestPhoto.imageDrawableRes ?: R.drawable.img_excavadora_1785933737096
                            Image(
                                painter = painterResource(id = imgRes),
                                contentDescription = "Última Foto Capturada",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(Color(0xDD0F1311))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "VER",
                                    color = GsciGreen,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Galería",
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (latestPhoto != null) "Última Foto" else "Galería",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Center: Shutter Button
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .border(3.5.dp, Color.White, CircleShape)
                        .background(Color.Transparent)
                        .clickable { onCapturePhoto() }
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }

                // Right: Description Note Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onOpenDescriptionDialog() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF212925))
                            .border(1.dp, Color(0xFF33423A), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Descripción",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Descripción", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}
