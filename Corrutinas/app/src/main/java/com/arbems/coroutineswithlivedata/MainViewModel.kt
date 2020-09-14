package com.arbems.coroutineswithlivedata

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(var ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

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