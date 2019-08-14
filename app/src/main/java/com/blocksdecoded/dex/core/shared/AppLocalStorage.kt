package com.blocksdecoded.dex.core.shared

class AppLocalStorage(
	private val sharedStorage: ISharedStorage
) : IAppLocalStorage {
	override var currentLanguage: String?
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
		set(value) {}
	override var isBackedUp: Boolean
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
		set(value) {}
	override var isBiometricOn: Boolean
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
		set(value) {}
	override var isLightModeOn: Boolean
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
		set(value) {}
	override var iUnderstand: Boolean
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
		set(value) {}
	override var baseCurrencyCode: String?
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
		set(value) {}
	override var blockTillDate: Long?
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
		set(value) {}
	override var failedAttempts: Int?
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
		set(value) {}
	override var lockoutUptime: Long?
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
		set(value) {}
	override var baseEthereumProvider: String?
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
		set(value) {}
	
	override fun clear() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}