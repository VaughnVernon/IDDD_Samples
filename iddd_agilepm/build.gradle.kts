plugins {
    id("iddd-conventions")
}

dependencies {
    implementation(project(":iddd_common"))

    implementation(group = "org.iq80.leveldb", name = "leveldb", version = "0.5")
    implementation(group = "com.google.code.gson", name = "gson", version = "2.1")

    testImplementation(testFixtures(project(":iddd_common")))
}
