package com.intellisoft.androidcuisine.views.Bienvenida

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.intellisoft.androidcuisine.domain.repository.auth.AuthRepositoryImpl
import com.intellisoft.androidcuisine.domain.repository.auth.AuthRepository
import com.intellisoft.androidcuisine.util.FcmHelper
import com.intellisoft.androidcuisine.util.SessionManager
import kotlinx.coroutines.launch

class BienvenidaViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = AuthRepositoryImpl()
    private val sessionManager = SessionManager.getInstance(application)

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _isSessionActive = MutableLiveData<Boolean>()
    val isSessionActive: LiveData<Boolean> = _isSessionActive

    fun checkSession() {
        if (sessionManager.isLoggedIn()) {
            _isSessionActive.value = true
        } else {
            if (sessionManager.getAccessToken() != null) {
                sessionManager.clearSession()
            }
            _isSessionActive.value = false
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginState.value = LoginState.Error("Completa todos los campos")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val fcmToken = FcmHelper.getToken(getApplication())

            if (fcmToken.isEmpty()) {
                _loginState.value = LoginState.Error("Error obteniendo token de notificaciones")
                return@launch
            }

            val result = authRepository.login(email, password, fcmToken)

            result.fold(
                onSuccess = { loginResponse ->
                    sessionManager.saveLoginData(loginResponse)
                    _loginState.value = LoginState.Success(loginResponse.user.nombre)
                },
                onFailure = { exception ->
                    _loginState.value = LoginState.Error(exception.message ?: "Error desconocido")
                }
            )
        }
    }
}

sealed class LoginState {
    object Loading : LoginState()
    data class Success(val userName: String) : LoginState()
    data class Error(val message: String) : LoginState()
}