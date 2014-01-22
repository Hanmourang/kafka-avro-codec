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

TODO


<a name="Development"></a>

# Development

## Build requirements

* Scala 2.10.3
* Java JDK 6 or 7
* sbt 0.10.3
* [Avro](http://avro.apache.org/) 1.7.5


## Building the code

    $ ./sbt clean compile


## Running tests

    $ ./sbt clean test


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
to send patches and pull requests to avro-hadoop-starter.


<a name="License"></a>

## License

Copyright © 2014 Michael G. Noll

See [LICENSE](LICENSE) for licensing information.
