package com.blocksdecoded.dex.core.zrx

import com.blocksdecoded.dex.core.manager.IEthereumKitManager
import com.blocksdecoded.dex.core.manager.IZrxKitManager

class RelayerAdapterManager(
	private val ethereumKitManager: IEthereumKitManager,
	private val zrxKitManager: IZrxKitManager
): IRelayerAdapterManager {
	override val refreshInterval = 15L
	private var defaultAdapter: IRelayerAdapter? = null

	override fun getMainAdapter(): IRelayerAdapter {
		if (defaultAdapter != null) return defaultAdapter!!

		defaultAdapter = RelayerAdapter(
			ethereumKitManager.defaultKit(),
			zrxKitManager.zrxKit(),
			refreshInterval,
			0
		)

		return defaultAdapter!!
	}
}
