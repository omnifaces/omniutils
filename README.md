[![Maven](https://img.shields.io/maven-metadata/v/https/repo.maven.apache.org/maven2/org/omnifaces/omniutils/maven-metadata.xml.svg)](https://repo.maven.apache.org/maven2/org/omnifaces/omniutils/)
[![Javadoc](https://javadoc.io/badge/org.omnifaces/omniutils.svg)](https://javadoc.io/doc/org.omnifaces/omniutils) 
[![Travis](https://travis-ci.org/omnifaces/omniutils.svg?branch=develop)](https://travis-ci.org/omnifaces/omniutils)
[![License](https://img.shields.io/:license-apache-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

OmniFaces Utils
=================

A utility library for Java SE.

OmniUtils is a set of Java SE libraries that includes JDK 8 collectors for working with streams (such as `ForEachBatchCollector` and `ReversedStreamCollector`), functional-like predicates (such as `isAnyEmpty`, `isOneOf`, and `coalesce`), reflection tools (such as `findField`, `listAnnotatedFields`, and `closestMatchingMethod`), security functions (such as `getCertificateChainFromServer` and `generateRandomRSAKeys`), image tools (such as `cropImage`, `toJpg`, `toPng` and `progressiveBilinearDownscale`) and much more!

## Install ##

Maven users can add OmniFaces by adding the following Maven coordinates to the pom.xml of a project:

```
<dependency>
    <groupId>org.omnifaces</groupId>
    <artifactId>omniutils</artifactId>
    <version>0.11</version>
</dependency>
```

## Notes ##

OmniUtils is still in its early stages of development

