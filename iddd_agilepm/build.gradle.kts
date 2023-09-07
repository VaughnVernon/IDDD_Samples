plugins {
    id("iddd-conventions")
}

dependencies {
    implementation(project(":iddd_common"))

    // testImplementation(files(this.project(':iddd_common').sourceSets.test.output))
}
