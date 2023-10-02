rootProject.name = "eternalfortune"

pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://repo.mineinabyss.com/releases")
		maven("https://repo.papermc.io/repository/maven-public/")
		mavenLocal()
	}

	val idofrontVersion: String by settings
	resolutionStrategy {
		eachPlugin {
			if (requested.id.id.startsWith("com.mineinabyss.conventions"))
				useVersion(idofrontVersion)
		}
	}
}

dependencyResolutionManagement {
	val idofrontVersion: String by settings

	repositories {
		maven("https://repo.mineinabyss.com/releases")
		maven("https://repo.mineinabyss.com/snapshots")
		mavenLocal()
	}

	versionCatalogs {
		create("libs").from("com.mineinabyss:catalog:$idofrontVersion")
		create("eLibs").from(files("gradle/eLibs.versions.toml"))
	}
}

val pluginName: String by settings
