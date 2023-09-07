plugins {
    java
}

dependencies {
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.5.8")
    testImplementation(group = "junit", name = "junit", version = "4.8.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
