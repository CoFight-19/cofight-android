package net.miksoft.covidcofight.domain

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.miksoft.covidcofight.data.models.User
import net.miksoft.covidcofight.data.storage.SharedPreferencesStorage

object UserController {

    private const val USER_KEY = "UserController_user"

    private val gson = Gson()

    private var cachedUser: User? = null

    suspend fun saveUser(user: User) =
        withContext(Dispatchers.IO) {
            SharedPreferencesStorage.put(
                USER_KEY,
               gson.toJson(user), encrypted = true
            )
        }

    suspend fun getLoggedInUser(): User? {
        if (cachedUser != null) {
            return cachedUser
        }
        return withContext(Dispatchers.IO) {
            SharedPreferencesStorage.get(USER_KEY, default = "", encrypted = true)?.run {
                if (this.isBlank()) {
                    return@run null
                }
                cachedUser =
                    gson.fromJson(this, User::class.java)
                cachedUser
            }
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        cachedUser = null
        SharedPreferencesStorage.remove(USER_KEY, encrypted = true)
    }
}
