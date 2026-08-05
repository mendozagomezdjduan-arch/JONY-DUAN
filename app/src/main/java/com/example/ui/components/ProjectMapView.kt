package com.example.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Satellite
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PhotoEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GsciGreen
import java.io.File
import kotlin.math.cos
import kotlin.math.sin

enum class MapStyle(val title: String, val icon: @Composable () -> Unit) {
    SATELLITE("Satelital", { Icon(Icons.Default.Satellite, contentDescription = null, tint = GsciGreen) }),
    TOPOGRAPHIC("Topográfico", { Icon(Icons.Default.Terrain, contentDescription = null, tint = Color(0xFFFFB74D)) }),
    CADASTRAL("Cadastral UTM", { Icon(Icons.Default.GridOn, contentDescription = null, tint = Color(0xFF64B5F6)) })
}

@Composable
fun ProjectMapView(
    photos: List<PhotoEntity>,
    onSelectPhoto: (PhotoEntity) -> Unit,
    onEditPhotoNote: (PhotoEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedMapStyle by remember { mutableStateOf(MapStyle.SATELLITE) }
    var selectedPhoto by remember(photos) { mutableStateOf<PhotoEntity?>(photos.firstOrNull()) }

    // Map gesture transformations
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Parse numeric UTM coordinates for plotting
    val photoPoints = remember(photos) {
        photos.map { photo ->
            val e = photo.east.replace("[^0-9.]".toRegex(), "").toDoubleOrNull() ?: 278945.0
            val n = photo.north.replace("[^0-9.]".toRegex(), "").toDoubleOrNull() ?: 8654321.0
            Triple(photo, e, n)
        }
    }

    // Bounding calculation for auto-scaling
    val (minE, maxE, minN, maxN) = remember(photoPoints) {
        if (photoPoints.isEmpty()) {
            Tuple4(278900.0, 279000.0, 8654000.0, 8654500.0)
        } else {
            val eList = photoPoints.map { it.second }
            val nList = photoPoints.map { it.third }
            var minE = eList.minOrNull() ?: 278900.0
            var maxE = eList.maxOrNull() ?: 279000.0
            var minN = nList.minOrNull() ?: 8654000.0
            var maxN = nList.maxOrNull() ?: 8654500.0

            // Ensure non-zero range
            if (maxE - minE < 50.0) {
                minE -= 25.0
                maxE += 25.0
            }
            if (maxN - minN < 50.0) {
                minN -= 25.0
                maxN += 25.0
            }
            Tuple4(minE, maxE, minN, maxN)
        }
    }

    val centerE = (minE + maxE) / 2.0
    val centerN = (minN + maxN) / 2.0
    val rangeE = (maxE - minE).coerceAtLeast(10.0)
    val rangeN = (maxN - minN).coerceAtLeast(10.0)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F1412))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
    ) {
        // Interactive Canvas Map
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.5f, 5f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height

            val mapCenterX = canvasW / 2f + offsetX
            val mapCenterY = canvasH / 2f + offsetY

            // Scale factor mapping UTM meters to Canvas Pixels
            val baseScale = (canvasW.coerceAtMost(canvasH) * 0.6f) / rangeE.coerceAtLeast(rangeN).toFloat()
            val currentScale = baseScale * scale

            // 1. Draw Map Background based on MapStyle
            when (selectedMapStyle) {
                MapStyle.SATELLITE -> {
                    drawRect(color = Color(0xFF141C18))
                    // Satellite terrain grid texture
                    val gridStep = 80f * scale
                    var x = (mapCenterX % gridStep)
                    while (x < canvasW) {
                        drawLine(
                            color = Color(0x114CAF50),
                            start = Offset(x, 0f),
                            end = Offset(x, canvasH),
                            strokeWidth = 1f
                        )
                        x += gridStep
                    }
                    var y = (mapCenterY % gridStep)
                    while (y < canvasH) {
                        drawLine(
                            color = Color(0x114CAF50),
                            start = Offset(0f, y),
                            end = Offset(canvasW, y),
                            strokeWidth = 1f
                        )
                        y += gridStep
                    }
                }
                MapStyle.TOPOGRAPHIC -> {
                    drawRect(color = Color(0xFF1B231F))
                    // Contour rings
                    for (r in 100..800 step 120) {
                        drawCircle(
                            color = Color(0x2281C784),
                            center = Offset(mapCenterX, mapCenterY),
                            radius = r * scale,
                            style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)))
                        )
                    }
                }
                MapStyle.CADASTRAL -> {
                    drawRect(color = Color(0xFF0D110F))
                    // Engineering UTM Grid lines
                    val gridStep = 100f * scale
                    var x = (mapCenterX % gridStep)
                    while (x < canvasW) {
                        drawLine(
                            color = Color(0x3300E676),
                            start = Offset(x, 0f),
                            end = Offset(x, canvasH),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                        )
                        x += gridStep
                    }
                    var y = (mapCenterY % gridStep)
                    while (y < canvasH) {
                        drawLine(
                            color = Color(0x3300E676),
                            start = Offset(0f, y),
                            end = Offset(canvasW, y),
                            strokeWidth = 1f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                        )
                        y += gridStep
                    }
                }
            }

            // 2. Draw Connecting Track Path between photo survey points
            if (photoPoints.size > 1) {
                val path = Path()
                photoPoints.forEachIndexed { idx, (_, e, n) ->
                    val px = mapCenterX + ((e - centerE) * currentScale).toFloat()
                    val py = mapCenterY - ((n - centerN) * currentScale).toFloat() // Y grows downward in canvas
                    if (idx == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                drawPath(
                    path = path,
                    color = Color(0xFF00E676),
                    style = Stroke(
                        width = 3f * scale.coerceAtLeast(1f),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                    )
                )
            }

            // 3. Draw Photo Markers and Azimuth Vision Cones
            photoPoints.forEach { (photo, e, n) ->
                val px = mapCenterX + ((e - centerE) * currentScale).toFloat()
                val py = mapCenterY - ((n - centerN) * currentScale).toFloat()

                val isSelected = selectedPhoto?.id == photo.id

                // Draw Camera Azimuth Orientation Cone (Heading Direction)
                val azRad = Math.toRadians(photo.azimuthDegrees.toDouble())
                val coneLength = 50f * scale.coerceAtLeast(1f)
                val spreadAngle = Math.toRadians(25.0)

                val leftAngle = azRad - spreadAngle
                val rightAngle = azRad + spreadAngle

                val x1 = px + (coneLength * sin(leftAngle)).toFloat()
                val y1 = py - (coneLength * cos(leftAngle)).toFloat()
                val x2 = px + (coneLength * sin(rightAngle)).toFloat()
                val y2 = py - (coneLength * cos(rightAngle)).toFloat()

                val conePath = Path().apply {
                    moveTo(px, py)
                    lineTo(x1, y1)
                    lineTo(x2, y2)
                    close()
                }

                drawPath(
                    path = conePath,
                    color = if (isSelected) Color(0x6600E676) else Color(0x33FFD54F)
                )
                drawPath(
                    path = conePath,
                    color = if (isSelected) GsciGreen else Color(0xFFFFD54F),
                    style = Stroke(width = 1.5f)
                )

                // Selection Glow Ring
                if (isSelected) {
                    drawCircle(
                        color = GsciGreen,
                        center = Offset(px, py),
                        radius = 24f * scale.coerceAtLeast(1f),
                        style = Stroke(width = 3f)
                    )
                }

                // Photo Marker Circle Badge
                drawCircle(
                    color = if (isSelected) GsciGreen else Color(0xFF1E2622),
                    center = Offset(px, py),
                    radius = 16f * scale.coerceAtLeast(1f)
                )
                drawCircle(
                    color = Color.White,
                    center = Offset(px, py),
                    radius = 16f * scale.coerceAtLeast(1f),
                    style = Stroke(width = 2f)
                )
            }
        }

        // Overlay Map Touch Marker Selection hit-boxes
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(photoPoints, scale, offsetX, offsetY) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // consumed in background canvas
                    }
                }
        ) {
            // Hit targets handled via bottom list or direct click logic
        }

        // Top Control Bar: Style Switcher & Center Reset
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Map Style selector pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xDD0F1311))
                    .padding(4.dp)
            ) {
                MapStyle.values().forEach { style ->
                    val isSelected = style == selectedMapStyle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) GsciGreen else Color.Transparent)
                            .clickable { selectedMapStyle = style }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            style.icon()
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = style.title,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Recenter Map Button
            IconButton(
                onClick = {
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xDD0F1311))
                    .border(1.dp, DarkBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Re-centrar mapa",
                    tint = GsciGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Top Left Map Scale & Coordinate System Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 60.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xDD0F1311))
                .border(1.dp, DarkBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Column {
                Text(
                    text = "SISTEMA: WGS 84 / UTM",
                    color = GsciGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "CENTRO E: ${centerE.toInt()} N: ${centerN.toInt()}",
                    color = Color(0xFFA0B0A8),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Bottom Selected Photo Inspection Card Overlay
        selectedPhoto?.let { photo ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xF20F1311))
                    .border(1.dp, GsciGreen, RoundedCornerShape(12.dp))
                    .clickable { onSelectPhoto(photo) }
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Thumbnail
                    val bitmap = remember(photo.imagePath) {
                        if (photo.imagePath.isNotBlank() && File(photo.imagePath).exists()) {
                            BitmapFactory.decodeFile(photo.imagePath)
                        } else null
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = photo.projectName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            val imgRes = photo.imageDrawableRes ?: R.drawable.img_excavadora_1785933737096
                            Image(
                                painter = painterResource(id = imgRes),
                                contentDescription = photo.projectName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Foto #${photo.photoNumber}",
                                color = GsciGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${photo.projectName}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "ESTE: ${photo.east} • NORTE: ${photo.north}",
                            color = Color(0xFFA0B0A8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ALT: ${photo.altitudeMeters} • AZIMUT: ${photo.azimuthDegrees}° ${photo.azimuthDirection}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (photo.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "📝 ${photo.description}",
                                color = Color(0xFFFFD54F),
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF263238))
                                .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(6.dp))
                                .clickable { onEditPhotoNote(photo) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar Nota",
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Nota",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GsciGreen)
                                .clickable { onSelectPhoto(photo) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Ver Foto",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class Tuple4<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
