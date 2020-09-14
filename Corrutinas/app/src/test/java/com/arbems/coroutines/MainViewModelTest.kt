package com.arbems.coroutines

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(coroutinesTestRule.testDispatcher)
    }

    @Test
    fun `success if user and pass are not empty`() {
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val observer = mock<Observer<Boolean>>()

            viewModel.loginResult.observeForever(observer)

            viewModel.login("alberto", "123456")

            verify(observer).onChanged(true)
        }
    }

    @Test
    fun `error if user is empty`() {
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val observer = mock<Observer<Boolean>>()

            viewModel.loginResult.observeForever(observer)

            viewModel.login("", "123456")

            verify(observer).onChanged(false)
        }
    }
}