# Contributing

## Workflow

1. Fork and clone this repository.
2. Create a new branch in your fork based off the **main** branch.
3. Make your changes.
4. Commit your changes, and push them.
5. Submit a Pull Request [here]!

## Contributing to the code

**The issue tracker is only for issue reporting or proposals/suggestions. If you
have a question, you can find us in our [Discord Server][discord server]**.

To contribute to this repository, feel free to create a new fork of the
repository and submit a pull request. We highly suggest using [IntelliJ IDEA][]
as your IDE as it will automatically do a lot of setup for you.

You can follow [this guide by sonatype][gpg-guide] to learn how to generate a
GPG key which is required when running `./gradlew build` locally.
Once you have generated the key you can export the private and public keys with the commands:

```sh
gpg --output private.pgp --armor --export-secret-key <key id here>  
gpg --output public.pgp --armor --export <key id here>    
```
You can find the `key id` by running `gpg --list-keys`
Once you have the keys exported you can copy the `gradle.properties.template` file
to `gradle.properties` and fill in the `signing.key`, and `signing.password` with the correct values.

Alternatively you can set `task.enabled` to `false` in `lib/build.gradle` to disable signing:
```gradle
tasks.whenTaskAdded { task ->
    if (task.name.contains("signArchives")) {
        task.enabled = false
    }
}
```
If you go this route, make sure to not commit this change when creating your pull request.

<!-- Link Dump -->

[discord server]: https://join.favware.tech
[here]: https://github.com/faware/java-result/pulls
[IntelliJ IDEA]: https://www.jetbrains.com/idea/
[gpg-guide]: https://central.sonatype.org/publish/requirements/gpg/#generating-a-key-pair
