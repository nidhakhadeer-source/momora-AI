package com.example.SIMATS.ui.home

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.R
import com.example.SIMATS.ui.theme.MomoraAITheme
import com.example.SIMATS.ui.theme.MomoraLavenderCard
import com.example.SIMATS.ui.theme.MomoraMintLight
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraSubheaderPurple
import com.example.SIMATS.ui.theme.MomoraTealPrimary
import com.example.SIMATS.ui.theme.MomoraTextBody
import com.example.SIMATS.ui.theme.MomoraTextHeadline
import com.example.SIMATS.ui.theme.MomoraTextMuted

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute) {
        uiState.navigateToRoute?.let { route ->
            onNavigateTo(route)
            viewModel.onEvent(HomeUiEvent.OnNavigationHandled)
        }
    }

    HomeScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { onEvent(HomeUiEvent.OnTabSelected(it)) }
            )
        },
        containerColor = MomoraScreenBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Header Bar: Saved Heart | momora AI | Notification Bell
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onEvent(HomeUiEvent.OnSavedClicked) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_heart_outline),
                            contentDescription = "Saved Items",
                            tint = MomoraTextHeadline,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Text(
                        text = "momora AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraTealPrimary
                    )

                    IconButton(
                        onClick = { onEvent(HomeUiEvent.OnNotificationsClicked) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bell),
                            contentDescription = "Notifications",
                            tint = MomoraTextHeadline,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Greeting Header: Good morning, Maya ☀️
                Text(
                    text = "Good morning, ${uiState.userName} ☀️",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MomoraTextHeadline,
                    fontSize = 26.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.dateGreetingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MomoraTextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Trimester Progress Card (Lavender)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MomoraLavenderCard)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiState.trimesterText,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MomoraSubheaderPurple,
                                    letterSpacing = 1.1.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = uiState.gestationalAgeWeeksText,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraTextHeadline
                                )

                                Text(
                                    text = uiState.gestationalAgeDaysText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MomoraSubheaderPurple
                                )
                            }

                            Icon(
                                painter = painterResource(id = R.drawable.ic_baby),
                                contentDescription = null,
                                tint = MomoraSubheaderPurple,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = uiState.babySizeComparison,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MomoraTextHeadline
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { uiState.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MomoraTealPrimary,
                            trackColor = Color(0xFFE8DFED),
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = uiState.dueDetailsText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MomoraTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Daily Check-in Banner Card (Mint)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MomoraMintLight)
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "A moment for you",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraTextHeadline
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "How are you feeling today? A small check-in helps us understand your day.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MomoraTextBody,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onEvent(HomeUiEvent.OnStartCheckInClicked) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MomoraTealPrimary,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Start daily check-in",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Today at a glance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today at a glance",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraTextHeadline
                    )

                    Text(
                        text = "View trackers",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraTealPrimary,
                        modifier = Modifier.clickable { onEvent(HomeUiEvent.OnViewTrackersClicked) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Trackers Grid Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Water Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Water",
                                fontSize = 12.sp,
                                color = MomoraTextMuted
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.waterLogged,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraTextHeadline
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.waterGoal,
                                fontSize = 12.sp,
                                color = MomoraTextMuted
                            )
                        }
                    }

                    // Sleep Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Last night",
                                fontSize = 12.sp,
                                color = MomoraTextMuted
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.sleepLogged,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraTextHeadline
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Sleep logged",
                                fontSize = 12.sp,
                                color = MomoraTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Your next steps
                Text(
                    text = "Your next steps",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraTextHeadline
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Next steps card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        // Item 1: Prenatal vitamin
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEvent(HomeUiEvent.OnPrenatalVitaminClicked) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MomoraMintLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_pill),
                                        contentDescription = null,
                                        tint = MomoraTealPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "Prenatal vitamin",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MomoraTextHeadline
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "With breakfast · 8:00 AM",
                                        fontSize = 12.sp,
                                        color = MomoraTextMuted
                                    )
                                }
                            }

                            Text(
                                text = "Taken",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraTealPrimary
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF2F4F3))

                        // Item 2: 28-week antenatal visit
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEvent(HomeUiEvent.OnAntenatalVisitClicked) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MomoraMintLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_calendar),
                                        contentDescription = null,
                                        tint = MomoraTealPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "28-week antenatal visit",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MomoraTextHeadline
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Wed 28 Oct · 10:30 AM",
                                        fontSize = 12.sp,
                                        color = MomoraTextMuted
                                    )
                                }
                            }

                            Text(
                                text = "Prepare",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraTealPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Daily Plan Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .clickable { onEvent(HomeUiEvent.OnOpenDailyPlanClicked) }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MomoraMintLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_sparkles),
                                    contentDescription = null,
                                    tint = MomoraTealPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Your gentle daily plan",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MomoraTextHeadline
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Food, movement and a little rest",
                                    fontSize = 12.sp,
                                    color = MomoraTextMuted
                                )
                            }
                        }

                        Text(
                            text = "Open",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MomoraTealPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                iconRes = R.drawable.ic_nav_home,
                label = "Home",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) }
            )
            NavItem(
                iconRes = R.drawable.ic_nav_track,
                label = "Track",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) }
            )
            NavItem(
                iconRes = R.drawable.ic_sparkles,
                label = "AI",
                isSelected = selectedTab == 2,
                onClick = { onTabSelected(2) }
            )
            NavItem(
                iconRes = R.drawable.ic_nav_learn,
                label = "Learn",
                isSelected = selectedTab == 3,
                onClick = { onTabSelected(3) }
            )
            NavItem(
                iconRes = R.drawable.ic_nav_user,
                label = "You",
                isSelected = selectedTab == 4,
                onClick = { onTabSelected(4) }
            )
        }
    }
}

@Composable
private fun NavItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) MomoraMintLight else Color.Transparent)
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = if (isSelected) MomoraTealPrimary else MomoraTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MomoraTealPrimary else MomoraTextMuted
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    MomoraAITheme {
        HomeScreenContent(
            uiState = HomeUiState(),
            onEvent = {}
        )
    }
}
