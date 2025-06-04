pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        flatDir {
            dirs("libs") // This tells Gradle to look for AARs in the 'libs' directory
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "BranchLinkSimulator"
include(":app")
 