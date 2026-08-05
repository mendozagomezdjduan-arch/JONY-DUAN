package com.example.utils

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

data class UtmCoordinate(
    val zone: String,
    val easting: Double,
    val northing: Double,
    val formattedEast: String,
    val formattedNorth: String
)

object UtmConverter {
    fun convertLatLonToUtm(lat: Double, lon: Double): UtmCoordinate {
        val a = 6378137.0 // WGS84 equatorial radius
        val f = 1.0 / 298.257223563 // WGS84 flattening
        val k0 = 0.9996

        val zoneNumber = floor((lon + 180.0) / 6.0).toInt() + 1
        val isSouthernHemi = lat < 0
        val bandLetter = if (isSouthernHemi) "S" else "N"
        val zoneStr = "${zoneNumber}${bandLetter}"

        val lon0 = (zoneNumber - 1) * 6.0 - 180.0 + 3.0 // central meridian
        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lon)
        val lon0Rad = Math.toRadians(lon0)

        val e = Math.sqrt(2 * f - f * f)
        val e2 = e * e
        val ePrime2 = e2 / (1.0 - e2)

        val N = a / Math.sqrt(1.0 - e2 * sin(latRad) * sin(latRad))
        val T = tan(latRad) * tan(latRad)
        val C = ePrime2 * cos(latRad) * cos(latRad)
        val A = (lonRad - lon0Rad) * cos(latRad)

        val M = a * (
            (1.0 - e2 / 4.0 - 3.0 * e2 * e2 / 64.0 - 5.0 * e2 * e2 * e2 / 256.0) * latRad
                - (3.0 * e2 / 8.0 + 3.0 * e2 * e2 / 32.0 + 45.0 * e2 * e2 * e2 / 1024.0) * sin(2.0 * latRad)
                + (15.0 * e2 * e2 / 256.0 + 45.0 * e2 * e2 * e2 / 1024.0) * sin(4.0 * latRad)
                - (35.0 * e2 * e2 * e2 / 3072.0) * sin(6.0 * latRad)
        )

        var easting = k0 * N * (
            A + (1.0 - T + C) * A * A * A / 6.0
                + (5.0 - 18.0 * T + T * T + 72.0 * C - 58.0 * ePrime2) * A * A * A * A * A / 120.0
        ) + 500000.0

        var northing = k0 * (
            M + N * tan(latRad) * (
                A * A / 2.0 + (5.0 - T + 9.0 * C + 4.0 * C * C) * A * A * A * A / 24.0
                    + (61.0 - 58.0 * T + T * T + 600.0 * C - 330.0 * ePrime2) * A * A * A * A * A * A / 720.0
            )
        )

        if (isSouthernHemi) {
            northing += 10000000.0 // 10,000,000 meter offset for southern hemisphere
        }

        val eastFormatted = String.format("%.3f", easting)
        val northFormatted = String.format("%.3f", northing)

        return UtmCoordinate(
            zone = zoneStr,
            easting = easting,
            northing = northing,
            formattedEast = eastFormatted,
            formattedNorth = northFormatted
        )
    }

    fun getDirectionLabel(azimuthDegrees: Int): String {
        val normalized = (azimuthDegrees % 360 + 360) % 360
        return when {
            normalized in 23..67 -> "NE"
            normalized in 68..112 -> "E"
            normalized in 113..157 -> "SE"
            normalized in 158..202 -> "S"
            normalized in 203..247 -> "SO"
            normalized in 248..292 -> "O"
            normalized in 293..337 -> "NO"
            else -> "N"
        }
    }

    fun formatAltitude(meters: Double, unit: String): String {
        return if (unit.contains("Pies") || unit.contains("ft")) {
            val feet = meters * 3.28084
            String.format(java.util.Locale.US, "%.1f ft", feet)
        } else {
            String.format(java.util.Locale.US, "%.1f msnm", meters)
        }
    }

    fun formatAltitudeString(altitudeStr: String, unit: String): String {
        val numeric = altitudeStr.replace("[^0-9.-]".toRegex(), "").toDoubleOrNull() ?: return altitudeStr
        return formatAltitude(numeric, unit)
    }

    fun decimalToDmsLat(lat: Double): String {
        val dir = if (lat >= 0) "N" else "S"
        val absLat = abs(lat)
        val degrees = absLat.toInt()
        val minutesDouble = (absLat - degrees) * 60.0
        val minutes = minutesDouble.toInt()
        val seconds = (minutesDouble - minutes) * 60.0
        return String.format(java.util.Locale.US, "%d°%02d'%04.1f\"%s", degrees, minutes, seconds, dir)
    }

    fun decimalToDmsLon(lon: Double): String {
        val dir = if (lon >= 0) "E" else "O"
        val absLon = abs(lon)
        val degrees = absLon.toInt()
        val minutesDouble = (absLon - degrees) * 60.0
        val minutes = minutesDouble.toInt()
        val seconds = (minutesDouble - minutes) * 60.0
        return String.format(java.util.Locale.US, "%d°%02d'%04.1f\"%s", degrees, minutes, seconds, dir)
    }

    fun formatLatLon(lat: Double, lon: Double, format: String): Pair<String, String> {
        return if (format.contains("DMS") || format.contains("GMS")) {
            Pair(decimalToDmsLat(lat), decimalToDmsLon(lon))
        } else {
            Pair(
                String.format(java.util.Locale.US, "%.6f° %s", abs(lat), if (lat >= 0) "N" else "S"),
                String.format(java.util.Locale.US, "%.6f° %s", abs(lon), if (lon >= 0) "E" else "O")
            )
        }
    }
}
