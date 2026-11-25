package com.example.SIMATS.ui.nutrition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import com.example.SIMATS.data.model.NutritionMealItem
import com.example.SIMATS.data.model.NutritionPlanData
import com.example.SIMATS.data.model.NutritionPriority
import com.example.SIMATS.data.model.PriorityIconType
import com.example.SIMATS.ui.components.formatErrorMessage
import com.example.SIMATS.ui.theme.MomoraBodyMuted
import com.example.SIMATS.ui.theme.MomoraMintBadgeBg
import com.example.SIMATS.ui.theme.MomoraMintBadgeText
import com.example.SIMATS.ui.theme.MomoraPinkCircleBg
import com.example.SIMATS.ui.theme.MomoraPurpleDark
import com.example.SIMATS.ui.theme.MomoraPurplePrimary
import com.example.SIMATS.ui.theme.MomoraPurpleText
import com.example.SIMATS.ui.theme.MomoraScreenBackground

private fun cleanDots(text: String): String {
    return text.replace("??", " · ").replace("  ·  ", " · ").trim()
}

@Composable
fun NutritionPlanScreen(
    viewModel: NutritionPlanViewModel,
    modifier: Modifier = Modifier,
    selectedNavIndex: Int = 3,
    onNavigateBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavTabSelected: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    NutritionPlanScreenContent(
        modifier = modifier,
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onRetryClick = { viewModel.onEvent(NutritionPlanUiEvent.OnRetryClicked) },
        onFeaturedMealClick = { viewModel.onEvent(NutritionPlanUiEvent.OnFeaturedMealClicked) },
        onPriorityClick = { id -> viewModel.onEvent(NutritionPlanUiEvent.OnPriorityClicked(id)) },
        onSelectMealType = { type -> viewModel.onEvent(NutritionPlanUiEvent.OnSelectMealType(type)) },
        onDismissPrioritySheet = { viewModel.onEvent(NutritionPlanUiEvent.OnDismissPrioritySheet) },
        onDismissMealSheet = { viewModel.onEvent(NutritionPlanUiEvent.OnDismissMealSheet) },
        onBackClick = {
            viewModel.onEvent(NutritionPlanUiEvent.OnBackClicked)
            onNavigateBack()
        },
        onNotificationClick = {
            viewModel.onEvent(NutritionPlanUiEvent.OnNotificationClicked)
            onNotificationClick()
        },
        onNavTabSelected = { index ->
            viewModel.onEvent(NutritionPlanUiEvent.OnNavTabSelected(index))
            onNavTabSelected(index)
        }
    )
}

@Composable
fun NutritionPlanScreenContent(
    modifier: Modifier = Modifier,
    uiState: NutritionPlanUiState = NutritionPlanUiState(),
    onRetryClick: () -> Unit = {},
    onFeaturedMealClick: () -> Unit = {},
    onPriorityClick: (Int) -> Unit = {},
    onSelectMealType: (String) -> Unit = {},
    onDismissPrioritySheet: () -> Unit = {},
    onDismissMealSheet: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    selectedNavIndex: Int = 3,
    onNavTabSelected: (Int) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val data = uiState.planData

    // Priority Detail Bottom Sheet
    uiState.selectedPriority?.let { priority ->
        PriorityDetailBottomSheet(
            priority = priority,
            onDismiss = onDismissPrioritySheet
        )
    }

    // Active Meal Detail Bottom Sheet
    if (uiState.isMealSheetOpen) {
        val currentMeal = data?.meals?.find { it.type.equals(uiState.selectedMealType, ignoreCase = true) }
            ?: data?.meals?.firstOrNull()
            ?: NutritionMealItem(
                type = uiState.selectedMealType,
                title = data?.featuredMealTitle?.ifBlank { "Balanced Nourish Meal" } ?: "Balanced Nourish Meal",
                preparationMinutes = 20,
                tags = listOfNotNull(data?.featuredMealDetails?.takeIf { it.isNotBlank() }),
                ingredients = listOf(
                    "1 cup fresh washed leafy greens or vegetables",
                    "1/2 cup cooked wholesome grains (quinoa, brown rice or oats)",
                    "1 serving well-cooked protein (pasteurized eggs, tofu or poultry)",
                    "1 tbsp heart-healthy fats (olive oil, avocado or nuts)"
                ),
                steps = listOf(
                    "Wash and rinse all fresh ingredients thoroughly.",
                    "Cook grains and proteins to safe, well-done temperatures.",
                    "Assemble warmly in a bowl and drizzle with light olive oil.",
                    "Enjoy fresh in calm, relaxed bites."
                )
            )

        MealDetailBottomSheet(
            meal = currentMeal,
            onDismiss = onDismissMealSheet
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MomoraScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = MomoraPurpleText
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onNotificationClick() },
                contentAlignment = Alignment.Center
            ) {
                BellIcon(
                    color = MomoraPurplePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. Body
        if (uiState.isLoading && data == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MomoraPurplePrimary)
            }
        } else if (uiState.errorMessage != null && data == null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Unable to load nutrition plan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatErrorMessage(uiState.errorMessage),
                        fontSize = 13.sp,
                        color = Color(0xFFD32F2F),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MomoraPurplePrimary)
                            .clickable { onRetryClick() }
                            .padding(horizontal = 24.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Retry",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else if (data == null || (data.priorities.isEmpty() && data.featuredMealTitle.isEmpty() && data.meals.isEmpty())) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No nutrition plans available",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nutritional guidance tailored to your trimester will appear here.",
                        fontSize = 13.sp,
                        color = MomoraBodyMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MomoraPurplePrimary)
                            .clickable { onRetryClick() }
                            .padding(horizontal = 24.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Retry",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // Gentle Fade-In Animation when content is ready
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
                    animationSpec = tween(400),
                    initialOffsetY = { 30 }
                ),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    if (uiState.errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFEBEE))
                                .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formatErrorMessage(uiState.errorMessage),
                                    fontSize = 12.sp,
                                    color = Color(0xFFC62828),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Retry",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraPurplePrimary,
                                    modifier = Modifier.clickable { onRetryClick() }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Top Trimester Badge
                    val trimesterNum = when {
                        data.trimester in 1..3 -> data.trimester
                        data.trimesterTitle.contains("first", ignoreCase = true) -> 1
                        data.trimesterTitle.contains("third", ignoreCase = true) -> 3
                        else -> 2
                    }
                    val trimesterBadgeText = when (trimesterNum) {
                        1 -> "Trimester 1 · Weeks 1–12"
                        3 -> "Trimester 3 · Weeks 28–40"
                        else -> "Trimester 2 · Weeks 13–27"
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MomoraMintBadgeBg)
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MomoraMintBadgeText)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = trimesterBadgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MomoraMintBadgeText,
                            letterSpacing = 0.3.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Nourish you both",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = cleanDots(data.trimesterTitle.ifBlank { "Balanced ideas for your second trimester." }),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = MomoraBodyMuted
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Meal Type Switcher Chips
                    val mealChips = listOf(
                        "breakfast" to "Breakfast",
                        "lunch" to "Lunch",
                        "snack" to "Snack",
                        "dinner" to "Dinner"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        mealChips.forEach { (typeKey, label) ->
                            val isSelected = uiState.selectedMealType.equals(typeKey, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isSelected) MomoraPurplePrimary else Color.White)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MomoraPurplePrimary else Color(0xFFE2D6E8),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .clickable { onSelectMealType(typeKey) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MomoraPurpleText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Selected Meal Card with soft elevation
                    val activeMeal = data.meals.find { it.type.equals(uiState.selectedMealType, ignoreCase = true) }
                        ?: data.meals.firstOrNull()
                        ?: NutritionMealItem(
                            type = uiState.selectedMealType,
                            title = data.featuredMealTitle.ifBlank { "Chickpea & Quinoa Bowl" },
                            preparationMinutes = 25,
                            tags = listOfNotNull(data.featuredMealDetails.takeIf { it.isNotBlank() }),
                            ingredients = listOf(
                                "1/2 cup cooked quinoa",
                                "1/2 cup warm rinsed chickpeas",
                                "1 cup chopped spinach",
                                "1/4 sliced avocado",
                                "Lemon-tahini dressing"
                            ),
                            steps = listOf(
                                "Base your bowl with fluffy warm quinoa and fresh spinach.",
                                "Add rinsed chickpeas and sliced ripe avocado.",
                                "Drizzle with lemon-tahini dressing.",
                                "Toss gently and enjoy warm or at room temperature."
                            )
                        )

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(18.dp))
                            .clickable { onFeaturedMealClick() }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Meal Illustration
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFFAF0EE)),
                                contentAlignment = Alignment.Center
                            ) {
                                MealBowlIllustration(
                                    modifier = Modifier.size(width = 190.dp, height = 120.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(MomoraMintBadgeBg)
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                    ) {
                                    Text(
                                        text = "${activeMeal.type.uppercase()} INSPIRATION",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MomoraMintBadgeText,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Text(
                                    text = "${activeMeal.preparationMinutes} min prep",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MomoraBodyMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = cleanDots(activeMeal.title),
                                fontSize = 17.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurpleDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            val mealDetailsText = if (activeMeal.tags.isNotEmpty()) {
                                "${activeMeal.preparationMinutes} min · ${activeMeal.tags.joinToString(" · ")}"
                            } else {
                                data.featuredMealDetails
                            }
                            Text(
                                text = cleanDots(mealDetailsText),
                                fontSize = 12.sp,
                                color = MomoraBodyMuted
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Tap to view ingredients & steps",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MomoraPurplePrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "›",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraPurplePrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // "Gentle daily priorities" Section
                    Text(
                        text = "Gentle daily priorities",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Key nutritional pillars to support you and your baby",
                        fontSize = 12.sp,
                        color = MomoraBodyMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Priorities Card with soft elevation
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFEFE8F4), RoundedCornerShape(18.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            data.priorities.forEachIndexed { index, item ->
                                PriorityRow(
                                    item = item,
                                    onClick = { onPriorityClick(item.id) }
                                )
                                if (index < (data.priorities.size - 1)) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(Color(0xFFF7F2FA))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // 3. Bottom Dashboard Navigation Bar
        DashboardBottomNavBar(
            selectedIndex = selectedNavIndex,
            onTabSelected = onNavTabSelected
        )
    }
}

@Composable
private fun PriorityRow(
    item: NutritionPriority,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            val iconBg = when (item.iconType) {
                PriorityIconType.IRON -> Color(0xFFE8F5E9)
                PriorityIconType.CALCIUM -> Color(0xFFF3E5F5)
                PriorityIconType.HYDRATION -> Color(0xFFE1F5FE)
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                when (item.iconType) {
                    PriorityIconType.IRON -> LeafIcon(color = Color(0xFF2E7D32), modifier = Modifier.size(17.dp))
                    PriorityIconType.CALCIUM -> BottleIcon(color = MomoraPurplePrimary, modifier = Modifier.size(17.dp))
                    PriorityIconType.HYDRATION -> WaterDropIcon(color = Color(0xFF0288D1), modifier = Modifier.size(17.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = cleanDots(item.title),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleText
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = cleanDots(item.subtitle),
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    color = MomoraBodyMuted
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "›",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF8C7F92)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriorityDetailBottomSheet(
    priority: NutritionPriority,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header: Icon + Title + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val iconBg = when (priority.iconType) {
                        PriorityIconType.IRON -> Color(0xFFE8F5E9)
                        PriorityIconType.CALCIUM -> Color(0xFFF3E5F5)
                        PriorityIconType.HYDRATION -> Color(0xFFE1F5FE)
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        when (priority.iconType) {
                            PriorityIconType.IRON -> LeafIcon(color = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                            PriorityIconType.CALCIUM -> BottleIcon(color = MomoraPurplePrimary, modifier = Modifier.size(18.dp))
                            PriorityIconType.HYDRATION -> WaterDropIcon(color = Color(0xFF0288D1), modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = cleanDots(priority.title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF4ECF8))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 14.sp,
                        color = MomoraPurplePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detail text (or fallback to subtitle)
            val displayText = priority.detail.ifBlank { priority.subtitle }
            Text(
                text = cleanDots(displayText),
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = Color(0xFF2D1F38)
            )

            // Bulleted "Good food choices" list
            if (priority.foods.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Good food choices",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraPurpleDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                priority.foods.forEach { food ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MomoraPurplePrimary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = cleanDots(food),
                            fontSize = 13.5.sp,
                            color = MomoraPurpleText
                        )
                    }
                }
            }

            // Care Tip
            if (priority.tip.isNotBlank()) {
                Spacer(modifier = Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MomoraMintBadgeBg)
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = "💡",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Care Tip",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E2C28)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = cleanDots(priority.tip),
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = Color(0xFF334440)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Educational Disclaimer Note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF7F3FA))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Educational information, not medical advice",
                    fontSize = 11.5.sp,
                    color = MomoraBodyMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Close Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MomoraPurplePrimary)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Close",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealDetailBottomSheet(
    meal: NutritionMealItem,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header: Category & Title + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MomoraMintBadgeBg)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${meal.type.uppercase()} INSPIRATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraMintBadgeText,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = cleanDots(meal.title),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val tagsText = if (meal.tags.isNotEmpty()) {
                        "${meal.preparationMinutes} min · " + meal.tags.joinToString(" · ")
                    } else {
                        "${meal.preparationMinutes} min prep"
                    }
                    Text(
                        text = cleanDots(tagsText),
                        fontSize = 12.sp,
                        color = MomoraBodyMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF4ECF8))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 14.sp,
                        color = MomoraPurplePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Ingredients Section
            if (meal.ingredients.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🥗", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ingredients",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFAF7FC))
                        .border(1.dp, Color(0xFFF0E8F4), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        meal.ingredients.forEach { ingredient ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "•",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraPurplePrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = cleanDots(ingredient),
                                    fontSize = 13.5.sp,
                                    lineHeight = 19.sp,
                                    color = MomoraPurpleText
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Preparation Steps Section
            if (meal.steps.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🍳", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Preparation Steps",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraPurpleDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                meal.steps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEDE4F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraPurplePrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = cleanDots(step),
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp,
                            color = MomoraPurpleText,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Educational Disclaimer Note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF7F3FA))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Educational information, not medical advice",
                    fontSize = 11.5.sp,
                    color = MomoraBodyMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Close Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MomoraPurplePrimary)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Close",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Meal Bowl Illustration Drawing.
 */
@Composable
private fun MealBowlIllustration(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer Bowl
        drawCircle(
            color = Color(0xFFE2A782),
            radius = h * 0.42f,
            center = Offset(w * 0.50f, h * 0.50f)
        )

        drawCircle(
            color = Color(0xFFF9E8DC),
            radius = h * 0.38f,
            center = Offset(w * 0.50f, h * 0.50f)
        )

        // Quinoa & Chickpeas background
        drawCircle(
            color = Color(0xFFE8C88A),
            radius = h * 0.32f,
            center = Offset(w * 0.50f, h * 0.50f)
        )

        // Avocado slices (Green)
        val avocado = Path().apply {
            moveTo(w * 0.42f, h * 0.35f)
            cubicTo(w * 0.52f, h * 0.28f, w * 0.60f, h * 0.38f, w * 0.54f, h * 0.48f)
        }
        drawPath(avocado, color = Color(0xFF7FA272))

        // Roasted Vegetables (Orange)
        drawCircle(color = Color(0xFFDA7043), radius = h * 0.06f, center = Offset(w * 0.55f, h * 0.58f))
        drawCircle(color = Color(0xFFDA7043), radius = h * 0.05f, center = Offset(w * 0.45f, h * 0.60f))

        // Side Drink Glass
        drawCircle(
            color = Color(0xFFFAF0D8),
            radius = h * 0.16f,
            center = Offset(w * 0.82f, h * 0.30f)
        )

        // Side Yogurt Bowl
        drawCircle(
            color = Color.White,
            radius = h * 0.18f,
            center = Offset(w * 0.85f, h * 0.70f)
        )
    }
}

/**
 * Bottom Navigation Bar for Care & Nutrition.
 */
@Composable
private fun DashboardBottomNavBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, Color(0xFFF0E8F4), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(top = 8.dp, bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                label = "Home",
                isSelected = selectedIndex == 0,
                onClick = { onTabSelected(0) },
                icon = { HomeNavIcon(color = if (selectedIndex == 0) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Track",
                isSelected = selectedIndex == 1,
                onClick = { onTabSelected(1) },
                icon = { TrackNavIcon(color = if (selectedIndex == 1) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "AI",
                isSelected = selectedIndex == 2,
                onClick = { onTabSelected(2) },
                icon = { SparklesIcon(color = if (selectedIndex == 2) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Care",
                isSelected = selectedIndex == 3,
                onClick = { onTabSelected(3) },
                icon = { CareNavIcon(color = if (selectedIndex == 3) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
            NavTabItem(
                label = "Profile",
                isSelected = selectedIndex == 4,
                onClick = { onTabSelected(4) },
                icon = { UserNavIcon(color = if (selectedIndex == 4) MomoraPurplePrimary else MomoraBodyMuted, modifier = Modifier.size(20.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(134.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF333333))
        )
    }
}

@Composable
private fun NavTabItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFFF4ECF8) else Color.Transparent)
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MomoraPurplePrimary else MomoraBodyMuted
        )
    }
}

@Composable
private fun LeafIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val leafPath = Path().apply {
            moveTo(w * 0.20f, h * 0.80f)
            cubicTo(w * 0.20f, h * 0.30f, w * 0.70f, h * 0.20f, w * 0.80f, h * 0.20f)
            cubicTo(w * 0.80f, h * 0.70f, w * 0.70f, h * 0.80f, w * 0.20f, h * 0.80f)
            close()
        }
        drawPath(leafPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
private fun BottleIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawRect(color = color, topLeft = Offset(w * 0.30f, h * 0.30f), size = Size(w * 0.40f, h * 0.55f), style = Stroke(width = strokeWidth))
        drawRect(color = color, topLeft = Offset(w * 0.38f, h * 0.15f), size = Size(w * 0.24f, h * 0.15f), style = Stroke(width = strokeWidth))
    }
}

@Composable
private fun WaterDropIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val dropPath = Path().apply {
            moveTo(w * 0.50f, h * 0.15f)
            cubicTo(w * 0.85f, h * 0.55f, w * 0.80f, h * 0.85f, w * 0.50f, h * 0.85f)
            cubicTo(w * 0.20f, h * 0.85f, w * 0.15f, h * 0.55f, w * 0.50f, h * 0.15f)
            close()
        }
        drawPath(dropPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun BellIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val bellPath = Path().apply {
            moveTo(w * 0.25f, h * 0.65f)
            lineTo(w * 0.75f, h * 0.65f)
            cubicTo(w * 0.70f, h * 0.40f, w * 0.65f, h * 0.25f, w * 0.50f, h * 0.25f)
            cubicTo(w * 0.35f, h * 0.25f, w * 0.30f, h * 0.40f, w * 0.25f, h * 0.65f)
            close()
        }
        drawPath(bellPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        drawCircle(color = color, radius = strokeWidth * 1.2f, center = Offset(w * 0.50f, h * 0.78f))
    }
}

@Composable
private fun ShieldIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val shieldPath = Path().apply {
            moveTo(w * 0.50f, h * 0.12f)
            lineTo(w * 0.85f, h * 0.28f)
            cubicTo(w * 0.85f, h * 0.60f, w * 0.65f, h * 0.85f, w * 0.50f, h * 0.90f)
            cubicTo(w * 0.35f, h * 0.85f, w * 0.15f, h * 0.60f, w * 0.15f, h * 0.28f)
            close()
        }

        drawPath(shieldPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

        val checkPath = Path().apply {
            moveTo(w * 0.38f, h * 0.50f)
            lineTo(w * 0.48f, h * 0.60f)
            lineTo(w * 0.64f, h * 0.40f)
        }
        drawPath(checkPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun HomeNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val housePath = Path().apply {
            moveTo(w * 0.20f, h * 0.80f)
            lineTo(w * 0.20f, h * 0.45f)
            lineTo(w * 0.50f, h * 0.20f)
            lineTo(w * 0.80f, h * 0.45f)
            lineTo(w * 0.80f, h * 0.80f)
            close()
        }
        drawPath(housePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun TrackNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val pulsePath = Path().apply {
            moveTo(w * 0.10f, h * 0.50f)
            lineTo(w * 0.30f, h * 0.50f)
            lineTo(w * 0.40f, h * 0.25f)
            lineTo(w * 0.55f, h * 0.75f)
            lineTo(w * 0.70f, h * 0.40f)
            lineTo(w * 0.80f, h * 0.50f)
            lineTo(w * 0.90f, h * 0.50f)
        }

        drawPath(pulsePath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun CareNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val heartPath = Path().apply {
            moveTo(w * 0.50f, h * 0.82f)
            cubicTo(w * 0.20f, h * 0.62f, w * 0.05f, h * 0.42f, w * 0.15f, h * 0.24f)
            cubicTo(w * 0.25f, h * 0.10f, w * 0.45f, h * 0.14f, w * 0.50f, h * 0.28f)
            cubicTo(w * 0.55f, h * 0.14f, w * 0.75f, h * 0.10f, w * 0.85f, h * 0.24f)
            cubicTo(w * 0.95f, h * 0.42f, w * 0.80f, h * 0.62f, w * 0.50f, h * 0.82f)
        }

        drawPath(heartPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Composable
private fun UserNavIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        drawCircle(color = color, radius = w * 0.20f, center = Offset(w * 0.50f, h * 0.35f), style = Stroke(width = strokeWidth))

        val bodyArc = Path().apply {
            moveTo(w * 0.20f, h * 0.80f)
            cubicTo(w * 0.30f, h * 0.60f, w * 0.70f, h * 0.60f, w * 0.80f, h * 0.80f)
        }
        drawPath(bodyArc, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
    }
}

@Composable
private fun SparklesIcon(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 1.8.dp.toPx()

        val starPath = Path().apply {
            moveTo(w * 0.50f, h * 0.15f)
            cubicTo(w * 0.50f, h * 0.35f, w * 0.65f, h * 0.50f, w * 0.85f, h * 0.50f)
            cubicTo(w * 0.65f, h * 0.50f, w * 0.50f, h * 0.65f, w * 0.50f, h * 0.85f)
            cubicTo(w * 0.50f, h * 0.65f, w * 0.35f, h * 0.50f, w * 0.15f, h * 0.50f)
            cubicTo(w * 0.35f, h * 0.50f, w * 0.50f, h * 0.35f, w * 0.50f, h * 0.15f)
            close()
        }

        drawPath(starPath, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 760)
@Composable
fun NutritionPlanScreenPreview() {
    NutritionPlanScreenContent()
}
