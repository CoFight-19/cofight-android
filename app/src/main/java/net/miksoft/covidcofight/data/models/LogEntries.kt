package net.miksoft.covidcofight.data.models

class LogEntries : ArrayList<LogEntries.LogEntryItem>(){
    data class LogEntryItem(
        val ts: Long,
        val user_id: Int
    )
}