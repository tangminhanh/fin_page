plugins {
    application
    java
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.yahoofinance-api:YahooFinanceAPI:3.17.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    // Set the main class for the application.
    mainClass.set("org.example.App")
}

tasks.test {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
