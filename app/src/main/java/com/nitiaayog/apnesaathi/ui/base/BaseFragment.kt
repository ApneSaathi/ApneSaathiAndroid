package com.nitiaayog.apnesaathi.ui.base

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
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

        val REQUIRED_CALL_PERMISSIONS: Array<String> = arrayOf(
            android.Manifest.permission.CALL_PHONE/*, android.Manifest.permission.READ_PHONE_STATE*/
        )

        const val CONST_PERMISSION_CALL_PHONE: Int = 110
        const val CONST_PERMISSION_FROM_SETTINGS: Int = 1101

        private val TAG: String = "TAG -- ${BaseFragment::class.java.simpleName} -->"

        private const val NAVIGATION_DELAY: Long = 5000L
    }

    private var callStatusIdle: Int = 0
    private val phoneStateListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            super.onCallStateChanged(state, phoneNumber)
            when (state) {
                TelephonyManager.CALL_STATE_IDLE -> {
                    println("$TAG Call Idle")
                    callStatusIdle = if (callStatusIdle == 1) {
                        // Here we will stop listening the call status because call is disconnected.
                        stopListeningCallStatus()
                        println("$TAG Call State Stopped")
                        0
                    } else 1 // Call just initialized
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    // Once call will be placed CALL_STATE_IDLE and CALL_STATE_OFFHOOK will be
                    // called regardless of called person has picked up a call or not for outgoing
                    // calls only.
                    println("$TAG Call Off Hook")
                }
                TelephonyManager.CALL_STATE_RINGING -> {
                    // Works only for Incoming calls
                    println("$TAG Call Ringing")
                }
                else -> println("$TAG Call Other")
            }
        }
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
                    return
                } else {
                    prepareToCallPerson()
                    return
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONST_PERMISSION_FROM_SETTINGS) {
            if (checkPermissions(REQUIRED_CALL_PERMISSIONS)) prepareToCallPerson()
            else Snackbar.make(
                constraintLayout, R.string.call_permission_denied, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        try {
            callStatusIdle = 0
            stopListeningCallStatus()
        } catch (e: Exception) {
            println("$TAG ${e.message}")
        }
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

    private fun checkPermissions(permissions: Array<String>): Boolean {
        permissions.forEach { if (!checkPermission(it)) return false }
        return true
    }

    private fun requestPermissions(permissions: Array<String>) =
        requestPermissions(permissions, CONST_PERMISSION_CALL_PHONE)

    protected fun prepareToCallPerson() {
        if (checkPermissions(REQUIRED_CALL_PERMISSIONS)) onCallPermissionGranted()
        else requestPermissions(REQUIRED_CALL_PERMISSIONS)
    }

    private fun registerReceiver() {
        if (checkPermissions(REQUIRED_CALL_PERMISSIONS)) {
            (requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?)?.run {
                this.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
            }
        }
    }

    private fun stopListeningCallStatus() {
        (requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?)?.run {
            this.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }
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
                    } else requestPermissions(arrayOf(permission))
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
        registerReceiver()
        Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:9016903906")//7600598153
            if (this.resolveActivity(activity!!.packageManager) != null) startActivity(this)
            else onCallPermissionDenied()
        }

        Observable.timer(NAVIGATION_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                val intent = Intent(activity, SeniorCitizenFeedbackFormActivity::class.java)
                intent.putExtra(CALL_ID, selectedCallData.callId)
                startActivity(intent)
            }.autoDispose(disposables)

        /*val intent = Intent(activity, SeniorCitizenFeedbackFormActivity::class.java)
        intent.putExtra(CALL_ID, selectedCallData.callId)
        startActivity(intent)*/
    }

    abstract fun provideViewModel(): VM

    @LayoutRes
    abstract fun provideLayoutResource(): Int

    abstract fun onCallPermissionGranted()

    abstract fun onCallPermissionDenied()
}