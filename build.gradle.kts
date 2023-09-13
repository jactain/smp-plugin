import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

val paperVersion = "1.19.2-R0.1-SNAPSHOT"
val hikariVersion = "4.0.3"
val h2Version = "2.1.214"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly(group = "io.papermc.paper", name = "paper-api", version = paperVersion)
    implementation(group = "com.zaxxer", name = "HikariCP", version = hikariVersion)
    implementation(group = "com.h2database", name = "h2", version = h2Version)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.named<DefaultTask>("build") {
    dependsOn("shadowJar")
}

tasks.withType<ShadowJar> {
    exclude("META-INF/**.txt")
    exclude("META-INF/maven/**")
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(17)
}