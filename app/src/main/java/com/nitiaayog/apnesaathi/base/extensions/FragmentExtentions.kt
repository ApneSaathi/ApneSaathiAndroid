package com.nitiaayog.apnesaathi.base.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nitiaayog.apnesaathi.R

inline fun <reified VM : ViewModel> Fragment.getViewModel(crossinline factory: () -> VM): VM =
    createViewModel(factory)

inline fun <reified VM : ViewModel> Fragment.createViewModel(crossinline factory: () -> VM): VM {

    @Suppress("UNCHECKED_CAST")
    val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = factory() as VM
    }

    return ViewModelProvider(this, viewModelFactory)[VM::class.java]
}

fun Fragment.addFragment(
    containerId: Int, fragment: Fragment, fragmentTag: String, addToBackStack: Boolean = true
) = fragmentManager!!.beginTransaction()
    .setCustomAnimations(
        R.anim.slide_in_from_right, R.anim.slide_out_to_left, 0, 0
    )
    .add(containerId, fragment, fragmentTag)
    .addToBackStack(if (addToBackStack) fragmentTag else null)
    .commit()

fun Fragment.replaceFragment(
    containerId: Int, fragment: Fragment, fragmentTag: String, addToBackStack: Boolean = true
) = fragmentManager!!.beginTransaction()
    .setCustomAnimations(
        R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left,
        R.anim.slide_out_to_right
    )
    .replace(containerId, fragment, fragmentTag)
    .addToBackStack(if (addToBackStack) fragmentTag else null)
    .commit()