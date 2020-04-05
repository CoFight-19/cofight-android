package net.miksoft.covidcofight

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.miksoft.covidcofight.domain.ble.GeneralBluetoothController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneralBluetoothController.init(this)
        setContentView(R.layout.activity_main)
    }
}
