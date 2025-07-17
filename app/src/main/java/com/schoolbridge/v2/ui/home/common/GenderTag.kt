package com.schoolbridge.v2.ui.home.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.schoolbridge.v2.domain.user.Gender

@Composable
fun GenderTag(gender: Gender, modifier: Modifier = Modifier) {
    val label = when (gender) {
        Gender.MALE -> "Male"
        Gender.FEMALE -> "Female"
        Gender.OTHER -> "Other"
        Gender.PREFER_NOT_TO_SAY -> "Prefer not to say"
        else -> ""
    }

    AssistChip(
        onClick = {},
        label = { Text(text = label) },
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}
