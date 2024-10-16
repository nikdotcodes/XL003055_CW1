import org.jreleaser.model.Distribution

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

tasks {
    jar {
        manifest {
            attributes(
                "Main-Class" to "codes.nikdot.Pipeline"
            )
        }
    }
    shadowJar {
        manifest.inheritFrom(jar.get().manifest)
    }
}

application {
    mainClass = "codes.nikdot.Pipeline"
}

jreleaser {
    project {
        name = "pipeline"
        version = "2024.10.2"
        versionPattern = "CALVER:YYYY.0M.MINOR"
        description = "Pipeline to read .txt files and extract information"
        authors = listOf("Nik Walne")
        tags = listOf("cli", "data processing")
        license = "Apache-2.0"
        links {
            homepage = "https://nik.codes"
            license = "https://www.apache.org/licenses/LICENSE-2.0"
        }
        copyright = "2024 NikDotCodes LTD"
    }
    release {
        github {
            name = "XL003055_CW1"
            host = "github.com"
            username = "nikdotcodes"
            token = "TOKEN_GOES_HERE"
            apiEndpoint = "https://api.github.com"
            overwrite = true
        }
    }
    checksum {
        name = "{{projectName}}-{{projectVersion}}_checksums.txt"
        algorithm("SHA-256")
        algorithm("MD5")
        individual = true
        artifacts = true
        files = true
    }
    distributions {
    }
    this.distributions.create("pipeline") {
        artifact {
            path.set(file("build/libs/pipeline.jar"))
        }
        distributionType = Distribution.DistributionType.SINGLE_JAR
    }
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