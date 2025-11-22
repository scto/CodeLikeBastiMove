/*
 * Copyright 2024 | Dmitri Chernysh | http://mobile-dev.pro
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.scto.navigation.screen

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.scto.chatlist.di.featureChatListModule
import com.scto.chatlist.view.ChatListScreen
import com.scto.chatlist.view.ChatListViewModel
import com.scto.di.rememberNavViewModel
import com.scto.navigation.Screen
import com.scto.ui.compositionlocal.LocalAnalytics
import com.scto.ui.ext.findActivity

fun NavGraphBuilder.chatListScreen() {
    composable(
        route = Screen.ChatList.route
    ) {
        val context = LocalContext.current
        val analytics = LocalAnalytics.current

        LaunchedEffect(Unit) {
            analytics.trackScreen(
                "ChatListScreen",
                context.findActivity()
            )
        }

        val viewModel = rememberNavViewModel<ChatListViewModel>(
            modules = { listOf(featureChatListModule) }
        )

        ChatListScreen(
            state = viewModel.uiState,
            onClick = { chat ->
                //TODO: open chat screen
            }
        )
    }
}
