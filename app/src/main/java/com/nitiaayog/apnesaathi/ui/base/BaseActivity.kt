package com.nitiaayog.apnesaathi.ui.base

import android.os.Build
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.nitiaayog.apnesaathi.ApneSaathiApplication
import com.nitiaayog.apnesaathi.base.extensions.setLightStatusBar
import com.nitiaayog.apnesaathi.datamanager.DataManager
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {

    protected val dataManager: DataManager by lazy { ApneSaathiApplication.getApiClient() }

    protected val viewModel: VM by lazy { provideViewModel() }

    protected val disposables: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutResource())
    }

    override fun onDestroy() {
        if ((disposables.size() > 0) && !disposables.isDisposed) {
            disposables.dispose()
            disposables.clear()
        }
        super.onDestroy()
    }

    protected fun setTransparentStatusBar() {
        setLightStatusBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
    }

    abstract fun provideViewModel(): VM

    @LayoutRes
    abstract fun provideLayoutResource(): Int
}