package se.zensum.test.util

import com.palantir.docker.compose.DockerComposeRule
import org.junit.jupiter.api.Test

@DockerCompose(services = ["redis"])
class DockerComposeExtensionTest {

    @Test
    fun test(dockerCompose: DockerComposeRule) {
        println(dockerCompose.containers().ip())
    }
}