import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

plugins {
    application
    java
    id("com.gradleup.shadow") version "8.3.0"
}

application {
    mainClass.set("org.example.Main")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.31")

    implementation(project(":string-utils"))
}

abstract class GenerateBuildInfoTask : DefaultTask() {

    @Input
    val gitCommitHash: Property<String> = project.objects.property(String::class.java)

    @TaskAction
    fun generate() {

        val outputFile = project.file("src/main/resources/build-passport.properties")

        val username = System.getenv("USERNAME")
            ?: System.getenv("USER")
            ?: "unknown"

        val os = System.getProperty("os.name")
        val javaVersion = System.getProperty("java.version")

        val date = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        var buildNumber = 1

        // читаем старый buildNumber (если файл уже есть)
        if (outputFile.exists()) {
            val props = Properties()
            props.load(outputFile.inputStream())
            buildNumber = (props.getProperty("buildNumber")?.toIntOrNull() ?: 0) + 1
        }

        val content = """
            user=$username
            os=$os
            javaVersion=$javaVersion
            buildTime=$date
            gitCommit=${gitCommitHash.get()}
            buildNumber=$buildNumber
            message=Hello from Gradle build!
        """.trimIndent()

        outputFile.parentFile.mkdirs()
        outputFile.writeText(content)

        println("build-passport.properties обновлён (buildNumber=$buildNumber)")
    }
}

tasks.register<GenerateBuildInfoTask>("generateBuildInfo") {

    group = "Custom"
    description = "Генерирует build-passport.properties"

    gitCommitHash.set(
        providers.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText.map { it.trim() }
    )
}

tasks.named("processResources") {
    dependsOn("generateBuildInfo")
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
