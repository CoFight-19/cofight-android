package net.miksoft.covidcofight.domain.ble

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.ParcelUuid
import net.miksoft.covidcofight.domain.UserController
import java.nio.charset.Charset


object BroadcastController {

    private val bluetoothManager
        get() = applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter
        get() = bluetoothManager.adapter.bluetoothLeAdvertiser
    private val settings = AdvertiseSettings.Builder()
        .setConnectable(true)
        .build()
    private val advertiseData = AdvertiseData.Builder()
        .setIncludeDeviceName(true)
        .build()
    private val scanResponseData
        get() = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(ServiceConst.SERVICE_UUID))
            .addServiceData(
                ParcelUuid(ServiceConst.SERVICE_DATA_UUID),
                userId!!.toByteArray(Charset.forName("UTF-8"))
            )
            .build()

    private lateinit var applicationContext: Context
    private var bluetoothGattServer: BluetoothGattServer? = null
    private var userId: String? = null

    fun init(applicationContext: Context) {
        this.applicationContext = applicationContext
    }

    suspend fun setOperation(enabled: Boolean): Boolean {
        if (enabled) {
            userId = UserController.getLoggedInUser()!!.user_id.toString()

            bluetoothAdapter.startAdvertising(
                settings,
                advertiseData,
                scanResponseData,
                AdvertisingCallback
            )

            bluetoothGattServer =
                bluetoothManager.openGattServer(applicationContext, ServerCallback)
            if (bluetoothGattServer == null) {
                // OS cannot provide GATT server, either not supported or error occurred
                return false
            }
            val service = BluetoothGattService(
                ServiceConst.SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY
            )
            val characteristic = BluetoothGattCharacteristic(
                ServiceConst.CHAR_UUID,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_READ
            )
            service.addCharacteristic(characteristic)
            bluetoothGattServer!!.addService(service)
        } else {
            bluetoothAdapter?.stopAdvertising(AdvertisingCallback)
            bluetoothGattServer?.close()
            userId = null
        }
        return enabled
    }

    private object ServerCallback : BluetoothGattServerCallback() {

        override fun onCharacteristicReadRequest(
            device: BluetoothDevice,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
            val value: ByteArray = userId!!.toByteArray(Charset.forName("UTF-8"))
            bluetoothGattServer?.sendResponse(
                device,
                requestId,
                BluetoothGatt.GATT_SUCCESS,
                0,
                value
            )
        }
        // TODO: Check base class methods and notify about failure
    }

    private object AdvertisingCallback : AdvertiseCallback() {
        // TODO: Check base class methods and notify about failure
    }
}

