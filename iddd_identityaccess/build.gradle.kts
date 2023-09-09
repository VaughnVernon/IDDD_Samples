plugins {
    id("iddd-conventions")
}

dependencies {
    implementation(project(":iddd_common"))
    implementation(group = "org.springframework", name = "spring-core", version = "3.1.4.RELEASE")
    implementation(group = "org.springframework", name = "spring-beans", version = "3.1.4.RELEASE")
    implementation(group = "org.springframework", name = "spring-tx", version = "3.1.4.RELEASE")
    implementation(group = "javax.ws.rs", name = "javax.ws.rs-api", version = "2.0-rc1")
    implementation(group = "org.jboss.resteasy", name = "resteasy-cache-core", version = "2.0.1.GA")

    implementation(group = "org.aspectj", name = "aspectjweaver", version = "1.7.2")
    implementation(group = "org.hibernate", name = "hibernate", version = "3.2.7.ga")

    testImplementation(testFixtures(project(":iddd_common")))
    testImplementation(group = "javax.persistence", name = "persistence-api", version = "1.0.2")
    testImplementation(group = "mysql", name = "mysql-connector-java", version = "5.1.6")
    testImplementation(group = "commons-dbcp", name = "commons-dbcp", version = "1.4")
    testImplementation(group = "javax.servlet", name = "servlet-api", version = "2.5")
    testImplementation(group = "org.jboss.resteasy", name = "tjws", version = "2.0.1.GA")

    testImplementation(group = "com.google.code.gson", name = "gson", version = "2.1")
}
