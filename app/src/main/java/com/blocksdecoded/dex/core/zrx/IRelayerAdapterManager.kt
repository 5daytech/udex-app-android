package com.blocksdecoded.dex.core.zrx

interface IRelayerAdapterManager {
	val refreshInterval: Long
	fun getMainAdapter(): IRelayerAdapter
}