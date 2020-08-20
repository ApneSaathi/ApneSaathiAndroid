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
import com.nitiaayog.apnesaathi.utility.REQUEST_CODE
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

    protected val dataManager: DataManager by lazy { ApneSaathiApplication.getDataClient() }

    protected val viewModel: VM by lazy { provideViewModel() }

    protected val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(provideLayoutResource(), container, false)

    /**
     * Requested permissions status callback
     * */
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

    /**
     * Dynamically check the permission i.e. Granted/Denied
     * */
    private fun checkPermission(permission: String): Boolean {
        val callPermission = ContextCompat.checkSelfPermission(context!!, permission)
        return (callPermission == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Request the permission
     * */
    private fun requestPermission(vararg permissions: String) =
        requestPermissions(permissions, CONST_PERMISSION_CALL_PHONE)

    /**
     * Check for the CALL permission and provide appropriate callback
     * */
    protected fun prepareToCallPerson() {
        if (checkPermission(PERMISSION_CALL_PHONE)) onCallPermissionGranted()
        else requestPermission(PERMISSION_CALL_PHONE)
    }

    /**
     * Show the details of requested permission that why this permission is important for app.
     * */
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

    /**
     * Just initiate call to provided phone number
     * */
    protected fun initiateCall(phoneNumber: String) {
        Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
            if (this.resolveActivity(activity!!.packageManager) != null) startActivity(this)
            else onCallPermissionDenied()
        }
    }

    /**
     * This method this initiate a call and after that it will navigate to Feedback for page
     * so that logged in person can store the Senior Citizen's feedback and if any issues are
     * raised by Sr. Citizen that would also be logged and volunteers/staff members can start
     * resolving it.
     * */
    protected fun placeCall(selectedCallData: CallData) {//, containerId: Int
        initiateCall(selectedCallData.contactNumber!!)

        Observable.timer(NAVIGATION_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                val intent = Intent(activity, SeniorCitizenFeedbackFormActivity::class.java)
                dataManager.setUserName(selectedCallData.srCitizenName ?: "")
                dataManager.setSrCitizenGender(selectedCallData.gender ?: "")
                intent.putExtra(CALL_ID, selectedCallData.callId)
                activity!!.startActivityForResult(intent, REQUEST_CODE)
            }.autoDispose(disposables)
    }

    /**
     * Every fragment/activity will have view model.Using this method we can dynamically create
     * ViewModel instance only for Fragment
     * */
    abstract fun provideViewModel(): VM

    /**
     * Ui file from layout folder only to load the user interface of Fragment
     **/
    @LayoutRes
    abstract fun provideLayoutResource(): Int

    /**
     * Once call permission is Granted the calling fragment will get the call back and initiate
     * appropriate actions
     **/
    abstract fun onCallPermissionGranted()

    /**
     * If call permission denied then calling fragment can get the call back and initiate
     * appropriate actions or show message
     * */
    abstract fun onCallPermissionDenied()
}