<div align="center">

# Result

**A Java implementation of a Result monad inspired by Rust's Result struct**

[![GitHub](https://img.shields.io/github/license/favware/java-result)](https://github.com/favware/java-result/blob/main/LICENSE)

[![Support Server](https://discord.com/api/guilds/512303595966824458/embed.png?style=banner2)](https://join.favware.tech)

</div>

***Important:*** This package is a mirror of https://github.com/jasongoodwin/better-java-monads, however that package is
not maintained any more. This way we can maintain the code ourselves. Furthermore, the dependency as pulled in from
maven-central was also unable to properly show javadoc comments for unknown reason. Big chunks of the following text are
taken straight from the original source code.

---

## About

This library adds a `Result` monad to Java. While `Optional` exists to express nulls in types, there is no way to
express a success/failure of a callable lambda expression. `Result` fills this gap.

The `Result` type is very similar to the `Result` in Scala's standard lib. Some inspiration is also taken from
the `Result` monad in the Rust language, and subsequently from `@sapphire/result`, the TypeScript implementation of the
former.

Lastly, this version of the `Result` monad also addresses all open issues in the original project, as well as including
the
code of all open pull requests.

## Installation

### Maven

```xml
<dependency>
    <groupId>tech.favware</groupId>
    <artifactId>result</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

**With Gradle Groovy**

```groovy
dependencies {
    implementation 'tech.favware:result:1.0.0'
}
```

**With Kotlin DSL**

```kotlin
dependencies {
    implementation("tech.favware:result:1.0.0")
}
```

## Usage

The `Result` monad was attributed to Twitter and placed into the Scala standard library.
While both Scala and Haskell have a monad Either which has a left and a right type,
a `Result` is specifically of a type `T` on success or an exception on failure.

The `Result` api is meant to be similar to the `Optional` type so has the same functions.

- `get()` returns the held value or throws the thrown exception
- `getUnchecked()` returns the held value or throws the thrown exception wrapped in a `RuntimeException` instance
- `map(x)` maps the success value `x` to a new value and type or otherwise passes the Failure forward.
- `flatMap((x) -> f(x))` maps the success value `x` to a new `Result` of f(x).
- `recover((t) -> x)` will return the success value of the `Result` in the success case or the value `x` in the failure
  case.
  Exposes the exception.
- `recoverWith((t) -> f(x))` will return the success value of the `Result` in the success case or a new `Result`
  of `f(x)` in
  the
  failure case. Exposes the exception.
- `filter((x) -> isTrue(x))` - If `Success`, returns the same `Success` if the predicate succeeds, otherwise, returns a
  `Failure` with type `NoSuchElementException`.
- `onSuccess((x) -> f(x))` execute some code on success - takes a `Consumer` (e.g. requires no return value).
- `onFailure((x) -> f(x))` execute some code on failure - takes a `Consumer` (e.g. requires no return value).
- `raise(x)` -> will throw an exception of type `x` when it happens.
- `orElse(x)` will return the success value of the `Result` in success case or the value `x` in failure case.
- `orElseResult(f)` will return the success value of the `Result` in success case or a new `Result(f)` in the failure
  case.
- `orElseThrow(() -> throw new T)` gets result or on failure will throw checked exception of type `T`
- `toOptional()` will return `Optional` of success value of `Result` (if not `null`), otherwise it will return an
  empty `Optional`

## Contributors

Please make sure to read the [Contributing Guide][contributing] before making a pull request.

Thank you to all the people who already contributed to Sapphire!

<a href="https://github.com/favware/java-result/graphs/contributors">
  <img alt="contributors" src="https://contrib.rocks/image?repo=favware/java-result" />
</a>

[contributing]: ./.github/CONTRIBUTING.md
