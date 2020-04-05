package net.miksoft.covidcofight.presentation.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import kotlinx.coroutines.launch
import net.miksoft.covidcofight.data.repositories.AuthenticationRepository
import net.miksoft.covidcofight.presentation.common.SingleLiveEvent
import net.miksoft.covidcofight.presentation.signup.SignUpFragmentDirections
import java.lang.Exception

class SignUpViewModel : ViewModel() {

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

    fun onDoneClicked(telephone: String) {
        isLoadingLiveData.value = true
        viewModelScope.launch {
            try {
                if (AuthenticationRepository.register(telephone)) {
                    navigationLiveEvent.value =
                        SignUpFragmentDirections.actionSignUpFragmentToVerifyFragment(
                            telephone.toInt()
                        )
                    isLoadingLiveData.value = false
                }
                else {
                    isLoadingLiveData.value = false
                    genericErrorLiveEvent.value = Unit
                }
            }
            catch (e: Exception) {
                isLoadingLiveData.value = false
                genericErrorLiveEvent.value = Unit
            }
        }
    }
}