package com.nitiaayog.apnesaathi.ui.base

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.nitiaayog.apnesaathi.ApneSaathiApplication
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.datamanager.DataManager
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment<VM : ViewModel> : Fragment() {

    companion object {
        const val PERMISSION_CALL_PHONE: String = android.Manifest.permission.CALL_PHONE
        const val CONST_PERMISSION_CALL_PHONE: Int = 110
        const val CONST_PERMISSION_FROM_SETTINGS: Int = 1101
    }

    protected val dataManager: DataManager by lazy { ApneSaathiApplication.getApiClient() }

    protected val viewModel: VM by lazy { provideViewModel() }

    protected val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(provideLayoutResource(), container, false)

    override fun onDestroy() {
        if ((disposables.size() > 0) && !disposables.isDisposed) {
            disposables.dispose()
            disposables.clear()
        }
        super.onDestroy()
    }

    protected fun checkPermission(permission: String): Boolean {
        val callPermission = ContextCompat.checkSelfPermission(context!!, permission)
        return (callPermission == PackageManager.PERMISSION_GRANTED)
    }

    protected fun requestPermission(vararg permissions: String) =
        requestPermissions(permissions, CONST_PERMISSION_CALL_PHONE)

    protected fun showPermissionTextPopup(
        @StringRes message: Int, navigateToSetting: Boolean, permission: String
    ) {
        AlertDialog.Builder(activity).setTitle(R.string.permission_detail).apply {
            this.setCancelable(false)
            this.setMessage(message)
            this.setPositiveButton(R.string.accept) { dialog, _ ->
                dialog.dismiss()
                if (navigateToSetting) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", activity!!.packageName, null)
                    }
                    startActivityForResult(intent, CONST_PERMISSION_FROM_SETTINGS)
                } else requestPermission(permission)
            }
            this.setNegativeButton(R.string.not_now) { dialog, _ -> dialog.dismiss() }
        }.create().show()
    }

    abstract fun provideViewModel(): VM

    @LayoutRes
    abstract fun provideLayoutResource(): Int
}