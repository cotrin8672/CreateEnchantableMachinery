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
    version = "${project.name}-${modVersion}-${libs.versions.minecraft.get()}"
}

kotlin.jvmToolchain(17)
java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    forge {
        mixin {
            mixinConfig("${modId}.mixins.json")
            mixinConfig("${modId}-common.mixins.json")
            defaultRefmapName.set("${modId}.refmap.json")
        }
    }
    silentMojangMappingsLicense()
    runs {
        create("data") {
            val generatedResources = file("../common/src/generated/resources")
            data()
            programArgs("--all", "--mod", modId)
            programArgs("--output", generatedResources.absolutePath)
        }
    }
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val compileClasspath: Configuration by configurations.getting
val runtimeClasspath: Configuration by configurations.getting
val developmentForge: Configuration by configurations.getting

configurations {
    common
    shadowCommon
    compileClasspath.configure { extendsFrom(common) }
    runtimeClasspath.configure { extendsFrom(common) }
    developmentForge.extendsFrom(common)
}

repositories {
    mavenCentral()
    maven("https://maven.blamejared.com")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.theillusivec4.top/")
    maven("https://maven.squiddev.cc")
    maven("https://maven.tterrag.com/")
    maven("https://maven.parchmentmc.org")
    maven {
        url = uri("https://cursemaven.com")
        content { includeGroup("curse.maven") }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        mappings("org.quiltmc:quilt-mappings:1.18.2+build.26:intermediary-v2")
        officialMojangMappings { nameSyntheticMembers = false }
    })

    forge(libs.forge)
    implementation(libs.forge.kotlin)

    modImplementation(libs.create.forge) {
        isTransitive = false
        artifact { classifier = "slim" }
    }
    modImplementation(libs.registrate)
    modImplementation(libs.flywheel)

    // modCompileOnly(libs.majrusz.library.forge)

    implementation(libs.koin)
    kotlinForgeRuntimeLibrary(libs.koin)
    include(libs.koin)

    common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
    shadowCommon(project(path = ":common", configuration = "transformProductionForge")) { isTransitive = false }
}

publisher {
    apiKeys {
        curseforge(System.getenv("CURSE_FORGE_API_KEY"))
        modrinth(System.getenv("MODRINTH_API_KEY"))
    }
    curseID.set("1061749")
    modrinthID.set("eqrvp4NK")
    versionType.set("release")
    changelog.set(file("../changelog.md"))
    version.set(modVersion)
    displayName.set("$modName ${project.name.replaceFirstChar { it.uppercase() }} ${libs.versions.minecraft.get()}-${modVersion}")
    setGameVersions(libs.versions.minecraft.get())
    setLoaders(project.name)
    setCurseEnvironment(CurseEnvironment.BOTH)
    artifact.set("build/libs/${base.archivesName}-${project.version}.jar")

    curseDepends {
        required("create", "kotlin-for-forge")
    }
    modrinthDepends {
        required("create", "kotlin-for-forge")
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
        "forgeVersion" to libs.versions.forge.get(),
        "forgeLoaderVersion" to libs.versions.forgeKotlin.get(),
        "createForgeVersion" to libs.versions.createForge.get(),
        "modId" to modId,
        "modName" to modName,
        "modLicense" to modLicense,
        "modVersion" to modVersion,
        "modAuthors" to modAuthors,
        "modDescription" to modDescription,
    )
    inputs.properties(replaceProperties)
    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(replaceProperties)
    }

    val commonResourcesDir = project(":common").layout.projectDirectory.dir("src/main/resources")
    from(commonResourcesDir) {
        include("logo.png")
    }
    into("src/main/resources")
}

tasks.shadowJar {
    exclude("fabric.mod.json")

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

val artifactType = Attribute.of("artifactType", String::class.java)

dependencies {
    attributesSchema {
        attribute(patchedFMLModType)
    }

    artifactTypes.getByName("jar") {
        attributes.attribute(patchedFMLModType, false)
    }

    registerTransform(PatchFMLModType::class) {
        from.attribute(patchedFMLModType, false).attribute(artifactType, "jar")
        to.attribute(patchedFMLModType, true).attribute(artifactType, "jar")
    }
}
