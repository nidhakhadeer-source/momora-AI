package com.example.SIMATS.ui.screens.splash

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.ui.theme.MomoraAiTheme

@Composable
fun SplashScreen(viewModel: SplashViewModel? = null) {
    // Colors based on the provided design
    val colorPrimary = Color(0xFF6A4E77) // Dark Purple for Momora AI
    val colorTextDark = Color(0xFF2C2C2C)
    val colorTextLight = Color(0xFF8E8E8E)
    val colorPinkLight = Color(0xFFFAEDF2)
    val colorPurpleLight = Color(0xFFF1EEF6)
    val colorGreenLight = Color(0xFFE8F5ED)
    val colorGreenText = Color(0xFF5A9D79)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header Icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Heart Icon in Pink Background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorPinkLight),
                contentAlignment = Alignment.Center
            ) {
                HeartIcon(modifier = Modifier.size(24.dp), color = colorPrimary)
            }

            // Right Help Icon
            Box(
                modifier = Modifier
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                InfoIcon(modifier = Modifier.size(24.dp), color = colorPrimary)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Center Logo Area
        Box(
            modifier = Modifier
                .size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer Pink Circle
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(colorPinkLight)
            )

            // Inner Purple Circle
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(colorPurpleLight),
                contentAlignment = Alignment.Center
            ) {
                HeartHandsIcon(modifier = Modifier.size(80.dp), color = colorPrimary)
            }

            // Small Green Heart Badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp, end = 24.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colorGreenLight),
                contentAlignment = Alignment.Center
            ) {
                HeartIcon(modifier = Modifier.size(14.dp), color = colorPrimary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Titles
        Text(
            text = "momora AI",
            color = colorPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "A little care. Every day.",
            color = colorTextDark,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Personalized pregnancy monitoring and gentle\nguidance, together.",
            color = colorTextLight,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(colorGreenLight)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "YOUR JOURNEY, SUPPORTED",
                color = colorGreenText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Pager Indicator Dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(colorPrimary)
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(colorPinkLight)
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(colorPinkLight)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer Text
        Text(
            text = "Private by design. Made with compassion.",
            color = colorTextLight,
            fontSize = 11.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MomoraAiTheme {
        SplashScreen()
    }
}

@Composable
fun HeartIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val path = Path().apply {
            moveTo(width / 2, height / 5)
            cubicTo(width * 5 / 14, 0f, 0f, height / 15, width / 28, height * 2 / 5)
            cubicTo(width / 14, height * 2 / 3, width * 3 / 7, height * 5 / 6, width / 2, height * 0.95f)
            cubicTo(width * 4 / 7, height * 5 / 6, width * 13 / 14, height * 2 / 3, width * 27 / 28, height * 2 / 5)
            cubicTo(width, height / 15, width * 9 / 14, 0f, width / 2, height / 5)
            close()
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun InfoIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val strokeWidth = 2.dp.toPx()
        
        // Draw outer circle
        drawCircle(
            color = color,
            radius = width / 2 - strokeWidth / 2,
            style = Stroke(width = strokeWidth)
        )
        
        // Draw question mark (simplified as a path)
        val path = Path().apply {
            moveTo(width * 0.35f, height * 0.35f)
            cubicTo(width * 0.35f, height * 0.2f, width * 0.65f, height * 0.2f, width * 0.65f, height * 0.35f)
            cubicTo(width * 0.65f, height * 0.5f, width * 0.5f, height * 0.5f, width * 0.5f, height * 0.6f)
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        
        // Draw dot
        drawCircle(
            color = color,
            radius = strokeWidth / 1.5f,
            center = Offset(width * 0.5f, height * 0.75f)
        )
    }
}

@Composable
fun HeartHandsIcon(modifier: Modifier = Modifier, color: Color) {
    // A stylized icon of hands forming a heart
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val strokeWidth = 3.dp.toPx()

        // We draw two shapes that interlock to look like hands forming a heart
        val leftHandPath = Path().apply {
            moveTo(width * 0.5f, height * 0.25f)
            cubicTo(width * 0.25f, height * 0.05f, 0f, height * 0.3f, width * 0.15f, height * 0.6f)
            lineTo(width * 0.5f, height * 0.95f)
            lineTo(width * 0.65f, height * 0.8f)
            cubicTo(width * 0.4f, height * 0.7f, width * 0.4f, height * 0.4f, width * 0.5f, height * 0.25f)
        }
        
        val rightHandPath = Path().apply {
            moveTo(width * 0.5f, height * 0.25f)
            cubicTo(width * 0.75f, height * 0.05f, width, height * 0.3f, width * 0.85f, height * 0.6f)
            lineTo(width * 0.5f, height * 0.95f)
            lineTo(width * 0.35f, height * 0.8f)
            cubicTo(width * 0.6f, height * 0.7f, width * 0.6f, height * 0.4f, width * 0.5f, height * 0.25f)
        }

        drawPath(
            path = leftHandPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        drawPath(
            path = rightHandPath,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        
        // Some details for fingers
        drawLine(
            color = color,
            start = Offset(width * 0.2f, height * 0.65f),
            end = Offset(width * 0.35f, height * 0.55f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(width * 0.8f, height * 0.65f),
            end = Offset(width * 0.65f, height * 0.55f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
