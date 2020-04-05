package net.miksoft.covidcofight.presentation.verify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import kotlinx.coroutines.launch
import net.miksoft.covidcofight.data.repositories.AuthenticationRepository
import net.miksoft.covidcofight.domain.UserController
import net.miksoft.covidcofight.presentation.common.SingleLiveEvent
import net.miksoft.covidcofight.presentation.verify.VerifyFragmentDirections
import java.lang.Exception

class VerifyViewModel : ViewModel() {

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData
    val genericError: LiveData<Unit>
        get() = genericErrorLiveEvent
    val navigation: LiveData<NavDirections>
        get() = navigationLiveEvent

    private val isLoadingLiveData = MutableLiveData<Boolean>()
    private val genericErrorLiveEvent =
        SingleLiveEvent<Unit>()
    private val navigationLiveEvent =
        SingleLiveEvent<NavDirections>()

    fun onCodeEntered(telephone: Int, code: String) {
        isLoadingLiveData.value = true
        viewModelScope.launch {
            try {
                UserController.saveUser(
                AuthenticationRepository.activate(telephone.toString(), code))
                navigationLiveEvent.value =
                    VerifyFragmentDirections.actionVerifyFragmentToMainFragment()
            }
            catch (e: Exception) {
                isLoadingLiveData.value = false
                genericErrorLiveEvent.value = Unit
            }
        }
    }

}