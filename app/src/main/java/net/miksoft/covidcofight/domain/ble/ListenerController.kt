package net.miksoft.covidcofight.domain.ble

import android.app.PendingIntent
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.ParcelUuid
import net.miksoft.covidcofight.domain.ConfigurationController
import no.nordicsemi.android.ble.BleManagerCallbacks
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import java.util.*
import kotlin.collections.ArrayList


object ListenerController: BleManagerCallbacks {

    private val pendingIntent by lazy {
        val intent = Intent(applicationContext, MyReceiver::class.java)
        intent.action = "com.example.ACTION_FOUND"
        PendingIntent.getBroadcast(applicationContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private val scanner: BluetoothLeScannerCompat by lazy {
       BluetoothLeScannerCompat.getScanner()
    }
    private val connectingDevices = mutableMapOf<BluetoothDevice, MyBleManager>()
    private val successfullyLoggedDevices = mutableMapOf<BluetoothDevice, Calendar>()

    private lateinit var applicationContext: Context

    fun init(applicationContext: Context) {
        this.applicationContext = applicationContext
    }

    fun setOperation(enabled: Boolean): Boolean {
        try {
            if (enabled) {
                val settings =
                    ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setReportDelay(ConfigurationController.SCANNING_RESULTS_MILLISECONDS)
                        .setUseHardwareBatchingIfSupported(false)
                        .build()
                val filters: MutableList<ScanFilter> =
                    ArrayList()
                filters.add(
                    ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid(ServiceConst.SERVICE_UUID))
                        .build()
                )
                scanner.startScan(filters, settings, applicationContext, pendingIntent)
            } else {
                scanner.stopScan(applicationContext, pendingIntent)
            }
            return enabled
        }
        catch (e: IllegalStateException) {
            // BLE not available (e.g. might be turned off)
            return false
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        if (connectingDevices.containsKey(device)) return
        if (successfullyLoggedDevices.containsKey(device)) {
            if (successfullyLoggedDevices[device]!!.after(Calendar.getInstance().also {
                    it.add(Calendar.HOUR, -ConfigurationController.DEDUPLICATE_HOURS)
                })) {
                return
            }
            else {
                successfullyLoggedDevices.remove(device)
            }
        }

        val manager = MyBleManager(applicationContext)
        manager.setManagerCallbacks(this)
        connectingDevices[device] = manager

        manager.connect(device)
            .timeout(100000)
            .retry(3, 200)
            .fail { d: BluetoothDevice?, status: Int ->
                connectingDevices.remove(d)
            }
            .enqueue()
    }

    fun addSuccessfullyLoggedDevice(device: BluetoothDevice) {
        successfullyLoggedDevices[device] = Calendar.getInstance()
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
    }

    override fun onDeviceDisconnected(device: BluetoothDevice) {
        connectingDevices.remove(device)
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
    }

    override fun onDeviceNotSupported(device: BluetoothDevice) {
    }

    override fun onBondingFailed(device: BluetoothDevice) {
    }

    override fun onServicesDiscovered(device: BluetoothDevice, optionalServicesFound: Boolean) {
    }

    override fun onBondingRequired(device: BluetoothDevice) {
    }

    override fun onLinkLossOccurred(device: BluetoothDevice) {
    }

    override fun onBonded(device: BluetoothDevice) {
    }

    override fun onDeviceReady(device: BluetoothDevice) {
    }

    override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {
        connectingDevices.remove(device)
    }

    override fun onDeviceConnecting(device: BluetoothDevice) {
    }
}