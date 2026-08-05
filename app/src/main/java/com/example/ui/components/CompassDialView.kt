package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GsciGreen

@Composable
fun CompassDialView(
    degrees: Int = 42,
    direction: String = "NE",
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xAA121815))
                .border(1.5.dp, Color(0xFF33423A), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(56.dp)) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.width / 2

                // Outer tick ring
                for (i in 0 until 12) {
                    val angleRad = Math.toRadians((i * 30).toDouble())
                    val innerR = if (i % 3 == 0) radius - 6f else radius - 3f
                    val outerR = radius - 1f

                    val startX = center.x + (innerR * Math.sin(angleRad)).toFloat()
                    val startY = center.y - (innerR * Math.cos(angleRad)).toFloat()
                    val endX = center.x + (outerR * Math.sin(angleRad)).toFloat()
                    val endY = center.y - (outerR * Math.cos(angleRad)).toFloat()

                    drawLine(
                        color = if (i % 3 == 0) Color.White else Color(0x77FFFFFF),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (i % 3 == 0) 2f else 1f
                    )
                }

                // Rotating Needle
                rotate(degrees.toFloat(), pivot = center) {
                    // North Needle (Red)
                    val northPath = Path().apply {
                        moveTo(center.x, center.y - (radius - 8f))
                        lineTo(center.x - 5f, center.y)
                        lineTo(center.x + 5f, center.y)
                        close()
                    }
                    drawPath(northPath, color = Color(0xFFEF5350))

                    // South Needle (White)
                    val southPath = Path().apply {
                        moveTo(center.x, center.y + (radius - 8f))
                        lineTo(center.x - 5f, center.y)
                        lineTo(center.x + 5f, center.y)
                        close()
                    }
                    drawPath(southPath, color = Color(0xCCFFFFFF))
                }

                // Center pivot dot
                drawCircle(color = GsciGreen, radius = 3f, center = center)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Heading label pill
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xCC121815))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$degrees° $direction",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
