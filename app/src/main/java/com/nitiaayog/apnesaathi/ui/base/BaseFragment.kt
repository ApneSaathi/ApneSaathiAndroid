package com.nitiaayog.apnesaathi.ui.base

import android.app.AlertDialog
import android.content.DialogInterface
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
import com.google.android.material.snackbar.Snackbar
import com.nitiaayog.apnesaathi.ApneSaathiApplication
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.model.CallData
import com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform.SeniorCitizenFeedbackFormActivity
import com.nitiaayog.apnesaathi.utility.CALL_ID
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.concurrent.TimeUnit

abstract class BaseFragment<VM : ViewModel> : Fragment() {

    companion object {
        private const val NAVIGATION_DELAY: Long = 5000L
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONST_PERMISSION_CALL_PHONE) {
            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    showPermissionTextPopup(
                        R.string.permission_text, !shouldShowRequestPermissionRationale(permission),
                        permission
                    )
                } else prepareToCallPerson()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONST_PERMISSION_FROM_SETTINGS) {
            if (checkPermission(PERMISSION_CALL_PHONE)) prepareToCallPerson()
            else Snackbar.make(
                constraintLayout, R.string.call_permission_denied, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        if ((disposables.size() > 0) && !disposables.isDisposed) {
            disposables.dispose()
            disposables.clear()
        }
        super.onDestroy()
    }

    private fun checkPermission(permission: String): Boolean {
        val callPermission = ContextCompat.checkSelfPermission(context!!, permission)
        return (callPermission == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission(vararg permissions: String) =
        requestPermissions(permissions, CONST_PERMISSION_CALL_PHONE)

    protected fun prepareToCallPerson() {
        if (checkPermission(PERMISSION_CALL_PHONE)) onCallPermissionGranted()
        else requestPermission(PERMISSION_CALL_PHONE)
    }

    private fun showPermissionTextPopup(
        @StringRes message: Int, navigateToSetting: Boolean, permission: String
    ) {
        val dialog = AlertDialog.Builder(activity, R.style.Theme_AlertDialog)
            .setTitle(R.string.permission_detail).apply {
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
            }.create()
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(context!!, R.color.color_orange))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(context!!, R.color.color_orange))
    }

    protected fun placeCall(selectedCallData: CallData, containerId: Int) {
        /*Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:${selectedUser.phoneNumber}")
            if (this.resolveActivity(activity!!.packageManager) != null) startActivity(this)
            else onCallPermissionDenied()
        }*/

        Observable.timer(NAVIGATION_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                /*addFragment(
                    containerId, SeniorCitizenEditFragment(), getString(R.string.edit_fragment)
                )*/
                val intent = Intent(activity, SeniorCitizenFeedbackFormActivity::class.java)
                intent.putExtra(CALL_ID, selectedCallData.callId)
                startActivity(intent)
            }.autoDispose(disposables)

        val intent = Intent(activity, SeniorCitizenFeedbackFormActivity::class.java)
        intent.putExtra(CALL_ID, selectedCallData.callId)
        startActivity(intent)
    }

    abstract fun provideViewModel(): VM

    @LayoutRes
    abstract fun provideLayoutResource(): Int

    abstract fun onCallPermissionGranted()

    abstract fun onCallPermissionDenied()
}