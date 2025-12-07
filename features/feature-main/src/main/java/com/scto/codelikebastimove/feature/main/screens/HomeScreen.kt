package com.scto.codelikebastimove.feature.main.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scto.codelikebastimove.feature.main.navigation.MainDestination

@Composable
fun HomeScreen(
    onNavigate: (MainDestination) -> Unit,
    onCreateProject: () -> Unit,
    onOpenProject: () -> Unit,
    onCloneRepository: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Android Code Studio",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = "Your Ideas, Anywhere",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        AppLogo()
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Loslegen",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = "Starten Sie Ihr neues großartiges Projekt!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        HomeActionButton(
            icon = Icons.Default.Add,
            title = "Projekt erstellen",
            onClick = onCreateProject
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        HomeActionButton(
            icon = Icons.Default.Folder,
            title = "Vorhandenes Projekt öffnen",
            onClick = onOpenProject
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        HomeActionButton(
            icon = Icons.Outlined.CloudDownload,
            title = "Repository klonen",
            onClick = onCloneRepository
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        HomeActionButton(
            icon = Icons.Default.Terminal,
            title = "Konsole",
            onClick = { onNavigate(MainDestination.Console) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        HomeActionButton(
            icon = Icons.Default.Settings,
            title = "Einstellungen",
            onClick = { onNavigate(MainDestination.Settings) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        HomeActionButton(
            icon = Icons.Default.Tune,
            title = "IDE Configurations",
            onClick = { onNavigate(MainDestination.Settings) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        HomeActionButton(
            icon = Icons.Default.Book,
            title = "Dokumentation",
            onClick = { onNavigate(MainDestination.Documentation) }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AppLogo(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2),
                        Color(0xFFE91E63),
                        Color(0xFFFF5722)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ACS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "</>",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun HomeActionButton(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
