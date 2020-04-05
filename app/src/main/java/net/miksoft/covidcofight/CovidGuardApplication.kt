package net.miksoft.covidcofight

import android.app.Application
import net.miksoft.covidcofight.data.storage.SharedPreferencesStorage
import net.miksoft.covidcofight.domain.NotificationsController
import net.miksoft.covidcofight.domain.ble.BroadcastController
import net.miksoft.covidcofight.domain.ble.ListenerController

class CovidGuardApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationsController.init(applicationContext)
        SharedPreferencesStorage.init(applicationContext)
        BroadcastController.init(applicationContext)
        ListenerController.init(applicationContext)
    }
}