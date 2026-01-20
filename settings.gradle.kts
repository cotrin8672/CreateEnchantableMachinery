pluginManagement {
    repositories {
        mavenLocal() // Check local first for -local versions
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.quiltmc.org/repository/release")
        maven("https://maven.firstdark.dev/releases")
        maven("https://maven.msrandom.net/repository/cloche/")
        gradlePluginPortal()
        mavenCentral()
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.apache.logging.log4j:log4j-api:2.24.3")
        classpath("org.apache.logging.log4j:log4j-core:2.24.3")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "CreateEnchantableMachinery"
