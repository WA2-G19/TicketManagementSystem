import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
	kotlin("plugin.jpa") version "1.7.22"
}

group = "it.polito.wa2.g19"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation ("org.json:json:20220320")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "mockito-core")
	}
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("com.ninja-squad:springmockk:4.0.2")
	testImplementation ("org.testcontainers:junit-jupiter:1.16.3")
	testImplementation("org.testcontainers:postgresql:1.17.6")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation ("com.github.dasniko:testcontainers-keycloak:2.5.0")

	implementation("org.keycloak:keycloak-admin-client:21.1.1")

	// using new @Observed on class and enaabled @ObservedAspect
	implementation("org.springframework.boot:spring-boot-starter-aop")
	// enabled endpoint and expose metrics
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")
	// handleing lifecycle of a span
	implementation("io.micrometer:micrometer-tracing-bridge-brave")
	// send span and trace data
	// endpoint is default to "http://locahost:9411/api/v2/spans" by actuator
	// we could setting by management.zipkin.tracing.endpoint
	implementation("io.zipkin.reporter2:zipkin-reporter-brave")
	// send logs by log Appender through URL
	implementation("com.github.loki4j:loki-logback-appender:1.4.0-rc2")

}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:1.16.3")
	}
}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

