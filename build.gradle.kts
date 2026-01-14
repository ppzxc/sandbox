plugins {
    id("java")
}

group = "io.github.ppzxc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.netty:netty-all:4.2.9.Final")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("org.projectlombok:lombok:1.18.42")
    implementation("org.slf4j:slf4j-api:1.7.36")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}