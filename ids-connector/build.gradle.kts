plugins {
    application
    id("org.springframework.boot")

    kotlin("jvm")
    kotlin("plugin.spring")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    launchScript()
    layered()
}

dependencies {
    api(project(":ids-webconsole"))
    api(project(":ids-settings"))
    api(project(":ids-container-manager"))
    api(project(":ids-route-manager"))
    api(project(":ids-infomodel-manager"))
    api(project(":ids-dataflow-control"))

    // Camel Spring Boot integration
    implementation("org.apache.camel.springboot:camel-spring-boot-starter")

    // Camel components
    implementation("org.apache.camel.springboot:camel-rest-starter")
    implementation("org.apache.camel.springboot:camel-http-starter")

    implementation(project(":camel-idscp2"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")
}
