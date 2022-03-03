import Com_mineinabyss_conventions_platform_gradle.Deps

val idofrontVersion: String by project
val gearyPlatformVersion: String by project
val deeperworldVersion: String by project
val guiyVersion: String by project

plugins {
	id("com.mineinabyss.conventions.kotlin")
	id("com.mineinabyss.conventions.papermc")
	id("com.mineinabyss.conventions.copyjar")
	id("com.mineinabyss.conventions.publication")
	kotlin("plugin.serialization")
}

repositories {
	mavenCentral()
	maven("https://repo.mineinabyss.com/releases")
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	maven("https://jitpack.io")
}

dependencies {
	// MineInAbyss platform
	compileOnly(Deps.kotlinx.serialization.json)
	compileOnly(Deps.kotlinx.serialization.kaml)
	compileOnly(Deps.minecraft.skedule)

	compileOnly(Deps.`sqlite-jdbc`) { isTransitive = false }
	compileOnly(Deps.exposed.core) { isTransitive = false }
	compileOnly(Deps.exposed.dao) { isTransitive = false }
	compileOnly(Deps.exposed.jdbc) { isTransitive = false }
	compileOnly(Deps.exposed.`java-time`) { isTransitive = false }

	// Geary platform
	compileOnly(platform("com.mineinabyss:geary-platform:$gearyPlatformVersion"))
	compileOnly("com.mineinabyss:geary-papermc-core")
	compileOnly("com.mineinabyss:geary-commons-papermc")
	compileOnly("com.mineinabyss:looty")

	// Other plugins
	compileOnly("com.mineinabyss:deeperworld:$deeperworldVersion")
	compileOnly("com.mineinabyss:guiy-compose:$guiyVersion")

	// Shaded
	implementation("com.mineinabyss:idofront:$idofrontVersion")

}