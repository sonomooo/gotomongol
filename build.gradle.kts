plugins {
    id("org.springframework.boot") version "4.0.6" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    kotlin("jvm") version "2.2.20" apply false
    kotlin("plugin.spring") version "2.2.20" apply false
    kotlin("plugin.jpa") version "2.2.20" apply false
}

allprojects {
    group = "com.gotomongol"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencies {
        "implementation"(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")
        "implementation"("com.fasterxml.jackson.module:jackson-module-kotlin")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
        }
    }
}
