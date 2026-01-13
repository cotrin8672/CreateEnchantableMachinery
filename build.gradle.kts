plugins {
    alias(libs.plugins.cloche)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.modPublisher)
}

buildscript {
    val v = "2.23.1"

    configurations.configureEach {
        resolutionStrategy.force(
            "org.apache.logging.log4j:log4j-api:$v",
            "org.apache.logging.log4j:log4j-core:$v"
        )
    }
}

allprojects {
    val v = "2.23.1"

    configurations.configureEach {
        resolutionStrategy.force(
            "org.apache.logging.log4j:log4j-api:$v",
            "org.apache.logging.log4j:log4j-core:$v",
            "org.apache.logging.log4j:log4j-slf4j2-impl:$v"
        )
    }
}

group = "io.github.cotrin8672"
version = "1.0.0"

kotlin.jvmToolchain(17)
java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    cloche.librariesMinecraft()

    mavenCentral()

    cloche {
        main()

        mavenForge()
        mavenFabric()

        mavenParchment()
    }

    maven("https://maven.shedaniel.me/") // Cloth Config, REI
    maven("https://maven.blamejared.com/") // JEI
    maven("https://maven.quiltmc.org/repository/release") // Quilt Mappings
    maven("https://maven.tterrag.com/")
    maven("https://maven.createmod.net/")
    maven("https://mvn.devos.one/snapshots/")
    maven("https://mvn.devos.one/releases/")
    maven("https://maven.jamieswhiteshirt.com/libs-release")
    maven("com.jamieswhiteshirt")
    maven("fuzs.forgeconfigapiport")
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

val modId: String by project
val modName: String by project
val modVersion: String by project
val modAuthors: String by project
val modLicense: String by project
val modDescription: String by project

cloche {
    minecraftVersion = libs.versions.minecraft.get()

    metadata {
        this@metadata.modId = "createenchantablemachinery"
        this@metadata.name = modName
        this@metadata.description = modDescription
        this@metadata.license = modLicense
        author(modAuthors)
    }

    mappings {
        official()
    }

    common {
        dependencies {
//            modImplementation(libs.create.forge) { isTransitive = false }
//            modImplementation(libs.ponder.forge)
//            modCompileOnly(libs.flywheel.api.forge)
//            modRuntimeOnly(libs.flywheel.forge)
//            modImplementation(libs.registrate.forge)
        }
    }

    forge {
        loaderVersion = "47.3.7"

        dependencies {
            modImplementation(libs.forge.kotlin)
//            modImplementation(libs.create.forge) { isTransitive = false }
//            modImplementation(libs.ponder.forge)
//            modCompileOnly(libs.flywheel.api.forge)
//            modRuntimeOnly(libs.flywheel.forge)
//            modImplementation(libs.registrate.forge)
        }

        runs {
            client {
                workingDirectory(file("run/client"))
            }

            server {
                workingDirectory(file("run/server"))
            }
        }
    }

    fabric {
        loaderVersion = libs.versions.fabricLoader.get()
        includedClient()

        dependencies {
            fabricApi(libs.versions.fabricApi.get())
//            modImplementation(libs.create.fabric)
//            libs.bundles.porting.lib.get().forEach { modApi(it) }
//            modImplementation(libs.ponder.fabric)
//            modCompileOnly(libs.flywheel.api.fabric)
//            modRuntimeOnly(libs.flywheel.fabric)
//            modImplementation(libs.registrate.fabric)
        }

        runs {
            client {
                workingDirectory(file("run/client"))
            }

            server {
                workingDirectory(file("run/server"))
            }
        }
    }
}

extensions.configure<SourceSetContainer>("sourceSets") {
    named("main") {
        java.setSrcDirs(listOf("src/commonMain/java"))
        kotlin.setSrcDirs(listOf("src/commonMain/kotlin"))
        resources.setSrcDirs(listOf("src/commonMain/resources"))
    }

    named("forge") {
        java.setSrcDirs(listOf("src/forgeMain/java"))
        kotlin.setSrcDirs(listOf("src/forgeMain/kotlin"))
        resources.setSrcDirs(listOf("src/forgeMain/resources"))
    }

    named("fabric") {
        java.setSrcDirs(listOf("src/fabricMain/java"))
        kotlin.setSrcDirs(listOf("src/fabricMain/kotlin"))
        resources.setSrcDirs(listOf("src/fabricMain/resources"))
    }
}

//publisher {
//    apiKeys {
//        curseforge(System.getenv("CURSE_FORGE_API_KEY"))
//        modrinth(System.getenv("MODRINTH_API_KEY"))
//    }
//
//    curseID.set("1061749")
//    modrinthID.set("eqrvp4NK")
//    versionType.set("release")
//    changelog.set(file("changelog.md"))
//    version.set(project.version.toString())
//    displayName.set("$modName $modVersion NeoForge")
//    setGameVersions(libs.versions.minecraft.get())
//    setLoaders(ModLoader.NEOFORGE)
//    setCurseEnvironment(CurseEnvironment.BOTH)
//    artifact.set("build/libs/${base.archivesName.get()}-${project.version}.jar")
//
//    curseDepends {
//        required("create", "kotlin-for-forge")
//    }
//    modrinthDepends {
//        required("create", "kotlin-for-forge")
//    }
//}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

