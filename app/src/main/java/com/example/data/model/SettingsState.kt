package com.example.data.model

data class SettingsState(
    // Información en fotografía (Metadata Overlay toggles)
    val showFecha: Boolean = true,
    val showHora: Boolean = true,
    val showGps: Boolean = true,
    val showUtm: Boolean = true,
    val showAltitud: Boolean = true,
    val showAzimut: Boolean = true,
    val showDescripcion: Boolean = true,
    val showLogo: Boolean = true,
    val showNombreProyecto: Boolean = true,
    val showNumeroFoto: Boolean = true,

    // Unidades de Medida y Formato Coordenadas
    val altitudeUnit: String = "Metros (m)", // "Metros (m)" vs "Pies (ft)"
    val coordFormat: String = "Grados Decimales (DD)", // "Grados Decimales (DD)" vs "Grados, Minutos, Segundos (DMS)"
    val coordSystem: String = "WGS 84 / UTM",
    val utmZone: String = "18S",

    // Otros Ajustes
    val imageQuality: String = "Original (Sin compresión)",
    val saveOriginal: Boolean = true,
    val templateInfo: String = "Estándar 1",
    val templateColor: String = "Blanco"
)
