import com.hypherionmc.modpublisher.properties.CurseEnvironment
import com.hypherionmc.modpublisher.properties.ModLoader

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.modDevGradle)
    alias(libs.plugins.modPublisher)
}

val modId: String by project
val modName: String by project
val modVersion: String by project
val modGroupId: String by project

group = modGroupId
version = "${modVersion}+mc${libs.versions.minecraft.get()}-neoforge"

base {
    archivesName = modId
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
    maven("https://maven.blamejared.com/") // JEI
    maven("https://maven.createmod.net") // Create, Ponder, Flywheel
    maven("https://mvn.devos.one/snapshots") // Registrate
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") // Forge Config API Port
    maven("https://api.modrinth.com/maven") // Modrinth Maven
    maven("https://maven.theillusivec4.top/") // Curios API
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

    runtimeOnly("top.theillusivec4.curios:curios-neoforge:9.2.2+1.21.1")
    compileOnly("top.theillusivec4.curios:curios-neoforge:9.2.2+1.21.1:api")
}

publisher {
    apiKeys {
        curseforge(System.getenv("CURSE_FORGE_API_KEY"))
        modrinth(System.getenv("MODRINTH_API_KEY"))
    }

    curseID.set("1061749")
    modrinthID.set("eqrvp4NK")
    versionType.set("release")
    changelog.set(file("changelog.md"))
    version.set(project.version.toString())
    displayName.set("$modName $modVersion")
    setGameVersions(libs.versions.minecraft.get())
    setLoaders(ModLoader.NEOFORGE)
    setCurseEnvironment(CurseEnvironment.BOTH)
    artifact.set("build/libs/${base.archivesName.get()}-${project.version}.jar")

    curseDepends {
        required("create", "kotlin-for-forge")
    }
    modrinthDepends {
        required("create", "kotlin-for-forge")
    }
}

val generateModMetadata = tasks.withType<ProcessResources>().configureEach {
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
