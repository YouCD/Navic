/*
 * Navic, a Subsonic music streaming app for Android
 * Copyright (c) 2026 paige
 * SPDX-License-Identifier: GPL-3.0-only
 */

package paige.navic.domain.models.snackbars


data class PlayerEvent(
	val resource: Int,
	val args: List<Any> = emptyList()
)
