package com.nitiaayog.apnesaathi.ui.base

import android.content.Context
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.nitiaayog.apnesaathi.ApneSaathiApplication
import com.nitiaayog.apnesaathi.datamanager.DataManager
import com.nitiaayog.apnesaathi.utility.LanguageUtils
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {

    protected val dataManager: DataManager by lazy { ApneSaathiApplication.getDataClient() }

    protected val viewModel: VM by lazy { provideViewModel() }

    protected val disposables: CompositeDisposable by lazy { CompositeDisposable() }

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

    /*protected fun setTransparentStatusBar() {
        setLightStatusBar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
    }*/

    /**
     * Every fragment/activity will have view model.Using this method we can dynamically create
     * ViewModel instance only for Activity
     * */
    abstract fun provideViewModel(): VM

    /**
     * Ui file from layout folder only to load the user interface of Activity
     **/
    @LayoutRes
    abstract fun provideLayoutResource(): Int

    /**
     * Through this method we will load Ui in selected language
     **/
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        LanguageUtils.changeLanguage(dataManager, applicationContext, resources)
    }
}