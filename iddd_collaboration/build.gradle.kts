plugins {
    id("iddd-conventions")
}

dependencies {
    implementation(project(":iddd_common"))
    implementation(group = "org.jboss.resteasy", name = "resteasy-jaxrs", version = "2.0.1.GA")

    implementation(group = "org.springframework", name = "spring-core", version = "3.1.4.RELEASE")
    implementation(group = "org.springframework", name = "spring-beans", version = "3.1.4.RELEASE")
    implementation(group = "org.springframework", name = "spring-context", version = "3.1.4.RELEASE")
    implementation(group = "com.google.code.gson", name = "gson", version = "2.1")

    testImplementation(testFixtures(project(":iddd_common")))
    testImplementation(group = "javax.persistence", name = "persistence-api", version = "1.0.2")
    testImplementation(group = "mysql", name = "mysql-connector-java", version = "5.1.6")
    testImplementation(group = "commons-dbcp", name = "commons-dbcp", version = "1.4")
}
