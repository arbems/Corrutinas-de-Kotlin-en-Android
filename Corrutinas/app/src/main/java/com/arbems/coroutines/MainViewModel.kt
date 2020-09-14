package com.arbems.coroutines

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(var ioDispatcher: CoroutineDispatcher = Dispatchers.IO) : ViewModel() {

    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> get() = _loginResult

    fun login(user: String, password: String) {

        // Create a new coroutine to move the execution off the UI thread
        viewModelScope.launch {
            val result = try {
                validateLogin(user, password)
            } catch (e: Exception) {
                Result.Error(Exception("Network request failed"))
            }

            when (result) {
                is Result.Success<*> -> {
                    _loginResult.value = true
                }
                is Result.Error -> {
                    _loginResult.value = false
                }
            }

        }
    }

    private suspend fun validateLogin(user: String, password: String): Result<Any> {

        // Move the execution of the coroutine to the I/O dispatcher
        return withContext(ioDispatcher) {

            // Blocking network request code
            Thread.sleep(2000) // emulates network request

            if(user.isNotEmpty() && password.isNotEmpty())
                Result.Success<LoginResponse>(LoginResponse())
            else{
                Result.Error(Exception("Network request failed"))
            }
        }
    }
}