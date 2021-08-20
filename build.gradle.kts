val exposedVersion: String by project

plugins {
	id("com.mineinabyss.conventions.kotlin")
	kotlin("plugin.serialization")
	id("com.mineinabyss.conventions.papermc")
	id("com.mineinabyss.conventions.publication")
}

repositories {
	mavenCentral()
	maven("https://repo.mineinabyss.com/releases")
	maven("https://jitpack.io")
}

dependencies {
	// Kotlin spice dependencies
	compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json")
	compileOnly("com.charleskorn.kaml:kaml")

	// Database
	slim("org.jetbrains.exposed:exposed-core:$exposedVersion") { isTransitive = false }
	slim("org.jetbrains.exposed:exposed-dao:$exposedVersion") { isTransitive = false }
	slim("org.jetbrains.exposed:exposed-jdbc:$exposedVersion") { isTransitive = false }
	slim("org.jetbrains.exposed:exposed-java-time:$exposedVersion") { isTransitive = false }
	// Sqlite
	slim("org.xerial:sqlite-jdbc:3.30.1")

	// Kotlin JVM
	slim(kotlin("stdlib-jdk8"))

	// Shaded
	implementation("com.mineinabyss:idofront:1.17.1-0.6.23")

}