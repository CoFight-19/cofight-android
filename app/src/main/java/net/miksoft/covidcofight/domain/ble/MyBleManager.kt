package net.miksoft.covidcofight.domain.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import net.miksoft.covidcofight.domain.EntriesController
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.BleManagerCallbacks
import no.nordicsemi.android.ble.data.Data

class MyBleManager(context: Context) :
    BleManager<BleManagerCallbacks?>(context) {

    private var firstCharacteristic: BluetoothGattCharacteristic? = null

    override fun getGattCallback(): BleManagerGattCallback {
        return MyManagerGattCallback()
    }

    private inner class MyManagerGattCallback : BleManagerGattCallback() {

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val service = gatt.getService(ServiceConst.SERVICE_UUID)
            if (service != null) {
                firstCharacteristic = service.getCharacteristic(ServiceConst.CHAR_UUID)
            }
            return firstCharacteristic != null
        }

        override fun initialize() {
            readCharacteristic(firstCharacteristic)
                .with { bluetoothDevice: BluetoothDevice, data: Data ->
                    val decoded = data.value?.toString(Charsets.UTF_8)
                    if (decoded != null) {
                        EntriesController.log(decoded)
                        ListenerController.addSuccessfullyLoggedDevice(bluetoothDevice)
                    }
                }
                .enqueue()
        }

        override fun onDeviceDisconnected() {
            firstCharacteristic = null
        }
    }
}
