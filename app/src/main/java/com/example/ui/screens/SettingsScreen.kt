package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SettingsState
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.GsciGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    settings: SettingsState,
    onBack: () -> Unit,
    onToggleFecha: () -> Unit,
    onToggleHora: () -> Unit,
    onToggleGps: () -> Unit,
    onToggleUtm: () -> Unit,
    onToggleAltitud: () -> Unit,
    onToggleAzimut: () -> Unit,
    onToggleDescripcion: () -> Unit,
    onToggleLogo: () -> Unit,
    onToggleNombreProyecto: () -> Unit,
    onToggleNumeroFoto: () -> Unit,
    onToggleSaveOriginal: () -> Unit,
    onSelectAltitudeUnit: () -> Unit = {},
    onSelectCoordFormat: () -> Unit = {},
    onSelectCoordSystem: () -> Unit,
    onSelectUtmZone: () -> Unit,
    onSelectQuality: () -> Unit,
    onSelectTemplate: () -> Unit,
    onSelectColor: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Configuración",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // SECTION 1: INFORMACIÓN EN FOTOGRAFÍA
            Text(
                text = "INFORMACIÓN EN FOTOGRAFÍA",
                color = GsciGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            SettingsSwitchRow(
                icon = Icons.Default.CalendarToday,
                title = "Fecha",
                checked = settings.showFecha,
                onCheckedChange = { onToggleFecha() }
            )

            SettingsSwitchRow(
                icon = Icons.Default.Schedule,
                title = "Hora",
                checked = settings.showHora,
                onCheckedChange = { onToggleHora() }
            )

            SettingsSwitchRow(
                icon = Icons.Default.LocationOn,
                title = "Coordenadas GPS",
                checked = settings.showGps,
                onCheckedChange = { onToggleGps() }
            )

            SettingsSwitchRow(
                icon = Icons.Default.GridOn,
                title = "Coordenadas UTM",
                checked = settings.showUtm,
                onCheckedChange = { onToggleUtm() }
            )

            SettingsSwitchRow(
                icon = Icons.Default.Height,
                title = "Altitud",
                checked = settings.showAltitud,
                onCheckedChange = { onToggleAltitud() }
            )

            SettingsSwitchRow(
                icon = Icons.Default.Explore,
                title = "Azimut (Brújula)",
                checked = settings.showAzimut,
                onCheckedChange = { onToggleAzimut() }
            )

            SettingsSwitchRow(
                icon = Icons.Default.Description,
                title = "Descripción",
                checked = settings.showDescripcion,
                onCheckedChange = { onToggleDescripcion() }
            )

            SettingsSwitchRow(
                icon = Icons.Default.Terrain,
                title = "Logo de Empresa",
                checked = settings.showLogo,
                onCheckedChange = { onToggleLogo() }
            )

            SettingsSwitchRow(
                icon = Icons.Default.Badge,
                title = "Nombre del Proyecto",
                checked = settings.showNombreProyecto,
                onCheckedChange = { onToggleNombreProyecto() }
            )

            SettingsSwitchRow(
                icon = Icons.Default.FormatListNumbered,
                title = "Número de Foto",
                checked = settings.showNumeroFoto,
                onCheckedChange = { onToggleNumeroFoto() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 2: UNIDADES DE MEDIDA
            Text(
                text = "UNIDADES DE MEDIDA",
                color = GsciGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            SettingsOptionRow(
                title = "Unidad de Altitud",
                value = settings.altitudeUnit,
                onClick = onSelectAltitudeUnit
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 3: FORMATO COORDENADAS
            Text(
                text = "FORMATO COORDENADAS",
                color = GsciGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            SettingsOptionRow(
                title = "Formato de Latitud/Longitud",
                value = settings.coordFormat,
                onClick = onSelectCoordFormat
            )

            SettingsOptionRow(
                title = "Sistema de Coordenadas",
                value = settings.coordSystem,
                onClick = onSelectCoordSystem
            )

            SettingsOptionRow(
                title = "Zona UTM",
                value = settings.utmZone,
                onClick = onSelectUtmZone
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 4: OTROS AJUSTES
            Text(
                text = "OTROS AJUSTES",
                color = GsciGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            SettingsOptionRow(
                title = "Calidad de Imagen",
                value = settings.imageQuality,
                onClick = onSelectQuality
            )

            SettingsSwitchRow(
                icon = null,
                title = "Guardar foto original",
                subtitle = "Guarda la foto sin modificar",
                checked = settings.saveOriginal,
                onCheckedChange = { onToggleSaveOriginal() }
            )

            SettingsOptionRow(
                title = "Plantilla de Información",
                value = settings.templateInfo,
                onClick = onSelectTemplate
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectColor() }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Color de la Plantilla",
                    color = TextPrimary,
                    fontSize = 15.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = settings.templateColor,
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSwitchRow(
    icon: ImageVector?,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 15.sp
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GsciGreen,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = Color(0xFF2A332E)
            )
        )
    }
}

@Composable
fun SettingsOptionRow(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 15.sp
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                color = GsciGreen,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = GsciGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
