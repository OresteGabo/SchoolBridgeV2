package com.schoolbridge.v2.ui.home.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.domain.user.CurrentUser.*

@Composable
fun LinkedStudentRow(
    list: List<LinkedStudent>,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier.padding(vertical = 8.dp)) {
        items(list) { student ->
            Card(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(160.dp)
                    .height(100.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${student.firstName} ${student.lastName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "ID: ${student.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
