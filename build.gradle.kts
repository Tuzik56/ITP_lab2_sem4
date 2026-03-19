import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("java")
    application
    id("com.gradleup.shadow") version "8.3.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("org.example.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(project(":app"))
    implementation(project(":string-utils"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.withType<JavaExec> {
    jvmArgs("-Dfile.encoding=UTF-8")
}

tasks.shadowJar {
    manifest {
        attributes(Pair("Main-Class", "org.example.Main"))
    }
}

abstract class PrintInfoTask : DefaultTask() {
    @TaskAction
    fun print() {
        println("======================================")
        println("Это моя первая пользовательская задача!")
        println("Проект: ${project.name}")
        println("Версия Gradle: ${project.gradle.gradleVersion}")
        println("======================================")
    }
}

tasks.register<PrintInfoTask>("printInfo") {
    group = "Custom"
    description = "Выводит информацию о проекте"
}

tasks.register("generateBuildPassport") {

    val outputDir = file("$projectDir/app/src/main/resources")
    val outputFile = file("$outputDir/build-passport.properties")

    doLast {

        val username = System.getenv("USERNAME")
            ?: System.getenv("USER")
            ?: "unknown"

        val os = System.getProperty("os.name")
        val javaVersion = System.getProperty("java.version")

        val date = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val content = """
            user=$username
            os=$os
            javaVersion=$javaVersion
            buildTime=$date
            message=Hello from Gradle build!
        """.trimIndent()

        outputDir.mkdirs()
        outputFile.writeText(content)

        println("Файл build-passport.properties создан")
    }
}

tasks.named("processResources") {
    dependsOn("generateBuildPassport")
}
