package net.miksoft.covidcofight.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.miksoft.covidcofight.data.models.User

object AuthenticationRepository : BaseRepository() {

    private const val paramTelephone = "telephone"

    suspend fun register(telephone: String): Boolean = callService(
        method = "user_register",
        parameters = mapOf(paramTelephone to telephone)
    ) == "1"

    suspend fun activate(telephone: String, authCode: String) = withContext(Dispatchers.IO) {
        callService(
            method = "user_activate",
            parameters = mapOf(
                paramTelephone to telephone,
                "auth_code" to authCode
            )
        ).run {
            gson.fromJson(this, User::class.java)
        }
    }
}