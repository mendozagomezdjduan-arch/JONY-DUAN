package com.example.ui.screens

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PhotoEntity
import com.example.data.model.SettingsState
import com.example.ui.components.WatermarkOverlayCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.GsciGreen
import java.io.File

@Composable
fun PhotoResultScreen(
    photo: PhotoEntity?,
    settings: SettingsState,
    onBack: () -> Unit,
    onEditDescription: () -> Unit
) {
    val context = LocalContext.current

    if (photo == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("No hay fotografía seleccionada", color = Color.White)
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Photo Image: render file path bitmap or fallback drawable
        val bitmap = remember(photo.imagePath) {
            if (photo.imagePath.isNotBlank() && File(photo.imagePath).exists()) {
                BitmapFactory.decodeFile(photo.imagePath)
            } else null
        }

        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Fotografía con metadatos",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val imageRes = photo.imageDrawableRes ?: R.drawable.img_excavadora_1785933737096
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Fotografía con metadatos",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Action Bar

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x99000000))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Fotografía con Metadatos",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row {
                IconButton(onClick = onEditDescription) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Descripción",
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Fotografía exportada con sello GSCI INGENIERÍA en alta resolución.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir",
                        tint = GsciGreen
                    )
                }
            }
        }

        // Stamped Watermark Card Overlay at Bottom (Matching Screen 4)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            WatermarkOverlayCard(photo = photo, settings = settings)
        }
    }
}
