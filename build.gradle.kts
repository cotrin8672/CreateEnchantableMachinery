import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.cloche)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.modPublisher)
}

group = "io.github.cotrin8672"
version = "1.0.0"

kotlin.jvmToolchain(17)

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xmulti-platform")
    }
}

repositories {
    cloche.librariesMinecraft()

    mavenCentral()
    mavenLocal()

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
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://maven.terraformersmc.com/")
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
        parchment(libs.versions.parchiment.get())
    }

    common {
        dependencies {
            modCompileOnly("com.simibubi.create:create-1.20.1:${libs.versions.createForge.get()}:slim")
            modCompileOnly(libs.ponder.forge)
            modCompileOnly(libs.flywheel.api.forge)
            modCompileOnly(libs.registrate.forge)

        }
    }

    forge {
        loaderVersion = libs.versions.forge.get()

        metadata {
            modLoader.set("kotlinforforge")
            loaderVersion {
                start = libs.versions.forgeKotlin.get()
            }
        }

        dependencies {
            modImplementation(libs.forge.kotlin)
            modImplementation("com.simibubi.create:create-1.20.1:${libs.versions.createForge.get()}:slim") {
                isTransitive = false
            }
            modImplementation(libs.ponder.forge)
            modCompileOnly(libs.flywheel.api.forge)
            modRuntimeOnly(libs.flywheel.forge)
            modImplementation(libs.registrate.forge)

            implementation(libs.mixinExtra)
        }

        datagenDirectory.set(file("src/commonMain/generated"))

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
            modImplementation(libs.fabric.kotlin)
            modImplementation(libs.create.fabric)
            libs.bundles.porting.lib.get().forEach { modApi(it) }
            modImplementation(libs.ponder.fabric)
            modCompileOnly(libs.flywheel.api.fabric)
            modRuntimeOnly(libs.flywheel.fabric)
            modImplementation(libs.registrate.fabric)
            modImplementation(libs.modmenu)
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
        resources.setSrcDirs(listOf("src/commonMain/resources", "src/commonMain/generated"))
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

tasks.register("printForgeCompileClasspath") {
    doLast {
        val files = configurations.getByName("forgeCompileClasspath").resolve()
        val sorted = files.sortedBy { it.absolutePath }
        println("forgeCompileClasspathCount=${sorted.size}")
        sorted.take(20).forEach { f ->
            println("cpFile=${f.name} -> ${f.absolutePath}")
        }

        val create = sorted.filter { f ->
            val p = f.absolutePath
            p.contains("\\com.simibubi.create\\", ignoreCase = true) ||
                    p.contains("create-1.20.1", ignoreCase = true) ||
                    p.contains("6.0.8-289", ignoreCase = true)
        }
        println("forgeCompileClasspathCreateCount=${create.size}")
        create.take(5).forEach { f ->
            println("cpCreateFile=${f.name} -> ${f.absolutePath}")
        }
    }
}

tasks.register("printCompileForgeKotlinClasspath") {
    doLast {
        val raw = tasks.named("compileForgeKotlin").get()
        println("compileForgeKotlinTaskClass=${raw.javaClass.name}")
        val t = raw as org.jetbrains.kotlin.gradle.tasks.KotlinCompile
        val all = t.libraries.files.sortedBy { it.name }
        println("librariesCount=${all.size}")
        all.take(10).forEach { f -> println("libFile=${f.name}") }

        val create = t.libraries.files
            .sortedBy { it.absolutePath }
            .filter { f ->
                val p = f.absolutePath
                p.contains("\\com.simibubi.create\\", ignoreCase = true) ||
                        p.contains("create-1.20.1", ignoreCase = true) ||
                        p.contains("6.0.8-289", ignoreCase = true)
            }

        println("createLibrariesCount=${create.size}")
        create.take(5).forEach { f -> println("createLibFile=${f.name} -> ${f.absolutePath}") }
    }
}

tasks.register("printForgeCreateArtifacts") {
    doLast {
        val cfg = configurations.getByName("forgeCompileClasspath")
        val artifacts = cfg.resolvedConfiguration.resolvedArtifacts
        val related = artifacts.filter { a ->
            val id = a.moduleVersion.id
            id.group.contains("simibubi", ignoreCase = true) ||
                    id.group.contains("create", ignoreCase = true) ||
                    id.name.contains("create", ignoreCase = true)
        }

        println("forgeRelatedArtifactsCount=${related.size}")
        related.forEach { a ->
            val id = a.moduleVersion.id
            println("forgeRelatedArtifact=${id.group}:${id.name}:${id.version} -> ${a.file.name}")
        }
    }
}

afterEvaluate {
    tasks.named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileForgeKotlin").configure {
        libraries.setFrom(configurations.getByName("forgeCompileClasspath"))
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
