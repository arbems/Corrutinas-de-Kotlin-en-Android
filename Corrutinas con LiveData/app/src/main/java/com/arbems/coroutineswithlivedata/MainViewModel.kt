package com.arbems.coroutineswithlivedata

import androidx.lifecycle.*
import kotlinx.coroutines.*

class MainViewModel(var ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    val loginResult: LiveData<Boolean> = liveData {
        val data = loadUser() // loadUser is a suspend function.
        emit(data)
    }

    private suspend fun loadUser(): Boolean {
        delay(3000)
        return true
    }

    fun onSubmitClicked(user: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = withContext(ioDispatcher) { validateLogin(user, password) }
        }
    }

    private fun validateLogin(user: String, password: String): Boolean {
        Thread.sleep(2000)
        return user.isNotEmpty() && password.isNotEmpty()
    }
}