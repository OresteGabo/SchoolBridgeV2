package com.schoolbridge.v2.ui.home.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.R
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.components.AppSubHeader
import com.schoolbridge.v2.ui.common.components.SpacerS

/**
 * Displays horizontally scrollable student cards.
 *
 * @param students List of linked students, nullable.
 * @param modifier Modifier applied to the section.
 */
@Composable
fun StudentListSection(
    students: List<CurrentUser.LinkedStudent>?,
    modifier: Modifier = Modifier
) {
    if (!students.isNullOrEmpty()) {
        Row(modifier = modifier.fillMaxWidth()) {
            AppSubHeader("🧑‍🤝‍🧑 " + t(R.string.your_children))
        }
        SpacerS()
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(students) { _, student ->
                StudentCard(student = student)
            }
        }
    }
}