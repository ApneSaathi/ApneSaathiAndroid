package com.nitiaayog.apnesaathi.base.extensions.rx

import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

private const val TIME_DURATION_SHORT: Long = 300L
private const val TIME_DURATION_MEDIUM: Long = 500L

fun View.throttleClick(): Observable<Unit> = clicks()
    .throttleLatest(TIME_DURATION_MEDIUM, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())

fun AppCompatEditText.onTextChanges(onNext: (text: CharSequence) -> Unit): Disposable =
    textChanges().throttleLatest(
        TIME_DURATION_SHORT, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()
    ).subscribe(onNext)