package com.oseungjun.todosummer.feature.statistics.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oseungjun.todosummer.core.common.localization.stringResource
import com.oseungjun.todosummer.core.ui.theme.LocalCategoryChartColors
import com.oseungjun.todosummer.core.ui.theme.LocalStatsColors
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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // AI 리포트 섹션
        AIReportSection(
            state = state,
            onGenerateReport = { onIntent(StatisticsIntent.GenerateAIReport) },
            onAnalyzeProcrastination = { onIntent(StatisticsIntent.AnalyzeProcrastination) }
        )
    }
}

/**
 * AI 리포트 섹션 - 모던 디자인
 */
@Composable
private fun AIReportSection(
    state: StatisticsState,
    onGenerateReport: () -> Unit,
    onAnalyzeProcrastination: () -> Unit
) {
    val statsColors = LocalStatsColors.current
    
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 섹션 헤더 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // AI 아이콘 배경
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "AI 코치",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        // 남은 횟수 표시
                        val remainingCount = state.maxDailyAIUsage - state.dailyAIUsageCount
                        Text(
                            text = "(오늘 ${remainingCount}회 남음)",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (remainingCount > 0) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.error
                        )
                    }
                    val periodText = when (state.period) {
                        StatisticsPeriod.WEEK -> "최근 7일"
                        StatisticsPeriod.MONTH -> "최근 30일"
                    }
                    Text(
                        text = "$periodText 데이터를 분석합니다",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // AI 리포트 & 미루기 패턴 버튼 (가로 배치) - 각각 독립적으로 표시
        val showReportButton = state.aiReportSummary.isEmpty() && !state.isGeneratingAIReport
        val showPatternButton = state.procrastinationComment.isEmpty() && !state.isAnalyzingProcrastination
        
        if (showReportButton || showPatternButton) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // AI 리포트 버튼
                if (showReportButton) {
                    Card(
                        onClick = onGenerateReport,
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "AI 리포트",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "생성하기",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                
                // 미루기 패턴 버튼
                if (showPatternButton) {
                    Card(
                        onClick = onAnalyzeProcrastination,
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "미루기 패턴",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                            Text(
                                text = "분석하기",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
        
        // 로딩 상태 - AI 리포트
        if (state.isGeneratingAIReport) {
            AILoadingCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = "AI 리포트 생성 중",
                subtitle = "데이터를 분석하고 있어요..."
            )
        }
        
        // 로딩 상태 - 미루기 패턴
        if (state.isAnalyzingProcrastination) {
            AILoadingCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = "패턴 분석 중",
                subtitle = "미루기 습관을 찾고 있어요..."
            )
        }
        
        // AI 리포트 결과
        if (state.aiReportSummary.isNotEmpty()) {
            AIReportResultCard(
                summary = state.aiReportSummary,
                insights = state.aiReportInsights,
                actionItems = state.aiReportActionItems,
                onRefresh = onGenerateReport
            )
        }
        
        // 미루기 패턴 결과
        if (state.procrastinationComment.isNotEmpty()) {
            ProcrastinationPatternCard(
                categories = state.procrastinationCategories,
                timeSlots = state.procrastinationTimeSlots,
                comment = state.procrastinationComment,
                onRefresh = onAnalyzeProcrastination
            )
        }
        
        // 에러 메시지
        state.aiReportError?.let { error ->
            ErrorCard(message = error)
        }
        state.procrastinationError?.let { error ->
            ErrorCard(message = error)
        }
    }
}

/**
 * AI 로딩 카드
 */
@Composable
private fun AILoadingCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 로딩 인디케이터
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(56.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
                icon()
            }
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 에러 카드
 */
@Composable
private fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

/**
 * AI 리포트 결과 카드 - 모던 디자인
 */
@Composable
private fun AIReportResultCard(
    summary: String,
    insights: List<String>,
    actionItems: List<String>,
    onRefresh: () -> Unit
) {
    val statsColors = LocalStatsColors.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statsColors.cardBackground
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 헤더 + 새로고침 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "AI 리포트",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "새로고침",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // 요약 카드
            if (summary.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Summarize,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "요약",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = summary,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                        )
                    }
                }
            }
            
            // 인사이트 섹션
            if (insights.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "이번 기간 인사이트",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    
                    insights.forEachIndexed { index, insight ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                                Text(
                                    text = insight,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            
            // 액션 아이템 섹션
            if (actionItems.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "다음 기간 추천 액션",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    
                    actionItems.forEach { action ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiary)
                                )
                                Text(
                                    text = action,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 미루기 패턴 카드 - 모던 디자인
 */
@Composable
private fun ProcrastinationPatternCard(
    categories: List<String>,
    timeSlots: List<String>,
    comment: String,
    onRefresh: () -> Unit
) {
    val statsColors = LocalStatsColors.current
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statsColors.cardBackground
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 헤더 + 새로고침 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "미루기 패턴",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "새로고침",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            // 자주 미루는 카테고리
            if (categories.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "자주 미루는 카테고리",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { category ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // 자주 미루는 시간대
            if (timeSlots.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "자주 미루는 시간대",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            timeSlots.forEach { slot ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = slot,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // AI 코멘트
            if (comment.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = comment,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.3,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun PeriodSelector(
    selectedPeriod: StatisticsPeriod,
    onPeriodSelected: (StatisticsPeriod) -> Unit
) {
    val strings = stringResource()
    val statsColors = LocalStatsColors.current
    
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
    val statsColors = LocalStatsColors.current
    
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
    val categoryChartColors = LocalCategoryChartColors.current
    val baseColors = categoryChartColors.colors
    
    // 카테고리별로 고정된 색상 할당 (순서대로 0~9번 인덱스 사용)
    val sortedCategories = distribution.keys.sorted()
    val categoryColorMap = sortedCategories.mapIndexed { index, categoryName ->
        categoryName to baseColors[index % baseColors.size]
    }.toMap()
    
    val colors = distribution.keys.map { categoryName ->
        categoryColorMap[categoryName] ?: baseColors[0]
    }
    
    val statsColors = LocalStatsColors.current
    
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
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    val canvasSize = size.minDimension
                    val strokeWidth = canvasSize * 0.2f
                    val radius = (canvasSize - strokeWidth) / 2
                    val centerOffset = Offset(
                        (canvasSize - radius * 2) / 2,
                        (canvasSize - radius * 2) / 2
                    )
                    
                    // 배경 원 (연한 회색)
                    drawArc(
                        color = Color.Gray.copy(alpha = 0.1f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidth
                        ),
                        size = Size(radius * 2, radius * 2),
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
                                cap = StrokeCap.Butt
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
                            val strings = stringResource()
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
            
            // 현재 수치 표시
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
                                .width(40.dp)
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
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
