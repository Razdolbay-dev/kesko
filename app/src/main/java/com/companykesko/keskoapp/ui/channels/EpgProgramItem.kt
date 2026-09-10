package com.companykesko.keskoapp.ui.channels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.companykesko.keskoapp.data.EpgProgram
import com.companykesko.keskoapp.ui.common.formatEpgTime

@Composable
fun EpgProgramItem(program: EpgProgram) {
    val isPast = program.isPast == true
    val isCurrent = program.isCurrent == true

    val contentAlpha = if (isPast) 0.5f else 1f
    val titleColor = when {
        isCurrent -> MaterialTheme.colorScheme.primary
        isPast -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${formatEpgTime(program.time)} – ${formatEpgTime(program.timeTo)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor.copy(alpha = contentAlpha)
                )
                Spacer(Modifier.weight(1f))
                program.duration?.let { mins ->
                    Text(
                        text = "${mins} мин",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = program.name ?: "Без названия",
                fontSize = 14.sp,
                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                color = titleColor.copy(alpha = contentAlpha),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            program.descr?.takeIf { it.isNotBlank() }?.let { descr ->
                Spacer(Modifier.height(4.dp))
                Text(
                    text = descr,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}