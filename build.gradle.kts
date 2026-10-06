import com.hypherionmc.modpublisher.properties.CurseEnvironment
import com.hypherionmc.modpublisher.properties.ModLoader
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.loom)
    alias(libs.plugins.modPublisher)
}

val modId: String by project
val modName: String by project
val modVersion: String by project
val modGroupId: String by project

group = modGroupId
version = "${modVersion}+mc${libs.versions.minecraft.get()}-fabric"

base {
    archivesName = modId
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

kotlin {
    jvmToolchain(25)
    compilerOptions.jvmTarget = JvmTarget.JVM_25
}

loom {
    splitEnvironmentSourceSets()
    accessWidenerPath = file("src/main/resources/$modId.classtweaker")

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }

    runs {
        named("server") {
            // Applies every mixin at startup, see Cem.onInitialize
            vmArg("-Dcem.auditMixins=true")
        }
    }
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven")
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)
    implementation(libs.fabric.kotlin)

    // The fabric-api artifact only nests the module jars, so each module used at compile time is named here.
    val fabricApiVersion = libs.versions.fabricApi.get()
    listOf(
        "fabric-api-base",
        "fabric-item-api-v1",
        "fabric-lifecycle-events-v1",
        "fabric-loot-api-v3",
        "fabric-networking-api-v1",
    ).forEach { implementation(fabricApi.module(it, fabricApiVersion)) }
    runtimeOnly(libs.fabric.api)

    // Create Fly embeds Create, Flywheel, Ponder and Catnip.
    implementation(libs.createFly)
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
    displayName.set("$modName $modVersion Fabric")
    setGameVersions(libs.versions.minecraft.get())
    setLoaders(ModLoader.FABRIC)
    setCurseEnvironment(CurseEnvironment.BOTH)
    artifact.set("build/libs/${base.archivesName.get()}-${project.version}.jar")

    curseDepends {
        required("create-fly", "fabric-language-kotlin", "fabric-api")
    }
    modrinthDepends {
        required("create-fly", "fabric-language-kotlin", "fabric-api")
    }
}

tasks.processResources {
    val modLicense: String by project
    val modAuthors: String by project
    val modDescription: String by project

    val replaceProperties = mapOf(
        "minecraftVersion" to libs.versions.minecraft.get(),
        "loaderVersion" to libs.versions.fabricLoader.get(),
        "fabricKotlinVersion" to libs.versions.fabricKotlin.get(),
        "modId" to modId,
        "modName" to modName,
        "modLicense" to modLicense,
        "modVersion" to modVersion,
        "modAuthors" to modAuthors,
        "modDescription" to modDescription,
    )

    inputs.properties(replaceProperties)
    filesMatching("fabric.mod.json") {
        expand(replaceProperties)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 25
}

tasks.named<Wrapper>("wrapper").configure {
    distributionType = Wrapper.DistributionType.BIN
}
