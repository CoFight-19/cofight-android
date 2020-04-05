package net.miksoft.covidcofight.domain.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.miksoft.covidcofight.MainActivity
import net.miksoft.covidcofight.R
import net.miksoft.covidcofight.data.storage.SharedPreferencesStorage
import net.miksoft.covidcofight.domain.NotificationsController
import net.miksoft.covidcofight.presentation.common.showConfirmationDialog
import net.miksoft.covidcofight.presentation.common.showInfoDialog

object GeneralBluetoothController {

    private const val OPERATION_KEY = "GeneralBluetoothController_operation"
    private const val PERMISSION_REQUEST_CODE = 1111

    private lateinit var activity: MainActivity
    private var isInitialized = false

    private val operationStatus
        get() = SharedPreferencesStorage.get(OPERATION_KEY, default = true)

    fun init(activity: MainActivity) {
        GeneralBluetoothController.activity = activity
    }

    suspend fun resumeOperation(): Boolean {
        return setOperation(
            operationStatus
        )
    }

    suspend fun toggleOperation(): Boolean {
        return setOperation(
            !operationStatus
        )
    }

    suspend fun reset() {
        setOperation(
            false
        )
        SharedPreferencesStorage.remove(OPERATION_KEY)
    }

    private suspend fun setOperation(enabled: Boolean): Boolean {
        if (enabled && !canEnable()) {
            SharedPreferencesStorage.put(OPERATION_KEY, false)
            return false
        }

        if (isInitialized && enabled == operationStatus) return enabled

        val broadcast = BroadcastController.setOperation(enabled)
        val listener = ListenerController.setOperation(enabled)
        NotificationsController.setRunningNotification(enabled)
        SharedPreferencesStorage.put(OPERATION_KEY, enabled)

        if (enabled && !broadcast && !listener) {
                BroadcastController.setOperation(false)
                ListenerController.setOperation(false)
                NotificationsController.setRunningNotification(false)
                SharedPreferencesStorage.put(OPERATION_KEY, false)
                return false
        }

        isInitialized = true
        return enabled
    }

    private fun canEnable(): Boolean =
        checkForPermissions() && checkForBluetoothEnabled()

    private fun checkForPermissions(): Boolean {
        val neededPermissions = mutableListOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        ).let {
            it.add(
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
                    Manifest.permission.ACCESS_COARSE_LOCATION
                else
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
            it.toList()
        }
        for (permission in neededPermissions) {
            if (isPermissionMissing(permission)) {
                showPermissionsConfirmation(neededPermissions)
                return false
            }
        }
        return true
    }

    private fun checkForBluetoothEnabled(): Boolean {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(
                enableBtIntent,
                PERMISSION_REQUEST_CODE
            )
            return false
        }

        // isMultipleAdvertisementSupported() requires BT to be enabled
        if (!BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported
            || BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser == null
        ) {
            activity.showInfoDialog(R.string.bluetooth_not_supported)
            return false
        }

        return true
    }

    private fun isPermissionMissing(permission: String): Boolean =
        (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED)

    private fun showPermissionsConfirmation(neededPermissions: List<String>) {
        activity.showConfirmationDialog(
            titleResId = R.string.permissions_confirmation_title,
            messageResId = R.string.permissions_confirmation_body,
            action = {
                ActivityCompat.requestPermissions(
                    activity,
                    neededPermissions.toTypedArray(),
                    PERMISSION_REQUEST_CODE
                )
            }
        )
    }
}