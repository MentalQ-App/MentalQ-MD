package com.c242_ps246.mentalq.ui.main.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.c242_ps246.mentalq.R
import com.c242_ps246.mentalq.data.remote.response.ListNoteItem
import com.c242_ps246.mentalq.ui.theme.MentalQTheme
import com.c242_ps246.mentalq.ui.utils.Utils.formatDate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun DashboardScreen(onNavigateToNoteDetail: (String) -> Unit) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val listNote by viewModel.listNote.collectAsState()
    val userData by viewModel.userData.collectAsState()
    val streakInfo by viewModel.streakInfo.collectAsState()
    val predictedStatusMode by viewModel.predictedStatusMode.collectAsState()
    val (weekDay, day) = getTodayDateFormatted()

    LaunchedEffect(Unit) {
        viewModel.loadLatestNotes()
        viewModel.calculateStreak()
        viewModel.getUserData()
        viewModel.getPredictedStatusMode()
    }

    val scrollState = rememberScrollState()
    val toolbarHeight = 150.dp
    val minShrinkHeight = 0.dp
    val maxCornerRadius = 28.dp
    val minCornerRadius = 0.dp

    val scrollProgress = if (scrollState.value == 0) {
        0f
    } else {
        (scrollState.value.toFloat() / scrollState.maxValue.toFloat()).coerceIn(0f, 1f)
    }

    val currentHeight = lerp(toolbarHeight, minShrinkHeight, scrollProgress)
    val currentCornerRadius = lerp(maxCornerRadius, minCornerRadius, scrollProgress)

    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.1f),
                        radius = 80.dp.toPx(),
                        center = Offset(size.width - 40.dp.toPx(), 40.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = 60.dp.toPx(),
                        center = Offset(40.dp.toPx(), 60.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.05f),
                        radius = 40.dp.toPx(),
                        center = Offset(size.width - 80.dp.toPx(), size.height * 0.4f)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(currentHeight)
                        .padding(top = 48.dp * (1 - scrollProgress)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.mental_health_matters),
                        fontSize = 24.sp * (1 - scrollProgress),
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 16.dp * (1 - scrollProgress))
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(
                        topStart = currentCornerRadius,
                        topEnd = currentCornerRadius,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(top = 32.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.profile_card_bg),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Card(
                                    modifier = Modifier.fillMaxSize(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.Transparent
                                    )
                                ) {
                                    Text(
                                        text = (stringResource(id = R.string.hello_user) + userData?.name + "!"),
                                        modifier = Modifier.padding(start = 8.dp, top = 16.dp),
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                modifier = Modifier.wrapContentSize(),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outlineVariant
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 4.dp
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Transparent
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(100.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = stringResource(R.string.diary_streak),
                                            modifier = Modifier.padding(top = 16.dp),
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                        Text(
                                            text = streakInfo.currentStreak.toString(),
                                            modifier = Modifier.padding(top = 8.dp),
                                            style = TextStyle(
                                                fontSize = 32.sp,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    if (uiState.isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                    Canvas(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .align(Alignment.BottomCenter)
                                    ) {
                                        val path = Path()
                                        val width = size.width
                                        val height = size.height

                                        path.moveTo(0f, height * 0.5f)
                                        path.cubicTo(
                                            width * 0.7f, height * 0.7f,
                                            width * 0.3f, height * 0.2f,
                                            width, height * 0.6f
                                        )
                                        path.lineTo(width, height)
                                        path.lineTo(0f, height)
                                        path.close()

                                        drawPath(
                                            path = path,
                                            color = primaryColor.copy(alpha = 0.5f)
                                        )

                                        val path2 = Path()
                                        path2.moveTo(0f, height * 0.6f)
                                        path2.cubicTo(
                                            width * 0.1f, height * 0.1f,
                                            width * 0.7f, height * 1.2f,
                                            width, height * 0.8f
                                        )
                                        path2.lineTo(width, height)
                                        path2.lineTo(0f, height)
                                        path2.close()

                                        drawPath(
                                            path = path2,
                                            color = primaryColor.copy(alpha = 1f)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                modifier = Modifier
                                    .width(60.dp)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 4.dp
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(
                                            top = 16.dp,
                                            start = 8.dp,
                                            end = 8.dp
                                        )
                                        .fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = weekDay,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = day,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.current_state),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = predictedStatusMode
                                        ?: stringResource(id = R.string.note_not_sufficient),
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = if (predictedStatusMode != null) {
                                        when (predictedStatusMode) {
                                            "Anxiety" -> stringResource(R.string.anxiety_message)
                                            "Bipolar" -> stringResource(R.string.bipolar_message)
                                            "Depression" -> stringResource(R.string.depression_message)
                                            "Normal" -> stringResource(R.string.normal_message)
                                            "Personality Disorder" -> stringResource(R.string.personality_disorder_message)
                                            "Stress" -> stringResource(R.string.stress_message)
                                            "Suicidal" -> stringResource(R.string.suicidal_message)
                                            else -> stringResource(R.string.unknown_condition_message)
                                        }
                                    } else {
                                        stringResource(R.string.add_more_note)
                                    },
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(top = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Text(
                            text = stringResource(R.string.latest_diary),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        if (uiState.isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        Column(
                            modifier = Modifier
                                .heightIn(min = 500.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(listNote.size) { index ->
                                LatestDiaryCard(listNote[index], onNavigateToNoteDetail)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LatestDiaryCard(note: ListNoteItem, onItemClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClick(note.id) }),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = note.title ?: "",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = note.content ?: "",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 2,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = formatDate(note.createdAt.toString()),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getTodayDateFormatted(): Pair<String, String> {
    val today = LocalDate.now()
    val dayFormatter = DateTimeFormatter.ofPattern("d")
    val weekDayFormatter = DateTimeFormatter.ofPattern("E")

    val day = today.format(dayFormatter)
    val weekDay = today.format(weekDayFormatter)

    return Pair(weekDay, day)
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview
@Composable
fun DashboardScreenPreview() {
    MentalQTheme {
        DashboardScreen {}
    }
}