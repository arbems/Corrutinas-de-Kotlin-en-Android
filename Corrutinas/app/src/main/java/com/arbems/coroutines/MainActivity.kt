package com.arbems.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.ContinuationInterceptor

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.loginResult.observe(this) { success ->
            Toast.makeText(applicationContext, if (success) "Success" else "Failure", Toast.LENGTH_SHORT).show()
        }

        button.setOnClickListener {
            viewModel.login(editTextUserName.text.toString(), editTextPassword.text.toString())
        }


        runBlocking() {
            val context = this.coroutineContext
            val job = context[Job]
            val continuationInterceptor = context[ContinuationInterceptor]
            val coroutineExceptionHandler = context[CoroutineExceptionHandler]
            val coroutineName = context[CoroutineName]
        }



    }

}