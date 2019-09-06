package com.blocksdecoded.dex.core.shared

class AppPreferences(
	private val sharedStorage: ISharedStorage
) : IAppPreferences {

	private val IS_LIGHT_MODE_ENABLED = "is_light_mode_enabled"
	private val IS_FINGERPRINT_ENABLED = "is_fingerprint_enabled"

	override var isBackedUp: Boolean
		get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
		set(value) {}

	override var isFingerprintEnabled: Boolean
		get() = sharedStorage.getPreference(IS_FINGERPRINT_ENABLED, false)
		set(value) { sharedStorage.setPreference(IS_FINGERPRINT_ENABLED, value) }

	override var isLightModeEnabled: Boolean
		get() = sharedStorage.getPreference(IS_LIGHT_MODE_ENABLED, false)
		set(value) { sharedStorage.setPreference(IS_LIGHT_MODE_ENABLED, value) }

	override var iUnderstand: Boolean
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
		sharedStorage.clear()
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}