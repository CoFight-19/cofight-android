package net.miksoft.covidcofight.domain.ble

import android.bluetooth.le.BluetoothLeScanner
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.ParcelUuid
import net.miksoft.covidcofight.domain.EntriesController
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanResult


class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(BluetoothLeScanner.EXTRA_LIST_SCAN_RESULT)) {
            val results: ArrayList<ScanResult> =
                intent.getParcelableArrayListExtra(BluetoothLeScannerCompat.EXTRA_LIST_SCAN_RESULT)
            for (result in results) {
                val otherDeviceUserId = result.scanRecord?.serviceData?.get(ParcelUuid(ServiceConst.SERVICE_DATA_UUID))?.toString(Charsets.UTF_8)
                if (otherDeviceUserId != null) {
                    EntriesController.log(otherDeviceUserId)
                }
                else {
                    ListenerController.connectToDevice(result.device)
                }
            }
        }
    }
}