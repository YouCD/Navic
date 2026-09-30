/*
 * Navic, a Subsonic music streaming app for Android
 * Copyright (c) 2026 paige
 * SPDX-License-Identifier: GPL-3.0-only
 */

package paige.navic.data.database

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import paige.navic.data.database.dao.AlbumDao
import paige.navic.data.database.dao.ArtistDao
import paige.navic.data.database.dao.DownloadDao
import paige.navic.data.database.dao.GenreDao
import paige.navic.data.database.dao.LyricDao
import paige.navic.data.database.dao.PlaylistDao
import paige.navic.data.database.dao.RadioDao
import paige.navic.data.database.dao.SongDao
import paige.navic.data.database.dao.SyncActionDao
import paige.navic.data.database.entity.AlbumEntity
import paige.navic.data.database.entity.ArtistEntity
import paige.navic.data.database.entity.DownloadEntity
import paige.navic.data.database.entity.GenreEntity
import paige.navic.data.database.entity.LyricEntity
import paige.navic.data.database.entity.PlaylistEntity
import paige.navic.data.database.entity.PlaylistSongCrossRef
import paige.navic.data.database.entity.RadioEntity
import paige.navic.data.database.entity.SongEntity
import paige.navic.data.database.entity.SyncActionEntity

@Database(
	version = 21,
	entities = [
		AlbumEntity::class,
		GenreEntity::class,
		PlaylistEntity::class,
		PlaylistSongCrossRef::class,
		SongEntity::class,
		ArtistEntity::class,
		RadioEntity::class,
		LyricEntity::class,
		SyncActionEntity::class,
		DownloadEntity::class
	]
)
@ColumnTypeConverters(Converters::class)
abstract class CacheDatabase : RoomDatabase() {
	abstract fun albumDao(): AlbumDao
	abstract fun genreDao(): GenreDao
	abstract fun downloadDao(): DownloadDao
	abstract fun playlistDao(): PlaylistDao
	abstract fun songDao(): SongDao
	abstract fun artistDao(): ArtistDao
	abstract fun radioDao(): RadioDao
	abstract fun lyricDao(): LyricDao
	abstract fun syncActionDao(): SyncActionDao
}
