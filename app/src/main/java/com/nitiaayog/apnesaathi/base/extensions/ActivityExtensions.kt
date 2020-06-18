package com.nitiaayog.apnesaathi.base.extensions

import android.content.Intent
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar

inline fun <reified VM : ViewModel> AppCompatActivity.getViewModel(crossinline factory: () -> VM): VM =
    createViewModel(factory)

inline fun <reified VM : ViewModel> AppCompatActivity.createViewModel(crossinline factory: () -> VM): VM {

    @Suppress("UNCHECKED_CAST")
    val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = factory() as VM
    }

    return ViewModelProvider(this, viewModelFactory)[VM::class.java]
}

fun <T : AppCompatActivity> AppCompatActivity.getTargetIntent(targetActivity: Class<T>) =
    Intent(this, targetActivity)

/*fun AppCompatActivity.setLightStatusBar() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    var flags = window.decorView.systemUiVisibility
    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    window.decorView.systemUiVisibility = flags
}*/

fun CallSnackbar(rootRelativeLayout: RelativeLayout, message: String) {
    val snackbar: Snackbar = Snackbar.make(rootRelativeLayout, message, Snackbar.LENGTH_LONG)
    snackbar.show()
}

fun AppCompatActivity.addFragment(
    containerId: Int, fragment: Fragment, fragmentTag: String, addToBackStack: Boolean = true
) = supportFragmentManager.beginTransaction()
    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
    .add(containerId, fragment, fragmentTag)
    .addToBackStack(if (addToBackStack) fragmentTag else null)
    .commit()

fun AppCompatActivity.replaceFragment(
    containerId: Int, fragment: Fragment, fragmentTag: String, addToBackStack: Boolean = true
) = supportFragmentManager.beginTransaction()
    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
    .replace(containerId, fragment, fragmentTag)
    .addToBackStack(if (addToBackStack) fragmentTag else null)
    .commit()
