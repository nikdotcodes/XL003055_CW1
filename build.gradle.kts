plugins {
    id("java-library")
    id("application")
    id("org.jreleaser") version "1.14.0"
}

application {
    mainClass.set("codes.nikdot.Pipeline")
}

jreleaser {
    project {
        name = "Pipeline"
        version = project.version.toString()
        versionPattern = "CALVER:yyyy.0M0D"
        description = "Pipeline to read .txt files and extract information"
        authors = listOf("Nik Walne")
        tags = listOf("cli", "data processing")
        license = "Apache-2.0"
        links {
            homepage = "https://nik.codes"
            license = "https://www.apache.org/licenses/LICENSE-2.0"
        }
    }
    release {
        generic {
            repoOwner = "nikdotcodes"
            host = "nik.codes"
            token = "__NOT_A_REAL_TOKEN__"
            overwrite = true
        }
    }
}

group = "codes.nikdot"
version = "2024.1001"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.cleartk:cleartk-stanford-corenlp:3.0.0")
    implementation("info.picocli:picocli:4.7.6")
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("org.slf4j:slf4j-nop:1.7.32")
}

tasks.test {
    useJUnitPlatform()
}
