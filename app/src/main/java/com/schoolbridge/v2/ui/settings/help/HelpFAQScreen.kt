package com.schoolbridge.v2.ui.settings.help

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.R
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.AdaptivePageFrame
import com.schoolbridge.v2.ui.common.SchoolBridgePatternBackground
import com.schoolbridge.v2.ui.common.isExpandedLayout

data class FAQItem(
    val question: String,
    val answer: String,
    val tags: List<String> = emptyList()
)

data class FAQCategory(
    val title: String,
    val questions: List<FAQItem>
)

private data class FAQCategoryMeta(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpFAQScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val categories = remember { embeddedFaqs() }
    val isExpanded = isExpandedLayout()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    val filteredCategories = remember(categories, searchQuery, selectedCategory) {
        filterFAQ(
            allCategories = categories,
            query = searchQuery,
            selectedCategory = selectedCategory
        )
    }

    LaunchedEffect(filteredCategories) {
        if (filteredCategories.size == 1 && filteredCategories.first().questions.size == 1) {
            expandedStates[filteredCategories.first().questions.first().question] = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t(R.string.help_and_faq)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = t(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SchoolBridgePatternBackground(dotAlpha = 0.018f, gradientAlpha = 0.045f)

            AdaptivePageFrame(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                maxContentWidth = 1180.dp
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        HelpHeroCard(
                            totalQuestions = categories.sumOf { it.questions.size },
                            totalCategories = categories.size,
                            isExpanded = isExpanded
                        )
                    }

                    item {
                        HelpSearchCard(
                            searchQuery = searchQuery,
                            onQueryChange = { searchQuery = it }
                        )
                    }

                    item {
                        CategoryFilterRow(
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it }
                        )
                    }

                    if (filteredCategories.isEmpty()) {
                        item {
                            HelpEmptyState(searchQuery = searchQuery)
                        }
                    } else {
                        filteredCategories.forEach { category ->
                            item(category.title) {
                                FAQCategorySection(
                                    category = category,
                                    expandedStates = expandedStates
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun embeddedFaqs(): List<FAQCategory> = helpFaqContent()

fun filterFAQ(
    allCategories: List<FAQCategory>,
    query: String,
    selectedCategory: String? = null
): List<FAQCategory> {
    val normalizedQuery = query.trim()
    return allCategories
        .asSequence()
        .filter { selectedCategory == null || it.title == selectedCategory }
        .mapNotNull { category ->
            val matchedQuestions = if (normalizedQuery.isBlank()) {
                category.questions
            } else {
                category.questions.filter { item ->
                    item.question.contains(normalizedQuery, ignoreCase = true) ||
                        item.answer.contains(normalizedQuery, ignoreCase = true) ||
                        item.tags.any { it.contains(normalizedQuery, ignoreCase = true) }
                }
            }
            if (matchedQuestions.isNotEmpty()) category.copy(questions = matchedQuestions) else null
        }
        .toList()
}

@Composable
private fun HelpHeroCard(
    totalQuestions: Int,
    totalCategories: Int,
    isExpanded: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ManageAccounts,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Help Center",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Find answers about role requests, school communication, payments, schedules, and profile access across the latest SchoolBridge flow.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isExpanded) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HelpStatPill(label = "Questions", value = totalQuestions.toString())
                    HelpStatPill(label = "Topics", value = totalCategories.toString())
                    HelpStatPill(label = "Updated", value = "Latest app")
                }
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HelpStatPill(label = "Questions", value = totalQuestions.toString())
                    HelpStatPill(label = "Topics", value = totalCategories.toString())
                }
            }
        }
    }
}

@Composable
private fun HelpStatPill(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun HelpSearchCard(
    searchQuery: String,
    onQueryChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Search help topics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Try words like role request, payment receipt, meeting, call invite, timetable, parent link, or profile verification.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                placeholder = {
                    Text(t(R.string.search_faqs))
                }
            )
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<FAQCategory>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    val metas = remember(categories) {
        categories.map { category ->
            FAQCategoryMeta(
                title = category.title,
                icon = categoryIcon(category.title)
            )
        }
    }

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("All topics") }
        )

        metas.forEach { meta ->
            FilterChip(
                selected = selectedCategory == meta.title,
                onClick = { onCategorySelected(if (selectedCategory == meta.title) null else meta.title) },
                label = { Text(meta.title) },
                leadingIcon = {
                    Icon(
                        imageVector = meta.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun FAQCategorySection(
    category: FAQCategory,
    expandedStates: MutableMap<String, Boolean>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = categoryIcon(category.title),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${category.questions.size} answer${if (category.questions.size == 1) "" else "s"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            category.questions.forEach { faq ->
                val isExpanded = expandedStates[faq.question] ?: false
                FAQQuestionCard(
                    question = faq.question,
                    answer = faq.answer,
                    isExpanded = isExpanded,
                    onToggleExpand = { expandedStates[faq.question] = !isExpanded }
                )
            }
        }
    }
}

@Composable
private fun FAQQuestionCard(
    question: String,
    answer: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand)
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(if (isExpanded) 180f else 0f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun HelpEmptyState(searchQuery: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }
            Text(
                text = "No answer found yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (searchQuery.isBlank()) {
                    "Try another topic or open a more specific section."
                } else {
                    "Try different words such as parent link, role request, finance, timetable, or thread."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun categoryIcon(title: String): ImageVector = when (title) {
    "Roles & access" -> Icons.Default.AdminPanelSettings
    "Messages, requests & calls" -> Icons.Default.ChatBubbleOutline
    "Finance & receipts" -> Icons.Default.CreditCard
    "Schedule & meetings" -> Icons.Default.CalendarMonth
    else -> Icons.Default.Security
}

@Preview(showBackground = true)
@Composable
private fun HelpFAQScreenPreview() {
    MaterialTheme {
        HelpFAQScreen(onBack = {})
    }
}
