import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

plugins {
    idea
    alias(libs.plugins.kotlin)
}

val pluginName: String by extra(rootProject.name.split("-").joinToString("") { it.replaceFirstChar { char -> char.uppercase() } })
val javaTarget = 17

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaTarget))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper)
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
}

extra.apply {
    val pluginName = rootProject.name.split("-").joinToString("") { it.replaceFirstChar { char -> char.uppercase() } }

    set("pluginName", pluginName)
    set("packageName", rootProject.name.replace("-", ""))
    set("kotlinVersion", libs.versions.kotlin.get())
    set("paperVersion", libs.versions.paper.get().split(".").take(2).joinToString(separator = ".").replace("-R0", "")) //.replace("-R0", "") << 1.x.x가 아닌 1.x 버전인 경우, R0이 포함될 수 있음.
    set("pluginLibraries", "")

    val pluginLibraries = LinkedHashSet<String>()

    configurations.findByName("implementation")?.allDependencies?.forEach { dependency ->
        val group = dependency.group ?: error("group is null")
        var name = dependency.name ?: error("name is null")
        var version = dependency.version

        if (dependency !is ProjectDependency) {
            if (group == "org.jetbrains.kotlin" && version == null) {
                version = getKotlinPluginVersion()
            }

            requireNotNull(version) { "version is null" }
            require(version != "latest.release") { "version is latest.release" }

            pluginLibraries += "$group:$name:$version"
            set("pluginLibraries", pluginLibraries.joinToString("\n  ") { "- '$it'" })
        }
    }
}

tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    register<Jar>("devJar") {
        archiveBaseName.set(pluginName)
        archiveClassifier.set("dev")
        from(sourceSets.main.get().output)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }.also { jar ->
        register<Copy>("testDevJar") {
            val pluginsDir = rootProject.file(".server/plugins-dev")
            from(jar)
            into(pluginsDir)
        }
    }
}

idea {
    module {
        excludeDirs.add(file(".server"))
        excludeDirs.addAll(allprojects.map { it.buildDir })
        excludeDirs.addAll(allprojects.map { it.file(".gradle") })
    }
}
