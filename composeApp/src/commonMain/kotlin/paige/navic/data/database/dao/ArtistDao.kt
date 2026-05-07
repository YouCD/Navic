package paige.navic.data.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow
import paige.navic.data.database.entities.ArtistEntity
import paige.navic.shared.Logger

@Dao
interface ArtistDao {
	@Query("SELECT * FROM ArtistEntity WHERE serverId = :serverId ORDER BY name COLLATE NOCASE ASC")
	suspend fun getArtistsAlphabeticalByName(serverId: String): List<ArtistEntity>

	@Query("SELECT * FROM ArtistEntity WHERE serverId = :serverId ORDER BY RANDOM()")
	suspend fun getArtistsRandom(serverId: String): List<ArtistEntity>

	@Query("SELECT * FROM ArtistEntity WHERE serverId = :serverId AND starredAt IS NOT NULL ORDER BY starredAt DESC")
	suspend fun getArtistsStarred(serverId: String): List<ArtistEntity>

	@Query("SELECT * FROM ArtistEntity WHERE serverId = :serverId ORDER BY name COLLATE NOCASE ASC")
	fun getAllArtists(serverId: String): Flow<List<ArtistEntity>>

	@Query("SELECT * FROM ArtistEntity WHERE serverId = :serverId")
	suspend fun getAllArtistsList(serverId: String): List<ArtistEntity>

	@Query("SELECT * FROM ArtistEntity WHERE artistId = :artistId AND serverId = :serverId LIMIT 1")
	suspend fun getArtistById(artistId: String, serverId: String): ArtistEntity?

	@Query("SELECT EXISTS(SELECT 1 FROM ArtistEntity WHERE artistId = :artistId AND serverId = :serverId AND starredAt IS NOT NULL)")
	suspend fun isArtistStarred(artistId: String, serverId: String): Boolean

	@Query("SELECT * FROM ArtistEntity WHERE serverId = :serverId AND name LIKE '%' || :query || '%' COLLATE NOCASE")
	suspend fun searchArtistsList(query: String, serverId: String): List<ArtistEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertArtist(artist: ArtistEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertArtists(artists: List<ArtistEntity>)

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertArtistsIgnoringConflicts(artists: List<ArtistEntity>)

	@Query("DELETE FROM ArtistEntity WHERE artistId = :artistId AND serverId = :serverId")
	suspend fun deleteArtist(artistId: String, serverId: String)

	@Query("DELETE FROM ArtistEntity WHERE serverId = :serverId")
	suspend fun clearAllArtistsForServer(serverId: String)

	@Query("SELECT artistId FROM ArtistEntity WHERE serverId = :serverId")
	suspend fun getAllArtistIds(serverId: String): List<String>

	@Query("SELECT * FROM ArtistEntity WHERE serverId = :serverId AND artistId IN (:ids)")
	suspend fun getArtistsByIds(ids: List<String>, serverId: String): List<ArtistEntity>

	@Transaction
	suspend fun updateAllArtists(serverId: String, remoteArtists: List<ArtistEntity>) {
		val remoteIds = remoteArtists.map { it.artistId }.toSet()
		getAllArtistIds(serverId).forEach { localId ->
			if (localId !in remoteIds) {
				Logger.w("ArtistDao", "artist $localId no longer exists remotely")
				deleteArtist(localId, serverId)
			}
		}
		insertArtists(remoteArtists)
	}
}
