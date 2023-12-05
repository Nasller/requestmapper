import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val env: MutableMap<String, String> = System.getenv()
val dir: String = projectDir.parentFile.absolutePath
fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.gradleIntelliJPlugin)
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    sandboxDir.set("${rootProject.rootDir}/idea-sandbox")
    downloadSources.set(true)
    updateSinceUntilBuild.set(false)
    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

kotlin {
    jvmToolchain(properties("javaVersion").toInt())
}

tasks {
    runIde {
        systemProperties["idea.is.internal"] = true
    }

    // Set the JVM compatibility versions
    properties("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = it
        }
    }

    buildSearchableOptions {
        enabled = false
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
    }

    publishPlugin {
        token.set(env["PUBLISH_TOKEN"])
        channels.set(listOf(env["PUBLISH_CHANNEL"] ?: "default"))
    }

    signPlugin {
        certificateChainFile.set(File(env.getOrDefault("CERTIFICATE_CHAIN", "$dir/pluginCert/chain.crt")))
        privateKeyFile.set(File(env.getOrDefault("PRIVATE_KEY", "$dir/pluginCert/private.pem")))
        password.set(File(env.getOrDefault("PRIVATE_KEY_PASSWORD", "$dir/pluginCert/password.txt")).readText(Charsets.UTF_8))
    }
}