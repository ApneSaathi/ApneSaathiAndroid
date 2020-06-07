package com.nitiaayog.apnesaathi.base.extensions.rx

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

private const val DELAY_SUBSCRIPTION_MILLIS: Long = 700L

fun Disposable.autoDispose(compositeDisposable: CompositeDisposable) = compositeDisposable.add(this)

// Observable Pattern
fun <T> Observable<T>.subscribeAndObserve(): Observable<T> = subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.subscribeAndObserve(
    onNext: (t: T) -> Unit, onError: (Throwable) -> Unit, onComplete: () -> Unit
): Disposable =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(onNext, onError, onComplete)

fun <T> Observable<T>.subscribeAndObserveWithDelaySubscription(): Observable<T> =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .delaySubscription(DELAY_SUBSCRIPTION_MILLIS, TimeUnit.MILLISECONDS)

fun <T> Observable<T>.subscribeAndObserveWithDelaySubscription(
    onNext: (t: T) -> Unit, onError: (Throwable) -> Unit, onComplete: () -> Unit
): Disposable =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .delaySubscription(DELAY_SUBSCRIPTION_MILLIS, TimeUnit.MILLISECONDS)
        .subscribe(onNext, onError, onComplete)

// Single Observable Pattern
fun <T> Single<T>.subscribeAndObserve(): Single<T> = subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.subscribeAndObserve(onSuccess: (t: T) -> Unit, onError: (Throwable) -> Unit):
        Disposable =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(onSuccess, onError)

fun <T> Single<T>.subscribeAndObserveWithDelaySubscription(): Single<T> =
    subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .delaySubscription(DELAY_SUBSCRIPTION_MILLIS, TimeUnit.MILLISECONDS)

fun <T> Single<T>.subscribeAndObserveWithDelaySubscription(
    onSuccess: (t: T) -> Unit, onError: (Throwable) -> Unit
): Disposable =
    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .delaySubscription(DELAY_SUBSCRIPTION_MILLIS, TimeUnit.MILLISECONDS)
        .subscribe(onSuccess, onError)