@Suppress("DSL_SCOPE_VIOLATION")
plugins {
	alias(libs.plugins.mia.kotlin.jvm)
	alias(libs.plugins.kotlinx.serialization)
	alias(libs.plugins.mia.papermc)
	alias(libs.plugins.mia.copyjar)
	alias(libs.plugins.mia.nms)
	alias(libs.plugins.mia.publication)
	alias(libs.plugins.mia.autoversion)
}

repositories {
	mavenCentral()
	maven("https://repo.mineinabyss.com/releases")
	maven("https://repo.mineinabyss.com/snapshots")
	maven("https://repo.dmulloy2.net/repository/public") // ProtocolLib
	maven("https://jitpack.io")
	maven("https://repo.jeff-media.com/public") // PersistentDataSerializer
	mavenLocal()
}

dependencies {
	// MineInAbyss platform
	compileOnly(libs.kotlinx.serialization.json)
	compileOnly(libs.kotlinx.serialization.kaml)
	compileOnly(libs.kotlinx.coroutines)
	compileOnly(libs.minecraft.mccoroutine)

	compileOnly(eLibs.geary.papermc)
	compileOnly(eLibs.blocky)
	compileOnly(libs.minecraft.plugin.modelengine)
	compileOnly(libs.minecraft.plugin.protocollib)

	implementation(libs.bundles.idofront.core)
	implementation(libs.idofront.nms)
}
