package paige.navic.data.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.entities.GenreEntity
import paige.navic.data.database.relations.GenreWithAlbums
import paige.navic.shared.Logger

@Dao
interface GenreDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertGenre(song: GenreEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertGenres(genres: List<GenreEntity>)

	@Query("DELETE FROM GenreEntity WHERE genreName = :genreName AND serverId = :serverId")
	suspend fun deleteGenre(genreName: String, serverId: String)

	@Transaction
	@Query("SELECT * FROM GenreEntity WHERE serverId = :serverId ORDER BY albumCount DESC")
	suspend fun getGenres(serverId: String): List<GenreEntity>

	@Transaction
	@Query("SELECT * FROM GenreEntity WHERE serverId = :serverId ORDER BY albumCount DESC")
	fun getGenresFlow(serverId: String): Flow<List<GenreEntity>>

	@Transaction
	@Query("SELECT * FROM GenreEntity WHERE serverId = :serverId ORDER BY albumCount DESC")
	suspend fun getGenresWithAlbums(serverId: String): List<GenreWithAlbums>

	@Transaction
	@Query("SELECT * FROM GenreEntity WHERE serverId = :serverId ORDER BY albumCount DESC")
	fun getGenresWithAlbumsFlow(serverId: String): Flow<List<GenreWithAlbums>>

	@Query("DELETE FROM GenreEntity WHERE serverId = :serverId")
	suspend fun clearGenresForServer(serverId: String)

	@Query("SELECT genreName FROM GenreEntity WHERE serverId = :serverId")
	suspend fun getAllGenreNames(serverId: String): List<String>

	@Transaction
	suspend fun updateAllGenres(serverId: String, remoteGenres: List<GenreEntity>) {
		val remoteNames = remoteGenres.map { it.genreName }.toSet()
		getAllGenreNames(serverId).forEach { localName ->
			if (localName !in remoteNames) {
				deleteGenre(localName, serverId)
			}
		}
		insertGenres(remoteGenres)
	}
}
