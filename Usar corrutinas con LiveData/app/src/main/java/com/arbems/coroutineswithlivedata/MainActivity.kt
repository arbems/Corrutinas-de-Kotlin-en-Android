package com.arbems.coroutineswithlivedata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.loginResult.observe(this) { success ->
            Toast.makeText(applicationContext, if (success) "Success" else "Failure", Toast.LENGTH_SHORT).show()
        }

        button.setOnClickListener {
            viewModel.onSubmitClicked(editTextUserName.text.toString(), editTextPassword.text.toString())
        }
    }

}