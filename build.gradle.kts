plugins {
    id("com.gradleup.shadow") version "8.3.3"
    id("org.jreleaser") version "1.14.0"
    id("java")
    id("java-library")
    id("application")
}

java {
    sourceCompatibility = JavaVersion.VERSION_22
    targetCompatibility = JavaVersion.VERSION_22
    manifest().attributes(
        "Main-Class" to "codes.nikdot.Pipeline"
    )
}

tasks.named("distZip").configure {dependsOn("shadowJar")}
tasks.named("distTar").configure {dependsOn("shadowJar")}
tasks.named("startScripts").configure {dependsOn("shadowJar")}
tasks.named("startShadowScripts").configure {dependsOn("jar")}
tasks {
    shadowJar {
        manifest {
            attributes(
                "Main-Class" to "codes.nikdot.Pipeline"
            )
        }
        archiveClassifier.set("")
        archiveVersion.set("")
    }
}

application {
    mainClass = "codes.nikdot.Pipeline"
}

group = "codes.nikdot"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5:models")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5:models-english")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5:models-english-kbp")
    implementation("info.picocli:picocli:4.7.6")
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("org.slf4j:slf4j-nop:1.7.32")
}

tasks.test {
    useJUnitPlatform()
}