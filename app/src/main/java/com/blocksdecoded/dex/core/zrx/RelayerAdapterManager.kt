package com.blocksdecoded.dex.core.zrx

import android.os.Handler
import android.os.HandlerThread
import com.blocksdecoded.dex.core.manager.AuthManager
import com.blocksdecoded.dex.core.manager.ICoinManager
import com.blocksdecoded.dex.core.manager.IEthereumKitManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class RelayerAdapterManager(
	private val coinManager: ICoinManager,
    private val ethereumKitManager: IEthereumKitManager,
    private val zrxKitManager: IZrxKitManager,
    private val authManager: AuthManager
): IRelayerAdapterManager, HandlerThread("R") {

	private val handler: Handler
	private val disposables = CompositeDisposable()

	override val refreshInterval = 15L

	override var mainRelayer: IRelayerAdapter? = null
	override val mainRelayerUpdatedSignal: BehaviorSubject<Unit> = BehaviorSubject.create()

	init {
		start()

		handler = Handler(looper)

		disposables.add(authManager.authDataSignal
			.subscribeOn(Schedulers.io())
			.observeOn(Schedulers.io())
			.subscribe { initRelayer() })
	}

	override fun refresh() {

	}

	override fun initRelayer() {
		handler.post {
			authManager.authData?.let { auth ->
				mainRelayer = RelayerAdapter(
					coinManager,
					ethereumKitManager.ethereumKit(auth),
					zrxKitManager.zrxKit(),
					refreshInterval,
					0
				)

				mainRelayerUpdatedSignal.onNext(Unit)
			}
		}
	}
}
