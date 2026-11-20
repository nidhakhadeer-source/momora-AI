package com.example.SIMATS.ui.roleselect

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.SIMATS.data.model.UserRole
import com.example.SIMATS.ui.theme.InterFontFamily
import com.example.SIMATS.ui.theme.ThemeManager

@Composable
fun RoleSelectScreen(
    modifier: Modifier = Modifier,
    onRoleSelected: (UserRole) -> Unit = {},
    onNavigateBack: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()
    val isDark = ThemeManager.isDarkTheme()

    // Pastel card styling for Patient (Pink), Doctor (Blue), Admin (Yellow/Orange)
    val patientCardBg = if (isDark) Color(0xFF281C23) else Color(0xFFFDF0F4)
    val patientBorder = if (isDark) Color(0xFF48303E) else Color(0xFFF8DCE3)
    val patientIconBg = if (isDark) Color(0xFF3B2533) else Color(0xFFF8D3DC)
    val patientIconColor = if (isDark) Color(0xFFE89BB4) else Color(0xFFC04B76)

    val doctorCardBg = if (isDark) Color(0xFF172233) else Color(0xFFF0F5FD)
    val doctorBorder = if (isDark) Color(0xFF2B3F5B) else Color(0xFFD6E3F8)
    val doctorIconBg = if (isDark) Color(0xFF21324B) else Color(0xFFCCE0F8)
    val doctorIconColor = if (isDark) Color(0xFF93C5FD) else Color(0xFF2563EB)

    val adminCardBg = if (isDark) Color(0xFF2A2016) else Color(0xFFFEF8ED)
    val adminBorder = if (isDark) Color(0xFF4E3C27) else Color(0xFFFDE4C2)
    val adminIconBg = if (isDark) Color(0xFF3B2E1D) else Color(0xFFFCDCB0)
    val adminIconColor = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onNavigateBack != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "←",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(40.dp))
            }
        }

        // 2. Main Content Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Header Title
            Text(
                text = "Select Your Role",
                fontFamily = InterFontFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "Continue as:",
                fontFamily = InterFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 1. Patient Card (Pink - Expectant Mother)
            RoleSelectionCard(
                title = "Patient",
                subtitle = "Expectant Mother",
                cardBgColor = patientCardBg,
                cardBorderColor = patientBorder,
                iconBgColor = patientIconBg,
                icon = { PatientMotherIcon(color = patientIconColor, modifier = Modifier.size(24.dp)) },
                onClick = { onRoleSelected(UserRole.PATIENT) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Doctor Card (Blue - Medical Professional)
            RoleSelectionCard(
                title = "Doctor",
                subtitle = "Medical Professional",
                cardBgColor = doctorCardBg,
                cardBorderColor = doctorBorder,
                iconBgColor = doctorIconBg,
                icon = { DoctorStethoscopeIcon(color = doctorIconColor, modifier = Modifier.size(24.dp)) },
                onClick = { onRoleSelected(UserRole.DOCTOR) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Admin Card (Yellow/Orange - Administrator)
            RoleSelectionCard(
                title = "Admin",
                subtitle = "Administrator",
                cardBgColor = adminCardBg,
                cardBorderColor = adminBorder,
                iconBgColor = adminIconBg,
                icon = { AdminShieldIcon(color = adminIconColor, modifier = Modifier.size(24.dp)) },
                onClick = { onRoleSelected(UserRole.ADMIN) }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. Bottom Indicator Bar
        Box(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally)
                .width(134.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        )
    }
}

/**
 * Large rounded role selection card with soft elevation, icon circle on left,
 * title/subtitle in middle, ">" arrow on right, and an interactive press animation.
 */
@Composable
private fun RoleSelectionCard(
    title: String,
    subtitle: String,
    cardBgColor: Color,
    cardBorderColor: Color,
    iconBgColor: Color,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "RoleCardScale"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 1.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(1.dp, cardBorderColor, RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = title,
                        fontFamily = InterFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = subtitle,
                        fontFamily = InterFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = ">",
                fontFamily = InterFontFamily,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Expectant Mother / Patient vector icon drawing.
 */
@Composable
private fun PatientMotherIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        // Head
        drawCircle(
            color = color,
            radius = w * 0.18f,
            center = Offset(w * 0.45f, h * 0.28f),
            style = Stroke(width = strokeWidth)
        )

        // Pregnant silhouette body & belly
        val bodyPath = Path().apply {
            moveTo(w * 0.30f, h * 0.85f)
            cubicTo(w * 0.30f, h * 0.55f, w * 0.35f, h * 0.48f, w * 0.45f, h * 0.48f)
            cubicTo(w * 0.55f, h * 0.48f, w * 0.75f, h * 0.56f, w * 0.75f, h * 0.70f)
            cubicTo(w * 0.75f, h * 0.82f, w * 0.60f, h * 0.85f, w * 0.42f, h * 0.85f)
        }
        drawPath(bodyPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

        // Heart accent near belly
        val heartPath = Path().apply {
            moveTo(w * 0.60f, h * 0.68f)
            cubicTo(w * 0.55f, h * 0.62f, w * 0.50f, h * 0.64f, w * 0.50f, h * 0.68f)
            cubicTo(w * 0.50f, h * 0.73f, w * 0.60f, h * 0.78f, w * 0.60f, h * 0.78f)
            cubicTo(w * 0.60f, h * 0.78f, w * 0.70f, h * 0.73f, w * 0.70f, h * 0.68f)
            cubicTo(w * 0.70f, h * 0.64f, w * 0.65f, h * 0.62f, w * 0.60f, h * 0.68f)
        }
        drawPath(heartPath, color = color, style = Stroke(width = 1.2.dp.toPx()))
    }
}

/**
 * Medical Professional / Doctor Stethoscope vector icon drawing.
 */
@Composable
private fun DoctorStethoscopeIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val tubePath = Path().apply {
            moveTo(w * 0.30f, h * 0.20f)
            lineTo(w * 0.30f, h * 0.45f)
            cubicTo(w * 0.30f, h * 0.75f, w * 0.70f, h * 0.75f, w * 0.70f, h * 0.45f)
            lineTo(w * 0.70f, h * 0.20f)
        }
        drawPath(tubePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Earpieces
        drawCircle(color = color, radius = strokeWidth * 1.2f, center = Offset(w * 0.30f, h * 0.20f))
        drawCircle(color = color, radius = strokeWidth * 1.2f, center = Offset(w * 0.70f, h * 0.20f))

        // Chest piece
        val chestLine = Path().apply {
            moveTo(w * 0.50f, h * 0.68f)
            lineTo(w * 0.50f, h * 0.78f)
        }
        drawPath(chestLine, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
        drawCircle(color = color, radius = strokeWidth * 2f, center = Offset(w * 0.50f, h * 0.82f))
    }
}

/**
 * Administrator / Shield vector icon drawing.
 */
@Composable
private fun AdminShieldIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val shieldPath = Path().apply {
            moveTo(w * 0.50f, h * 0.15f)
            lineTo(w * 0.85f, h * 0.28f)
            cubicTo(w * 0.85f, h * 0.60f, w * 0.65f, h * 0.84f, w * 0.50f, h * 0.90f)
            cubicTo(w * 0.35f, h * 0.84f, w * 0.15f, h * 0.60f, w * 0.15f, h * 0.28f)
            close()
        }
        drawPath(shieldPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Keyhole / badge in center
        drawCircle(color = color, radius = strokeWidth * 1.8f, center = Offset(w * 0.50f, h * 0.46f))
        val keyPath = Path().apply {
            moveTo(w * 0.46f, h * 0.50f)
            lineTo(w * 0.54f, h * 0.50f)
            lineTo(w * 0.52f, h * 0.66f)
            lineTo(w * 0.48f, h * 0.66f)
            close()
        }
        drawPath(keyPath, color = color)
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectScreenPreview() {
    RoleSelectScreen()
}
