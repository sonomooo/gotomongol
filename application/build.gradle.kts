dependencies {
    api(project(":core-domain"))
    api(project(":user-core"))
    api(project(":tour-core"))
    api(project(":review-core"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}
