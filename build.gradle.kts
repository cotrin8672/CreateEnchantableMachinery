plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.modDevGradle)
}

val modId: String by project
val modVersion: String by project
val modGroupId: String by project

group = modGroupId

base {
    archivesName = modId
    version = "${modVersion}-mc${libs.versions.minecraft.get()}"
}

kotlin {
    jvmToolchain(21)
}

neoForge {
    version = libs.versions.neoforge.get()

    parchment {
        mappingsVersion = libs.versions.parchiment.get()
        minecraftVersion = libs.versions.minecraft.get()
    }

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        create("data") {
            data()

            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

configurations {
    val localRuntime by configurations.creating

    configurations.named("runtimeClasspath") {
        extendsFrom(localRuntime)
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content { includeGroup("thedarkcolour") }
    }
    maven {
        url = uri("https://jm.gserv.me/repository/maven-public") // JourneyMap
        content {
            includeGroup("info.journeymap")
            includeGroup("mysticdrew")
        }
    }
    maven("https://maven.blamejared.com/") // JEI
    maven("https://maven.createmod.net/") // Ponder, Flywheel
    maven("https://mvn.devos.one/snapshots") // Registrate
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") // Forge Config API Port
    maven("https://api.modrinth.com/maven") // Modrinth Maven
}

dependencies {
    implementation(libs.kotlinforforge)
    implementation(libs.create) {
        isTransitive = false
    }
    implementation(libs.ponder)
    compileOnly(libs.flywheel.api)
    runtimeOnly(libs.flywheel)
    implementation(libs.registrate)
}

val generateModMetadata = tasks.withType<ProcessResources>().configureEach {
    val modName: String by project
    val modLicense: String by project
    val modAuthors: String by project
    val modDescription: String by project

    val replaceProperties = mapOf(
        "minecraftVersion" to libs.versions.minecraft.get(),
        "minecraftVersionRage" to "[${libs.versions.minecraft.get()},)",
        "neoforgeVersion" to libs.versions.neoforge.get(),
        "neoforgeVersionRange" to "[21.1.0,)",
        "loaderVersionRange" to "[${libs.versions.kotlinforforge.get()},)",
        "createVersionRange" to "[6.0.0,)",
        "modId" to modId,
        "modName" to modName,
        "modLicense" to modLicense,
        "modVersion" to modVersion,
        "modAuthors" to modAuthors,
        "modDescription" to modDescription,
    )

    inputs.properties(replaceProperties)
    filesMatching(listOf("META-INF/neoforge.mods.toml")) {
        expand(replaceProperties)
    }
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets.main.get().resources.srcDir("src/generated/resources")
neoForge.ideSyncTask(tasks.processResources)

tasks.named<Wrapper>("wrapper").configure {
    distributionType = Wrapper.DistributionType.BIN
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
