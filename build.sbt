import sbtrelease.ReleasePlugin._

organization := "com.miguno"

name := "kafka-avro-codec"

scalaVersion := "2.10.3"

sbtVersion := "0.13.1"

seq(sbtavro.SbtAvro.avroSettings : _*)

// Configure the desired Avro version.  sbt-avro automatically injects a libraryDependency.
(version in avroConfig) := "1.7.5"

// Look for *.avsc etc. files in src/test/avro
(sourceDirectory in avroConfig) <<= (sourceDirectory in Test)(_ / "avro")

(stringType in avroConfig) := "String"

resolvers ++= Seq(
  "typesafe-repository" at "http://repo.typesafe.com/typesafe/releases/",
  // Required to retrieve Kafka 0.8.0 release artifacts
  "Apache releases" at "https://repository.apache.org/content/repositories/releases/"
)

libraryDependencies ++= Seq(
  // The excludes of jms, jmxtools and jmxri are required as per https://issues.apache.org/jira/browse/KAFKA-974.
  // The exclude of slf4j-simple is because it overlaps with our use of logback with slf4j facade;  without the exclude
  // we get slf4j warnings and logback's configuration is not picked up.
  "org.apache.kafka" % "kafka_2.10" % "0.8.0"
    exclude("javax.jms", "jms")
    exclude("com.sun.jdmk", "jmxtools")
    exclude("com.sun.jmx", "jmxri")
    exclude("org.slf4j", "slf4j-simple"),
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test"
)

publishMavenStyle := true

publishTo <<= version { (v: String) => Some("Clojars.org" at "https://clojars.org/repo") }

// Configure your Clojars.org credentials in $HOME/.clojars-credentials.txt as documented at
// http://www.scala-sbt.org/release/docs/Detailed-Topics/Publishing#credentials
credentials += Credentials(Path.userHome / ".clojars-credentials.txt")

publishArtifact in Test := false

pomIncludeRepository := { x => false }

pomExtra := (
  <url>https://github.com/miguno/kafka-avro-codec</url>
  <licenses>
    <license>
      <name>Apache</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:miguno/kafka-avro-codec.git</url>
    <connection>scm:git:git@github.com:miguno/kafka-avro-codec.git</connection>
  </scm>
  <developers>
    <developer>
      <id>miguno</id>
      <name>Michael G. Noll</name>
      <url>https://github.com/miguno</url>
    </developer>
  </developers>)

releaseSettings
