plugins {
    alias(libs.plugins.architectury)
    alias(libs.plugins.loom)
    alias(libs.plugins.kotlin)
    java
}

val modId: String by project
val modVersion: String by project

base {
    archivesName = modId
    version = "mc${libs.versions.minecraft.get()}-${modVersion}-${project.name}"
}

kotlin.jvmToolchain(17)
java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

architectury {
    common("forge", "fabric")
}

loom {
    silentMojangMappingsLicense()
}

sourceSets.main {
    resources {
        srcDir("src/generated/resources")
    }
}

repositories {
    maven("https://api.modrinth.com/maven") // LazyDFU
    maven("https://maven.terraformersmc.com/releases/") // Mod Menu
    maven("https://mvn.devos.one/snapshots/") // Create Fabric, Porting Lib, Forge Tags, Milk Lib, Registrate Fabric
    maven("https://mvn.devos.one/releases/") // Porting Lib Releases
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") // Forge Config API Port
    maven("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
    maven("https://jitpack.io/") // Mixin Extras, Fabric ASM
    maven("https://maven.parchmentmc.org")
    mavenCentral()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        mappings("org.quiltmc:quilt-mappings:1.18.2+build.26:intermediary-v2")
        officialMojangMappings { nameSyntheticMembers = false }
    })
    modImplementation(libs.fabric.loader)
    modCompileOnly(libs.fabric.api)
    modCompileOnly(libs.create.fabric)
    compileOnly(libs.koin)
}