plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
}

group = "tech.favware"
version = "1.2.0"
description = "A Java implementation of a Result monad inspired by Rust\"s Result struct"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
    mavenLocal()
}

val junitVersion = "5.9.2"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"

    if (JavaVersion.current().isJava9Compatible()) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

tasks.whenTaskAdded {
    if (this.name.contains("signArchives")) {
        this.enabled = true
    }
}

signing {
    val signingKey: String = project.findProperty("signing.key") as String? ?: System.getenv("PGP_SIGNING_KEY")
    val signingPassword: String = project.findProperty("signing.password") as String?
            ?: System.getenv("PGP_SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)

    sign(configurations.getByName("archives"))
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "result"
            from(components["java"])

            pom {
                name.set("Result")

                description.set("A Java implementation of a Result monad inspired by Rust's Result struct.")
                url.set("https://github.com/favware/java-result/")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("favna")
                        name.set("Jeroen Claassens")
                        email.set("support@favware.tech")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/favware/java-result.git")
                    developerConnection.set("scm:git:ssh://github.com:favware/java-result.git")
                    url.set("http://github.com/favware/java-result/tree/main")
                }
            }
        }
    }

    repositories {
        maven {
            name = "OSSHR"

            url = if (project.version.toString().endsWith("-SNAPSHOT")) {
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            } else {
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            }

            credentials {
                username = project.findProperty("osshr.user") as String? ?: System.getenv("OSSHR_USERNAME")
                password = project.findProperty("osshr.key") as String? ?: System.getenv("OSSHR_TOKEN")
            }
        }

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/favware/java-result")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_TOKEN")
            }
        }

        maven {
            name = "Local"
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}
