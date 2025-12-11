package com.scto.codelikebastimove.feature.main.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import com.scto.codelikebastimove.core.templates.api.TreeNode
import com.scto.codelikebastimove.feature.explorer.FileItem
import com.scto.codelikebastimove.feature.main.MainViewModel
import com.scto.codelikebastimove.feature.main.ProjectViewMode
import com.scto.codelikebastimove.feature.treeview.TreeView
import java.io.File

@Composable
fun ProjectContent(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // WICHTIG: Wir holen den Pfad jetzt direkt aus dem UI State, der aus dem Datastore wiederhergestellt wurde.
    // Das behebt das Problem, dass der Pfad verloren geht oder im ProjectManager noch nicht gesetzt ist.
    val projectPath = uiState.projectPath
    val projectRoot = remember(projectPath) {
        if (projectPath.isNotBlank()) File(projectPath) else null
    }

    // ViewType wird jetzt auch im ViewModel/State verwaltet
    val selectedViewType = uiState.projectViewType
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Berechne den Baum basierend auf der Auswahl neu, wenn sich das Root oder der Typ ändert
    val treeNodes = remember(projectRoot, selectedViewType) {
        if (projectRoot == null || !projectRoot.exists()) {
            emptyList()
        } else {
            when (selectedViewType) {
                ProjectViewMode.ANDROID -> createAndroidViewTree(projectRoot)
                ProjectViewMode.PROJECT -> createProjectViewTree(projectRoot)
                ProjectViewMode.PACKAGES -> createPackagesViewTree(projectRoot)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Kopfzeile mit Ansichtsauswahl (Android, Projekt, Pakete)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { isDropdownExpanded = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedViewType.displayName,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Ansicht wechseln")

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                ProjectViewMode.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.displayName) },
                        onClick = {
                            viewModel.updateProjectViewMode(type)
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        HorizontalDivider()

        if (treeNodes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Kein Projekt geöffnet oder Ordner leer", 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = Color.Gray
                    )
                    if (projectPath.isNotBlank()) {
                        Text(
                            text = "Pfad: $projectPath",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        } else {
            // Zeige den generierten Baum an
            TreeView(
                nodes = treeNodes,
                onNodeClick = { node ->
                    // Logik zum Öffnen der Datei
                    if (!node.value.isDirectory) {
                        viewModel.openFile(node.value.path)
                    }
                }
            )
        }
    }
}

// --------------------------------------------------------------------------------
// Dynamische Baum-Erstellungs-Funktionen
// --------------------------------------------------------------------------------

/**
 * Erstellt eine Ansicht ähnlich wie "Android" in Android Studio.
 * Gruppiert Dateien in Manifests, Java (Code), Res (Ressourcen) und Gradle Scripts.
 */
private fun createAndroidViewTree(root: File): List<TreeNode<FileItem>> {
    val nodes = mutableListOf<TreeNode<FileItem>>()
    val appDir = File(root, "app")

    // Wir erstellen virtuelle Knoten, die nicht unbedingt physisch so existieren,
    // aber auf physische Ordner verweisen, um den Inhalt korrekt zu laden.

    // 1. App Modul (als Root-ähnlicher Container)
    val appChildren = mutableListOf<TreeNode<FileItem>>()

    // a) Manifests
    val manifestFile = File(appDir, "src/main/AndroidManifest.xml")
    if (manifestFile.exists()) {
        val manifestNode = TreeNode(
            value = FileItem(file = manifestFile, name = "manifests", isDirectory = true), // Virtueller Ordner Name
            children = mutableListOf(
                TreeNode(FileItem(file = manifestFile, name = "AndroidManifest.xml", isDirectory = false))
            ),
            isExpanded = true
        )
        appChildren.add(manifestNode)
    }

    // b) Java / Kotlin (Quellcode)
    // Wir schauen in src/main/java und src/main/kotlin
    val javaSrc = File(appDir, "src/main/java")
    val kotlinSrc = File(appDir, "src/main/kotlin")
    val codeChildren = mutableListOf<TreeNode<FileItem>>()

    if (javaSrc.exists()) {
        codeChildren.addAll(createPackagesViewTree(javaSrc)) // Wiederverwendung der Paket-Logik für Quellcode
    }
    if (kotlinSrc.exists()) {
        codeChildren.addAll(createPackagesViewTree(kotlinSrc))
    }

    if (codeChildren.isNotEmpty()) {
        appChildren.add(
            TreeNode(
                value = FileItem(file = appDir, name = "java", isDirectory = true), // Virtueller Name "java" wie in AS
                children = codeChildren,
                isExpanded = true
            )
        )
    }

    // c) Res (Ressourcen)
    val resDir = File(appDir, "src/main/res")
    if (resDir.exists()) {
        // Hier nutzen wir einfach den Projekt-View für den res Ordner, da die Struktur dort meist flach ist
        val resNodes = createProjectViewTree(resDir)
        if (resNodes.isNotEmpty()) {
             appChildren.add(
                TreeNode(
                    value = FileItem(file = resDir, name = "res", isDirectory = true),
                    children = resNodes.toMutableList(),
                    isExpanded = false
                )
            )
        }
    }

    // Füge den "app" Knoten hinzu
    if (appChildren.isNotEmpty()) {
        nodes.add(
            TreeNode(
                value = FileItem(file = appDir, name = "app", isDirectory = true),
                children = appChildren,
                isExpanded = true
            )
        )
    }

    // 2. Gradle Scripts
    // Sammelt alle .gradle und .gradle.kts Dateien sowie properties
    val gradleFiles = root.listFiles { file ->
        file.name.endsWith(".gradle") ||
        file.name.endsWith(".gradle.kts") ||
        file.name == "gradle.properties" ||
        file.name == "libs.versions.toml"
    }?.sortedBy { it.name }

    if (gradleFiles != null && gradleFiles.isNotEmpty()) {
        val scriptNodes = gradleFiles.map { file ->
            TreeNode(FileItem(file = file, name = file.name, isDirectory = false))
        }.toMutableList()

        // Füge auch app/build.gradle.kts hinzu falls vorhanden
        val appBuildGradle = File(appDir, "build.gradle.kts")
        if (appBuildGradle.exists()) {
            scriptNodes.add(0, TreeNode(FileItem(file = appBuildGradle, name = "build.gradle.kts (Module: :app)", isDirectory = false)))
        }

        nodes.add(
            TreeNode(
                value = FileItem(file = root, name = "Gradle Scripts", isDirectory = true), // Virtueller Ordner
                children = scriptNodes,
                isExpanded = false
            )
        )
    }

    return nodes
}

/**
 * Erstellt eine 1:1 Abbildung des Dateisystems.
 * Rekursiver Ansatz.
 */
private fun createProjectViewTree(root: File): List<TreeNode<FileItem>> {
    if (!root.exists() || !root.isDirectory) return emptyList()

    val files = root.listFiles() ?: return emptyList()
    
    // Sortieren: Ordner zuerst, dann Dateien, jeweils alphabetisch
    val sortedFiles = files.sortedWith(compareBy({ !it.isDirectory }, { it.name }))

    return sortedFiles.map { file ->
        val fileItem = FileItem(
            file = file,
            name = file.name,
            isDirectory = file.isDirectory
        )
        
        val children = if (file.isDirectory) {
            createProjectViewTree(file).toMutableList()
        } else {
            mutableListOf()
        }

        TreeNode(
            value = fileItem,
            children = children,
            isExpanded = false // Standardmäßig eingeklappt
        )
    }
}

/**
 * Erstellt eine Paket-Ansicht.
 * Leere Verzeichnisse werden flach zusammengefasst (z.B. com -> example -> app wird zu com.example.app).
 */
private fun createPackagesViewTree(root: File): List<TreeNode<FileItem>> {
    if (!root.exists() || !root.isDirectory) return emptyList()

    val files = root.listFiles() ?: return emptyList()
    val sortedFiles = files.sortedWith(compareBy({ !it.isDirectory }, { it.name }))
    val nodes = mutableListOf<TreeNode<FileItem>>()

    for (file in sortedFiles) {
        if (file.isDirectory) {
            // Prüfen, ob Flattening möglich ist
            // Flattening Bedingungen:
            // 1. Hat genau ein Kind
            // 2. Dieses Kind ist ein Verzeichnis
            val flattenedNode = tryFlattenPackage(file)
            nodes.add(flattenedNode)
        } else {
            // Normale Datei
            nodes.add(
                TreeNode(
                    value = FileItem(file = file, name = file.name, isDirectory = false),
                    children = mutableListOf(),
                    isExpanded = false
                )
            )
        }
    }
    return nodes
}

/**
 * Hilfsfunktion, um Verzeichnisse rekursiv zusammenzufassen (z.B. com/scto/app -> com.scto.app)
 */
private fun tryFlattenPackage(dir: File): TreeNode<FileItem> {
    val children = dir.listFiles()
    
    // Wenn genau 1 Kind existiert und es ein Ordner ist -> Flatten
    if (children != null && children.size == 1 && children[0].isDirectory) {
        val childDir = children[0]
        val childNode = tryFlattenPackage(childDir)
        
        // Wir erstellen ein neues FileItem, das den kombinierten Namen trägt, aber auf das tiefste Verzeichnis zeigt
        // oder auf das aktuelle (je nach gewünschtem Verhalten beim Klicken).
        // Hier: Name kombinieren "dir.child"
        val newName = "${dir.name}.${childNode.value.name}"
        
        return TreeNode(
            value = FileItem(file = childNode.value.file, name = newName, isDirectory = true),
            children = childNode.children,
            isExpanded = false
        )
    } else {
        // Kein Flattening möglich, normal rekursiv absteigen
        val normalChildren = createPackagesViewTree(dir).toMutableList()
        return TreeNode(
            value = FileItem(file = dir, name = dir.name, isDirectory = true),
            children = normalChildren,
            isExpanded = false
        )
    }
}
