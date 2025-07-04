dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    implementation(project(":common:event"))
    implementation(project(":common:data-serializer"))
    testImplementation(kotlin("test"))
}