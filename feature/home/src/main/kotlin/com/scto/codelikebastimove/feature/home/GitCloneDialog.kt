package com.scto.codelikebastimove.feature.home

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.scto.codelikebastimove.core.datastore.ClonedRepository
import com.scto.codelikebastimove.core.datastore.GitCredentialsStore
import com.scto.codelikebastimove.core.datastore.UserPreferencesRepository
import com.scto.codelikebastimove.core.templates.api.GradleLanguage
import com.scto.codelikebastimove.core.templates.api.Project
import com.scto.codelikebastimove.core.templates.api.ProjectConfig
import com.scto.codelikebastimove.core.templates.api.ProjectLanguage
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class CloneDialogStep {
  GIT_CONFIG,
  CREDENTIALS,
  CLONE_OPTIONS,
}

@Composable
fun GitCloneDialog(context: Context, onDismiss: () -> Unit, onCloneSuccess: (Project) -> Unit) {
  val scope = rememberCoroutineScope()
  val userPrefsRepo = remember { UserPreferencesRepository(context) }
  val credentialsStore = remember { GitCredentialsStore(context) }

  var currentStep by remember { mutableStateOf(CloneDialogStep.GIT_CONFIG) }
  var isLoading by remember { mutableStateOf(true) }

  var gitUserName by remember { mutableStateOf("") }
  var gitUserEmail by remember { mutableStateOf("") }
  var gitConfigured by remember { mutableStateOf(false) }

  var credentialsUsername by remember { mutableStateOf("") }
  var credentialsPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var saveCredentials by remember { mutableStateOf(true) }
  var credentialsConfigured by remember { mutableStateOf(false) }

  var repositoryUrl by remember { mutableStateOf("") }
  var branchName by remember { mutableStateOf("main") }
  var cloneSubmodules by remember { mutableStateOf(false) }
  var isCloning by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var cloneProgress by remember { mutableStateOf("") }

  LaunchedEffect(Unit) {
    val gitConfig = userPrefsRepo.getGitConfigOnce()
    gitUserName = gitConfig.userName
    gitUserEmail = gitConfig.userEmail
    gitConfigured = gitConfig.isConfigured()

    val credentials = credentialsStore.getCredentials()
    credentialsUsername = credentials.username
    credentialsPassword = credentials.password
    credentialsConfigured = credentials.isConfigured()

    currentStep =
      when {
        !gitConfigured -> CloneDialogStep.GIT_CONFIG
        !credentialsConfigured -> CloneDialogStep.CREDENTIALS
        else -> CloneDialogStep.CLONE_OPTIONS
      }

    isLoading = false
  }

  AlertDialog(
    onDismissRequest = { if (!isCloning) onDismiss() },
    title = {
      Text(
        text =
          when (currentStep) {
            CloneDialogStep.GIT_CONFIG -> "Git Konfiguration"
            CloneDialogStep.CREDENTIALS -> "Git Zugangsdaten"
            CloneDialogStep.CLONE_OPTIONS -> "Repository klonen"
          },
        style = MaterialTheme.typography.headlineSmall,
      )
    },
    text = {
      if (isLoading) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          CircularProgressIndicator()
          Spacer(modifier = Modifier.height(16.dp))
          Text("Lade Einstellungen...")
        }
      } else {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
          when (currentStep) {
            CloneDialogStep.GIT_CONFIG -> {
              GitConfigContent(
                userName = gitUserName,
                onUserNameChange = { gitUserName = it },
                userEmail = gitUserEmail,
                onUserEmailChange = { gitUserEmail = it },
              )
            }
            CloneDialogStep.CREDENTIALS -> {
              CredentialsContent(
                username = credentialsUsername,
                onUsernameChange = { credentialsUsername = it },
                password = credentialsPassword,
                onPasswordChange = { credentialsPassword = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = it },
                saveCredentials = saveCredentials,
                onSaveCredentialsChange = { saveCredentials = it },
              )
            }
            CloneDialogStep.CLONE_OPTIONS -> {
              CloneOptionsContent(
                repositoryUrl = repositoryUrl,
                onRepositoryUrlChange = { repositoryUrl = it },
                branchName = branchName,
                onBranchNameChange = { branchName = it },
                cloneSubmodules = cloneSubmodules,
                onCloneSubmodulesChange = { cloneSubmodules = it },
                isCloning = isCloning,
                cloneProgress = cloneProgress,
              )
            }
          }

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = errorMessage!!,
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodySmall,
            )
          }
        }
      }
    },
    confirmButton = {
      when (currentStep) {
        CloneDialogStep.GIT_CONFIG -> {
          Button(
            onClick = {
              if (gitUserName.isBlank()) {
                errorMessage = "Bitte gib deinen Namen ein"
                return@Button
              }
              if (gitUserEmail.isBlank()) {
                errorMessage = "Bitte gib deine E-Mail ein"
                return@Button
              }
              errorMessage = null
              scope.launch {
                userPrefsRepo.setGitConfig(gitUserName, gitUserEmail)
                gitConfigured = true
                currentStep =
                  if (credentialsConfigured) {
                    CloneDialogStep.CLONE_OPTIONS
                  } else {
                    CloneDialogStep.CREDENTIALS
                  }
              }
            },
            enabled = !isLoading,
          ) {
            Text("Weiter")
          }
        }
        CloneDialogStep.CREDENTIALS -> {
          Button(
            onClick = {
              if (credentialsUsername.isBlank()) {
                errorMessage = "Bitte gib deinen Benutzernamen ein"
                return@Button
              }
              if (credentialsPassword.isBlank()) {
                errorMessage = "Bitte gib dein Passwort/Token ein"
                return@Button
              }
              errorMessage = null
              if (saveCredentials) {
                credentialsStore.saveCredentials(credentialsUsername, credentialsPassword)
              }
              credentialsConfigured = true
              currentStep = CloneDialogStep.CLONE_OPTIONS
            },
            enabled = !isLoading,
          ) {
            Text("Weiter")
          }
        }
        CloneDialogStep.CLONE_OPTIONS -> {
          Button(
            onClick = {
              if (repositoryUrl.isBlank()) {
                errorMessage = "Bitte gib die Repository-URL ein"
                return@Button
              }
              if (branchName.isBlank()) {
                errorMessage = "Bitte gib den Branch-Namen ein"
                return@Button
              }
              errorMessage = null
              isCloning = true
              cloneProgress = "Starte Klonen..."

              scope.launch {
                try {
                  val result =
                    cloneRepository(
                      context = context,
                      url = repositoryUrl,
                      branch = branchName,
                      cloneSubmodules = cloneSubmodules,
                      username = credentialsUsername,
                      password = credentialsPassword,
                      onProgress = { cloneProgress = it },
                    )

                  result.fold(
                    onSuccess = { project ->
                      userPrefsRepo.addClonedRepository(
                        ClonedRepository(
                          path = project.path,
                          url = repositoryUrl,
                          branch = branchName,
                          clonedAt = System.currentTimeMillis(),
                        )
                      )
                      addSafeDirectory(project.path)
                      onCloneSuccess(project)
                    },
                    onFailure = { error ->
                      errorMessage = "Fehler beim Klonen: ${error.message}"
                      isCloning = false
                    },
                  )
                } catch (e: Exception) {
                  errorMessage = "Fehler: ${e.message}"
                  isCloning = false
                }
              }
            },
            enabled = !isCloning,
          ) {
            Text(if (isCloning) "Klone..." else "Klonen")
          }
        }
      }
    },
    dismissButton = {
      if (currentStep != CloneDialogStep.GIT_CONFIG || gitConfigured) {
        TextButton(
          onClick = {
            when (currentStep) {
              CloneDialogStep.CREDENTIALS -> {
                currentStep = CloneDialogStep.GIT_CONFIG
              }
              CloneDialogStep.CLONE_OPTIONS -> {
                currentStep = CloneDialogStep.CREDENTIALS
              }
              else -> onDismiss()
            }
          },
          enabled = !isCloning,
        ) {
          Text(if (currentStep == CloneDialogStep.GIT_CONFIG) "Abbrechen" else "Zurueck")
        }
      } else {
        TextButton(onClick = onDismiss, enabled = !isCloning) { Text("Abbrechen") }
      }
    },
  )
}

@Composable
private fun GitConfigContent(
  userName: String,
  onUserNameChange: (String) -> Unit,
  userEmail: String,
  onUserEmailChange: (String) -> Unit,
) {
  Text(
    text = "Diese Angaben werden fuer Git Commits verwendet.",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
  )

  Spacer(modifier = Modifier.height(16.dp))

  OutlinedTextField(
    value = userName,
    onValueChange = onUserNameChange,
    label = { Text("Name") },
    placeholder = { Text("Max Mustermann") },
    modifier = Modifier.fillMaxWidth(),
    singleLine = true,
  )

  Spacer(modifier = Modifier.height(12.dp))

  OutlinedTextField(
    value = userEmail,
    onValueChange = onUserEmailChange,
    label = { Text("E-Mail") },
    placeholder = { Text("max@example.com") },
    modifier = Modifier.fillMaxWidth(),
    singleLine = true,
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
  )
}

@Composable
private fun CredentialsContent(
  username: String,
  onUsernameChange: (String) -> Unit,
  password: String,
  onPasswordChange: (String) -> Unit,
  passwordVisible: Boolean,
  onPasswordVisibilityChange: (Boolean) -> Unit,
  saveCredentials: Boolean,
  onSaveCredentialsChange: (Boolean) -> Unit,
) {
  Text(
    text = "Git Zugangsdaten fuer private Repositories.",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
  )

  Spacer(modifier = Modifier.height(8.dp))

  Text(
    text = "Fuer GitHub: Nutze einen Personal Access Token als Passwort.",
    style = MaterialTheme.typography.bodySmall,
    color = MaterialTheme.colorScheme.primary,
  )

  Spacer(modifier = Modifier.height(16.dp))

  OutlinedTextField(
    value = username,
    onValueChange = onUsernameChange,
    label = { Text("Benutzername") },
    modifier = Modifier.fillMaxWidth(),
    singleLine = true,
  )

  Spacer(modifier = Modifier.height(12.dp))

  OutlinedTextField(
    value = password,
    onValueChange = onPasswordChange,
    label = { Text("Passwort / Token") },
    modifier = Modifier.fillMaxWidth(),
    singleLine = true,
    visualTransformation =
      if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    trailingIcon = {
      IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
        Icon(
          imageVector =
            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
          contentDescription = if (passwordVisible) "Passwort verbergen" else "Passwort anzeigen",
        )
      }
    },
  )

  Spacer(modifier = Modifier.height(12.dp))

  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    Checkbox(checked = saveCredentials, onCheckedChange = onSaveCredentialsChange)
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = "Zugangsdaten sicher speichern", style = MaterialTheme.typography.bodyMedium)
  }
}

@Composable
private fun CloneOptionsContent(
  repositoryUrl: String,
  onRepositoryUrlChange: (String) -> Unit,
  branchName: String,
  onBranchNameChange: (String) -> Unit,
  cloneSubmodules: Boolean,
  onCloneSubmodulesChange: (Boolean) -> Unit,
  isCloning: Boolean,
  cloneProgress: String,
) {
  OutlinedTextField(
    value = repositoryUrl,
    onValueChange = onRepositoryUrlChange,
    label = { Text("Repository URL") },
    placeholder = { Text("https://github.com/user/repo.git") },
    modifier = Modifier.fillMaxWidth(),
    singleLine = true,
    enabled = !isCloning,
  )

  Spacer(modifier = Modifier.height(12.dp))

  OutlinedTextField(
    value = branchName,
    onValueChange = onBranchNameChange,
    label = { Text("Branch") },
    placeholder = { Text("main") },
    modifier = Modifier.fillMaxWidth(),
    singleLine = true,
    enabled = !isCloning,
  )

  Spacer(modifier = Modifier.height(16.dp))

  Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    Text(
      text = "Submodule klonen",
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.weight(1f),
    )
    Switch(
      checked = cloneSubmodules,
      onCheckedChange = onCloneSubmodulesChange,
      enabled = !isCloning,
    )
  }

  if (isCloning) {
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(16.dp))

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      CircularProgressIndicator(modifier = Modifier.height(24.dp).width(24.dp))
      Spacer(modifier = Modifier.width(16.dp))
      Text(
        text = cloneProgress,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

private suspend fun cloneRepository(
  context: Context,
  url: String,
  branch: String,
  cloneSubmodules: Boolean,
  username: String,
  password: String,
  onProgress: (String) -> Unit,
): Result<Project> =
  withContext(Dispatchers.IO) {
    try {
      val repoName = extractRepoName(url)
      val projectsDir = File(context.filesDir, "projects")
      if (!projectsDir.exists()) {
        projectsDir.mkdirs()
      }

      val targetDir = File(projectsDir, repoName)
      if (targetDir.exists()) {
        targetDir.deleteRecursively()
      }

      onProgress("Bereite Klonen vor...")

      val cloneUrl =
        if (username.isNotBlank() && password.isNotBlank() && url.startsWith("https://")) {
          val encodedUsername = java.net.URLEncoder.encode(username, "UTF-8")
          val encodedPassword = java.net.URLEncoder.encode(password, "UTF-8")
          url.replace("https://", "https://$encodedUsername:$encodedPassword@")
        } else {
          url
        }

      val cloneCommand = buildList {
        add("git")
        add("clone")
        add("--branch")
        add(branch)
        add("--single-branch")
        add("--progress")
        if (cloneSubmodules) {
          add("--recurse-submodules")
        }
        add(cloneUrl)
        add(targetDir.absolutePath)
      }

      onProgress("Klone Repository...")

      val processBuilder =
        ProcessBuilder(cloneCommand).redirectErrorStream(true).directory(projectsDir)

      processBuilder.environment()["GIT_TERMINAL_PROMPT"] = "0"

      val process = processBuilder.start()
      val output = StringBuilder()

      process.inputStream.bufferedReader().use { reader ->
        var line: String?
        while (reader.readLine().also { line = it } != null) {
          val sanitizedLine = (line ?: "").replace(username, "***").replace(password, "***")
          output.appendLine(sanitizedLine)

          if (
            sanitizedLine.contains("Receiving objects:") ||
              sanitizedLine.contains("Resolving deltas:") ||
              sanitizedLine.contains("Cloning into")
          ) {
            val displayLine = sanitizedLine.take(50) + if (sanitizedLine.length > 50) "..." else ""
            onProgress(displayLine)
          }
        }
      }

      val exitCode = process.waitFor()

      if (exitCode != 0) {
        return@withContext Result.failure(Exception("Git clone fehlgeschlagen (Code: $exitCode)"))
      }

      if (cloneSubmodules) {
        onProgress("Initialisiere Submodule...")
        val submoduleProcess =
          ProcessBuilder("git", "submodule", "update", "--init", "--recursive")
            .redirectErrorStream(true)
            .directory(targetDir)
            .start()
        submoduleProcess.waitFor()
      }

      onProgress("Abgeschlossen!")

      val project =
        Project(
          name = repoName,
          path = targetDir.absolutePath,
          config =
            ProjectConfig(
              projectName = repoName,
              packageName = "com.cloned.$repoName",
              minSdk = 29,
              targetSdk = 34,
              compileSdk = 34,
              language = ProjectLanguage.KOTLIN,
              gradleLanguage = GradleLanguage.KOTLIN_DSL,
            ),
          files = emptyList(),
        )

      Result.success(project)
    } catch (e: Exception) {
      Result.failure(e)
    }
  }

private fun extractRepoName(url: String): String {
  return url.trimEnd('/').substringAfterLast('/').removeSuffix(".git").ifBlank { "repository" }
}

private suspend fun addSafeDirectory(path: String) =
  withContext(Dispatchers.IO) {
    try {
      val process =
        ProcessBuilder("git", "config", "--global", "--add", "safe.directory", path)
          .redirectErrorStream(true)
          .start()
      process.waitFor()
    } catch (e: Exception) {}
  }
