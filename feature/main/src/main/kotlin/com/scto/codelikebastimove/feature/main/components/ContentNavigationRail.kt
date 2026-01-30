package com.scto.codelikebastimove.feature.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.feature.main.MainContentType

@Composable
fun ContentNavigationRail(
  selectedContent: MainContentType,
  onContentSelected: (MainContentType) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(color = MaterialTheme.colorScheme.surfaceContainer, modifier = modifier.width(72.dp)) {
    Column(
      modifier = Modifier.fillMaxHeight().padding(vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Top,
    ) {
      MainContentType.entries.forEach { contentType ->
        val selected = contentType == selectedContent

        NavigationRailItem(
          selected = selected,
          onClick = { onContentSelected(contentType) },
          icon = {
            Icon(
              imageVector = if (selected) contentType.selectedIcon else contentType.icon,
              contentDescription = contentType.title,
            )
          },
          label = {
            Text(
              text = contentType.title,
              fontSize = 10.sp,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
              textAlign = TextAlign.Center,
            )
          },
          alwaysShowLabel = true,
          colors =
            NavigationRailItemDefaults.colors(
              selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
              selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
              indicatorColor = MaterialTheme.colorScheme.primaryContainer,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        )

        Spacer(modifier = Modifier.height(4.dp))
      }
    }
  }
}
