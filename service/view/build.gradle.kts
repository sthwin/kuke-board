dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation(project(":common:snowflake"))
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation(kotlin("test"))
}