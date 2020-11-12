plugins {
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
}

group = "com.github.viktorpenelski"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation ("com.google.code.gson:gson:2.8.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation("io.socket:socket.io-client:1.0.0")

    implementation("org.xerial:sqlite-jdbc:3.32.3.2")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

// https://gist.github.com/orip/4951642
tasks.withType<AbstractTestTask> {
    afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
        if (desc.parent == null) { // will match the outermost suite
            println("""
                ---
                Result: ${result.resultType}
                  ${result.testCount} tests
                  ${result.successfulTestCount} successful
                  ${result.failedTestCount} failures
                  ${result.skippedTestCount} skipped
                ---
            """.trimIndent())
        }
    }))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}