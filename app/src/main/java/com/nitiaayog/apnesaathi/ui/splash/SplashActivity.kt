package com.nitiaayog.apnesaathi.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nitiaayog.apnesaathi.ApneSaathiApplication
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.ui.dashboard.DashBoardActivity
import com.nitiaayog.apnesaathi.ui.fragments.SeniorCitizenDetailsFragment
import com.nitiaayog.apnesaathi.ui.localization.LanguageSelectionActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        disposable = Observable.timer(2000L, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { navigateToNextActivity() }
    }

    override fun onBackPressed() {
    }

    override fun onDestroy() {
        disposable?.run {
            if (!this.isDisposed) this.dispose()
        }
        super.onDestroy()
    }

    private fun navigateToNextActivity() {
        val dataManager = ApneSaathiApplication.getApiClient()
        val targetIntent = getTargetIntent(
            if (dataManager.isLogin()) DashBoardActivity::class.java
            else LanguageSelectionActivity::class.java
        )
        startActivity(targetIntent)
        finish()
    }
}