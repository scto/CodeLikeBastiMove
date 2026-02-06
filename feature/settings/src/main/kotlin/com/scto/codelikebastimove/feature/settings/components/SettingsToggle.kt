package com.scto.codelikebastimove.feature.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SettingsToggle(
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    enabled: Boolean = true,
    showSwitch: Boolean = true,
    checked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    startWidget: (@Composable () -> Unit)? = null,
    endWidget: (@Composable () -> Unit)? = null,
) {
    val clickModifier = if (enabled) {
        Modifier.clickable {
            if (showSwitch && onCheckedChange != null) {
                onCheckedChange(!checked)
            } else {
                onClick?.invoke()
            }
        }
    } else {
        Modifier
    }

    PreferenceTemplate(
        title = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        modifier = modifier.then(clickModifier),
        enabled = enabled,
        description = {
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        startWidget = startWidget,
        endWidget = if (showSwitch) {
            {
                Switch(
                    checked = checked,
                    onCheckedChange = if (enabled) onCheckedChange else null,
                    enabled = enabled,
                )
            }
        } else {
            endWidget
        },
    )
}
