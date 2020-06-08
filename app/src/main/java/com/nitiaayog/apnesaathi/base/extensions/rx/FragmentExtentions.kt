package com.nitiaayog.apnesaathi.base.extensions.rx

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified VM : ViewModel> Fragment.getViewModel(crossinline factory: () -> VM): VM =
    createViewModel(factory)

inline fun <reified VM : ViewModel> Fragment.createViewModel(crossinline factory: () -> VM): VM {

    @Suppress("UNCHECKED_CAST")
    val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = factory() as VM
    }

    return ViewModelProvider(this, viewModelFactory)[VM::class.java]
}