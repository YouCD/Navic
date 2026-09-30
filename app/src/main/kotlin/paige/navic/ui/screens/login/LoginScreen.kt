/*
 * Navic, a Subsonic music streaming app for Android
 * Copyright (c) 2026 paige
 * SPDX-License-Identifier: GPL-3.0-only
 */

package paige.navic.ui.screens.login

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import paige.navic.ui.screens.login.pages.LoginScreenContent

@Composable
fun LoginScreen() {
	Scaffold { innerPadding ->
		LoginScreenContent(
			innerPadding = innerPadding
		)
	}
}
