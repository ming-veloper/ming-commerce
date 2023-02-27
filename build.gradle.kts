import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    java
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "com.ming"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val asciidoctorExt: Configuration by configurations.creating
val snippetsDir by extra { file("build/generated-snippets") }
val copyResourcesMap = mapOf(
    "build/docs/asciidoc" to "BOOT-INF/classes/static/docs",
    "src/main/resources/static/script/main.js" to "BOOT-INF/classes/static/script"
)

tasks.register("docsRemove") {
    println("빌드 전 이전 restDocs 문서들을 삭제합니다.")
    val docs = file("src/main/resources/static/docs")
    docs.listFiles()?.forEach {
        println("${it.name}을 삭제합니다.")
        it.delete()
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("com.auth0:java-jwt:4.2.1")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.modelmapper:modelmapper:3.1.1")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.security:spring-security-test")
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

task("printBuildInfo") {
    val now = LocalDateTime.now()
    println("---------------------------------------------")
    val formattedString = now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 - HH시 mm분 ss초"))
    println("빌드 시간 : $formattedString")
    println("=============================================")
    println(
        "  __  __              _   _     ____   \n" +
                "U|' \\/ '|u   ___     | \\ |\"| U /\"___|u \n" +
                "\\| |\\/| |/  |_\"_|   <|  \\| |>\\| |  _ / \n" +
                " | |  | |    | |    U| |\\  |u | |_| |  \n" +
                " |_|  |_|  U/| |\\u   |_| \\_|   \\____|  \n" +
                "<<,-,,-..-,_|___|_,-.||   \\\\,-._)(|_   \n" +
                " (./  \\.)\\_)-' '-(_/ (_\")  (_/(__)__)  "
    )
    println("=============================================")
}

tasks {
    compileJava {
        dependsOn("printBuildInfo", "docsRemove")
    }

    test {
        outputs.dir(snippetsDir)
    }

    asciidoctor {
        dependsOn("test")
        inputs.dir(snippetsDir)
        configurations(asciidoctorExt.name)
        doLast {
            copy {
                println("###A")
                from("build/docs/asciidoc")
                into("src/main/resources/static/docs")
            }
        }
    }

    bootJar {
        dependsOn(asciidoctor)
        copyResourcesMap.forEach {
            from(it.key) {
                into(it.value)
            }
        }
    }
}

task("release", type = Exec::class) {
    dependsOn("build")

    doLast {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine(listOf("eb", "setenv", "SPRING_PROFILES_ACTIVE=prod"))
            standardOutput = stdout
        }

        println("eb setenv SPRING_PROFILES_ACTIVE=prod: \n$stdout")

        exec {
            commandLine(listOf("eb", "deploy"))
            standardOutput = stdout
        }

        println("eb deploy: \n$stdout")
        println("Release succeeded")
    }
}

