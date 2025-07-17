package com.schoolbridge.v2.ui.home.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
fun AddressCard(address: Address, modifier: Modifier = Modifier) {
    if (
        address.district.isNullOrBlank() &&
        address.sector.isNullOrBlank() &&
        address.cell.isNullOrBlank() &&
        address.village.isNullOrBlank()
    ) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Address",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            address.district?.let {
                Text(text = "District: $it", style = MaterialTheme.typography.bodyMedium)
            }
            address.sector?.let {
                Text(text = "Sector: $it", style = MaterialTheme.typography.bodyMedium)
            }
            address.cell?.let {
                Text(text = "Cell: $it", style = MaterialTheme.typography.bodyMedium)
            }
            address.village?.let {
                Text(text = "Village: $it", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
