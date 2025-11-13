package com.example.todosummer.feature.statistics.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todosummer.core.common.localization.stringResource
import com.example.todosummer.core.ui.theme.LocalStatsColors
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun StatisticsRoute(
    modifier: Modifier = Modifier
) {
    val viewModel: StatisticsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    
    StatisticsScreen(
        state = state,
        onIntent = viewModel::onIntent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    state: StatisticsState,
    onIntent: (StatisticsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = stringResource()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 헤더
        Text(
            text = strings.statisticsActivityReport,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        // 기간 선택 탭
        PeriodSelector(
            selectedPeriod = state.period,
            onPeriodSelected = { onIntent(StatisticsIntent.ChangePeriod(it)) }
        )
        
        // 인사이트 카드
        if (state.insight.isNotEmpty()) {
            InsightCard(insight = state.insight)
        }
        
        // 총 완료 & 완료율
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = strings.statisticsTotalCompleted,
                value = state.totalCompleted.toString(),
                subtitle = "${state.totalTodos} ${strings.statisticsOf}",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = strings.statisticsCompletionRate,
                value = "${(state.completionRate * 100).roundToInt()}%",
                subtitle = if (state.completionRate >= 0.7f) strings.statisticsGreat else strings.statisticsKeepGoing,
                modifier = Modifier.weight(1f)
            )
        }
        
        // 최다 카테고리
        if (state.topCategory.isNotEmpty()) {
            StatCard(
                title = strings.statisticsTopCategory,
                value = state.topCategory,
                subtitle = "",
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // 카테고리 분포
        if (state.categoryDistribution.isNotEmpty()) {
            CategoryDistributionCard(
                distribution = state.categoryDistribution
            )
        }
        
        // 추이 차트
        if (state.trendData.isNotEmpty()) {
            TrendCard(
                period = state.period,
                trendData = state.trendData,
                trendLabels = state.trendLabels,
                currentRate = state.completionRate
            )
        }
    }
}
@Composable
private fun PeriodSelector(
    selectedPeriod: StatisticsPeriod,
    onPeriodSelected: (StatisticsPeriod) -> Unit
) {
    val strings = com.example.todosummer.core.common.localization.stringResource()
    val statsColors = com.example.todosummer.core.ui.theme.LocalStatsColors.current
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatisticsPeriod.entries.forEach { period ->
            val isSelected = selectedPeriod == period
            Card(
                onClick = { onPeriodSelected(period) },
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        statsColors.cardBackground
                    }
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 4.dp else 0.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (period) {
                            StatisticsPeriod.WEEK -> strings.statisticsPeriodWeek
                            StatisticsPeriod.MONTH -> strings.statisticsPeriodMonth
                            StatisticsPeriod.ALL -> strings.statisticsPeriodAll
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightCard(insight: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = insight,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String = "",
    modifier: Modifier = Modifier
) {
    val statsColors = com.example.todosummer.core.ui.theme.LocalStatsColors.current
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = statsColors.cardBackground
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun CategoryDistributionCard(
    distribution: Map<String, Int>
) {
    val total = distribution.values.sum()
    
    // 테마에서 카테고리 색상 가져오기 (고정 10개 색상)
    val categoryChartColors = com.example.todosummer.core.ui.theme.LocalCategoryChartColors.current
    val baseColors = categoryChartColors.colors
    
    // 카테고리별로 고정된 색상 할당 (순서대로 0~9번 인덱스 사용)
    val sortedCategories = distribution.keys.sorted()
    val categoryColorMap = sortedCategories.mapIndexed { index, categoryName ->
        categoryName to baseColors[index % baseColors.size]
    }.toMap()
    
    val colors = distribution.keys.map { categoryName ->
        categoryColorMap[categoryName] ?: baseColors[0]
    }
    
    val statsColors = com.example.todosummer.core.ui.theme.LocalStatsColors.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statsColors.cardBackground
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            val strings = stringResource()
            Text(
                text = strings.statisticsCategoryDistribution,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 모던한 도넛 차트
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                // 배경 원 (그림자 효과)
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    val canvasSize = size.minDimension
                    val strokeWidth = canvasSize * 0.2f
                    val radius = (canvasSize - strokeWidth) / 2
                    val centerOffset = androidx.compose.ui.geometry.Offset(
                        (canvasSize - radius * 2) / 2,
                        (canvasSize - radius * 2) / 2
                    )
                    
                    // 배경 원 (연한 회색)
                    drawArc(
                        color = Color.Gray.copy(alpha = 0.1f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidth
                        ),
                        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                        topLeft = centerOffset
                    )
                    
                    // 카테고리별 원호 (두껍고 선명하게)
                    var startAngle = -90f
                    distribution.entries.forEachIndexed { index, (_, count) ->
                        val sweepAngle = (count.toFloat() / total.toFloat()) * 360f
                        val color = colors.getOrElse(index) { Color.Gray }
                        
                        // 메인 원호
                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            style = Stroke(
                                width = strokeWidth,
                                cap = androidx.compose.ui.graphics.StrokeCap.Butt
                            ),
                            size = Size(radius * 2, radius * 2),
                            topLeft = centerOffset
                        )
                        
                        startAngle += sweepAngle
                    }
                }
                
                // 중앙 카드
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val strings = com.example.todosummer.core.common.localization.stringResource()
                            Text(
                                text = "$total",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = strings.statisticsTotal,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 카테고리 목록
            distribution.entries.forEachIndexed { index, (category, count) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(colors.getOrElse(index) { Color.Gray })
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = "${(count.toFloat() / total * 100).roundToInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (index < distribution.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TrendCard(
    period: StatisticsPeriod,
    trendData: List<Float>,
    trendLabels: List<String>,
    currentRate: Float
) {
    val statsColors = LocalStatsColors.current

    val title = when (period) {
        StatisticsPeriod.WEEK -> "주간 완료율 추이"
        StatisticsPeriod.MONTH -> "월간 완료율 추이"
        StatisticsPeriod.ALL -> "월별 활동 추이"
    }
    
    val previousRate = trendData.getOrNull(trendData.size - 2) ?: 0f
    val lastRate = trendData.lastOrNull() ?: currentRate
    val change = lastRate - previousRate
    val changePercent = if (previousRate > 0) (change / previousRate * 100).roundToInt() else 0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statsColors.cardBackground
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 현재 수치 표시 (전체 기간은 표시 안 함)
            if (period != StatisticsPeriod.ALL) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${(currentRate * 100).roundToInt()}%",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (changePercent != 0) {
                        val compareText = when (period) {
                            StatisticsPeriod.WEEK -> "vs last week"
                            StatisticsPeriod.MONTH -> "vs last month"
                            else -> ""
                        }
                        Text(
                            text = "${if (changePercent > 0) "+" else ""}$changePercent% $compareText",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (changePercent > 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // 바 차트
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                trendData.forEachIndexed { index, rate ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(if (period == StatisticsPeriod.ALL) 30.dp else 40.dp)
                                .height((rate * 120).dp.coerceAtLeast(4.dp))
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(
                                    if (index == trendData.size - 1) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = trendLabels.getOrNull(index) ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
