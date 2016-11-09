# javadoc.io-linker

This Gradle plugin scans your compile dependencies, and configures the javadoc task to link Javadoc.io.

## Usage

```groovy
buildscript {
    repositories {
        maven { url "https://plugins.gradle.org/m2/" }
    }
    dependencies {
        classpath "org.danilopianini:build-commons:0.1.6"
    }
}

// Configure your dependencies here!

apply plugin: 'org.danilopianini.javadoc.io-linker'

```

It is **very important** to apply the plugin AFTER the configuration of the compile dependencies. In fact, it relies on resolved dependencies to figure out the actual version used (the plugin would fail with version ranges otherwise).
