plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Selenium
    implementation("org.seleniumhq.selenium:selenium-java:4.27.0")

    // AssertJ for richer assertions in POM tests
    testImplementation("org.assertj:assertj-core:3.26.3")

    // Apache Commons Lang for random data generation
    implementation("org.apache.commons:commons-lang3:3.17.0")
}

tasks.test {
    useJUnitPlatform()

    // Pass admin credentials from -D system properties through to the JVM
    // (used by the admin tests if/when they are re-enabled).
    systemProperty("admin.email", System.getProperty("admin.email", ""))
    systemProperty("admin.password", System.getProperty("admin.password", ""))

    // Generate HTML test reports
    reports {
        html.required.set(true)
        junitXml.required.set(true)
    }

    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<JavaExec> {
    jvmArgs = listOf("-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8")
}
