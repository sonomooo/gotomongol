dependencies {
    api(project(":domain"))
    api(project(":user-core"))
    api(project(":infra-persistence"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}
