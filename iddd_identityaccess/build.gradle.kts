plugins {
    id("iddd-conventions")
}

dependencies {
    implementation(project(":iddd_common"))
    implementation(group = "org.springframework", name = "spring", version = "2.5.6")
    implementation(group = "javax.ws.rs", name = "javax.ws.rs-api", version = "2.0-rc1")
    implementation(group = "org.jboss.resteasy", name = "resteasy-cache-core", version = "2.0.1.GA")

    // testImplementation (files (this.project(":iddd_common").sourceSets.test.output)    )
    testImplementation(group = "javax.persistence", name = "persistence-api", version = "1.0.2")
    testImplementation(group = "mysql", name = "mysql-connector-java", version = "5.1.6")
    testImplementation(group = "commons-dbcp", name = "commons-dbcp", version = "1.4")
    testImplementation(group = "javax.servlet", name = "servlet-api", version = "2.5")
    testImplementation(group = "org.jboss.resteasy", name = "tjws", version = "2.0.1.GA")
}