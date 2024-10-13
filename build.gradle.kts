plugins {
    alias(libs.plugins.architectury)
    alias(libs.plugins.loom) apply false
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.kotlin) apply false
}

architectury {
    minecraft = libs.versions.minecraft.get()
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://maven.shedaniel.me/") // Cloth Config, REI
        maven("https://maven.blamejared.com/") // JEI
        maven("https://maven.quiltmc.org/repository/release") // Quilt Mappings
        maven("https://maven.tterrag.com/")
    }
}

allprojects {
    group = "io.github.cotrin8672"

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
