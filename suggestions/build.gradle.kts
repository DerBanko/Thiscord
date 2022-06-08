plugins {
    id("java")
    application
}

group = "tv.banko"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.12")
    implementation("org.mongodb:mongodb-driver-reactivestreams:4.6.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")
    implementation("org.slf4j:slf4j-simple:1.7.36")
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.7")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<Jar> {
    val classpath = configurations.runtimeClasspath

    inputs.files(classpath).withNormalizer(ClasspathNormalizer::class.java)

    manifest {
        attributes["Main-Class"] = "tv.banko.suggestions.Main"

        attributes(
            "Class-Path" to classpath.map { cp -> cp.joinToString(" ") { "./lib/" + it.name } }
        )
    }
}

application {
    mainClass.set("tv.banko.suggestions.Main")
}