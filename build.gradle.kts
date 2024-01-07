import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.8"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    id("com.google.cloud.tools.jib") version "3.4.0"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.invictoprojects.streetlyshop"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

extra["springCloudAzureVersion"] = "4.5.0"
extra["springCloudVersion"] = "2021.0.5"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // mongodb
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.11.2")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.2")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.2")

    // random string
    implementation("org.apache.commons:commons-lang3:3.12.0")

    // swagger
    implementation("io.springfox:springfox-boot-starter:3.0.0")

    // azure storage
    implementation("com.azure.spring:spring-cloud-azure-starter-storage")

    // image scale
    implementation("org.imgscalr:imgscalr-lib:4.2")

    // file utils
    implementation("commons-io:commons-io:2.11.0")

    // openfeign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // string substitutor
    implementation("org.apache.commons:commons-text:1.10.0")

    implementation("org.junit.jupiter:junit-jupiter:5.8.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

dependencyManagement {
    imports {
        mavenBom("com.azure.spring:spring-cloud-azure-dependencies:${property("springCloudAzureVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    from {
        image = "eclipse-temurin:11-jre-alpine"
    }
    container {
        mainClass = "com.invictoprojects.streetlyshop.StreetlyShopApiApplicationKt"
    }
}

