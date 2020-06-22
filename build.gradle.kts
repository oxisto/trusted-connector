import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.yaml.snakeyaml.Yaml

buildscript {

    dependencies {
        classpath("org.yaml:snakeyaml:1.26")
    }
}

plugins {

    // Spring Boot
    id("org.springframework.boot") version "2.3.1.RELEASE" apply false
    id("io.spring.dependency-management") version "1.0.8.RELEASE"

    // Other needed plugins
    id("com.moowork.node") version "1.3.1" apply false
    id("com.benjaminsproule.swagger") version "1.0.10" apply false

    // Protobuf
    id("com.google.protobuf") version "0.8.12" apply false

    // Kotlin specific
    kotlin("jvm") version "1.3.72" apply false
    kotlin("plugin.spring") version "1.3.50" apply false
}

ext.set("libraryVersions", Yaml().loadAs(java.io.FileInputStream(file("${rootDir}/libraryVersions.yaml")), Map::class.java))

allprojects {

    group = "de.fhg.ids"
    version = "3.0.2"

    tasks.withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

}

subprojects {
    repositories {
        mavenCentral()

        // References IAIS repository that contains the infomodel artifacts
        maven("https://maven.iais.fraunhofer.de/artifactory/eis-ids-public/")
    }

    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.spring.dependency-management")

    dependencies {
        val implementation by configurations

        implementation(kotlin("reflect"))
        implementation(kotlin("stdlib-jdk8"))
    }

    configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:2.3.1.RELEASE")
        }

        imports {
            // need to stick to 3.0 because of org.apache.camel.support.dump.RouteStatDump and ModelHelper
            mavenBom("org.apache.camel.springboot:camel-spring-boot-dependencies:3.0.0")
        }
    }

    // just to make bills of materials (bom) easier to see in the dependency tree
    val bom by configurations.creating


    // Configuration for dependencies that will be provided through features in the OSGi environment
    val providedByFeature by configurations.creating

    // Configurations for dependencies that will be provided through bundles in the OSGi environment
    // Separate configurations are required when two bundles depend on different versions of the same bundle!
    val providedByBundle by configurations.creating
    val unixSocketBundle by configurations.creating
    val infomodelBundle by configurations.creating

    // Configurations for bundles grouped to dedicated features apart from the main ids feature
    val influxFeature by configurations.creating
    val zmqFeature by configurations.creating

    // OSGi core dependencies which will just be there during runtime
    val osgiCore by configurations.creating

    // For artifacts that should be included as "compile" dependencies into published maven artifacts
    val publishCompile by configurations.creating

    // The "compile" configuration needs to be extended for dependency resolution of mavenpublish
    configurations["compile"].extendsFrom(providedByFeature, providedByBundle, unixSocketBundle, infomodelBundle, osgiCore, publishCompile)
    // Some compileOnly dependencies are also needed for unit tests
    //testImplementation.extendsFrom compileOnly
}

/*

the<DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}
 */