import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

tasks.register("generateBuildPassport") {

    val outputDir = file("$projectDir/src/main/resources")
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

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
