package com.blocksdecoded.dex.core.zrx

import com.blocksdecoded.dex.core.manager.IEthereumKitManager
import com.blocksdecoded.dex.core.manager.IZrxKitManager

class RelayerAdapterManager(
	private val ethereumKitManager: IEthereumKitManager,
	private val zrxKitManager: IZrxKitManager
): IRelayerAdapterManager {
	override val refreshInterval = 10L
	
	override var mainAdapter: RelayerAdapter? = null
		get() {
			if (field == null) {
				field = RelayerAdapter(
					ethereumKitManager.defaultKit(),
					zrxKitManager.zrxKit(),
					refreshInterval,
					0
				)
			}
			
			return field
		}
}
