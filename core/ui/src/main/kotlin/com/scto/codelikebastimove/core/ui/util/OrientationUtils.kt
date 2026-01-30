package com.scto.codelikebastimove.core.ui.util

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
@ReadOnlyComposable
fun isLandscape(): Boolean {
  val configuration = LocalConfiguration.current
  return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

@Composable
@ReadOnlyComposable
fun isCompactHeight(): Boolean {
  val configuration = LocalConfiguration.current
  return configuration.screenHeightDp < 480
}

@Composable
@ReadOnlyComposable
fun isTablet(): Boolean {
  val configuration = LocalConfiguration.current
  return configuration.smallestScreenWidthDp >= 600
}
