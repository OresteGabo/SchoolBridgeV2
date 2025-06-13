package com.schoolbridge.v2.ui.settings.help

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.R
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.components.AppParagraph
import com.schoolbridge.v2.ui.common.components.SectionHeader
import com.schoolbridge.v2.ui.common.components.SpacerL
import com.schoolbridge.v2.ui.common.components.SpacerS
import com.schoolbridge.v2.ui.common.components.SpacerXS
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class FAQItem(
    val question: String,
    val answer: String,
    val tags: List<String> = emptyList()
)


data class FAQCategory(
    val title: String,
    val questions: List<FAQItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpFAQScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val offlineFaqs = remember { embeddedFaqs() }
    val searchQuery = remember { mutableStateOf("") }
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    var showDeepSearchPrompt by remember { mutableStateOf(false) }
    var isSearchingOnline by remember { mutableStateOf(false) }
    var onlineResults by remember { mutableStateOf<List<FAQItem>>(emptyList()) }

    val filteredOffline = remember(searchQuery.value, offlineFaqs) {
        filterFAQ(offlineFaqs, searchQuery.value)
    }

    val allMatchesEmpty = filteredOffline.all { it.questions.isEmpty() } && onlineResults.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t(R.string.help_and_faq)) },
                colors = TopAppBarDefaults.topAppBarColors(),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = t( R.string.back))
                    }
                }
            )
        }

    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = t(R.string.faq_intro),
                style = MaterialTheme.typography.bodyMedium
            )

            //Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = {
                    searchQuery.value = it
                    showDeepSearchPrompt = true
                    onlineResults = emptyList()
                },
                placeholder = { Text(t(R.string.search_faqs)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.value.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery.value = ""
                            showDeepSearchPrompt = false
                            onlineResults = emptyList()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (searchQuery.value.isEmpty())
                    t(R.string.common_questions)
                else
                    t(R.string.search_results_for, searchQuery.value),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (allMatchesEmpty && showDeepSearchPrompt) {
                Spacer(modifier = Modifier.height(40.dp))
                SearchOnlinePrompt(
                    query = searchQuery.value,
                    isSearching = isSearchingOnline,
                    onSearch = {
                        scope.launch {
                            isSearchingOnline = true
                            onlineResults = fetchOnlineFAQs(searchQuery.value)
                            isSearchingOnline = false
                        }
                    }
                )
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (searchQuery.value.isNotEmpty() && onlineResults.isNotEmpty()) {
                    item {
                        Text(
                            text = t(R.string.from_online),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(onlineResults.size) { faqIndex ->
                        ExpandableFAQItem(
                            faq = onlineResults[faqIndex],
                            isInitiallyExpanded = expandedStates[onlineResults[faqIndex].question] ?: false,
                            onToggle = { expandedStates[onlineResults[faqIndex].question] = it }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }

                items(filteredOffline.size) { categoryIndex ->
                    FAQCategorySection(
                        category = filteredOffline[categoryIndex],
                        expandedStates = expandedStates
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}




fun embeddedFaqs(): List<FAQCategory> = listOf() //generateFAQs()


suspend fun fetchOnlineFAQs(query: String): List<FAQItem> {
    delay(1000) // simulate network delay
    return listOf(
        FAQItem("How can I contact support?", "You can email us at help@schoolbridge.app"),
        FAQItem("Can I unlink a student?", "Currently, you need admin help to unlink a child.")
    ).filter {
        it.question.contains(query, ignoreCase = true) || it.answer.contains(query, ignoreCase = true)
    }
}

fun filterFAQ(
    allCategories: List<FAQCategory>,
    query: String
): List<FAQCategory> {
    if (query.isBlank()) return allCategories

    val lowercaseQuery = query.lowercase().trim()

    return allCategories.mapNotNull { category ->
        val matchedQuestions = category.questions.filter { item ->
            val questionMatch = item.question.contains(lowercaseQuery, ignoreCase = true)
            val tagMatch = item.tags.any { it.contains(lowercaseQuery, ignoreCase = true) }
            val answerMatch = item.answer.contains(lowercaseQuery, ignoreCase = true)

            questionMatch || tagMatch || answerMatch
        }

        if (matchedQuestions.isNotEmpty()) {
            category.copy(questions = matchedQuestions)
        } else null
    }
}


@Composable
fun ExpandableFAQItem(
    faq: FAQItem,
    isInitiallyExpanded: Boolean = false,
    onToggle: (Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(isInitiallyExpanded) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                isExpanded = !isExpanded
                onToggle(isExpanded)
            }
            .animateContentSize()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.QuestionAnswer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            SpacerL()
            Text(
                text = faq.question,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
            )
        }

        if (isExpanded) {
            SpacerXS()
            AppParagraph(
                text = faq.answer,
                modifier = Modifier.padding(start = 26.dp, end = 8.dp, bottom = 4.dp)
            )
        }
    }
}



@Composable
fun FAQItemCard(
    question: String,
    answer: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .animateContentSize()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.QuestionAnswer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            SpacerL()
            Text(
                text = question,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
            )
        }
        if (isExpanded) {
            SpacerXS()
            AppParagraph(
                text = answer,
                modifier = Modifier.padding(start = 26.dp, end = 8.dp, bottom = 4.dp)
            )
        }
    }
}
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = t(R.string.search)
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
fun FAQCategorySection(
    category: FAQCategory,
    expandedStates: MutableMap<String, Boolean>
) {
    SectionHeader(title = category.title)
    SpacerS()
    category.questions.forEachIndexed { i, faq ->
        var isExpanded by remember { mutableStateOf(expandedStates[faq.question] ?: false) }

        FAQItemCard(
            question = faq.question,
            answer = faq.answer,
            isExpanded = isExpanded,
            onToggleExpand = {
                isExpanded = !isExpanded
                expandedStates[faq.question] = isExpanded
            }
        )

        if (i < category.questions.lastIndex) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 26.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
fun SearchOnlinePrompt(
    query: String,
    isSearching: Boolean,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = t(R.string.no_results),
            color = Color.Gray
        )
        SpacerS()
        Button(onClick = onSearch) {
            if (isSearching) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                SpacerS()
            }
            Text(t(R.string.search_online))
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun HelpFAQScreenPreview() {
    MaterialTheme {
        HelpFAQScreen(
            onBack = {}
        )
    }
}
