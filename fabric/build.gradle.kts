import com.hypherionmc.modpublisher.properties.CurseEnvironment

plugins {
    alias(libs.plugins.architectury)
    alias(libs.plugins.loom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.modPublisher)
    java
}

val modId: String by project
val modVersion: String by project
val modName: String by project

base {
    archivesName = modId
    version = "${modVersion}-mc${libs.versions.minecraft.get()}-${project.name}"
}

kotlin.jvmToolchain(17)
java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

architectury {
    platformSetupLoomIde()
    fabric()
}

fabricApi {
    configureDataGeneration()
}

loom {
    mixin {
        defaultRefmapName.set("${modId}.refmap.json")
    }
    silentMojangMappingsLicense()

    runs {
        getByName("datagen") {
            client()

            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${project(":common").file("src/generated/resources")}")
            vmArg("-Dporting_lib.datagen.existing_resources=${project(":common").file("src/main/resources")}")
            vmArg("-Dporting_lib.datagen.existing-mod=create")

            environmentVariable("DATAGEN", "TRUE")
        }
    }
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val compileClasspath: Configuration by configurations.getting
val runtimeClasspath: Configuration by configurations.getting
val developmentFabric: Configuration by configurations.getting

configurations {
    common
    shadowCommon
    compileClasspath.configure { extendsFrom(common) }
    runtimeClasspath.configure { extendsFrom(common) }
    developmentFabric.extendsFrom(common)
}

repositories {
    mavenCentral() // Mixin Extras, Fabric ASM
    maven("https://api.modrinth.com/maven") // LazyDFU
    maven("https://maven.terraformersmc.com/releases/") // Mod Menu
    maven("https://mvn.devos.one/snapshots/")
    maven("https://mvn.devos.one/releases/") // Porting Lib Releases
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") // Forge Config API Port
    maven("https://maven.jamieswhiteshirt.com/libs-release")// Reach Entity Attributes
    maven("https://jitpack.io/")
    maven("https://maven.parchmentmc.org")
    maven {
        url = uri("https://cursemaven.com")
        content { includeGroup("curse.maven") }
    }
}

configurations.configureEach {
    resolutionStrategy {
        force(libs.fabric.loader)
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.kotlin)
    modImplementation(libs.create.fabric)

    modLocalRuntime(libs.modmenu)
    // modCompileOnly(libs.majrusz.library.fabric)

    implementation(libs.koin)
    include(libs.koin)

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionFabric")) { isTransitive = false }
}

publisher {
    apiKeys {
        curseforge(System.getenv("CURSE_FORGE_API_KEY"))
        modrinth(System.getenv("MODRINTH_API_KEY"))
    }
    curseID.set("1126577")
    modrinthID.set("eqrvp4NK")
    versionType.set("release")
    changelog.set(file("../changelog.md"))
    version.set(project.version.toString())
    displayName.set("$modName ${project.name.replaceFirstChar { it.uppercase() }} ${libs.versions.minecraft.get()}-${modVersion}")
    setGameVersions(libs.versions.minecraft.get())
    setLoaders(project.name, "quilt")
    setCurseEnvironment(CurseEnvironment.BOTH)
    artifact.set("build/libs/${base.archivesName.get()}-${project.version}.jar")

    curseDepends {
        required("create-fabric", "fabric-language-kotlin")
    }
    modrinthDepends {
        required("create-fabric", "fabric-language-kotlin")
    }
}

tasks.processResources {
    val modId: String by project
    val modName: String by project
    val modLicense: String by project
    val modVersion: String by project
    val modAuthors: String by project
    val modDescription: String by project

    val replaceProperties = mapOf(
        "minecraftVersion" to libs.versions.minecraft.get(),
        "createFabricVersion" to libs.versions.createFabric.get(),
        "fabricLoaderVersion" to libs.versions.fabricLoader.get(),
        "fabricApiVersion" to libs.versions.fabricApi.get(),
        "modId" to modId,
        "modName" to modName,
        "modLicense" to modLicense,
        "modVersion" to modVersion,
        "modAuthors" to modAuthors,
        "modDescription" to modDescription
    )
    inputs.properties(replaceProperties)
    filesMatching(listOf("fabric.mod.json")) {
        expand(replaceProperties)
    }

    val commonResourcesDir = project(":common").layout.projectDirectory.dir("src/main/resources")
    from(commonResourcesDir) {
        include("logo.png")
    }
    into("src/main/resources")
}

tasks.shadowJar {
    configurations = listOf(shadowCommon)
    archiveClassifier = "dev-shadow"
}

tasks.remapJar {
    input.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveClassifier = null
}

tasks.jar {
    archiveClassifier = "dev"
}

tasks.named<Jar>("sourcesJar") {
    val commonSources = project(":common").tasks.named<Jar>("sourcesJar").get()
    dependsOn(commonSources)
    from(commonSources.archiveFile.map(::zipTree))
}

components.getByName("java") {
    this as AdhocComponentWithVariants
    this.withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
        skip()
    }
}
