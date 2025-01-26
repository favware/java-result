plugins {
    id("com.gradle.develocity") version "3.19.1"
}

rootProject.name = "result"

develocity {
    if (System.getenv("CI") != null) {
        buildScan {
            publishing.onlyIf { true }
            termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
            termsOfUseAgree.set("yes")
        }
    }
}
