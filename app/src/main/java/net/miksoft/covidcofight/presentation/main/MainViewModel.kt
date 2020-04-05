package net.miksoft.covidcofight.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.miksoft.covidcofight.R
import net.miksoft.covidcofight.data.repositories.MainRepository
import net.miksoft.covidcofight.domain.EntriesController
import net.miksoft.covidcofight.domain.UserController
import net.miksoft.covidcofight.domain.ble.GeneralBluetoothController
import net.miksoft.covidcofight.presentation.common.SingleLiveEvent

class MainViewModel : ViewModel() {

    val isEnabled: LiveData<Boolean>
        get() = isEnabledLiveEvent
    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData
    val genericError: LiveData<Unit>
        get() = genericErrorLiveEvent
    val navigation: LiveData<NavDirections>
        get() = navigationLiveEvent
    val infoDialog: LiveData<Int>
        get() = infoDialogLiveEvent

    private val isEnabledLiveEvent =
        MutableLiveData<Boolean>()
    private val isLoadingLiveData = MutableLiveData<Boolean>()
    private val genericErrorLiveEvent =
        SingleLiveEvent<Unit>()
    private val navigationLiveEvent =
        SingleLiveEvent<NavDirections>()
    private val infoDialogLiveEvent =
        SingleLiveEvent<Int>()

    init {
        viewModelScope.launch {
            isEnabledLiveEvent.value =
                withContext(viewModelScope.coroutineContext) {
                    GeneralBluetoothController.resumeOperation()
                }
        }
    }

    fun onScanningStatusClicked() {
        viewModelScope.launch {
            isEnabledLiveEvent.value = withContext(viewModelScope.coroutineContext) {
                GeneralBluetoothController.toggleOperation()
            }
        }
    }

    fun onUploadDataClicked() {
        isLoadingLiveData.value = true
        viewModelScope.launch {
            try {
                MainRepository.userUploadData(
                    UserController.getLoggedInUser()!!,
                    EntriesController.logEntries()
                )
                infoDialogLiveEvent.value = R.string.upload_data_success
                isLoadingLiveData.value = false
            } catch (e: Exception) {
                isLoadingLiveData.value = false
                genericErrorLiveEvent.value = Unit
            }
        }
    }

    fun onLogOutDeleteClicked() {
        viewModelScope.launch {
            UserController.logout()
            EntriesController.deleteAll()
            GeneralBluetoothController.reset()
            navigationLiveEvent.value = MainFragmentDirections.actionMainFragmentToIntroFragment()
        }
    }
}