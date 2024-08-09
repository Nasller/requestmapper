val env: MutableMap<String, String> = System.getenv()
val dir: String = projectDir.parentFile.absolutePath
fun properties(key: String) = providers.gradleProperty(key)

plugins {
    // Java support
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.gradleIntelliJPlugin)
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

repositories {
    mavenCentral()
    // IntelliJ Platform Gradle Plugin Repositories Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-repositories-extension.html
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        create(properties("platformType"), properties("platformVersion"))

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(properties("platformBundledPlugins").map { it.split(',') })
        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(properties("platformPlugins").map { it.split(',') })

        instrumentationTools()
        zipSigner()
    }
}

intellijPlatform {
    pluginConfiguration {
        name = properties("pluginName").get()
        version = project.version.toString()

        ideaVersion {
            sinceBuild = properties("pluginSinceBuild").get()
        }
    }
    sandboxContainer = layout.projectDirectory.dir("idea-sandbox")
    buildSearchableOptions = false

    publishing {
        token.set(env["PUBLISH_TOKEN"])
        channels.set(listOf(env["PUBLISH_CHANNEL"] ?: "default"))
    }

    signing {
        certificateChainFile.set(File(env.getOrDefault("CERTIFICATE_CHAIN", "$dir/pluginCert/chain.crt")))
        privateKeyFile.set(File(env.getOrDefault("PRIVATE_KEY", "$dir/pluginCert/private.pem")))
        password.set(File(env.getOrDefault("PRIVATE_KEY_PASSWORD", "$dir/pluginCert/password.txt")).readText(Charsets.UTF_8))
    }
}

kotlin {
    jvmToolchain(properties("javaVersion").get().toInt())
}

tasks {
    runIde {
        systemProperties["idea.is.internal"] = true
    }

    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }
}