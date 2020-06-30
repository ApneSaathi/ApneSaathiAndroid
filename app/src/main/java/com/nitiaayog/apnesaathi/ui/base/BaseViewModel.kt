package com.nitiaayog.apnesaathi.ui.base

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.utility.NetworkProvider
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel : ViewModel() {

    protected val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    protected val loaderObservable: MutableLiveData<NetworkRequestState> by lazy {
        MutableLiveData<NetworkRequestState>()
    }

    protected fun checkNetworkAvailability(context: Context): Boolean {
        if (!NetworkProvider.isConnected(context)) {
            loaderObservable.value = NetworkRequestState.NetworkNotAvailable
            return false
        }
        return true
    }

    override fun onCleared() {
        if ((disposables.size() > 0) && !disposables.isDisposed) {
            disposables.dispose()
            disposables.clear()
        }
        super.onCleared()
    }
}