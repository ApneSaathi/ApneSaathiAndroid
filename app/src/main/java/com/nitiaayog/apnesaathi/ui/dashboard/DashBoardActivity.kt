package com.nitiaayog.apnesaathi.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.adapter.ViewPagerAdapter
import com.nitiaayog.apnesaathi.base.extensions.getTargetIntent
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import com.nitiaayog.apnesaathi.ui.dashboard.seniorcitizenssupporttoday.SeniorCitizensSupportedToday
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.concurrent.TimeUnit

class DashBoardActivity : BaseActivity<DashBoardViewModel>() {

    companion object {
        private const val FLIP_IMAGE_DELAY: Long = 3500
    }

    private var intervalDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransparentStatusBar()

        initViewPager()
        initClicks()
    }

    override fun onResume() {
        super.onResume()
        intervalDisposable = Observable.interval(FLIP_IMAGE_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                viewPager.currentItem =
                    if (viewPager.currentItem == (viewPager.adapter!!.count - 1)) 0
                    else viewPager.currentItem + 1
            }
    }

    override fun onPause() {
        intervalDisposable?.run {
            if (!this.isDisposed) this.dispose()
        }
        super.onPause()
    }

    override fun provideViewModel(): DashBoardViewModel =
        getViewModel { DashBoardViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.activity_dashboard

    private fun initViewPager() {
        val pageMargin =
            (resources.getDimension(R.dimen.dimen_30) / resources.displayMetrics.density).toInt()
        val padding =
            (resources.getDimension(R.dimen.view_size_62) / resources.displayMetrics.density).toInt()

        viewPager.apply {
            this.clipToPadding = false
            this.setPadding(padding, 0, padding, 0)
            this.pageMargin = pageMargin
            this.offscreenPageLimit = 2
            this.adapter = ViewPagerAdapter()
        }
    }

    private fun initClicks() {
        tvTotalCallsVsConnected.throttleClick().subscribe {
            /*val options = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(
                getTargetIntent(SeniorCitizenFeedbackFormActivity::class.java),
                options.toBundle()
            )*/
            navigateToNextScreen(SeniorCitizensSupportedToday::class.java)
        }.autoDispose(disposables)

        tvTotalSrCitizensSupported.throttleClick().subscribe {

        }.autoDispose(disposables)

        tvCallsConnectedYesterday.throttleClick().subscribe {

        }.autoDispose(disposables)

        tvAverageConnectedCallsPerDay.throttleClick().subscribe {

        }.autoDispose(disposables)

        tvCallsConnectedToday.throttleClick().subscribe {

        }.autoDispose(disposables)

        tvSrCitizensSupportedToday.throttleClick().subscribe {

        }.autoDispose(disposables)

        tvCallsPending.throttleClick().subscribe {

        }.autoDispose(disposables)

        tvComplainsAndResolve.throttleClick().subscribe {

        }.autoDispose(disposables)
    }

    private fun <T : AppCompatActivity> navigateToNextScreen(targetActivity: Class<T>) {
        startActivity(getTargetIntent(targetActivity))
    }
}