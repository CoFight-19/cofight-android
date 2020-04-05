package net.miksoft.covidcofight.data.repositories

import android.net.Uri
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.miksoft.covidcofight.domain.ConfigurationController
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

abstract class BaseRepository {

    companion object {
        private const val methodQuery = "method"

        private val baseUrl = Uri.parse(ConfigurationController.BASE_URL)
        private val client = OkHttpClient()
    }

    protected val gson = Gson()

    protected suspend fun callService(
        method: String,
        parameters: Map<String, String> = mapOf(),
        requestBody: RequestBody? = null
    ): String {
        val uri = baseUrl
            .buildUpon()
            .appendQueryParameter(methodQuery, method)
        for ((key, value) in parameters) {
            uri.appendQueryParameter(key, value)
        }
        val request: Request = Request.Builder()
            .post(requestBody ?: "".toRequestBody())
            .url(
                uri
                    .build()
                    .toString()
            )
            .build()
        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().body!!.string()
        }
    }
}
