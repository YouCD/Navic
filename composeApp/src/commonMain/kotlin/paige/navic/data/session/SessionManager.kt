package paige.navic.data.session

import com.russhwolf.settings.set
import dev.zt64.subsonic.client.SubsonicAuth
import dev.zt64.subsonic.client.SubsonicClient
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import paige.navic.data.models.User
import paige.navic.data.models.settings.Settings
import com.russhwolf.settings.Settings as KmpSettings

data class ServerConfig(
	val id: String,
	val url: String,
	val username: String
)

object SessionManager {
	private val settings = KmpSettings()
	private val _isLoggedIn = MutableStateFlow(false)
	val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

	private val _activeServerId = MutableStateFlow(settings.getStringOrNull("activeServerId"))
	val activeServerId: StateFlow<String?> = _activeServerId.asStateFlow()

	var api: SubsonicClient = createClient(
		"",
		"",
		""
	)
		private set

	init {
		_activeServerId.value?.let {
			refreshClient(it)
			_isLoggedIn.value = true
		}
	}

	private fun createClient(instanceUrl: String, username: String, password: String) = SubsonicClient(
		baseUrl = instanceUrl,
		auth = SubsonicAuth.Token(
			username = username,
			password = password
		),
		client = "Navic",
		clientConfig = {
			install(UserAgent) {
				agent = "Navic"
			}

			val customHeaders = Settings.shared.customHeadersMap()
			if (customHeaders.isNotEmpty()) {
				defaultRequest {
					customHeaders.forEach { (key, value) -> header(key, value) }
				}
			}
		}
	)

	val currentUser: User?
		get() {
			val activeId = _activeServerId.value ?: return null
			val username = settings.getStringOrNull("server_${activeId}_username") ?: return null
			_isLoggedIn.value = true

			return User(
				name = username,
				avatarUrl = api.getAvatarUrl(username)
			)
		}

	fun getSavedServers(): List<ServerConfig> {
		val existingServers = settings.getString("serverIds", "").split(",").filter { it.isNotEmpty() }
		return existingServers.mapNotNull { id ->
			val url = settings.getStringOrNull("server_${id}_url")
			val username = settings.getStringOrNull("server_${id}_username")
			if (url != null && username != null) {
				ServerConfig(id, url, username)
			} else {
				null
			}
		}
	}

	suspend fun login(
		instanceUrl: String,
		username: String,
		password: String
	) {
		val client = createClient(instanceUrl, username, password)

		try {
			client.ping()
		} catch (e: Exception) {
			throw Exception(
				"Failed to connect to the instance. Please check your credentials and try again.",
				e
			)
		}

		val serverId = "${instanceUrl}_${username}".hashCode().toString()
		val existingServers = settings.getString("serverIds", "").split(",").filter { it.isNotEmpty() }.toMutableSet()

		existingServers.add(serverId)
		settings["serverIds"] = existingServers.joinToString(",")
		settings["server_${serverId}_url"] = instanceUrl
		settings["server_${serverId}_username"] = username
		settings["server_${serverId}_password"] = password

		setActiveServer(serverId, client)
	}

	fun switchServer(serverId: String) {
		refreshClient(serverId)
		settings["activeServerId"] = serverId
		_activeServerId.value = serverId
		_isLoggedIn.value = true
	}

	private fun setActiveServer(serverId: String, client: SubsonicClient) {
		settings["activeServerId"] = serverId
		_activeServerId.value = serverId
		api = client
		_isLoggedIn.value = true
	}

	fun removeServer(serverId: String) {
		val existingServers = settings.getString("serverIds", "").split(",").filter { it.isNotEmpty() }.toMutableSet()
		existingServers.remove(serverId)
		settings["serverIds"] = existingServers.joinToString(",")

		settings.remove("server_${serverId}_url")
		settings.remove("server_${serverId}_username")
		settings.remove("server_${serverId}_password")

		if (_activeServerId.value == serverId) {
			logout()
		}
	}

	fun logout() {
		settings["activeServerId"] = null
		_activeServerId.value = null
		_isLoggedIn.value = false
		api = createClient("", "", "")
	}

	fun refreshClient(serverId: String = _activeServerId.value ?: "") {
		if (serverId.isEmpty()) return
		api = createClient(
			instanceUrl = settings.getString("server_${serverId}_url", ""),
			username = settings.getString("server_${serverId}_username", ""),
			password = settings.getString("server_${serverId}_password", ""),
		)
	}
}
