plugins {
	java
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.jotatec"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("io.projectreactor:reactor-core:3.4.15")
	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation(platform("org.junit:junit-bom:5.9.1"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation("junit:junit:4.13.2")
	implementation("org.jsoup:jsoup:1.14.3")
	implementation("org.apache.commons:commons-lang3:3.12.0")
	implementation("org.javatuples:javatuples:1.2")
	implementation("org.json:json:20210307")
	implementation("org.apache.httpcomponents:httpclient:4.5.13")
	implementation("org.openjfx:javafx-controls:16")
	implementation("org.openjfx:javafx-fxml:16")
	implementation(files("lib/mysql-connector-j-8.3.0.jar"))
	}

tasks.withType<Test> {
	useJUnitPlatform()
}
