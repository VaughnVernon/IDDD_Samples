plugins {
    id("iddd-conventions")
    `java-test-fixtures`
}

dependencies {
    implementation(group = "commons-logging", name = "commons-logging", version = "1.1.2") //, transitive= true)
    implementation(group = "com.google.code.gson", name = "gson", version = "2.1")
    implementation(group = "com.rabbitmq", name = "amqp-client", version = "3.0.4")
    implementation(group = "org.hibernate", name = "hibernate", version = "3.2.7.ga")
    implementation(group = "org.springframework", name = "spring", version = "2.5.6")
    implementation(group = "org.iq80.leveldb", name = "leveldb", version = "0.5")
    implementation(group = "org.aspectj", name = "aspectjweaver", version = "1.7.2")
    implementation(group = "javassist", name = "javassist", version = "3.8.0.GA")
    implementation(group = "javax.transaction", name = "jta", version = "1.1")

    testImplementation(group = "javax.persistence", name = "persistence-api", version = "1.0.2")
    testImplementation(group = "mysql", name = "mysql-connector-java", version = "5.1.6")
    testImplementation(group = "commons-dbcp", name = "commons-dbcp", version = "1.4")

    testFixturesImplementation(group = "junit", name = "junit", version = "4.8.2")
    testFixturesImplementation(group = "com.google.code.gson", name = "gson", version = "2.1")
}
