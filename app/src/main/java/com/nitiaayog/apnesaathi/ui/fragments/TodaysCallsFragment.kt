package com.nitiaayog.apnesaathi.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.TodaysCallsAdapter
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.getViewModel
import com.nitiaayog.apnesaathi.model.CallsConnected
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenfeedbackform.SeniorCitizenFeedbackFormActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.include_recyclerview.*
import java.util.concurrent.TimeUnit

class TodaysCallsFragment : BaseFragment<TodaysCallsViewModel>(),
    TodaysCallsAdapter.OnItemClickListener {

    companion object {
        private const val NAVIGATION_DELAY: Long = 5000L
    }

    private var lastSelectedPosition: Int = -1
    private var lastSelectedItem: CallsConnected? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TodaysCallsAdapter(viewModel.prepareData())
        adapter.setOnItemClickListener(this)
        rvList.adapter = adapter
    }

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
            else Snackbar.make(rvList, R.string.call_permission_denied, Snackbar.LENGTH_LONG).show()
        } else Snackbar.make(rvList, R.string.call_permission_denied, Snackbar.LENGTH_LONG).show()
    }

    override fun provideViewModel(): TodaysCallsViewModel =
        getViewModel { TodaysCallsViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.include_recyclerview

    override fun onItemClick(position: Int, callsConnected: CallsConnected) {
        lastSelectedPosition = position
        lastSelectedItem = callsConnected
        prepareToCallPerson()
    }

    private fun prepareToCallPerson() {
        if (checkPermission(PERMISSION_CALL_PHONE)) placeCall()
        else requestPermission(PERMISSION_CALL_PHONE)
    }

    private fun placeCall() {
        Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:${lastSelectedItem!!.phoneNumber}")
            if (this.resolveActivity(activity!!.packageManager) != null)
                startActivity(this)
            else Snackbar.make(rvList, R.string.not_handle_action, Snackbar.LENGTH_LONG).show()
        }

        Observable.timer(NAVIGATION_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                startActivity(Intent(activity, SeniorCitizenFeedbackFormActivity::class.java))
            }.autoDispose(disposables)
    }
}