package com.scto.codelikebastimove.feature.onboarding

import android.os.Build
import android.os.Environment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel(),
    onOnboardingComplete: () -> Unit
) {
    val onboardingConfig by viewModel.onboardingConfig.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var hasRealFilePermission by remember { mutableStateOf(checkFileAccessPermission()) }
    
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            hasRealFilePermission = checkFileAccessPermission()
            viewModel.setFileAccessPermissionGranted(hasRealFilePermission)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentPage) {
            0 -> WelcomePage(
                onNextClick = { viewModel.nextPage() }
            )
            1 -> PermissionsPage(
                onboardingConfig = onboardingConfig,
                onFileAccessPermissionChange = { granted ->
                    viewModel.setFileAccessPermissionGranted(granted)
                    hasRealFilePermission = granted
                },
                onUsageAnalyticsPermissionChange = { viewModel.setUsageAnalyticsPermissionGranted(it) },
                onBatteryOptimizationChange = { viewModel.setBatteryOptimizationDisabled(it) },
                onNextClick = { viewModel.nextPage() },
                onBackClick = { viewModel.previousPage() }
            )
            2 -> InstallationOptionsPage(
                onboardingConfig = onboardingConfig,
                onOpenJdkVersionChange = { viewModel.setSelectedOpenJdkVersion(it) },
                onBuildToolsVersionChange = { viewModel.setSelectedBuildToolsVersion(it) },
                onGitEnabledChange = { viewModel.setGitEnabled(it) },
                onGitLfsEnabledChange = { viewModel.setGitLfsEnabled(it) },
                onSshEnabledChange = { viewModel.setSshEnabled(it) },
                onNextClick = { viewModel.nextPage() },
                onBackClick = { viewModel.previousPage() }
            )
            3 -> SummaryPage(
                onboardingConfig = onboardingConfig,
                canComplete = hasRealFilePermission,
                onStartInstallation = {
                    val currentPermission = checkFileAccessPermission()
                    if (currentPermission) {
                        viewModel.startInstallation()
                        viewModel.completeOnboarding()
                        onOnboardingComplete()
                    } else {
                        hasRealFilePermission = false
                        viewModel.setFileAccessPermissionGranted(false)
                    }
                },
                onBackClick = { viewModel.previousPage() }
            )
        }
    }
}

private fun checkFileAccessPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        true
    }
}
