package com.blocksdecoded.dex.core.zrx

import com.blocksdecoded.dex.core.UnauthorizedException
import com.blocksdecoded.dex.core.manager.AuthManager
import com.blocksdecoded.dex.core.manager.IEthereumKitManager
import com.blocksdecoded.dex.core.zrx.kit.IZrxKitManager

class RelayerAdapterManager(
	private val ethereumKitManager: IEthereumKitManager,
	private val zrxKitManager: IZrxKitManager,
	private val authManager: AuthManager
): IRelayerAdapterManager {
	override val refreshInterval = 15L
	private var defaultAdapter: IRelayerAdapter? = null

	override fun getMainAdapter(): IRelayerAdapter {
		if (defaultAdapter != null) return defaultAdapter!!
		
		authManager.authData?.let { auth ->
			defaultAdapter = RelayerAdapter(
				ethereumKitManager.ethereumKit(auth),
				zrxKitManager.zrxKit(),
				refreshInterval,
				0
			)
			
			return defaultAdapter!!
		}
		
		throw UnauthorizedException()
	}
}
