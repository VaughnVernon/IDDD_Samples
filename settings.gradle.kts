include(
    "iddd_common",
    "iddd_identityaccess",
    "iddd_collaboration",
    "iddd_agilepm",
)

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()

        // JBoss Repo
        maven { url = uri("https://repository.jboss.org/nexus/content/groups/public-jboss/") }
    }
}
