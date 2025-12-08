package com.scto.codelikebastimove.feature.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = viewModel(),
    onOnboardingComplete: () -> Unit
) {
    val onboardingConfig by viewModel.onboardingConfig.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()

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
                onFileAccessPermissionChange = { viewModel.setFileAccessPermissionGranted(it) },
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
                canComplete = onboardingConfig.fileAccessPermissionGranted,
                onStartInstallation = {
                    if (onboardingConfig.fileAccessPermissionGranted) {
                        viewModel.startInstallation()
                        viewModel.completeOnboarding()
                        onOnboardingComplete()
                    }
                },
                onBackClick = { viewModel.previousPage() }
            )
        }
    }
}
