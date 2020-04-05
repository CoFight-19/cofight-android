package net.miksoft.covidcofight.domain

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.miksoft.covidcofight.data.models.LogEntries
import net.miksoft.covidcofight.data.storage.SharedPreferencesStorage
import java.util.*

object EntriesController {

    private const val LOG_KEY = "LogController_log"

    private val gson = Gson()

    private var cachedLogEntries: LogEntries? = null

    fun log(userIdString: String) {
        GlobalScope.launch(Dispatchers.IO) {
            initIfNeeded()

            val userId = userIdString.toInt()
            val oldestTimestampSec = Calendar.getInstance().also {
                it.add(Calendar.HOUR, -ConfigurationController.DEDUPLICATE_HOURS)
            }.toEpochSec()
            if (cachedLogEntries!!.any { it.user_id == userId && it.ts > oldestTimestampSec }) {
                return@launch
            }

            cachedLogEntries!!.add(
                LogEntries.LogEntryItem(
                    Calendar.getInstance().toEpochSec(),
                    userId
                )
            )
            storeEntries(cachedLogEntries!!)
        }
    }

    fun logEntries(): LogEntries {
        initIfNeeded()
        return cachedLogEntries!!
    }

    fun deleteAll() {
        GlobalScope.launch {
            cachedLogEntries = null
            SharedPreferencesStorage.remove(LOG_KEY, encrypted = true)
        }
    }

    private fun initIfNeeded() {
        if (cachedLogEntries != null) return

        SharedPreferencesStorage.get(LOG_KEY, default = "", encrypted = true).run {
            cachedLogEntries = if (this.isNullOrBlank()) {
                LogEntries()
            } else {
                cleanUpOldEntries(
                    gson.fromJson(this, LogEntries::class.java)
                )
            }
        }
    }

    private fun cleanUpOldEntries(logEntries: LogEntries): LogEntries {
        val oldestTimestampSec = Calendar.getInstance().also {
            it.add(Calendar.DATE, -ConfigurationController.RETENTION_DAYS)
        }.toEpochSec()

        val filteredLogEntries = LogEntries().also {
            it.addAll(logEntries.filter { entry ->
                entry.ts > oldestTimestampSec
            })
        }

        if (logEntries != filteredLogEntries) {
            storeEntries(filteredLogEntries)
        }
        return filteredLogEntries
    }

    private fun storeEntries(logEntries: LogEntries) {
        SharedPreferencesStorage.put(
            LOG_KEY,
            gson.toJson(logEntries), encrypted = true
        )
    }

    private fun Calendar.toEpochSec(): Long = this.timeInMillis/1000
}