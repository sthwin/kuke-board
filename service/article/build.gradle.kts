dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation(project(":common:snowflake"))
    implementation(project(":common:outbox-message-relay"))
    implementation(project(":common:event"))
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation(kotlin("test"))
}