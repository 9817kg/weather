plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "SpringBoot-Multimodules"
include("module-api")
include("module-core")
include("module-domain")

