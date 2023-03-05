
plugins {
    java
    application
    id("com.palantir.graal") version "0.10.0"
}

group = "io.perf.report"
version = "1.0"

description = "Github Actions Java CMD"

graal {
    mainClass("io.perf.report.PerfReportCli")
    outputName("xperf")

    option("-H:EnableURLProtocols=http,https")
    option("-H:+AddAllCharsets")

    option("--enable-all-security-services")
    option("--allow-incomplete-classpath")
    option("--no-fallback")
    javaVersion("11")
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("info.picocli:picocli-codegen:4.1.4")

    implementation("info.picocli:picocli:4.1.4")
    implementation("net.quux00.simplecsv:simplecsv:2.0")
    implementation("log4j:log4j:1.2.17")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("de.vandermeer:asciitable:0.3.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks {
    test {
        useJUnitPlatform()
    }
}

application {
    mainClass.set("io.perf.report.PerfReportCli")
}

tasks.jar {
    archiveFileName.set("xperf.jar")
}