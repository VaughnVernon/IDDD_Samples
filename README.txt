These are the sample Bounded Contexts from the book
"Implementing Domain-Driven Design" by Vaughn Vernon:

http://vaughnvernon.co/?page_id=168

The models and surrounding architectural mechanisms
may be in various states of flux as the are refined
over time. Some tests may be incomplete. The code is
not meant to be a reflection of a production quality
work, but rather as a set of reference projects for
the book.

Points of Interest
------------------

The iddd_agilepm project uses a key-value store as
its underlying persistence mechanism, and in particular
is LevelDB. Actually the LevelDB in use is a pure Java
implementation: https://github.com/dain/leveldb

Currently iddd_agilepm doesn't employ a container of
any kind (such as Spring).

The iddd_collaboration project uses Event Sourcing and
CQRS. It purposely avoids the use of an object-relational
mapper, showing that a simple JDBC-based query engine
and DTO matter can be used instead. This technique does
have its limitations, but it is meant to be small and fast
and require no configuration or annotations. It is not
meant to be perfect.

It may be helpful to make one additional mental note on
the iddd_collaboration CQRS implementation. To keep the
example simple it persists the Event Sourced write model
and the CQRS read model in one thread. Since two different
stores are used--LevelDB for the Event Journal and MySQL
for the read model--there may be very slight opportunities
for inconsistency, but not much. The idea was to keep the
two models as close to consistent as possible without
using the same data storage (and transaction) for both.
Two different storage mechanisms were used purposely to
demonstrate that they can be separate.

The iddd_identityaccess project uses uses object-relational
mapping (Hibernate), but so as not to leave it "boring" it
provides a RESTful client interface and even publishes
Domain-Event notifications via REST (logs) and RabbitMQ.

Finally the iddd_common project provides a number of reusable
components. This is not an attempt to be a framework, but
just leverages reuse to the degree that code copying doesn't
liter each project. This is not a recommendation, but it
did work well and save a considerable amount of work while
producing the samples.

Build and Dependencies
----------------------

A local dependency cache was used during development, and the
final build will be missing for a few more days. I will use
Gradle, and need to provide support for maven/ivy repository.
Until the time that the build is completed, this is a list of
dependencies in the local cache:

antlr-2.7.6.jar
aspectjweaver.jar
cglib-nodep-2.1_3.jar
commons-codec-1.2.jar
commons-collections-3.1.jar
commons-dbcp-1.4.jar
commons-httpclient-3.1.jar
commons-logging-1.1.1.jar
commons-pool-1.6.jar
dom4j-1.6.1.jar
gson-2.1.jar
guava-12.0.jar
hibernate3.jar
javassist-3.8.0.GA.jar
jaxrs-api-2.0.1.GA.jar
jettison-1.2.jar
jta-1.1.jar
junit-3.8.2.jar
junit-4.8.2.jar
leveldb-0.6-SNAPSHOT.jar
leveldb-api-0.6-SNAPSHOT.jar
mail-1.4.jar
mysql-connector-java-5.1.6-bin.jar
persistence-api-1.0.jar
rabbitmq-client.jar
resteasy-atom-provider-2.0.1.GA.jar
resteasy-cache-core-2.0.1.GA.jar
resteasy-cdi-2.0.1.GA.jar
resteasy-fastinfoset-provider-2.0.1.GA.jar
resteasy-guice-2.0.1.GA.jar
resteasy-jackson-provider-2.0.1.GA.jar
resteasy-jaxb-provider-2.0.1.GA.jar
resteasy-jaxrs-2.0.1.GA.jar
resteasy-jettison-provider-2.0.1.GA.jar
resteasy-jsapi-2.0.1.GA.jar
resteasy-links-2.0.1.GA.jar
resteasy-multipart-provider-2.0.1.GA.jar
resteasy-oauth-2.0.1.GA.jar
resteasy-spring-2.0.1.GA.jar
resteasy-yaml-provider-2.0.1.GA.jar
scannotation-1.0.2.jar
servlet-api-2.5.jar
slf4j-api-1.5.8.jar
slf4j-simple-1.5.8.jar
snappy-0.2.jar
spring.jar
tjws-2.0.1.GA.jar

A few of these jar files may actually not be used, but
I won't try to sort that out at the moment.

That's it for now. I hope you benefit from the samples.

Vaughn Vernon
Author: Implementing Domain-Driven Design
Twitter: @VaughnVernon
http://vaughnvernon.co/
