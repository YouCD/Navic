/*
 * Navic, a Subsonic music streaming app for Android
 * Copyright (c) 2026 paige
 * SPDX-License-Identifier: GPL-3.0-only
 */

package paige.navic.ui.screens.queue.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import paige.navic.domain.manager.ConnectivityManager
import paige.navic.domain.manager.DownloadManager

class QueueViewModel(
	connectivityManager: ConnectivityManager,
	downloadManager: DownloadManager
) : ViewModel() {
	val listState = LazyListState()
	val isOnline = connectivityManager.isOnline
	val downloadedSongs = downloadManager.downloadedSongs
}
