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
version = "${modVersion}+mc${libs.versions.minecraft.get()}-forge"

base {
    archivesName = modId
}

kotlin {
    jvmToolchain(17)
}

legacyForge {
    version = libs.versions.forge.get()

    parchment {
        mappingsVersion = libs.versions.parchment.get()
        minecraftVersion = libs.versions.minecraft.get()
    }

    runs {
        create("client") {
            client()
            systemProperty("forge.enabledGameTestNamespaces", modId)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("forge.enabledGameTestNamespaces", modId)
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
            gameDirectory.set(project.file("run-$name"))
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

mixin {
    config("${modId}.mixins.json")
    add(sourceSets.main.get(), "${modId}.refmap.json")
}

val localRuntime: Configuration by configurations.creating

configurations {
    configurations.named("runtimeClasspath") {
        extendsFrom(localRuntime)
    }
}

obfuscation {
    createRemappingConfiguration(localRuntime)
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content { includeGroup("thedarkcolour") }
    }
    maven("https://maven.blamejared.com/") // JEI
    maven("https://maven.createmod.net") // Create, Ponder, Flywheel
    maven("https://maven.tterrag.com") // Registrate
    maven("https://maven.ithundxr.dev/mirror")
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") // Forge Config API Port
    maven("https://api.modrinth.com/maven") // Modrinth Maven
    maven("https://maven.theillusivec4.top/") // Curios API
}

dependencies {
    implementation(libs.kotlinforforge)
    modImplementation("com.simibubi.create:create-1.20.1:6.0.7-281:slim") {
        isTransitive = false
    }
    modImplementation(libs.ponder)
    modCompileOnly(libs.flywheel.api)
    modRuntimeOnly(libs.flywheel)
    modImplementation(libs.registrate)

    compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")!!)
    implementation(libs.mixin.forge)

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
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
    displayName.set("$modName $modVersion Forge")
    setGameVersions(libs.versions.minecraft.get())
    setLoaders(ModLoader.FORGE)
    setCurseEnvironment(CurseEnvironment.BOTH)
    artifact.set("build/libs/${base.archivesName.get()}-${project.version}.jar")

    curseDepends {
        required("create", "kotlin-for-forge")
    }
    modrinthDepends {
        required("create", "kotlin-for-forge")
    }
}

tasks.jar {
    manifest.attributes(mapOf("MixinConfigs" to "${modId}.mixins.json"))
}

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val modLicense: String by project
    val modAuthors: String by project
    val modDescription: String by project

    val replaceProperties = mapOf(
        "minecraftVersion" to libs.versions.minecraft.get(),
        "minecraftVersionRage" to "[${libs.versions.minecraft.get()},)",
        "forgeVersion" to libs.versions.forge.get(),
        "forgeVersionRange" to "[47.1.3,)",
        "loaderVersionRange" to "[${libs.versions.kotlinforforge.get()},)",
        "createVersionRange" to "[6.0.7,)",
        "modId" to modId,
        "modName" to modName,
        "modLicense" to modLicense,
        "modVersion" to modVersion,
        "modAuthors" to modAuthors,
        "modDescription" to modDescription,
    )

    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets.main.get().resources.srcDir("src/generated/resources")
sourceSets.main.get().resources.srcDir(generateModMetadata)
legacyForge.ideSyncTask(generateModMetadata)

tasks.named<Wrapper>("wrapper").configure {
    distributionType = Wrapper.DistributionType.BIN
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
