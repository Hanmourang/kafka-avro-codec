# kafka-avro-codec

Avro encoder/decoder for use as `serializer.class` in Apache Kafka 0.8

---

Table of Contents

* <a href="#Usage">Usage</a>
* <a href="#Development">Development</a>
* <a href="#Contributing">Contributing</a>
* <a href="#License">License</a>

---


<a name="Usage"></a>

# Usage

## Background: Avro and Strings

By default Avro uses `CharSequence` for fields defined as `string` in an Avro schema.  Oftentimes this is not what you
want -- see the discussions at [AVRO-803](https://issues.apache.org/jira/browse/AVRO-803) and
[Apache Avro: map uses CharSequence as key](http://stackoverflow.com/questions/19728853/apache-avro-map-uses-charsequence-as-key)
for details.

For the unit tests included in this project we explicitly configure Avro to use `java.lang.String` instead of
`CharSequence` (or Avro's own [Utf8](https://avro.apache.org/docs/current/api/java/org/apache/avro/util/Utf8.html)
class), which provides us with the behavior you would expect when working with string-like objects in Java/Scala.

How you would configure Avro's "string behavior" depends on your project setup.  In our case we are using
[sbt](http://www.scala-sbt.org/) and [sbt-avro](https://github.com/cavorite/sbt-avro) to compile Avro schemas into Java
code.  Here, [build.sbt](build.sbt) is the place to configure Avro through sbt-avro's `stringType` setting.
If you are not using sbt but, say, Maven then you would need to add `<stringType>String</stringType>` to the
configuration of avro-maven-plugin in your `pom.xml`.


## Using the codec in Kafka

## Assumptions

The codec assumes you are using Avro with code generation, i.e. you create an Avro schema (see
[twitter.avsc](src/test/avro/twitter.avsc)) and then compile the schema into Java code.  "With code generation" is the
scenario where you would use `SpecificDatumWriter` and `SpecificDatumReader` in Avro (which is what this codec does)
instead of `GenericDatumWriter` and `GenericDatumReader`.

See the [Avro documentation](http://avro.apache.org/docs/1.7.6/gettingstartedjava.html) for details.


### Dependency management

_Note: The build artifacts published in Clojars.org are built against Java 6._

When using sbt add the following lines to `build.sbt`:

```
resolvers ++= Seq(
  "clojars-repository" at "https://clojars.org/repo"
)

libraryDependencies ++= Seq(
  "com.miguno" %% "kafka-avro-codec" % "0.1.1"
)
```

When using gradle add the following lines to `build.gradle`:

```
repositories {
  mavenCentral()
  maven { url 'https://clojars.org/repo' }
}

dependencies {
  compile 'com.miguno:kafka-avro-codec_2.10:0.1.1'
}
```


### Implementing a custom codec for your data records

Let us assume you have defined an Avro schema for Twitter tweets, and you want to use this schema for data messages that
you sent into a Kafka topic (see [twitter.avsc](src/test/avro/twitter.avsc)).  To implement an accompanying Avro
encoder/decoder pair for Kafka you would need to extend two base classes:

* [AvroEncoder](src/main/scala/com/miguno/kafka/avro/AvroEncoder.scala)
* [AvroDecoder](src/main/scala/com/miguno/kafka/avro/AvroDecoder.scala)

Example:

```scala
package your.app

import kafka.utils.VerifiableProperties
import com.miguno.kafka.avro.AvroEncoder
import com.miguno.avro.Tweet // <-- This is your Avro record, see twitter.avsc

class TweetAvroEncoder[Tweet](props: VerifiableProperties = null) extends AvroEncoder[Tweet](props,
  Tweet.getClassSchema)

class TweetAvroDecoder[Tweet](props: VerifiableProperties = null) extends AvroDecoder[Tweet](props,
  Tweet.getClassSchema)
```


### Using the custom codec in a Kafka producer

TODO


### Using the custom codec in a Kafka consumer

```scala
val topic = "zerg.hydra" // name of the Kafka topic you are reading from
val numThreads = 3 // number of threads for reading from that topic (note: #partitions should be >= 3 in this example)
val topicCountMap = Map(topic -> numThreads)
val valueDecoder = new TweetAvroDecoder[Tweet]
val keyDecoder = valueDecoder // or use `null` in case you explicitly not want to use keys (note that in Kafka 0.8
                              // this means some topic partitions may never see data)
val consumerMap = consumerConnector.createMessageStreams(topicCountMap, keyDecoder, valueDecoder)
```


<a name="Development"></a>

# Development

## Build requirements

* Scala 2.10.5
* sbt 0.13.8
* Java JDK 6 or 7
* [Avro](http://avro.apache.org/) 1.7.6


## Building the code

    $ ./sbt clean compile


## Running tests

    $ ./sbt clean test


## Packaging the code

    $ ./sbt clean package


## Publishing build artifacts

### Configure Clojars.org user credentials

Create a file `~/.clojars-credentials.txt` with the following contents:

```
realm=clojars
host=clojars.org
user=YOUR_CLOJARS_USERNAME
password=YOUR_CLOJARS_PASSWORD
```

Security-wise it is also recommended to `chmod 600 ~/.clojars-credentials.txt`.


### Publish to Clojars.org

TODO: Explain versioning etc.

Publish the build artifacts via:

    $ ./sbt clean test publish


<a name="Contributing"></a>

## Contributing to kafka-avro-codec

Code contributions, bug reports, feature requests etc. are all welcome.

If you are new to GitHub please read [Contributing to a project](https://help.github.com/articles/fork-a-repo) for how
to send patches and pull requests to kafka-avro-codec.


<a name="License"></a>

## License

Copyright © 2014 Michael G. Noll

See [LICENSE](LICENSE) for licensing information.
