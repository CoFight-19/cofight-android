package net.miksoft.covidcofight.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.miksoft.covidcofight.data.models.LogEntries
import net.miksoft.covidcofight.data.models.User
import okhttp3.MultipartBody

object MainRepository : BaseRepository() {

    suspend fun userUploadData(user: User, logEntries: LogEntries) = withContext(Dispatchers.IO) {
        callService(
            method = "user_upload_data",
            parameters = mapOf(
                "user_id" to user.user_id.toString(),
                "access_token" to user.access_token
            ),
            requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("jdata", gson.toJson(logEntries))
                .build()
        ).also {
            if (it != "true") {
                throw IllegalStateException("Error uploading user data")
            }
        }
    }
}