package com.nitiaayog.apnesaathi.ui.base

import android.content.Context
import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.utility.NetworkProvider
import com.nitiaayog.apnesaathi.utility.REQUEST_DATA_COUNT
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseViewModel : ViewModel() {

    /**
     * Configurations for the pagination
     * */
    protected val config: PagedList.Config by lazy {
        PagedList.Config.Builder().apply {
            setEnablePlaceholders(false)
            setPrefetchDistance(2)
            setInitialLoadSizeHint(REQUEST_DATA_COUNT)
            setPageSize(REQUEST_DATA_COUNT)
        }.build()
    }

    protected val disposables: CompositeDisposable by lazy { CompositeDisposable() }

    /**
     * Network state observable for api calls
     * */
    protected val loaderObservable: MutableLiveData<NetworkRequestState> by lazy {
        MutableLiveData<NetworkRequestState>()
    }

    /**
     * Update Network state as per api status
     * */
    @UiThread
    protected fun updateNetworkState(state: NetworkRequestState) {
        loaderObservable.postValue(state)
    }

    protected fun checkNetworkAvailability(context: Context, apiName: String): Boolean {
        if (!NetworkProvider.isConnected(context)) {
            updateNetworkState(NetworkRequestState.NetworkNotAvailable(apiName))
            return false
        }
        return true
    }

    /**
     * Perform task on IO thread but while executing io block view is getting destroyed then
     * task will be cancelled/discarded
     **/
    protected suspend fun io(block: suspend CoroutineScope.() -> Unit) =
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) { block.invoke(this) }

    /**
     * Perform task on Main(Ui) thread but while executing io block view is getting destroyed then
     * task will be cancelled/discarded
     **/
    protected suspend fun ui(block: suspend CoroutineScope.() -> Unit) =
        withContext(viewModelScope.coroutineContext + Dispatchers.Main) { block.invoke(this) }

    override fun onCleared() {
        if ((disposables.size() > 0) && !disposables.isDisposed) {
            disposables.dispose()
            disposables.clear()
        }
        super.onCleared()
    }
}