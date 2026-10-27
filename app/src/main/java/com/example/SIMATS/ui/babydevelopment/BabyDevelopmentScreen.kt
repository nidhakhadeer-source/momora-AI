package com.example.SIMATS.ui.babydevelopment

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.R
import com.example.SIMATS.ui.home.BottomNavBar
import com.example.SIMATS.ui.theme.MomoraAITheme
import com.example.SIMATS.ui.theme.MomoraCardBackground
import com.example.SIMATS.ui.theme.MomoraMintLight
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraTealPrimary
import com.example.SIMATS.ui.theme.MomoraTextBody
import com.example.SIMATS.ui.theme.MomoraTextHeadline
import com.example.SIMATS.ui.theme.MomoraTextMuted

@Composable
fun BabyDevelopmentScreen(
    viewModel: BabyDevelopmentViewModel = viewModel(),
    onNavigateTo: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToRoute, uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.onEvent(BabyDevelopmentUiEvent.OnNavigationHandled)
        } else {
            uiState.navigateToRoute?.let { route ->
                onNavigateTo(route)
                viewModel.onEvent(BabyDevelopmentUiEvent.OnNavigationHandled)
            }
        }
    }

    BabyDevelopmentScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun BabyDevelopmentScreenContent(
    uiState: BabyDevelopmentUiState,
    onEvent: (BabyDevelopmentUiEvent) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = { onEvent(BabyDevelopmentUiEvent.OnTabSelected(it)) }
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

                // Header Bar: Back Arrow | momora AI | Bookmark
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onEvent(BabyDevelopmentUiEvent.OnBackClicked) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = "Back",
                            tint = MomoraTextHeadline,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "momora AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MomoraTealPrimary
                    )

                    IconButton(
                        onClick = { onEvent(BabyDevelopmentUiEvent.OnToggleBookmark) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bookmark),
                            contentDescription = "Bookmark",
                            tint = if (uiState.isBookmarked) MomoraTealPrimary else MomoraTextHeadline,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Headline: A little more you, every day
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MomoraTextHeadline,
                    fontSize = 26.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.weekSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MomoraTextMuted
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Womb Illustration Hero Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MomoraCardBackground)
                        .padding(vertical = 20.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_womb_illustration),
                        contentDescription = "Baby Womb Illustration",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Measurement Stats Row (Length & Weight)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Approx. length Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Approx. length",
                                fontSize = 12.sp,
                                color = MomoraTextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = uiState.approxLength,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraTextHeadline
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.lengthUnit,
                                fontSize = 12.sp,
                                color = MomoraTealPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Approx. weight Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Approx. weight",
                                fontSize = 12.sp,
                                color = MomoraTextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = uiState.approxWeight,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MomoraTextHeadline
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.weightUnit,
                                fontSize = 12.sp,
                                color = MomoraTealPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: This week's little wonders
                Text(
                    text = "This week's little wonders",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraTextHeadline
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Wonders List Card (White)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column {
                        uiState.wondersList.forEachIndexed { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEvent(BabyDevelopmentUiEvent.OnWonderItemClicked(item)) }
                                    .padding(vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(MomoraMintLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = item.iconRes),
                                            contentDescription = null,
                                            tint = MomoraTealPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = item.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MomoraTextHeadline
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.subtitle,
                                            fontSize = 12.sp,
                                            color = MomoraTextMuted
                                        )
                                    }
                                }

                                Icon(
                                    painter = painterResource(id = R.drawable.ic_chevron_right),
                                    contentDescription = null,
                                    tint = MomoraTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            if (index < uiState.wondersList.size - 1) {
                                HorizontalDivider(color = Color(0xFFF2F4F3))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: And in your changing body
                Text(
                    text = uiState.changingBodyTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MomoraTextHeadline
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = uiState.changingBodyText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MomoraTextBody,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Footer Disclaimer Note
                Text(
                    text = uiState.disclaimerText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MomoraTextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BabyDevelopmentScreenPreview() {
    MomoraAITheme {
        BabyDevelopmentScreenContent(
            uiState = BabyDevelopmentUiState(),
            onEvent = {}
        )
    }
}
