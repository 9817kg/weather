import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("io.spring.dependency-management") version "1.1.4"
	id("org.springframework.boot") version "3.2.3"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22" apply false
	kotlin("plugin.jpa") version "1.6.21" apply false
}

java.sourceCompatibility = JavaVersion.VERSION_17

allprojects {
	group = "com.example"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "java")

	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")

	apply(plugin = "kotlin")
	apply(plugin = "kotlin-spring") //all-open
	apply(plugin = "kotlin-jpa")

	dependencies {
		// springboot
		implementation ("org.springframework.boot:spring-boot-starter-thymeleaf")
		implementation("org.springframework.boot:spring-boot-starter-web")
		api("org.springframework.boot:spring-boot-starter-data-jpa")
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		developmentOnly("org.springframework.boot:spring-boot-devtools")
		api ("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.12.5")
		api ("javax.xml.bind:jaxb-api:2.3.1")
		// kotlin
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
		api  ("org.json:json:20220320")
		// DB
		api ("mysql:mysql-connector-java:8.0.33")
		api ("org.springframework.boot:spring-boot-starter-jdbc")
		api ("mysql:mysql-connector-java")

		// test
		testImplementation("org.springframework.boot:spring-boot-starter-test")
	}



	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "19"
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	configurations {
		compileOnly {
			extendsFrom(configurations.annotationProcessor.get())
		}
	}
}

// module core 에 module api, consumer이 의존
project(":module-api") {
	dependencies {
		implementation(project(":module-core"))
		implementation(project(":module-domain"))
	}
}

project(":module-domain") {
	dependencies {
		implementation(project(":module-core"))
	}
}





// core 설정
project(":module-core") {
	val jar: Jar by tasks
	val bootJar: BootJar by tasks

	bootJar.enabled = false
	jar.enabled = true

}