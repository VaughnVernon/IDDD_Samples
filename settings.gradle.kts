import java.net.URI

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
        maven { url = URI("https://repository.jboss.org/nexus/content/groups/public-jboss/") }
    }
}
